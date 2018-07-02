package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：首页频道
 * @author chenqiwei 2018年4月12日下午18:38:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfHomePageChannelVo extends AbstractSerial{

	private static final long serialVersionUID = -7150400787252733902L;

	private Long tabId;
	private String tabName;
	
	public Long getTabId() {
		return tabId;
	}
	public void setTabId(Long tabId) {
		this.tabId = tabId;
	}
	public String getTabName() {
		return tabName;
	}
	public void setTabName(String tabName) {
		this.tabName = tabName;
	}
	
	
	
	
}
