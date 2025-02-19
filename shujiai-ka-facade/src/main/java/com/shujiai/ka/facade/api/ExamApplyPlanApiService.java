package com.shujiai.ka.facade.api;

import com.shujiai.ka.facade.dto.ExamApplyPlanDTO;
import com.shujiai.ka.facade.request.ExamApplyPlanQueryRequest;
import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface ExamApplyPlanApiService{
    /**
     * 获取列表
     *
     * @param context
     * @param appId
     * @param version
     * @param request
     * @return
     */
    Result<List<ExamApplyPlanDTO>> getExamApplyPlanList(@Valid @NotNull Context context,
                                                        @Valid @NotEmpty(message = "appId字段不能为空") String appId,
                                                        @Valid @NotEmpty(message = "version字段不能为空") String version,
                                                        ExamApplyPlanQueryRequest request) ;

}
