package com.ald.fanbei.api.dal.domain;

import java.util.Date;

public class AfRecommendShareDo {
    private String id = java.util.UUID.randomUUID().toString().replace("-","");
    private long user_id;
    private String recommend_code;
    private Date gmt_create;

    /**
     * 0 微信朋友圈，1 微信好友，2 qq空间 ，3二维码
     */
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUser_id() {
        return user_id;
    }

    public void setUser_id(long user_id) {
        this.user_id = user_id;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getRecommend_code() {
        return recommend_code;
    }

    public void setRecommend_code(String recommend_code) {
        this.recommend_code = recommend_code;
    }
}
