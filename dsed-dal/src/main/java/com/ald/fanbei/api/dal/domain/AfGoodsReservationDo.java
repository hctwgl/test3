package com.ald.fanbei.api.dal.domain;
 
import java.util.Date;
 
import com.ald.fanbei.api.common.AbstractSerial;
 
/**
 * @类描述：
 * 活动商品预约信息Do
 * @author chengkang 2017年6月8日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsReservationDo extends AbstractSerial {
 
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Long userId;
    private Long activityId;
    private Long goodsId;
    private Long rsvNums;
    private String rsvNo;
    private Date gmtCreate;
    private Date gmtModified;
    private String status;
    private String remark;
    
    
    public AfGoodsReservationDo() {
        super();
    }
 
    public AfGoodsReservationDo(Long userId, Long activityId, Long goodsId,
            String status) {
        super();
        this.userId = userId;
        this.activityId = activityId;
        this.goodsId = goodsId;
        this.status = status;
    }
 
 
    public AfGoodsReservationDo(Long userId, Long activityId, Long goodsId,
            Long rsvNums, String rsvNo, Date gmtCreate, Date gmtModified,
            String status, String remark) {
        super();
        this.userId = userId;
        this.activityId = activityId;
        this.goodsId = goodsId;
        this.rsvNums = rsvNums;
        this.rsvNo = rsvNo;
        this.gmtCreate = gmtCreate;
        this.gmtModified = gmtModified;
        this.status = status;
        this.remark = remark;
    }
 
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getActivityId() {
        return activityId;
    }
    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
    public Long getGoodsId() {
        return goodsId;
    }
    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
    public Long getRsvNums() {
        return rsvNums;
    }
    public void setRsvNums(Long rsvNums) {
        this.rsvNums = rsvNums;
    }
    public String getRsvNo() {
        return rsvNo;
    }
    public void setRsvNo(String rsvNo) {
        this.rsvNo = rsvNo;
    }
    public Date getGmtCreate() {
        return gmtCreate;
    }
    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }
    public Date getGmtModified() {
        return gmtModified;
    }
    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
}