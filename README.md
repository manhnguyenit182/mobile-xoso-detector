<div align="center">
  <h1>🎰 Mobile Xổ Số Detector</h1>
  <p><i>Ứng dụng AI dò vé số đa miền thông minh, nhanh và chính xác nhất</i></p>
  
  <p align="center">
    <img src="https://img.shields.io/badge/Spring_Boot-3.4-brightgreen?style=for-the-badge&logo=springboot" alt="Spring Boot">
    <img src="https://img.shields.io/badge/React_Native-Expo-blue?style=for-the-badge&logo=react" alt="React Native">
    <img src="https://img.shields.io/badge/TypeScript-5.x-blue?style=for-the-badge&logo=typescript" alt="TypeScript">
    <img src="https://img.shields.io/badge/PostgreSQL-latest-blue?style=for-the-badge&logo=postgresql" alt="PostgreSQL">
    <img src="https://img.shields.io/badge/Gemini_AI-Flash_3.1--Lite-orange?style=for-the-badge&logo=google-gemini" alt="Gemini AI">
  </p>
</div>

---

**Mobile Xổ Số Detector** không chỉ là một công cụ tiện ích, mà là một trải nghiệm trọn vẹn dành cho người chơi xổ số kiến thiết. Bạn sẽ không bao giờ cần phải tra cứu thủ công đau mắt nữa. Chỉ với một thao tác chụp ảnh đơn giản, siêu AI của Google sẽ phân tích tờ vé số và báo kết quả ngay lập tức!

Mọi thông tin về kết quả xổ số của cả 3 miền Bắc - Trung - Nam đều được hệ thống cào (crawl) tự động và liên tục mỗi chiều, đảm bảo tính cập nhật và chính xác tuyệt đối.

---

## 📖 Mục Lục

