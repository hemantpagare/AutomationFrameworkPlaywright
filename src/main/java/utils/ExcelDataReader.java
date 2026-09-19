package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelDataReader {
    private static Map<String, Map<String, String>> testDataMap = new HashMap<>();

    public static void loadExcelData(String excelFilePath) {
        testDataMap.clear();
        File file = new File(excelFilePath);
        System.out.println(">>> [DEBUG] Attempting to load Excel from absolute path: " + file.getAbsolutePath());

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            int colCount = headerRow.getPhysicalNumberOfCells();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Cell tcIdCell = row.getCell(0);
                if (tcIdCell == null) continue;

                // Read TC_ID securely as string and trim whitespace
                tcIdCell.setCellType(CellType.STRING);
                String tcId = tcIdCell.getStringCellValue().trim();

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    String header = headerRow.getCell(j).getStringCellValue().trim();
                    Cell cell = row.getCell(j);
                    String value = "";
                    if (cell != null) {
                        cell.setCellType(CellType.STRING);
                        value = cell.getStringCellValue().trim();
                    }
                    rowData.put(header, value);
                }
                testDataMap.put(tcId, rowData);
            }
            System.out.println(">>> [DEBUG] Successfully loaded Excel data. Total rows mapped: " + testDataMap.size());
        } catch (Exception e) {
            throw new RuntimeException("Failed to load Excel test data from path: " + file.getAbsolutePath(), e);
        }
    }

    public static String getData(String tcId, String columnName) {
        Map<String, String> rowData = testDataMap.get(tcId);
        if (rowData == null) {
            throw new RuntimeException("No test data found for Test Case ID: [" + tcId + "]. Available IDs in map: " + testDataMap.keySet());
        }
        return rowData.getOrDefault(columnName, "");
    }
}