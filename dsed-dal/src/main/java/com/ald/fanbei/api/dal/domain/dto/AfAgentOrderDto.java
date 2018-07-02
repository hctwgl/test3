/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfAgentOrderDo;

/**
 * @类描述：
 * @author suweili 2017年4月24日下午4:07:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAgentOrderDto extends AfAgentOrderDo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String secTtype;
	private String numId;
	private String goodsName;
	private String goodsIcon;
	private Date gmtCreate;
	private BigDecimal priceAmount;
	private BigDecimal saleAmount;
	private BigDecimal actualAmount;
	/**
	 * @return the secTtype
	 */
	public String getSecTtype() {
		return secTtype;
	}
	/**
	 * @param secTtype the secTtype to set
	 */
	public void setSecTtype(String secTtype) {
		this.secTtype = secTtype;
	}
	/**
	 * @return the numId
	 */
	public String getNumId() {
		return numId;
	}
	/**
	 * @param numId the numId to set
	 */
	public void setNumId(String numId) {
		this.numId = numId;
	}
	/**
	 * @return the goodsName
	 */
	public String getGoodsName() {
		return goodsName;
	}
	/**
	 * @param goodsName the goodsName to set
	 */
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	/**
	 * @return the goodsIcon
	 */
	public String getGoodsIcon() {
		return goodsIcon;
	}
	/**
	 * @param goodsIcon the goodsIcon to set
	 */
	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}
	/**
	 * @return the priceAmount
	 */
	public BigDecimal getPriceAmount() {
		return priceAmount;
	}
	/**
	 * @param priceAmount the priceAmount to set
	 */
	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}
	/**
	 * @return the saleAmount
	 */
	public BigDecimal getSaleAmount() {
		return saleAmount;
	}
	/**
	 * @param saleAmount the saleAmount to set
	 */
	public void setSaleAmount(BigDecimal saleAmount) {
		this.saleAmount = saleAmount;
	}
	/**
	 * @return the actualAmount
	 */
	public BigDecimal getActualAmount() {
		return actualAmount;
	}
	/**
	 * @param actualAmount the actualAmount to set
	 */
	public void setActualAmount(BigDecimal actualAmount) {
		this.actualAmount = actualAmount;
	}
	/**
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	
}
