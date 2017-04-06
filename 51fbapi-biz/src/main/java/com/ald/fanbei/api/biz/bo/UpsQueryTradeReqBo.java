package com.ald.fanbei.api.biz.bo;


/**
 *@类现描述：支付路由查询单笔资金交易bo
 *@author chenjinhu 2017年2月19日 下午1:49:59
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsQueryTradeReqBo extends UpsReqBo{
	private static final long serialVersionUID = 8310455391344267354L;
	
	private String tradeType;  //交易类型
	private String tradeNo;  //交易订单号
	
	public String getTradeType() {
		return tradeType;
	}
	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
		this.put("tradeType", tradeType);
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
		this.put("tradeNo", tradeNo);
	}

}
