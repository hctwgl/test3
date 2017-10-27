package com.ald.fanbei.api.dal.domain.query;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.page.Page;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;

/**
 * 
 *@类描述：AfGoodsQuery
 *@author 何鑫 2017年2月17日  09:50:53
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGoodsQuery extends Page<AfGoodsDo>{

	private static final long serialVersionUID = -722303985401230132L;

	private Long categoryId;
	
	private String keywords;
	
	private BigDecimal minAmount;//到手价最小值
	
	private BigDecimal maxAmount;//到手价最大值

	private String source;//商品来源
	
	private Integer appVersion;//版本号
	
	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the appVersion
	 */
	public Integer getAppVersion() {
		return appVersion;
	}

	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}
	
}
