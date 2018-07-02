package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

public class AfRecommendUserDo extends AbstractSerial {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private int id ;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 推荐人ID
     */
    private Long parentId;

    /**
     * 是否删除
     */
    private boolean isDelete = false;

    /**
     * 来源 0 正常数据 ，1自造假数据
     */
    private byte origin = (byte)0;

    /**
     * 是否己借款
     */
    private boolean isLoan = false;

    /**
     * 借款时间
     */
    private Date loanTime;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 奖励金额
     */
    private BigDecimal prizeMoney = BigDecimal.valueOf(0);

    /**
     * 借款人数（假数据用）
     */
    private int  loanUserCount = 0;

    /**
     * 推荐人数（假数据用）
     */
    private int recommendCount =1;

    /**
     *
     */
    private Date gmtModified;

    /**
     * 颜色加重 前台展示用的
     */
    private String color;
    
    private String source;

    
    
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * 状态 前台展示用的
     */
    private String status;

    /**
     * 注册时间 前台展示用的
     */
    private String createTime;

    /**
     * userName 手机号码 前台展示用的
     */
    private String userName;
    
    /**
     * type 活动类型  0 默认，1新的
     */
    private Integer type;
    
    
    private Integer isSelfsupportRebate;
    
    private Long firstSelfsupportOrder;
    
    private Long firstBoluomeOrder;
    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public void setUser_id(Long user_id) {
        this.userId = user_id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public boolean isIs_delete() {
        return isDelete;
    }

    public void setIs_delete(boolean is_delete) {
        this.isDelete = is_delete;
    }

    public byte getOrigin() {
        return origin;
    }

    public void setOrigin(byte origin) {
        this.origin = origin;
    }

    public boolean isIs_loan() {
        return isLoan;
    }

    public void setIs_loan(boolean is_loan) {
        this.isLoan = is_loan;
    }

    public Date getLoan_time() {
        return loanTime;
    }

    public void setLoan_time(Date loan_time) {
        this.loanTime = loan_time;
    }

    public Date getGmt_create() {
        return gmtCreate;
    }

    public void setGmt_create(Date gmt_create) {
        this.gmtCreate = gmt_create;
    }

    public BigDecimal getPrize_money() {
        return prizeMoney;
    }

    public void setPrize_money(BigDecimal prize_money) {
        this.prizeMoney = prize_money;
    }

    public int getLoan_user_count() {
        return loanUserCount;
    }

    public void setLoan_user_count(int loan_user_count) {
        this.loanUserCount = loan_user_count;
    }

    public int getRecommend_count() {
        return recommendCount;
    }

    public void setRecommend_count(int recommend_count) {
        this.recommendCount = recommend_count;
    }

    public Date getGmt_modified() {
        return gmtModified;
    }

    public void setGmt_modified(Date gmt_modified) {
        this.gmtModified = gmt_modified;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsSelfsupportRebate() {
        return isSelfsupportRebate;
    }

    public void setIsSelfsupportRebate(Integer isSelfsupportRebate) {
        this.isSelfsupportRebate = isSelfsupportRebate;
    }

    public Long getFirstSelfsupportOrder() {
        return firstSelfsupportOrder;
    }

    public void setFirstSelfsupportOrder(Long firstSelfsupportOrder) {
        this.firstSelfsupportOrder = firstSelfsupportOrder;
    }

    public Long getFirstBoluomeOrder() {
        return firstBoluomeOrder;
    }

    public void setFirstBoluomeOrder(Long firstBoluomeOrder) {
        this.firstBoluomeOrder = firstBoluomeOrder;
    }
    
    
    
}
