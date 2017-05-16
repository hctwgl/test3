package com.ald.fanbei.api.biz.bo;

import java.math.BigDecimal;
import java.util.HashMap;

/**
 * 
 * @类描述：添加风控白名单信息请求Bo
 * @author fumeiai 2017年5月11日上午11:31:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WhiteUserRequestBo extends HashMap<String, String> {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	private String consumerNo; // 用户在业务系统中唯一标示
	private String realName; // 真实姓名
	private String phone; // 手机号码
	private String idNo; // 身份证号码
	private String grantAmount;// 授信额度
//	private String whiteList;//白名单标识
	private String signInfo; // 签名信息

	public String getConsumerNo() {
		return consumerNo;
	}

	public void setConsumerNo(String consumerNo) {
		this.consumerNo = consumerNo;
		this.put("consumerNo", consumerNo);
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
		this.put("realName", realName);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
		this.put("phone", phone);
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
		this.put("idNo", idNo);
	}

	public String getGrantAmount() {
		return grantAmount;
	}

	public void setGrantAmount(String grantAmount) {
		this.grantAmount = grantAmount;
		this.put("grantAmount", grantAmount);
	}

	public String getSignInfo() {
		return signInfo;
	}

	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}

}
