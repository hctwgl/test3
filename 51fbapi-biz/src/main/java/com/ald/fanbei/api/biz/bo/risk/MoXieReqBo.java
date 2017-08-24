package com.ald.fanbei.api.biz.bo.risk;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类描述：魔蝎运营商谁请求参数拼接对象
 *@author chengkang 2017年6月24日下午4:17:10
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class MoXieReqBo extends AbstractSerial {
	
	private static final long serialVersionUID = -7933537167707533015L;
	private String phone;//用户手机号
	private String idcard;//用户身份证号
	private String name;//用户姓名
	
	public MoXieReqBo() {
		super();
	}
	
	
	public MoXieReqBo(String phone, String idcard, String name) {
		super();
		this.phone = phone;
		this.idcard = idcard;
		this.name = name;
	}


	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getIdcard() {
		return idcard;
	}
	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
