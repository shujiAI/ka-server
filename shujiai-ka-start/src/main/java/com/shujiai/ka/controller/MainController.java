package com.shujiai.ka.controller;

import com.shujiai.ka.facade.api.ExamApplyPlanApiService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 监控
 * 用于监控应用状态是否正常
 */
@Controller
@RestController
public class MainController {

    @Reference(check = false, group = "${dubbo.provider.group}", version = "${dubbo.provider.version}", timeout = 10000, retries = 0)
    private ExamApplyPlanApiService tenantService;
    @RequestMapping(value = "/checkpreload.htm")
    public @ResponseBody String checkPreload() {
        return "success";
    }

}
