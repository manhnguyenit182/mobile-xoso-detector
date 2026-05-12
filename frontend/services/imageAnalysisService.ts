import * as FileSystem from 'expo-file-system/legacy';
import { apiClient } from './apiClient';

// ─────────────────────────────────────────────────────────────────────────────
// Types
// ─────────────────────────────────────────────────────────────────────────────

export interface AnalyzeTicketResponse {
  ticketNumber: string | null;
  drawDate: string | null;
  provinceCode: string | null;
  resultsAvailable: boolean;
  prizes: PrizeResult[];
  errorMessage: string | null;
}

export interface PrizeResult {
  prizeLevel: string;
  prizeAmount: number;
  ticketNumber: string;
  provinceCode: string;
  drawDate: string;
}

export interface TicketData {
  so_ve: string | null;
  ngay_xo_so: string | null;
  dai_xo_so: string | null;
}

export interface BackendCheckResponse {
  prizeLevel?: string;
  prizeAmount?: number;
  ticketNumber?: string;
  provinceCode?: string;
  drawDate?: string;
}

// ─────────────────────────────────────────────────────────────────────────────
// Utility
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Đọc file ảnh và chuyển sang base64
 */
export async function convertImageToBase64(photoUri: string): Promise<string> {
  return await FileSystem.readAsStringAsync(photoUri, {
    encoding: 'base64',
  });
}

// ─────────────────────────────────────────────────────────────────────────────
// Core API
// ─────────────────────────────────────────────────────────────────────────────

/**
 * Gửi ảnh vé số (base64) lên backend để phân tích (OCR + AI + dò vé).
 */
export async function analyzeTicketImage(photoUri: string): Promise<AnalyzeTicketResponse> {
  const base64 = await convertImageToBase64(photoUri);

  const response = await apiClient.post<AnalyzeTicketResponse>('/api/analyze-ticket', { 
    imageBase64: base64 
  });

  return response.data;
}

/**
 * Dò vé thủ công (nhập số tay).
 */
export async function checkTicketManually(ticketData: TicketData): Promise<BackendCheckResponse[]> {
  const response = await apiClient.post<BackendCheckResponse[]>('/api/check-ticket', {
    ticketNumber: ticketData.so_ve,
    drawDate: ticketData.ngay_xo_so,
    provinceCode: ticketData.dai_xo_so,
  });

  return response.data;
}
