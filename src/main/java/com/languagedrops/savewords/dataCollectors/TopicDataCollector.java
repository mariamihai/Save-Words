package com.languagedrops.savewords.dataCollectors;

import com.languagedrops.savewords.config.DocumentParsingConfig;
import com.languagedrops.savewords.model.WordInfo;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.languagedrops.savewords.config.DocumentParsingConfig.SRC_ATTRIBUTE;

@Component
@Slf4j
public class TopicDataCollector {

    private DocumentParsingConfig documentParsingConfig;

    public TopicDataCollector(DocumentParsingConfig documentParsingConfig) {
        this.documentParsingConfig = documentParsingConfig;
    }

    public List<WordInfo> getWordsForLesson(String url) throws IOException {
        List<WordInfo> words = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();
        Elements rows = getTopicElements(doc);

        rows.stream().forEach(row -> words.add(constructNewWordFromRow(row)));
        return words;
    }

    private Elements getTopicElements(Document doc) {
        return doc.body().getElementsByClass(documentParsingConfig.topicRow);
    }

    private WordInfo constructNewWordFromRow(Element row) {
        return new WordInfo(getTranslatedWord(row),
                            getNativeWord(row),
                            getUrlForIllustration(row),
                            isAnimation(row));
    }

    private String getTranslatedWord(Element row) {
        return row.getElementsByClass(documentParsingConfig.translatedWord).get(0).child(0).text();
    }

    private String getNativeWord(Element row) {
        return row.getElementsByClass(documentParsingConfig.nativeWord).get(0).child(0).text();
    }

    private boolean isAnimation(Element row) {
        return row.getElementsByClass(documentParsingConfig.illustrationForWord)
                  .get(0)
                  .getElementsByClass(documentParsingConfig.animation).size() == 1;

    }

    private String getUrlForIllustration(Element row) {
        return row.getElementsByClass(documentParsingConfig.illustrationForWord).get(0).child(0).attr(SRC_ATTRIBUTE);
    }
}
