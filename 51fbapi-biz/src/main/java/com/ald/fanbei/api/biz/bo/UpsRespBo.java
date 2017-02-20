package com.ald.fanbei.api.biz.bo;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;


/**
 *@类现描述：支付响应路由父类
 *@author chenjinhu 2017年2月19日 下午2:37:22
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsRespBo{

	private String version;  //版本号     接口版本号，此处为10
	private String service;  //接口服务名称，此处为 authSign
	private String merNo;  //UPS商户号    UPS分配给商户的商户号
	private String orderNo;  //订单号    业务订单号，必须保证唯一
	private String payCanal;  //支付渠道     见 附件一 支付渠道
	private String clientType;  //客户端类型
	private String merPriv;  //私有域     商户自定义参数，响应时原样返回
	private String reqExt;  //扩展参数
	private String respCode;  //应答码
	private String respDesc;  //应答码描述
	private String tppRespCode;  //第三方响应应答码
	

	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getMerNo() {
		return merNo;
	}
	public void setMerNo(String merNo) {
		this.merNo = merNo;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getPayCanal() {
		return payCanal;
	}
	public void setPayCanal(String payCanal) {
		this.payCanal = payCanal;
	}
	public String getClientType() {
		return clientType;
	}
	public void setClientType(String clientType) {
		this.clientType = clientType;
	}
	public String getMerPriv() {
		return merPriv;
	}
	public void setMerPriv(String merPriv) {
		this.merPriv = merPriv;
	}
	public String getReqExt() {
		return reqExt;
	}
	public void setReqExt(String reqExt) {
		this.reqExt = reqExt;
	}
	public String getRespCode() {
		return respCode;
	}
	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}
	public String getRespDesc() {
		return respDesc;
	}
	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
	public String getTppRespCode() {
		return tppRespCode;
	}
	public void setTppRespCode(String tppRespCode) {
		this.tppRespCode = tppRespCode;
	}
	
	@Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
