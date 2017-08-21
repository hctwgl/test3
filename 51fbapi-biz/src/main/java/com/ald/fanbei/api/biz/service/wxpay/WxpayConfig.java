package com.ald.fanbei.api.biz.service.wxpay;

/**
 * 
 *@类描述：微信支付配置
 *
 *@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxpayConfig {
	
	public static final String KEY_SIGN                    = "sign";
	public static final String WX_REFUND_API               = "https://api.mch.weixin.qq.com/secapi/pay/refund";
	public static final String WX_UNIFIEDORDER_API         = "https://api.mch.weixin.qq.com/pay/unifiedorder";
	public static final String WX_ORDERQUERY_API           = "https://api.mch.weixin.qq.com/pay/orderquery";

	
	public static final String WX_APP_ID                   = "wx7b2b0aa8b3f0459e";
	public static final String WX_MCH_ID                   = "1441757802";
	
	public static final String WX_REFUND_QUERY_API               = "https://api.mch.weixin.qq.com/pay/refundquery";
	
	public static final String RESULT_CODE               = "result_code";
	public static final String RESULT_CODE_SUCCESS       = "SUCCESS";
	
	public static final String TRADE_STATE               = "trade_state";
	public static final String TRADE_STATE_SUCCESS       = "SUCCESS";//SUCCESS—支付成功 
	public static final String TRADE_STATE_NOTPAY        = "NOTPAY";//NOTPAY—未支付 
	public static final String TRADE_STATE_CLOSED        = "CLOSED";//CLOSED—已关闭 
	public static final String TRADE_STATE_USERPAYING    = "USERPAYING";//USERPAYING--用户支付中 
	public static final String TRADE_STATE_PAYERROR       = "PAYERROR";//PAYERROR--支付失败(其他原因，如
	
	
//	public static final String WX_ORDERQUERY_API           = "https://api.mch.weixin.qq.com/pay/orderquery";

}
