package com.ald.fanbei.api.biz.bo.assetside.edspay;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @类现描述：钱包平台退回债权请求实体
 * @author chengkang 2017年11月29日 14:29:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayInvestorInfoBo implements Serializable {

	private static final long serialVersionUID = 4347678991772430075L;

	private String investorPhone;

	private String investorName;

	private String investorCardId;

	private BigDecimal amount;

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getInvestorPhone() {
		return investorPhone;
	}

	public void setInvestorPhone(String investorPhone) {
		this.investorPhone = investorPhone;
	}

	public String getInvestorName() {
		return investorName;
	}

	public void setInvestorName(String investorName) {
		this.investorName = investorName;
	}

	public String getInvestorCardId() {
		return investorCardId;
	}

	public void setInvestorCardId(String investorCardId) {
		this.investorCardId = investorCardId;
	}
}
