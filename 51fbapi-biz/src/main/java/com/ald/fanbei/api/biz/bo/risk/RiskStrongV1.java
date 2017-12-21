package com.ald.fanbei.api.biz.bo.risk;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ContactRelationType;
import com.ald.fanbei.api.common.util.RSAUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.dbunit.util.Base64;

/**
 * 
 * @类描述：用户认证是调用强风控信息
 * @author chefeipeng 2017年11月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskStrongV1 extends RiskRegisterStrongReqBo {
	private static final long serialVersionUID = 1L;

	public RiskStrongV1(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		super(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, notifyHost);
	}

	@Override
	protected void create(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		setConsumerNo(consumerNo);
		setEvent(event);
		setOrderNo(riskOrderNo);
		
		JSONObject userInfo = new JSONObject();
		userInfo.put("realName", afUserDo.getRealName());
		userInfo.put("phone", afUserDo.getMobile());
		userInfo.put("idNo", accountDo.getIdNumber());
		userInfo.put("email", afUserDo.getEmail());
		userInfo.put("alipayNo", accountDo.getAlipayAccount());
		userInfo.put("openId", accountDo.getOpenId());
		userInfo.put("address", afUserDo.getAddress());
		userInfo.put("channel", CHANNEL);
		userInfo.put("reqExt", "");
		userInfo.put("realName", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getRealName()));
		userInfo.put("phone", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getMobile()));
		userInfo.put("idNo", RSAUtil.encrypt(PRIVATE_KEY, accountDo.getIdNumber()));
		userInfo.put("email", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getEmail()));
		setUserInfo(JSON.toJSONString(userInfo));

		setDirectory(JSON.toJSONString(StringUtil.filterEmoji(directory)));

		if (!StringUtil.isBlank(afUserAuthDo.getContactorName())) {
			JSONObject linkManInfo = new JSONObject();
			linkManInfo.put("name", StringUtil.filterEmoji(afUserAuthDo.getContactorName()));
			ContactRelationType contactRelationType = ContactRelationType.findRoleTypeByName(afUserAuthDo.getContactorType());
			if (contactRelationType == null) {
				linkManInfo.put("relation", ContactRelationType.others.getCode());
			} else {
				linkManInfo.put("relation", contactRelationType.getCode());
			}
			linkManInfo.put("idNo", "");
			linkManInfo.put("phone", afUserAuthDo.getContactorMobile());
			linkManInfo.put("reqExt", "");
			setLinkManInfo(JSON.toJSONString(linkManInfo));
		}

		String notifyUrl = "/third/risk/registerStrongRiskV1";
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