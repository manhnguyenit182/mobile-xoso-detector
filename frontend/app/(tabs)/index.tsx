import { CameraType, CameraView, useCameraPermissions } from 'expo-camera';
import * as ImagePicker from 'expo-image-picker';
import * as Haptics from 'expo-haptics';
import { useRef, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Dimensions,
  Image,
  ScrollView,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { analyzeTicketImage, AnalyzeTicketResponse } from '../../services/imageAnalysisService';
import { saveTicketToHistory, formatPrizeAmount } from '../../services/ticketHistoryService';
import { Brand, PrizeColors } from '../../constants/theme';

const { width: SCREEN_W, height: SCREEN_H } = Dimensions.get('window');

// ─────────────────────────────────────────────────────────────────────────────
// Result Screen
// ─────────────────────────────────────────────────────────────────────────────

function ResultScreen({
  result,
  photoUri,
  onRetake,
}: {
  result: AnalyzeTicketResponse;
  photoUri: string;
  onRetake: () => void;
}) {
  const hasWon = result.prizes && result.prizes.length > 0;
  const totalPrize = (result.prizes ?? []).reduce((s, p) => s + p.prizeAmount, 0);

  return (
    <ScrollView style={rs.container} contentContainerStyle={rs.content} bounces={false}>
      {/* Header */}
      <View style={[rs.header, { backgroundColor: hasWon ? Brand.gold : Brand.danger }]}>
        <Text style={rs.emoji}>{hasWon ? '🎉' : result.errorMessage ? '⚠️' : '😔'}</Text>
        <Text style={rs.headerTitle}>
          {hasWon ? 'CHÚC MỪNG!' : result.errorMessage ? 'LỖI NHẬN DẠNG' : 'KHÔNG TRÚNG'}
        </Text>
        {hasWon && (
          <Text style={rs.totalPrize}>Tổng thưởng: {formatPrizeAmount(totalPrize)}</Text>
        )}
      </View>

      {/* Ticket Info */}
      <View style={rs.ticketCard}>
        <Text style={rs.cardTitle}>📋 Thông Tin Vé</Text>
        <InfoRow label="Số vé" value={result.ticketNumber ?? '—'} highlight />
        <InfoRow label="Đài" value={result.provinceCode ?? '—'} />
        <InfoRow label="Ngày xổ" value={result.drawDate ?? '—'} />
      </View>

      {/* Prize List */}
      {hasWon && (
        <View style={rs.prizeCard}>
          <Text style={rs.cardTitle}>🏆 Các Giải Trúng</Text>
          {result.prizes.map((prize, idx) => (
            <View
              key={idx}
              style={[rs.prizeRow, { borderLeftColor: PrizeColors[prize.prizeLevel] ?? Brand.neon }]}
            >
              <Text style={[rs.prizeLevel, { color: PrizeColors[prize.prizeLevel] ?? Brand.neon }]}>
                {prize.prizeLevel}
              </Text>
              <Text style={rs.prizeAmount}>{formatPrizeAmount(prize.prizeAmount)}</Text>
            </View>
          ))}
        </View>
      )}

      {/* Error Message */}
      {result.errorMessage && (
        <View style={rs.errorCard}>
          <Text style={rs.errorText}>{result.errorMessage}</Text>
        </View>
      )}

      {/* Actions */}
      <View style={rs.actions}>
        <TouchableOpacity style={rs.retakeBtn} onPress={onRetake}>
          <Text style={rs.retakeBtnText}>📷 Quét Vé Khác</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

function InfoRow({ label, value, highlight }: { label: string; value: string; highlight?: boolean }) {
  return (
    <View style={rs.infoRow}>
      <Text style={rs.infoLabel}>{label}</Text>
      <Text style={[rs.infoValue, highlight && rs.highlighted]}>{value}</Text>
    </View>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Main Screen
// ─────────────────────────────────────────────────────────────────────────────

export default function ScanScreen() {
  const [facing, setFacing] = useState<CameraType>('back');
  const [permission, requestPermission] = useCameraPermissions();
  const [photo, setPhoto] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<AnalyzeTicketResponse | null>(null);
  const cameraRef = useRef<CameraView>(null);

  if (!permission) return <View style={styles.darkBg} />;

  if (!permission.granted) {
    return (
      <View style={styles.permissionContainer}>
        <Text style={styles.permissionTitle}>📷 Cần Quyền Camera</Text>
        <Text style={styles.permissionDesc}>
          Ứng dụng cần quyền camera để quét và nhận dạng vé số của bạn.
        </Text>
        <TouchableOpacity style={styles.grantBtn} onPress={requestPermission}>
          <Text style={styles.grantBtnText}>Cấp Quyền</Text>
        </TouchableOpacity>
      </View>
    );
  }

  // ── Result view ──
  if (result) {
    return (
      <ResultScreen
        result={result}
        photoUri={photo!}
        onRetake={() => { setResult(null); setPhoto(null); }}
      />
    );
  }

  // ── Preview view ──
  if (photo) {
    return (
      <View style={styles.darkBg}>
        <StatusBar barStyle="light-content" backgroundColor={Brand.darkBg} />
        <View style={styles.previewHeader}>
          <Text style={styles.previewHeaderText}>Kiểm tra ảnh vé số</Text>
        </View>

        <Image source={{ uri: photo }} style={styles.previewImage} resizeMode="contain" />

        {loading && (
          <View style={styles.loadingOverlay}>
            <View style={styles.loadingCard}>
              <ActivityIndicator size="large" color={Brand.gold} />
              <Text style={styles.loadingTitle}>Đang phân tích...</Text>
              <Text style={styles.loadingDesc}>
                OCR → AI → Dò kết quả
              </Text>
            </View>
          </View>
        )}

        <View style={styles.previewActions}>
          <TouchableOpacity
            style={[styles.actionBtn, styles.actionBtnOutline]}
            onPress={() => setPhoto(null)}
            disabled={loading}
          >
            <Text style={styles.actionBtnOutlineText}>↩ Chụp Lại</Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[styles.actionBtn, styles.actionBtnPrimary, loading && styles.disabled]}
            onPress={handleAnalyze}
            disabled={loading}
          >
            <Text style={styles.actionBtnPrimaryText}>
              {loading ? 'Đang xử lý...' : '✨ Phân Tích'}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
    );
  }

  // ── Camera view ──
  return (
    <View style={styles.darkBg}>
      <StatusBar barStyle="light-content" backgroundColor="transparent" translucent />

      <CameraView ref={cameraRef} style={styles.camera} facing={facing}>
        {/* Top bar */}
        <View style={styles.topBar}>
          <View style={styles.topBarInner}>
            <Text style={styles.appTitle}>🎰 XỔ SỐ DETECTOR</Text>
            <TouchableOpacity onPress={() => setFacing(f => f === 'back' ? 'front' : 'back')}>
              <Text style={styles.flipIcon}>🔄</Text>
            </TouchableOpacity>
          </View>
        </View>

        {/* Scan overlay */}
        <View style={styles.scanArea}>
          <View style={styles.scanFrame}>
            {/* Corners */}
            <View style={[styles.corner, styles.cornerTL]} />
            <View style={[styles.corner, styles.cornerTR]} />
            <View style={[styles.corner, styles.cornerBL]} />
            <View style={[styles.corner, styles.cornerBR]} />

            {/* Scan line animation hint */}
            <View style={styles.scanHint}>
              <Text style={styles.scanHintText}>Đặt vé số vào khung</Text>
            </View>
          </View>
        </View>

        {/* Bottom controls */}
        <View style={styles.bottomBar}>
          {/* Gallery button */}
          <TouchableOpacity style={styles.sideBtn} onPress={pickImage}>
            <Text style={styles.sideBtnText}>🖼️</Text>
            <Text style={styles.sideBtnLabel}>Thư viện</Text>
          </TouchableOpacity>

          {/* Capture button */}
          <TouchableOpacity style={styles.captureOuter} onPress={takePicture}>
            <View style={styles.captureMiddle}>
              <View style={styles.captureInner} />
            </View>
          </TouchableOpacity>

          {/* Spacer */}
          <View style={styles.sideBtn} />
        </View>
      </CameraView>
    </View>
  );

  // ── Handlers ──
  async function takePicture() {
    if (!cameraRef.current) return;
    try {
      await Haptics.impactAsync(Haptics.ImpactFeedbackStyle.Medium);
      const photoData = await cameraRef.current.takePictureAsync();
      setPhoto(photoData?.uri ?? null);
    } catch (error) {
      Alert.alert('Lỗi', 'Không thể chụp ảnh');
    }
  }

  async function pickImage() {
    const perm = await ImagePicker.requestMediaLibraryPermissionsAsync();
    if (!perm.granted) {
      Alert.alert('Từ chối', 'Cần quyền truy cập thư viện ảnh');
      return;
    }
    const picked = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      allowsEditing: false,
      quality: 1,
    });
    if (!picked.canceled && picked.assets[0]?.uri) {
      setPhoto(picked.assets[0].uri);
    }
  }

  async function handleAnalyze() {
    if (!photo) return;
    setLoading(true);
    try {
      const res = await analyzeTicketImage(photo);
      await saveTicketToHistory(res, photo);

      if (res.prizes && res.prizes.length > 0) {
        await Haptics.notificationAsync(Haptics.NotificationFeedbackType.Success);
      }

      setResult(res);
    } catch (error: any) {
      const msg = error.response?.data?.message || error.message || 'Không thể phân tích ảnh';
      Alert.alert('Lỗi', msg);
    } finally {
      setLoading(false);
    }
  }
}

// ─────────────────────────────────────────────────────────────────────────────
// Styles
// ─────────────────────────────────────────────────────────────────────────────

const styles = StyleSheet.create({
  darkBg: {
    flex: 1,
    backgroundColor: Brand.darkBg,
  },
  permissionContainer: {
    flex: 1,
    backgroundColor: Brand.darkBg,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
  },
  permissionTitle: {
    fontSize: 24,
    fontWeight: 'bold',
    color: '#FFF',
    marginBottom: 16,
    textAlign: 'center',
  },
  permissionDesc: {
    color: '#AAA',
    textAlign: 'center',
    marginBottom: 32,
    lineHeight: 22,
  },
  grantBtn: {
    backgroundColor: Brand.primary,
    paddingHorizontal: 32,
    paddingVertical: 14,
    borderRadius: 30,
  },
  grantBtnText: {
    color: '#FFF',
    fontWeight: 'bold',
    fontSize: 16,
  },
  camera: {
    flex: 1,
  },
  // Top bar
  topBar: {
    paddingTop: 50,
    paddingHorizontal: 20,
    paddingBottom: 10,
    backgroundColor: 'rgba(13, 6, 32, 0.6)',
  },
  topBarInner: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  appTitle: {
    color: Brand.gold,
    fontSize: 18,
    fontWeight: '800',
    letterSpacing: 1,
  },
  flipIcon: {
    fontSize: 24,
  },
  // Scan area overlay
  scanArea: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  scanFrame: {
    width: SCREEN_W * 0.82,
    height: SCREEN_W * 0.52,
    position: 'relative',
    justifyContent: 'center',
    alignItems: 'center',
  },
  corner: {
    position: 'absolute',
    width: 28,
    height: 28,
    borderColor: Brand.gold,
    borderWidth: 3,
  },
  cornerTL: { top: 0, left: 0, borderRightWidth: 0, borderBottomWidth: 0, borderTopLeftRadius: 4 },
  cornerTR: { top: 0, right: 0, borderLeftWidth: 0, borderBottomWidth: 0, borderTopRightRadius: 4 },
  cornerBL: { bottom: 0, left: 0, borderRightWidth: 0, borderTopWidth: 0, borderBottomLeftRadius: 4 },
  cornerBR: { bottom: 0, right: 0, borderLeftWidth: 0, borderTopWidth: 0, borderBottomRightRadius: 4 },
  scanHint: {
    backgroundColor: 'rgba(13, 6, 32, 0.7)',
    paddingHorizontal: 16,
    paddingVertical: 6,
    borderRadius: 20,
  },
  scanHintText: {
    color: Brand.gold,
    fontSize: 13,
    fontWeight: '600',
  },
  // Bottom controls
  bottomBar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingHorizontal: 40,
    paddingVertical: 30,
    backgroundColor: 'rgba(13, 6, 32, 0.85)',
  },
  sideBtn: {
    width: 56,
    alignItems: 'center',
  },
  sideBtnText: {
    fontSize: 28,
  },
  sideBtnLabel: {
    color: '#AAA',
    fontSize: 11,
    marginTop: 4,
  },
  captureOuter: {
    width: 80,
    height: 80,
    borderRadius: 40,
    borderWidth: 3,
    borderColor: Brand.gold,
    justifyContent: 'center',
    alignItems: 'center',
  },
  captureMiddle: {
    width: 68,
    height: 68,
    borderRadius: 34,
    backgroundColor: 'rgba(255, 215, 0, 0.15)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  captureInner: {
    width: 54,
    height: 54,
    borderRadius: 27,
    backgroundColor: Brand.gold,
  },
  // Preview
  previewHeader: {
    paddingTop: 54,
    paddingBottom: 16,
    paddingHorizontal: 20,
    backgroundColor: Brand.darkCard,
    alignItems: 'center',
  },
  previewHeaderText: {
    color: '#FFF',
    fontSize: 18,
    fontWeight: '700',
  },
  previewImage: {
    flex: 1,
    backgroundColor: '#000',
  },
  loadingOverlay: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: 'rgba(13, 6, 32, 0.85)',
    justifyContent: 'center',
    alignItems: 'center',
  },
  loadingCard: {
    backgroundColor: Brand.darkCard,
    borderRadius: 20,
    padding: 32,
    alignItems: 'center',
    width: 220,
    borderWidth: 1,
    borderColor: Brand.gold,
  },
  loadingTitle: {
    color: '#FFF',
    fontSize: 18,
    fontWeight: '700',
    marginTop: 16,
  },
  loadingDesc: {
    color: '#888',
    fontSize: 13,
    marginTop: 8,
  },
  previewActions: {
    flexDirection: 'row',
    gap: 12,
    padding: 20,
    backgroundColor: Brand.darkCard,
  },
  actionBtn: {
    flex: 1,
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: 'center',
  },
  actionBtnOutline: {
    borderWidth: 1.5,
    borderColor: Brand.primaryLight,
  },
  actionBtnOutlineText: {
    color: Brand.primaryLight,
    fontWeight: '700',
    fontSize: 15,
  },
  actionBtnPrimary: {
    backgroundColor: Brand.primary,
  },
  actionBtnPrimaryText: {
    color: '#FFF',
    fontWeight: '700',
    fontSize: 15,
  },
  disabled: {
    opacity: 0.6,
  },
});

