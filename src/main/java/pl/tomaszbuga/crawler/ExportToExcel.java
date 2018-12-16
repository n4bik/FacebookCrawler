package pl.tomaszbuga.crawler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;

class ExportToExcel {
    private final FacebookLikesCounter facebookLikesCounter;

    ExportToExcel(FacebookLikesCounter facebookLikesCounter) {
        this.facebookLikesCounter = facebookLikesCounter;
    }

    void exportToXls(String fileName) {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();

            XSSFSheet sheet = workbook.createSheet("Sheet1");
            int rowNum = 0;
            for (String like : facebookLikesCounter.getLikes()) {
                String[] split = like.split(";");
                Row row = sheet.createRow(rowNum++);
                createList(split, row);
            }

            try (FileOutputStream out = new FileOutputStream(new File(fileName))) {
                workbook.write(out);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createList(String[] split, Row row) {
        Cell cell = row.createCell(0);
        cell.setCellValue(split[0]);
        cell = row.createCell(1);
        cell.setCellValue(split[1]);
    }
}