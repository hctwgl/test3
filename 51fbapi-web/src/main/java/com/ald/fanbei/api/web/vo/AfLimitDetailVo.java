package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月23日下午14:09:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfLimitDetailVo extends AbstractSerial{

	private static final long serialVersionUID = 6875560170559803552L;

	private Long refId;//明细id
	private String name;//名称
	private BigDecimal amount;//金额
	private Date gmtCreate;	
	private String limitNo;//明细编号
	private String type;//明细类型：【BORROW:借款,REPAYMENT:还款】
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getLimitNo() {
		return limitNo;
	}
	public void setLimitNo(String limitNo) {
		this.limitNo = limitNo;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getRefId() {
		return refId;
	}
	public void setRefId(Long refId) {
		this.refId = refId;
	}
}
