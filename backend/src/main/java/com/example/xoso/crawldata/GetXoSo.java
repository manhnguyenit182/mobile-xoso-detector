package com.example.xoso.crawldata;

import java.text.SimpleDateFormat;
import java.util.*;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class GetXoSo {
  public boolean isNewData = false;

  // ─────────────────────────────────────────────────────────────────────────
  // Public entry point – tự nhận diện miền qua URL
  // ─────────────────────────────────────────────────────────────────────────

  public String fetchData(String url) throws Exception {
    boolean isMienBac = url.contains("mien-bac");
    if (isMienBac) {
      return fetchMienBac(url);
    } else {
      return fetchMienNamTrung(url);
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // MIỀN NAM / MIỀN TRUNG
  // data-quantity ở element cha = số tỉnh trong ngày (VD: 3)
  // Mỗi giải có (số_tỉnh × số_hàng) phần tử, phân bổ lần lượt theo tỉnh.
  // ─────────────────────────────────────────────────────────────────────────

  private String fetchMienNamTrung(String url) throws Exception {
    Document doc = fetchDoc(url);
    if (doc == null) return null;

    // Phần tử cha chứa danh sách tỉnh và data-quantity = số tỉnh
    Element container = doc.select("[data-quantity]").first();
    if (container == null) {
      throw new Exception("Không tìm thấy phần tử với thuộc tính data-quantity");
    }

    int numOfProvinces = Integer.parseInt(container.attr("data-quantity"));
    Elements spans = container.select("div.quantity-of-number span.wrap-text");
    List<String> provinceList = new ArrayList<>();
    for (Element span : spans) {
      provinceList.add(span.text());
    }
    System.out.println("Tỉnh thành trong ngày: " + provinceList);
    System.out.println("Số tỉnh trong ngày: " + numOfProvinces);

    Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();
    for (String province : provinceList) {
      result.put(province, new LinkedHashMap<>());
    }

    // Số phần tử mỗi giải = numOfProvinces × số_hàng
    // Giải ĐB(1), Nhất(2), Nhì(3), Năm(6), Bảy(8), Tám(9): 1 hàng → limit = numOfProvinces
    // Giải Ba(4): 2 hàng  → limit = numOfProvinces * 2
    // Giải Tư(5): 7 hàng  → limit = numOfProvinces * 7
    // Giải Sáu(7): 3 hàng → limit = numOfProvinces * 3
    addToJsonArrayNamTrung(doc, "1", numOfProvinces, numOfProvinces * 1, provinceList, result);
    addToJsonArrayNamTrung(doc, "2", numOfProvinces, numOfProvinces * 1, provinceList, result);
    addToJsonArrayNamTrung(doc, "3", numOfProvinces, numOfProvinces * 1, provinceList, result);
    addToJsonArrayNamTrung(doc, "4", numOfProvinces, numOfProvinces * 2, provinceList, result);
    addToJsonArrayNamTrung(doc, "5", numOfProvinces, numOfProvinces * 7, provinceList, result);
    addToJsonArrayNamTrung(doc, "6", numOfProvinces, numOfProvinces * 1, provinceList, result);
    addToJsonArrayNamTrung(doc, "7", numOfProvinces, numOfProvinces * 3, provinceList, result);
    addToJsonArrayNamTrung(doc, "8", numOfProvinces, numOfProvinces * 1, provinceList, result);
    addToJsonArrayNamTrung(doc, "9", numOfProvinces, numOfProvinces * 1, provinceList, result);

    return new JSONObject(result).toString();
  }

  /**
   * Phân bổ các phần tử data-prize theo tỉnh thành (miền Nam/Trung).
   * Thứ tự: phần tử 0 → tỉnh 0, phần tử 1 → tỉnh 1, ..., phần tử N → tỉnh (N % numProvinces)
   */
  private void addToJsonArrayNamTrung(Document doc, String prizeType, int numOfProvinces,
      int limit, List<String> provinceList, Map<String, Map<String, List<String>>> result) {
    Elements items = doc.getElementsByAttributeValue("data-prize", prizeType);
    for (int i = 0; i < limit && i < items.size(); i++) {
      String province = provinceList.get(i % numOfProvinces);
      result.get(province).computeIfAbsent(prizeType, k -> new ArrayList<>())
          .add(items.get(i).attr("data-value"));
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // MIỀN BẮC
  // data-quantity ở MỖI Ô GIẢI = số phần tử trong ô đó (không phải số tỉnh).
  //
  // Cấu trúc HTML thực tế (kqxs.vn):
  //   data-prize=1 (ĐB):   2 phần tử (2 ngày liền được load, lấy phần tử đầu tiên)
  //   data-prize=2 (Nhất): 2 phần tử → lấy phần tử đầu tiên
  //   data-prize=3 (Nhì):  4 phần tử → 2 số hôm nay (lấy 2 phần tử đầu)
  //   data-prize=4 (Ba):  12 phần tử → 6 số hôm nay (lấy 6 phần tử đầu)
  //   data-prize=5 (Tư):   8 phần tử → 4 số hôm nay (lấy 4 phần tử đầu)
  //   data-prize=6 (Năm): 12 phần tử → 6 số hôm nay (lấy 6 phần tử đầu)
  //   data-prize=7 (Sáu): 10 phần tử → 3 số hôm nay (lấy 3 phần tử đầu)
  //   data-prize=8 (Bảy):  8 phần tử → 4 số hôm nay (lấy 4 phần tử đầu)
  //   (không có data-prize=9 ở miền bắc)
  //
  // Số lượng phần tử thực = data-quantity của ô giải hôm nay.
  // Vì trang load 2 ngày, số phần tử trong DOM = 2 × data-quantity.
  // Ta dùng data-quantity của ô đầu tiên để biết số phần tử cần lấy.
  // ─────────────────────────────────────────────────────────────────────────

  private String fetchMienBac(String url) throws Exception {
    Document doc = fetchDoc(url);
    if (doc == null) return null;

    // Xác định đài Miền Bắc theo ngày trong tuần
    Calendar calendar = Calendar.getInstance();
    int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    String province = switch (dayOfWeek) {
        case Calendar.MONDAY -> "Hà Nội";
        case Calendar.TUESDAY -> "Quảng Ninh";
        case Calendar.WEDNESDAY -> "Bắc Ninh";
        case Calendar.THURSDAY -> "Hà Nội";
        case Calendar.FRIDAY -> "Hải Phòng";
        case Calendar.SATURDAY -> "Nam Định";
        case Calendar.SUNDAY -> "Thái Bình";
        default -> "Hà Nội";
    };

    Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();
    result.put(province, new LinkedHashMap<>());

    // Với mỗi giải, đọc data-quantity từ container của hàng đầu tiên
    // để biết bao nhiêu số thuộc ngày hôm nay, rồi lấy đúng số đó.
    String[] prizeLevels = {"1", "2", "3", "4", "5", "6", "7", "8"};
    for (String prizeLevel : prizeLevels) {
      addToJsonArrayMienBac(doc, prizeLevel, province, result);
    }

    System.out.println("Đã crawl Miền Bắc (" + province + "): " + result.get(province));
    return new JSONObject(result).toString();
  }

  /**
   * Lấy các số của 1 giải cho Miền Bắc.
   * Chiến lược:
   *   1. Tìm tất cả elements có data-prize = prizeLevel.
   *   2. Tìm element cha gần nhất có class "quantity-of-number" và đọc data-quantity
   *      → đây là số phần tử thuộc ngày hôm nay trong ô đầu tiên.
   *   3. Lấy đúng (countToday) phần tử đầu tiên trong danh sách.
   */
  private void addToJsonArrayMienBac(Document doc, String prizeLevel,
      String province, Map<String, Map<String, List<String>>> result) {
    Elements items = doc.getElementsByAttributeValue("data-prize", prizeLevel);
    if (items.isEmpty()) return;

    // Đọc data-quantity từ container của phần tử đầu tiên
    Element firstContainer = items.first().closest(".quantity-of-number");
    int countToday = items.size(); // fallback: lấy tất cả
    if (firstContainer != null) {
      String qty = firstContainer.attr("data-quantity");
      if (!qty.isEmpty()) {
        try {
          countToday = Integer.parseInt(qty);
        } catch (NumberFormatException ignored) {}
      }
    }

    List<String> values = new ArrayList<>();
    for (int i = 0; i < countToday && i < items.size(); i++) {
      values.add(items.get(i).attr("data-value"));
    }

    if (!values.isEmpty()) {
      result.get(province).put(prizeLevel, values);
      System.out.println("  Giải " + prizeLevel + " (" + countToday + " số): " + values);
    }
  }

  // ─────────────────────────────────────────────────────────────────────────
  // HELPERS
  // ─────────────────────────────────────────────────────────────────────────

  private Document fetchDoc(String url) throws Exception {
    Document doc = Jsoup.connect(url)
        .userAgent("Mozilla/5.0")
        .timeout(10000)
        .get();
    doc.outputSettings().indentAmount(2).prettyPrint(true);

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
    String currentDate = sdf.format(new Date());
    System.out.println("Ngày hiện tại: " + currentDate);

    if (doc.text().contains(currentDate)) {
      System.out.println("Có text này trong trang!");
      isNewData = true;
      System.out.println("Dữ liệu mới đã có sẵn.");
      return doc;
    } else {
      System.out.println("Dữ liệu mới chưa có.");
      return null;
    }
  }
}
