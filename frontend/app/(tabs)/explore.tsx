import { useState } from 'react';
import {
  Alert,
  FlatList,
  Modal,
  Platform,
  Pressable,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View,
} from 'react-native';
import { Dimensions } from 'react-native';
import { DateTimePickerAndroid } from '@react-native-community/datetimepicker';
import ConfettiCannon from 'react-native-confetti-cannon';
import { checkTicketManually } from '../../services/imageAnalysisService';

const { width: SCREEN_W } = Dimensions.get('window');
import { formatPrizeAmount } from '../../services/ticketHistoryService';
import { Brand, PrizeColors } from '../../constants/theme';

// ─────────────────────────────────────────────────────────────────────────────
// Data
// ─────────────────────────────────────────────────────────────────────────────

type Region = 'MN' | 'MT' | 'MB';

const REGION_LABELS: Record<Region, string> = {
  MN: '🌴 Miền Nam',
  MT: '🏖️ Miền Trung',
  MB: '❄️ Miền Bắc',
};

const PROVINCES: Record<Region, string[]> = {
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

// ─────────────────────────────────────────────────────────────────────────────
// Component
// ─────────────────────────────────────────────────────────────────────────────

export default function ManualCheckScreen() {
  const [region, setRegion] = useState<Region>('MN');
  const [ticketNumber, setTicketNumber] = useState('');
  const [drawDate, setDrawDate] = useState('');
  const [provinceName, setProvinceName] = useState('');
  const [modalVisible, setModalVisible] = useState(false);
  const [loading, setLoading] = useState(false);
  const [prizes, setPrizes] = useState<any[] | null>(null);

  const provinces = PROVINCES[region];

  const showDatePicker = () => {
    DateTimePickerAndroid.open({
      value: drawDate ? new Date(drawDate) : new Date(),
      onChange: (event, selectedDate) => {
        if (event.type === 'set' && selectedDate) {
          const d = selectedDate.toISOString().split('T')[0];
          setDrawDate(d);
        }
      },
      mode: 'date',
    });
  };

  const handleRegionChange = (r: Region) => {
    setRegion(r);
    setProvinceName('');
  };

  const handleSubmit = async () => {
    if (!ticketNumber || !drawDate || !provinceName) {
      Alert.alert('Thiếu thông tin', 'Vui lòng nhập đầy đủ: Số vé, Ngày và Đài xổ số.');
      return;
    }
    const digits = region === 'MB' ? 5 : 6;
    if (ticketNumber.length !== digits || !/^\d+$/.test(ticketNumber)) {
      Alert.alert('Số vé không hợp lệ', `Vé ${REGION_LABELS[region].split(' ').pop()} cần ${digits} chữ số.`);
      return;
    }

    setLoading(true);
    setPrizes(null);
    try {
      const data = await checkTicketManually({
        so_ve: ticketNumber,
        ngay_xo_so: drawDate,
        dai_xo_so: provinceName,
      });
      setPrizes(data);
    } catch (error: any) {
      Alert.alert('Lỗi', error.response?.data?.message || error.message || 'Không thể kết nối backend');
    } finally {
      setLoading(false);
    }
  };

  const hasWon = prizes && prizes.length > 0;
  const totalPrize = (prizes ?? []).reduce((s: number, p: any) => s + (p.prizeAmount ?? 0), 0);

  return (
    <View style={{ flex: 1 }}>
      <ScrollView style={s.container} contentContainerStyle={s.content} keyboardShouldPersistTaps="handled">
        <StatusBar barStyle="light-content" backgroundColor={Brand.darkBg} />

      {/* Header */}
      <View style={s.header}>
        <Text style={s.headerTitle}>🎟️ Dò Vé Thủ Công</Text>
        <Text style={s.headerSub}>Nhập thông tin vé để kiểm tra kết quả</Text>
      </View>

      {/* Region Selector */}
      <View style={s.section}>
        <Text style={s.label}>Chọn miền</Text>
        <View style={s.regionRow}>
          {(Object.keys(REGION_LABELS) as Region[]).map((r) => (
            <TouchableOpacity
              key={r}
              style={[s.regionChip, region === r && s.regionChipActive]}
              onPress={() => handleRegionChange(r)}
            >
              <Text style={[s.regionChipText, region === r && s.regionChipTextActive]}>
                {REGION_LABELS[r]}
              </Text>
            </TouchableOpacity>
          ))}
        </View>
      </View>

      {/* Ticket Number */}
      <View style={s.section}>
        <Text style={s.label}>
          Số Vé ({region === 'MB' ? '5 số' : '6 số'})
        </Text>
        <TextInput
          style={s.input}
          placeholder={region === 'MB' ? 'Nhập 5 số...' : 'Nhập 6 số...'}
          placeholderTextColor="#555"
          value={ticketNumber}
          keyboardType="numeric"
          maxLength={region === 'MB' ? 5 : 6}
          onChangeText={setTicketNumber}
        />
      </View>

      {/* Draw Date */}
      <View style={s.section}>
        <Text style={s.label}>Ngày Xổ Số</Text>
        <Pressable style={s.datePicker} onPress={showDatePicker}>
          <Text style={[s.datePickerText, !drawDate && s.placeholder]}>
            {drawDate || 'Chọn ngày xổ số...'}
          </Text>
          <Text style={s.datePickerIcon}>📅</Text>
        </Pressable>
      </View>

      {/* Province Selector */}
      <View style={s.section}>
        <Text style={s.label}>Đài Xổ Số</Text>
        <Pressable style={s.datePicker} onPress={() => setModalVisible(true)}>
          <Text style={[s.datePickerText, !provinceName && s.placeholder]}>
            {provinceName || 'Chọn đài xổ số...'}
          </Text>
          <Text style={s.datePickerIcon}>🏛️</Text>
        </Pressable>
      </View>

      {/* Submit Button */}
      <TouchableOpacity
        style={[s.submitBtn, loading && s.disabled]}
        onPress={handleSubmit}
        disabled={loading}
      >
        <Text style={s.submitBtnText}>
          {loading ? '⏳ Đang dò vé...' : '🔍 Dò Vé Ngay'}
        </Text>
      </TouchableOpacity>

      {/* Result */}
      {prizes !== null && (
        <View style={[s.resultCard, hasWon ? s.resultWon : s.resultLost]}>
          {hasWon ? (
            <>
              <Text style={s.resultEmoji}>🎉</Text>
              <Text style={s.resultTitle}>CHÚC MỪNG!</Text>
              <Text style={s.resultSub}>Tổng thưởng: {formatPrizeAmount(totalPrize)}</Text>
              {prizes.map((prize: any, idx: number) => (
                <View key={idx} style={[s.prizeItem, { borderLeftColor: PrizeColors[prize.prizeLevel] ?? Brand.neon }]}>
                  <Text style={[s.prizeLabel, { color: PrizeColors[prize.prizeLevel] ?? Brand.neon }]}>
                    {prize.prizeLevel}
                  </Text>
                  <Text style={s.prizeMoney}>{formatPrizeAmount(prize.prizeAmount)}</Text>
                </View>
              ))}
            </>
          ) : (
            <>
              <Text style={s.resultEmoji}>😔</Text>
              <Text style={s.resultTitle}>KHÔNG TRÚNG</Text>
              <Text style={s.resultSub}>Vé <Text style={{ color: Brand.gold, fontWeight: '800' }}>{ticketNumber}</Text> chưa trúng lần này</Text>
            </>
          )}
        </View>
      )}

      {/* Province Modal */}
      <Modal
        visible={modalVisible}
        transparent
        animationType="slide"
        onRequestClose={() => setModalVisible(false)}
      >
        <View style={s.modalOverlay}>
          <View style={s.modalBox}>
            <View style={s.modalHeader}>
              <Text style={s.modalTitle}>Chọn Đài Xổ Số</Text>
              <TouchableOpacity onPress={() => setModalVisible(false)}>
                <Text style={s.modalClose}>✕</Text>
              </TouchableOpacity>
            </View>
            <FlatList
              data={provinces}
              keyExtractor={(item) => item}
              renderItem={({ item }) => (
                <Pressable
                  style={({ pressed }) => [s.provinceItem, pressed && s.provinceItemPressed]}
                  onPress={() => { setProvinceName(item); setModalVisible(false); }}
                >
                  <Text style={s.provinceItemText}>{item}</Text>
                  {item === provinceName && <Text style={s.provinceCheck}>✓</Text>}
                </Pressable>
              )}
            />
          </View>
        </View>
      </Modal>
      </ScrollView>

      {/* Confetti */}
      {hasWon && (
        <ConfettiCannon
          count={200}
          origin={{ x: SCREEN_W / 2, y: -20 }}
          autoStart={true}
          fadeOut={true}
          fallSpeed={3000}
        />
      )}
    </View>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Styles
// ─────────────────────────────────────────────────────────────────────────────

const s = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Brand.darkBg,
  },
  content: {
    paddingBottom: 60,
  },
  header: {
    paddingTop: 54,
    paddingBottom: 24,
    paddingHorizontal: 20,
    backgroundColor: Brand.darkCard,
    borderBottomWidth: 1,
    borderBottomColor: Brand.darkCard2,
  },
  headerTitle: {
    color: '#FFF',
    fontSize: 24,
    fontWeight: '900',
    letterSpacing: 0.5,
  },
  headerSub: {
    color: '#888',
    fontSize: 14,
    marginTop: 4,
  },
  section: {
    marginHorizontal: 16,
    marginTop: 20,
  },
  label: {
    color: '#AAA',
    fontSize: 13,
    fontWeight: '600',
    letterSpacing: 0.5,
    marginBottom: 8,
    textTransform: 'uppercase',
  },
  regionRow: {
    flexDirection: 'row',
    gap: 10,
  },
  regionChip: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 12,
    backgroundColor: Brand.darkCard,
    alignItems: 'center',
    borderWidth: 1.5,
    borderColor: Brand.darkCard2,
  },
  regionChipActive: {
    backgroundColor: Brand.primary,
    borderColor: Brand.primaryLight,
  },
  regionChipText: {
    color: '#888',
    fontSize: 12,
    fontWeight: '700',
  },
  regionChipTextActive: {
    color: '#FFF',
  },
  input: {
    backgroundColor: Brand.darkCard,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: Brand.darkCard2,
    color: '#FFF',
    fontSize: 22,
    fontWeight: '800',
    letterSpacing: 4,
    padding: 16,
    textAlign: 'center',
  },
  datePicker: {
    backgroundColor: Brand.darkCard,
    borderRadius: 12,
    borderWidth: 1.5,
    borderColor: Brand.darkCard2,
    padding: 16,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  datePickerText: {
    color: '#FFF',
    fontSize: 15,
    fontWeight: '600',
  },
  placeholder: {
    color: '#555',
    fontWeight: '400',
  },
  datePickerIcon: {
    fontSize: 20,
  },
  submitBtn: {
    margin: 20,
    marginTop: 28,
    backgroundColor: Brand.primary,
    paddingVertical: 18,
    borderRadius: 16,
    alignItems: 'center',
    shadowColor: Brand.primary,
    shadowOffset: { width: 0, height: 6 },
    shadowOpacity: 0.5,
    shadowRadius: 12,
    elevation: 8,
  },
  submitBtnText: {
    color: '#FFF',
    fontSize: 17,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  disabled: {
    opacity: 0.6,
  },
  // Result
  resultCard: {
    marginHorizontal: 16,
    borderRadius: 20,
    padding: 24,
    alignItems: 'center',
    borderWidth: 1.5,
  },
  resultWon: {
    backgroundColor: 'rgba(255,215,0,0.06)',
    borderColor: Brand.gold,
  },
  resultLost: {
    backgroundColor: 'rgba(255,59,92,0.06)',
    borderColor: Brand.danger,
  },
  resultEmoji: {
    fontSize: 48,
    marginBottom: 8,
  },
  resultTitle: {
    color: '#FFF',
    fontSize: 22,
    fontWeight: '900',
    letterSpacing: 1,
  },
  resultSub: {
    color: '#AAA',
    fontSize: 14,
    marginTop: 6,
    marginBottom: 16,
    textAlign: 'center',
  },
  prizeItem: {
    width: '100%',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 12,
    paddingLeft: 14,
    marginBottom: 8,
    borderLeftWidth: 4,
    backgroundColor: 'rgba(255,255,255,0.04)',
    borderRadius: 8,
  },
  prizeLabel: {
    fontSize: 13,
    fontWeight: '700',
  },
  prizeMoney: {
    color: '#FFF',
    fontSize: 15,
    fontWeight: '800',
  },
  // Modal
  modalOverlay: {
    flex: 1,
    backgroundColor: 'rgba(0,0,0,0.7)',
    justifyContent: 'flex-end',
  },
  modalBox: {
    backgroundColor: Brand.darkCard,
    borderTopLeftRadius: 24,
    borderTopRightRadius: 24,
    maxHeight: '75%',
    paddingBottom: 20,
  },
  modalHeader: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 20,
    borderBottomWidth: 1,
    borderBottomColor: Brand.darkCard2,
  },
  modalTitle: {
    color: '#FFF',
    fontSize: 18,
    fontWeight: '700',
  },
  modalClose: {
    color: '#888',
    fontSize: 20,
    fontWeight: '700',
  },
  provinceItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 20,
    paddingVertical: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: Brand.darkCard2,
  },
  provinceItemPressed: {
    backgroundColor: Brand.darkCard2,
  },
  provinceItemText: {
    color: '#DDD',
    fontSize: 16,
  },
  provinceCheck: {
    color: Brand.success,
    fontSize: 18,
    fontWeight: '700',
  },
});
