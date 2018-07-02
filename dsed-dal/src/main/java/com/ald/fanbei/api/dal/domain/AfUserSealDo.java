package com.ald.fanbei.api.dal.domain;

import java.util.Date;

public class AfUserSealDo {
    private Long id;

    private String userSeal;

    private Long userId;

    private String userAccountId;

    private String userType;//用户类型 1：公司 2：个人 3：钱包用户

    private String userName;

    private String edspayUserCardId;

    private Date gmtCreate;

    private Byte isDelete;

    public String getEdspayUserCardId() {
        return edspayUserCardId;
    }

    public void setEdspayUserCardId(String edspayUserCardId) {
        this.edspayUserCardId = edspayUserCardId;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserSeal() {
        return userSeal;
    }

    public void setUserSeal(String userSeal) {
        this.userSeal = userSeal == null ? null : userSeal.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}