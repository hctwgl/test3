package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.domain.query.AfUserAuthQuery;

/**
 * @类现描述：
 * @author chenjinhu 2017年2月15日 下午3:09:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAuthService")
public class AfUserAuthServiceImpl implements AfUserAuthService {

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
			// && (null != auth.getGmtMobile() &&
			// DateUtil.beforeDay(auth.getGmtMobile(),
			// DateUtil.addMonths(new Date(), 2)))// 手机运营商认证时间小于两个月
			// && StringUtil.equals(YesNoStatus.YES.getCode(),
			// auth.getContactorStatus())// 紧急联系人
			// && StringUtil.equals(YesNoStatus.YES.getCode(),
			// auth.getLocationStatus())// 定位
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

    public Map<String, Object> getCreditPromoteInfo(Long userId, Date now, AfUserAccountDto userDto, AfUserAuthDo authDo, Integer appVersion, String scene) {
	Map<String, Object> data = new HashMap<String, Object>();
	Map<String, Object> creditModel = new HashMap<String, Object>();
	Map<String, Object> zmModel = new HashMap<String, Object>();
	Map<String, Object> locationModel = new HashMap<String, Object>();
	Map<String, Object> contactorModel = new HashMap<String, Object>();
	long between = 0l;
	AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.creditScoreAmount.getCode());
	// JSONObject json = JSONObject.parseObject(afResourceDo.getValue());
	JSONArray arry = JSON.parseArray(afResourceDo.getValue());
	Integer sorce = userDto.getCreditScore();
	if (!scene.equals(UserAccountSceneType.CASH.getCode())) {
	    AfUserAccountSenceDo afUserAccountSence = afUserAccountSenceService.getByUserIdAndScene(scene, userId);
	    userDto.setAuAmount(afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getAuAmount());
	    userDto.setUsedAmount(afUserAccountSence == null ? BigDecimal.ZERO : afUserAccountSence.getUsedAmount());
	}
	AfResourceDo afResource = afResourceService.getConfigByTypesAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowRiskMostAmount.getCode());
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
	if (StringUtil.equals(authDo.getRealnameStatus(), YesNoStatus.YES.getCode()) && StringUtil.equals(authDo.getZmStatus(), YesNoStatus.NO.getCode())) {
	    String authParamUrl = ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
	    zmModel.put("zmxyAuthUrl", authParamUrl);
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
	// 3.6.7是否显示运营图片
	// if(StringUtil.equals(authDo.getRiskStatus(),
	// RiskStatus.A.getCode())){
	// data.put("isShowImage", "N");
	// }else{
	// data.put("isShowImage", "N");
	// }
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

	data.put("showExtraTab", "1");

	afResourceDo = afResourceService.getSingleResourceBytype("AUTH_STATUS_DESCRIPTION");
	if (!SceneType.CASH.getName().equals(scene)) {
	    data.put("showExtraTab", afResourceDoAuth.getValue());
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
		    if (SceneType.ONLINE.getName().equals(scene))
			data.put("riskRetrialRemind", afResourceDo.getValue3() + "，请" + between + "天后重新提交审核");
		    else {
			data.put("riskRetrialRemind", afResourceDo.getValue4() + "，" + between + "天后可重试");
		    }
		} else if (between == 1) {
		    if (SceneType.ONLINE.getName().equals(scene)) {
			data.put("riskRetrialRemind", afResourceDo.getValue3() + "，明天可以重新提交审核");
			data.put("title2", afResourceDo.getValue3() + "，明天可以重新提交审核");
		    } else {
			data.put("riskRetrialRemind", afResource.getValue4() + "，明天可以重新提交审核");
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
		data.put("highestAmount", afResource.getValue());// 可获取最高额度
		data.put("currentAmount", userDto.getAuAmount());// 当前认证额度
		data.put("useableAmount", userDto.getAuAmount().subtract(userDto.getUsedAmount()));// 剩余可使用额度
		data.put("title2", afResource.getValue1());
		data.put("sceneStatus", "4");// 认证成功
	    } else {
		data.put("basicStatus", "P");
		data.put("riskStatus", "P");
		data.put("flag", "Y");
		data.put("title1", "基础信息认证中");
		data.put("title2", afResource.getValue2());
		data.put("sceneStatus", "6");// 认证中
	    }

	}

	return data;
    }
}
