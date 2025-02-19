package com.shujiai.ka.dubbo.consumer;

import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.usercenter.facade.api.DepartmentService;
import com.shujiai.usercenter.facade.dto.req.query.DepartmentBatchQueryReq;
import com.shujiai.usercenter.facade.dto.req.query.DepartmentQueryByName;
import com.shujiai.usercenter.facade.dto.req.query.DepartmentQueryByNameAndParentId;
import com.shujiai.usercenter.facade.dto.rsp.DepartmentDTO;
import com.shujiai.usercenter.facade.dto.rsp.DepartmentTreeDTO;
import com.shujiai.usercenter.facade.dto.rsp.HrEmployeeDTO;
import com.shujiai.usercenter.facade.v2.api.DeptCoreService;
import com.shujiai.usercenter.facade.v2.api.EmployeeCoreService;
import com.shujiai.usercenter.facade.v2.request.DepartCoreQuery;
import com.shujiai.usercenter.facade.v2.request.EmployeeCoreQuery;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lianjiang.yulj
 * @date 2022/01/05
 * @time 12:21 上午
 * @description
 */

@Slf4j
@Component
public class DepartmentServiceClient {


    @Reference(check = false, group = "${dubbo.usercenter.group.name}", version = "${dubbo.usercenter.version}", timeout = 10000, retries = 1)
    private DepartmentService departmentService;

    @Reference(check = false, group = "${dubbo.usercenter.group.name}", version = "${dubbo.usercenter.version}", timeout = 10000, retries = 1)
    private EmployeeCoreService employeeCoreService;

    @Reference(check = false, group = "${dubbo.usercenter.group.name}", version = "${dubbo.usercenter.version}", timeout = 10000, retries = 1)
    private DeptCoreService deptCoreService;


    public List<String> getDeptIdsByHrg(Context context, List<String> queryDeptIds) {
        Result<List<DepartmentDTO>> result = departmentService.queryCurrentHrDept(context);
        List<DepartmentDTO> allHrgDeptList = result.getData();
        if (org.springframework.util.CollectionUtils.isEmpty(allHrgDeptList)) {
            return Lists.newArrayList();
        }

        if (org.springframework.util.CollectionUtils.isEmpty(queryDeptIds)) {
            return allHrgDeptList.stream().map(DepartmentDTO::getId).distinct().collect(Collectors.toList());
        }
        // 判断是否包含查询的部门ID
        List<DepartmentDTO> departmentDTOList = this.queryAllChildByMultiDeptId(context, queryDeptIds);

        List<String> deptIds = CollectionUtils.isEmpty(departmentDTOList) ? com.google.common.collect.Lists.newArrayList() : departmentDTOList.stream().map(DepartmentDTO::getId).collect(Collectors.toList());

        List<String> hrgDeptIds = allHrgDeptList.stream().map(DepartmentDTO::getId).filter(deptIds::contains).collect(Collectors.toList());
        return hrgDeptIds;
    }

    /**
     * 获取部门树
     *
     * @param context
     * @return
     */
    public List<DepartmentTreeDTO> queryDeptTree(Context context) {
        Result<List<DepartmentTreeDTO>> result = departmentService.queryDeptTree(context);

        return result.getData();
    }

    /**
     * 获取当前部门及子级所有部门
     *
     * @param context
     * @return
     */
    public List<DepartmentDTO> queryAllChildByDeptId(Context context, String deptId) {
        DepartmentBatchQueryReq req = new DepartmentBatchQueryReq();
        req.setDeptIds(Collections.singletonList(deptId));
        Result<List<DepartmentDTO>> result = departmentService.queryAllChildByDeptId(context, req);

        return result.getData();
    }

    /**
     * 获取多个部门及子级所有部门
     *
     * @param context
     * @return
     */
    public List<DepartmentDTO> queryAllChildByMultiDeptId(Context context, List<String> deptIdList) {
        DepartmentBatchQueryReq req = new DepartmentBatchQueryReq();
        req.setDeptIds(deptIdList);
        Result<List<DepartmentDTO>> result = departmentService.queryAllChildByDeptId(context, req);

        return Optional.ofNullable(result.getData()).orElse(Lists.newArrayList());
    }

