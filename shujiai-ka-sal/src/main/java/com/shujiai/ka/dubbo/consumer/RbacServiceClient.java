package com.shujiai.ka.dubbo.consumer;

import cn.hutool.core.lang.Assert;
import com.shujiai.ka.constant.KAConstant;
import com.shujiai.base.context.Context;
import com.shujiai.base.exception.BizException;
import com.shujiai.base.result.Result;
import com.shujiai.base.result.ResultCode;
import com.shujiai.rbac.facade.api.*;
import com.shujiai.rbac.facade.api.field.PermissionFieldConfigReadService;
import com.shujiai.rbac.facade.constants.RbacResultCodes;
import com.shujiai.rbac.facade.dto.*;
import com.shujiai.rbac.facade.dto.base.PageResult;
import com.shujiai.rbac.facade.dto.field.PermissionFieldConfigVO;
import com.shujiai.rbac.facade.dto.request.departperm.CreateDepartPermRequest;
import com.shujiai.rbac.facade.dto.request.departperm.DeleteDepartPermRequest;
import com.shujiai.rbac.facade.dto.request.field.LoadPermFieldConfigRequest;
import com.shujiai.rbac.facade.dto.request.permission.*;
import com.shujiai.rbac.facade.dto.request.role.*;
import com.shujiai.rbac.facade.dto.request.userperm.*;
import com.shujiai.rbac.facade.dto.request.userrole.*;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xiaokang
 * @date 2023/11/5 15:36
 */
@Component
public class RbacServiceClient {
    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private UserPermissionService userPermissionService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private UserRoleService userRoleService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private RoleService roleService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private PermissionService permissionService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private RolePermService rolePermService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private DepartPermService departPermService;

    @Reference(check = false, group = "${dubbo.rbac.group.name}", version = "${dubbo.rbac.version}", timeout = 10000, retries = 0)
    private PermissionFieldConfigReadService permissionFieldConfigReadService;


