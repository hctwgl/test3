package com.ald.fanbei.api.web.api.auth;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.web.h5.api.dsed.DsedSmsCodeSubmitApi;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankDidiRiskService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.IPTransferUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：签约银行卡时短信验证
 *@author hexin 2017年2月28日 下午4:03:21
 *@since 4.1.2以下适用，4.1.2以上替换接口见{@link DsedSmsCodeSubmitApi}
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
		if( 396 < context.getAppVersion() && context.getAppVersion() <402&&requestDataVo.getId().startsWith("a")){
			if(null != oldPassword){
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				oldPassword = md5(oldPassword);
				String salt = UserUtil.getSalt();
				String newPwd = UserUtil.getPassword(oldPassword, salt);
				afUserAccountDo.setUserId(context.getUserId());
				afUserAccountDo.setSalt(salt);
				afUserAccountDo.setPassword(newPwd);
				afUserAccountService.updateUserAccount(afUserAccountDo);
			}
		}else {
			if(null != oldPassword){
				AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
				String salt = UserUtil.getSalt();
				String newPwd = UserUtil.getPassword(oldPassword, salt);
				afUserAccountDo.setUserId(context.getUserId());
				afUserAccountDo.setSalt(salt);
				afUserAccountDo.setPassword(newPwd);
				afUserAccountService.updateUserAccount(afUserAccountDo);
			}
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("test", "");
		resp.setResponseData(map);
		return resp;
	}

	public static String md5(String source) {

		StringBuffer sb = new StringBuffer(32);

		try {
			MessageDigest md    = MessageDigest.getInstance("MD5");
			byte[] array        = md.digest(source.getBytes("utf-8"));

			for (int i = 0; i < array.length; i++) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
		} catch (Exception e) {
			logger.error("Can not encode the string '" + source + "' to MD5!", e);
			return null;
		}

		return sb.toString();
	}

}
