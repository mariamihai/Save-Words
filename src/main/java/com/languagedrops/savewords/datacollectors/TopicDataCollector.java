package com.languagedrops.savewords.datacollectors;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.languagedrops.savewords.config.DocumentParsingConfig.HREF_ATTRIBUTE;
import static com.languagedrops.savewords.config.DocumentParsingConfig.SRC_ATTRIBUTE;
import static com.languagedrops.savewords.config.DocumentParsingConfig.TITLE_ATTRIBUTE;

@Component
@Slf4j
public class TopicDataCollector {

    private DocumentParsingConfig documentParsingConfig;

    private HashMap<String, String> relatedTopics = new HashMap<>();

    public TopicDataCollector(DocumentParsingConfig documentParsingConfig) {
        this.documentParsingConfig = documentParsingConfig;
    }

    public List<WordInfo> getWordsForLesson(String url) throws IOException {
        List<WordInfo> words = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();
        setRelatedTopics(doc);

        Elements rows = getTopicElements(doc);
        rows.stream().forEach(row -> words.add(constructNewWordFromRow(row)));
        return words;
    }

    private Elements getTopicElements(Document doc) {
        return doc.body().getElementsByClass(documentParsingConfig.topicRow);
    }

    private void setRelatedTopics(Document doc) {
        Elements relatedTopicsElements = doc.body().getElementsByClass(documentParsingConfig.linkableTopics);

        relatedTopicsElements.stream().forEach(element -> {
            Element child = element.child(0);
            relatedTopics.putIfAbsent(child.attr(TITLE_ATTRIBUTE), child.attr(HREF_ATTRIBUTE));
        });
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

    public Map<String, String> getRelatedTopics() {
        return relatedTopics;
    }
}
