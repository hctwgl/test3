package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：提交风控审核
 * 
 * @author fmai 2017年6月6日 10:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authStrongRiskApi")
public class AuthStrongRiskApi implements ApiHandle {
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	RiskUtil riskUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		
		if (StringUtils.equals(afUserAuthDo.getZmStatus(), YesNoStatus.NO.getCode())) {//请先完成芝麻信用授权
			throw new FanbeiException(FanbeiExceptionCode.ZHIMA_CREDIT_INFO_EXIST_ERROR);
		}
		if (StringUtils.equals(afUserAuthDo.getMobileStatus(), YesNoStatus.NO.getCode())) {//请先完成运营商授权
			throw new FanbeiException(FanbeiExceptionCode.OPERATOR_INFO_EXIST_ERROR);
		}
		if (StringUtils.equals(afUserAuthDo.getContactorStatus(), YesNoStatus.NO.getCode())) {//请先完成紧急联系人设置
			throw new FanbeiException(FanbeiExceptionCode.EMERGENCY_CONTACT_INFO_EXIST_ERROR);
		}		
				
		AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);
		
		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
		if (idNumberDo == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
		} else {
			AfUserDo afUserDo = afUserService.getUserById(userId);
			String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
			String ipAddress = CommonUtil.getIpAddr(request);
			AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);

			try {
				RiskRespBo riskResp = riskUtil.registerStrongRisk(idNumberDo.getUserId() + "", "ALL", afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox, card.getCardNumber());
						
//						.register(idNumberDo.getUserId() + "", idNumberDo.getName(), accountDo.getMobile(), idNumberDo.getCitizenId(), accountDo.getEmail(),
//						accountDo.getAlipayAccount(), accountDo.getAddress());
				if(!riskResp.isSuccess()){
          			throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
          		}
			} catch (Exception e) {
				RiskRespBo riskResp = riskUtil.modify(idNumberDo.getUserId() + "", idNumberDo.getName(), accountDo.getMobile(), idNumberDo.getCitizenId(), accountDo.getEmail(),
						accountDo.getAlipayAccount(), accountDo.getAddress(), accountDo.getOpenId());
				if (!riskResp.isSuccess()) {
					throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR);
				}
				logger.error("更新风控用户失败：" + idNumberDo.getUserId());
			} 

/*			afUserDo.setRealName(idNumberDo.getName());
			afUserService.updateUser(afUserDo);

			accountDo.setRealName(idNumberDo.getName());
			accountDo.setIdNumber(idNumberDo.getCitizenId());
			afUserAccountService.updateUserAccountRealNameAndIdNumber(accountDo);*/

			return resp;
		}

	}

	public AfIdNumberDo idNumberDoWithIdNumberInfo(String address, String citizenId, String gender, String nation, String name, String validDateBegin, String validDateEnd,
			String birthday, String agency, String idFrontUrl, String idBehindUrl, AfIdNumberDo idNumberDo) {
		if (idNumberDo == null) {
			idNumberDo = new AfIdNumberDo();
		}
		idNumberDo.setAddress(address);
		idNumberDo.setAgency(agency);
		idNumberDo.setBirthday(birthday);
		idNumberDo.setCitizenId(citizenId);
		idNumberDo.setGender(gender);
		idNumberDo.setIdBehindUrl(idBehindUrl);
		idNumberDo.setIdFrontUrl(idFrontUrl);
		idNumberDo.setName(name);
		idNumberDo.setNation(nation);
		idNumberDo.setValidDateBegin(validDateBegin);
		idNumberDo.setValidDateEnd(validDateEnd);

		return idNumberDo;
	}

}
