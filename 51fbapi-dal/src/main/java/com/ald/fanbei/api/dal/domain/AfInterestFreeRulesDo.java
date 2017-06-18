/**
 * 
 */
package com.ald.fanbei.api.dal.domain;

import java.util.Date;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author xiaotianjian 2017年6月4日下午8:32:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfInterestFreeRulesDo extends AbstractSerial {
	
private static final long serialVersionUID = 3050189614771455156L;
	
	private Long rid;
	private Date gmtCreate;
	private Date gmtModified;
	private String name;//免息规则名称
	private String ruleJson;//规则json如[{'nper':'3','freeNper':'1'},{'nper':'6','freeNper':'5'},{'nper':'9','freeNper':'2'},{'nper':'12','freeNper':'5'}]
	
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
	 * @return the gmtCreate
	 */
	public Date getGmtCreate() {
		return gmtCreate;
	}
	/**
	 * @param gmtCreate the gmtCreate to set
	 */
	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}
	/**
	 * @return the gmtModified
	 */
	public Date getGmtModified() {
		return gmtModified;
	}
	/**
	 * @param gmtModified the gmtModified to set
	 */
	public void setGmtModified(Date gmtModified) {
		this.gmtModified = gmtModified;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the ruleJson
	 */
	public String getRuleJson() {
		return ruleJson;
	}
	/**
	 * @param ruleJson the ruleJson to set
	 */
	public void setRuleJson(String ruleJson) {
		this.ruleJson = ruleJson;
	}


}
