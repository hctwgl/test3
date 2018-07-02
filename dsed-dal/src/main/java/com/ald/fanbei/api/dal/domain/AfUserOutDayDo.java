package com.ald.fanbei.api.dal.domain;

import java.util.Date;

/**
 * @author honghzengpei 2017/10/9 11:17
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserOutDayDo {
    private Long id;
    private Long userId;
    private Date gmtCreate;
    private Date gmtModify;
    private Integer outDay;
    private Integer payDay;

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

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getOutDay() {
        return outDay;
    }

    public void setOutDay(Integer outDay) {
        this.outDay = outDay;
    }

    public Integer getPayDay() {
        return payDay;
    }

    public void setPayDay(Integer payDay) {
        this.payDay = payDay;
    }
}
