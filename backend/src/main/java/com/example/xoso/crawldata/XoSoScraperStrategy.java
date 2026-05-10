package com.example.xoso.crawldata;

import org.jsoup.nodes.Document;
import java.util.List;
import java.util.Map;

public interface XoSoScraperStrategy {
    /**
     * Parse HTML Document thành dữ liệu kết quả xổ số.
     * @param doc HTML Document của trang kết quả
     * @return Map<Tên tỉnh, Map<Mã giải, Danh sách số trúng>>
     */
    Map<String, Map<String, List<String>>> scrape(Document doc) throws Exception;
}
