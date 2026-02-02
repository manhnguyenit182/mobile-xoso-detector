import axios from 'axios';
import * as FileSystem from 'expo-file-system/legacy';

const API_KEY = 'AIzaSyDPCzqZQZtz3nQn-RSiLGNdhuZIEkREB90';
const GEMINI_API_KEY = 'AIzaSyDPQWUa9c_cANL9fpNb4YxP0dOLBzzCUTk';
const BACKEND_URL = 'https://unmanually-egestive-viki.ngrok-free.dev';

export interface TicketData {
  so_ve: string | null;
  ngay_xo_so: string | null;
  dai_xo_so: string | null;
}

export interface BackendResponse {
  ticketNumber: string;
  drawDate: string;
  provinceCode: string;
  result: any;
}

/**
 * Đọc ảnh và chuyển sang base64
 */
export async function convertImageToBase64(photoUri: string): Promise<string> {
  return await FileSystem.readAsStringAsync(photoUri, {
    encoding: 'base64',
  });
}

/**
 * Sử dụng Google Cloud Vision API để OCR text từ ảnh
 */
export async function extractTextFromImage(base64Image: string): Promise<string> {
  const response = await axios.post(`https://vision.googleapis.com/v1/images:annotate?key=${API_KEY}`, {
    requests: [
      {
        image: {
          content: base64Image,
        },
        features: [
          {
            type: 'TEXT_DETECTION',
            maxResults: 10,
          },
        ],
      },
    ],
  });

  const textAnnotations = response.data.responses[0]?.textAnnotations;
  if (textAnnotations && textAnnotations.length > 0) {
    return textAnnotations[0].description;
  }

  throw new Error('Không phát hiện văn bản nào trong ảnh');
}

/**
 * Sử dụng Gemini AI để phân tích và trích xuất thông tin vé số từ text OCR
 */
export async function parseTicketData(ocrText: string): Promise<TicketData> {
  const genResponse = await axios.post(
    'https://generativelanguage.googleapis.com/v1beta/models/gemini-3-flash-preview:generateContent',
    {
      contents: [
        {
          parts: [
            {
              text: `Bạn là một hệ thống trích xuất dữ liệu có cấu trúc từ văn bản OCR của vé số Việt Nam.

NHIỆM VỤ DUY NHẤT:
- Phân tích văn bản OCR đầu vào
- Trả về DUY NHẤT một object JSON hợp lệ

ĐỊNH DẠNG BẮT BUỘC:
- Chỉ được trả về JSON thuần
- Không được giải thích
- Không được thêm markdown
- Không được thêm text trước hoặc sau JSON
- Không được xuống dòng thừa
- Không được thêm field ngoài schema

SCHEMA DUY NHẤT ĐƯỢC PHÉP:

{
  "so_ve": "string | null",
  "ngay_xo_so": "yyyy-MM-dd | null",
  "dai_xo_so": "string | null"
}

QUY TẮC TRÍCH XUẤT:
- Chỉ trích xuất dữ liệu nếu xuất hiện RÕ RÀNG trong OCR
- Không suy đoán
- Không tự sửa định dạng
- Nếu không tìm thấy → trả về null cho field đó
- Tên đài phải được chuẩn hoá (ví dụ: "BÌNH THUẬN" → "Bình Thuận")

Văn bản OCR:

${ocrText}`,
            },
          ],
        },
      ],
      generationConfig: {
        thinkingConfig: {
          thinkingLevel: 'low',
        },
      },
    },
    {
      headers: {
        'x-goog-api-key': GEMINI_API_KEY,
        'Content-Type': 'application/json',
      },
    },
  );

  const data = genResponse.data.candidates[0].content.parts[0].text;
  console.log('Kết quả phân tích:', data);

  return JSON.parse(data);
}

/**
 * Gửi thông tin vé số lên backend để kiểm tra kết quả
 */
export async function checkTicketWithBackend(ticketData: TicketData): Promise<BackendResponse> {
  const response = await axios.post(
    `${BACKEND_URL}/api/check-ticket`,
    {
      ticketNumber: ticketData.so_ve,
      drawDate: ticketData.ngay_xo_so,
      provinceCode: ticketData.dai_xo_so,
    },
    {
      headers: {
        'ngrok-skip-browser-warning': 'true',
        'Content-Type': 'application/json',
      },
    },
  );

  console.log('Backend response:', response.data);
  return response.data;
}

/**
 * Hàm chính để phân tích ảnh vé số - kết hợp tất cả các bước
 */
export async function analyzeTicketImage(photoUri: string): Promise<BackendResponse> {
  // Bước 1: Chuyển ảnh sang base64
  const base64 = await convertImageToBase64(photoUri);

  // Bước 2: OCR text từ ảnh
  const ocrText = await extractTextFromImage(base64);

  // Bước 3: Phân tích text để lấy thông tin vé
  const ticketData = await parseTicketData(ocrText);

  // Bước 4: Gửi lên backend để kiểm tra
  const result = await checkTicketWithBackend(ticketData);

  return result;
}
