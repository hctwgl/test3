package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 颜值测试红包表实体类实体
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:37:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfFacescoreRedDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 红包的金额
     */
    private BigDecimal amount;

    /**
     * 人脸图片的存储路径
     */
    private String imageurl;

    /**
     * 红包配置表的id
     */
    private Long configId;


    /**
     * 获取主键Id
     *
     * @return rid
     */
    public Long getRid(){
      return rid;
    }

    /**
     * 设置主键Id
     * 
     * @param 要设置的主键Id
     */
    public void setRid(Long rid){
      this.rid = rid;
    }
    
    /**
     * 获取红包的金额
     *
     * @return 红包的金额
     */
    public BigDecimal getAmount(){
      return amount;
    }

    /**
     * 设置红包的金额
     * 
     * @param amount 要设置的红包的金额
     */
    public void setAmount(BigDecimal amount){
      this.amount = amount;
    }

    /**
     * 获取人脸图片的存储路径
     *
     * @return 人脸图片的存储路径
     */
    public String getImageurl(){
      return imageurl;
    }

    /**
     * 设置人脸图片的存储路径
     * 
     * @param imageurl 要设置的人脸图片的存储路径
     */
    public void setImageurl(String imageurl){
      this.imageurl = imageurl;
    }

    /**
     * 获取红包配置表的id
     *
     * @return 红包配置表的id
     */
    public Long getConfigId(){
      return configId;
    }

    /**
     * 设置红包配置表的id
     * 
     * @param configId 要设置的红包配置表的id
     */
    public void setConfigId(Long configId){
      this.configId = configId;
    }

}