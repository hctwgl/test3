package com.ald.fanbei.api.biz.bo.risk;

import org.dbunit.util.Base64;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.common.util.RSAUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * @类描述：用户信息同步
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskUser extends RiskRegisterStrongReqBo {
	private static final long serialVersionUID = 1L;

	/**
	 * @param consumerNo
	 * @param event
	 * @param afUserDo
	 * @param accountDo
	 * @param CHANNEL
	 * @param PRIVATE_KEY
	 */
	public RiskUser(String consumerNo, String event,  AfUserDo afUserDo, 
			 AfUserAccountDto accountDo,  String CHANNEL, String PRIVATE_KEY) {
		super(consumerNo, event, null, afUserDo, null, null, null, accountDo, null, null, CHANNEL, PRIVATE_KEY, null, null);
	}


	@Override
	protected void create(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		setConsumerNo(consumerNo);
		setEvent(event);
		
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
//		userInfo.put("realName", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getRealName()));
//		userInfo.put("phone", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getMobile()));
//		userInfo.put("idNo", RSAUtil.encrypt(PRIVATE_KEY, accountDo.getIdNumber()));
//		userInfo.put("email", RSAUtil.encrypt(PRIVATE_KEY, afUserDo.getEmail()));
//		setUserInfo(Base64.encodeString(JSON.toJSONString(userInfo)));
		setUserInfo(JSON.toJSONString(userInfo));
	}

}
