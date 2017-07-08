package com.ald.fanbei.api.web.api.borrowCash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.AfGameResultDo;
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
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			// 首先判断用户是否有资格参与拆红包活动
		
			AfBorrowCashDo afLastBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
			List<AfGameResultDo> gameResultList =  afGameResultService.getTearPacketResultByUserId(userId, afLastBorrowCashDo.getRid());
			String status  = afLastBorrowCashDo.getStatus();
			int takePartTime = 0;
			if(gameResultList != null){
				takePartTime = gameResultList.size();
			}
			if(!("TRANSED".equals(status) && takePartTime < 1)
					&& !("FINSH".equals(status) && takePartTime < 2)) {
				throw new FanbeiException();
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
			afUserCouponService.grantCoupon(userId, Long.parseLong(couponId), "TEAR_PACKET", afGameResultDo.getRid() + "");
			
			// 获取优惠券信息
			AfCouponDo afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
			data.put("prizeName", afCouponDo.getName());
			data.put("prizeType", afCouponDo.getType());
		} catch (Exception e) {
			logger.error("TearPacketApi=>" + e.toString());
		}
		resp.setResponseData(data);
		return resp;
	}
}
