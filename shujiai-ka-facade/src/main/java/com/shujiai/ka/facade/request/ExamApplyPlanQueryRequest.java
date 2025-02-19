package com.shujiai.ka.facade.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class ExamApplyPlanQueryRequest implements Serializable {

    @NotNull(message = "ID不能为空")
    private Integer id;

    private Integer year;
}
