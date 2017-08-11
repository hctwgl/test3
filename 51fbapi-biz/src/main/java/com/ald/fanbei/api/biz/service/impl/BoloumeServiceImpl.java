package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;

import org.eclipse.jetty.util.security.Credential.MD5;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoCardInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoLoginInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoPayInfoBo;
import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoRespBo;
import com.ald.fanbei.api.biz.service.BoluomeService;
import com.ald.fanbei.api.common.enums.BoluomePayType;
import com.ald.fanbei.api.common.util.DigestUtil;

/**
 *@类描述：
 *@author xiaotianjian 2017年8月10日下午5:58:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("boluomeService")
public class BoloumeServiceImpl implements BoluomeService {

	@Override
	public BoluomeGetDidiRiskInfoRespBo getRiskInfo(String orderId, String type) {
		BoluomeGetDidiRiskInfoRespBo resp = new BoluomeGetDidiRiskInfoRespBo();
		BoluomeGetDidiRiskInfoPayInfoBo pay_info = new BoluomeGetDidiRiskInfoPayInfoBo();
		pay_info.setLat(new BigDecimal("30.293594"));
		pay_info.setLng(new BigDecimal("120.16141"));
		pay_info.setPay_type(BoluomePayType.DEBITCARD.getCode());
		pay_info.setTime(System.currentTimeMillis());
		BoluomeGetDidiRiskInfoCardInfoBo card_info = new BoluomeGetDidiRiskInfoCardInfoBo();
		card_info.setCard_id(DigestUtil.MD5("16668"));
		card_info.setPeople_id(DigestUtil.MD5("68885"));
		card_info.setChannel(BoluomePayType.DEBITCARD.getCode());
		card_info.setDeviceid("863389038473537");
		card_info.setIp("36.23.96.138");
		card_info.setLat(new BigDecimal("30.293594"));
		card_info.setLng(new BigDecimal("120.16141"));
		card_info.setSource("app");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		card_info.setTime(c.getTimeInMillis());
		card_info.setStatus("1");
		card_info.setWifi_mac("");
		BoluomeGetDidiRiskInfoLoginInfoBo login_info = new BoluomeGetDidiRiskInfoLoginInfoBo();
		login_info.setDeviceid("863389038473537");
		login_info.setIp("36.23.96.138");
		login_info.setLat(new BigDecimal("30.293594"));
		login_info.setLng(new BigDecimal("120.16141"));
		login_info.setSource("app");
		login_info.setTime(System.currentTimeMillis());
		if ("PASSAGE_INFO".equals(type)) {
			resp.setCard_info(card_info);
			resp.setLogin_info(login_info);
		} else if("PAY_INFO".equals(type)){
			resp.setCard_info(card_info);
			resp.setPay_Info(pay_info);
		}
		return resp;
	}

}
