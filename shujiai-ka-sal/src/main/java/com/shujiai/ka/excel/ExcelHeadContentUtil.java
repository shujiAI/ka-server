package com.shujiai.ka.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.AbstractCellStyleStrategy;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import org.apache.poi.ss.usermodel.*;

public class ExcelHeadContentUtil extends AbstractCellStyleStrategy {

    public ExcelHeadContentUtil() {
    }

    @Override
    protected void setHeadCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
    }

    @Override
    protected void setContentCellStyle(Cell cell, Head head, Integer relativeRowIndex) {
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }


    public static HorizontalCellStyleStrategy getHorizontalCellStyleStrategy(Boolean hasBorder,int titleType) {
        // 头的策略
        WriteCellStyle headWriteCellStyle = new WriteCellStyle();
        // 背景设置为白色
        headWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.index);
        //headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        headWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        //边框
        if(hasBorder){
            headWriteCellStyle.setBorderBottom(BorderStyle.THIN);
            headWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            headWriteCellStyle.setBorderRight(BorderStyle.THIN);
            headWriteCellStyle.setBorderTop(BorderStyle.THIN);
        }else {
            headWriteCellStyle.setBorderBottom(BorderStyle.NONE);
            headWriteCellStyle.setBorderLeft(BorderStyle.NONE);
            headWriteCellStyle.setBorderRight(BorderStyle.NONE);
            headWriteCellStyle.setBorderTop(BorderStyle.NONE);
        }

        //自动换行
        headWriteCellStyle.setWrapped(true);
        WriteFont headWriteFont = new WriteFont();
        if(titleType==1){
            headWriteFont.setBold(true);
            headWriteFont.setFontName("宋体");
            headWriteFont.setFontHeightInPoints((short)18);

            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        }else if(titleType==2){
            headWriteFont.setBold(true);
            headWriteFont.setFontName("宋体");
            headWriteFont.setFontHeightInPoints((short)14);

            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.CENTER);
        }else {
            headWriteFont.setBold(false);
            headWriteFont.setFontName("宋体");
            headWriteFont.setFontHeightInPoints((short)12);

            headWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        }

        headWriteCellStyle.setWriteFont(headWriteFont);
        // 内容的策略
        WriteCellStyle contentWriteCellStyle = new WriteCellStyle();
        // 这里需要指定 FillPatternType 为FillPatternType.SOLID_FOREGROUND 不然无法显示背景颜色.头默认了 FillPatternType所以可以不指定
        contentWriteCellStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        // 背景颜色
        contentWriteCellStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        contentWriteCellStyle.setHorizontalAlignment(HorizontalAlignment.LEFT);
        contentWriteCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //边框
        if(hasBorder){
            contentWriteCellStyle.setBorderBottom(BorderStyle.THIN);
            contentWriteCellStyle.setBorderLeft(BorderStyle.THIN);
            contentWriteCellStyle.setBorderRight(BorderStyle.THIN);
            contentWriteCellStyle.setBorderTop(BorderStyle.THIN);
        }else {
            contentWriteCellStyle.setBorderBottom(BorderStyle.NONE);
            contentWriteCellStyle.setBorderLeft(BorderStyle.NONE);
            contentWriteCellStyle.setBorderRight(BorderStyle.NONE);
            contentWriteCellStyle.setBorderTop(BorderStyle.NONE);
        }
        //自动换行
        contentWriteCellStyle.setWrapped(true);
        //文字
        WriteFont contentWriteFont = new WriteFont();
        // 字体大小
        contentWriteFont.setFontHeightInPoints((short)12);
        contentWriteFont.setFontName("宋体");
        contentWriteCellStyle.setWriteFont(contentWriteFont);
        // 这个策略是 头是头的样式 内容是内容的样式 其他的策略可以自己实现
        return new HorizontalCellStyleStrategy(headWriteCellStyle, contentWriteCellStyle);
    }

}
