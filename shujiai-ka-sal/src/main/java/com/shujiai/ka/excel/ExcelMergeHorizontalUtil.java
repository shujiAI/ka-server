package com.shujiai.ka.excel;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

/**
 * 横向合并工具
 */
public class ExcelMergeHorizontalUtil extends AbstractMergeStrategy {

    private final int firstCol;
    private final int lastCol;

    public ExcelMergeHorizontalUtil(int firstCol, int lastCol) {
        this.firstCol = firstCol;
        this.lastCol = lastCol;
    }

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer integer) {
        if(firstCol < lastCol){
            CellRangeAddress cellAddresses = new CellRangeAddress(cell.getRowIndex(), cell.getRowIndex(), firstCol, lastCol);
            sheet.addMergedRegionUnsafe(cellAddresses);
        }

    }
}
