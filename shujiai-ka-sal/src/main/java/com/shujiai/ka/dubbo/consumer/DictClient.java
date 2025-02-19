package com.shujiai.ka.dubbo.consumer;

import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.metadata.facade.DictService;
import com.shujiai.metadata.facade.dto.DictDTO;
import com.shujiai.metadata.facade.dto.DictItemDTO;
import com.shujiai.metadata.facade.dto.PageResult;
import com.shujiai.metadata.facade.dto.query.DictQuery;
import com.shujiai.metadata.facade.dto.request.ListDictItemsRequest;
import com.shujiai.metadata.facade.dto.request.ListDictRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DictClient {
    @Reference(check = false, group = "${dubbo.metadata.group.name}", version = "${dubbo.metadata.version}", timeout = 10000, retries = 0)
    private DictService dictService;

    /**
     * 获取字典
     */
    public List<DictDTO> listDictByPage(Context context, DictQuery request) {
        Result<PageResult<DictDTO>> result = dictService.listDictByPage(context, request);

        if (result != null && result.isSuccess() && result.getData().getRecords() != null) {
            return result.getData().getRecords();
        }

        return null;
    }

    public List<DictDTO> listDict(Context context, ListDictRequest request) {
        Result<List<DictDTO>> result = dictService.listDict(context, request);

        if (result != null && result.isSuccess() && result.getData() != null) {

            return result.getData().stream()
                    .filter(dict -> dict.getDictName().contains("基地自评")
                            || dict.getDictName().contains("表单库")
                            || "培训专业".equals(dict.getDictName())
                            || "通用是否".equals(dict.getDictName()))
                    .peek(dict -> {
                        ListDictItemsRequest itemRequest = new ListDictItemsRequest();
                        itemRequest.setDictId(dict.getId());
                        itemRequest.setStatus("ALL");
                        dict.setItems(listDictItems(context, itemRequest));
                    })
                    .collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 获取字典选项
     */
    public List<DictItemDTO> listDictItems(Context context, ListDictItemsRequest request) {
        Result<List<DictItemDTO>> result = dictService.listDictItems(context, request);

        if (result != null && result.isSuccess()) {
            return result.getData();
        }

        return null;
    }
}
