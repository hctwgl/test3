/**
 * 
 */
package com.ald.fanbei.api.web.api.user;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsSignReleaseRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年2月22日下午8:57:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("deleteBankCardApi")
public class DeleteBankCardApi implements ApiHandle {
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	UpsUtil upsUtil;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("pwd"), "").toString();
		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (afUserAccountDo == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
		if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {

			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
		}
		Map<String, Object> params = requestDataVo.getParams();
		Long bankId = NumberUtil.objToLongDefault(ObjectUtils.toString(params.get("bankId")), 0);
		
		AfUserBankcardDo bankcardDo=afUserBankcardService.getUserBankInfo(bankId);
		if(YesNoStatus.YES.getCode().equals(bankcardDo.getIsMain())){
			AfUserAuthDo authDo = new AfUserAuthDo();
			authDo.setUserId(context.getUserId());
			authDo.setBankcardStatus(YesNoStatus.NO.getCode());
			afUserAuthService.updateUserAuth(authDo);
		}
		//解绑
		UpsSignReleaseRespBo upsResult = upsUtil.signRelease(userId+"", bankcardDo.getBankCode(), 
				afUserAccountDo.getRealName(), bankcardDo.getMobile(), afUserAccountDo.getIdNumber(), 
				bankcardDo.getCardNumber(), "02");
		if(!upsResult.isSuccess()){
			throw new FanbeiException("sign release error",FanbeiExceptionCode.SIGN_RELEASE_ERROR);
		}
		AfUserBankcardDo afUserBankcardDo = new AfUserBankcardDo();
		afUserBankcardDo.setRid(bankId);
		afUserBankcardDo.setUserId(userId);
		afUserBankcardDo.setStatus(BankcardStatus.UNBIND.getCode());
		
		afUserBankcardService.updateUserBankcard(afUserBankcardDo);
		
		return resp;

	}

}
