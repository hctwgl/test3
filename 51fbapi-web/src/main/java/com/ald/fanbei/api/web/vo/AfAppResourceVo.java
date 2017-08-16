package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 * 
 * @类描述：app动态资源配置获取
 * @author chengkang 2017年7月14日下午18:38:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfAppResourceVo extends AbstractSerial{

	private static final long serialVersionUID = -7150400787252733902L;

	private String keyCode;
	private String keyMsg;
	private String remark;
	
	public AfAppResourceVo() {
		super();
	}

	public AfAppResourceVo(String keyCode, String keyMsg, String remark) {
		super();
		this.keyCode = keyCode;
		this.keyMsg = keyMsg;
		this.remark = remark;
	}

	public String getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(String keyCode) {
		this.keyCode = keyCode;
	}

	public String getKeyMsg() {
		return keyMsg;
	}

	public void setKeyMsg(String keyMsg) {
		this.keyMsg = keyMsg;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
