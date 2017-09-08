package com.ald.fanbei.api.dal.domain;

import java.util.Date;

/**
 * @author honghzengpei 2017/9/8 9:03
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfYibaoOrderDo {
    private Long id;
    private String orderNo;
    private String payType;
    private Date gtmCreate;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public Date getGtmCreate() {
        return gtmCreate;
    }

    public void setGtmCreate(Date gtmCreate) {
        this.gtmCreate = gtmCreate;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
