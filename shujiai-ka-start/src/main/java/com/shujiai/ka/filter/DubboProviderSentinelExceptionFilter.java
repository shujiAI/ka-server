package com.shujiai.ka.filter;

import com.shujiai.base.result.ResultCodes;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * 达博提供者哨兵异常过滤器
 *
 * @author zhangzeheng
 * @date 2022/06/14
 */
@Activate(group = CommonConstants.PROVIDER)
public class DubboProviderSentinelExceptionFilter extends ListenableFilter {
    public static final String SENTINEL_BLOCK_EXCEPTION_MSG_PREFIX = "SentinelBlockException: ";
    public static final String ESB_BLOCK_EXCEPTION_MSG_PREFIX = "bpmn dubbo provider sentinel block,methodName:";

    public DubboProviderSentinelExceptionFilter() {
        super.listener = new ExceptionListener();
    }

    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return invoker.invoke(invocation);
    }

    static class ExceptionListener implements Listener {

        public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {

            if (appResponse != null && appResponse.hasException()) {

                try {

                    Throwable exception = appResponse.getException();
                    if (ObjectUtils.isNotEmpty(exception.getMessage())
                        && exception.getMessage().contains(SENTINEL_BLOCK_EXCEPTION_MSG_PREFIX)) {
                        com.shujiai.base.result.Result result =
                            new com.shujiai.base.result.Result<>(ResultCodes.FLOW_CONTROL_ERROR.getCode(),
                                ESB_BLOCK_EXCEPTION_MSG_PREFIX + invocation.getMethodName(), null, false);
                        appResponse.setValue(result);
                        appResponse.setException(null);

                        return;
                    }

                } catch (Throwable e) {
                    return;
                }
            }
        }

        @Override
        public void onError(Throwable t, Invoker<?> invoker, Invocation invocation) {

        }
    }
}
