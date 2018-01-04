package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @Title: AfGoodsForSecondKill.java
 * @Package com.ald.fanbei.api.dal.domain.dto
 * @Description: TODO(用一句话描述该文件做什么)
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author qiao
 * @date 2017年12月8日 上午10:34:28
 * @version V1.0
 */
public class AfGoodsForSecondKill extends AbstractSerial {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4040529492366462846L;
	private Date serviceDate;
	private List<AfGoodsBuffer> goodsList;

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public List<AfGoodsBuffer> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<AfGoodsBuffer> goodsList) {
		this.goodsList = goodsList;
	}

	@Override
	public String toString() {
		return "AfGoodsForSecondKill [serviceDate=" + serviceDate + ", goodsList=" + goodsList + "]";
	}

	

	

}
