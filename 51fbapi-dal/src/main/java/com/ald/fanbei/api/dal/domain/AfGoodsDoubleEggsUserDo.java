package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双蛋活动实体
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 16:16:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfGoodsDoubleEggsUserDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间，预约时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 秒杀商品id(不是商品id，外键id）
     */
    private Long doubleEggsId;

    /**
     * 排序
     */
    private Long userId;

    /**
     * 是否已经预约：0:没有预约；1：已经预约
     */
    private Integer isOrdered;


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
     * 获取创建时间，预约时间
     *
     * @return 创建时间，预约时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间，预约时间
     * 
     * @param gmtCreate 要设置的创建时间，预约时间
     */
    public void setGmtCreate(Date gmtCreate){
      this.gmtCreate = gmtCreate;
    }

    /**
     * 获取最后修改时间
     *
     * @return 最后修改时间
     */
    public Date getGmtModified(){
      return gmtModified;
    }

    /**
     * 设置最后修改时间
     * 
     * @param gmtModified 要设置的最后修改时间
     */
    public void setGmtModified(Date gmtModified){
      this.gmtModified = gmtModified;
    }

    /**
     * 获取秒杀商品id(不是商品id，外键id）
     *
     * @return 秒杀商品id(不是商品id，外键id）
     */
    public Long getDoubleEggsId(){
      return doubleEggsId;
    }

    /**
     * 设置秒杀商品id(不是商品id，外键id）
     * 
     * @param doubleEggsId 要设置的秒杀商品id(不是商品id，外键id）
     */
    public void setDoubleEggsId(Long doubleEggsId){
      this.doubleEggsId = doubleEggsId;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Long getUserId(){
      return userId;
    }

    /**
     * 设置排序
     * 
     * @param userId 要设置的排序
     */
    public void setUserId(Long userId){
      this.userId = userId;
    }

    /**
     * 获取是否已经预约：0:没有预约；1：已经预约
     *
     * @return 是否已经预约：0:没有预约；1：已经预约
     */
    public Integer getIsOrdered(){
      return isOrdered;
    }

    /**
     * 设置是否已经预约：0:没有预约；1：已经预约
     * 
     * @param isOrdered 要设置的是否已经预约：0:没有预约；1：已经预约
     */
    public void setIsOrdered(Integer isOrdered){
      this.isOrdered = isOrdered;
    }

}