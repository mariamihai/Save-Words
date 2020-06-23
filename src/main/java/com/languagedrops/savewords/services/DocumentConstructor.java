package com.languagedrops.savewords.services;

import com.languagedrops.savewords.model.WordInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class DocumentConstructor {

    private static final String OVERVIEW_SHEET = "Overview";
    private static final String EXTENSION = ".xlsx";


    public void createInitialDocument(String folderPath, String language, String coreLink, Map<String, Map<String, String>> categoriesAndLessons) {
        try(Workbook workbook = new XSSFWorkbook()) {
            constructOverviewSheet(getSheet(workbook, OVERVIEW_SHEET), coreLink, categoriesAndLessons);

            writeToFile(folderPath, language, workbook);
        } catch (IOException e) {
            log.error("Exception thrown when creating the document", e);
        }
    }

    public void createSheetForCategory(String folderPath, String language, String categoryName, Map<String, List<WordInfo>> wordsInCategory) {
        try (FileInputStream file = new FileInputStream(new File(folderPath + language + EXTENSION))) {
            Workbook workbook = new XSSFWorkbook(file);

            constructCategorySheet(getSheet(workbook, categoryName),
                                   wordsInCategory);

            writeToFile(folderPath, language, workbook);
        } catch (FileNotFoundException e) {
            log.error("File " + language + ".xlsx was not found", e);
        } catch (IOException e) {
            log.error("Exception thrown when constructing the sheet for category " + categoryName, e);
        }
    }

    private void constructOverviewSheet(Sheet sheet, String coreLink, Map<String, Map<String, String>> categoriesAndTopics) {
        int rowCnt = 0;

        for(Map.Entry<String, Map<String, String>> entry : categoriesAndTopics.entrySet()) {
            Row categoryRow = sheet.createRow(rowCnt++);
            addCell(categoryRow, 0, entry.getKey());

            for(Map.Entry<String, String> topic : entry.getValue().entrySet()) {
                Row topicRow = sheet.createRow(rowCnt++);

                addCell(topicRow, 1, topic.getKey());
                addCell(topicRow, 2, coreLink + "/" +topic.getValue());
            }
        }
    }

    private void constructCategorySheet(Sheet categorySheet, Map<String, List<WordInfo>> wordsInCategory) {
        int rowCnt = 0;

        for(Map.Entry<String, List<WordInfo>> entry : wordsInCategory.entrySet()) {
            String topic = entry.getKey();

            Row topicRow = categorySheet.createRow(rowCnt++);
            addCell(topicRow,0, topic);

            for(WordInfo word : entry.getValue()) {
                Row wordRow = categorySheet.createRow(rowCnt++);

                addCell(wordRow, 1, word.getNativeWord());
                addCell(wordRow, 2, word.getTranslatedWord());
                addCell(wordRow, 3, word.getUrl());
            }
        }
    }

    private void writeToFile(String folderPath, String language, Workbook workbook) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(folderPath + language + EXTENSION));
        workbook.write(out);
        out.close();
    }

    private Sheet getSheet(Workbook workbook, String sheetName) {
        if(isSheetCreated(workbook, sheetName)) {
            workbook.removeSheetAt(workbook.getSheetIndex(sheetName));
        }

        return workbook.createSheet(sheetName);
    }
    private boolean isSheetCreated(Workbook workbook, String sheetName) {
        return workbook.getSheetIndex(sheetName) != -1;
    }

    private void addCell(Row row, int index, String value) {
        Cell cell = row.createCell(index);
        cell.setCellValue(value);
    }
}