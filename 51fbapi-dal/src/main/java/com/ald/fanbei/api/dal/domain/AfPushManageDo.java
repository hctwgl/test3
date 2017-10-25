package com.ald.fanbei.api.dal.domain;

import java.util.Date;

public class AfPushManageDo {
    private Long id;

    private Byte isDelete;

    private Date gmtCreate;

    private Date gmtSend;

    private Date gmtModified;

    private String type;

    private String title;

    private String contentPush;

    private String parameter;

    private String picturePushUrl;

    private Integer notificationDeliveryNum;

    private Integer messageDeliveryNum;

    private Integer notificationOpenNum;

    private Integer messageOpenNum;

    private String userType;

    private Long groupUserId;

    private String groupUserName;

    private String status;

    private Long notifyMsgId;

    private Long messageMsgId;

    private String jumpType;

    private String jumpUrl;

    private String systemType;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSystemType() {
        return systemType;
    }

    public void setSystemType(String systemType) {
        this.systemType = systemType;
    }

    public String getPicturePushUrl() {
        return picturePushUrl;
    }

    public String getJumpType() {
        return jumpType;
    }

    public void setJumpType(String jumpType) {
        this.jumpType = jumpType;
    }

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }

    public void setPicturePushUrl(String picturePushUrl) {
        this.picturePushUrl = picturePushUrl;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public Long getNotifyMsgId() {
        return notifyMsgId;
    }

    public void setNotifyMsgId(Long notifyMsgId) {
        this.notifyMsgId = notifyMsgId;
    }

    public Long getMessageMsgId() {
        return messageMsgId;
    }

    public void setMessageMsgId(Long messageMsgId) {
        this.messageMsgId = messageMsgId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Byte isDelete) {
        this.isDelete = isDelete;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtSend() {
        return gmtSend;
    }

    public void setGmtSend(Date gmtSend) {
        this.gmtSend = gmtSend;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContentPush() {
        return contentPush;
    }

    public void setContentPush(String contentPush) {
        this.contentPush = contentPush == null ? null : contentPush.trim();
    }

    public Integer getNotificationDeliveryNum() {
        return notificationDeliveryNum;
    }

    public void setNotificationDeliveryNum(Integer notificationDeliveryNum) {
        this.notificationDeliveryNum = notificationDeliveryNum;
    }

    public Integer getMessageDeliveryNum() {
        return messageDeliveryNum;
    }

    public void setMessageDeliveryNum(Integer messageDeliveryNum) {
        this.messageDeliveryNum = messageDeliveryNum;
    }

    public Integer getNotificationOpenNum() {
        return notificationOpenNum;
    }

    public void setNotificationOpenNum(Integer notificationOpenNum) {
        this.notificationOpenNum = notificationOpenNum;
    }

    public Integer getMessageOpenNum() {
        return messageOpenNum;
    }

    public void setMessageOpenNum(Integer messageOpenNum) {
        this.messageOpenNum = messageOpenNum;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType == null ? null : userType.trim();
    }

    public Long getGroupUserId() {
        return groupUserId;
    }

    public void setGroupUserId(Long groupUserId) {
        this.groupUserId = groupUserId;
    }

    public String getGroupUserName() {
        return groupUserName;
    }

    public void setGroupUserName(String groupUserName) {
        this.groupUserName = groupUserName == null ? null : groupUserName.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }
}