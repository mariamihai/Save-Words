package com.languagedrops.savewords.datacollectors;

import com.languagedrops.savewords.domain.WordInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.List;

import static com.languagedrops.savewords.config.DocumentParsingConfig.SVG;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_AOI;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_HEIGHT;
import static org.apache.batik.transcoder.SVGAbstractTranscoder.KEY_WIDTH;

@Component
@Slf4j
public class SvgCollector {

    private static final String TEMP_IMG = "tmp.svg";

    public void saveImagesForTopic(String path, List<WordInfo> wordsForTopic) {
        wordsForTopic
                .forEach(wordInfo -> saveImage(path, getValidNativeWord(wordInfo.getNativeWord()), wordInfo.getUrl(), wordInfo.getIsAnimation()));
    }

    private void saveImage(String path, String word, String url, boolean isAnimation) {
        if(isAnimation || isEmptyOrNullUrl(url)) {
            log.error("Couldn't process: " + word + "      " + path);
            return;
        }

        String svgString;
        try {
            Element svgElement = getSvgElement(url);
            svgString = svgElement.toString();
            String viewbox = svgElement.attr("viewbox");

            saveTempImage(svgString);
            convertToPng(path, word, getOriginalWidth(viewbox), getOriginalHeight(viewbox));
        } catch (IOException e) {
            log.trace("Invalid " + word + " - " + url, e);
        }
    }

    private Element getSvgElement(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get().getElementsByTag(SVG).first();
    }

    private void saveTempImage(String svgString) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TEMP_IMG))) {
            writer.write(replaceLowerCaseAttributes(revertColors(svgString)));
        }
    }

    private String revertColors(String svgString) {
        String replacement = "#000000";

        return svgString.replace("#FFFFFF", replacement)
                        .replace("#fdfdfd", replacement)
                        .replace("#fefefe", replacement)
                        .replace("#fff", "#000");
    }

    private String replaceLowerCaseAttributes(String svgString) {
        return svgString.replace("#fff", "#000")
                        .replace("viewbox", "viewBox")
                        .replace("foreignobject", "foreignObject")
                        .replace("requiredextensions", "requiredExtensions");
    }

    public void deletedTempImage() {
        File file = new File(TEMP_IMG);
        boolean isDeleted = file.delete();

        if(isDeleted) {
            log.info("The temporary svg file used to save the original images was deleted.");
        } else {
            log.warn("The temporary svg file ( " + TEMP_IMG + " ) used to save the original images was not deleted. ");
        }
    }

    private void convertToPng(String path, String word, Float width, Float height) {
        createVoidDirectory(path);

        String svgUriInput;
        try {
            svgUriInput = Paths.get(TEMP_IMG).toUri().toURL().toString();
            TranscoderInput inputSvgImage = new TranscoderInput(svgUriInput);
            OutputStream pngOutputStream = new FileOutputStream(path + "/" + word + ".png");
            TranscoderOutput outputPngImage = new TranscoderOutput(pngOutputStream);
            PNGTranscoder converter = new PNGTranscoder();
            converter.addTranscodingHint(KEY_WIDTH, width);
            converter.addTranscodingHint(KEY_HEIGHT, height);
            converter.addTranscodingHint(KEY_AOI, new Rectangle(0, 0, Math.round(width), Math.round(height)));
            converter.transcode(inputSvgImage, outputPngImage);
            pngOutputStream.flush();
            pngOutputStream.close();
        } catch (MalformedURLException e) {
            log.error("MalformedURLException for " + word + " - " + path, e);
        } catch (TranscoderException e) {
            log.error("TranscoderException for " + word + " - " + path, e);
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException for " + word + " - " + path, e);
        } catch (IOException e) {
            log.error("IOException for " + word + " - " + path, e);
        }
    }

    private void createVoidDirectory(String path) {
        File dir = new File( path);
        dir.mkdirs();
    }

    private boolean isEmptyOrNullUrl(String url) {
        return url == null || url.equals("");
    }

    private String getValidNativeWord(String nativeWord) {
        return nativeWord.replace("?", "")
                         .replace("/", " ");
    }

    private Float getOriginalWidth(String viewbox) {
        return Float.parseFloat(viewbox.split(" ")[2]);
    }

    private Float getOriginalHeight(String viewbox) {
        return Float.parseFloat(viewbox.split(" ")[3]);
    }
}