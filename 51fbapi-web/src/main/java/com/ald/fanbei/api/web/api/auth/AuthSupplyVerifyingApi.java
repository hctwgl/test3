package com.ald.fanbei.api.web.api.auth;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.SupplyCertifyStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类现描述：补充认证【认证中】设置
 * @author fmai 2017年7月11日 20:45
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authSupplyVerifyingApi")
public class AuthSupplyVerifyingApi implements ApiHandle {

	@Resource
	private AfUserAuthService afUserAuthService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		String authType = ObjectUtils.toString(requestDataVo.getParams().get("authType"));

		AfUserAuthDo authDo = new AfUserAuthDo();
		authDo.setUserId(context.getUserId());
		if (StringUtil.equals("FUND", authType)) {
			authDo.setGmtFund(new Date(System.currentTimeMillis()));
			authDo.setFundStatus(SupplyCertifyStatus.WAIT.getCode());
		} else if (StringUtil.equals("SOCIAL_SECURITY", authType)) {
			authDo.setGmtJinpo(new Date(System.currentTimeMillis()));
			authDo.setJinpoStatus(SupplyCertifyStatus.WAIT.getCode());
		} else if (StringUtil.equals("CREDIT", authType)) {
			authDo.setGmtCredit(new Date(System.currentTimeMillis()));
			authDo.setCreditStatus(SupplyCertifyStatus.WAIT.getCode());
		}
		if (afUserAuthService.updateUserAuth(authDo) > 0) {
			return resp;
		}
		throw new FanbeiException(FanbeiExceptionCode.FAILED);
	}

}
