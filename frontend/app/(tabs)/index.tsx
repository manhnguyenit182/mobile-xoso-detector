import axios from 'axios';
import { CameraType, CameraView, useCameraPermissions } from 'expo-camera';
import * as FileSystem from 'expo-file-system/legacy';
import * as ImagePicker from 'expo-image-picker';
import { useRef, useState } from 'react';

import { ActivityIndicator, Alert, Button, Image, StyleSheet, Text, TouchableOpacity, View } from 'react-native';
export default function HomeScreen() {
  const [facing, setFacing] = useState<CameraType>('back');
  const [permission, requestPermission] = useCameraPermissions();
  const [photo, setPhoto] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const cameraRef = useRef<CameraView>(null);
  const API_KEY = 'AIzaSyDPCzqZQZtz3nQn-RSiLGNdhuZIEkREB90';
  const GEMINIAPI_KEY = 'AIzaSyDPQWUa9c_cANL9fpNb4YxP0dOLBzzCUTk';
  if (!permission) {
    return <View />;
  }

  if (!permission.granted) {
    return (
      <View style={styles.container}>
        <Text style={{ textAlign: 'center' }}>We need your permission to show the camera</Text>
        <Button onPress={requestPermission} title="grant permission" />
      </View>
    );
  }

  const pickImage = async () => {
    // No permissions request is necessary for launching the image library

    const permissionResult = await ImagePicker.requestMediaLibraryPermissionsAsync();

    if (permissionResult.granted === false) {
      Alert.alert('Permission Denied', "You've refused to allow this app to access your photos!");
      return;
    }
    let result = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: false,
      quality: 1,
    });
    if (!result.canceled && result.assets && result.assets[0].uri != null) {
      setPhoto(result.assets[0].uri);
    }
  };
  function toggleCameraFacing() {
    setFacing((current) => (current === 'back' ? 'front' : 'back'));
  }

  async function takePicture() {
    if (cameraRef.current) {
      try {
        const photoData = await cameraRef.current.takePictureAsync();
        setPhoto(photoData?.uri || null);
        console.log('Photo:', photoData);
      } catch (error) {
        console.error('Error taking picture:', error);
        Alert.alert('Error', 'Failed to take picture');
      }
    }
  }

  function retakePicture() {
    setPhoto(null);
  }

  async function analyzeImage() {
    if (!photo) return;

    setLoading(true);
    try {
      // Đọc ảnh và chuyển sang base64
      const base64 = await FileSystem.readAsStringAsync(photo, {
        encoding: 'base64',
      });

      // Gửi request đến Google Cloud Vision API
      const response = await axios.post(`https://vision.googleapis.com/v1/images:annotate?key=${API_KEY}`, {
        requests: [
          {
            image: {
              content: base64,
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
        const detectedText = textAnnotations[0].description;
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
  "ngay_xo_so": "dd-mm-yyyy | null",
  "dai_xo_so": "string | null"
}

QUY TẮC TRÍCH XUẤT:
- Chỉ trích xuất dữ liệu nếu xuất hiện RÕ RÀNG trong OCR
- Không suy đoán
- Không tự sửa định dạng
- Nếu không tìm thấy → trả về null cho field đó
- Tên đài phải được chuẩn hoá (ví dụ: "BÌNH THUẬN" → "Bình Thuận")

Văn bản OCR:

${detectedText}`,
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
              'x-goog-api-key': GEMINIAPI_KEY,
              'Content-Type': 'application/json',
            },
          },
        );
        const data = genResponse.data.candidates[0].content.parts[0].text;
        console.log('Response from generative language model:', data);
        Alert.alert('Kết quả phân tích', data);
        // TODO gửi data về backend để lưu vào database và trả lại kết quả cho người dùng
      } else {
        Alert.alert('Thông báo', 'Không phát hiện văn bản nào trong ảnh');
      }
    } catch (error: any) {
      console.error('Error analyzing image:', error);
      const errorMessage = error.response?.data?.error?.message || error.message || 'Không thể phân tích ảnh';
      Alert.alert('Lỗi', errorMessage);
    } finally {
      setLoading(false);
    }
  }

  if (photo) {
    return (
      <View style={styles.container}>
        <Image source={{ uri: photo }} style={styles.preview} />
        {loading && (
          <View style={styles.loadingOverlay}>
            <ActivityIndicator size="large" color="#fff" />
            <Text style={styles.loadingText}>Đang phân tích ảnh...</Text>
          </View>
        )}
        <View style={styles.previewButtons}>
          <TouchableOpacity style={styles.button} onPress={retakePicture} disabled={loading}>
            <Text style={styles.text}>Chụp lại</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.button} onPress={analyzeImage} disabled={loading}>
            <Text style={styles.text}>Phân tích</Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      <CameraView ref={cameraRef} style={styles.camera} facing={facing}>
        <View style={styles.buttonContainer}>
          <TouchableOpacity style={styles.button} onPress={toggleCameraFacing}>
            <Text style={styles.text}>Flip Camera</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.captureButton} onPress={takePicture}>
            <Text style={styles.text}>📷</Text>
          </TouchableOpacity>
        </View>
        <View style={styles.previewButtons}>
          <TouchableOpacity style={styles.button} onPress={pickImage}>
            <Text style={styles.text}>Chọn từ thư viện</Text>
          </TouchableOpacity>
        </View>
      </CameraView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  camera: {
    flex: 1,
  },
  preview: {
    flex: 1,
    resizeMode: 'contain',
  },
  buttonContainer: {
    flex: 1,
    flexDirection: 'row',
    backgroundColor: 'transparent',
    margin: 64,
  },
  previewButtons: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    padding: 20,
    backgroundColor: 'black',
  },
  button: {
    alignSelf: 'flex-end',
    alignItems: 'center',
    padding: 15,
  },
  captureButton: {
    flex: 1,
    alignSelf: 'flex-end',
    alignItems: 'center',
    backgroundColor: 'rgba(255, 255, 255, 0.3)',
    borderRadius: 50,
    padding: 15,
  },
  text: {
    fontSize: 24,
    fontWeight: 'bold',
    color: 'white',
  },
  loadingOverlay: {
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.7)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingText: {
    color: 'white',
    marginTop: 10,
    fontSize: 16,
  },
});
