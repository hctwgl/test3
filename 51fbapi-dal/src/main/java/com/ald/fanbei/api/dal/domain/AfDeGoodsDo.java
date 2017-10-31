package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import java.util.Date;
import java.math.BigDecimal;

/**
 * 双十一砍价实体
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-10-17 11:40:19
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfDeGoodsDo extends AbstractSerial {

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
     * 商品规则
     */
    private Long goodspriceid;

    /**
     * 最低砍价到金额
     */
    private BigDecimal lowestprice;

    /**
     * 0 : 其它
            1 : iphone10
     */
    private Integer type;


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
     * 获取商品规则
     *
     * @return 商品规则
     */
    public Long getGoodspriceid(){
      return goodspriceid;
    }

    /**
     * 设置商品规则
     * 
     * @param goodspriceid 要设置的商品规则
     */
    public void setGoodspriceid(Long goodspriceid){
      this.goodspriceid = goodspriceid;
    }

    /**
     * 获取最低砍价到金额
     *
     * @return 最低砍价到金额
     */
    public BigDecimal getLowestprice(){
      return lowestprice;
    }

    /**
     * 设置最低砍价到金额
     * 
     * @param lowestprice 要设置的最低砍价到金额
     */
    public void setLowestprice(BigDecimal lowestprice){
      this.lowestprice = lowestprice;
    }

    /**
     * 获取0 : 其它
            1 : iphone10
     *
     * @return 0 : 其它
            1 : iphone10
     */
    public Integer getType(){
      return type;
    }

    /**
     * 设置0 : 其它
            1 : iphone10
     * 
     * @param type 要设置的0 : 其它
            1 : iphone10
     */
    public void setType(Integer type){
      this.type = type;
    }

}