    /**
     * 当前操作人所有任职部门(本级及以上)
     *
     * @param context
     * @return
     */
    public List<String> getIdentityPathDeptIds(Context context) {
        List<String> deptIds = new ArrayList<>();
        List<String> deptPaths = this.getDeptPaths(context);
        if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(deptPaths)) {
            for (String deptPath : deptPaths) {
                if (org.apache.commons.lang3.ObjectUtils.isNotEmpty(deptPath)) {
                    String[] pathDeptIds = deptPath.split("/");
                    for (String pathDeptId : pathDeptIds) {
                        if (!deptIds.contains(pathDeptId)) {
                            deptIds.add(pathDeptId);
                        }
                    }
                }
            }
        }

        return deptIds;
    }

    /**
     * 读取登录人的所有任职部门ID
     *
     * @param context
     * @return
     */
    public List<String> getMyDeptIds(Context context) {
        EmployeeCoreQuery employeeCoreQuery = new EmployeeCoreQuery();
        employeeCoreQuery.setTenantId(context.getTenantId());
        employeeCoreQuery.setIdentityId(context.getIdentityId());
        Result<List<HrEmployeeDTO>> result = employeeCoreService.queryByCondition(employeeCoreQuery);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        if (CollectionUtils.isNotEmpty(result.getData())) {
            return result.getData().stream().map(HrEmployeeDTO::getDeptId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取登录人的所有任职部门path
     *
     * @param context
     * @return
     */
    public List<String> getDeptPaths(Context context) {
        List<String> deptPaths = new ArrayList<>();
        EmployeeCoreQuery employeeCoreQuery = new EmployeeCoreQuery();
        employeeCoreQuery.setTenantId(context.getTenantId());
        employeeCoreQuery.setIdentityId(context.getIdentityId());
        Result<List<HrEmployeeDTO>> lstHrEmployeeResult = employeeCoreService.queryByCondition(employeeCoreQuery);

        if (lstHrEmployeeResult.isSuccess() && !ObjectUtils.isEmpty(lstHrEmployeeResult.getData())) {
            List<String> deptIds = lstHrEmployeeResult.getData().stream().map(HrEmployeeDTO::getDeptId).collect(Collectors.toList());
            if (!ObjectUtils.isEmpty(deptIds)) {
                DepartCoreQuery departCoreQuery = new DepartCoreQuery();
                departCoreQuery.setTenantId(context.getTenantId());
                departCoreQuery.setIdList(deptIds);
                Result<List<DepartmentDTO>> listDeptResult = deptCoreService.queryByCondition(departCoreQuery);
                if (listDeptResult.isSuccess() && !ObjectUtils.isEmpty(listDeptResult.getData())) {
                    for (DepartmentDTO departmentDTO : listDeptResult.getData()) {
                        deptPaths.add(departmentDTO.getPath());
                    }
                }
            }
        }
        return deptPaths;
    }

    public List<String> getCurrentDeptIds(Context context) {
        EmployeeCoreQuery employeeCoreQuery = new EmployeeCoreQuery();
        employeeCoreQuery.setTenantId(context.getTenantId());
        employeeCoreQuery.setIdentityId(context.getIdentityId());
        Result<List<HrEmployeeDTO>> lstHrEmployeeResult = employeeCoreService.queryByCondition(employeeCoreQuery);

        if (lstHrEmployeeResult.isSuccess() && !ObjectUtils.isEmpty(lstHrEmployeeResult.getData())) {
            return lstHrEmployeeResult.getData().stream().map(HrEmployeeDTO::getDeptId).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取部门Core
     *
     * @param id
     * @return
     */
    public DepartmentDTO getDeptCore(String id) {
        Result<DepartmentDTO> result = deptCoreService.getById(id);
        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return null;
    }

    /**
     * 获取部门列表Core
     *
     * @param query
     * @return
     */
    public List<DepartmentDTO> getDeptCoreList(Context context, DepartCoreQuery query) {
        query.setTenantId(context.getTenantId());
        Result<List<DepartmentDTO>> result = deptCoreService.queryByCondition(query);

        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return new ArrayList<>();
    }

    public List<DepartmentDTO> queryDeptByName(Context context, String name) {
        DepartmentQueryByName request = new DepartmentQueryByName();
        request.setName(name);
        Result<List<DepartmentDTO>> result = departmentService.queryDeptByName(context, request);
        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return new ArrayList<>();
    }

    public List<DepartmentDTO> queryDeptByNameAndParentId(Context context, String name, String parentId) {
        DepartmentQueryByNameAndParentId request = new DepartmentQueryByNameAndParentId();
        request.setName(name);
        request.setParentId(parentId);
        Result<List<DepartmentDTO>> result = departmentService.queryDeptByNameAndParentId(context, request);

        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return new ArrayList<>();
    }

}
