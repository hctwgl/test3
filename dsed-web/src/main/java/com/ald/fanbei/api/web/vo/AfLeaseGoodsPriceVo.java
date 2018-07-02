package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

import java.math.BigDecimal;

/**
 * @author zhourui on 2018年03月06日 15:27
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLeaseGoodsPriceVo extends AbstractSerial {
    private static final long serialVersionUID = 1893140295561540377L;

    private Long priceId;
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

    private String isSale;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * [{"nper":期数,"recoverRate":回收折扣,"monthlyRent":月租,"richieAmount":保费价格}]}
     */
    private String leaseParam;

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public String getPropertyValueIds() {
        return propertyValueIds;
    }

    public void setPropertyValueIds(String propertyValueIds) {
        this.propertyValueIds = propertyValueIds;
    }

    public String getPropertyValueNames() {
        return propertyValueNames;
    }

    public void setPropertyValueNames(String propertyValueNames) {
        this.propertyValueNames = propertyValueNames;
    }

    public BigDecimal getPriceAmount() {
        return priceAmount;
    }

    public void setPriceAmount(BigDecimal priceAmount) {
        this.priceAmount = priceAmount;
    }

    public String getIsSale() {
        return isSale;
    }

    public void setIsSale(String isSale) {
        this.isSale = isSale;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getLeaseParam() {
        return leaseParam;
    }

    public void setLeaseParam(String leaseParam) {
        this.leaseParam = leaseParam;
    }
}
