package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import  java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;



/**
 * @author zhourui on \ 15:15
 * @类描述：用户临时额度
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfInterimAuDo extends AbstractSerial {
    private static final long serialVersionUID = 1L;

    private Long rid;
    private Long userId;//用户ID
    private Date gmtCreate;//创建时间
    private Date gmtModifed;//修改时间
    private BigDecimal interimAmount;//临时额度
    private BigDecimal interimUsed;//已使用临时额度
    private Date gmtFailuretime;//失效时间

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
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

    public Date getGmtModifed() {
        return gmtModifed;
    }

    public void setGmtModifed(Date gmtModifed) {
        this.gmtModifed = gmtModifed;
    }

    public BigDecimal getInterimAmount() {
        return interimAmount;
    }

    public void setInterimAmount(BigDecimal interimAmount) {
        this.interimAmount = interimAmount;
    }

    public BigDecimal getInterimUsed() {
        return interimUsed;
    }

    public void setInterimUsed(BigDecimal interimUsed) {
        this.interimUsed = interimUsed;
    }

    public Date getGmtFailuretime() {
        return gmtFailuretime;
    }

    public void setGmtFailuretime(Date gmtFailuretime) {
        this.gmtFailuretime = gmtFailuretime;
    }
}
