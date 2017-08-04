package com.ald.fanbei.api.web.api.auth;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：补充认证页面展示接口
 * 
 * @author fmai 2017年6月6日 10:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authSupplyCertifyApi")
public class AuthSupplyCertifyApi implements ApiHandle {
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
		AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowRiskMostAmount.getCode());
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("fundStatus", afUserAuthDo.getFundStatus()); 
		map.put("socialSecurityStatus", afUserAuthDo.getJinpoStatus());
		map.put("creditStatus", afUserAuthDo.getCreditStatus());
		map.put("alipayStatus", afUserAuthDo.getAlipayStatus());
		map.put("currentAmount", accountDo.getAuAmount());
		map.put("highestAmount", afResourceDo.getValue());
		
		//添加是否已发起过公积金认证，来区分对应状态是初始化还是之前认证失败
		if (afUserAuthDo.getGmtFund() != null) {
			map.put("gmtFundExist", YesNoStatus.YES.getCode());
		} else {
			map.put("gmtFundExist", YesNoStatus.NO.getCode());
		}
		//添加是否已发起过社保认证，来区分对应状态是初始化还是之前认证失败
		if (afUserAuthDo.getGmtJinpo() != null) {
			map.put("gmtSocialSecurityExist", YesNoStatus.YES.getCode());
		} else {
			map.put("gmtSocialSecurityExist", YesNoStatus.NO.getCode());
		}
		//添加是否已发起过信用卡认证，来区分对应状态是初始化还是之前认证失败
		if (afUserAuthDo.getGmtCredit() != null) {
			map.put("gmtCreditExist", YesNoStatus.YES.getCode());
		} else {
			map.put("gmtCreditExist", YesNoStatus.NO.getCode());
		}
		//添加是否已发起过支付宝认证，来区分对应状态是初始化还是之前认证失败
		if (afUserAuthDo.getGmtAlipay() != null) {
			map.put("gmtAlipayExist", YesNoStatus.YES.getCode());
		} else {
			map.put("gmtAlipayExist", YesNoStatus.NO.getCode());
		}
		
		resp.setResponseData(map);
		
		return resp;
	}

}
