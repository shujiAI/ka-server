package com.shujiai.ka.dubbo.consumer;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.shujiai.ka.constant.KAConstant;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.ka.util.PageUtil;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.ka.constant.CommonCodes;
import com.shujiai.usercenter.facade.api.HrEmployeeService;
import com.shujiai.usercenter.facade.dto.req.BatchEditEmployeeDeptReq;
import com.shujiai.usercenter.facade.dto.req.DepartmentSaveReq;
import com.shujiai.usercenter.facade.dto.req.hr.BatchUpdateHrEmployeeReq;
import com.shujiai.usercenter.facade.dto.req.hr.DeputyEmployeeSaveReq;
import com.shujiai.usercenter.facade.dto.req.hr.HrEmploySaveReq;
import com.shujiai.usercenter.facade.dto.req.hr.rsp.EmployeeDetailDTO;
import com.shujiai.usercenter.facade.dto.req.query.*;
import com.shujiai.usercenter.facade.dto.rsp.HrEmployeeDTO;
import com.shujiai.usercenter.facade.dto.rsp.HrEmployeeSimpleDTO;
import com.shujiai.usercenter.facade.dto.rsp.IdentityDTO;
import com.shujiai.usercenter.facade.dto.rsp.TeamLeaderDTO;
import com.shujiai.usercenter.facade.enums.PositionTypeEnum;
import com.shujiai.usercenter.facade.page.BasePageParam;
import com.shujiai.usercenter.facade.page.Pager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lianjiang.yulj
 * @date 2022/01/05
 * @time 12:21 上午
 * @description
 */

@Slf4j
@Component
public class HrEmployeeServiceClient {

    @Reference(check = false, group = "${dubbo.usercenter.group.name}", version = "${dubbo.usercenter.version}", timeout = 10000)
    private HrEmployeeService hrEmployeeService;

    /**
     * 获取主职信息，不含删除
     *
     * @param context
     * @param identityId
     * @return
     */
    public HrEmployeeDTO getUserInfo(Context context, String identityId) {
        QueryByIdReq query = new QueryByIdReq();
        query.setId(identityId);
        query.setTenantId(context.getTenantId());
        Result<HrEmployeeDTO> result = hrEmployeeService.getChiefEmployeeByIdentityId(context, query);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public List<HrEmployeeDTO> listUsers(Context context, List<String> identityIds) {
        ByIdsReq query = new ByIdsReq();
        query.setIds(identityIds);
        Result<List<HrEmployeeDTO>> result = hrEmployeeService.getChiefEmployeesByIdentityIds(context, query);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Pager<HrEmployeeDTO> getEmployeeByCurrentDeptId(Context context, DepartmentUserPageQuery request) {
        Result<Pager<HrEmployeeDTO>> result = hrEmployeeService.getEmployeeByCurrentDeptId(context, request);

        if (!result.getSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "", result.getArgs());
            throw new KABizException(resultCode);
        }

        return result.getData();
    }

    /**
     * 查询同部门员工id（包含子部门）
     *
     * @param context
     * @param request
     * @return
     */
    public List<String> getSameDeptUserIds(Context context, DepartmentQueryReq request) {
        List<HrEmployeeDTO> list = this.getSameDeptUsers(context, request);
        List<String> idList = list.stream().map(dto -> dto.getIdentityId()).collect(Collectors.toList());

        return idList;
    }

    /**
     * 查询同部门员工（包含子部门）
     *
     * @param context
     * @param request
     * @return
     */
    public List<HrEmployeeDTO> getSameDeptUsers(Context context, DepartmentQueryReq request) {
        //封装分页参数
        Integer pageNo = 1;
        Integer pageSize = KAConstant.MAX_PAGE_SIZE;
        Result<Pager<HrEmployeeDTO>> result = new Result<>();
        //不分页
        request.setPageNo(1);
        request.setPageSize(pageSize);

        Pager<HrEmployeeDTO> data = new Pager<>();
        List<HrEmployeeDTO> list = new ArrayList<>();
        do {
            result = hrEmployeeService.getEmployeeByDeptId(context, request);
            data = result.getData();
            list.addAll(data.getRecordList());

            request.setPageNo(pageNo++);
            data.setRecordList(list);
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, result.getData().getSize(), result.getData().getTotal()));

        return list;
    }

