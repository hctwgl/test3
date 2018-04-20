package com.ald.fanbei.api.biz.bo;

/**
 * @类现描述：支付路由 快捷支付支付确认 bo
 * @author chenqiwei 2018年3月29日 下午16:42:33
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class UpsQuickPayConfirmReqBo extends UpsReqBo {
    private static final long serialVersionUID = 7977070544204337468L;

    private String smsCode; // 短信验证码
//    private String cardNo; // 银行卡号
//    private String tradeNo; // 原快捷支付交易订单号
//    private String notifyUrl; // 异步通知地址
//    private String userNo; // 用户在商户的唯一标识
    private String oldOrderNo;

    public String getSmsCode() {
	return smsCode;
    }

    public void setSmsCode(String smsCode) {
	this.smsCode = smsCode;
	this.put("smsCode", smsCode);
    }

    public String getOldOrderNo() {
	return oldOrderNo;
    }

    public void setOldOrderNo(String oldOrderNo) {
	this.oldOrderNo = oldOrderNo;
	this.put("oldOrderNo", oldOrderNo);
    }

}
