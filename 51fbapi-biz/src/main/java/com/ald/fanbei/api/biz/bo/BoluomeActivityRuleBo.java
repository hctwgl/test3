package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author chenqiwei 2017年10月09日上午10:07:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeActivityRuleBo extends AbstractSerial{
	
	private static final long serialVersionUID = 940048465975048069L;
	private Integer num;     
	private String ruleDescription;
	public Integer getNum() {
	    return num;
	}
	public void setNum(Integer num) {
	    this.num = num;
	}
	public String getRuleDescription() {
	    return ruleDescription;
	}
	public void setRuleDescription(String ruleDescription) {
	    this.ruleDescription = ruleDescription;
	}
	@Override
	public String toString() {
	    return "BoluomeActivityRuleBo [num=" + num + ", ruleDescription=" + ruleDescription + "]";
	}   
	 
	
	
}
