/**
 * Hệ thống màu sắc cho Xổ Số Detector App
 * Phong cách: Rực rỡ, Premium, Dark Mode
 * Palette: Gold & Deep Purple với accent đỏ/xanh neon
 */

export const Colors = {
  light: {
    text: '#1A0A2E',
    background: '#F5F0FF',
    tint: '#6B1FE8',
    icon: '#6B1FE8',
    tabIconDefault: '#A89BC8',
    tabIconSelected: '#6B1FE8',
    card: '#FFFFFF',
    border: '#E8E0FF',
    notification: '#FF3B5C',
  },
  dark: {
    text: '#F0EAFF',
    background: '#0D0620',
    tint: '#C084FC',
    icon: '#C084FC',
    tabIconDefault: '#5B4B7B',
    tabIconSelected: '#C084FC',
    card: '#1C1035',
    border: '#2D1B5E',
    notification: '#FF3B5C',
  },
};

/** Màu chủ đạo (dùng cho gradient, highlight) */
export const Brand = {
  /** Tím đậm – chủ đạo */
  primary: '#6B1FE8',
  /** Tím sáng – hover/active */
  primaryLight: '#9B5DE5',
  /** Vàng kim */
  gold: '#FFD700',
  /** Vàng nhạt */
  goldLight: '#FFE87C',
  /** Đỏ – cảnh báo / không trúng */
  danger: '#FF3B5C',
  /** Xanh lá – trúng giải */
  success: '#00E5A0',
  /** Xanh neon – accent */
  neon: '#00D4FF',
  /** Nền dark chính */
  darkBg: '#0D0620',
  /** Nền card dark */
  darkCard: '#1C1035',
  /** Nền card dark 2 */
  darkCard2: '#2A1756',
  /** Gradient chính */
  gradientStart: '#6B1FE8',
  gradientEnd: '#FF3B5C',
};

/** Map nhãn giải → màu */
export const PrizeColors: Record<string, string> = {
  'ĐẶC BIỆT': '#FFD700',
  'GIẢI NHẤT': '#C084FC',
  'GIẢI NHÌ': '#818CF8',
  'GIẢI BA': '#38BDF8',
  'GIẢI TƯ': '#34D399',
  'GIẢI NĂM': '#A3E635',
  'GIẢI SÁU': '#FB923C',
  'GIẢI BẢY': '#F472B6',
  'GIẢI TÁM': '#94A3B8',
  'KHUYẾN KHÍCH 1': '#FCD34D',
  'KHUYẾN KHÍCH 2': '#FDE68A',
};
