package com.ald.fanbei.api.biz.bo.ups;

/**
 * @类现描述：支付路由短信重发bo
 * @author chenqiwei 2018年3月29日 下午16:42:33
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsResendSmsReqBo extends UpsReqBo {
    private static final long serialVersionUID = 7977070544204337468L;
    private String oldOrderNo; // 原交易订单号
    private String tradeType; // 异步通知地址

    public String getOldOrderNo() {
	return oldOrderNo;
    }

    public void setOldOrderNo(String oldOrderNo) {
	this.oldOrderNo = oldOrderNo;
	this.put("oldOrderNo", oldOrderNo);
    }

    public String getTradeType() {
	return tradeType;
    }

    public void setTradeType(String tradeType) {
	this.tradeType = tradeType;
	this.put("tradeType", tradeType);
    }
}
