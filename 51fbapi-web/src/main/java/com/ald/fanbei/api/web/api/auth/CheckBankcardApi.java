package com.ald.fanbei.api.web.api.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：签约银行卡时短信验证
 *@author hexin 2017年2月28日 下午4:03:21
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkBankcardApi")
public class CheckBankcardApi implements ApiHandle {

	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
		Long bankId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("bankId")), 0);
		AfUserBankcardDo bank = afUserBankcardService.getUserBankcardById(bankId);
		UpsAuthSignValidRespBo upsResult = UpsUtil.authSignValid(context.getUserId()+"",bank.getCardNumber(), verifyCode, "02");
		
		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_ERROR);
		}
		
		//绑卡
		bank.setStatus(BankcardStatus.BIND.getCode());
		afUserBankcardService.updateUserBankcard(bank);
		//更新userAuth记录
		if(YesNoStatus.YES.getCode().equals(bank.getIsMain())){
			AfUserAuthDo authDo = new AfUserAuthDo();
			authDo.setUserId(context.getUserId());
			authDo.setBankcardStatus(YesNoStatus.YES.getCode());
			afUserAuthService.updateUserAuth(authDo);
		}
		//判断是否需要设置支付密码
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(context.getUserId());
		String allowPayPwd = YesNoStatus.YES.getCode();
		if(null != account.getPassword() && !StringUtil.equals("", account.getPassword())){
			allowPayPwd = YesNoStatus.NO.getCode();
		}
		resp.addResponseData("allowPayPwd", allowPayPwd);
		return resp;
	}

}
