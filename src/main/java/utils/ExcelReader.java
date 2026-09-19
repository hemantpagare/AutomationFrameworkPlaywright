package utils;

import org.apache.poi.ss.usermodel.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelReader {
    public static Map<String, String> getTestData(String excelPath, String sheetName, String testId) {
        Map<String, String> dataMap = new HashMap<>();
        try (InputStream fis = ExcelReader.class.getClassLoader().getResourceAsStream(excelPath)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = workbook.getSheet(sheetName);
            Row headerRow = sheet.getRow(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && row.getCell(0).getStringCellValue().equalsIgnoreCase(testId)) {
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell keyCell = headerRow.getCell(j);
                        Cell valCell = row.getCell(j);
                        dataMap.put(keyCell.getStringCellValue(), valCell != null ? valCell.toString() : "");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to read test data from Excel", e);
        }
        return dataMap;
    }
}