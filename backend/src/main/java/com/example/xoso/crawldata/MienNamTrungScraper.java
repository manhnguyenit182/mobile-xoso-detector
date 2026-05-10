package com.example.xoso.crawldata;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("mienNamTrungScraper")
public class MienNamTrungScraper implements XoSoScraperStrategy {

    @Override
    public Map<String, Map<String, List<String>>> scrape(Document doc) throws Exception {
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

        Map<String, Map<String, List<String>>> result = new LinkedHashMap<>();
        for (String province : provinceList) {
            result.put(province, new LinkedHashMap<>());
        }

        addToJsonArrayNamTrung(doc, "1", numOfProvinces, numOfProvinces * 1, provinceList, result);
        addToJsonArrayNamTrung(doc, "2", numOfProvinces, numOfProvinces * 1, provinceList, result);
        addToJsonArrayNamTrung(doc, "3", numOfProvinces, numOfProvinces * 1, provinceList, result);
        addToJsonArrayNamTrung(doc, "4", numOfProvinces, numOfProvinces * 2, provinceList, result);
        addToJsonArrayNamTrung(doc, "5", numOfProvinces, numOfProvinces * 7, provinceList, result);
        addToJsonArrayNamTrung(doc, "6", numOfProvinces, numOfProvinces * 1, provinceList, result);
        addToJsonArrayNamTrung(doc, "7", numOfProvinces, numOfProvinces * 3, provinceList, result);
        addToJsonArrayNamTrung(doc, "8", numOfProvinces, numOfProvinces * 1, provinceList, result);
        addToJsonArrayNamTrung(doc, "9", numOfProvinces, numOfProvinces * 1, provinceList, result);

        return result;
    }

    private void addToJsonArrayNamTrung(Document doc, String prizeType, int numOfProvinces,
                                        int limit, List<String> provinceList, Map<String, Map<String, List<String>>> result) {
        Elements items = doc.getElementsByAttributeValue("data-prize", prizeType);
        for (int i = 0; i < limit && i < items.size(); i++) {
            String province = provinceList.get(i % numOfProvinces);
            result.get(province).computeIfAbsent(prizeType, k -> new ArrayList<>())
                    .add(items.get(i).attr("data-value"));
        }
    }
}
