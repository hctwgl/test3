package com.ald.fanbei.api.dal.domain;

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
 public class AfDeRandomPropertyDo extends AbstractSerial {

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
     * 
     */
    private Long goodspriceid;

    /**
     * 砍价次数区间起始值
     */
    private Integer cutmincount;

    /**
     * 砍价次数区间结束值
     */
    private Integer cutmaxcount;

    /**
     * 砍价金额最小随机数
     */
    private Integer pricemin;

    /**
     * 砍价金额最大随机数
     */
    private Integer pricemax;


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
     * 获取
     *
     * @return 
     */
    public Long getGoodspriceid(){
      return goodspriceid;
    }

    /**
     * 设置
     * 
     * @param goodspriceid 要设置的
     */
    public void setGoodspriceid(Long goodspriceid){
      this.goodspriceid = goodspriceid;
    }

    /**
     * 获取砍价次数区间起始值
     *
     * @return 砍价次数区间起始值
     */
    public Integer getCutmincount(){
      return cutmincount;
    }

    /**
     * 设置砍价次数区间起始值
     * 
     * @param cutmincount 要设置的砍价次数区间起始值
     */
    public void setCutmincount(Integer cutmincount){
      this.cutmincount = cutmincount;
    }

    /**
     * 获取砍价次数区间结束值
     *
     * @return 砍价次数区间结束值
     */
    public Integer getCutmaxcount(){
      return cutmaxcount;
    }

    /**
     * 设置砍价次数区间结束值
     * 
     * @param cutmaxcount 要设置的砍价次数区间结束值
     */
    public void setCutmaxcount(Integer cutmaxcount){
      this.cutmaxcount = cutmaxcount;
    }

    /**
     * 获取砍价金额最小随机数
     *
     * @return 砍价金额最小随机数
     */
    public Integer getPricemin(){
      return pricemin;
    }

    /**
     * 设置砍价金额最小随机数
     * 
     * @param pricemin 要设置的砍价金额最小随机数
     */
    public void setPricemin(Integer pricemin){
      this.pricemin = pricemin;
    }

    /**
     * 获取砍价金额最大随机数
     *
     * @return 砍价金额最大随机数
     */
    public Integer getPricemax(){
      return pricemax;
    }

    /**
     * 设置砍价金额最大随机数
     * 
     * @param pricemax 要设置的砍价金额最大随机数
     */
    public void setPricemax(Integer pricemax){
      this.pricemax = pricemax;
    }

}