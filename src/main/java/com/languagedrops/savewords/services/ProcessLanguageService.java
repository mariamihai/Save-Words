package com.languagedrops.savewords.services;

import com.languagedrops.savewords.model.WordInfo;
import com.languagedrops.savewords.dataCollectors.LanguageDataCollector;
import com.languagedrops.savewords.dataCollectors.TopicDataCollector;
import com.languagedrops.savewords.dataCollectors.SvgCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ProcessLanguageService {

    @Autowired
    LanguageDataCollector languageDataCollector;
    @Autowired
    TopicDataCollector topicDataCollector;
    @Autowired
    DocumentConstructor documentConstructor;
    @Autowired
    SvgCollector svgCollector;


    public void processLanguage(String language, String startingPointLink, String coreLink, String folderPath) {
        Map<String, Map<String, String>> categoriesAndTopics;

        try {
            categoriesAndTopics = languageDataCollector.mapLanguage(startingPointLink);
        } catch (IOException e) {
            log.error("Couldn't load the language based on the given url - " + startingPointLink);
            log.error(e.toString());
            return;
        }

        documentConstructor.createInitialDocument(folderPath, language, coreLink, categoriesAndTopics);

        categoriesAndTopics.forEach((categoryName, topics) -> {
            Map<String, List<WordInfo>> wordsInCategory = processCategory(coreLink, folderPath, categoryName, topics);
            documentConstructor.createSheetForCategory(folderPath, language, categoryName, wordsInCategory);
        });

        svgCollector.deletedTempImage();
    }

    private Map<String, List<WordInfo>> processCategory(String coreLink, String folderPath,
                                                        String categoryName, Map<String, String> topics) {
        log.info("Processing category " + categoryName);

        Map<String, List<WordInfo>> wordsInCategory = new HashMap<>();

        topics.forEach((topicName, topicLink) -> {
            try {
                List<WordInfo> wordsForTopic = topicDataCollector.getWordsForLesson(coreLink + topicLink);
                wordsInCategory.put(topicName, wordsForTopic);

                svgCollector.saveImagesForTopic(folderPath + categoryName + "/" + topicName,
                                               wordsForTopic);

            } catch (IOException e) {
                log.trace("Couldn't load the topic's words. Topic " + topicName + " ( " + coreLink + topicLink + " )", e);
            }
        });

        return wordsInCategory;
    }
}
