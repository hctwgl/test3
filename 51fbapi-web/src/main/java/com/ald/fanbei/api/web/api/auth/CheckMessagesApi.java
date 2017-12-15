package com.ald.fanbei.api.web.api.auth;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.util.UserUtil;
import org.apache.commons.lang.ObjectUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.ald.fanbei.api.biz.bo.IPTransferBo;
import com.ald.fanbei.api.biz.bo.UpsAuthSignValidRespBo;
import com.ald.fanbei.api.biz.service.AfAuthTdService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankDidiRiskService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
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
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserBankDidiRiskDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserLoginLogDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 *@类现描述：签约银行卡时短信验证
 *@author hexin 2017年2月28日 下午4:03:21
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("checkMessagesApi")
public class CheckMessagesApi implements ApiHandle {

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
		String verifyCode = ObjectUtils.toString(requestDataVo.getParams().get("verifyCode"));
		String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"));
		Long bankId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("bankId")), null);
		BigDecimal lat = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("lat"), null);
		BigDecimal lng = NumberUtil.objToBigDecimalDefault(requestDataVo.getParams().get("lng"), null);
		String wifiMac = ObjectUtils.toString(requestDataVo.getParams().get("wifi_mac"));
		String userName = context.getUserName();
		if(null== bankId){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_REGIST_SMS_NOTEXIST);
		}
		AfUserBankcardDo bank = afUserBankcardService.getUserBankcardById(bankId);
		UpsAuthSignValidRespBo upsResult = upsUtil.authSignValid(context.getUserId()+"",bank.getCardNumber(), verifyCode, "02");

		if(!upsResult.isSuccess()){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.AUTH_BINDCARD_ERROR);
		}
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(context.getUserId());

		//绑卡
		bank.setStatus(BankcardStatus.BIND.getCode());
		afUserBankcardService.updateUserBankcard(bank);
		//更新userAuth记录
		if(YesNoStatus.YES.getCode().equals(bank.getIsMain())){
			//实名认证
			AfUserAuthDo authDo = new AfUserAuthDo();
			authDo.setUserId(context.getUserId());
			authDo.setBankcardStatus(YesNoStatus.YES.getCode());
			authDo.setRealnameScore(0);
			authDo.setRealnameStatus(YesNoStatus.YES.getCode());
			authDo.setGmtRealname(new Date());
			afUserAuthService.updateUserAuth(authDo);
			resp.addResponseData("realNameStatus", YesNoStatus.YES.getCode());
			resp.addResponseData("realNameScore", 0);
			//触发邀请人获得奖励规则
			AfUserDo userDo = afUserService.getUserById(context.getUserId());
			if(userDo.getRecommendId() > 0l){
				couponSceneRuleEnginerUtil.realNameAuth(context.getUserId(), userDo.getRecommendId());
			}
		}

		String ipAddress = CommonUtil.getIpAddr(request);
		if (lat == null || lng == null) {
			IPTransferBo bo = iPTransferUtil.parseIpToLatAndLng(ipAddress);
			lat = bo.getLatitude();
			lng = bo.getLongitude();
		}
		if (StringUtils.isEmpty(uuid)) {
			AfUserLoginLogDo loginInfo = afUserLoginLogService.getUserLastLoginInfo(userName);
			uuid = loginInfo.getUuid();
		}
		if(ipAddress == null || StringUtil.isEmpty(ipAddress)) {
			ipAddress = "0";
		}
		AfUserBankDidiRiskDo didiInfo = BuildInfoUtil.buildUserBankDidiRiskInfo(ipAddress, lat, lng, context.getUserId(), bankId, uuid, wifiMac);
		afUserBankDidiRiskService.saveRecord(didiInfo);
		//新版本绑定银行卡是可以设置支付密码
		String oldPassword = ObjectUtils.toString(requestDataVo.getParams().get("password"),null);
		//判断是否需要设置支付密码
		String allowPayPwd = YesNoStatus.YES.getCode();
		if(null != account.getPassword() && !StringUtil.equals("", account.getPassword())){
			allowPayPwd = YesNoStatus.NO.getCode();
		}
		resp.addResponseData("allowPayPwd", allowPayPwd);
		return resp;
	}

}
