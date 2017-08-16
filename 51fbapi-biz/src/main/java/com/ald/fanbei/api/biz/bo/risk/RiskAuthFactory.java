package com.ald.fanbei.api.biz.bo.risk;

import com.ald.fanbei.api.biz.bo.RiskRegisterStrongReqBo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
/**
 * 
 * @类描述：风控审批工厂类
 * @author fmai 2017年6月8日 09:58:30
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskAuthFactory {

	public static RiskRegisterStrongReqBo createRiskDo(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName, 
			String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost) {
		if ("ALL".equals(event)) {
			return new RiskStrong(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, notifyHost);
		} else if ("USER".equals(event)) {
			return new RiskUser(consumerNo, event, afUserDo, accountDo, CHANNEL, PRIVATE_KEY);
		} else if ("LINKMAN".equals(event)) {
			return new RiskLinkMan(consumerNo, event, afUserAuthDo);
		} else if ("DIRECTORY".equals(event)) {
			return new RiskContacts(consumerNo, event, directory);
		} else if ("REAUTH".equals(event)) {//有数据重新重新认证
			return new RiskReauth(consumerNo, event, riskOrderNo, appName, ipAddress, blackBox, cardNum, CHANNEL, notifyHost);
		}
		return null;
	}
}
