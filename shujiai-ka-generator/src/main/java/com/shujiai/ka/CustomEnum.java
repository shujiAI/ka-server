package com.shujiai.ka;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhanghp
 * @date 2022-07-17 23:11
 */
public enum CustomEnum {

    DTO("DTO", "templates/model/DTO.java.vm"),
    Request("Request", "templates/model/Request.java.vm"),
    PageRequest("PageRequest", "templates/model/PageRequest.java.vm"),

    DubboService("dubbo", "templates/model/dubboService.java.vm"),
    DubboServiceImpl("dubboImpl", "templates/model/dubboServiceImpl.java.vm");

    CustomEnum(String modelSuffix, String path) {
        this.modelSuffix = modelSuffix;
        this.path = path;
    }

    @Getter
    private String modelSuffix;

    @Getter
    private String path;

    public static List<String> getModel(){
        List<String> result = new ArrayList<>();
        CustomEnum[] values = CustomEnum.values();
        for (CustomEnum it : values) {
            result.add(it.getModelSuffix());
        }
        return result;
    }

}