    /**
     * 查询同部门员工id（包含子部门）-set集合
     *
     * @param context
     * @param request
     * @return
     */
    public Set<String> getSameDeptUserIdsSet(Context context, DepartmentQueryReq request) {
        List<HrEmployeeDTO> list = this.getSameDeptUsers(context, request);
        Set<String> idSet = list.stream().map(dto -> dto.getIdentityId()).collect(Collectors.toSet());

        return idSet;
    }

    public List<HrEmployeeDTO> getChiefEmployeesByIdentityIds(Context context, List<String> ids) {
        ByIdsReq request = new ByIdsReq();
        request.setIds(ids);
        Result<List<HrEmployeeDTO>> result = hrEmployeeService.getChiefEmployeesByIdentityIds(context, request);

        if (!result.getSuccess()) {
            ResultCode resultCode = new ResultCode(result.getCode(), result.getMessage(), "", result.getArgs());
            throw new KABizException(resultCode);
        }

        return result.getData();
    }

    /**
     * 获取登录人的所有D
     *
     * @param context
     * @return
     */
    public List<String> getTlChildIdentityIds(Context context) {
        List<String> identityIds = new ArrayList<>();

        IdentityIdReq identityIdReq = new IdentityIdReq();
        identityIdReq.setIdentityId(context.getIdentityId());
        Result<List<IdentityDTO>> result = hrEmployeeService.getChildIdentityIdsBySupervisorId(context, identityIdReq);
        if (result.isSuccess() && ObjectUtils.isNotEmpty(result.getData())) {
            identityIds = result.getData().stream().map(IdentityDTO::getIdentityId).collect(Collectors.toList());
        }

        return identityIds;
    }

    /**
     * 获取下级
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getMyChildrenEmployee(Context context) {
        HrEmployeeDTO chiefInfo = this.getAnyChiefByIdentityId(context);
        if (chiefInfo == null || chiefInfo.getId() == null) {
            return new ArrayList<>();
        }

        String id = chiefInfo.getId();

        return this.getMyChildrenEmployeeById(context, id);
    }

    /**
     * 按identityId获取任职主职信息-包括离职
     *
     * @param context
     * @return
     */
    public HrEmployeeDTO getAnyChiefByIdentityId(Context context) {
        IdentityIdReq request = new IdentityIdReq();
        request.setIdentityId(context.getIdentityId());
        Result<HrEmployeeDTO> result = hrEmployeeService.getAnyChiefByIdentityId(context, request);

        return result.getData();
    }

    /**
     * 按identityId获取任职主职信息-包括离职
     *
     * @param identityId
     * @return
     */
    public HrEmployeeDTO getAnyChiefByIdentityId(Context context, String identityId) {
        if (StringUtils.isEmpty(identityId)) {
            return null;
        }
        IdentityIdReq request = new IdentityIdReq();
        request.setIdentityId(identityId);
        Result<HrEmployeeDTO> result = hrEmployeeService.getAnyChiefByIdentityId(context, request);

        return result.getData();
    }

    /**
     * 获取下级
     *
     * @param context
     * @param id
     * @return
     */
    public List<HrEmployeeDTO> getMyChildrenEmployeeById(Context context, String id) {
        //不分页
        Integer pageNo = 1;
        Integer pageSize = KAConstant.MAX_PAGE_SIZE;

        QueryByIdReq request = new QueryByIdReq();
        request.setId(id);
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        Result<Pager<HrEmployeeDTO>> result = null;

        List<HrEmployeeDTO> list = new ArrayList<>();
        do {
            result = hrEmployeeService.getMyChildrenEmployee(context, request);
            if (result.getData() != null) {
                list.addAll(result.getData().getRecordList());
                pageNo++;
                request.setPageNo(pageNo);
            } else {
                break;
            }
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, result.getData().getRecordList().size(), result.getData().getTotal()));

