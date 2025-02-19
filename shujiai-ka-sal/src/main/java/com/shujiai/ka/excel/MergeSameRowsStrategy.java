package com.shujiai.ka.excel;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.List;

/**
 * 合并相同数据行策略
 */
@Data
public class MergeSameRowsStrategy implements CellWriteHandler {
    private int[] mergeColumnIndex;
    private int mergeRowIndex;

    private int mergeRowStartIndex=-1;
    private int mergeRowEndIndex=-1;
    private int mergeColumnStartIndex=-1;
    private int mergeColumnEndIndex=-1;

    public MergeSameRowsStrategy() {
    }

    public MergeSameRowsStrategy(int mergeRowIndex, int[] mergeColumnIndex) {
        this.mergeRowIndex = mergeRowIndex;
        this.mergeColumnIndex = mergeColumnIndex;
    }

    public MergeSameRowsStrategy(int mergeRowStartIndex,int mergeRowEndIndex, int mergeColumnStartIndex, int mergeColumnEndIndex) {
        this.mergeRowStartIndex = mergeRowStartIndex;
        this.mergeRowEndIndex = mergeRowEndIndex;
        this.mergeColumnStartIndex = mergeColumnStartIndex;
        this.mergeColumnEndIndex = mergeColumnEndIndex;
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, List<WriteCellData<?>> list, Cell cell, Head head, Integer integer, Boolean aBoolean) {
        // 当前行
        int curRowIndex = cell.getRowIndex();
        // 当前列
        int curColIndex = cell.getColumnIndex();

        if(mergeColumnIndex==null){
            if(mergeRowStartIndex!=-1 && mergeRowEndIndex!=-1 && mergeColumnStartIndex!=-1 && mergeColumnEndIndex!=-1){
                if(curRowIndex>=mergeRowStartIndex && curRowIndex<=mergeRowEndIndex && curColIndex>=mergeColumnStartIndex && curColIndex<=mergeColumnEndIndex){
                    mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
                }
            }else{
                //mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
            }
        }else{
            if (curRowIndex > mergeRowIndex) {
                for (int i = 0; i < mergeColumnIndex.length; i++) {
                    if (curColIndex == mergeColumnIndex[i]) {
                        mergeWithPrevRow(writeSheetHolder, cell, curRowIndex, curColIndex);
                        break;
                    }
                }
            }
        }

    }

    /**
     * 当前单元格向上合并
     *
     * @param writeSheetHolder sheet保持对象
     * @param cell             当前单元格
     * @param curRowIndex      当前行
     * @param curColIndex      当前列
     */
    private void mergeWithPrevRow(WriteSheetHolder writeSheetHolder, Cell cell, int curRowIndex, int curColIndex) {
        // 获取当前行的当前列的数据和上一行的当前列列数据，通过上一行数据是否相同进行合并
        Object curData = cell.getCellTypeEnum() == CellType.STRING ? cell.getStringCellValue() : cell.getNumericCellValue();
        Cell preCell = cell.getSheet().getRow(curRowIndex - 1).getCell(curColIndex);
        Object preData = preCell.getCellTypeEnum() == CellType.STRING ? preCell.getStringCellValue() : preCell.getNumericCellValue();

        // 比较当前行的第一列的单元格与上一行是否相同，相同合并当前单元格与上一行
        if (ObjectUtil.isNotNull(curData) && StringUtils.isNotBlank(curData.toString()) && curData.equals(preData)) {
            Sheet sheet = writeSheetHolder.getSheet();
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            boolean isMerged = false;
            for (int i = 0; i < mergedRegions.size() && !isMerged; i++) {
                CellRangeAddress cellRangeAddr = mergedRegions.get(i);
                // 若上一个单元格已经被合并，则先移出原有的合并单元，再重新添加合并单元
                if (cellRangeAddr.isInRange(curRowIndex - 1, curColIndex)) {
                    sheet.removeMergedRegion(i);
                    cellRangeAddr.setLastRow(curRowIndex);
                    sheet.addMergedRegion(cellRangeAddr);
                    isMerged = true;
                }
            }
            // 若上一个单元格未被合并，则新增合并单元
            if (!isMerged) {
                CellRangeAddress cellRangeAddress = new CellRangeAddress(curRowIndex - 1, curRowIndex, curColIndex, curColIndex);
                sheet.addMergedRegion(cellRangeAddress);
            }
        }
    }
}
