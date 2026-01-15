package com.example.xoso.crawldata;

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

  public String fetchData(String url) throws Exception {
    Document doc = Jsoup.connect(url)
        .userAgent("Mozilla/5.0")
        .timeout(10000)
        .get();
    // Nếu muốn “định dạng” HTML cho dễ nhìn:
    doc.outputSettings().indentAmount(2).prettyPrint(true);

    // TODO toi kiem tra lai du lieu
    Date currentDate = new Date();
    System.out.println("Ngày hiện tại: " + currentDate);
    if (doc.text().contains("13-01-2026")) {
      System.out.println("Có text này trong trang!");
      isNewData = true;
    }
    if (isNewData) {
      System.out.println("Dữ liệu mới đã có sẵn.");
    } else {
      System.out.println("Dữ liệu mới chưa có.");
      return null;
    }
    // lấy tỉnh thành có trong ngày hôm đó
    Element numOfProvincesElement = doc.select("[data-quantity]").first();
    System.out.println("elelement province" + numOfProvincesElement);
    if (numOfProvincesElement != null) {
      String quantity = numOfProvincesElement.attr("data-quantity");
      Elements spans = numOfProvincesElement.select("div.quantity-of-number span.wrap-text");

      List<String> provinceList = new ArrayList<>();
      for (Element span : spans) {
        provinceList.add(span.text());
      }
      System.out.println("Tỉnh thành trong ngày: " + provinceList);

      int numOfProvinces = Integer.parseInt(quantity);
      System.out.println("Số tỉnh trong ngày: " + numOfProvinces);

      // Tạo cấu trúc JSON
      Map<String, List<Map<String, String>>> result = new LinkedHashMap<>();
      for (String province : provinceList) {
        result.put(province, new ArrayList<>());
      }
      // giải đặc biệt
      List<Map<String, String>> ListGrandPrize = new ArrayList<>();
      addToJsonArray(ListGrandPrize, doc, "1", numOfProvinces, provinceList, result);
      // giải nhất
      List<Map<String, String>> ListFirstPrize = new ArrayList<>();
      addToJsonArray(ListFirstPrize, doc, "2", numOfProvinces, provinceList, result);
      // giải nhì
      List<Map<String, String>> ListSecondPrize = new ArrayList<>();
      addToJsonArray(ListSecondPrize, doc, "3", numOfProvinces, provinceList, result);
      // giải ba
      List<Map<String, String>> ListThirdPrize = new ArrayList<>();
      addToJsonArray(ListThirdPrize, doc, "4", numOfProvinces, provinceList, result);
      // giải tư
      List<Map<String, String>> ListFourthPrize = new ArrayList<>();
      addToJsonArray(ListFourthPrize, doc, "5", numOfProvinces, provinceList, result);
      // giải năm
      List<Map<String, String>> ListFifthPrize = new ArrayList<>();
      addToJsonArray(ListFifthPrize, doc, "6", numOfProvinces, provinceList, result);
      // giải sáu
      List<Map<String, String>> ListSixthPrize = new ArrayList<>();
      addToJsonArray(ListSixthPrize, doc, "7", numOfProvinces, provinceList, result);
      // giải bảy
      List<Map<String, String>> ListSeventhPrize = new ArrayList<>();
      addToJsonArray(ListSeventhPrize, doc, "8", numOfProvinces, provinceList, result);
      // giải tám
      List<Map<String, String>> ListEighthPrize = new ArrayList<>();
      addToJsonArray(ListEighthPrize, doc, "9", numOfProvinces, provinceList, result);
      JSONObject jsonResult = new JSONObject(result);
      return jsonResult.toString();

    } else {
      throw new Exception("Không tìm thấy phần tử với thuộc tính data-quantity");
    }

  }

  private void addToJsonArray(List<Map<String, String>> list, Document doc, String prizeType, int numOfProvinces,
      List<String> provinceList, Map<String, List<Map<String, String>>> result) {
    Elements items = doc.getElementsByAttributeValue("data-prize", prizeType);
    int limit = 0;
    // giới hạn số lượng phần tử thêm vào dựa trên loại giải
    if (prizeType == "1" || prizeType == "2" || prizeType == "3" || prizeType == "6" || prizeType == "8"
        || prizeType == "9") {
      limit = 3;
    }
    if (prizeType == "4") {
      limit = 6;
    } else if (prizeType == "5") {
      limit = 21;
    } else if (prizeType == "7") {
      limit = 9;
    }

    // thêm phần tử vào danh sách và phân bổ theo tỉnh thành
    for (int i = 0; i < limit; i++) {
      Element item = items.get(i);
      Map<String, String> obj = new LinkedHashMap<>();
      obj.put("value", item.attr("data-value"));
      obj.put("prize", item.attr("data-prize"));
      list.add(obj);
      String province = provinceList.get(i % numOfProvinces);
      result.get(province).add(obj);
    }

  }
}