- [✨ Tính Năng Nổi Bật](#-tính-năng-nổi-bật)
- [🛠️ Kiến Trúc Công Nghệ](#️-kiến-trúc-công-nghệ)
- [🚀 Hướng Dẫn Cài Đặt Lên Môi Trường Cục Bộ](#-hướng-dẫn-cài-đặt-lên-môi-trường-cục-bộ)
- [📝 Hướng Dẫn Sử Dụng Siêu Tốc](#-hướng-dẫn-sử-dụng-siêu-tốc)
- [🛡️ Cấp Độ Bảo Mật](#️-cấp-độ-bảo-mật)

---

## ✨ Tính Năng Nổi Bật

- **🔍 AI Camera Scanner:** Công nghệ OCR sắc nét từ Google Vision kết hợp với trí tuệ nhân tạo Gemini 3.1 Flash Lite mới nhất (03/2026), giúp "đọc" mọi dữ liệu nhòe, mờ trên vé số cực nhạy.
- **🗺️ Hỗ Trợ Đa Miền Trọn Vẹn:** Tự động điều chỉnh thuật toán dựa trên loại vé (Vé miền Bắc 5 số, miền Trung/Nam 6 số) đi kèm dữ liệu cơ cấu giải thưởng chuẩn xác.
- **⚡ Cập Nhật Tự Động (Auto Crawl):** Robot tự rà kết quả siêu tốc mỗi ngày từ 16h15 đến 18h30 tùy đài.
- **📚 Quản Lý Vé Thông Minh:** Tab _Lịch Sử_ xịn sò giúp bạn theo dõi vé nào đang chờ kết quả, vé nào đã trúng hoặc trượt, lưu trữ hoàn toàn tại thiết bị qua AsyncStorage.
- **✨ Trải Nghiệm Premium:** Khoác lên mình giao diện Dark Mode (Tím - Vàng Kim), tích hợp Haptic (rung phản hồi) cực êm và chế độ nổ pháo hoa Confetti hoàng tráng mỗi khi vinh danh người trúng giải.

---

## 🛠️ Kiến Trúc Công Nghệ

Sản phẩm được thiết kế với chuẩn Microservices-like tách biệt Frontend và Backend để đảm bảo quyền riêng tư và bảo mật tuyệt đối cho Key API.

### 📱 **Frontend (Giao diện Mobile App)**

- **Nền tảng:** React Native, Expo Router.
- **Tính năng native:** Expo Camera (kèm lưới ảo), Async Storage.
- **Thư viện UI/UX:** react-native-confetti-cannon cao cấp.

### ⚙️ **Backend (Trung tâm xử lý)**

- **Core Server:** Spring Boot 3.4 (Java 17).
- **Cơ sở dữ liệu:** PostgreSQL (Tự động migrate bằng Hibernate).
- **Công nghệ nhúng:**
  - `Google Vision API`: Nhận diện quang học OCR.
  - `Gemini 3.1 Flash Lite API`: Phân tách chuỗi OCR phức tạp thành DTO JSON sạch.
  - `Jsoup`: Trích xuất dữ liệu xổ số từ HTML các trang web lớn.

---

## 🚀 Hướng Dẫn Cài Đặt Lên Môi Trường Cục Bộ

### Bước 1: Chuẩn bị mã nguồn

Mở terminal và gõ:

```bash
git clone https://github.com/manhnguyenit182/mobile-xoso-detector.git
cd mobile-xoso-detector
```

### Bước 2: Kích hoạt Khối Backend (Server)

1. Truy cập vào phân hệ backend: `cd backend`
2. Tạo file cấu hình từ bản mẫu: `cp .env.example application.properties` (Windows: `copy .env.example application.properties`)
3. Mở file `application.properties` vừa tạo, gắn các API KEY của riêng bạn vào đó.
4. Bật máy chủ:
   ```bash
   ./mvnw spring-boot:run
   ```
   > Đợi đến khi Console hiển thị `Started XosoApplication in...` là Server đã thức giấc tại cổng 8080!

### Bước 3: Kích hoạt Khối Frontend (Mobile App)

1. Mở một terminal mới (không tắt terminal của server), chuyển hướng: `cd frontend`
2. Cài toàn bộ các gói thư viện Node: `npm install`
3. Đổi cấu hình môi trường: `cp .env.example .env` (Lưu ý: Bạn bắt buộc phải vào file `.env` chỉnh biến `EXPO_PUBLIC_BACKEND_URL` trỏ về IP Wifi của máy tính, vd: `http://192.168.1.5:8080` hoặc nếu dùng ngrok thì dán link ngrok vào đây).
4. Khởi động nhà máy Expo:
   ```bash
   npm start
   ```
5. Tải app **Expo Go** trên điện thoại, quét mã QR khổng lồ vừa hiện ra trên Terminal để tận hưởng thành quả!

---

## 📝 Hướng Dẫn Sử Dụng Siêu Tốc

- **Dò vé bằng mắt thần (Camera):** Nhấp sang tab **📸 Quét Vé**, cấp quyền cho máy ảnh. Đưa vé số vừa khít khung đo Vàng Kim và ấn nhẹ nút chụp. 2 giây sau kết quả và tiền thưởng sẽ hiện ra!
- **Dò vé kiểu dân gian (Thủ công):** Nhấp sang tab **🔍 Dò Tay**. Hệ thống trang bị sẵn "Chip 3 cực" để bạn chọn đài (Bắc-Trung-Nam) và tự lo liệu số lượng chữ số tối đa.
- **Kiểm kê tài sản (Lịch sử):** Sang tab **📜 Lịch Sử**, bạn sẽ thấy danh sách vé mình từng rà. Vé đã trúng sẽ highlight màu Vàng, vé tạch màu Xám, vé chưa sổ sẽ nảy màu Neon mòn gót đợi chờ.

---

## 🛡️ Cấp Độ Bảo Mật

- **Sạch sẽ ở Frontend:** Bạn sẽ không bao giờ tìm thấy các khóa bảo mật (Google Vision, Gemini Key, DB Password) bị tuồn vào Frontend.
- **Tất cả ở Backend:** Mọi chìa khóa tài sản đều được giấu khép kín bên trong lớp Backend Java bảo mật.

> ⚠️ **LƯU Ý NGHIÊM NGẶT:** Tuyệt đối không commit (đẩy code lên rạp) các file `application.properties` hoặc `.env` chứa API Key thật của bạn lên GitHub!

---

<div align="center">
  <i>Được xây dựng với 💖 và nỗ lực mang thần tài đến mọi nhà. Chúc may mắn! 🍀</i>
</div>
