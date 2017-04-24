package com.ald.fanbei.api.web.vo;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月22日上午10:08:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfRepaymentConfirmVo extends AbstractSerial{

	private static final long serialVersionUID = 2951256865651010110L;

	private String billId;//账单id
	private BigDecimal repayAmount;//还款金额
	private BigDecimal rebateAmount;//返现金额
	private Long couponId;//优惠券id
	private String couponName;//优惠券名称
	private BigDecimal couponAmount;//优惠券金额
	private List<AfUserCouponVo> couponList;
	public String getBillId() {
		return billId;
	}
	public void setBillId(String billId) {
		this.billId = billId;
	}
	public BigDecimal getRepayAmount() {
		return repayAmount;
	}
	public void setRepayAmount(BigDecimal repayAmount) {
		this.repayAmount = repayAmount;
	}
	public BigDecimal getRebateAmount() {
		return rebateAmount;
	}
	public void setRebateAmount(BigDecimal rebateAmount) {
		this.rebateAmount = rebateAmount;
	}
	public Long getCouponId() {
		return couponId;
	}
	public void setCouponId(Long couponId) {
		this.couponId = couponId;
	}
	public String getCouponName() {
		return couponName;
	}
	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}
	public BigDecimal getCouponAmount() {
		return couponAmount;
	}
	public void setCouponAmount(BigDecimal couponAmount) {
		this.couponAmount = couponAmount;
	}
	public List<AfUserCouponVo> getCouponList() {
		return couponList;
	}
	public void setCouponList(List<AfUserCouponVo> couponList) {
		this.couponList = couponList;
	}
}
