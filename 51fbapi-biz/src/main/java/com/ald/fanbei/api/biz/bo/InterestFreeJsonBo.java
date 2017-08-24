package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;


/**
 *@类描述：
 *@author xiaotianjian 2017年6月5日下午8:07:31
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class InterestFreeJsonBo implements Serializable{

	private static final long serialVersionUID = 8630223491731007225L;
	
	private Integer nper;//分期数
	private Integer freeNper;//分期免期数
	
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
	 * @return the freeNper
	 */
	public Integer getFreeNper() {
		return freeNper;
	}
	/**
	 * @param freeNper the freeNper to set
	 */
	public void setFreeNper(Integer freeNper) {
		this.freeNper = freeNper;
	}
	
	

}
