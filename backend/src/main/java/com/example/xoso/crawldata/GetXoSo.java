package com.example.xoso.crawldata;

import com.example.xoso.exception.CrawlDataException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class GetXoSo {

    private final XoSoScraperStrategy mienBacScraper;
    private final XoSoScraperStrategy mienNamTrungScraper;

    public GetXoSo(
            @Qualifier("mienBacScraper") XoSoScraperStrategy mienBacScraper,
            @Qualifier("mienNamTrungScraper") XoSoScraperStrategy mienNamTrungScraper) {
        this.mienBacScraper = mienBacScraper;
        this.mienNamTrungScraper = mienNamTrungScraper;
    }

    public Map<String, Map<String, List<String>>> fetchData(String url) throws Exception {
        Document doc = fetchDoc(url);
        if (doc == null) {
            return null; // Không có dữ liệu mới
        }

        boolean isMienBac = url.contains("mien-bac");
        if (isMienBac) {
            return mienBacScraper.scrape(doc);
        } else {
            return mienNamTrungScraper.scrape(doc);
        }
    }

    private Document fetchDoc(String url) throws Exception {
        Document doc;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(10000)
                    .get();
        } catch (Exception e) {
            throw new CrawlDataException("Lỗi kết nối Jsoup đến URL: " + url, e);
        }

        doc.outputSettings().indentAmount(2).prettyPrint(true);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());

        if (doc.text().contains(currentDate)) {
            return doc;
        } else {
            return null;
        }
    }
}
