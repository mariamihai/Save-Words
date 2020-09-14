package com.languagedrops.savewords.domain;

import lombok.*;

@Data
@AllArgsConstructor
public class WordInfo {

    private String translatedWord;
    private String nativeWord;
    private String url;
    private Boolean isAnimation;
}
