package com.languagedrops.savewords.dataCollectors;

import com.languagedrops.savewords.config.DocumentParsingConfig;
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
import static com.languagedrops.savewords.config.DocumentParsingConfig.TITLE_ATTRIBUTE;

@Component
public class LanguageDataCollector {

    private DocumentParsingConfig documentParsingConfig;

    public LanguageDataCollector(DocumentParsingConfig documentParsingConfig) {
        this.documentParsingConfig = documentParsingConfig;
    }

    public Map<String, Map<String, String>> mapLanguage(String startingPointLink) throws IOException {
        Document doc = Jsoup.connect(startingPointLink).get();
        Elements categories = getAllCategories(doc);

        Map<String, Map<String, String>> categoriesAndTopics = new HashMap<>();

        for(Element category : categories) {
            categoriesAndTopics.put(getCategoryName(category),
                                     getTopicsWithLinks(getTopicElements(category)));
        }

        return categoriesAndTopics;
    }

    private Map<String, String> getTopicsWithLinks(Elements categoryElements) {
        Map<String, String> topicsWithLinks = new HashMap<>();

        categoryElements.stream().forEach(categoryElement -> {
            List<Element> allTopics = getAllTopicElements(categoryElement);

            for(Element topicElement : allTopics) {
                topicsWithLinks.put(topicElement.attr(TITLE_ATTRIBUTE),
                                    topicElement.attr(HREF_ATTRIBUTE));
            }
        });

        return topicsWithLinks;
    }

    private Elements getAllCategories(Document doc) {
        return doc.body().getElementsByClass(documentParsingConfig.categoryContainer);
    }

    private Elements getTopicElements(Element element) {
        return element.getElementsByClass(documentParsingConfig.categoryTopics);
    }

    private String getCategoryName(Element element) {
        return element.getElementsByClass(documentParsingConfig.categoryTitle).first().child(0).text();
    }

    private List<Element> getAllTopicElements(Element element) {
        List<Element> allTopics = new ArrayList<>();

        Element firstColumn = element.getElementsByClass(documentParsingConfig.categoryColumn1).first();
        Element secondColumn = element.getElementsByClass(documentParsingConfig.categoryColumn2).first();

        allTopics.addAll(firstColumn.getElementsByClass(documentParsingConfig.topicInfo));
        allTopics.addAll(secondColumn.getElementsByClass(documentParsingConfig.topicInfo));

        return allTopics;
    }
}