// Result Screen Styles
const rs = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: Brand.darkBg,
  },
  content: {
    paddingBottom: 40,
  },
  header: {
    paddingTop: 54,
    paddingBottom: 28,
    alignItems: 'center',
  },
  emoji: {
    fontSize: 52,
    marginBottom: 8,
  },
  headerTitle: {
    color: '#000',
    fontSize: 26,
    fontWeight: '900',
    letterSpacing: 1,
  },
  totalPrize: {
    color: '#000',
    fontSize: 17,
    fontWeight: '700',
    marginTop: 6,
    opacity: 0.8,
  },
  ticketCard: {
    margin: 16,
    backgroundColor: Brand.darkCard,
    borderRadius: 16,
    padding: 20,
    borderWidth: 1,
    borderColor: Brand.darkCard2,
  },
  prizeCard: {
    marginHorizontal: 16,
    marginBottom: 16,
    backgroundColor: Brand.darkCard,
    borderRadius: 16,
    padding: 20,
    borderWidth: 1,
    borderColor: Brand.darkCard2,
  },
  errorCard: {
    margin: 16,
    backgroundColor: '#2D0A14',
    borderRadius: 16,
    padding: 20,
    borderWidth: 1,
    borderColor: Brand.danger,
  },
  errorText: {
    color: Brand.danger,
    fontSize: 14,
    lineHeight: 22,
  },
  cardTitle: {
    color: '#FFF',
    fontSize: 15,
    fontWeight: '700',
    marginBottom: 16,
    letterSpacing: 0.5,
  },
  infoRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: 8,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: Brand.darkCard2,
  },
  infoLabel: {
    color: '#888',
    fontSize: 14,
  },
  infoValue: {
    color: '#DDD',
    fontSize: 14,
    fontWeight: '600',
  },
  highlighted: {
    color: Brand.gold,
    fontSize: 18,
    fontWeight: '800',
    letterSpacing: 2,
  },
  prizeRow: {
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
  prizeLevel: {
    fontSize: 14,
    fontWeight: '700',
  },
  prizeAmount: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '800',
  },
  actions: {
    margin: 16,
  },
  retakeBtn: {
    backgroundColor: Brand.primary,
    paddingVertical: 16,
    borderRadius: 14,
    alignItems: 'center',
  },
  retakeBtnText: {
    color: '#FFF',
    fontSize: 16,
    fontWeight: '700',
  },
});
