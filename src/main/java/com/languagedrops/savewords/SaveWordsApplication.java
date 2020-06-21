package com.languagedrops.savewords;

import com.languagedrops.savewords.config.DefaultLanguageConfig;
import com.languagedrops.savewords.services.DocumentConstructor;
import com.languagedrops.savewords.services.ProcessLanguageService;
import com.languagedrops.savewords.dataCollectors.SvgCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SaveWordsApplication implements CommandLineRunner {

    @Autowired
    DefaultLanguageConfig defaultLanguageConfig;
    @Autowired
    ProcessLanguageService processLanguageService;

    @Autowired
    DocumentConstructor documentConstructor;

    @Autowired
    SvgCollector svgCollector;

    public static void main(String[] args) {
        SpringApplication.run(SaveWordsApplication.class, args);
    }

    @Override
    public void run(String... args) {
        processLanguageService.processLanguage(
                defaultLanguageConfig.defaultLanguage,
                defaultLanguageConfig.defaultStartingPointLink,
                defaultLanguageConfig.defaultCoreLink,
                defaultLanguageConfig.defaultFolderPath);
    }

}
