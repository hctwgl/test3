package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.bo.RiskQuotaRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.*;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.risk.ReqFromRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromSecondaryRiskBo;
import com.ald.fanbei.api.biz.bo.risk.ReqFromStrongRiskBo;
import com.ald.fanbei.api.biz.bo.risk.RespSecAuthInfoToRiskBo;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类现描述：
 * 
 * @author chenjinhu 2017年2月15日 下午3:09:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAuthService")
public class AfUserAuthServiceImpl implements AfUserAuthService {

	Logger logger = LoggerFactory.getLogger(AfUserAuthServiceImpl.class);
	@Resource
	AfUserAuthDao afUserAuthDao;
	@Resource
	AfUserAccountService afUserAccountService;

	@Resource
	private AfUserBankcardService afUserBankcardService;
	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfIdNumberService afIdNumberService;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	AfAuthRaiseStatusService afAuthRaiseStatusService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	TransactionTemplate transactionTemplate;

	@Override
	public int addUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.addUserAuth(afUserAuthDo);
	}

	@Override
	public int updateUserAuth(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.updateUserAuth(afUserAuthDo);
	}

	@Override
	public int updateUserAuthMobileStatusWait(AfUserAuthDo afUserAuthDo) {
		return afUserAuthDao.updateUserAuthMobileStatusWait(afUserAuthDo);
	}

	@Override
	public AfUserAuthDo getUserAuthInfoByUserId(Long userId) {
		return afUserAuthDao.getUserAuthInfoByUserId(userId);
	}

	@Override
	public String getConsumeStatus(Long userId, Integer appVersion) {
		AfUserAuthDo auth = afUserAuthDao.getUserAuthInfoByUserId(userId);
		AfUserAccountDo account = afUserAccountService.getUserAccountByUserId(userId);
		String status = YesNoStatus.NO.getCode();
		if (account.getAuAmount().compareTo(BigDecimal.ZERO) > 0) {
			if (appVersion == null || appVersion >= 340) {
				if (StringUtil.equals(YesNoStatus.YES.getCode(), auth.getZmStatus())// 芝麻信用已验证
						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getTeldirStatus())// 通讯录匹配状态
						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getMobileStatus())// 手机运营商

						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getRiskStatus())) { // 强风控状态
					status = YesNoStatus.YES.getCode();
				}
			} else {
				if (StringUtil.equals(YesNoStatus.YES.getCode(), auth.getZmStatus())// 芝麻信用已验证
						&& StringUtil.equals(YesNoStatus.YES.getCode(), auth.getTeldirStatus())// 通讯录匹配状态

						) {
					status = YesNoStatus.YES.getCode();
				}
			}

		}
		return status;
	}

	@Override
	public int getUserAuthCountWithIvs_statusIsY() {
		return afUserAuthDao.getUserAuthCountWithIvs_statusIsY();
	}

	@Override
	public List<AfUserAuthDo> getUserAuthListWithIvs_statusIsY(AfUserAuthQuery query) {
		return afUserAuthDao.getUserAuthListWithIvs_statusIsY(query);
	}

	public Map<String, Object> getCreditPromoteInfo(Long userId, Date now, AfUserAccountDto userDto, AfUserAuthDo authDo, Integer appVersion, String scene, AfResourceDo zhimaConfigResource) {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> creditModel = new HashMap<String, Object>();
		Map<String, Object> zmModel = new HashMap<String, Object>();
		Map<String, Object> locationModel = new HashMap<String, Object>();
		Map<String, Object> contactorModel = new HashMap<String, Object>();
		long between = 0l;

		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.creditScoreAmount.getCode());

		JSONArray arry = JSON.parseArray(afResourceDo.getValue());
		Integer sorce = userDto.getCreditScore();
		if (appVersion >= 406) {
			if (!scene.equals(UserAccountSceneType.CASH.getCode())) {
				AfUserAccountSenceDo afUserAccountSence = afUserAccountSenceService.getByUserIdAndScene(scene, userId);
				userDto.setAuAmount(afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getAuAmount());
				userDto.setUsedAmount(afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getUsedAmount());
			} else {
				// FIXME
				AfUserAccountSenceDo loanTotalSenceDo = afUserAccountSenceService.getByUserIdAndScene(SceneType.LOAN_TOTAL.getName(), userId);
				if (loanTotalSenceDo != null) {
					BigDecimal auAmount = loanTotalSenceDo.getAuAmount();
					if (BigDecimal.ZERO.compareTo(auAmount) != 0) {
						userDto.setAuAmount(auAmount);
					}
				}
			}
		} else {
			AfUserAccountSenceDo afUserAccountSence = afUserAccountSenceService.getByUserIdAndScene(UserAccountSceneType.ONLINE.getCode(), userId);
			BigDecimal onlineAuAmount = afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getAuAmount();
			BigDecimal onlineUsedAmount = afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getUsedAmount();
			userDto.setAuAmount(userDto.getAuAmount().add(onlineAuAmount));
			userDto.setUsedAmount(userDto.getUsedAmount().add(onlineUsedAmount));
		}
		AfResourceDo afResource = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowRiskMostAmount.getCode());
		data.put("highestAmount", afResource.getValue());
		int min = Integer.parseInt(afResourceDo.getValue1());// 最小分数
		if (sorce < min) {
			creditModel.put("creditLevel", "信用较差");
		} else {
			for (int i = 0; i < arry.size(); i++) {
				JSONObject obj = arry.getJSONObject(i);
				int minScore = obj.getInteger("minScore");
				int maxScore = obj.getInteger("maxScore");
				String desc = obj.getString("desc");
				if (minScore <= sorce && maxScore > sorce) {
					creditModel.put("creditLevel", desc);
				}
			}
		}

		creditModel.put("creditAssessTime", authDo.getGmtModified());
		creditModel.put("allowConsume", getConsumeStatus(authDo.getUserId(), appVersion));


		zmModel.put("zmStatus", authDo.getZmStatus());
		zmModel.put("zmScore", authDo.getZmScore());
		zmModel.put("isShow", zhimaConfigResource.getValue());
		Date zmReAuthDatetime = DateUtil.parseDateyyyyMMddHHmmss(zhimaConfigResource.getValue4());
		if (zmReAuthDatetime == null) {
			//默认值处理
			zmReAuthDatetime = DateUtil.getStartDate();
		}
		if (YesNoStatus.YES.getCode().equals(zhimaConfigResource.getValue()) && (StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode()) || (StringUtil.equals(authDo.getZmStatus(), YesNoStatus.YES.getCode()) && (authDo.getZmScore() == 0 || DateUtil.compareDate(zmReAuthDatetime, authDo.getGmtZm()))))) {
			String authParamUrl = ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
			AfResourceDo zhimaNewUrl = afResourceService.getSingleResourceBytype("zhimaNewUrl");

			if (zhimaNewUrl == null) {
				zmModel.put("zmxyAuthUrl", authParamUrl);
			} else {
				zmModel.put("zmxyAuthUrl", zhimaNewUrl.getValue() + "?userId=" + AesUtil.encryptToBase64(authDo.getUserId().toString(), "123"));
			}
		} else {
			zmModel.put("zmxyAuthUrl", "");
		}

		//展示给用户的芝麻认证描述文案
		if (StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())) {
			if (YesNoStatus.YES.getCode().equals(authDo.getBasicStatus())) {
				zmModel.put("zmDesc", "重新认证");
			} else {
				zmModel.put("zmDesc", "未认证");
			}
		} else if (authDo.getZmScore() == 0 || DateUtil.compareDate(zmReAuthDatetime, authDo.getGmtZm())) {
			zmModel.put("zmDesc", "重新认证");
		} else {
			if (NumberUtil.objToIntDefault(zhimaConfigResource.getValue1(), 0) == 1) {
				zmModel.put("zmDesc", authDo.getZmScore());
			} else {
				zmModel.put("zmDesc", "已认证");
			}
		}

		locationModel.put("locationStatus", authDo.getLocationStatus());
		locationModel.put("locationAddress", authDo.getLocationAddress());
		contactorModel.put("contactorStatus", authDo.getContactorStatus());
		contactorModel.put("contactorName", authDo.getContactorName());
		contactorModel.put("contactorMobile", authDo.getContactorMobile());
		contactorModel.put("contactorType", authDo.getContactorType());

		data.put("creditModel", creditModel);
		data.put("rrCreditStatus", YesNoStatus.YES.getCode());
		data.put("mobileStatus", authDo.getMobileStatus());
		// 添加是否已发起过运营商认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtMobile() != null) {
			data.put("gmtMobileExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtMobileExist", YesNoStatus.NO.getCode());
		}


		data.put("teldirStatus", authDo.getTeldirStatus());
		data.put("zmModel", zmModel);
		data.put("locationModel", locationModel);
		data.put("contactorModel", contactorModel);
		data.put("realNameStatus", authDo.getRealnameStatus());
		data.put("bankCardStatus", authDo.getBankcardStatus());

		// 3.6.9不在显示图片
		data.put("isShowImage", "N");

		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode())) {
			data.put("riskStatus", RiskStatus.A.getCode());
		} else {
			data.put("riskStatus", authDo.getRiskStatus());
		}
		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.A.getCode())) {
			data.put("flag", "N");
		} else {
			data.put("flag", "Y");
		}
		if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.SECTOR.getCode())) {
			data.put("basicStatus", RiskStatus.A.getCode());
		} else {
			data.put("basicStatus", authDo.getBasicStatus());
		}

		data.put("faceStatus", authDo.getFacesStatus());
		data.put("idNumber", Base64.encodeString(userDto.getIdNumber()));
		data.put("realName", userDto.getRealName());

		if (StringUtil.equals(authDo.getBankcardStatus(), YesNoStatus.YES.getCode())) {
			AfUserBankcardDo afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
			data.put("bankCard", afUserBankcardDo.getCardNumber());
			data.put("phoneNum", afUserBankcardDo.getMobile());
		}

		AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
		if (idNumberDo == null) {
			data.put("isUploadImage", "N");
		} else if (StringUtils.isNotBlank(idNumberDo.getIdFrontUrl()) && StringUtils.isNotBlank(idNumberDo.getIdBehindUrl())) {
			data.put("isUploadImage", "Y");
		} else {
			data.put("isUploadImage", "N");
		}
		if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.NO.getCode())) {
			Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(authDo.getGmtBasic()), 10);
			between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date(System.currentTimeMillis())), afterTenDay);
			if (between > 1) {
				data.put("riskRetrialRemind", "审核不通过，" + between + "天后可重新提交审核");
			} else if (between == 1) {
				data.put("riskRetrialRemind", "审核不通过，明天可以重新提交审核");
			} else {
				data.put("riskRetrialRemind", "可以尝试重新提交啦，完成补充认证可以提高成功率");
			}
		}

		// 信用描述
		AfResourceDo afResourceDoAuth = afResourceService.getSingleResourceBytype("CREDIT_AUTH_STATUS");
		if (StringUtil.equals(authDo.getRealnameStatus(), YesNoStatus.NO.getCode()) || StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode()) || StringUtil.equals(authDo.getMobileStatus(), YesNoStatus.NO.getCode()) || StringUtil.equals(authDo.getTeldirStatus(), YesNoStatus.NO.getCode()) || (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.A.getCode()) && StringUtil.equals(authDo.getBasicStatus(), RiskStatus.A.getCode()))) {
			if (StringUtil.equals(authDo.getBasicStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())) {
				data.put("title1", "你好，" + userDto.getRealName());
				data.put("title2", "请展开基础认证并重新认证芝麻信用，即可立即恢复额度");
			} else {
				data.put("title1", "你好，" + userDto.getRealName());
				data.put("title2", afResourceDoAuth.getValue1());
			}
		} else if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode()) && StringUtil.equals(authDo.getBasicStatus(), RiskStatus.SECTOR.getCode())) {
			data.put("title1", "暂无信用额度");
			data.put("title2", "可以尝试重新提交啦，完成补充认证可以提高成功率");
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.PROCESS.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.PROCESS.getCode())) {// 信用认证页面（认证中状态）
			data.put("title1", "基础信息认证中");
			data.put("title2", "完善补充认证能够增加审核通过率");
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.NO.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.NO.getCode())) {// 信用认证页面（失败状态）
			data.put("title1", "暂无信用额度");
			if (between > 1) {
				data.put("title2", "请" + between + "天后尝试重新提交，完成补充认证可提高成功率");
			} else if (between == 1) {
				data.put("title2", "审核不通过，明天可以重新提交审核");
			} else {
				data.put("title2", "可以尝试重新提交啦，完成补充认证可提高成功率");
			}
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.YES.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			data.put("highestAmount", afResource.getValue());
			data.put("currentAmount", userDto.getAuAmount());
			data.put("title2", "每完成一项补充认证都会提高相应额度");
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.NO.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {// 信用认证页面（基础认证失败状态，补充认证成功状态）
			data.put("highestAmount", afResource.getValue());
			data.put("currentAmount", userDto.getAuAmount());
			if (between > 1) {
				data.put("title2", "请" + between + "天后尝试重新提交，完成补充认证可提高成功率");
			} else if (between == 1) {
				data.put("title2", "审核不通过，明天可以重新提交审核");
			} else {
				data.put("title2", "可以尝试重新提交啦，完成补充认证可提高成功率");
			}
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.SECTOR.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			data.put("highestAmount", afResource.getValue());
			data.put("currentAmount", userDto.getAuAmount());
			data.put("title2", "可以尝试重新提交啦，完成基础认证可大幅度提高你的额度");
		} else if (StringUtil.equals(authDo.getBasicStatus(), RiskStatus.PROCESS.getCode()) && StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			data.put("highestAmount", afResource.getValue());
			data.put("currentAmount", userDto.getAuAmount());
			data.put("title2", "你的基础认证正在审核中，完成基础认证可大幅度提高你的额度");
		}

		// 是否跳轉到H5頁面，這個是為後續做擴展用，暫時還沒有跳轉到H5的需求（以免app發版）
		// NO(不跳)，H5(跳转到H5)，SC(跳转到补充认证)
		String isSkipH5 = "NO";
		if (StringUtil.equals(isSkipH5, "H5")) {
			data.put("h5Url", "");
		}

		if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.A.getCode())) {
			// data.put("url",
			// "https://f.51fanbei.com/test/af8076f9f38a5315.png?currentTime=" +
			// System.currentTimeMillis());
		} else if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.YES.getCode())) {
			data.put("url", "https://f.51fanbei.com/test/b9435048dd27d50e.png?currentTime=" + System.currentTimeMillis());
			isSkipH5 = "SC";
		} else if (StringUtil.equals(authDo.getRiskStatus(), RiskStatus.SECTOR.getCode()) || StringUtil.equals(authDo.getRiskStatus(), RiskStatus.NO.getCode())) {
			data.put("url", "https://f.51fanbei.com/test/d0f2a8be96752d16.png?currentTime=" + System.currentTimeMillis());
			isSkipH5 = "SC";
		}

		data.put("fundStatus", authDo.getFundStatus());
		data.put("socialSecurityStatus", authDo.getJinpoStatus());
		data.put("creditStatus", authDo.getCreditStatus());
		data.put("alipayStatus", authDo.getAlipayStatus());
		data.put("chsiStatus", authDo.getChsiStatus());
		data.put("zhengxinStatus", authDo.getZhengxinStatus());
		data.put("onlinebankStatus", authDo.getOnlinebankStatus());
		AfResourceDo bubbleConfigResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.BUBBLE_AUTH_CONFIG.getCode(), AfResourceSecType.BUBBLE_AUTH_RULE_CONFIG.getCode());
		data.put("isOpenBubbleAuth", bubbleConfigResourceDo.getValue());
		//开启冒泡认证
		if (YesNoStatus.YES.getCode().equals(bubbleConfigResourceDo.getValue())) {
			data.put("bubbleStatus", authDo.getBubbleStatus());
		}
		AfAuthRaiseStatusDo afAuthRaiseStatusDo = new AfAuthRaiseStatusDo();
		afAuthRaiseStatusDo.setUserId(userDto.getUserId());
		List<AfAuthRaiseStatusDo> listRaiseStatus = afAuthRaiseStatusService.getListByCommonCondition(afAuthRaiseStatusDo);
		AfResourceDo authDay = afResourceService.getSingleResourceBytype("SUPPLEMENT_AUTH_DAY");
		AfResourceDo userAuthDay = afResourceService.getSingleResourceBytype("USER_AUTH_DAY");
		if (scene.equals(UserAccountSceneType.ONLINE.getCode())) {
			authDay = afResourceService.getSingleResourceBytype("SUPPLEMENT_AUTH_DAY_ONLINE");
			//有效期一致，不在独立配置
			//userAuthDay = afResourceService.getSingleResourceBytype("USER_AUTH_DAY_ONLINE");
		}
		setAuthRaiseStatus(listRaiseStatus, scene, authDay, userAuthDay, data, authDo, bubbleConfigResourceDo.getValue());

		if (scene.equals(UserAccountSceneType.CASH.getCode())) {
			AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, UserAccountSceneType.BLD_LOAN.getCode());
			data.put("bldRiskStatus", afUserAuthStatusDo == null ? "N" : afUserAuthStatusDo.getStatus());
			// 白领贷强风控是否通过
			if (data.get("bldRiskStatus").equals("Y")) {
				AfResourceDo dialogShow = afResourceService.getSingleResourceBytype("ONLINEBANK_DIALOG_SHOW");
				data.put("onlinebankDialogShow", dialogShow == null ? "N" : dialogShow.getValue());
			}
		}

		addAdditionalStatus(data, authDo, bubbleConfigResourceDo.getValue());
		data.put("showExtraTab", "1");
		data.put("isSkipH5", isSkipH5);

		// 是否有数据失败过期状态
		AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, scene);
		if (afUserAuthStatusDo != null) {
			String causeReason = afUserAuthStatusDo.getCauseReason();
			if (causeReason != null && !"".equals(causeReason)) {
				JSONArray jsonArray = JSON.parseArray(causeReason);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					String auth = jsonObject.getString("auth");
					String status = jsonObject.getString("status");
					if (YesNoStatus.NO.getCode().equals(status)) {
						if ("operator".equals(auth)) {// 运营商状态已过期
							data.put("mobileStatus", "E");
						}
						if ("directory".equals(auth)) {// 通讯录状态已过期
							data.put("teldirStatus", "E");
						}
						if ("fund".equals(auth)) {// 公积金状态已过期
							data.put("fundStatus", "E");
						}
						if ("alipay".equals(auth)) {// 公积金状态已过期
							data.put("alipayStatus", "E");
						}

					}
				}

			}
		}

		afResourceDo = afResourceService.getSingleResourceBytype("AUTH_STATUS_DESCRIPTION");
		if (!SceneType.CASH.getName().equals(scene)) {
			data.put("showExtraTab", "0");
			AfUserAuthStatusDo afUserAuthStatus = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, scene);
			if (afUserAuthStatus == null || UserAuthSceneStatus.NO.getCode().equals(afUserAuthStatus.getStatus()) || UserAuthSceneStatus.PASSING.getCode().equals(afUserAuthStatus.getStatus())) {// 从未认证
				data.put("basicStatus", "A");
				data.put("riskStatus", "A");
				data.put("flag", "N");
				data.put("title1", "你好，" + userDto.getRealName());
				data.put("title2", afResourceDoAuth.getValue1());
				data.put("sceneStatus", "1");// 尚未认证状态
			} else if (UserAuthSceneStatus.FAILED.getCode().equals(afUserAuthStatus.getStatus())) {// 认证失败
				Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(afUserAuthStatus.getGmtModified()), 10);
				between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date(System.currentTimeMillis())), afterTenDay);
				data.put("riskStatus", "N");
				data.put("basicStatus", "N");
				data.put("flag", "Y");
				data.put("title1", "暂无信用额度");
				if (between > 1) {
					data.put("title2", "请" + between + "天后尝试重新提交");
					if (SceneType.ONLINE.getName().equals(scene)) data.put("riskRetrialRemind", afResourceDo.getValue3() + "，请" + between + "天后重新提交审核");
					else {
						data.put("riskRetrialRemind", afResourceDo.getValue4() + "，" + between + "天后可重试");
					}
				} else if (between == 1) {
					if (SceneType.ONLINE.getName().equals(scene)) {
						data.put("riskRetrialRemind", afResourceDo.getValue3() + "，明天可以重新提交审核");
						data.put("title2", afResourceDo.getValue3() + "，明天可以重新提交审核");
					} else {
						data.put("riskRetrialRemind", afResourceDo.getValue4() + "，明天可以重新提交审核");
						data.put("title2", afResourceDo.getValue4() + "，明天可以重新提交审核");
					}
				} else {
					data.put("title2", afResourceDo.getValue());
					data.put("riskRetrialRemind", afResourceDo.getValue());
					data.put("riskStatus", "A");
					data.put("basicStatus", "A");
				}

				data.put("sceneStatus", "3");// 强风控失败
			} else if (UserAuthSceneStatus.YES.getCode().equals(afUserAuthStatus.getStatus())) {
				data.put("basicStatus", "Y");
				data.put("riskStatus", "Y");
				data.put("flag", "Y");
				data.put("highestAmount", afResourceDo.getValue5());// 可获取最高额度
				data.put("currentAmount", userDto.getAuAmount());// 当前认证额度
				data.put("useableAmount", userDto.getAuAmount().subtract(userDto.getUsedAmount()));// 剩余可使用额度
				data.put("title2", afResourceDo.getValue1());
				data.put("sceneStatus", "4");// 认证成功

				// 是否显示补充认证（兼容老版本）
				if (appVersion < 411) {
					data.put("showExtraTab", afResourceDoAuth.getValue());
				} else {
					// 强风控通过则开启
					data.put("showExtraTab", "1");
				}
			} else {
				data.put("basicStatus", "P");
				data.put("riskStatus", "P");
				data.put("flag", "Y");
				data.put("title1", "基础信息认证中");
				data.put("title2", afResourceDo.getValue2());
				data.put("sceneStatus", "6");// 认证中
			}

		}

		return data;
	}

	@Override
	public boolean allBasicAuthPassed(Long userId) {
		AfUserAuthDo authInfo = afUserAuthDao.getUserAuthInfoByUserId(userId);
		return allBasicAuthPassed(authInfo);
	}

	@Override
	public boolean allBasicAuthPassed(AfUserAuthDo authInfo) {
		String bankcardStatus = authInfo.getBankcardStatus();
		String teldirStatus = authInfo.getTeldirStatus();
		String mobileStatus = authInfo.getMobileStatus();
		String zmStatus = authInfo.getZmStatus();
		String realnameStatus = authInfo.getRealnameStatus();

		if (StringUtils.equals("Y", bankcardStatus) && StringUtils.equals("Y", teldirStatus) && StringUtils.equals("Y", mobileStatus) && StringUtils.equals("Y", zmStatus) && StringUtils.equals("Y", realnameStatus)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean allSupplementAuthPassed(Long userId) {
		AfUserAuthDo authInfo = afUserAuthDao.getUserAuthInfoByUserId(userId);
		return allSupplementAuthPassed(authInfo);
	}

	@Override
	public boolean allSupplementAuthPassed(AfUserAuthDo authInfo) {
		String alipayStatus = authInfo.getAlipayStatus();
		String creditStatus = authInfo.getCreditStatus();
		String fundStatus = authInfo.getFundStatus();
		String jinpoStatus = authInfo.getJinpoStatus();
		String zhengxinStatus = authInfo.getZhengxinStatus();
		if (StringUtils.equals("Y", alipayStatus) && StringUtils.equals("Y", creditStatus) && StringUtils.equals("Y", fundStatus) && StringUtils.equals("Y", jinpoStatus) && StringUtils.equals("Y", zhengxinStatus)) {
			return true;
		}
		return false;
	}

	private Map<String, Object> getAuthRaiseStatus(List<AfAuthRaiseStatusDo> listRaiseStatus, AfResourceDo authDay, AfResourceDo userAuthDay, String scene, String auth_type, Date authDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		boolean isExist = false;
		for (AfAuthRaiseStatusDo afAuthRaiseStatusDo : listRaiseStatus) {
			if (afAuthRaiseStatusDo.getPrdType().equals(scene) && afAuthRaiseStatusDo.getAuthType().equals(auth_type)) {
				isExist = true;
			}
			if (afAuthRaiseStatusDo.getPrdType().equals(scene) && afAuthRaiseStatusDo.getAuthType().equals(auth_type) && !afAuthRaiseStatusDo.getRaiseStatus().equals("Y")) {
				data.put("status", "F");
				Integer day = 0;
				JSONArray jsonArray = JSON.parseArray(authDay.getValue());
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					if (obj.getString("type").equals(auth_type)) {
						day = obj.getInteger("day");
					}
				}
				Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(afAuthRaiseStatusDo.getGmtFinish()), day);
				long between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date(System.currentTimeMillis())), afterTenDay);
				if (between > 1) {
					data.put("title", "请" + between + "天后重新认证");
				} else if (between == 1) {
					data.put("title", "明天可以重新认证");
				} else {
					checkUserAuthDay(data, userAuthDay, auth_type, authDate);
				}
			}
		}
