package com.zing.test.netty.udp;

import java.net.InetSocketAddress;

public class LogEvent {

    public static final byte SEPARATOR = ':';

    private InetSocketAddress source;
    private long received;

    private String logfile;
    private String msg;

    public LogEvent(String logfile, String msg) {
        this(null, -1, logfile, msg);
    }

    public LogEvent(InetSocketAddress source, long received, String logfile, String msg) {
        this.source = source;
        this.received = received;
        this.logfile = logfile;
        this.msg = msg;
    }

    public InetSocketAddress getSource() {
        return source;
    }

    public long getReceived() {
        return received;
    }

    public String getLogfile() {
        return logfile;
    }

    public String getMsg() {
        return msg;
    }

}
