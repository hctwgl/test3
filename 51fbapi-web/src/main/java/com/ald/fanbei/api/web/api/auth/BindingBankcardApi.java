package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.bo.IPTransferBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.IPTransferUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BuildInfoUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BankcardStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;

/**
 *@类现描述：签约银行卡时短信验证
 *@author hexin 2017年2月28日 下午4:03:21
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("bindingBankcardApi")
public class BindingBankcardApi implements ApiHandle {

	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private CouponSceneRuleEnginerUtil couponSceneRuleEnginerUtil;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfAuthTdService afAuthTdService;
	@Resource
	UpsUtil upsUtil;
	@Resource
	IPTransferUtil iPTransferUtil;
	@Resource
	AfUserBankDidiRiskService afUserBankDidiRiskService;
	@Resource
	AfUserLoginLogService afUserLoginLogService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);


		//新版本绑定银行卡是可以设置支付密码
		String oldPassword = ObjectUtils.toString(requestDataVo.getParams().get("password"),null);
		if(context.getAppVersion()>397){
			if(null != oldPassword){
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				String password = Base64.decodeToString(oldPassword);
				String salt = UserUtil.getSalt();
				String newPwd = UserUtil.getPassword(password, salt);
				afUserAccountDo.setUserId(context.getUserId());
				afUserAccountDo.setSalt(salt);
				afUserAccountDo.setPassword(newPwd);
				afUserAccountService.updateUserAccount(afUserAccountDo);
			}
		}

		return resp;
	}

}
