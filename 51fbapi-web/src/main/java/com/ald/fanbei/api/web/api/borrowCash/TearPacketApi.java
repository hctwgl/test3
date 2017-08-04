package com.ald.fanbei.api.web.api.borrowCash;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.AfGameResultDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author jiangrongbo 2017年3月24日下午3:48:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tearPacketApi")
public class TearPacketApi  implements ApiHandle {

	@Resource
	AfGameService  afGameService;
	@Resource
	AfGameConfService afGameConfService;
	@Resource
	AfGameResultService afGameResultService;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfCouponService afCouponService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"), null);
		try {
			if(type == null || "LOAN_PACKET".equals(type)) {
				return tearloanPacket(resp,requestDataVo, context, request);
			} else if ("RISK_PACKET".equals(type)){
				return tearRiskPacket(resp,requestDataVo, context, request);
			}
		} catch (Exception e) {
			logger.error("TearPacketApi=>" + e.toString());
		}
		return resp;
	}

	// 拆风控拒绝红包
	private ApiHandleResponse tearRiskPacket(ApiHandleResponse resp,RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest request) {
		Long userId = context.getUserId();
		// 获取用户信息
		AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
		Map<String, Object> data = new HashMap<String, Object>();
		// 获取拆红包游戏信息
		AfGameDo gameDo = afGameService.getByCode("risk_packet");
		if(gameDo == null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
		}
		
		// 查询活动时间内是否有风控拒绝记录 FIXME
		Date gameStart = gameDo.getGmtStart();
		Date gameEnd = gameDo.getGmtEnd();
		List<AfBorrowCashDo> riskRefuseResultList =  afBorrowCashService.getRiskRefuseBorrowCash(userId, gameStart, gameEnd);
		List<AfGameResultDo> gameResultList =  afGameResultService.getTearRiskPacketResultByUserId(userId, "risk_packet");
		int takePartTime = 0;
		if(gameResultList != null){
			takePartTime = gameResultList.size();
		}
		if(riskRefuseResultList == null || riskRefuseResultList.size() != 1 || takePartTime >= 1) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NOT_CHANCE_TEAR_PACKET_ERROR);
		}
		
		// 获取游戏配置信息
		List<AfGameConfDo> afGameConfList = afGameConfService.getByGameId(gameDo.getRid());
		if(afGameConfList == null || afGameConfList.size() == 0) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
		}
		AfGameConfDo afGameConfDo = afGameConfList.get(0);
		String rules = afGameConfDo.getRule();
		try {
			// 按照概率抽奖
			JSONArray array = JSON.parseArray(rules);
			JSONObject item1 = array.getJSONObject(0);
			JSONArray prizeArray = item1.getJSONArray("prize");
			List<Map<String,Object>> awardList = new ArrayList<Map<String,Object>>();
			
			int totalRate = 0;
			for (int i = 0; i < prizeArray.size(); i++) {
				Map<String,Object> prizeMap = new HashMap<String,Object>();
				JSONObject prize =  prizeArray.getJSONObject(i);
				String prizeType = prize.getString("prize_type");
				String rate = prize.getString("rate");
				String prizeId = prize.getString("prize_id");
				String limitCount = prize.getString("limit_count");
				prizeMap.put("prizeType", prizeType);
				prizeMap.put("rate", rate);
				prizeMap.put("prizeId", prizeId);
				prizeMap.put("limitCount", limitCount);
				awardList.add(prizeMap);
				totalRate += Integer.parseInt(rate);
			}
			int result = new Random().nextInt(totalRate) + 1;
			// 判断中奖的是哪个奖品
			int startRate = 0;
			int endRate = 0;
			// 中奖奖品信息
			Map<String,Object> winPrizeInfo = null;
			for(Map<String,Object>  awardInfo : awardList) {
				startRate = endRate;
				String rate = (String) awardInfo.get("rate");
				endRate += Integer.parseInt(rate);
				if(startRate < result && result <=endRate) {
					winPrizeInfo = awardInfo;
					logger.info("TearPacketApi winPrizeInfo=>" + winPrizeInfo);
					break;
				}
			}
			String couponId = (String) winPrizeInfo.get("prizeId"); 
		
			// 获取用户最新一笔借款信息
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
			Long borrowId = 0l;
			if(afBorrowCashDo != null){
				borrowId = afBorrowCashDo.getRid();
			}
			// 抽奖后将抽奖结果记录到数据库中 FIXME
			
			
			
			// 添加抽奖结果信息
			AfGameResultDo afGameResultDo = afGameResultService.addGameResult(gameDo.getRid(), userInfo, borrowId, Long.parseLong(couponId), "Y");
			
			String prizeType = (String) winPrizeInfo.get("prizeType");
			if("BOLUOMI".equals(prizeType)) {
				// 发送菠萝蜜优惠券
				Long sceneId = Long.parseLong(couponId);
				grantBoluomiCoupon(sceneId, data, userId);
				
			} else{
				// 获取优惠券信息
				AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
				data.put("prizeName", afCouponDo.getName());
				data.put("prizeType", afCouponDo.getType());
				// 发送本地平台优惠券
				afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "RISK_PACKET", afGameResultDo.getRid() + "");
				// 更新优惠券已领取数量
				AfCouponDo couponDoT = new AfCouponDo();
				couponDoT.setRid(Long.parseLong(couponId));
				couponDoT.setQuotaAlready(1);
				afCouponService.updateCouponquotaAlreadyById(couponDoT);
			}
		} catch (Exception e) {
			logger.error("TearPacketApi=>" + e.toString());
			// 发送备用优惠券
			JSONArray array = JSON.parseArray(rules);
			JSONObject item1 = array.getJSONObject(0);
			JSONArray prizeArray = item1.getJSONArray("back_prize");
			JSONObject prize =  prizeArray.getJSONObject(0);
			String prizeType = prize.getString("prize_type");
			String couponId = prize.getString("prize_id");
			if(couponId != null && !"".equals(couponId)) {
				if("BOLUOMI".equals(prizeType)) {
					// 发送菠萝蜜优惠券
					Long sceneId = Long.parseLong(couponId);
					grantBoluomiCoupon(sceneId, data, userId);
					
				} else{
					// 获取优惠券信息
					AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
					data.put("prizeName", afCouponDo.getName());
					data.put("prizeType", afCouponDo.getType());
					
					AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
					Long borrowId = 0l;
					if(afBorrowCashDo != null){
						borrowId = afBorrowCashDo.getRid();
					}
					// 抽奖后将抽奖结果记录到数据库中 FIXME
					
					// 添加抽奖结果信息
					AfGameResultDo afGameResultDo = afGameResultService.addGameResult(gameDo.getRid(), userInfo, borrowId, Long.parseLong(couponId), "Y");
					
					// 发送本地平台优惠券
					afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "RISK_PACKET", afGameResultDo.getRid() + "");
					// 更新优惠券已领取数量
					AfCouponDo couponDoT = new AfCouponDo();
					couponDoT.setRid(Long.parseLong(couponId));
					couponDoT.setQuotaAlready(1);
					afCouponService.updateCouponquotaAlreadyById(couponDoT);
			}
			}
		}
		resp.setResponseData(data);
		return resp;
	}

	// 拆借钱红包
	private ApiHandleResponse tearloanPacket(ApiHandleResponse resp, RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest request) {
		Map<String, Object> data = new HashMap<String, Object>();
		Long userId = context.getUserId();
		try {
			// 首先判断用户是否有资格参与拆红包活动
			AfBorrowCashDo afLastBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
			List<AfGameResultDo> gameResultList =  afGameResultService.getTearPacketResultByUserId(userId, afLastBorrowCashDo.getRid(), "tear_packet");
			String status  = afLastBorrowCashDo.getStatus();
			int takePartTime = 0;
			if(gameResultList != null){
				takePartTime = gameResultList.size();
			}
			if("FINSH".equals(status) && takePartTime > 1) {
				throw new FanbeiException("不符合抽奖条件");
			} 
			// 获取拆红包游戏信息
			AfGameDo gameDo = afGameService.getByCode("tear_packet");
			if(gameDo == null){
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
			}
			// 获取游戏配置信息
			List<AfGameConfDo> afGameConfList = afGameConfService.getByGameId(gameDo.getRid());
			if(afGameConfList == null || afGameConfList.size() == 0) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.NOT_CONFIG_GAME_INFO_ERROR);
			}
			AfGameConfDo afGameConfDo = afGameConfList.get(0);
			String rules = afGameConfDo.getRule();
			// 按照概率抽奖
			JSONArray array = JSON.parseArray(rules);
			JSONObject item1 = array.getJSONObject(0);
			JSONArray prizeArray = item1.getJSONArray("prize");
			List<Map<String,Object>> awardList = new ArrayList<Map<String,Object>>();
			
			int totalRate = 0;
			for (int i = 0; i < prizeArray.size(); i++) {
				Map<String,Object> prizeMap = new HashMap<String,Object>();
				JSONObject prize =  prizeArray.getJSONObject(i);
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
			Map<String,Object> winPrizeInfo = null;
			for(Map<String,Object>  awardInfo : awardList) {
				startRate = endRate;
				String rate = (String) awardInfo.get("rate");
				endRate += Integer.parseInt(rate);
				if(startRate < result && result <=endRate) {
					winPrizeInfo = awardInfo;
					logger.info("TearPacketApi winPrizeInfo=>" + winPrizeInfo);
					break;
				}
			}
			String couponId = (String) winPrizeInfo.get("prizeId"); 
			// 获取用户信息
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
			
			// 获取用户最新一笔借款信息
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
			Long borrowId = 0l;
			if(afBorrowCashDo != null){
				borrowId = afBorrowCashDo.getRid();
			}
			// 添加抽奖结果信息
			AfGameResultDo afGameResultDo = afGameResultService.addGameResult(gameDo.getRid(), userInfo, borrowId, Long.parseLong(couponId), "Y");
			
			String prizeType = (String) winPrizeInfo.get("prizeType");
			if("BOLUOMI".equals(prizeType)) {
				// 发送菠萝蜜优惠券
				Long sceneId = Long.parseLong(couponId);
				grantBoluomiCoupon(sceneId, data, userId);
			} else{
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
		} catch (Exception e) {
			logger.error("TearPacketApi=>" + e.toString());
			// 发送备用优惠券
			
		}
		resp.setResponseData(data);
		return resp;
	}
			
	private void grantBoluomiCoupon(Long sceneId, Map<String, Object> data, Long userId ) {
		logger.info(" pickBoluomeCoupon begin , sceneId = {}, userId = {}",sceneId, userId);
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
		String url = resourceInfo.getValue();
		if(url != null) {
			url = url.replace(" ", "");
		}
		String resultString = HttpUtil.doHttpPostJsonParam(url, JSONObject.toJSONString(bo));
		logger.info("pickBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (!"0".equals(resultJson.getString("code"))) {
			throw new FanbeiException(resultJson.getString("msg"));
		} else if (JSONArray.parseArray(resultJson.getString("data")).size() == 0){
			throw new FanbeiException("仅限领取一次，请勿重复领取！");
		}
	}
}
