package com.zing.bigdata.hos.core.auth.model;

import com.zing.bigdata.hos.core.user.model.CoreUtils;

import java.util.Date;

public class TokenInfo {

    private String token;

    /**
     * 单位：天
     */
    private int expireTime;

    private Date refreshTime;

    private Date createTime;

    private boolean active;

    private String creator;

    public TokenInfo() {

    }

    public TokenInfo(String creator) {
        this.token = CoreUtils.getUUIDStr();
        this.expireTime = 7;
        Date date = new Date();
        this.refreshTime = date;
        this.createTime = date;
        this.active = true;
        this.creator = creator;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public Date getRefreshTime() {
        return refreshTime;
    }

    public void setRefreshTime(Date refreshTime) {
        this.refreshTime = refreshTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
}
