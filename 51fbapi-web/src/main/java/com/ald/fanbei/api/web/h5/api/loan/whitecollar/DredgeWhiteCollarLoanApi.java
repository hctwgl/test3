package com.ald.fanbei.api.web.h5.api.loan.whitecollar;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.DredgeWhiteCollarLoanParam;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.google.common.collect.Maps;
/**
 * 开通白领贷
 * @author rongbo
 *
 */
@NeedLogin
@Component("dredgeWhiteCollarLoanApi")
@Validator("dredgeWhiteCollarLoanParam")
public class DredgeWhiteCollarLoanApi implements H5Handle {

	@Resource
	RiskUtil riskUtil;

	@Resource
	AfUserBankcardService afUserBankcardService;
	
	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource
	AfUserAuthService afUserAuthService;
	
	@Resource
	BizCacheUtil bizCacheUtil;
	
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		Long userId = context.getUserId();
		DredgeWhiteCollarLoanParam param = (DredgeWhiteCollarLoanParam) context.getParamEntity();
		// 提交风控审核，获取白领贷额度
		String clientIp = context.getClientIp();
		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
		if(mainCard == null) {
			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
		}
		String cardNo = mainCard.getCardNumber();
		String appName = context.getId().substring(context.getId().lastIndexOf("_") + 1, context.getId().length());
		AfUserDo afUserDo = afUserService.getUserById(userId);
		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		Object directory = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + userId);
		String riskOrderNo = riskUtil.getOrderNo("loan", cardNo.substring(cardNo.length() - 4, cardNo.length()));
		
		Map<String,Object> extUserInfo = getExtUserInfo(param);
		
		RiskRespBo riskResp = riskUtil.dredgeWhiteCollarLoan(ObjectUtils.toString(userId), "ALL", afUserDo,
				afUserAuthDo, appName, clientIp, accountDo, param.getBlackBox(), cardNo, riskOrderNo,
				param.getBqsBlackBox(), "23", ObjectUtils.toString(directory), extUserInfo);
		
		AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
		afUserAuthStatusDo.setScene(SceneType.BLD_LOAN.getCode());
		afUserAuthStatusDo.setUserId(userId);
		if (!riskResp.isSuccess()) {
			// 认证失败
			afUserAuthStatusDo.setStatus("C");
		} else {
			// 认证中
			afUserAuthStatusDo.setStatus("A");
		}
		// 新增或修改认证记录
		afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
		resp.setResponseData(data);
		return resp;
	}

	
	private Map<String, Object> getExtUserInfo(DredgeWhiteCollarLoanParam param) {
		Map<String,Object> extUserInfo = Maps.newHashMap();
		extUserInfo.put("companyName", param.getCompany());
		extUserInfo.put("jobPosition", param.getStation());
		extUserInfo.put("companyContact", param.getPhone());
		extUserInfo.put("income", param.getIncome());
		extUserInfo.put("marriage", param.getMaritalStatus());
		return extUserInfo;
	}

}
