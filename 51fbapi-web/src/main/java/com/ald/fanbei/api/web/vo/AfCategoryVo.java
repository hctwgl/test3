package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午10:44:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfCategoryVo extends AbstractSerial{

	private static final long serialVersionUID = -7150400787252733902L;

	private Long rid;
	private String name;
	public Long getRid() {
		return rid;
	}
	public void setRid(Long rid) {
		this.rid = rid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
