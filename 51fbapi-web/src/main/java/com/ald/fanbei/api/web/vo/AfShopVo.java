package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年3月26日下午11:23:36
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfShopVo extends AbstractSerial {

	private static final long serialVersionUID = 4308867993909978028L;
	private Long rid;
	private String name;//商城名称
	private String icon;//图片
	private BigDecimal rebateAmount;//用户返利
	private String rebateUnit;//用户返利单位【RMB人民币,PERCENTAGE百分比】
	private String type;//类型
	private String shopUrl;//商品链接地址
	
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the icon
	 */
	public String getIcon() {
		return icon;
	}
	/**
	 * @param icon the icon to set
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}
	/**
	 * @return the rebateAmount
	 */
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	/**
	 * @param rebateAmount the rebateAmount to set
	 */
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	/**
	 * @return the rebateUnit
	 */
	public String getRebateUnit() {
		return rebateUnit;
	}
	/**
	 * @param rebateUnit the rebateUnit to set
	 */
	public void setRebateUnit(String rebateUnit) {
		this.rebateUnit = rebateUnit;
	}
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the shopUrl
	 */
	public String getShopUrl() {
		return shopUrl;
	}
	/**
	 * @param shopUrl the shopUrl to set
	 */
	public void setShopUrl(String shopUrl) {
		this.shopUrl = shopUrl;
	}
	
	

}
