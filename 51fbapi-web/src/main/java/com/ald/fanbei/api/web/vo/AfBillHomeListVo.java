package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月21日上午10:21:27
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfBillHomeListVo extends AbstractSerial{

	private static final long serialVersionUID = 3895403925733056700L;
	private Long biilId;//账单id	 
	private String name;//借款名称	 
	private Integer nper;//分期数	 
	private Integer billNper;//当前期	 
	private BigDecimal billAmount;//账单金额	 
	private Date gmtCreate;//借款时间	 
	private String billStatus;//账单状态 Y:已还款 ，N:未还款 ,F:冻结
	private String borrowNo;//借款编号
	public Long getBiilId() {
		return biilId;
	}
	public void setBiilId(Long biilId) {
		this.biilId = biilId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNper() {
		return nper;
	}
	public void setNper(Integer nper) {
		this.nper = nper;
	}
	public Integer getBillNper() {
		return billNper;
	}
	public void setBillNper(Integer billNper) {
		this.billNper = billNper;
	}
	public BigDecimal getBillAmount() {
		return billAmount;
	}
	public void setBillAmount(BigDecimal billAmount) {
		this.billAmount = billAmount;
	}
	public Date getGmtCreate() {
		return gmtCreate;
	}
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	public String getBillStatus() {
		return billStatus;
	}
	public void setBillStatus(String billStatus) {
		this.billStatus = billStatus;
	}
	public String getBorrowNo() {
		return borrowNo;
	}
	public void setBorrowNo(String borrowNo) {
		this.borrowNo = borrowNo;
	}
	
}
