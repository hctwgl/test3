package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Map;

/**  
 * @Title: AfFreshmanGoodsVo
 * @Package com.ald.fanbei.api.web.vo
 * @Description: 新用户专享商品
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年11月3日
 * @version V1.0  
 */
public class AfFreshmanGoodsVo extends AfGoodsVo {

	 private BigDecimal decreasePrice;//立减金额
	 
	 private Map<String, Object> nperMap;//月供

	public BigDecimal getDecreasePrice() {
		return decreasePrice;
	}

	public void setDecreasePrice(BigDecimal decreasePrice) {
		this.decreasePrice = decreasePrice;
	}

	public Map<String, Object> getNperMap() {
		return nperMap;
	}

	public void setNperMap(Map<String, Object> nperMap) {
		this.nperMap = nperMap;
	}
}
