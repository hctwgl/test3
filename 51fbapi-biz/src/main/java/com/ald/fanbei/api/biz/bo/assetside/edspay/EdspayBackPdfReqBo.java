package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.util.List;

/**
 * @类现描述：钱包平台退回债权请求实体
 * @author chengkang 2017年11月29日 14:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayBackPdfReqBo implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;

	/**
	 * 债权类型，0现金贷，1消费分期
	 */
	private Integer debtType;

	private String orderNo;

	private String companyName;

	private String companyTaxNo;

	private String protocolUrl;

	private String borrowerName;

	private List investorList;


	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

	public List getInvestorList() {
		return investorList;
	}

	public void setInvestorList(List investorList) {
		this.investorList = investorList;
	}

	public Integer getDebtType() {
		return debtType;
	}

	public void setDebtType(Integer debtType) {
		this.debtType = debtType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyTaxNo() {
		return companyTaxNo;
	}

	public void setCompanyTaxNo(String companyTaxNo) {
		this.companyTaxNo = companyTaxNo;
	}

	public String getProtocolUrl() {
		return protocolUrl;
	}

	public void setProtocolUrl(String protocolUrl) {
		this.protocolUrl = protocolUrl;
	}
}