        return list;
    }

    /**
     * 获取公司所有人员
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getRootEmployeeInTenant(Context context) {

        //不分页
        Integer pageNo = 1;
        Integer pageSize = KAConstant.MAX_PAGE_SIZE;

        BasePageParam request = new BasePageParam();
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);
        Result<Pager<HrEmployeeDTO>> result = null;

        List<HrEmployeeDTO> list = new ArrayList<>();
        do {
            result = hrEmployeeService.getRootEmployeeInTenant(context, request);
            if (result.getData() != null) {
                list.addAll(result.getData().getRecordList());
                pageNo++;
                request.setPageNo(pageNo);
            } else {
                break;
            }
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, result.getData().getRecordList().size(), result.getData().getTotal()));

        return list;
    }

    /**
     * 按identityId获取任职主职信息
     *
     * @param context
     * @return
     */
    public HrEmployeeDTO getChiefEmployeeByIdentityId(Context context) {
        return getChiefEmployeeByIdentityId(context, context.getIdentityId());
    }

    public HrEmployeeDTO getChiefEmployeeByIdentityId(Context context, String identityId) {
        return getChiefEmployeeByIdentityId(context, identityId, null);
    }

    public HrEmployeeDTO getChiefEmployeeByIdentityId(Context context, String identityId, Boolean needDeleted) {
        QueryByIdReq request = new QueryByIdReq();
        request.setId(identityId);
        // true表示需要删除,false表示不需要删除
        if (needDeleted != null) {
            if (Boolean.TRUE.equals(needDeleted)) {
                request.setDelete(false);
            } else {
                request.setDelete(true);
            }
        }
        Result<HrEmployeeDTO> result = hrEmployeeService.getChiefEmployeeByIdentityId(context, request);
        if (!Boolean.TRUE.equals(needDeleted)) {
            if (result != null && result.getData() != null && result.getData().getIsDeleted() == 0) {
                return result.getData();
            } else {
                return null;
            }
        }
        return result.getData();
    }

    /**
     * 查询同部门主职员工id（包含子部门）-set集合
     *
     * @param context
     * @return
     */
    public Set<String> getSameDeptChiefUserIds(Context context, List<String> deptIds) {
        List<HrEmployeeDTO> list = this.getSameDeptChiefUsers(context, deptIds);
        Set<String> idSet = list.stream().map(dto -> dto.getIdentityId()).collect(Collectors.toSet());

        return idSet;
    }

    /**
     * 查询同部门主职员工id（包含子部门）-set集合
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getSameDeptChiefUsers(Context context, List<String> deptIds) {
        DeptHrEmployeeQueryReq request = new DeptHrEmployeeQueryReq();
        request.setDeptIds(deptIds);
        request.setPositionType(PositionTypeEnum.PRINCIPAL.getDesc());
        request.setLeave(false);
        List<HrEmployeeDTO> list = this.getEmployeeList(context, request);

        return list;
    }

    /**
     * getEmployeeList
     *
     * @param context 系统上下文
     * @param request {@link DeptHrEmployeeQueryReq}
     * @return {@link  HrEmployeeDTO}
     */
    public List<HrEmployeeDTO> getEmployeeList(Context context, DeptHrEmployeeQueryReq request) {
        //封装分页参数
        Integer pageNo = 1;
        Integer pageSize = KAConstant.MAX_PAGE_SIZE;
        //不分页
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);

        Result<Pager<HrEmployeeDTO>> result;
        Pager<HrEmployeeDTO> data;
        List<HrEmployeeDTO> list = new ArrayList<>();
        do {
            result = hrEmployeeService.getEmployeeList(context, request);
            data = result.getData();
            list.addAll(data.getRecordList());

            request.setPageNo(pageNo++);
            data.setRecordList(list);
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, result.getData().getSize(), result.getData().getTotal()));

        return list;
    }

    /**
     * 查询同部门主副职员工id（包含子部门）-set集合
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getSameDeptAllUsers(Context context, List<String> deptIds) {
        DeptHrEmployeeQueryReq request = new DeptHrEmployeeQueryReq();
        request.setDeptIds(deptIds);
        request.setLeave(false);
        List<HrEmployeeDTO> list = this.getEmployeeList(context, request);

        return list;
    }

    /**
     * 获取所有主职员工
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getAllChiefPositionEmployee(Context context) {
        Result<List<HrEmployeeDTO>> result = hrEmployeeService.getAllChiefPositionEmployee(context);

        return result.getData();
    }

    /**
     * 获取用户主职信息的领导id
     */
    public List<String> getChiefLeaderLine(List<Map<String, String>> chiefLeaderLineDto) {
        if (chiefLeaderLineDto == null || chiefLeaderLineDto.size() == 0) {
            return new ArrayList<>();
        }
        return chiefLeaderLineDto.stream().map(dto -> (String) dto.get("id")).collect(Collectors.toList());
    }


    private Map<String, String> genSimpleUser(TeamLeaderDTO teamLeaderDTO) {
        Map<String, String> dto = new HashMap<>(8);

        String userName = teamLeaderDTO.getUserName();
        String nickName = teamLeaderDTO.getNickName();
        dto.put("id", teamLeaderDTO.getIdentityId());
        dto.put("userName", userName);
        dto.put("nickName", nickName);
        dto.put("avatar", teamLeaderDTO.getAvatar());
        String label = userName + (StringUtils.isEmpty(nickName) ? "" : (" (" + nickName + ")"));
        dto.put("label", label);

        return dto;
    }

    /**
     * 获取某个用户所有任职信息
     *
     * @param context
     * @return
     */
    public List<HrEmployeeDTO> getPostByIdentityId(Context context) {
        return this.getPostByIdentityId(context, context.getIdentityId());
    }

    public List<HrEmployeeDTO> getPostByIdentityId(Context context, String identityId) {
        return this.getPostByIdentityId(context, identityId, null);
    }

    public List<HrEmployeeDTO> getPostByIdentityId(Context context, String identityId, Boolean needDeleted) {
        QueryByIdReq request = new QueryByIdReq();
        request.setId(identityId);
        // true表示需要删除,false表示不需要删除
        request.setDelete(Boolean.TRUE.equals(needDeleted));
        Result<List<HrEmployeeDTO>> result = hrEmployeeService.getPostByIdentityId(context, request);
        List<HrEmployeeDTO> list = result.getData();
        return list == null ? Lists.newArrayList() : list.stream().filter(e -> {
            if (Boolean.TRUE.equals(needDeleted)) {
                return e.getIsDeleted() > 0;
            } else {
                return e.getIsDeleted() == 0;
            }
        }).collect(Collectors.toList());
    }

    /**
     * 获取某个用户任职信息详情
     *
     * @param context
     * @return
     */
    public EmployeeDetailDTO getEmployeeDetailByIdentityId(Context context, String identityId) {
        IdentityIdReq request = new IdentityIdReq();
        request.setIdentityId(identityId);
        Result<EmployeeDetailDTO> result = hrEmployeeService.getEmployeeDetailByIdentityId(context, request);
        EmployeeDetailDTO dto = result.getData();

        return dto;
    }

    /**
     * 删除副职
     *
     * @param context     系统上下文
     * @param employeeIds 任职ID
     */
    public void batchDeleteHrEmployeeById(Context context, List<String> employeeIds) {
        if (CollectionUtils.isNotEmpty(employeeIds)) {
            ByIdsReq request = new ByIdsReq();
            request.setIds(employeeIds);
            hrEmployeeService.batchDeleteHrEmployeeById(context, request);
        }
    }

    /**
     * 添加副职
     *
     * @param context 系统上下文
     * @param request {@link DeputyEmployeeSaveReq}
     */
    public void createDeputyPosition(Context context, DeputyEmployeeSaveReq request) {
        hrEmployeeService.createDeputyPosition(context, request);
    }

    public List<HrEmployeeDTO> getEmployeeByIds(Context context, List<String> employeeIds) {
        ByIdsReq request = new ByIdsReq();
        request.setIds(employeeIds);
        Result<List<HrEmployeeDTO>> result = hrEmployeeService.getEmployeeByIds(context, request);
        if (result != null && result.getData() != null) {
            return result.getData();
        }
        return null;
    }

    public void createHrEmployee(Context context, HrEmploySaveReq request) {
        Result<Void> result = hrEmployeeService.createHrEmployee(context, request);
        if (result != null) {
            if (!result.getSuccess()) {
                throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                        CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", result.getMessage())));
            }
            return;
        }
        throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", "任职信息创建失败")));
    }

    public void updateHrEmployee(Context context, List<HrEmploySaveReq> hrEmploySaveReqs) {
        BatchUpdateHrEmployeeReq request = new BatchUpdateHrEmployeeReq();
        request.setEmployees(hrEmploySaveReqs);
        Result<Void> result = hrEmployeeService.batchUpdateHrEmployee(context, request);
        if (result != null) {
            if (!result.getSuccess()) {
                throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                        CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", result.getMessage())));
            }
            return;
        }
        throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", "任职信息更新失败")));
    }


    public void batchUpdateDept(Context context, String sourceEmployeeId, DepartmentSaveReq saveReq) {
        if (StringUtils.isEmpty(sourceEmployeeId) || saveReq == null) {
            throw new BizException(CommonCodes.INVALID_PARAM);
        }
        BatchEditEmployeeDeptReq request = new BatchEditEmployeeDeptReq();
        request.setIds(Lists.newArrayList(sourceEmployeeId));
        request.setDepartmentSaveReq(saveReq);
        Result<Void> result = hrEmployeeService.batchUpdateDept(context, request);
        if (result != null) {
            if (!result.getSuccess()) {
                throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                        CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", result.getMessage())));
            }
            return;
        }
        throw new BizException(new ResultCode(CommonCodes.BIZ_ERROR.getCode(),
                CommonCodes.BIZ_ERROR.getMessage(), MapUtil.of("message", "任职信息更新失败")));
    }

    /**
     * getOnlyEmployeeSimpleList
     * 只返回最基础的简单字段信息
     *
     * @param context 系统上下文
     * @param request {@link DeptHrEmployeeQueryReq}
     * @return {@link  HrEmployeeDTO}
     */
    public List<HrEmployeeSimpleDTO> getOnlyEmployeeSimpleList(Context context, DeptHrEmployeeQueryReq request) {
        //封装分页参数
        Integer pageNo = 1;
        Integer pageSize = KAConstant.MAX_PAGE_SIZE;
        //不分页
        request.setPageNo(pageNo);
        request.setPageSize(pageSize);

        Result<Pager<HrEmployeeSimpleDTO>> result;
        Pager<HrEmployeeSimpleDTO> data;
        List<HrEmployeeSimpleDTO> list = new ArrayList<>();
        do {
            result = hrEmployeeService.getOnlyEmployeeSimpleList(context, request);
            data = result.getData();
            list.addAll(data.getRecordList());

            request.setPageNo(pageNo++);
            data.setRecordList(list);
        } while (PageUtil.checkHasMoreRecord(pageNo, pageSize, result.getData().getSize(), result.getData().getTotal()));

        return list;
    }

}
