package com.zing.common.trace;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.HashMap;
import java.util.Map;

public class TraceContext {

    public static final String PREFIX = "xtrace_";

    private static final TransmittableThreadLocal<TraceContext> LOCAL = new TransmittableThreadLocal<TraceContext>() {
        @Override
        protected TraceContext initialValue() {
            return new TraceContext();
        }
    };

    protected final Map<String, String> attachments = new HashMap<>();

    protected TraceContext() {
    }

    public static TraceContext getContext() {
        return LOCAL.get();
    }

    public static void removeContext() {
        LOCAL.remove();
    }

    public String getAttachment(String key) {
        return attachments.get(key);
    }

    public TraceContext setAttachment(String key, String value) {
        if (value == null) {
            attachments.remove(key);
        } else {
            attachments.put(key, value);
        }
        return this;
    }

    public TraceContext removeAttachment(String key) {
        attachments.remove(key);
        return this;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public TraceContext setAttachments(Map<String, String> attachment) {
        this.attachments.clear();
        if (attachment != null && attachment.size() > 0) {
            this.attachments.putAll(attachment);
        }
        return this;
    }

    public void clearAttachments() {
        this.attachments.clear();
    }

}
