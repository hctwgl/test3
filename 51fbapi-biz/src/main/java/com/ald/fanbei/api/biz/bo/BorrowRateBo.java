package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.math.BigDecimal;


/**
 *@类描述：
 *@author xiaotianjian 2017年6月5日下午8:07:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BorrowRateBo implements Serializable{
	
	private static final long serialVersionUID = -7768466960998539790L;
	
	private BigDecimal rate;//分期利率
	private BigDecimal poundageRate;//手续费率
	private BigDecimal rangeBegin;//最低手续费
	private BigDecimal rangeEnd;//最高手续费
	private Integer nper;//分期数
	/**
	 * @return the rate
	 */
	public BigDecimal getRate() {
		return rate;
	}
	/**
	 * @param rate the rate to set
	 */
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	/**
	 * @return the poundageRate
	 */
	public BigDecimal getPoundageRate() {
		return poundageRate;
	}
	/**
	 * @param poundageRate the poundageRate to set
	 */
	public void setPoundageRate(BigDecimal poundageRate) {
		this.poundageRate = poundageRate;
	}
	/**
	 * @return the rangeBegin
	 */
	public BigDecimal getRangeBegin() {
		return rangeBegin;
	}
	/**
	 * @param rangeBegin the rangeBegin to set
	 */
	public void setRangeBegin(BigDecimal rangeBegin) {
		this.rangeBegin = rangeBegin;
	}
	/**
	 * @return the rangeEnd
	 */
	public BigDecimal getRangeEnd() {
		return rangeEnd;
	}
	/**
	 * @param rangeEnd the rangeEnd to set
	 */
	public void setRangeEnd(BigDecimal rangeEnd) {
		this.rangeEnd = rangeEnd;
	}
	/**
	 * @return the nper
	 */
	public Integer getNper() {
		return nper;
	}
	/**
	 * @param nper the nper to set
	 */
	public void setNper(Integer nper) {
		this.nper = nper;
	}
	
	
	

}
