package com.shujiai.ka.excel;


import com.alibaba.excel.write.metadata.WriteTable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ExcelTableDataDTO {

    private WriteTable writeTable;

    private Integer excelArrange;

    private List<List<Object>> valueList;

}
