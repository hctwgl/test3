package com.ald.fanbei.api.biz.bo.risk;

import java.util.Map;

import com.ald.fanbei.api.biz.bo.DredgeWhiteCollarLoanReqBo;
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

	public enum RiskEventType{
		ALL,
		USER,
		LINKMAN,
		DIRECTORY,
		REAUTH
	}

	public static RiskRegisterStrongReqBo createRiskDo(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName,
														String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost ,String bqsBlackBox) {
		if ("ALL".equals(event)) {
			return new RiskStrong(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, notifyHost,bqsBlackBox);
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

	/**
	 * 3.9.7认证流程兼容新老版本
	 * @param consumerNo
	 * @param event
	 * @param riskOrderNo
	 * @param afUserDo
	 * @param afUserAuthDo
	 * @param appName
	 * @param ipAddress
	 * @param accountDo
	 * @param blackBox
	 * @param cardNum
	 * @param CHANNEL
	 * @param PRIVATE_KEY
	 * @param directory
	 * @param notifyHost
	 * @return
	 */
	public static RiskRegisterStrongReqBo createRiskDoV1(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName,
													   String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost,String bqsBlackBox,String riskScene) {
		if ("ALL".equals(event)) {
			return new RiskStrongV1(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, notifyHost,bqsBlackBox,riskScene);
		} else if ("USER".equals(event)) {
			return new RiskUser(consumerNo, event, afUserDo, accountDo, CHANNEL, PRIVATE_KEY);
		} else if ("LINKMAN".equals(event)) {
			return new RiskLinkMan(consumerNo, event, afUserAuthDo);
		} else if ("DIRECTORY".equals(event)) {
			return new RiskContacts(consumerNo, event, directory);
		} else if ("REAUTH".equals(event)) {//有数据重新重新认证
			return new RiskReauthV1(consumerNo, event, riskOrderNo, appName, ipAddress, blackBox, cardNum, CHANNEL, notifyHost,bqsBlackBox,riskScene);
		}
		return null;
	}
	
	
	/**
	 * 3.9.7认证流程兼容新老版本
	 * @param consumerNo
	 * @param event
	 * @param riskOrderNo
	 * @param afUserDo
	 * @param afUserAuthDo
	 * @param appName
	 * @param ipAddress
	 * @param accountDo
	 * @param blackBox
	 * @param cardNum
	 * @param CHANNEL
	 * @param PRIVATE_KEY
	 * @param directory
	 * @param notifyHost
	 * @param extUserInfo 
	 * @return
	 */
	public static DredgeWhiteCollarLoanReqBo createDredgeWhiteCollarLoanBo(String consumerNo, String event, String riskOrderNo, AfUserDo afUserDo, AfUserAuthDo afUserAuthDo, String appName,
													   String ipAddress, AfUserAccountDto accountDo, String blackBox, String cardNum, String CHANNEL, String PRIVATE_KEY, String directory, String notifyHost,String bqsBlackBox,String riskScene, Map<String, Object> extUserInfo,String selectedType,String address,String censusRegister) {
		
		return new DredgeWhiteCollarLoan(consumerNo, event, riskOrderNo, afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, cardNum, CHANNEL, PRIVATE_KEY, directory, notifyHost,bqsBlackBox,riskScene,extUserInfo,selectedType, address,censusRegister);
		
	}
}
