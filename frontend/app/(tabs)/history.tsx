import { useCallback, useState } from 'react';
import {
  Alert,
  FlatList,
  StatusBar,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import { useFocusEffect } from '@react-navigation/native';
import {
  clearTicketHistory,
  deleteTicketFromHistory,
  formatPrizeAmount,
  getTicketHistory,
  TicketHistoryItem,
  TicketStatus,
} from '../../services/ticketHistoryService';
import { Brand, PrizeColors } from '../../constants/theme';

// ─────────────────────────────────────────────────────────────────────────────
// Status Config
// ─────────────────────────────────────────────────────────────────────────────

const STATUS_CONFIG: Record<TicketStatus, { emoji: string; label: string; color: string }> = {
  WON: { emoji: '🏆', label: 'TRÚNG', color: Brand.gold },
  LOST: { emoji: '😔', label: 'TRƯỢT', color: '#555' },
  PENDING: { emoji: '⏳', label: 'CHỜ KQ', color: Brand.neon },
  ERROR: { emoji: '⚠️', label: 'LỖI', color: Brand.danger },
};

// ─────────────────────────────────────────────────────────────────────────────
// Sub-components
// ─────────────────────────────────────────────────────────────────────────────

function HistoryCard({
  item,
  onDelete,
}: {
  item: TicketHistoryItem;
  onDelete: (id: string) => void;
}) {
  const cfg = STATUS_CONFIG[item.status];
  const scannedDate = new Date(item.scannedAt).toLocaleDateString('vi-VN', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  });

  return (
    <View style={[card.container, { borderLeftColor: cfg.color }]}>
      {/* Row 1: Status badge + ticket number */}
      <View style={card.row}>
        <View style={[card.badge, { backgroundColor: cfg.color + '22', borderColor: cfg.color }]}>
          <Text style={card.badgeEmoji}>{cfg.emoji}</Text>
          <Text style={[card.badgeLabel, { color: cfg.color }]}>{cfg.label}</Text>
        </View>
        <Text style={card.ticketNumber}>{item.ticketNumber ?? '—'}</Text>
        <TouchableOpacity onPress={() => onDelete(item.id)} style={card.deleteBtn}>
          <Text style={card.deleteIcon}>🗑️</Text>
        </TouchableOpacity>
      </View>

      {/* Row 2: Province + Draw date */}
      <View style={card.row2}>
        <Text style={card.meta}>🏛️ {item.provinceCode ?? '—'}</Text>
        <Text style={card.meta}>📅 {item.drawDate ?? '—'}</Text>
      </View>

      {/* Prize list */}
      {item.prizes.length > 0 && (
        <View style={card.prizeList}>
          {item.prizes.map((p, idx) => (
            <View key={idx} style={card.prizeRow}>
              <Text style={[card.prizeLevel, { color: PrizeColors[p.prizeLevel] ?? Brand.neon }]}>
                {p.prizeLevel}
              </Text>
              <Text style={card.prizeAmount}>{formatPrizeAmount(p.prizeAmount)}</Text>
            </View>
          ))}
          {item.totalPrize > 0 && (
            <Text style={card.total}>Tổng: {formatPrizeAmount(item.totalPrize)}</Text>
          )}
        </View>
      )}

      {/* Scanned timestamp */}
      <Text style={card.time}>Quét lúc {scannedDate}</Text>
    </View>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────

export default function HistoryScreen() {
  const [history, setHistory] = useState<TicketHistoryItem[]>([]);
  const [filter, setFilter] = useState<TicketStatus | 'ALL'>('ALL');

  useFocusEffect(
    useCallback(() => {
      loadHistory();
    }, []),
  );

  async function loadHistory() {
    const data = await getTicketHistory();
    setHistory(data);
  }

  async function handleDelete(id: string) {
    Alert.alert('Xóa vé?', 'Bạn có chắc muốn xóa vé này khỏi lịch sử?', [
      { text: 'Hủy', style: 'cancel' },
      {
        text: 'Xóa',
        style: 'destructive',
        onPress: async () => {
          await deleteTicketFromHistory(id);
          await loadHistory();
        },
      },
    ]);
  }

  async function handleClearAll() {
    Alert.alert('Xóa tất cả?', 'Toàn bộ lịch sử vé sẽ bị xóa.', [
      { text: 'Hủy', style: 'cancel' },
      {
        text: 'Xóa hết',
        style: 'destructive',
        onPress: async () => {
          await clearTicketHistory();
          setHistory([]);
        },
      },
    ]);
  }

  const filtered = filter === 'ALL' ? history : history.filter((h) => h.status === filter);

  return (
    <View style={s.container}>
      <StatusBar barStyle="light-content" backgroundColor={Brand.darkBg} />

      {/* Header */}
      <View style={s.header}>
        <View>
          <Text style={s.headerTitle}>📜 Lịch Sử Vé</Text>
          <Text style={s.headerSub}>{history.length} vé đã quét</Text>
        </View>
        {history.length > 0 && (
          <TouchableOpacity onPress={handleClearAll}>
            <Text style={s.clearAll}>Xóa hết</Text>
          </TouchableOpacity>
        )}
      </View>

      {/* Filter chips */}
      <View style={s.filterRow}>
        {(['ALL', 'WON', 'PENDING', 'LOST', 'ERROR'] as const).map((f) => (
          <TouchableOpacity
            key={f}
            style={[s.chip, filter === f && s.chipActive]}
            onPress={() => setFilter(f)}
          >
            <Text style={[s.chipText, filter === f && s.chipTextActive]}>
              {f === 'ALL' ? 'Tất cả' : STATUS_CONFIG[f].emoji + ' ' + STATUS_CONFIG[f].label}
            </Text>
          </TouchableOpacity>
        ))}
      </View>

      {/* List */}
      {filtered.length === 0 ? (
        <View style={s.empty}>
          <Text style={s.emptyEmoji}>🎟️</Text>
          <Text style={s.emptyText}>
            {history.length === 0
              ? 'Chưa có vé nào được quét'
              : 'Không có vé nào trong mục này'}
          </Text>
          <Text style={s.emptyHint}>Chụp ảnh hoặc nhập tay vé số để bắt đầu</Text>
        </View>
      ) : (
        <FlatList
          data={filtered}
          keyExtractor={(item) => item.id}
          renderItem={({ item }) => (
            <HistoryCard item={item} onDelete={handleDelete} />
          )}
          contentContainerStyle={s.list}
          showsVerticalScrollIndicator={false}
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
  header: {
    paddingTop: 54,
    paddingBottom: 16,
    paddingHorizontal: 20,
    backgroundColor: Brand.darkCard,
    borderBottomWidth: 1,
    borderBottomColor: Brand.darkCard2,
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'flex-end',
  },
  headerTitle: {
    color: '#FFF',
    fontSize: 24,
    fontWeight: '900',
  },
  headerSub: {
    color: '#888',
    fontSize: 13,
    marginTop: 2,
  },
  clearAll: {
    color: Brand.danger,
    fontSize: 14,
    fontWeight: '700',
  },
  filterRow: {
    flexDirection: 'row',
    paddingHorizontal: 12,
    paddingVertical: 12,
    gap: 8,
    flexWrap: 'wrap',
  },
  chip: {
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    backgroundColor: Brand.darkCard,
    borderWidth: 1,
    borderColor: Brand.darkCard2,
  },
  chipActive: {
    backgroundColor: Brand.primary,
    borderColor: Brand.primaryLight,
  },
  chipText: {
    color: '#888',
    fontSize: 12,
    fontWeight: '600',
  },
  chipTextActive: {
    color: '#FFF',
  },
  list: {
    padding: 12,
    gap: 12,
  },
  empty: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 40,
  },
  emptyEmoji: {
    fontSize: 60,
    marginBottom: 16,
  },
  emptyText: {
    color: '#FFF',
    fontSize: 18,
    fontWeight: '700',
    textAlign: 'center',
  },
  emptyHint: {
    color: '#666',
    fontSize: 14,
    textAlign: 'center',
    marginTop: 8,
  },
});

const card = StyleSheet.create({
  container: {
    backgroundColor: Brand.darkCard,
    borderRadius: 16,
    padding: 16,
    borderLeftWidth: 4,
    borderWidth: 1,
    borderColor: Brand.darkCard2,
  },
  row: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 10,
    marginBottom: 10,
  },
  row2: {
    flexDirection: 'row',
    gap: 16,
    marginBottom: 10,
  },
  badge: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 8,
    paddingVertical: 4,
    borderRadius: 8,
    borderWidth: 1,
    gap: 4,
  },
  badgeEmoji: {
    fontSize: 12,
  },
  badgeLabel: {
    fontSize: 11,
    fontWeight: '800',
    letterSpacing: 0.5,
  },
  ticketNumber: {
    flex: 1,
    color: Brand.gold,
    fontSize: 22,
    fontWeight: '900',
    letterSpacing: 3,
  },
  deleteBtn: {
    padding: 4,
  },
  deleteIcon: {
    fontSize: 18,
  },
  meta: {
    color: '#888',
    fontSize: 13,
  },
  prizeList: {
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: Brand.darkCard2,
    paddingTop: 10,
    marginTop: 2,
    gap: 6,
  },
  prizeRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  prizeLevel: {
    fontSize: 13,
    fontWeight: '700',
  },
  prizeAmount: {
    color: '#FFF',
    fontSize: 13,
    fontWeight: '700',
  },
  total: {
    color: Brand.gold,
    fontWeight: '800',
    fontSize: 14,
    marginTop: 4,
    textAlign: 'right',
  },
  time: {
    color: '#555',
    fontSize: 11,
    marginTop: 10,
    textAlign: 'right',
  },
});
