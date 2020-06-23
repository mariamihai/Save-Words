package com.languagedrops.savewords.services;

import com.languagedrops.savewords.model.WordInfo;
import com.languagedrops.savewords.datacollectors.LanguageDataCollector;
import com.languagedrops.savewords.datacollectors.TopicDataCollector;
import com.languagedrops.savewords.datacollectors.SvgCollector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProcessLanguageService {

    private LanguageDataCollector languageDataCollector;
    private TopicDataCollector topicDataCollector;
    private DocumentConstructor documentConstructor;
    private SvgCollector svgCollector;

    private static final String ADDITIONAL_CATEGORY = "Random";

    public ProcessLanguageService(LanguageDataCollector languageDataCollector, TopicDataCollector topicDataCollector,
                                  DocumentConstructor documentConstructor, SvgCollector svgCollector) {
        this.languageDataCollector = languageDataCollector;
        this.topicDataCollector = topicDataCollector;
        this.documentConstructor = documentConstructor;
        this.svgCollector = svgCollector;
    }

    public void processLanguage(String language, String startingPointLink, String coreLink, String folderPath) {
        Map<String, Map<String, String>> categoriesAndTopics;

        try {
            categoriesAndTopics = languageDataCollector.mapLanguage(startingPointLink);
        } catch (IOException e) {
            log.error("Couldn't load the language based on the given url - " + startingPointLink, e);
            return;
        }

        documentConstructor.createInitialDocument(folderPath, language, coreLink, categoriesAndTopics);

        categoriesAndTopics.forEach((categoryName, topics) -> {
            Map<String, List<WordInfo>> wordsInCategory = processCategory(coreLink, categoryName, folderPath, topics);
            documentConstructor.createSheetForCategory(folderPath, language, categoryName, wordsInCategory);
        });

        Map<String, List<WordInfo>> additionalWords = processAdditionalTopics(folderPath, ADDITIONAL_CATEGORY, coreLink);
        documentConstructor.createSheetForCategory(folderPath, language, ADDITIONAL_CATEGORY, additionalWords);

        svgCollector.deletedTempImage();
    }

    private Map<String, List<WordInfo>> processCategory(String coreLink, String categoryName,
                                                        String folderPath, Map<String, String> topics) {
        log.info("Processing category " + categoryName);

        Map<String, List<WordInfo>> wordsInCategory = new HashMap<>();

        topics.forEach((topicName, topicLink) ->
                wordsInCategory.putAll(processTopic(topicName, topicLink,
                        folderPath, categoryName,
                        coreLink)));

        return wordsInCategory;
    }

    private Map<String, List<WordInfo>> processAdditionalTopics(String folderPath, String categoryName, String coreLink) {
        log.info("Processing the additional topics");

        Map<String, List<WordInfo>> wordsInAdditionalTopics = new HashMap<>();

        Set<Map.Entry<String, String>> availableRelatedTopics = getAvailableRelatedTopics();
        availableRelatedTopics.forEach(entry ->
                wordsInAdditionalTopics.putAll(processTopic(entry.getKey(), entry.getValue(),
                                                            folderPath, categoryName,
                                                            coreLink)));

        return wordsInAdditionalTopics;
    }

    private Map<String, List<WordInfo>> processTopic(String topicName, String topicLink,
                                                     String folderPath, String categoryName,
                                                     String coreLink) {
        Map<String, List<WordInfo>> wordsInTopic = new HashMap<>();

        try {
            List<WordInfo> wordsForTopic = topicDataCollector.getWordsForLesson(coreLink + topicLink);
            wordsInTopic.put(topicName, wordsForTopic);

            svgCollector.saveImagesForTopic(folderPath + categoryName + "/" + topicName, wordsForTopic);
        } catch (IOException e) {
            log.error("Couldn't load the topic's words. Topic " + topicName + " ( " + coreLink + topicLink + " )", e);
        }

        return wordsInTopic;
    }

    private Set<Map.Entry<String, String>> getAvailableRelatedTopics() {
        Set<String> allTopicNames = languageDataCollector.getAllTopicNames();
        Map<String, String> relatedTopics = topicDataCollector.getRelatedTopics();

        return relatedTopics.entrySet().stream()
                .filter(entry -> !allTopicNames.contains(entry.getKey()))
                .collect(Collectors.toSet());
    }
}
