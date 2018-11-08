package com.zaiou.web.common.export;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jxl.Cell;
import jxl.Range;
import jxl.Sheet;
import jxl.Workbook;
import jxl.format.CellFormat;
import jxl.read.biff.BiffException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description: excel 转pdf 工具类 （导入的excel 支持2012 以前版本）
 * @auther: LB 2018/11/6 15:38
 * @modify: LB 2018/11/6 15:38
 */
public class Excel2Pdf {
    public static void excelToPdf(String startPath, String endPath) throws DocumentException, IOException, BiffException {
        try {
            Document document = new Document(PageSize.A4, 0, 0, 50, 0);
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(endPath));
            BaseFont bf;
            //           bf = BaseFont.createFont("TSong-Light", "UniGB-UCS2-H",
            //         BaseFont.NOT_EMBEDDED);//创建字体
            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",BaseFont.EMBEDDED);
//            bf = BaseFont.createFont(path2, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            Font font = new Font(bf, 12);//使用字体
            int rowNum = 0;
            int colNum = 0;

            InputStream is = new FileInputStream(startPath);
            Workbook workbook = Workbook.getWorkbook(is);
            Sheet sheet = workbook.getSheet(0);
            int column = sheet.getColumns();
            //下面是找出表格中的空行和空列
            List<Integer> nullCol = new ArrayList<>();
            List<Integer> nullRow = new ArrayList<>();
            for (int j = 0; j < sheet.getColumns(); j++) {
                int nullColNum = 0;
                for (int i = 0; i < sheet.getRows(); i++) {
                    Cell cell = sheet.getCell(j, i);
                    String str = cell.getContents();
                    if (str == null || "".equals(str)) {
                        nullColNum++;
                    }
                }
                if (nullColNum == sheet.getRows()) {
                    nullCol.add(j);
                    column--;
                }
            }
            for (int i = 0; i < sheet.getRows(); i++) {
                int nullRowNum = 0;
                for (int j = 0; j < sheet.getColumns(); j++) {
                    Cell cell = sheet.getCell(j, i);
                    String str = cell.getContents();
                    if (str == null || "".equals(str)) {
                        nullRowNum++;
                    }
                }
                if (nullRowNum == sheet.getColumns()) {
                    nullRow.add(i);
                }
            }
            PdfPTable table = new PdfPTable(column);
            Range[] ranges = sheet.getMergedCells();
            PdfPCell cell1 = new PdfPCell();
            for (int i = 0; i < sheet.getRows(); i++) {
                if (nullRow.contains(i)) {    //如果这一行是空行，这跳过这一行
                    continue;
                }
                for (int j = 0; j < sheet.getColumns(); j++) {
                    if (nullCol.contains(j)) {    //如果这一列是空列，则跳过这一列
                        continue;
                    }
                    boolean flag = true;
                    Cell cell = sheet.getCell(j, i);
                    String str = cell.getContents();

                    CellFormat cellFormat = cell.getCellFormat();
                    jxl.format.Font fonts = cellFormat.getFont();
                    String boldWeight = fonts.getBoldWeight()+"";
                    //判断Excel单元格是否是粗体 如果是粗体字体设置为粗体
                    if("700".equals(boldWeight)) {
                        font = new Font(bf, 12, Font.BOLD);//使用字体
                    }
                    for (Range range : ranges) {    //合并的单元格判断和处理
                        if (j >= range.getTopLeft().getColumn() && j <= range.getBottomRight().getColumn()
                                && i >= range.getTopLeft().getRow() && i <= range.getBottomRight().getRow()) {
                            if (str == null || "".equals(str)) {
                                flag = false;
                                break;
                            }
                            rowNum = range.getBottomRight().getRow() - range.getTopLeft().getRow() + 1;
                            colNum = range.getBottomRight().getColumn() - range.getTopLeft().getColumn() + 1;
                            if (rowNum > colNum) {
                                cell1 = mergeRow(str, font, rowNum);
                                cell1.setColspan(colNum);
                                table.addCell(cell1);
                            } else {
                                cell1 = mergeCol(str, font, colNum);
                                cell1.setRowspan(rowNum);
                                table.addCell(cell1);
                            }
                            //System.out.println(num1 + "  " + num2);
                            flag = false;
                            break;
                        }
                    }
                    if (flag) {
                        table.addCell(getPDFCell(str, font));
                    }
                    //font重新还回初始状态
                    font = new Font(bf, 12);//使用字体
                }
            }

            workbook.close();
            document.open();
            document.add(table);
            document.close();
            writer.close();
        } catch (Exception e) {
            throw e;
        }
    }

    //合并行的静态函数
    public static PdfPCell mergeRow(String str, Font font, int i) {

        //创建单元格对象，将内容及字体传入
        PdfPCell cell = new PdfPCell(new Paragraph(str, (Font) font));
        //设置单元格内容居中
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //将该单元格所在列包括该单元格在内的i行单元格合并为一个单元格
        cell.setRowspan(i);

        return cell;
    }

    //合并列的静态函数
    public static PdfPCell mergeCol(String str, Font font, int i) {

        PdfPCell cell = new PdfPCell(new Paragraph(str, (Font) font));
        cell.setMinimumHeight(25);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        //将该单元格所在行包括该单元格在内的i列单元格合并为一个单元格
        cell.setColspan(i);

        return cell;
    }

    //获取指定内容与字体的单元格
    public static PdfPCell getPDFCell(String string, Font font) {
        //创建单元格对象，将内容与字体放入段落中作为单元格内容
        PdfPCell cell = new PdfPCell(new Paragraph(string, (Font) font));

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        //设置最小单元格高度
        cell.setMinimumHeight(25);
        return cell;
    }
}
