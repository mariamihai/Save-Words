package com.languagedrops.savewords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentParsingConfig {

    @Value("${doc.language.category.container}")
    public String categoryContainer;

    @Value("${doc.language.category.title}")
    public String categoryTitle;

    @Value("${doc.language.category.topics}")
    public String categoryTopics;

    @Value("${doc.language.category.grid.column1}")
    public String categoryColumn1;

    @Value("${doc.language.category.grid.column2}")
    public String categoryColumn2;

    @Value("${doc.language.category.topic.info}")
    public String topicInfo;

    @Value("${doc.language.topic.row}")
    public String topicRow;

    @Value("${doc.language.topic.translated.word}")
    public String translatedWord;

    @Value("${doc.language.topic.native.word}")
    public String nativeWord;

    @Value("${doc.language.topic.illustration.for.word}")
    public String illustrationForWord;

    @Value("${doc.language.topic.animation}")
    public String animation;

    @Value("${doc.language.topic.linkable.topics}")
    public String linkableTopics;

    public static final String TITLE_ATTRIBUTE = "title";
    public static final String HREF_ATTRIBUTE = "href";
    public static final String SRC_ATTRIBUTE = "src";


    public static final String SVG = "svg";
}
