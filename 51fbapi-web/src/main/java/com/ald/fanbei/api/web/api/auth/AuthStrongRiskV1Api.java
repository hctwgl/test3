package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.math.BigDecimal;
import java.util.*;

/**
 * @类描述：提交风控审核
 * 
 * @author chefeipeng 2017年11月6日 10:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("authStrongRiskV1Api")
public class AuthStrongRiskV1Api implements ApiHandle {

	@Resource
	RiskUtil riskUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfIdNumberService afIdNumberService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfCouponSceneService afCouponSceneService;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfGameService afGameService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfGameConfService afGameConfService;
	@Resource
	AfGameResultService afGameResultService;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;

	@Resource
	private CouponSceneRuleEnginerUtil couponSceneRuleEnginerUtil;

	@Resource
	AfRecommendUserService afRecommendUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		Integer appVersion = context.getAppVersion();

    	String lockKey = Constants.CACHEKEY_APPLY_STRONG_RISK_LOCK + userId;
    	if (bizCacheUtil.getObject(lockKey) == null) {
    	    bizCacheUtil.saveObject(lockKey, lockKey, 30);
    	} else {
    	    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.STRONG_RISK_STATUS_ERROR);
    	}

		logger.info("authStrongRiskV1Api requestDataVo:"+requestDataVo.toString());
		//认证场景 20（现金），21（线上），22（线下）
		String scene = ObjectUtils.toString(requestDataVo.getParams().get("scene"));//场景
		boolean numberOfAuth = false;//是否是新手引导过来
		if(StringUtils.isBlank(scene))
		{//兼容老版本认证多个场景
			numberOfAuth= true;
			scene = "CASH,ONLINE";
		}
		String[] sceneArray = scene.split(",");
		String riskScene = "20";
		if (sceneArray.length > 1) {
			numberOfAuth = true;
		}
		//用户认证信息场景
		AfUserAuthStatusDo afUserAuthStatusDo = new AfUserAuthStatusDo();
		afUserAuthStatusDo.setUserId(userId);
		afUserAuthStatusDo.setScene(sceneArray[0]);
		riskScene = getRiskScene(sceneArray[0]);

		String riskCheckStatus = "0";
		AfUserAuthStatusDo afUserAuthStatus = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, scene);
        	if (afUserAuthStatus == null || !UserAuthSceneStatus.PASSING.getCode().equals(afUserAuthStatus.getStatus())) {
        	    JSONObject riskCheckData = riskUtil.authDataCheck(userId, riskScene);
        	    riskCheckStatus = riskCheckData.getString("success");
        	    // 有数据过期了
        	    if ("55".equals(riskCheckStatus)) {
        		ApiHandleResponse apiHandleResponse = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILURE_DATA_ERROR);
        		String failureData = riskCheckData.get("failureData").toString();
        		String[] failureDataArray = failureData.split(",");
        		JSONArray jsonArray = new JSONArray();
        		for (int i = 0; i < failureDataArray.length; i++) {
        		    JSONObject jsonObject = new JSONObject();
        		    jsonObject.put("auth", failureDataArray[i]);
        		    jsonObject.put("status", "N");// 失效状态
        		    jsonArray.add(jsonObject);
        		}
        		afUserAuthStatusDo.setStatus(UserAuthSceneStatus.NO.getCode());
        		afUserAuthStatusDo.setCauseReason(jsonArray.toString());
        		afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
        		bizCacheUtil.delCache(lockKey);
        		return apiHandleResponse;
        	    }
        	}

		//调用风控失败了
		if (!"0".equals(riskCheckStatus) && !"55".equals(riskCheckStatus)) {
    			bizCacheUtil.delCache(lockKey);
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CALL_RISK_FAIL);
		}

		//try {
			AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);

			if (StringUtils.equals(afUserAuthDo.getZmStatus(), YesNoStatus.NO.getCode())) {// 请先完成芝麻信用授权
	        		bizCacheUtil.delCache(lockKey);
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.ZHIMA_CREDIT_INFO_EXIST_ERROR);
			}
			if (!StringUtils.equals(afUserAuthDo.getMobileStatus(), YesNoStatus.YES.getCode())) {// 请先完成运营商授权
	        		bizCacheUtil.delCache(lockKey);
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.OPERATOR_INFO_EXIST_ERROR);
			}
			if (appVersion < 368 && StringUtils.equals(afUserAuthDo.getTeldirStatus(), YesNoStatus.NO.getCode())) {// 请先完成紧急联系人设置
	        		bizCacheUtil.delCache(lockKey);
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.EMERGENCY_CONTACT_INFO_EXIST_ERROR);
			}

                	if (SceneType.CASH.getCode().equals(riskScene)) {
                	    if (!StringUtils.equals(afUserAuthDo.getBasicStatus(), RiskStatus.A.getCode()) && !StringUtils.equals(afUserAuthDo.getBasicStatus(), RiskStatus.SECTOR.getCode())) {
                		bizCacheUtil.delCache(lockKey);
                		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RISK_OREADY_FINISH_ERROR);
                	    }
                	} else {
                	    if (afUserAuthStatus != null) {
                		if (StringUtils.equals(afUserAuthStatus.getStatus(), UserAuthSceneStatus.CHECKING.getCode())) {
                		    bizCacheUtil.delCache(lockKey);
                		    return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.RISK_OREADY_FINISH_ERROR);
                		} else if (StringUtils.equals(afUserAuthStatus.getStatus(), UserAuthSceneStatus.FAILED.getCode())) {
                		    Date afterTenDay = DateUtil.addDays(DateUtil.getEndOfDate(afUserAuthStatus.getGmtModified()), 10);
                		    long between = DateUtil.getNumberOfDatesBetween(DateUtil.getEndOfDate(new Date(System.currentTimeMillis())), afterTenDay);
                
                		    if (between > 1) {
                			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR, "请" + between + "天后尝试重新提交");
                
                		    } else if (between == 1) {
                			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR, "明天可以重新提交审核");
                		    }
                		}
                	    }
                	}

			Object directoryCache = bizCacheUtil.getObject(Constants.CACHEKEY_USER_CONTACTS + userId);
			if (directoryCache == null) {
	        		bizCacheUtil.delCache(lockKey);
				if (appVersion < 368) {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CANOT_FIND_DIRECTORY_ERROR);// 没取到通讯录时，让用户重新设置紧急联系人
				} else {
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NEED_AGAIN_DIRECTORY_PROOF_ERROR);// 没取到通讯录时，让用户重新社交认证
				}
			}

			AfUserBankcardDo card = afUserBankcardService.getUserMainBankcardByUserId(userId);

			AfIdNumberDo idNumberDo = afIdNumberService.selectUserIdNumberByUserId(userId);
			if (idNumberDo == null) {
	        		bizCacheUtil.delCache(lockKey);
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_CARD_INFO_EXIST_ERROR);
			} else {
				AfUserDo afUserDo = afUserService.getUserById(userId);
				String appName = (requestDataVo.getId().startsWith("i") ? "alading_ios" : "alading_and");
				String ipAddress = CommonUtil.getIpAddr(request);
				AfUserAccountDto accountDo = afUserAccountService.getUserAndAccountByUserId(userId);

				String cardNo = card.getCardNumber();
				String riskOrderNo = riskUtil.getOrderNo("regi", cardNo.substring(cardNo.length() - 4, cardNo.length()));
				AfUserAuthDo authDo = new AfUserAuthDo();
				authDo.setUserId(context.getUserId());
				authDo.setGmtRisk(new Date());
				authDo.setGmtBasic(new Date());
				try {
				        String directory = directoryCache.toString();
				    
					RiskRespBo riskResp = riskUtil.registerStrongRiskV1(idNumberDo.getUserId() + "", "ALL", afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox,
							card.getCardNumber(), riskOrderNo, bqsBlackBox, riskScene,directory);

					if (!riskResp.isSuccess()) {
						return processRishAuthFail(requestDataVo.getId(), riskScene, authDo ,afUserAuthDo, afUserAuthStatusDo);
					} else {						
						if (!StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.YES.getCode())) {
							authDo.setRiskStatus(RiskStatus.PROCESS.getCode());
						}
						if (SceneType.CASH.getCode().equals(riskScene)) {
							processRishComplete(afUserAuthDo, requestDataVo, context, request, userId, idNumberDo, resp);
							afUserAuthService.updateUserAuth(authDo);
						} else {
							afUserAuthStatusDo.setGmtModified(new Date());
							afUserAuthStatusDo.setStatus(UserAuthSceneStatus.CHECKING.getCode());
							afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
						}
					}

					if (numberOfAuth) {//新手引导过来的二次调用
						riskScene = getRiskScene(sceneArray[1]);
						afUserAuthStatusDo.setScene(sceneArray[1]);
						riskOrderNo = riskUtil.getOrderNo("regi", cardNo.substring(cardNo.length() - 4, cardNo.length()));
						RiskRespBo riskResp1 = riskUtil.registerStrongRiskV1(idNumberDo.getUserId() + "", "ALL", afUserDo, afUserAuthDo, appName, ipAddress, accountDo, blackBox,
								card.getCardNumber(), riskOrderNo, bqsBlackBox, riskScene,directory);

						if (!riskResp1.isSuccess()) {
							return processRishAuthFail(requestDataVo.getId(), riskScene,authDo, afUserAuthDo, afUserAuthStatusDo);
						} else {
							if (!StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.YES.getCode())) {
								authDo.setRiskStatus(RiskStatus.PROCESS.getCode());
							}
							if (SceneType.CASH.getCode().equals(riskScene)) {
								processRishComplete(afUserAuthDo, requestDataVo, context, request, userId, idNumberDo, resp);
								afUserAuthService.updateUserAuth(authDo);
							} else {
								afUserAuthStatusDo.setGmtModified(new Date());
								afUserAuthStatusDo.setStatus(UserAuthSceneStatus.CHECKING.getCode());
								afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
							}
						}
					}
				} catch (Exception e) {
					if (!StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.YES.getCode())) {
						authDo.setRiskStatus(RiskStatus.A.getCode());
					}
					authDo.setBasicStatus(RiskStatus.A.getCode());
					for (int i = 0; i < sceneArray.length; i++) {
						if (SceneType.CASH.getName().equals(sceneArray[i])) {
							afUserAuthService.updateUserAuth(authDo);
						}
					}
					logger.error("提交用户认证信息到风控失败,场景" + idNumberDo.getUserId() + "," + scene);
		        		bizCacheUtil.delCache(lockKey);
					throw new FanbeiException(FanbeiExceptionCode.RISK_REGISTER_ERROR, e);
				}
				return resp;
			}
//		} finally {
//			bizCacheUtil.delCache(lockKey);
//		}

	}

	private  void processRishComplete(AfUserAuthDo afUserAuthDo,RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request,Long userId, AfIdNumberDo idNumberDo,ApiHandleResponse resp){

	 // 提交过信用认证,第一次给用户发放优惠劵
	 		HashMap<String, String> creditRebateMap = new HashMap<String, String>();
	 		String creditRebateMsg = "";
	 		//List<AfUserAuthStatusDo> afUserAuthStatusDoSuccess = afUserAuthStatusService.selectAfUserAuthStatusByUserIdAndStatus(userId, UserAuthSceneStatus.YES.getCode());
	 		//if (afUserAuthStatusDoSuccess != null && afUserAuthStatusDoSuccess.size() > 0) {//判断是否有场景认证通过了,第一次通过发奖品
	 			if (afUserAuthDo.getRiskStatus().equals(RiskStatus.A.getCode())) {
	 				// 发放优惠劵工作
	 				// creditRebateMsg = getCreditAuthMsg(context,
	 				// creditRebateMsg);
	 			         logger.info("processRishComplete userId = " + userId);
	 				 couponSceneRuleEnginerUtil.creditAuth(context.getUserId());
	 				// 随机发放奖品
	 				try {
	 					Map<String, Object> prizeInfo = getAuthPrize(requestDataVo, context, request);
	 					if (prizeInfo != null) {
	 						creditRebateMsg = (String) prizeInfo.get("prizeName");
	 					}
	 				} catch (Exception e) {
	 					// ignore error
	 					logger.error("getAuthPrize=>" + e.getMessage());
	 				}

	 				// #region 新增需求 实名认证成功后 给钱10块钱给推荐人
	 				afRecommendUserService.updateRecommendCash(userId);
	 				// #endregion
	 			}
	 		//}

	 		bizCacheUtil.delCache(Constants.CACHEKEY_USER_CONTACTS + idNumberDo.getUserId());
	 		if (context.getAppVersion() > 367) {
	 			creditRebateMap.put("creditRebateMsg", creditRebateMsg);
	 			resp.setResponseData(creditRebateMap);
	 		}
	}

	private ApiHandleResponse processRishAuthFail(String requestId, String riskScene, AfUserAuthDo authDo ,AfUserAuthDo afUserAuthDo, AfUserAuthStatusDo afUserAuthStatusDo)
	{
		if (SceneType.CASH.getCode().equals(riskScene)) {
			if (!StringUtil.equals(afUserAuthDo.getRiskStatus(), RiskStatus.YES.getCode())) {
				authDo.setBasicStatus(RiskStatus.A.getCode());
				afUserAuthService.updateUserAuth(authDo);
			}
		} else {
			if (!StringUtil.equals(afUserAuthStatusDo.getStatus(), UserAuthSceneStatus.YES.getCode())) {
				afUserAuthStatusDo.setGmtModified(new Date());
				afUserAuthStatusDo.setStatus(UserAuthSceneStatus.FAILED.getCode());
				afUserAuthStatusService.addOrUpdateAfUserAuthStatus(afUserAuthStatusDo);
			}
		}
		return new ApiHandleResponse(requestId, FanbeiExceptionCode.RISK_REGISTER_ERROR);
	}

	private String getRiskScene(String name)
	{
		if(SceneType.CASH.getName().equals(name)){
			return SceneType.CASH.getCode();
		}else if(SceneType.ONLINE.getName().equals(name)){
			return SceneType.ONLINE.getCode();
		}else if(SceneType.TRAIN.getName().equals(name)){
			return SceneType.TRAIN.getCode();
		}else{
			return SceneType.CASH.getCode();
		}
	}

	private Map<String, Object> getAuthPrize(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		Long userId = context.getUserId();

		Map<String, Object> data = new HashMap<String, Object>();
		// 获取拆红包游戏信息
		AfGameDo gameDo = afGameService.getByCode("tear_packet");
		if (gameDo == null) {
			throw new FanbeiException(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
		}
		// 判断活动时间
		Date startDate = gameDo.getGmtStart();
		Date endDate = gameDo.getGmtEnd();
		Date nowDate = new Date();
		if (nowDate.before(startDate) || nowDate.after(endDate)) {
			return null;
		}
		// 获取游戏配置信息
		List<AfGameConfDo> afGameConfList = afGameConfService.getByGameId(gameDo.getRid());
		if (afGameConfList == null || afGameConfList.size() == 0) {
			throw new FanbeiException(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
		}
		AfGameConfDo afGameConfDo = afGameConfList.get(0);
		String rules = afGameConfDo.getRule();
		// 按照概率抽奖
		JSONArray array = JSON.parseArray(rules);
		JSONObject item1 = array.getJSONObject(0);
		JSONArray prizeArray = item1.getJSONArray("prize");
		List<Map<String, Object>> awardList = new ArrayList<Map<String, Object>>();

		int totalRate = 0;
		for (int i = 0; i < prizeArray.size(); i++) {
			Map<String, Object> prizeMap = new HashMap<String, Object>();
			JSONObject prize = prizeArray.getJSONObject(i);
			String prizeType = prize.getString("prize_type");
			String rate = prize.getString("rate");
			String prizeId = prize.getString("prize_id");
			prizeMap.put("prizeType", prizeType);
			prizeMap.put("rate", rate);
			prizeMap.put("prizeId", prizeId);
			awardList.add(prizeMap);
			totalRate += Integer.parseInt(rate);
		}
		int result = new Random().nextInt(totalRate) + 1;
		// 判断中奖的是哪个奖品
		int startRate = 0;
		int endRate = 0;
		// 中奖奖品信息
		Map<String, Object> winPrizeInfo = null;
		for (Map<String, Object> awardInfo : awardList) {
			startRate = endRate;
			String rate = (String) awardInfo.get("rate");
			endRate += Integer.parseInt(rate);
			if (startRate < result && result <= endRate) {
				winPrizeInfo = awardInfo;
				logger.info("TearPacketApi winPrizeInfo=>" + winPrizeInfo);
				break;
			}
		}
		String couponId = (String) winPrizeInfo.get("prizeId");
		// 获取用户信息
		AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
		// 添加抽奖结果信息
		AfGameResultDo afGameResultDo = afGameResultService.addGameResult(gameDo.getRid(), userInfo, "Auth", Long.parseLong(couponId), "Y");
		String prizeType = (String) winPrizeInfo.get("prizeType");
		if ("BOLUOMI".equals(prizeType)) {
			// 发送菠萝蜜优惠券
			Long sceneId = Long.parseLong(couponId);
			logger.info(" pickBoluomeCoupon begin , sceneId = {}, userId = {}", sceneId, userId);
			if (sceneId == null) {
				throw new FanbeiException(FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
			}
			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(sceneId);
			if (resourceInfo == null) {
				logger.error("couponSceneId is invalid");
				throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
			}
			data.put("prizeName", resourceInfo.getName());
			data.put("prizeType", "BOLUOMI");

			PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
			bo.setUser_id(userId + StringUtil.EMPTY);

			Date gmtStart = DateUtil.parseDate(resourceInfo.getValue1(), DateUtil.DATE_TIME_SHORT);
			Date gmtEnd = DateUtil.parseDate(resourceInfo.getValue2(), DateUtil.DATE_TIME_SHORT);

			if (DateUtil.beforeDay(new Date(), gmtStart)) {
				throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_NOT_START);
			}
			if (DateUtil.afterDay(new Date(), gmtEnd)) {
				throw new FanbeiException(FanbeiExceptionCode.PICK_BRAND_COUPON_DATE_END);
			}
			String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
			logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
			JSONObject resultJson = JSONObject.parseObject(resultString);
			if (!"0".equals(resultJson.getString("code"))) {
				throw new FanbeiException(resultJson.getString("msg"));
			} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0) {
				throw new FanbeiException("仅限领取一次，请勿重复领取！");
			}
		} else {
			// 获取优惠券信息
			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
			data.put("prizeName", afCouponDo.getName());
			data.put("prizeType", afCouponDo.getType());
			// 发送本地平台优惠券
			afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "TEAR_PACKET", afGameResultDo.getRid() + "");
			// 更新优惠券已领取数量
			AfCouponDo couponDoT = new AfCouponDo();
			couponDoT.setRid(Long.parseLong(couponId));
			couponDoT.setQuotaAlready(1);
			afCouponService.updateCouponquotaAlreadyById(couponDoT);
		}
		return data;
	}

	// 获得优惠劵
	private String getCreditAuthMsg(FanbeiContext context, String creditRebateMsg) {
		// 给客户端发送获得现金的信息
		AfUserAccountLogDo userAccountLogDo = new AfUserAccountLogDo();
		userAccountLogDo.setUserId(context.getUserId());
		userAccountLogDo.setType(CouponSenceRuleType.CREDITAUTH.getCode());
		BigDecimal crediRebateAmount = afUserAccountLogDao.getUserAmountByType(userAccountLogDo);
		List<AfUserCouponDto> userCouponList = afUserCouponService.getUserCouponByUserIdAndSourceType(context.getUserId(), CouponSenceRuleType.CREDITAUTH.getCode());
		if (!CollectionUtil.isEmpty(userCouponList)) {
			for (AfUserCouponDto afUserCouponDto : userCouponList) {

				if (afUserCouponDto.getType().equals(CouponType.MOBILE.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元话费充值劵\n";
				}
				if (afUserCouponDto.getType().equals(CouponType.REPAYMENT.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元还款卷\n";
				}
				if (afUserCouponDto.getType().equals(CouponType.FULLVOUCHER.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元满减卷\n";
				}
				if (afUserCouponDto.getType().equals(CouponType.CASH.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元现金劵\n";
				}
				if (afUserCouponDto.getType().equals(CouponType.ACTIVITY.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元会场劵\n";
				}
				if (afUserCouponDto.getType().equals(CouponType.FREEINTEREST.getCode())) {
					creditRebateMsg = creditRebateMsg + afUserCouponDto.getAmount().intValue() + "元借钱免息劵\n";
				}

			}
		}
		if (crediRebateAmount != null) {
			if (crediRebateAmount.compareTo(BigDecimal.ZERO) > 0) {
				creditRebateMsg = creditRebateMsg + crediRebateAmount.intValue() + "元现金奖励\n";
			}
		}

		return creditRebateMsg;
	}

}
