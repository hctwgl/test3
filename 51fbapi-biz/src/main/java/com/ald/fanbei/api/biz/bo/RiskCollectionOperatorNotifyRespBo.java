package com.ald.fanbei.api.biz.bo;

import java.util.Date;
import java.util.HashMap;

import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.DateUtil;

/**
 *@类现描述：催收平台响应实体
 *@author chengkang 2017年08月02日 14:29:12
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskCollectionOperatorNotifyRespBo extends HashMap<String, String>{

	private static final long serialVersionUID = 6478938143376881332L;
	
	private String code;
	private String timestamp;
	private String msg;
	private String sign;
	
	public RiskCollectionOperatorNotifyRespBo() {
		super();
	}
	
	public RiskCollectionOperatorNotifyRespBo(FanbeiThirdRespCode respInfo) {
		super();
		this.setCode(respInfo.getCode());
		this.setMsg(respInfo.getMsg());
		this.setTimestamp(DateUtil.getDateTimeFull(new Date()));
	}
	
	public RiskCollectionOperatorNotifyRespBo(String code, String timestamp,
			String msg, String sign) {
		super();
		this.code = code;
		this.timestamp = timestamp;
		this.msg = msg;
		this.sign = sign;
	}

	public void resetMsgInfo(FanbeiThirdRespCode respInfo){
		this.setCode(respInfo.getCode());
		this.setMsg(respInfo.getMsg());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
		this.put("code", code);
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
		this.put("msg", msg);
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
		this.put("sign", sign);
	}
	
}