    /**
     * 批量权限校验
     *
     * @param context
     * @param request
     * @return
     */
    public List<CheckPermDTO> checkPerm(Context context, CheckPermResActionBatchRequest request) {
        Result<List<CheckPermDTO>> result = userPermissionService.checkPerm(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 单个权限校验
     *
     * @param context
     * @param request
     * @return
     */
    public CheckPermDTO checkPerm(Context context, CheckPermResActionRequest request) {
        Result<CheckPermDTO> result = userPermissionService.checkPerm(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 批量根据 PermissionCode来验证权限，验证用户是否具备 permCode 的权限.
     * 用户至少有1条有效(未过期)的路径具备该权限就返回True，没有任何路径具备该权限就返回False.
     *
     * @return 验权结果列表
     */
    public List<CheckPermDTO> checkPerm(Context context, CheckPermCodeBatchRequest request) {
        Result<List<CheckPermDTO>> result = userPermissionService.checkPerm(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 校验单个权限
     *
     * @param context
     * @param request
     * @return
     */
    public CheckPermDTO checkPerm(Context context, CheckPermCodeRequest request) {
        Result<CheckPermDTO> result = userPermissionService.checkPerm(context, request);
        if (!result.isSuccess()) {
            // 资源不存在默认不拦截
            if (RbacResultCodes.RESOURCE_NOT_EXIST.getCode().equals(result.getCode())) {
                CheckPermDTO checkPermDTO = new CheckPermDTO();
                checkPermDTO.setbValid(true);
                return checkPermDTO;
            }
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public CheckRoleDTO checkRole(Context context, CheckRoleCodeRequest request) {
        Result<CheckRoleDTO> result = userRoleService.checkRole(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public List<CheckRoleDTO> checkRole(Context context, CheckRoleCodeBatchRequest request) {
        Result<List<CheckRoleDTO>> result = userRoleService.checkRole(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public List<CheckRoleDTO> checkRole(Context context, String... ruleCodes){
        Assert.notEmpty(ruleCodes, "角色code不能为空");
        CheckRoleCodeBatchRequest request = new CheckRoleCodeBatchRequest();
        request.setRoleCodeList(Arrays.stream(ruleCodes).collect(Collectors.toList()));
        return checkRole(context, request);
    }

    public boolean matchOnlyOneRule(Context context, String... ruleCodes){
        Assert.notEmpty(ruleCodes, "角色code不能为空");
        CheckRoleCodeBatchRequest request = new CheckRoleCodeBatchRequest();
        request.setRoleCodeList(Arrays.stream(ruleCodes).collect(Collectors.toList()));
        List<CheckRoleDTO> checkRoleDTOS = checkRole(context, request);
        return checkRoleDTOS.stream().anyMatch(i -> Boolean.TRUE.equals(i.getbValid()));
    }

    public Boolean addRolePerm(Context context, AddRolePermRequest request) {
        Result<Boolean> result = roleService.addRolePerm(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }


    public Boolean deleteRolePerm(Context context, DeleteRolePermRequest request) {
        Result<Boolean> result = roleService.deleteRolePerm(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Boolean deleteRoleAdmin(Context context, UpdateRoleAdminRequest request) {
        Result<Boolean> result = roleService.deleteRoleAdmin(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public PageResult<UserRoleDTO> getUserRole(Context context, QueryUserRoleByUserIdRequest request) {
        Result<PageResult<UserRoleDTO>> result = userRoleService.queryByUserId(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public PageResult<UserRoleDTO> getUserByRole(Context context, QueryUserRoleByRoleCodeRequest request) {
        Result<PageResult<UserRoleDTO>> result = userRoleService.queryByRoleCode(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public List<RoleDTO> findAppRole(Context context, QueryRoleRequest request) {
        Result<List<RoleDTO>> result = roleService.findAppRole(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public String createRole(Context context, AddRoleRequest addRoleRequest) {
        Result<String> result = roleService.create(context, addRoleRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Boolean updateRole(Context context, UpdateRoleRequest updateRoleRequest) {
        Result<Boolean> result = roleService.update(context, updateRoleRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Boolean deleteRole(Context context, DeleteRoleRequest delRoleRequest) {
        Result<Boolean> result = roleService.delete(context, delRoleRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public String createPermission(Context context, CreatePermRequest createPermRequest) {
        Result<String> result = permissionService.create(context, createPermRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Boolean updatePermission(Context context, UpdatePermRequest updatePermRequest) {
        Result<Boolean> result = permissionService.update(context, updatePermRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Boolean deletePermission(Context context, DeletePermRequest deletePermRequest) {
        Result<Boolean> result = permissionService.delete(context, deletePermRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 判断权限编码是否在RBAC存在
     * @param context
     * @param permCode
     * @return
     */
    public boolean isExist(Context context, String permCode){
        GetPermRequest request = new GetPermRequest();
        request.setPermCode(permCode);
        Result<PermissionDTO> result = permissionService.get(context, request);
        if (!result.isSuccess()) {
            //权限不存在
            if (RbacResultCodes.RESOURCE_NOT_EXIST.getCode().equals(result.getCode())) {
                return false;
            }
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        if (ObjectUtils.isEmpty(result.getData())) {
            return false;
        }
        return true;
    }

    /**
     * 查询权限项
     * @param context
     * @param request
     * @return
     */
    public PageResult<PermissionDTO> query(Context context, QueryPermRequest request){
        Result<PageResult<PermissionDTO>> result = permissionService.query(context,request);
        if(!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }
    /**
     * 给用户授权
     */
    public String authorizeUserPermission(Context context, AddPermToUserRequest request) {
        Result<String> result = userPermissionService.authorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }


    /**
     * 给用户批量授权
     *
     * @param context
     * @param request
     * @return
     */
    public List<String> addPermListToUser(Context context, AddPermListToUserRequest request) {
        Result<List<String>> result = userPermissionService.authorizeBatch(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 批量给用户授权
     */
    public List<String> addUserListToPerm(Context context, AddUserListToPermRequest request) {
        Result<List<String>> result = userPermissionService.authorizeBatch(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 给用户批量添加角色
     */
    public List<String> addRoleListToUser(Context context, AddRoleListToUserRequest request) {
        Result<List<String>> result = userRoleService.authorizeBatch(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }


    /**
     * 给角色批量添加用户
     */
    public List<String> addUserListToRole(Context context, AddUserListToRoleRequest request) {
        Result<List<String>> result = userRoleService.authorizeBatch(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 删除用户角色
     */
    public Boolean deleteUserRole(Context context, DeleteUserRoleRequest request) {
        Result<Boolean> result = userRoleService.unauthorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 删除用户权限
     */
    public Boolean deleteUserPerm(Context context, DeleteUserPermRequest request) {
        Result<Boolean> result = userPermissionService.unauthorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 查询用户所有权限
     *
     * @param context
     * @return
     */
    public FullRolePermDTO findFullPermission(Context context) {
        Result<FullRolePermDTO> result = rolePermService.findFullPermission(context);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 清除应用权限数据
     *
     * @param context
     * @param appId
     * @return
     */
    public boolean deleteByApp(Context context, String appId) {
        DeleteByAppRequest deleteByAppRequest = new DeleteByAppRequest();
        deleteByAppRequest.setAppKey(appId);
        Result<Boolean> result = rolePermService.deleteByApp(context, deleteByAppRequest);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 获取授权的用户列表根据权限编码
     * @param context
     * @param permCode
     * @return
     */
    public List<UserPermissionDTO> queryUsersByPermCode(Context context, String permCode){
        Result<List<UserPermissionDTO>> result= userPermissionService.queryByPermCode(context,permCode);
        if(! result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 获取授权的部门列表根据权限编码
     * @param context
     * @param permCode
     * @return
     */
    public List<DepartPermDTO> queryDeptsByPermCode(Context context, String permCode){
        Result<List<DepartPermDTO>> result= departPermService.queryByPermCode(context,permCode);
        if(! result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }
    /**
     * 获取授权的角色列表根据权限编码
     * @param context
     * @param permCode
     * @return
     */
    public List<RolePermissionDTO> queryRolesByPermCode(Context context, String permCode){
        Result<List<RolePermissionDTO>> result= rolePermService.queryByPermCode(context,permCode);
        if(! result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 批量给权限添加用户
     *
     * @return 授权id列表
     */
    public List<String> authorize(Context context, AddUserListToPermRequest request) {
        Result<List<String>> result = userPermissionService.authorizeBatch(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 授权权限给部门
     * @param context
     * @param request
     * @return
     */
    public Boolean authorize(Context context, CreateDepartPermRequest request) {
        Result<Boolean> result = departPermService.authorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }
    /**
     * 回收 部门授权ID 或者 部门Code + PermCode 部门权限信息
     */
    public Boolean unauthorize(Context context, DeleteDepartPermRequest request){
        Result<Boolean> result = departPermService.unauthorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 根据授权ID  或者 身份id+permCode 来回收授权
     * @param context
     * @param request
     * @return
     */
    public Boolean unauthorize(Context context, DeleteUserPermRequest request){
        Result<Boolean> result = userPermissionService.unauthorize(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 查询哪些角色实际拥有此权限
     * @param context context
     * @param permCode 权限code
     * @return 实际拥有此权限的角色code列表
     */
    public List<String> findRoleCodesByPerm(Context context, String permCode) {
        Result<List<String>> result = permissionService.findRoleCodesByPerm(context, permCode);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public Map<String, List<String>> findRoleCodesByPermCodes(Context context, List<String> permCodes) {
        ListByPermCodesRequest request = new ListByPermCodesRequest();
        request.setCodes(permCodes);
        Result<Map<String, List<String>>> result = permissionService.findRoleCodesByPermCodes(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    public List<String> listPermByCodes(Context context, List<String> permCodes) {
        ListByPermCodesRequest request = new ListByPermCodesRequest();
        request.setCodes(permCodes);
        Result<List<String>> result = permissionService.listPermByCodes(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 添加角色管理员
     * @param context context
     * @param request request
     */
    public void addRoleAdmin(Context context, UpdateRoleAdminRequest request) {
        Result<Boolean> result = roleService.addRoleAdmin(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 移除角色管理员
     * @param context context
     * @param request request
     */
    public void removeRoleAdmin(Context context, UpdateRoleAdminRequest request) {
        Result<Boolean> result = roleService.deleteRoleAdmin(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
    }

    /**
     * 查询字段权限
     *
     * @param context
     * @param permissionKeys
     * @param identityIdList
     * @return
     */
    public Map<String, List<String>> queryPermFieldListByIdentityIdList(Context context, List<String> permissionKeys, List<String> identityIdList) {
        if (StringUtils.isBlank(context.getIdentityId())) {
            return Collections.emptyMap();
        }
        Result<Map<String, List<String>>> result = permissionFieldConfigReadService.queryPermFieldListByIdentityIdList(context, permissionKeys, identityIdList);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 查询当前用户的权限字段
     *
     * @param context
     * @param needAll
     * @return
     */
    public List<PermissionFieldConfigVO> queryPermFieldConfig(Context context, Boolean needAll) {
        Result<List<PermissionFieldConfigVO>> result = permissionFieldConfigReadService.loadPermFieldConfig(context, needAll);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 查询当前用户看他人字段的权限
     *
     * @param context
     * @param request
     * @return
     */
    public List<PermissionFieldConfigVO> queryPermFieldConfig(Context context, LoadPermFieldConfigRequest request) {
        Result<List<PermissionFieldConfigVO>> result = permissionFieldConfigReadService.loadPermFieldConfig(context, request);
        if (!result.isSuccess()) {
            throw new BizException(new ResultCode(result.getCode(), result.getMessage(), result.getArgs()));
        }
        return result.getData();
    }

    /**
     * 按角色查找用户-按个角色
     */
    public List<UserRoleDTO> getAllUsersOfRole(Context context, String roleCode, List<String> userIds, boolean returnUserDept) {
        QueryUserRoleByRoleCodeRequest request = new QueryUserRoleByRoleCodeRequest();
        request.setRoleCode(roleCode);
        request.setUserIds(userIds);
        request.setReturnUserDept(returnUserDept);

        Long pageNo = 1L;
        request.setPageNo(pageNo);
        request.setPageSize(KAConstant.MAX_PAGE_SIZE);

        Result<PageResult<UserRoleDTO>> result = userRoleService.queryByRoleCode(context, request);

        List<UserRoleDTO> list = new ArrayList<>();
        while(result != null && result.getData() != null && result.getData().getRecords().size() > 0){
            list.addAll(result.getData().getRecords());

            pageNo++;
            request.setPageNo(pageNo);
            result = userRoleService.queryByRoleCode(context, request);
        }
        return list;
    }

    /**
     * 按角色查找用户id
     */
    public List<String> getAllUserIdsOfRole(Context context, String roleCode, List<String> userIds) {
        final List<UserRoleDTO> dtoList = this.getAllUsersOfRole(context, roleCode, userIds, false);
        if(CollectionUtils.isEmpty(dtoList)){
            return null;
        }
        return dtoList.stream().map(UserRoleDTO::getIdentityId).collect(Collectors.toList());
    }
}
