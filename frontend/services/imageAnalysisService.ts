import axios from 'axios';
import * as FileSystem from 'expo-file-system/legacy';

const BACKEND_URL = process.env.EXPO_PUBLIC_BACKEND_URL || 'http://10.0.2.2:8080';

// ─────────────────────────────────────────────────────────────────────────────
// Types
// ─────────────────────────────────────────────────────────────────────────────

export interface AnalyzeTicketResponse {
  ticketNumber: string | null;
  drawDate: string | null;
  provinceCode: string | null;
  resultsAvailable: boolean;
  prizes: PrizeResult[];
  errorMessage: string | null;
}

export interface PrizeResult {
  prizeLevel: string;
  prizeAmount: number;
  ticketNumber: string;
  provinceCode: string;
  drawDate: string;
}

export interface TicketData {
  so_ve: string | null;
  ngay_xo_so: string | null;
  dai_xo_so: string | null;
}

export interface BackendCheckResponse {
  prizeLevel?: string;
  prizeAmount?: number;
  ticketNumber?: string;
  provinceCode?: string;
  drawDate?: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Utility
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Đọc file ảnh và chuyển sang base64
 */
export async function convertImageToBase64(photoUri: string): Promise<string> {
  return await FileSystem.readAsStringAsync(photoUri, {
    encoding: 'base64',
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Core API (gọi backend, không gọi Google/Gemini trực tiếp nữa)
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Gửi ảnh vé số (base64) lên backend để phân tích (OCR + AI + dò vé).
 * Backend sẽ xử lý tất cả: Google Vision → Gemini → DrawService.
 */
export async function analyzeTicketImage(photoUri: string): Promise<AnalyzeTicketResponse> {
  const base64 = await convertImageToBase64(photoUri);

  const response = await axios.post<AnalyzeTicketResponse>(
    `${BACKEND_URL}/api/analyze-ticket`,
    { imageBase64: base64 },
    {
      headers: {
        'ngrok-skip-browser-warning': 'true',
        'Content-Type': 'application/json',
      },
      timeout: 30000, // 30s - OCR + AI có thể chậm
    },
  );

  console.log('Backend analyze response:', response.data);
  return response.data;
}

/**
 * Dò vé thủ công (nhập số tay).
 */
export async function checkTicketManually(ticketData: TicketData): Promise<BackendCheckResponse[]> {
  const response = await axios.post<BackendCheckResponse[]>(
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
      timeout: 10000,
    },
  );

  console.log('Backend check response:', response.data);
  return response.data;
}
