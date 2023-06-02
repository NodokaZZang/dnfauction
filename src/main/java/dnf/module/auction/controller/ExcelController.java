package dnf.module.auction.controller;

import dnf.module.auction.repository.BoardRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class ExcelController {

    @Autowired
    BoardRepository boardRepository;

    @GetMapping("/download")
    public void download(HttpServletResponse res) throws Exception {
        /**
         * excel sheet 생성
         */
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("작성게시글"); // 엑셀 sheet 이름
        sheet.setDefaultColumnWidth(28); // 디폴트 너비 설정

        /**
         * header font style
         */
        XSSFFont headerXSSFFont = (XSSFFont) workbook.createFont();
        headerXSSFFont.setColor(new XSSFColor(new byte[]{(byte) 255, (byte) 255, (byte) 255}));

        /**
         * header cell style
         */
        XSSFCellStyle headerXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        // 테두리 설정
        headerXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        headerXssfCellStyle.setBorderRight(BorderStyle.THIN);
        headerXssfCellStyle.setBorderTop(BorderStyle.THIN);
        headerXssfCellStyle.setBorderBottom(BorderStyle.THIN);
        headerXssfCellStyle.setAlignment(HorizontalAlignment.CENTER);
        // 배경 설정
        headerXssfCellStyle.setFillForegroundColor(new XSSFColor(new byte[]{(byte) 34, (byte) 37, (byte) 41}));
        headerXssfCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerXssfCellStyle.setFont(headerXSSFFont);

        /**
         * body cell style
         */
        XSSFCellStyle bodyXssfCellStyle = (XSSFCellStyle) workbook.createCellStyle();

        // 테두리 설정
        bodyXssfCellStyle.setBorderLeft(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderRight(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderTop(BorderStyle.THIN);
        bodyXssfCellStyle.setBorderBottom(BorderStyle.THIN);

        /**
         * header data
         */
        int rowCount = 0; // 데이터가 저장될 행
        String headerNames[] = new String[]{"번호", "제목", "작성자", "작성일", "조회수", "추천"};

        Row headerRow = null;
        Cell headerCell = null;

        headerRow = sheet.createRow(rowCount++);
        for(int i=0; i<headerNames.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headerNames[i]); // 데이터 추가
            headerCell.setCellStyle(headerXssfCellStyle); // 스타일 추가
        }

        /**
         * body data
         */

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserDetails userDetails = (UserDetails)principal;
        String userId = userDetails.getUsername();


        List<Map<String, Object>> dataList = boardRepository.mylistViewAll(userId);

        Row bodyRow = null;
        for(Map<String, Object> data : dataList) {
            bodyRow = sheet.createRow(rowCount++);

            Cell sqCell = bodyRow.createCell(0);
            sqCell.setCellStyle(bodyXssfCellStyle);
            Cell titleCell = bodyRow.createCell(1);
            titleCell.setCellStyle(bodyXssfCellStyle);
            Cell regByCell = bodyRow.createCell(2);
            regByCell.setCellStyle(bodyXssfCellStyle);
            Cell regDtCell = bodyRow.createCell(3);
            regDtCell.setCellStyle(bodyXssfCellStyle);
            Cell viewCell = bodyRow.createCell(4);
            viewCell.setCellStyle(bodyXssfCellStyle);
            Cell voteCell = bodyRow.createCell(5);
            voteCell.setCellStyle(bodyXssfCellStyle);

            sqCell.setCellValue((Long) data.get("sq"));
            titleCell.setCellValue((String) data.get("title"));
            regByCell.setCellValue((String) data.get("regBy"));
            regDtCell.setCellValue((String) data.get("regDt"));
            viewCell.setCellValue((int) data.get("view"));
            voteCell.setCellValue((int) data.get("vote"));
        }

        /**
         * download
         */
        String fileName = "spring_excel_download";

        res.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        res.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
        ServletOutputStream servletOutputStream = res.getOutputStream();

        workbook.write(servletOutputStream);
        workbook.close();
        servletOutputStream.flush();
        servletOutputStream.close();
    }
}
