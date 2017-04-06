package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 *@类描述：AfUserFootmarkDo
 *@author 何鑫 2017年1月19日  14:57:42
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfUserFootmarkDo extends AbstractSerial{

	private static final long serialVersionUID = 7852110258204555698L;

	private Long	rid;
	
	private Date	gmtCreate;//创建时间
	
	private Date	gmtModified;//最近修改时间
	
	private Long	userId;//用户id
	
	private Long	goodsId;//商品id
	
	private String	goodsName;//商品名称
	
	private String  goodsIcon;//商品图标
	
	private BigDecimal priceAmount;//商品金额
	
	private BigDecimal commissionAmount;//返利金额
	
	private BigDecimal commissionRate;//返利比例
	
	private String   source;//商品来源【T:toabao淘宝，C: collect采集 ，B:brand品牌】
	
	private String   browseTime;//浏览时间记录

	public Long getRid() {
		return rid;
	}

	public void setRid(Long rid) {
		this.rid = rid;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModified() {
		return gmtModified;
	}

	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsIcon() {
		return goodsIcon;
	}

	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}

	public BigDecimal getPriceAmount() {
		return priceAmount;
	}

	public void setPriceAmount(BigDecimal priceAmount) {
		this.priceAmount = priceAmount;
	}

	public BigDecimal getCommissionAmount() {
		return commissionAmount;
	}

	public void setCommissionAmount(BigDecimal commissionAmount) {
		this.commissionAmount = commissionAmount;
	}

	public BigDecimal getCommissionRate() {
		return commissionRate;
	}

	public void setCommissionRate(BigDecimal commissionRate) {
		this.commissionRate = commissionRate;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getBrowseTime() {
		return browseTime;
	}

	public void setBrowseTime(String browseTime) {
		this.browseTime = browseTime;
	}
}
