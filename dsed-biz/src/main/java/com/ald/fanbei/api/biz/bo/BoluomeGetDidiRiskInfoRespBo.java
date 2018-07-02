package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;


/**
 *@类描述：
 *@author xiaotianjian 2017年8月10日下午4:46:09
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BoluomeGetDidiRiskInfoRespBo extends AbstractSerial {
	
	private static final long serialVersionUID = 431214786314452193L;
	
	private BoluomeGetDidiRiskInfoPayInfoBo pay_Info;//支付信息
	
	private BoluomeGetDidiRiskInfoCardInfoBo card_info;//绑卡信息
	
	private BoluomeGetDidiRiskInfoLoginInfoBo login_info;//登陆信息

	/**
	 * @return the pay_Info
	 */
	public BoluomeGetDidiRiskInfoPayInfoBo getPay_Info() {
		return pay_Info;
	}

	/**
	 * @param pay_Info the pay_Info to set
	 */
	public void setPay_Info(BoluomeGetDidiRiskInfoPayInfoBo pay_Info) {
		this.pay_Info = pay_Info;
	}

	/**
	 * @return the card_info
	 */
	public BoluomeGetDidiRiskInfoCardInfoBo getCard_info() {
		return card_info;
	}

	/**
	 * @param card_info the card_info to set
	 */
	public void setCard_info(BoluomeGetDidiRiskInfoCardInfoBo card_info) {
		this.card_info = card_info;
	}

	/**
	 * @return the login_info
	 */
	public BoluomeGetDidiRiskInfoLoginInfoBo getLogin_info() {
		return login_info;
	}

	/**
	 * @param login_info the login_info to set
	 */
	public void setLogin_info(BoluomeGetDidiRiskInfoLoginInfoBo login_info) {
		this.login_info = login_info;
	}
	
	
	
}
