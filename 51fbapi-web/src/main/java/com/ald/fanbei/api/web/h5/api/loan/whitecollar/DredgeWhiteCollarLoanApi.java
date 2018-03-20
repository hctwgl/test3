package com.ald.fanbei.api.web.h5.api.loan.whitecollar;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
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
 * 
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
	AfUserAuthStatusService afUserAuthStatusService;

	@Resource
	AfUserAccountSenceService afUserAccountSenceService;

	@Resource
	AfIdNumberService afIdNumberService;

	@Resource
	BizCacheUtil bizCacheUtil;

	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		Long userId = context.getUserId();
		DredgeWhiteCollarLoanParam param = (DredgeWhiteCollarLoanParam) context.getParamEntity();
		// 提交风控审核，获取白领贷额度
		String clientIp = context.getClientIp();
		
		String lockKey = Constants.CACHEKEY_APPLY_BLD_RISK_LOCK + userId;
    	if (bizCacheUtil.getObject(lockKey) == null) {
    	    bizCacheUtil.saveObject(lockKey, lockKey, 30);
    	} else {
    	    throw new FanbeiException(FanbeiExceptionCode.STRONG_RISK_STATUS_ERROR);
    	}
    	
    	try {
    		AfUserBankcardDo mainCard = afUserBankcardService.getUserMainBankcardByUserId(userId);
    		if (mainCard == null) {
    			throw new FanbeiException(FanbeiExceptionCode.USER_MAIN_BANKCARD_NOT_EXIST_ERROR);
    		}
    		String cardNo = mainCard.getCardNumber();
    		String appName = context.getId().startsWith("i") ? "alading_ios" : "alading_and";
    		AfUserDo afUserDo = afUserService.getUserById(userId);
    		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
    		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
    		// 验证所选认证是否通过
    		checkAuthStatus(afUserAuthDo, param);

    		Object directory = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + userId);
    		String riskOrderNo = riskUtil.getOrderNo("loan", cardNo.substring(cardNo.length() - 4, cardNo.length()));

    		Map<String, Object> extUserInfo = getExtUserInfo(param);

    		// 查询户籍地址
    		AfIdNumberDo idNumberInfo = afIdNumberService.getIdNumberInfoByUserId(userId);
    		String censusRegister = StringUtils.EMPTY;
    		if (idNumberInfo != null) {
    			censusRegister = idNumberInfo.getAddress();
    		}

    		RiskRespBo riskResp = riskUtil.dredgeWhiteCollarLoan(ObjectUtils.toString(userId), "ALL", afUserDo,
    				afUserAuthDo, appName, clientIp, accountDo, param.getBlackBox(), cardNo, riskOrderNo,
    				param.getBqsBlackBox(), "23", ObjectUtils.toString(directory), extUserInfo, param.getSelectedType(),
    				param.getAddress(), censusRegister);

    		AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
    		afUserAuthStatusDo.setScene(SceneType.BLD_LOAN.getName());
    		afUserAuthStatusDo.setUserId(userId);
    		AfUserAccountSenceDo bldSenceDo = afUserAccountSenceService.buildAccountScene(userId,
    				LoanType.BLD_LOAN.getCode(), "0");

    		if (!riskResp.isSuccess()) {
    			// 认证失败
    			afUserAuthStatusDo.setStatus("C");
    			bizCacheUtil.delCache(lockKey);
    		} else {
    			// 认证中
    			afUserAuthStatusDo.setStatus("A");
    		}
    		// 新增或修改认证记录
    		afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
    		afUserAccountSenceService.saveOrUpdateAccountSence(bldSenceDo);
    		resp.setResponseData(data);
    	} catch( Exception e) {
    		bizCacheUtil.delCache(lockKey);
    		throw e;
    	}

		return resp;
	}

	private void checkAuthStatus(AfUserAuthDo afUserAuthDo, DredgeWhiteCollarLoanParam param) {
		String selectedType = param.getSelectedType();
		if (StringUtils.equals(selectedType, "fund")) {
			String fundStatus = afUserAuthDo.getFundStatus();
			if (!StringUtils.equals("Y", fundStatus)) {
				throw new FanbeiException(FanbeiExceptionCode.SELECTED_AUTH_TYPE_NOT_PASS);
			}
		} else if (StringUtils.equals(selectedType, "socialsecurity")) {
			String jinponStatus = afUserAuthDo.getJinpoStatus();
			if (!StringUtils.equals("Y", jinponStatus)) {
				throw new FanbeiException(FanbeiExceptionCode.SELECTED_AUTH_TYPE_NOT_PASS);
			}
		} else if (StringUtils.equals(selectedType, "ebank")) {
			String onlinebankStatus = afUserAuthDo.getOnlinebankStatus();
			if (!StringUtils.equals("Y", onlinebankStatus)) {
				throw new FanbeiException(FanbeiExceptionCode.SELECTED_AUTH_TYPE_NOT_PASS);
			}
		}

	}

	private Map<String, Object> getExtUserInfo(DredgeWhiteCollarLoanParam param) {
		Map<String, Object> extUserInfo = Maps.newHashMap();
		extUserInfo.put("companyName", param.getCompany());
		extUserInfo.put("jobPosition", param.getStation());
		extUserInfo.put("companyContact", param.getPhone());
		extUserInfo.put("income", param.getIncome());
		extUserInfo.put("marriage", param.getMaritalStatus());
		return extUserInfo;
	}

}
