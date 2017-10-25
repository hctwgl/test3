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
 public class AfDeUserGoodsVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;


    /**
     * 被砍价总次数（包含已到底价后砍价次数）
     */
    private Integer cutCount;

    /**
     * 已砍金额
     */
    private BigDecimal cutPrice;

    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 	排名
     */
    private Integer index;

 
    public Integer getCutCount() {
        return cutCount;
    }

    public void setCutCount(Integer cutCount) {
        this.cutCount = cutCount;
    }

    public BigDecimal getCutPrice() {
        return cutPrice;
    }

    public void setCutPrice(BigDecimal cutPrice) {
        this.cutPrice = cutPrice;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

  
 }