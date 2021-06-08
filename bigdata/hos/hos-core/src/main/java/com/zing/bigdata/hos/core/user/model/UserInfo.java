package com.zing.bigdata.hos.core.user.model;

import java.util.Date;

public class UserInfo {

    private String userId;

    private String username;

    private String password;

    private String detail;

    private SystemRole systemRole;

    private Date createTime;

    public UserInfo(String username, String password, String detail, SystemRole systemRole) {
        this.userId = CoreUtils.getUUIDStr();
        this.username = username;
        this.password = CoreUtils.getMd5Password(password);
        this.detail = detail;
        this.systemRole = systemRole;
        this.createTime = new Date();
    }

    public UserInfo() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public SystemRole getSystemRole() {
        return systemRole;
    }

    public void setSystemRole(SystemRole systemRole) {
        this.systemRole = systemRole;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
