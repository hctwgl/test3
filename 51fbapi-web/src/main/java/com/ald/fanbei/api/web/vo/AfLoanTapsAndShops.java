package com.ald.fanbei.api.web.vo;

import java.util.List;

import org.apache.log4j.lf5.viewer.LogFactor5LoadingDialog;

import com.ald.fanbei.api.common.AbstractSerial;
/**
 * 借贷超市的taps还有哥哥tap下的shops
 * <p>Title:AfLoanTapsAndShops <p>
 * <p>Description: <p>
 * @Copyright (c)  浙江阿拉丁电子商务股份有限公司 All Rights Reserved. 
 * @author qiao
 * @date 2017年7月27日下午5:17:39
 *
 */
public class AfLoanTapsAndShops extends AbstractSerial{

	private static final long serialVersionUID = -7397970575872233356L;

    private String name;

    private String alias;

    private Integer sort;

    private List<AfLoanShopVo> loanShopList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public List<AfLoanShopVo> getLoanShopList() {
		return loanShopList;
	}

	public void setLoanShopList(List<AfLoanShopVo> loanShopList) {
		this.loanShopList = loanShopList;
	}

    
}
