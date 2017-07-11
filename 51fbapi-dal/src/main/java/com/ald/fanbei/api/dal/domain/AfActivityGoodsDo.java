package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

public class AfActivityGoodsDo extends AbstractSerial {


	private static final long serialVersionUID = -928070127716709540L;
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified; 
	private BigDecimal specialPrice; // 特价
	private Date startTime;  // 商品开始时间
	private Date validStart; // 有效开始时间
	private Date validEnd;   // 有效结束时间
	private Long goodsCount; // 商品数量
	private Long limitCount; // 限购数量
	private Long goodsId; // 商品id
	private String type; // 商品类型:PUTONG,TEMAI,FANLI
	
	
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
	public BigDecimal getSpecialPrice() {
		return specialPrice;
	}
	public void setSpecialPrice(BigDecimal specialPrice) {
		this.specialPrice = specialPrice;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getValidStart() {
		return validStart;
	}
	public void setValidStart(Date validStart) {
		this.validStart = validStart;
	}
	public Date getValidEnd() {
		return validEnd;
	}
	public void setValidEnd(Date validEnd) {
		this.validEnd = validEnd;
	}
	public Long getGoodsCount() {
		return goodsCount;
	}
	public void setGoodsCount(Long goodsCount) {
		this.goodsCount = goodsCount;
	}
	public Long getLimitCount() {
		return limitCount;
	}
	public void setLimitCount(Long limitCount) {
		this.limitCount = limitCount;
	}
	public Long getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
