package com.ald.fanbei.api.web.api.borrow;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 * @类描述：获取其他支付方式
 * @author fmai 2017年7月11日 15:54
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOtherPayWayApi")
public class GetOtherPayWayApi implements ApiHandle {

	@Resource
	private AfUserAuthService afUserAuthService;

	@Resource
	private AfUserBankcardService afUserBankcardService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserBankcardDo bankInfo = afUserBankcardService.getUserMainBankcardByUserId(userId);
		resp.addResponseData("bankcardStatus", authDo.getBankcardStatus());
		if (!StringUtil.equals(authDo.getBankcardStatus(), YesNoStatus.NO.getCode())) {
			resp.addResponseData("bankId", bankInfo.getRid());
			resp.addResponseData("bankName", bankInfo.getBankName());
			resp.addResponseData("bankCode", bankInfo.getBankCode());
			resp.addResponseData("bankIcon", bankInfo.getBankIcon());
			resp.addResponseData("isValid", bankInfo.getIsValid());
		}
		return resp;
	}

}
