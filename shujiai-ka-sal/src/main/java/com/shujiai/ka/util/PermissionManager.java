package com.shujiai.ka.util;

import com.shujiai.ka.constant.KAResultCodes;
import com.shujiai.ka.exceptions.KABizException;
import com.shujiai.base.context.Context;
import com.shujiai.base.result.Result;
import com.shujiai.usercenter.facade.api.IdentityService;
import com.shujiai.usercenter.facade.dto.req.query.IdentityQueryReq;
import com.shujiai.usercenter.facade.dto.rsp.IdentityDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guowei
 */
@Slf4j
@Component
public class PermissionManager {
    @Reference(check = false, group = "${dubbo.usercenter.group.name}", version = "${dubbo.usercenter.version}",
            timeout = 10000)
    private IdentityService identityService;

    private final ConcurrentHashMap<String, Context> contextMap = new ConcurrentHashMap<>();

    public Context getContextBy1818(String tenantId) {
        //获取系统用户的identityId
        if (StringUtils.isEmpty(tenantId)) {
            return null;
        }
        if (contextMap.get(tenantId) != null) {
            return contextMap.get(tenantId);
        }
        IdentityDTO user1818 = getIdentityIdByPhoneAndTenantId(tenantId, "1818");
        Context context = new Context(tenantId, user1818.getIdentityId());
        contextMap.put(tenantId, context);

        return context;
    }

    /**
     * 根据租户id、手机号获取用户identityId
     *
     * @param tenantId 租户
     * @param phone    电话
     * @return 身份信息
     */
    public IdentityDTO getIdentityIdByPhoneAndTenantId(String tenantId, String phone) {
        if (!StringUtils.hasLength(tenantId)
                || !StringUtils.hasLength(phone)) {
            throw new KABizException(KAResultCodes.KA_IDENTITY_SERVICE_EXCEPTION, "租户id和手机号不能为空");
        }
        Context context = new Context();
        context.setTenantId(tenantId);

        IdentityQueryReq req = new IdentityQueryReq();
        req.setPhone(phone);
        Result<IdentityDTO> result = identityService.queryByPhoneAndTenantId(context, req);
        if (result != null && result.isSuccess()) {
            return result.getData();
        } else {
            log.warn("IdentityServiceClient getIdentityIdByPhoneAndTenantId failed, tenantId={},phone={}", tenantId, phone);
            return new IdentityDTO();
        }
    }
}
