package utility;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

public class ExcelOperations {

    public static void main(String[] args) throws Exception {

        String data[][] = new String[2][2];
        data[0][0] = "H";
        data[0][1] = "V";
        data[1][0] = "A";
        data[1][1] = "P";

        System.out.println(Arrays.deepToString(data));

        System.out.println(readRecordFromExcel("src/test/resources/foodTestData.xlsx", "Sheet1"));
    }

    public static List<Map<String, Object>> readRecordFromExcel(String filePath, String sheetName) throws Exception {
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheet(sheetName);

        int totalRow = sheet.getLastRowNum() + 1;
        int totalColumn = sheet.getRow(0).getLastCellNum();


        List<String> headers = new ArrayList<>();

        Row cells = sheet.getRow(0);
        for (Cell cell : cells) {
            headers.add(cell.getStringCellValue());
        }

        List<Map<String, Object>> rowList = new ArrayList<>();

        for (int rowCount = 1; rowCount < totalRow; rowCount++) {

            Map<String, Object> pair = new HashMap<>();

            Row currentRow = sheet.getRow(rowCount);
            for (int colCount = 0; colCount < totalColumn; colCount++) {
                Cell cell = currentRow.getCell(colCount);
                if (CellType.NUMERIC == cell.getCellType()) {
                    pair.put(headers.get(colCount), cell.getNumericCellValue());
                } else if (CellType.STRING == cell.getCellType()) {
                    pair.put(headers.get(colCount), cell.getStringCellValue());
                }
            }
            rowList.add(pair);
        }

        return rowList;
    }

}
