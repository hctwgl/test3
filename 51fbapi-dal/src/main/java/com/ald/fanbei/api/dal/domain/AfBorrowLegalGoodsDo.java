package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 实体
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfBorrowLegalGoodsDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 商品Id
     */
    private Long goodsId;

    /**
     * 起始利润金额
     */
    private BigDecimal startProfitAmount;

    /**
     * 结束利润金额
     */
    private BigDecimal endProfitAmount;

    /**
     * 排序
     */
    private Integer sort;


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
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getGmtCreate(){
      return gmtCreate;
    }

    /**
     * 设置创建时间
     * 
     * @param gmtCreate 要设置的创建时间
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
     * 获取商品Id
     *
     * @return 商品Id
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品Id
     * 
     * @param goodsId 要设置的商品Id
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取起始利润金额
     *
     * @return 起始利润金额
     */
    public BigDecimal getStartProfitAmount(){
      return startProfitAmount;
    }

    /**
     * 设置起始利润金额
     * 
     * @param startProfitAmount 要设置的起始利润金额
     */
    public void setStartProfitAmount(BigDecimal startProfitAmount){
      this.startProfitAmount = startProfitAmount;
    }

    /**
     * 获取结束利润金额
     *
     * @return 结束利润金额
     */
    public BigDecimal getEndProfitAmount(){
      return endProfitAmount;
    }

    /**
     * 设置结束利润金额
     * 
     * @param endProfitAmount 要设置的结束利润金额
     */
    public void setEndProfitAmount(BigDecimal endProfitAmount){
      this.endProfitAmount = endProfitAmount;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Integer getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Integer sort){
      this.sort = sort;
    }

}