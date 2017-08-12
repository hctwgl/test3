package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

public class AfRecommendUserDo extends AbstractSerial {
    /**
     * 主键
     */
    private int id ;

    /**
     * 用户ID
     */
    private Long user_id;

    /**
     * 推荐人ID
     */
    private Long parentId;

    /**
     * 是否删除
     */
    private boolean is_delete = false;

    /**
     * 来源 0 正常数据 ，1自造假数据
     */
    private byte origin = (byte)0;

    /**
     * 是否己借款
     */
    private boolean is_loan = false;

    /**
     * 借款时间
     */
    private Date loan_time;

    /**
     * 创建时间
     */
    private Date gmt_create;

    /**
     * 奖励金额
     */
    private BigDecimal prize_money = BigDecimal.valueOf(0);

    /**
     * 借款人数（假数据用）
     */
    private int  loan_user_count = 0;

    /**
     * 推荐人数（假数据用）
     */
    private int recommend_count =1;

    /**
     *
     */
    private Date gmt_modified;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isIs_delete() {
        return is_delete;
    }

    public void setIs_delete(boolean is_delete) {
        this.is_delete = is_delete;
    }

    public byte getOrigin() {
        return origin;
    }

    public void setOrigin(byte origin) {
        this.origin = origin;
    }

    public boolean isIs_loan() {
        return is_loan;
    }

    public void setIs_loan(boolean is_loan) {
        this.is_loan = is_loan;
    }

    public Date getLoan_time() {
        return loan_time;
    }

    public void setLoan_time(Date loan_time) {
        this.loan_time = loan_time;
    }

    public Date getGmt_create() {
        return gmt_create;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmt_create = gmt_create;
    }

    public BigDecimal getPrize_money() {
        return prize_money;
    }

    public void setPrize_money(BigDecimal prize_money) {
        this.prize_money = prize_money;
    }

    public int getLoan_user_count() {
        return loan_user_count;
    }

    public void setLoan_user_count(int loan_user_count) {
        this.loan_user_count = loan_user_count;
    }

    public int getRecommend_count() {
        return recommend_count;
    }

    public void setRecommend_count(int recommend_count) {
        this.recommend_count = recommend_count;
    }

    public Date getGmt_modified() {
        return gmt_modified;
    }

    public void setGmt_modified(Date gmt_modified) {
        this.gmt_modified = gmt_modified;
    }
}
