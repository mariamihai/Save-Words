package com.languagedrops.savewords.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DefaultLanguageConfig {

    @Value("${default.link.starting.point}")
    public String defaultStartingPointLink;

    @Value("${default.link.core}")
    public String defaultCoreLink;

    @Value("${default.language}")
    public String defaultLanguage;


    @Value("${default.folder.path}")
    public String defaultFolderPath;
}