//	if (!isExist && !scene.equals(UserAccountSceneType.CASH.getCode())) {
//	    if (checkUserAuthDay(data, userAuthDay, auth_type, authDate)) {
//		data.put("status", "F");
//		data.put("title", "");
//	    }
//	}
		return data;
	}

	public boolean getAuthRaiseStatus(AfAuthRaiseStatusDo afAuthRaiseStatusDo, String scene, String auth_type, Date authDate) {
		Map<String, Object> data = new HashMap<String, Object>();
		AfResourceDo authDay = afResourceService.getSingleResourceBytype("SUPPLEMENT_AUTH_DAY");
		AfResourceDo userAuthDay = afResourceService.getSingleResourceBytype("USER_AUTH_DAY");
		if (scene.equals(UserAccountSceneType.ONLINE.getCode())) {
			authDay = afResourceService.getSingleResourceBytype("SUPPLEMENT_AUTH_DAY_ONLINE");
		}

		if (afAuthRaiseStatusDo == null && !scene.equals(UserAccountSceneType.CASH.getCode())) {
			// 分期场景可以继续验证有效期（现金场景未老用户情况，已经提额不再需要提额）
			return checkUserAuthDay(data, userAuthDay, auth_type, authDate);
		}

		if (afAuthRaiseStatusDo != null) {
			logger.info("afAuthRaiseStatusDo:" + afAuthRaiseStatusDo.toString());
			if ("N".equals(afAuthRaiseStatusDo.getRaiseStatus())) {
				// 验证有效期
				return checkUserAuthDay(data, userAuthDay, auth_type, authDate);
			}
			if ("F".equals(afAuthRaiseStatusDo.getRaiseStatus())) {
				// 验证禁止期
				Integer day = 0;
				JSONArray jsonArray = JSON.parseArray(authDay.getValue());
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject obj = jsonArray.getJSONObject(i);
					if (obj.getString("type").equals(auth_type)) {
						day = obj.getInteger("day");
					}
				}
				Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(afAuthRaiseStatusDo.getGmtFinish()), day);
				long between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date(System.currentTimeMillis())), afterTenDay);
				logger.info("验证禁止期:" + scene + ",afterTenDay:" + afterTenDay + ",between:" + between);
				// 验证有效期
				if (between <= 0) {
					return checkUserAuthDay(data, userAuthDay, auth_type, authDate);
				} else {
					return false;
				}
			}
		}

		return false;
	}

	private boolean checkUserAuthDay(Map<String, Object> data, AfResourceDo userAuthDay, String auth_type, Date authDate) {
		Integer day = 0;
		JSONArray userAuthDayArray = JSON.parseArray(userAuthDay.getValue());
		for (int i = 0; i < userAuthDayArray.size(); i++) {
			JSONObject obj = userAuthDayArray.getJSONObject(i);
			if (obj.getString("type").equals(auth_type)) {
				day = obj.getInteger("day");
				break;
			}
		}
		Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(authDate), day);
		long between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date()), afterTenDay);
		data.put("title", "");
		logger.info("验证有效期:" + auth_type + ",afterTenDay:" + afterTenDay + ",between:" + between);
		if (between < 0) {
			data.put("status", "N");
			return false;
		}
		return true;
	}


	private void setAuthRaiseStatus(List<AfAuthRaiseStatusDo> listRaiseStatus, String scene, AfResourceDo authDay, AfResourceDo userAuthDay, Map<String, Object> data, AfUserAuthDo authDo, String bubbleSwitch) {
		Map<String, Object> supplementAuth = new HashMap<String, Object>();
		if (authDo.getFundStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.FUND.getCode(), authDo.getGmtFund());
			if (supplementAuth.get("status") != null) {
				data.put("fundStatus", supplementAuth.get("status"));
				data.put("fundTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.FUND.getCode(), authDo.getGmtFund());
				if (supplementAuth.get("status") != null) {
					data.put("fundStatus", supplementAuth.get("status"));
					data.put("fundTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getJinpoStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.INSURANCE.getCode(), authDo.getGmtJinpo());
			if (supplementAuth.get("status") != null) {
				data.put("socialSecurityStatus", supplementAuth.get("status"));
				data.put("socialSecurityTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.INSURANCE.getCode(), authDo.getGmtJinpo());
				if (supplementAuth.get("status") != null) {
					data.put("socialSecurityStatus", supplementAuth.get("status"));
					data.put("socialSecurityTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getCreditStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.CARDEMAIL.getCode(), authDo.getGmtCredit());
			if (supplementAuth.get("status") != null) {
				data.put("creditStatus", supplementAuth.get("status"));
				data.put("creditTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.CARDEMAIL.getCode(), authDo.getGmtCredit());
				if (supplementAuth.get("status") != null) {
					data.put("creditStatus", supplementAuth.get("status"));
					data.put("creditTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getAlipayStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.ALIPAY.getCode(), authDo.getGmtAlipay());
			if (supplementAuth.get("status") != null) {
				data.put("alipayStatus", supplementAuth.get("status"));
				data.put("alipayTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.ALIPAY.getCode(), authDo.getGmtAlipay());
				if (supplementAuth.get("status") != null) {
					data.put("alipayStatus", supplementAuth.get("status"));
					data.put("alipayTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getChsiStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.CHSI.getCode(), authDo.getGmtChsi());
			if (supplementAuth.get("status") != null) {
				data.put("chsiStatus", supplementAuth.get("status"));
				data.put("chsiTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.CHSI.getCode(), authDo.getGmtChsi());
				if (supplementAuth.get("status") != null) {
					data.put("chsiStatus", supplementAuth.get("status"));
					data.put("chsiTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getZhengxinStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.ZHENGXIN.getCode(), authDo.getGmtZhengxin());
			if (supplementAuth.get("status") != null) {
				data.put("zhengxinStatus", supplementAuth.get("status"));
				data.put("zhengxinTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.ZHENGXIN.getCode(), authDo.getGmtZhengxin());
				if (supplementAuth.get("status") != null) {
					data.put("zhengxinStatus", supplementAuth.get("status"));
					data.put("zhengxinTitle", supplementAuth.get("title"));
				}
			}
		}
		if (YesNoStatus.YES.getCode().equals(bubbleSwitch) && "Y".equals(authDo.getBubbleStatus())) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.BUBBLE.getCode(), authDo.getGmtBubble());
			if (supplementAuth.get("status") != null) {
				data.put("bubbleStatus", supplementAuth.get("status"));
				data.put("bubbleTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.BUBBLE.getCode(), authDo.getGmtZhengxin());
				if (supplementAuth.get("status") != null) {
					data.put("bubbleStatus", supplementAuth.get("status"));
					data.put("bubbleTitle", supplementAuth.get("title"));
				}
			}
		}
		if (authDo.getOnlinebankStatus().equals("Y")) {
			supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, scene, AuthType.BANK.getCode(), authDo.getGmtOnlinebank());
			if (supplementAuth.get("status") != null) {
				data.put("onlinebankStatus", supplementAuth.get("status"));
				data.put("onlinebankTitle", supplementAuth.get("title"));
			}
			// 如果白领贷强风控通过判断是否提额
			if (SceneType.CASH.getName().equals(scene) && data.get("bldRiskStatus") != null && data.get("bldRiskStatus").equals("Y")) {
				supplementAuth = getAuthRaiseStatus(listRaiseStatus, authDay, userAuthDay, UserAccountSceneType.BLD_LOAN.getCode(), AuthType.BANK.getCode(), authDo.getGmtOnlinebank());
				if (supplementAuth.get("status") != null) {
					data.put("onlinebankStatus", supplementAuth.get("status"));
					data.put("onlinebankTitle", supplementAuth.get("title"));
				}
			}
		}
	}

	private void addAdditionalStatus(Map<String, Object> data, AfUserAuthDo authDo, String bubbleSwitch) {
		// 添加是否已发起过网银认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtOnlinebank() != null) {
			data.put("gmtOnlinebankExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtOnlinebankExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过公积金认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtFund() != null) {
			data.put("gmtFundExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtFundExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过社保认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtJinpo() != null) {
			data.put("gmtSocialSecurityExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtSocialSecurityExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过信用卡认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtCredit() != null) {
			data.put("gmtCreditExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtCreditExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过支付宝认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtAlipay() != null) {
			data.put("gmtAlipayExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtAlipayExist", YesNoStatus.NO.getCode());
		}

		// 添加是否已发起过学信网认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtChsi() != null) {
			data.put("gmtChsiExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtChsiExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过学信网认证，来区分对应状态是初始化还是之前认证失败
		if (authDo.getGmtZhengxin() != null) {
			data.put("gmtZhengxinExist", YesNoStatus.YES.getCode());
		} else {
			data.put("gmtZhengxinExist", YesNoStatus.NO.getCode());
		}
		// 添加是否已发起过冒泡认证，来区分对应状态是初始化还是之前认证失败
		if (YesNoStatus.YES.getCode().equals(bubbleSwitch)) {
			if (authDo.getGmtBubble() != null) {
				data.put("gmtBubbleExist", YesNoStatus.YES.getCode());
			} else {
				data.put("gmtBubbleExist", YesNoStatus.NO.getCode());
			}
		}

	}

	@Override
	public boolean passWhiteList(String userName) {
		List<String> whiteIdsList = new ArrayList<String>();

		AfResourceDo whiteListInfo = afResourceService.getSingleResourceBytype(Constants.APPLY_BRROW_CASH_WHITE_LIST);
		if (whiteListInfo != null) {
			whiteIdsList = CollectionConverterUtil.convertToListFromArray(whiteListInfo.getValue3().split(","), new Converter<String, String>() {
				@Override
				public String convert(String source) {
					return source.trim();
				}
			});
		}

		return whiteIdsList.contains(userName);
	}


    /* ---------------------------------
	 * start 此区域内 处理风控主动调用       |
	 * --------------------------------- */

	/**
	 * 处理来自风控主动推送的强风控回调
	 */
	@Override
	public void dealFromStrongRiskForcePush(final ReqFromStrongRiskBo reqBo) {
		final Long userId = reqBo.consumerNo;

		if (afBorrowCashService.haveDealingBorrowCash(userId)) {
			throw new FanbeiException("ConsumerNo=" + userId + " have deal borrow cash");
		}

		if (StringUtils.equals("10", reqBo.result)) {
			if (SceneType.CASH.getCode().equals(reqBo.scene)) { // 认证通过
				transactionTemplate.execute(new TransactionCallback<Boolean>() {
					public Boolean doInTransaction(TransactionStatus arg0) {
						logger.info("dealFromStrongRiskForcePush start totalAmount =" + reqBo.totalAmount.toString() + ",userId=" + userId + ",loanType=LOAN_TOTAL");
						AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, "LOAN_TOTAL", reqBo.totalAmount.toString());
						afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);

						updateRiskStatus(RiskStatus.YES, userId);

						AfUserAccountDo accountDo = new AfUserAccountDo();
						accountDo.setUserId(userId);
						accountDo.setAuAmount(reqBo.amount);
						afUserAccountService.updateUserAccount(accountDo);
						return true;
					}
				});
			}
		} else if (StringUtils.equals("30", reqBo.result)) {   // 认证未通过
			if (SceneType.CASH.getCode().equals(reqBo.scene)) {
				this.updateRiskStatus(RiskStatus.NO, userId);
			}
		}
	}

	@Override
	public void dealFromSecondaryRiskForcePush(final ReqFromSecondaryRiskBo reqBo) {
		final Long userId = reqBo.consumerNo;

		if (afBorrowCashService.haveDealingBorrowCash(userId)) {
			throw new FanbeiException("ConsumerNo=" + userId + " have deal borrow cash");
		}
		for (RiskQuotaRespBo.Result result : reqBo.results) {
			final String authType = result.getScene();
			final AuthType at = RiskSceneAuthType.findByCode(authType);
			final String raiseStatus = result.getResult();

			transactionTemplate.execute(new TransactionCallback<Boolean>() {
				public Boolean doInTransaction(TransactionStatus arg0) {
					if (StringUtils.equals(RiskRaiseResult.PASS.getCode(), raiseStatus)) {
						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
						afUserAccountDo.setUserId(userId);
						afUserAccountDo.setAuAmount(reqBo.amount);
						afUserAccountService.updateUserAccount(afUserAccountDo);

						AfUserAccountSenceDo totalAccountSenceDo = afUserAccountSenceService.buildAccountScene(userId, SceneType.LOAN_TOTAL.getName(), reqBo.totalAmount.toEngineeringString());
						afUserAccountSenceService.saveOrUpdateAccountSence(totalAccountSenceDo);

						AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, authType, LoanType.CASH.getCode(), "Y", reqBo.amount, new Date());
						afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

						updateSecAuthStatus(userId, at, SecAuthStatus.YES);
					} else {
						AfAuthRaiseStatusDo raiseStatusDo = afAuthRaiseStatusService.buildAuthRaiseStatusDo(userId, reqBo.results[0].getScene(), LoanType.CASH.getCode(), "F", BigDecimal.ZERO, new Date());
						afAuthRaiseStatusService.saveOrUpdateRaiseStatus(raiseStatusDo);

						updateSecAuthStatus(userId, at, SecAuthStatus.YES);
					}
					return true;
				}
			});
		}

	}

	@Override
	public RespSecAuthInfoToRiskBo getSecondaryAuthInfo(ReqFromRiskBo reqBo) {
		AfUserAuthDo info = getUserAuthInfoByUserId(reqBo.consumerNo);
		if (info == null) {
			throw new FanbeiException("None exist consumerNo = " + reqBo.consumerNo);
		}
		RespSecAuthInfoToRiskBo resp = new RespSecAuthInfoToRiskBo();
		resp.isAlipayAuthed = !SecAuthStatus.INIT.getCode().equals(info.getAlipayStatus());
		resp.isBankAuthed = !SecAuthStatus.INIT.getCode().equals(info.getOnlinebankStatus());
		resp.isCardEmailAuthed = !SecAuthStatus.INIT.getCode().equals(info.getCreditStatus());
		resp.isChsiAuthed = !SecAuthStatus.INIT.getCode().equals(info.getChsiStatus());
		resp.isFundAuthed = !SecAuthStatus.INIT.getCode().equals(info.getFundStatus());
		resp.isInsuranceAuthed = !SecAuthStatus.INIT.getCode().equals(info.getJinpoStatus());
		resp.isZhengxinAuthed = !SecAuthStatus.INIT.getCode().equals(info.getZhengxinStatus());
		return resp;
	}


	@Override
	public void dealRaiseQuota(AfUserAuthDo afUserAuthDo, JSONObject dataObj) {
		try {
			String riskStatus = dataObj.getString("riskStatus");

			String secAlipayStatus = dataObj.getString("secAlipayStatus");
			String secBankStatus = dataObj.getString("secBankStatus");
			String secCardEmailStatus = dataObj.getString("secCardEmailStatus");
			String secChsiStatus = dataObj.getString("secChsiStatus");
			String secFundStatus = dataObj.getString("secFundStatus");
			String secInsuranceStatus = dataObj.getString("secInsuranceStatus");
			String secZhengxinStatus = dataObj.getString("secZhengxinStatus");

			this.checkStatusLegality(riskStatus, secAlipayStatus, secBankStatus, secCardEmailStatus, secChsiStatus, secFundStatus, secInsuranceStatus, secZhengxinStatus);

			afUserAuthDo.setRiskStatus(riskStatus);

			afUserAuthDo.setAlipayStatus(secAlipayStatus);
			afUserAuthDo.setOnlinebankStatus(secBankStatus);
			afUserAuthDo.setCreditStatus(secCardEmailStatus);
			afUserAuthDo.setChsiStatus(secChsiStatus);
			afUserAuthDo.setFundStatus(secFundStatus);
			afUserAuthDo.setJinpoStatus(secInsuranceStatus);
			afUserAuthDo.setZhengxinStatus(secZhengxinStatus);

			updateUserAuth(afUserAuthDo);
		} catch (Exception e) {
			logger.error("dealRaiseQuota error, consumerNo=" + afUserAuthDo.getUserId() + "dataObj=" + dataObj.toJSONString() + "," + e.getMessage(), e);
		}
	}

	private void checkStatusLegality(String... statuses) {
		for (String status : statuses) {
			if (SecAuthStatus.YES.getCode().equals(status) || SecAuthStatus.NO.getCode().equals(status)) {
			} else {
				throw new FanbeiException("Illegal status value = " + status);
			}
		}
	}

	private void updateRiskStatus(RiskStatus status, Long userId) {
		AfUserAuthDo authDo = new AfUserAuthDo();
		Date cur = new Date(System.currentTimeMillis());
		authDo.setUserId(userId);
		authDo.setRiskStatus(status.getCode());
		authDo.setBasicStatus(status.getCode());
		authDo.setGmtBasic(cur);
		authDo.setGmtRisk(cur);
		updateUserAuth(authDo);
	}

	private void updateSecAuthStatus(Long userId, AuthType authType, SecAuthStatus secAuthstatus) {
		Date cur = new Date();
		AfUserAuthDo afUserAuthDo = new AfUserAuthDo();
		afUserAuthDo.setUserId(userId);

		if (AuthType.ALIPAY.equals(authType)) {
			afUserAuthDo.setAlipayStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtAlipay(cur);
		} else if (AuthType.BANK.equals(authType)) {
			afUserAuthDo.setOnlinebankStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtCredit(cur);
		} else if (AuthType.CARDEMAIL.equals(authType)) {
			afUserAuthDo.setCreditStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtCredit(cur);
		} else if (AuthType.CHSI.equals(authType)) {
			afUserAuthDo.setChsiStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtChsi(cur);
		} else if (AuthType.FUND.equals(authType)) {
			afUserAuthDo.setFundStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtFund(cur);
		} else if (AuthType.INSURANCE.equals(authType)) {
			afUserAuthDo.setJinpoStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtJinpo(cur);
		} else if (AuthType.ZHENGXIN.equals(authType)) {
			afUserAuthDo.setZhengxinStatus(secAuthstatus.getCode());
			afUserAuthDo.setGmtZhengxin(cur);
		} else {
			throw new FanbeiException("Illegal sec auth type=" + authType);
		}

		afUserAuthDo.setRiskStatus(RiskStatus.YES.getCode());
		updateUserAuth(afUserAuthDo);
	}
	/* ---------------------------------
	 * end 此区域内处理风控主动调用   		|
	 * --------------------------------- */


    /**
     * 签到领金币 获取用户层级
     * @param userId
     * @return
     */
    public List<Integer> signRewardUserLevel(Long userId,HashMap<String,Object> hashMap) {
        String loyalUsers;
        String ordinaryUser;
        String specialUser;
        String newUser;
        //用户层级
        int count = afOrderService.getFinishOrderCount(userId);
        //是否是忠实用户(count超过二次)
        if (count > 2) {
            loyalUsers = "Y";
        } else {
            loyalUsers = "N";
        }
        //是否是购物一次用户(count等于1次)
        if (count == 1) {
            ordinaryUser = "Y";
        } else {
            ordinaryUser = "N";
        }
        //消费分期强风控是否通过用户
        String onLicneStatus = riskOnline(hashMap);
        //消费分期强风控是否通过用户而且未购物
        if (StringUtil.equals("Y", onLicneStatus) && count == 0) {
            specialUser = "Y";
        } else {
            specialUser = "N";
        }
        //是否是新用户
        if (count > 0 || StringUtil.equals("Y", onLicneStatus)) {
            newUser = "N";
        } else {
            if (hashMap != null) {
                if (hashMap.get("gmtFaces") == null && StringUtil.equals("N", hashMap.get("bankcardStatus")+"") && hashMap.get("gmtRealname") == null && StringUtil.equals("N", hashMap.get("realnameStatus")+"") && StringUtil.equals("N", hashMap.get("facesStatus")+"")) {
                    newUser = "Y";
                } else {
                    newUser = "N";
                }
            } else {
                newUser = "N";
            }
        }

        List<Integer> list = new ArrayList<>();
		list.add(0);
        if (newUser.equals("Y")) {
			list.add(1);
        }
        if (onLicneStatus.equals("Y")) {
            list.add(2);
        }
        if (ordinaryUser.equals("Y")) {
			list.add(3);
        }
        if (loyalUsers.equals("Y")) {
			list.add(4);
        }
        if (specialUser.equals("Y")) {
			list.add(5);
        }
        return list;
    }



    private String riskOnline(HashMap<String,Object> hashMap){
        String flag ;
        if(hashMap != null){
            if(hashMap.get("status").equals("Y")){
                flag = "Y";
            }else{
                flag = "N";
            }
        }else{
            flag = "N";
        }
        return flag;
    }

    @Override
	public HashMap<String,Object> getUserAuthInfo(Long userId){
    	return afUserAuthDao.getUserAuthInfo(userId);
	}

}
