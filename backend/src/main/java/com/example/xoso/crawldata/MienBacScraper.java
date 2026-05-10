package com.example.xoso.crawldata;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("mienBacScraper")
public class MienBacScraper implements XoSoScraperStrategy {

    @Override
    public Map<String, Map<String, List<String>>> scrape(Document doc) throws Exception {
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

        String[] prizeLevels = {"1", "2", "3", "4", "5", "6", "7", "8"};
        for (String prizeLevel : prizeLevels) {
            addToJsonArrayMienBac(doc, prizeLevel, province, result);
        }

        return result;
    }

    private void addToJsonArrayMienBac(Document doc, String prizeLevel,
                                       String province, Map<String, Map<String, List<String>>> result) {
        Elements items = doc.getElementsByAttributeValue("data-prize", prizeLevel);
        if (items.isEmpty()) return;

        Element firstContainer = items.first().closest(".quantity-of-number");
        int countToday = items.size();
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
        }
    }
}
