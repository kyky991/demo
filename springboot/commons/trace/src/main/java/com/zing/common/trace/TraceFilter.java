package com.zing.common.trace;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;

import java.util.Map;

@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER})
public class TraceFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

        if (RpcContext.getContext().isConsumerSide()) {
            Map<String, String> attachments = TraceContext.getContext().getAttachments();
            attachments.forEach((key, value) -> RpcContext.getContext().setAttachment(key, value));
        } else {
            Map<String, String> attachments = RpcContext.getContext().getAttachments();
            attachments.entrySet().stream().filter(e -> e.getKey().startsWith(TraceContext.PREFIX))
                    .forEach(e -> TraceContext.getContext().setAttachment(e.getKey(), e.getValue()));
        }

        try {
            return invoker.invoke(invocation);
        } finally {
            if (RpcContext.getContext().isProviderSide()) {
                TraceContext.removeContext();
            }
        }
    }

}
