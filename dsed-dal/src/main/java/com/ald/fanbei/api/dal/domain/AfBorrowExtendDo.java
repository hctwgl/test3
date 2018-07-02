package com.ald.fanbei.api.dal.domain;

import java.util.Date;

/**
 * @author honghzengpei 2017/11/24 21:14
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBorrowExtendDo {
    private Long id;
    private Integer inBill;
    private Date gmtModified;
    private Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getInBill() {
        return inBill;
    }

    public void setInBill(Integer inBill) {
        this.inBill = inBill;
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
}
