# 🎰 Mobile Xổ Số Detector

**Mobile Xổ Số Detector** là một ứng dụng di động thông minh giúp người dùng dò kết quả xổ số kiến thiết tự động một cách nhanh chóng và chính xác. Bằng việc kết hợp công nghệ nhận dạng hình ảnh (OCR) Google Vision và Trí tuệ nhân tạo (Gemini AI), ứng dụng cho phép người dùng chỉ cần chụp ảnh tờ vé số để biết ngay kết quả cùng số tiền trúng thưởng.

Ngoài ra, hệ thống cũng hỗ trợ dò vé thủ công cho cả 3 miền (Bắc, Trung, Nam) với dữ liệu được cập nhật tự động hàng ngày.

---

## ✨ Tính Năng Nổi Bật

- 📸 **Camera AI Scanner:** Tự động nhận diện chữ (OCR) trên tờ vé số và phân tích thông tin bằng Gemini AI.
- 🎯 **Hỗ Trợ 3 Miền:** Thuật toán dò vé xử lý linh hoạt vé miền Bắc (5 số) và miền Trung/Nam (6 số).
- 🏆 **Cơ Cấu Giải Thưởng Chính Xác:** Tính toán và hiển thị số tiền trúng thưởng theo đúng cơ cấu giải của từng miền.
- 🕒 **Crawl Tự Động:** Hệ thống tự động thu thập kết quả cập nhật sớm nhất từ các đài xổ số.
- 📜 **Lịch Sử Vé Quét:** Tích hợp bộ nhớ tạm lưu trữ và quản lý lại những tờ vé số đã kiểm tra.
- 🎨 **Giao Diện Premium:** Trải nghiệm thiết kế Dark Mode cực xịn, cùng hiệu ứng Confetti chúc mừng khi trúng giải lớn.

---

## 🛠️ Kiến Trúc Công Nghệ

Dự án bao gồm 2 phần chính:

### 1. Frontend (Mobile App)
- **Framework:** React Native & Expo
- **Giao diện:** Expo Router, StyleSheet (Dark Mode premium palette)
- **Chức năng:** Expo Camera, Async Storage (lưu lịch sử)
- **Kết nối:** Axios (gọi API backend)

### 2. Backend (Server)
- **Framework:** Spring Boot 3.4
- **Database:** PostgreSQL (Lưu trữ lịch sử số quay)
- **Công nghệ lõi:**
  - `Google Vision API` để OCR hình ảnh.
  - `Gemini AI API` (gemini-flash) phân tích ngữ nghĩa bóc tách ngày, số vé, đài quay.
  - `Jsoup` để cào (crawl) dữ liệu xổ số tự động.

---

## 🚀 Hướng Dẫn Cài Đặt

### Bước 1: Sao chép dự án
```bash
git clone https://github.com/manhnguyenit182/mobile-xoso-detector.git
cd mobile-xoso-detector
```

### Bước 2: Cài đặt Backend
1. Di chuyển vào thư mục backend:
   ```bash
   cd backend
   ```
2. Cấu hình biến môi trường:
   - Copy file `.env.example` thành `application.properties` (hoặc cấu hình trực tiếp biến hệ thống).
   - Điền các thông tin API_KEY của bạn vào file.
3. Chạy server Spring Boot:
   ```bash
   ./mvnw spring-boot:run
   ```

### Bước 3: Cài đặt Frontend
1. Mở một terminal mới, di chuyển vào thư mục frontend:
   ```bash
   cd frontend
   ```
2. Cài đặt các thư viện cần thiết:
   ```bash
   npm install
   ```
3. Cấu hình biến môi trường:
   - Copy file `.env.example` thành `.env`.
   - Điền đường dẫn BACKEND_URL của bạn (có thể dùng ngrok nếu bạn chạy ứng dụng trên thiết bị thật).
4. Khởi chạy ứng dụng:
   ```bash
   npm start
   ```
5. Dùng ứng dụng Expo Go trên điện thoại để quét mã QR và trải nghiệm.

---

## 📝 Hướng Dẫn Sử Dụng

1. **Quét Vé Số Đoán Thưởng Thần Tốc:**
   - Tại màn hình chính, chọn qua **Quét Vé**.
   - Hướng camera vào tờ vé số, sao cho vừa vặn khung nhận diện.
   - Bấm vào nút chụp. AI sẽ lo phần còn lại và trả cho bạn số tiền trúng thưởng kèm hiệu ứng nếu may mắn!

2. **Dò Vé Thủ Công Chính Xác:**
   - Chuyển qua tab **Dò Tay**.
   - Lựa chọn Miền Bắc/Trung/Nam.
   - Nhập thông tin: Ngày xổ số, Tên Đài, Số Vé (5 số cho MB, 6 số cho MN/MT).
   - Chọn "Tìm Kiếm" và nhận kết quả tức thì.

3. **Xem Lại Lịch Sử Vé Số Đã Mua:**
   - Bấm vào tab **Lịch Sử**.
   - Ứng dụng sẽ lưu lại rảnh rỗi chờ ngày rinh tài lộc. Bạn cũng có thể lọc theo từng trạng thái (Trúng, Trượt...).

---

## 🛡️ Bảo Mật
Dự án được bảo vệ an toàn bằng cách ẩn toàn bộ API Key (Google Vision & Gemini) dưới Backend. Tuyệt đối **KHÔNG** đưa bất kì key nhạy cảm nào vào Frontend. 
Đảm bảo bạn không commit các file `.env` chứa API thật lên GitHub!

---
*Chúc bạn có những trải nghiệm tuyệt vời và trúng Vietlott/Kiến thiết dài dài với Mobile Xổ Số Detector! 🍀*
