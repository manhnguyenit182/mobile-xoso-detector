import AsyncStorage from '@react-native-async-storage/async-storage';
import { AnalyzeTicketResponse } from './imageAnalysisService';

const HISTORY_KEY = 'xoso_ticket_history';
const MAX_HISTORY_ITEMS = 50;

// ─────────────────────────────────────────────────────────────────────────────
// Types
// ─────────────────────────────────────────────────────────────────────────────

export type TicketStatus = 'PENDING' | 'WON' | 'LOST' | 'ERROR';

export interface TicketHistoryItem {
  id: string;
  ticketNumber: string | null;
  drawDate: string | null;
  provinceCode: string | null;
  scannedAt: string; // ISO timestamp
  status: TicketStatus;
  prizes: Array<{
    prizeLevel: string;
    prizeAmount: number;
  }>;
  totalPrize: number;
  photoUri?: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Pure functions
// ─────────────────────────────────────────────────────────────────────────────

function computeStatus(response: AnalyzeTicketResponse): TicketStatus {
  if (response.errorMessage) return 'ERROR';
  if (!response.resultsAvailable) return 'PENDING';
  if (response.prizes && response.prizes.length > 0) return 'WON';
  return 'LOST';
}

function computeTotal(response: AnalyzeTicketResponse): number {
  return (response.prizes ?? []).reduce((sum, p) => sum + (p.prizeAmount ?? 0), 0);
}

// ─────────────────────────────────────────────────────────────────────────────
// API
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Lưu kết quả quét vé vào lịch sử.
 */
export async function saveTicketToHistory(
  response: AnalyzeTicketResponse,
  photoUri?: string,
): Promise<TicketHistoryItem> {
  const newItem: TicketHistoryItem = {
    id: Date.now().toString(),
    ticketNumber: response.ticketNumber,
    drawDate: response.drawDate,
    provinceCode: response.provinceCode,
    scannedAt: new Date().toISOString(),
    status: computeStatus(response),
    prizes: (response.prizes ?? []).map((p) => ({
      prizeLevel: p.prizeLevel,
      prizeAmount: p.prizeAmount,
    })),
    totalPrize: computeTotal(response),
    photoUri,
  };

  const existing = await getTicketHistory();
  const updated = [newItem, ...existing].slice(0, MAX_HISTORY_ITEMS);
  await AsyncStorage.setItem(HISTORY_KEY, JSON.stringify(updated));
  return newItem;
}

/**
 * Lấy danh sách lịch sử vé đã quét (mới nhất trước).
 */
export async function getTicketHistory(): Promise<TicketHistoryItem[]> {
  try {
    const raw = await AsyncStorage.getItem(HISTORY_KEY);
    return raw ? JSON.parse(raw) : [];
  } catch {
    return [];
  }
}

/**
 * Xóa toàn bộ lịch sử.
 */
export async function clearTicketHistory(): Promise<void> {
  await AsyncStorage.removeItem(HISTORY_KEY);
}

/**
 * Xóa một vé theo id.
 */
export async function deleteTicketFromHistory(id: string): Promise<void> {
  const existing = await getTicketHistory();
  const updated = existing.filter((item) => item.id !== id);
  await AsyncStorage.setItem(HISTORY_KEY, JSON.stringify(updated));
}

/**
 * Format tiền thưởng sang dạng đọc được ("2 tỷ", "500 triệu", ...)
 */
export function formatPrizeAmount(amount: number): string {
  if (amount >= 1_000_000_000) {
    const val = amount / 1_000_000_000;
    return `${val % 1 === 0 ? val.toFixed(0) : val.toFixed(1)} tỷ`;
  }
  if (amount >= 1_000_000) {
    const val = amount / 1_000_000;
    return `${val % 1 === 0 ? val.toFixed(0) : val.toFixed(1)} triệu`;
  }
  if (amount >= 1_000) {
    return `${(amount / 1_000).toFixed(0)} nghìn`;
  }
  return amount.toLocaleString('vi-VN');
}
