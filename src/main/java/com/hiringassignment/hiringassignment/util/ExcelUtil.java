package com.hiringassignment.hiringassignment.util;

import com.hiringassignment.hiringassignment.entity.RedditTopPost;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelUtil {

    public static ByteArrayOutputStream generateExcelFromPosts(List<RedditTopPost> posts) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Reddit Top Posts");

            String[] headers = {
                    "CrawlId", "PostId", "Title", "Author", "Subreddit", "Score",
                    "Url", "Permalink", "NumComments", "CreatedAt"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIdx = 1;
            if (posts != null) {
                for (RedditTopPost post : posts) {
                    Row row = sheet.createRow(rowIdx++);

                    if (post.getId() != null) {
                        row.createCell(0).setCellValue(post.getId().getCrawlId() != null ? post.getId().getCrawlId() : 0);
                        row.createCell(1).setCellValue(post.getId().getId() != null ? post.getId().getId() : "");
                    } else {
                        row.createCell(0).setCellValue("");
                        row.createCell(1).setCellValue("");
                    }

                    row.createCell(2).setCellValue(post.getTitle() != null ? post.getTitle() : "");
                    row.createCell(3).setCellValue(post.getAuthor() != null ? post.getAuthor() : "");
                    row.createCell(4).setCellValue(post.getSubreddit() != null ? post.getSubreddit() : "");
                    row.createCell(5).setCellValue(post.getScore() != null ? post.getScore() : 0);
                    row.createCell(6).setCellValue(post.getUrl() != null ? post.getUrl() : "");
                    row.createCell(7).setCellValue(post.getPermalink() != null ? post.getPermalink() : "");
                    row.createCell(8).setCellValue(post.getNumComments() != null ? post.getNumComments() : 0);
                    row.createCell(9).setCellValue(post.getCreatedAt() != null ? post.getCreatedAt().toString() : "");
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