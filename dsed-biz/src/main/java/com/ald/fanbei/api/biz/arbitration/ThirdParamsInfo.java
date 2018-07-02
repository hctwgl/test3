package com.ald.fanbei.api.biz.arbitration;

/**
 * 接口请求参数记录
 * @Title: ThirdParamsInfo.java
 * @Author: mo.jf
 * @Date: 2017年5月9日下午3:49:44 
 * @Description:
 */
public class ThirdParamsInfo{

	//商户号
	private String merchantCode;
	//请求数据格式（JSON）
	private String format;
	//编码格式(utf-8)
	private String encode;
	//业务编码
	private String busiCode;
	//请求参数
	private String param;
	//请求时间
	private String time;
	//签名串
	private String signCode;
	//签名类型（MD5）
	private String signType;
	//返回结果
	private String result;
	
	public ThirdParamsInfo(){}

	public String getMerchantCode() {
		return merchantCode;
	}

	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

	public String getParam() {
		return param;
	}

	public void setParam(String param) {
		this.param = param;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSignCode() {
		return signCode;
	}

	public void setSignCode(String signCode) {
		this.signCode = signCode;
	}

	public String getSignType() {
		return signType;
	}

	public void setSignType(String signType) {
		this.signType = signType;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}

