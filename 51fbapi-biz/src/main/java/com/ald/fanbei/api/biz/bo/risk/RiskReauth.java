package com.ald.fanbei.api.biz.bo.risk;

import org.dbunit.util.Base64;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：用户认证是调用强风控信息
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskReauth extends RiskRegisterStrongReqBo {
	private static final long serialVersionUID = 1L;

	public RiskReauth(String consumerNo, String event, String riskOrderNo, String appName, String ipAddress, String blackBox, String cardNum, String CHANNEL, String notifyHost) {
		super(consumerNo, event, riskOrderNo, null, null, appName, ipAddress, null, blackBox, cardNum, CHANNEL, null, null, notifyHost);
	}

	@Override
	protected void create(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		setConsumerNo(consumerNo);
		setEvent(event);
		setOrderNo(riskOrderNo);
		
		String notifyUrl = "/third/risk/registerStrongRisk";
		JSONObject riskInfo = new JSONObject();
		riskInfo.put("channel", CHANNEL);
		riskInfo.put("scene", "20");
		riskInfo.put("notifyUrl", notifyHost + notifyUrl);
		riskInfo.put("reqExt", "");
		setRiskInfo(JSON.toJSONString(riskInfo));

		JSONObject eventInfo = new JSONObject();
		eventInfo.put("eventType", Constants.EVENT_FINANCE_LIMIT);
		eventInfo.put("appName", appName);
		eventInfo.put("cardNo", cardNum);
		eventInfo.put("blackBox", blackBox);
		eventInfo.put("ipAddress", ipAddress);
		setEventInfo(Base64.encodeString(JSON.toJSONString(eventInfo)));
	}

}
