package com.ald.fanbei.api.biz.bo.risk;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.common.enums.ContactRelationType;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 * @类描述：用户联系人信息同步
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskLinkMan extends RiskRegisterStrongReqBo {

	private static final long serialVersionUID = 1L;

	public RiskLinkMan(String consumerNo, String event, AfUserAuthDo afUserAuthDo) {
		super(consumerNo, event, null, null, afUserAuthDo, null, null, null, null, null, null, null, null, null);
	}

	@Override
	protected void create(String consumerNo,  String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		
		setConsumerNo(consumerNo);
		setEvent(event);
		
		JSONObject linkManInfo = new JSONObject();
		linkManInfo.put("name", afUserAuthDo.getContactorName());
		ContactRelationType contactRelationType = ContactRelationType.findRoleTypeByName(afUserAuthDo.getContactorType());
		if (contactRelationType == null) {
			linkManInfo.put("relation", ContactRelationType.others.getCode());
		} else {
			linkManInfo.put("relation", contactRelationType.getCode());
		}
		linkManInfo.put("idNo", "");
		linkManInfo.put("phone", afUserAuthDo.getContactorMobile());
		linkManInfo.put("reqExt", "");
//		setLinkManInfo(Base64.encodeString(JSON.toJSONString(linkManInfo)));
		setLinkManInfo(JSON.toJSONString(linkManInfo));
	}

}
