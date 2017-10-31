/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年5月5日下午6:19:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class NperDo extends AbstractSerial {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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
	private Integer nper;//期数
	private BigDecimal rate;//利率
}
