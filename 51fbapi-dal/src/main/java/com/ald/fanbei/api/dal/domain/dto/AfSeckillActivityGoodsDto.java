package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfSeckillActivityGoodsDo;

import java.util.Date;
import java.util.zip.DataFormatException;

public class AfSeckillActivityGoodsDto extends AfSeckillActivityGoodsDo {
    private Date gmtStart;
    private Date gmtEnd;
    private Integer closeTime;
    private Integer goodsLimitCount;
    public Date getGmtStart() {
        return gmtStart;
    }

    public void setGmtStart(Date gmtStart) {
        this.gmtStart = gmtStart;
    }

    public Date getGmtEnd() {
        return gmtEnd;
    }

    public void setGmtEnd(Date gmtEnd) {
        this.gmtEnd = gmtEnd;
    }

    public Integer getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Integer closeTime) {
        this.closeTime = closeTime;
    }

    public Integer getGoodsLimitCount() {
        return goodsLimitCount;
    }

    public void setGoodsLimitCount(Integer goodsLimitCount) {
        this.goodsLimitCount = goodsLimitCount;
    }
}
