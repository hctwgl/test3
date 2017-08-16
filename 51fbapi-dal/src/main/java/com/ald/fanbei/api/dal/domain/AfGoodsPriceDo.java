package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;
import java.util.Date;

/**
 * '第三方-上树请求记录实体
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-14 11:23:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 public class AfGoodsPriceDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long rid;


    

    public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
     */
    private String propertyValueIds;

    /**
     * 各个属性的组合,以逗号分隔
     */
    private String propertyValueNames;

    /**
     * 市场价格
     */
    private BigDecimal priceAmount;

    /**
     * 销售价格
     */
    private BigDecimal actualAmount;

    /**
     * 销量
     */
    private Integer saleCount;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 排序
     */
    private Long sort;

    /**
     * 上下架状态，N：下架，Y：上架
     */
    private String isSale;



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
     * 获取创建者
     *
     * @return 创建者
     */
    public String getCreator(){
      return creator;
    }

    /**
     * 设置创建者
     * 
     * @param creator 要设置的创建者
     */
    public void setCreator(String creator){
      this.creator = creator;
    }

    /**
     * 获取最后修改者
     *
     * @return 最后修改者
     */
    public String getModifier(){
      return modifier;
    }

    /**
     * 设置最后修改者
     * 
     * @param modifier 要设置的最后修改者
     */
    public void setModifier(String modifier){
      this.modifier = modifier;
    }

    /**
     * 获取商品id
     *
     * @return 商品id
     */
    public Long getGoodsId(){
      return goodsId;
    }

    /**
     * 设置商品id
     * 
     * @param goodsId 要设置的商品id
     */
    public void setGoodsId(Long goodsId){
      this.goodsId = goodsId;
    }

    /**
     * 获取商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
     *
     * @return 商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
     */
    public String getPropertyValueIds(){
      return propertyValueIds;
    }

    /**
     * 设置商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
     * 
     * @param propertyValueIds 要设置的商品属性组合,属性值id从小到大排序，多个逗号隔开,开始和结尾都需要带上逗号，如“,1,5,3,”
     */
    public void setPropertyValueIds(String propertyValueIds){
      this.propertyValueIds = propertyValueIds;
    }

    /**
     * 获取各个属性的组合,以逗号分隔
     *
     * @return 各个属性的组合,以逗号分隔
     */
    public String getPropertyValueNames(){
      return propertyValueNames;
    }

    /**
     * 设置各个属性的组合,以逗号分隔
     * 
     * @param propertyValueNames 要设置的各个属性的组合,以逗号分隔
     */
    public void setPropertyValueNames(String propertyValueNames){
      this.propertyValueNames = propertyValueNames;
    }

    

    public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}

	/**
     * 获取销量
     *
     * @return 销量
     */
    public Integer getSaleCount(){
      return saleCount;
    }

    /**
     * 设置销量
     * 
     * @param saleCount 要设置的销量
     */
    public void setSaleCount(Integer saleCount){
      this.saleCount = saleCount;
    }

    /**
     * 获取库存
     *
     * @return 库存
     */
    public Integer getStock(){
      return stock;
    }

    /**
     * 设置库存
     * 
     * @param stock 要设置的库存
     */
    public void setStock(Integer stock){
      this.stock = stock;
    }

    /**
     * 获取排序
     *
     * @return 排序
     */
    public Long getSort(){
      return sort;
    }

    /**
     * 设置排序
     * 
     * @param sort 要设置的排序
     */
    public void setSort(Long sort){
      this.sort = sort;
    }

    /**
     * 获取上下架状态，N：下架，Y：上架
     *
     * @return 上下架状态，N：下架，Y：上架
     */
    public String getIsSale(){
      return isSale;
    }

    /**
     * 设置上下架状态，N：下架，Y：上架
     * 
     * @param isSale 要设置的上下架状态，N：下架，Y：上架
     */
    public void setIsSale(String isSale){
      this.isSale = isSale;
    }

}