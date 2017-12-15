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
import java.security.MessageDigest;
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
		if( 396 < context.getAppVersion() && context.getAppVersion() <402){
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
		//判断是否需要设置支付密码
		String allowPayPwd = YesNoStatus.YES.getCode();
		if(null != account.getPassword() && !StringUtil.equals("", account.getPassword())){
			allowPayPwd = YesNoStatus.NO.getCode();
		}
		resp.addResponseData("allowPayPwd", allowPayPwd);
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
