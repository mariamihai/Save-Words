package com.languagedrops.savewords.model;

import lombok.*;

@Data
@AllArgsConstructor
public class WordInfo {

    private String translatedWord;
    private String nativeWord;
    private String url;
    private Boolean isAnimation;
}
