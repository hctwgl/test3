package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 *@类现描述：
 *@author chenjinhu 2017年2月19日 下午2:37:22
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsReqBo extends HashMap<String, String> {

	private static final long serialVersionUID = -5197302494589757587L;

	private String version;  //版本号     接口版本号，此处为10
	private String service;  //接口服务名称，此处为 authSign
	private String merNo;  //UPS商户号    UPS分配给商户的商户号
	private String orderNo;  //订单号    业务订单号，必须保证唯一
	private String payCanal;  //支付渠道     见 附件一 支付渠道
	private String clientType;  //客户端类型
	private String merPriv;  //私有域     商户自定义参数，响应时原样返回
	private String reqExt;  //扩展参数
	private String signInfo;
	

	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
		this.put("version", version);
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
		this.put("service", service);
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
		this.put("merNo", merNo);
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
		this.put("orderNo", orderNo);
	}
	public String getPayCanal() {
		return payCanal;
	}
	public void setPayCanal(String payCanal) {
		this.payCanal = payCanal;
		this.put("payCanal", payCanal);
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
		this.put("clientType", clientType);
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
		this.put("merPriv", merPriv);
	}
	public String getReqExt() {
		return reqExt;
	}
	public void setReqExt(String reqExt) {
		this.reqExt = reqExt;
		this.put("reqExt", reqExt);
	}
	
	public String getSignInfo() {
		return signInfo;
	}
	public void setSignInfo(String signInfo) {
		this.signInfo = signInfo;
		this.put("signInfo", signInfo);
	}
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
