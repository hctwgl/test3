/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * @类描述：
 * @author suweili 2017年3月3日下午5:56:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfMoblieChargeDo extends AbstractSerial {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long rid;
	private String province;
	private String company;
	private String priceJson;
	/**
	 * @return the rid
	 */
	public Long getRid() {
		return rid;
	}
	/**
	 * @param rid the rid to set
	 */
	public void setRid(Long rid) {
		this.rid = rid;
	}
	/**
	 * @return the province
	 */
	public String getProvince() {
		return province;
	}
	/**
	 * @param province the province to set
	 */
	public void setProvince(String province) {
		this.province = province;
	}
	/**
	 * @return the company
	 */
	public String getCompany() {
		return company;
	}
	/**
	 * @param company the company to set
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	/**
	 * @return the priceJson
	 */
	public String getPriceJson() {
		return priceJson;
	}
	/**
	 * @param priceJson the priceJson to set
	 */
	public void setPriceJson(String priceJson) {
		this.priceJson = priceJson;
	}


}
