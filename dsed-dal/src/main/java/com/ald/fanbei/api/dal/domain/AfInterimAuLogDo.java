package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author zhourui on 2017年11月23日 15:53
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfInterimAuLogDo extends AbstractSerial {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Date interimAmount;
    private Date gmtFailuretime;
    private Long userId;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInterimAmount() {
        return interimAmount;
    }

    public void setInterimAmount(Date interimAmount) {
        this.interimAmount = interimAmount;
    }

    public Date getGmtFailuretime() {
        return gmtFailuretime;
    }

    public void setGmtFailuretime(Date gmtFailuretime) {
        this.gmtFailuretime = gmtFailuretime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
