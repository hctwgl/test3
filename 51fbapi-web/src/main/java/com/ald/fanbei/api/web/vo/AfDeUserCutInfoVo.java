package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双十一砍价实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfDeUserCutInfoVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 本次砍价金额
     */
    private BigDecimal cutPrice;

    /**
     * 
     */
    private String nickName;

    /**
     * 
     */
    private String headImgUrl;

    /**
     * 本次砍价后商品剩余价格
     */
    private BigDecimal remainPrice;

   
    public BigDecimal getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(BigDecimal cutPrice) {
        this.cutPrice = cutPrice;
    }

   

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public BigDecimal getRemainPrice() {
        return remainPrice;
    }

    public void setRemainPrice(BigDecimal remainPrice) {
        this.remainPrice = remainPrice;
    }

    

}