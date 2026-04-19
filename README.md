<div align="center">
  <h1>🎰 Mobile Xổ Số Detector</h1>
  <p><i>Ứng dụng AI nhận diện và tra cứu kết quả xổ số đa miền thông minh, nhanh và chính xác</i></p>
  
  <p align="center">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot">
    <img src="https://img.shields.io/badge/React_Native-Expo-blue?style=for-the-badge&logo=react" alt="React Native">
    <img src="https://img.shields.io/badge/TypeScript-5.x-blue?style=for-the-badge&logo=typescript" alt="TypeScript">
    <img src="https://img.shields.io/badge/PostgreSQL-latest-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
    <img src="https://img.shields.io/badge/Gemini_AI-Flash_3.1--Lite-orange?style=for-the-badge&logo=google-gemini" alt="Gemini AI">
  </p>
</div>

---

**Mobile Xổ Số Detector** mang đến giải pháp tra cứu kết quả xổ số kiến thiết tự động và thuận tiện. Tích hợp công nghệ nhận diện hình ảnh của Google và AI, hệ thống có khả năng phân tích vé số qua thao tác chụp ảnh và đối chiếu kết quả ngay lập tức.

Hệ thống cung cấp kết quả xổ số cập nhật liên tục cho cả 3 miền Bắc, Trung, Nam, đảm bảo tính chính xác thông qua quy trình thu thập dữ liệu (crawling) tự động hàng ngày.

---

## 📖 Mục Lục

- [✨ Tính Năng Nổi Bật](#-tính-năng-nổi-bật)
- [🛠️ Kiến Trúc Công Nghệ](#️-kiến-trúc-công-nghệ)
- [🚀 Hướng Dẫn Cài Đặt Lên Môi Trường Cục Bộ](#-hướng-dẫn-cài-đặt-lên-môi-trường-cục-bộ)
- [📝 Hướng Dẫn Sử Dụng](#-hướng-dẫn-sử-dụng)

---

## ✨ Tính Năng Nổi Bật

- **🔍 AI Camera Scanner:** Sử dụng công nghệ OCR từ Google Vision kết hợp cùng Gemini 3.1 Flash Lite để đọc và phân tích dữ liệu trên vé số với độ chính xác cao.
- **🗺️ Hỗ Trợ Đa Miền:** Thuật toán tự động nhận diện và xử lý linh hoạt cho các loại vé khác nhau (miền Bắc 5 số, miền Trung/Nam 6 số) đi kèm dữ liệu giải thưởng tương ứng.
- **⚡ Cập Nhật Tự Động (Auto Crawl):** Thu thập dữ liệu kết quả phân giải tự động hàng ngày theo khung giờ quay số tương ứng.
- **📚 Quản Lý Lịch Sử Tiện Lợi:** Hỗ trợ theo dõi trạng thái vé số đã quét với dữ liệu được lưu trữ an toàn trên thiết bị thông qua AsyncStorage.
- **✨ Trải Nghiệm Người Dùng Tối Ưu:** Giao diện hỗ trợ Dark Mode thân thiện, tích hợp hiệu ứng phản hồi xúc giác (Haptics) và các hiệu ứng hình ảnh sống động nhằm tăng cường trải nghiệm.

---

## 🛠️ Kiến Trúc Công Nghệ

Dự án áp dụng kiến trúc phân tách Frontend và Backend rõ ràng nhằm đảm bảo bảo mật cho các hệ thống và dịch vụ liên quan.

### 📱 **Frontend (Mobile App)**

- **Nền tảng:** React Native, Expo Router.
- **Tính năng nổi bật:** Expo Camera, AsyncStorage để lưu trữ dữ liệu cục bộ.
- **Thư viện UI/UX:** react-native-confetti-cannon hỗ trợ hiệu ứng hiển thị.

### ⚙️ **Backend (RESTful API Server)**

- **Core Framework:** Spring Boot 3.4 (Java 17).
- **Cơ sở dữ liệu:** PostgreSQL (Tích hợp Hibernate cho việc quản lý mô hình dữ liệu).
- **Công nghệ tích hợp:**
  - `Google Vision API`: Nhận diện và trích xuất chuỗi văn bản (OCR).
  - `Gemini 3.1 Flash Lite API`: Phân tích và cấu trúc hóa chuỗi OCR để tạo ra dữ liệu chuẩn hóa.
  - `Jsoup`: Trích xuất dữ liệu kết quả từ các trang thông tin xổ số uy tín.

---

## 🚀 Hướng Dẫn Cài Đặt Lên Môi Trường Cục Bộ

### Bước 1: Chuẩn bị mã nguồn

Mở terminal và thực thi luồng lệnh sau:

```bash
git clone https://github.com/manhnguyenit182/mobile-xoso-detector.git
cd mobile-xoso-detector
```

### Bước 2: Kích hoạt Backend

1. Di chuyển vào thư mục backend: `cd backend`
2. Tạo file cấu hình từ file mẫu: `cp .env.example application.properties` (Đối với Windows: `copy .env.example application.properties`).
3. Chỉnh sửa `application.properties` để thêm các thông tin cấu hình và API Key cần thiết.
4. Khởi chạy server:
   ```bash
   ./mvnw spring-boot:run
   ```
   > Đợi đến khi Console hiển thị log `Started XosoApplication` xác nhận máy chủ đã sẵn sàng tại cổng 8080.

### Bước 3: Kích hoạt Frontend

1. Từ thư mục gốc, bảo đảm server vẫn đang chạy, mở tab terminal mới và chuyển tiếp: `cd frontend`
2. Cài đặt các gói thư viện cần thiết: `npm install`
3. Thiết lập biến môi trường: `cp .env.example .env` 
   - *Lưu ý:* Cập nhật giá trị `EXPO_PUBLIC_BACKEND_URL` trong file `.env` trỏ tới địa chỉ IP của máy chú định tuyến local, ví dụ `http://192.168.x.x:8080` (hoặc sửa đổi hostname tương ứng).
4. Khởi chạy môi trường phát triển Expo:
   ```bash
   npm start
   ```
5. Quét mã QR được cung cấp bằng ứng dụng **Expo Go** trên thiết bị di động để trải nghiệm ứng dụng.

---

## 📝 Hướng Dẫn Sử Dụng

- **Tra Cứu Bằng Camera:** Chuyển qua tab **📸 Quét Vé**, cấp quyền cho máy ảnh. Đóng khung vé số vào vùng lưới chỉ định và chụp. Kết quả trúng thưởng sẽ hiển thị ngay sau đó.
- **Tra Cứu Thủ Công:** Sử dụng tab **🔍 Dò Tay** làm tùy chọn tìm kiếm truyền thống. Lựa chọn nhà đài mong muốn và điền các chữ số vé theo thứ tự để xem kết quả.
- **Xem Lại Lịch Sử Điểm Tra Cứu:** Tab **📜 Lịch Sử** lưu trữ danh sách các vé đã qua xử lý, được phân loại rõ ràng bằng mã màu nhằm theo dõi tình trạng trúng giải.

---

<div align="center">
  <i>Hệ thống được phát triển nhằm mục đích cung cấp trải nghiệm tra cứu tiện lợi và dễ dàng nhất cho người dùng.</i>
</div>
