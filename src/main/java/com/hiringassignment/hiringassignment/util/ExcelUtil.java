package com.hiringassignment.hiringassignment.util;

import com.hiringassignment.hiringassignment.dto.Child;
import com.hiringassignment.hiringassignment.dto.ListingData;
import com.hiringassignment.hiringassignment.dto.PostData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {
    public static ByteArrayOutputStream generateExcelFromListing(ListingData listingData) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Posts");

            Row headerRow = sheet.createRow(0);
            String[] headers = {"Id", "Title", "Author", "Subreddit", "Ups", "Downs", "Score", "Url", "Permalink", "CreatedUtc", "Thumbnail"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            List<Child> children = listingData.getChildren();
            int rowIdx = 1;
            if (children != null) {
                for (Child child : children) {
                    PostData post = child.getData();
                    if (post == null) continue;

                    Row row = sheet.createRow(rowIdx++);
                    row.createCell(0).setCellValue(post.getId());
                    row.createCell(1).setCellValue(post.getTitle());
                    row.createCell(2).setCellValue(post.getAuthor());
                    row.createCell(3).setCellValue(post.getSubreddit());
                    row.createCell(4).setCellValue(post.getUps());
                    row.createCell(5).setCellValue(post.getDowns());
                    row.createCell(6).setCellValue(post.getScore());
                    row.createCell(7).setCellValue(post.getUrl());
                    row.createCell(8).setCellValue(post.getPermalink());
                    row.createCell(9).setCellValue(post.getCreatedUtc());
                    row.createCell(10).setCellValue(post.getThumbnail());
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream;
        }
    }
}
