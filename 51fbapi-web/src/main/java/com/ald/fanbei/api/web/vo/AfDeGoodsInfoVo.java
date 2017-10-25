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
 public class AfDeGoodsInfoVo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 商品图片
     */
    private String image;
    
    /**
     * 参与人数
     */
    private long totalCount;
    
    /**
     * 商品名称
     */
    private String name;
    /**
     * 结束时间
     */
    private long endTime;
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
   
    public long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getEndTime() {
        return endTime;
    }
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
    
    
   
}