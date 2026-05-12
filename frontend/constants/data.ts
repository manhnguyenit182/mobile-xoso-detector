export type Region = 'MN' | 'MT' | 'MB';

export const REGION_LABELS: Record<Region, string> = {
  MN: '🌴 Miền Nam',
  MT: '🏖️ Miền Trung',
  MB: '❄️ Miền Bắc',
};

export const PROVINCES: Record<Region, string[]> = {
  MN: [
    'Hồ Chí Minh', 'Đồng Tháp', 'Cà Mau', 'Bến Tre', 'Vũng Tàu',
    'Bạc Liêu', 'Đồng Nai', 'Cần Thơ', 'Sóc Trăng', 'Tây Ninh',
    'An Giang', 'Bình Thuận', 'Vĩnh Long', 'Bình Dương', 'Trà Vinh',
    'Long An', 'Bình Phước', 'Hậu Giang', 'Tiền Giang', 'Kiên Giang', 'Đà Lạt',
  ],
  MT: [
    'Đà Nẵng', 'Quảng Nam', 'Quảng Ngãi', 'Bình Định', 'Phú Yên',
    'Khánh Hòa', 'Kon Tum', 'Gia Lai', 'Đắk Lắk', 'Đắk Nông',
    'Quảng Bình', 'Quảng Trị', 'Thừa Thiên Huế', 'Ninh Thuận',
  ],
  MB: [
    'Hà Nội', 'Hải Phòng', 'Quảng Ninh', 'Bắc Ninh', 'Bắc Giang',
    'Hải Dương', 'Hưng Yên', 'Vĩnh Phúc', 'Hà Nam', 'Nam Định',
    'Thái Bình', 'Ninh Bình', 'Thái Nguyên', 'Tuyên Quang', 'Lào Cai',
    'Yên Bái', 'Phú Thọ', 'Điện Biên', 'Hòa Bình', 'Sơn La',
  ],
};
