package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfGameChanceType;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGameChanceDao;
import com.ald.fanbei.api.dal.dao.AfGameFivebabyDao;
import com.ald.fanbei.api.dal.dao.AfGameResultDao;
import com.ald.fanbei.api.dal.domain.AfGameChanceDo;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;
import com.ald.fanbei.api.dal.domain.AfGameFivebabyDo;
import com.ald.fanbei.api.dal.domain.AfGameResultDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfGameResultDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

@Service("afGameResultService")
public class AfGameResultServiceImpl implements AfGameResultService {
	
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	AfGameResultDao afGameResultDao;
	@Resource
	AfGameChanceDao afGameChanceDao;
	@Resource
	AfGameConfService afGameConfService;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	AfGameFivebabyDao afGameFivebabyDao;
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfResourceService afResourceService;
	
	@Override
	public List<AfGameResultDo> getLatestRecord() {//先从缓存中拿，缓存中拿不到再到表中拿，缓存在抓娃娃接口中维护
		String cacheKey = Constants.CACHEKEY_LATEST_GAMEERSULT_LIST;
		List<AfGameResultDo> afGameResultList = bizCacheUtil.getObjectList(cacheKey);
		if(afGameResultList == null || afGameResultList.size() < 15){
			afGameResultList =  afGameResultDao.getLatestRecord();
		}
		return afGameResultList;
	}

	@Override
	public AfGameResultDo dealWithResult(AfUserDo userInfo, String catchResult, final String item, String code) {
		AfGameResultDo gameResult = null;
		// 验证code是否正确
		AfGameChanceDo chanceDo = checkCode(userInfo, code);
		
		//次数减1，code清除
		this.dealWithChanceCount(chanceDo);
		
		// 未抓中,result中增加记录返回
		if("N".equals(catchResult)){
			gameResult = this.addGameResult(chanceDo.getGameId(),userInfo, code, item,"0", 0l, "N");
			return gameResult;
		}
		
		// 抓中，取配置
		final String lotteryItem = (1 + CommonUtil.getRandomNum(Integer.parseInt(item)))+"";//随机获得一个娃娃的配置来抽奖
		Map<String, JSONObject> itemPrizeMap = getGameConfItemPrizeMap();
		JSONObject prizeObject = itemPrizeMap.get(lotteryItem);
		// 按概率抽奖，若该用户第一次抽奖,则必中奖；若非第一次抽奖则按对应娃娃编号配置的概率抽奖
		List<AfGameResultDto> gameResults = afGameResultDao.getResultDtoByUserId(userInfo.getRid());
		List<AfGameResultDto> itemGameResults = CollectionConverterUtil.convertToListFromList(gameResults, new Converter<AfGameResultDto, AfGameResultDto>() {
			@Override
			public AfGameResultDto convert(AfGameResultDto source) {
				if(lotteryItem.equals(source.getItem())){
					return source;
				}
				return null;
			}
		});
		
		//是否第一次抓中
		boolean isFirstCatch = checkIsFirstCatch(itemGameResults);
		
		String couponId = this.lottery(userInfo.getRid(), prizeObject,itemGameResults, isFirstCatch);
		
		//新增或更新五娃记录，增加result;
		dealWithFivebabyDao(userInfo.getRid(), item, gameResults.size()==0?true:false);
		if(couponId == null){//未抽中
			gameResult = this.addGameResult(chanceDo.getGameId(),userInfo, code, item,lotteryItem, 0l, "N");
		}else{//抽中
			gameResult = this.addGameResult(chanceDo.getGameId(),userInfo, code, item,lotteryItem, Long.parseLong(couponId), "Y");
			//发券或红包
			afUserCouponService.grantCoupon(userInfo.getRid(), Long.parseLong(couponId), "CATCH_TOLL", gameResult.getRid()+"");
			//更新redis中最近中奖的20人
			dealWithLatest20Result(gameResult);
		}
		
		return gameResult;
	}
	
	private boolean checkIsFirstCatch(List<AfGameResultDto> gameResults){
		boolean isFirstCatch = true;
		for(AfGameResultDto resultItem : gameResults){
			if("Y".equals(resultItem.getResult())){
				isFirstCatch = false;
				break;
			}
			isFirstCatch = true;
		}
		return isFirstCatch;
	}
	
	private void dealWithLatest20Result(AfGameResultDo resultDo){
		String cacheKey = Constants.CACHEKEY_LATEST_GAMEERSULT_LIST;
		List<AfGameResultDo> afGameResultList = bizCacheUtil.getObjectList(cacheKey);
		if(afGameResultList == null){
			afGameResultList = new LinkedList<AfGameResultDo>();
			afGameResultList.add(resultDo);
		}else if(afGameResultList.size() < 20){
			afGameResultList.add(resultDo);
		}else{
			afGameResultList.add(resultDo);
			while(afGameResultList.size() > 20){
				afGameResultList.remove(0);
			}
		}
		bizCacheUtil.saveObjectList(cacheKey, afGameResultList);
	}
	
	/**
	 * 抽奖，获取优惠券id
	 * @param userId 用户id
	 * @param prizeObject 载体奖品配置
	 * @param isFirstLottery 是否第一次抓中娃娃
	 * @return
	 */
	private String lottery(Long userId,JSONObject prizeObject,List<AfGameResultDto> gameResults,boolean isFirstLottery){
		try{
			JSONArray rules = prizeObject.getJSONArray("prize");
			Map<String,JSONObject> rulesMap = new HashMap<String, JSONObject>();
			List<Integer> rates = new ArrayList<Integer>();
			int sumRate = 0;
			List<String> prizeIds = new ArrayList<String>();
			for(int i = 0 ;i < rules.size() ;i ++){
				int rateValue = Integer.parseInt(rules.getJSONObject(i).getString("rate"));
				sumRate = sumRate + rateValue;
				rates.add(rateValue);
				prizeIds.add(rules.getJSONObject(i).getString("prize_id"));
				rulesMap.put(rules.getJSONObject(i).getString("prize_id"), rules.getJSONObject(i));
			}
			logger.info(userId + " lottery=" + gameResults.size() + ";" + isFirstLottery + ";" + sumRate);
			//如果是第一次登录，比中奖：算法为：[如：coupon1的概率为10，coupon2的概率为8，coupon3的概率为20，则取一个小于38的随机数，若随机数落在0-9，则抽中coupon1,若落在10-17则为coupon2,若落在18-37则为coupon3]
			if(isFirstLottery){
				if(sumRate < 1){
					dealWithCoupon(prizeIds.get(0), rulesMap);
					return prizeIds.get(0);
				}
				int randomVal = CommonUtil.getRandomNum(sumRate);
				logger.info(userId + " randomVal=" + randomVal);
				int tempValue = 0;
				for(int i = 0 ;i < rates.size() ;i ++){
					tempValue = tempValue + rates.get(i);
					if(randomVal < tempValue){
						dealWithCoupon(prizeIds.get(i), rulesMap);
						return prizeIds.get(i);
					}
				}
			}else{//如果不是第一次、则先计算当前中奖概率、再计算单项中奖概率，扣除已经满足奖项的概率再根据概率计算优惠券区间
				int userAwardCount = 0;
				Map<String,Integer> couponIdResultMap = new HashMap<String, Integer>();//用户优惠券中奖结果
				for(int i=0;i < gameResults.size(); i ++){
					AfGameResultDto resultItem = gameResults.get(i);
					if("Y".equals(resultItem.getResult())){//中奖
						userAwardCount = userAwardCount + 1;
						if(couponIdResultMap.get(resultItem.getLotteryResult()+"") != null){
							couponIdResultMap.put(resultItem.getLotteryResult()+"", couponIdResultMap.get(resultItem.getLotteryResult()+"") + 1);
						}else{
							couponIdResultMap.put(resultItem.getLotteryResult()+"", 1);
						}
					}
				}
				
				BigDecimal personalRate = BigDecimalUtil.divide(sumRate, 100);//单个娃娃配置的总中奖概率
				BigDecimal userRate = new BigDecimal(0);//单个娃娃用户已经中奖的概率
				if(gameResults.size() > 0){
					userRate = BigDecimalUtil.divide(userAwardCount, gameResults.size());
				}
				if(personalRate.compareTo(new BigDecimal(1)) != 0 && userRate.compareTo(personalRate) >=0){//获奖概率已经大于个人总体概率
					return null;
				}
				//判断单项中间概率,如果单项概率已经大于配置的中奖概率了，不能再中该奖了
				for(String item:couponIdResultMap.keySet()){
					int itemIndex = prizeIds.indexOf(item);
					if(itemIndex <0 ){//配置变更了，已经中奖的优惠券已经没有了
						continue;
					}
					BigDecimal itemRate = BigDecimalUtil.divide(couponIdResultMap.get(item), gameResults.size()).multiply(new BigDecimal(100));//用户单项中奖概率
					if(rulesMap.get(item) != null && itemRate.compareTo(new BigDecimal(rulesMap.get(item).getString("rate"))) > 0){//如果单项中奖概率已经大于配置则该项不参与抽奖
						rulesMap.remove(item);
						prizeIds.remove(itemIndex);
						rates.remove(itemIndex);
					}else{//否则，对应项的配置概率-已经中奖的概率
						rates.set(itemIndex, rates.get(itemIndex)-itemRate.intValue());
					}
				}
				
				if(rates.size() > 0){//还有对应的配置
					int randomVal = CommonUtil.getRandomNum(100-userRate.multiply(new BigDecimal(100)).intValue());
					logger.info(userId + " randomVal=" + randomVal + ",userRate=" + userRate + ",rates=" + rates);
					int tempValue = 0;
					for(int i = 0 ;i < rates.size() ;i ++){
						tempValue = tempValue + rates.get(i);
						if(randomVal < tempValue){
							String prizeId = prizeIds.get(i);
							dealWithCoupon(prizeId, rulesMap);
							return prizeId;
							
						}
					}
					return null;
				}
			}
			return null;
		}catch(FanbeiException e){
			if(e.getErrorCode().getErrorCode() == FanbeiExceptionCode.ADD_WHITE_USER_PRIMARIES_ERROR.getErrorCode()){
				logger.info("coupon count is limit");
			}
			return null;
		}
	}
	
	private void dealWithCoupon(String prizeId,Map<String,JSONObject> rulesMap){
		Date date=new Date();
		String key = Constants.CACHEKEY_GAME_LIMIT + DateUtil.formatDate(date, DateUtil.DEFAULT_PATTERN);
		JSONObject prizeObj = rulesMap.get(prizeId);
		String type = prizeObj.getString("prize_type");
		List<AfResourceDo> limitRes = afResourceService.getLocalByType(Constants.RES_GAME_AWARD_COUNT_LIMIT);
		Long cashCount = (Long)bizCacheUtil.getObject(key + type);
		cashCount = cashCount == null?0l:cashCount;
		if(cashCount%100 == 1){
			logger.info(StringUtil.appendStrs(limitRes,",cashCount=",cashCount,",type=",type));
		}
		if(CouponType.CASH.getCode().equals(type)){
			if(cashCount + 1 > Long.parseLong(limitRes.get(0).getValue())){
				throw new FanbeiException("未中奖",FanbeiExceptionCode.ADD_WHITE_USER_PRIMARIES_ERROR);
			}
			cashCount = cashCount + 1;
		}else if(CouponType.REPAYMENT.getCode().equals(type)){
			if(cashCount + 1 > Long.parseLong(limitRes.get(0).getValue1())){
				throw new FanbeiException("未中奖",FanbeiExceptionCode.ADD_WHITE_USER_PRIMARIES_ERROR);
			}
			cashCount = cashCount + 1;
		}else if(CouponType.FULLVOUCHER.getCode().equals(type)){
			if(cashCount + 1 > Long.parseLong(limitRes.get(0).getValue2())){
				throw new FanbeiException("未中奖",FanbeiExceptionCode.ADD_WHITE_USER_PRIMARIES_ERROR);
			}
			cashCount = cashCount + 1;
		}
		if(cashCount > 0l){
			bizCacheUtil.saveObject(key + type, cashCount,Constants.SECOND_OF_ONE_DAY);
		}
	}
	
	private Map<String, JSONObject> getGameConfItemPrizeMap(){
		List<AfGameConfDo> gameConfList = afGameConfService.getByGameCode("catch_doll");
		AfGameConfDo gameConf = null;
		for(AfGameConfDo confItem:gameConfList){
			if("L".equals(confItem.getType())){
				gameConf = confItem;
				break;
			}
		}
		String rule = gameConf.getRule();
		JSONArray rules = JSON.parseArray(rule);
		Map<String, JSONObject> itemPrizeMap = new HashMap<String, JSONObject>();
		for(int i = 0 ;i < rules.size() ; i ++){
			JSONObject item = rules.getJSONObject(i);
			itemPrizeMap.put(item.getString("item"), item);
		}
		return itemPrizeMap;
	}
	
	private AfGameChanceDo checkCode(AfUserDo userInfo, String code){
		AfGameChanceDo chanceDo = null;
		if(code.startsWith(AfGameChanceType.DAY.getCode())){
			chanceDo = afGameChanceDao.getByUserIdType(userInfo.getRid(), AfGameChanceType.DAY.getCode(), DateUtil.formatDate(new Date(),DateUtil.DEFAULT_PATTERN).substring(2));
		}else{
			chanceDo = afGameChanceDao.getByUserIdType(userInfo.getRid(), AfGameChanceType.INVITE.getCode(), null);
		}
		List<String> existCodes = StringUtil.splitToList(chanceDo.getCodes(), ",");
		if(existCodes.size() == 0 || !existCodes.remove(code)){
			throw new FanbeiException("invalid chance code",FanbeiExceptionCode.GAME_CHANCE_CODE_ERROR);
		}
		
		chanceDo.setCodes(StringUtil.turnListToStr(existCodes, ","));
		return chanceDo;
	}
	
	
	private void dealWithFivebabyDao(Long userId,String item,boolean isFirstAward){

		AfGameFivebabyDo exitBabys = afGameFivebabyDao.getByUserId(userId);
		AfGameFivebabyDo fiveBaby = new AfGameFivebabyDo();
		fiveBaby.setUserId(userId);
		if(exitBabys == null){
			fiveBaby.setItem1Count(0);
			fiveBaby.setItem2Count(0);
			fiveBaby.setItem3Count(0);
			fiveBaby.setItem4Count(0);
			fiveBaby.setItem5Count(0);
			if("1".equals(item)){
				fiveBaby.setItem1Count(1);
			}else if("2".equals(item)){
				fiveBaby.setItem2Count(1);
			}else if("3".equals(item)){
				fiveBaby.setItem3Count(1);
			}else if("4".equals(item)){
				fiveBaby.setItem4Count(1);
			}else if("5".equals(item)){
				fiveBaby.setItem5Count(1);
			}
			afGameFivebabyDao.addGameFivebaby(fiveBaby);
		}else{
			if("1".equals(item)){
				fiveBaby.setItem1Count(1);
				exitBabys.setItem1Count(exitBabys.getItem1Count()+1);
			}else if("2".equals(item)){
				fiveBaby.setItem2Count(1);
				exitBabys.setItem2Count(exitBabys.getItem2Count()+1);
			}else if("3".equals(item)){
				fiveBaby.setItem3Count(1);
				exitBabys.setItem3Count(exitBabys.getItem3Count()+1);
			}else if("4".equals(item)){
				fiveBaby.setItem4Count(1);
				exitBabys.setItem4Count(exitBabys.getItem4Count()+1);
			}else if("5".equals(item)){
				fiveBaby.setItem5Count(1);
				exitBabys.setItem5Count(exitBabys.getItem5Count()+1);
			}
			if("N".equals(exitBabys.getIsFinish())&& exitBabys.getItem1Count() > 0 && exitBabys.getItem2Count() > 0 && exitBabys.getItem3Count() > 0 && exitBabys.getItem4Count() > 0 && exitBabys.getItem5Count() > 0){
				fiveBaby.setIsFinish("Y");
			}
			afGameFivebabyDao.updateGameFivebaby(fiveBaby);
		}
	}
	
	private void dealWithChanceCount(AfGameChanceDo chanceDo){
		AfGameChanceDo chanceUpdate = new AfGameChanceDo();
		chanceUpdate.setRid(chanceDo.getRid());
		chanceUpdate.setUsedCount(1);
		chanceUpdate.setCodes(chanceDo.getCodes());
		afGameChanceDao.updateGameChance(chanceUpdate);
	}
	
	public AfGameResultDo addGameResult(Long gameId,AfUserDo user,Long borrowId,Long couponId,String lotteryResult) {
		return addGameResult(gameId,user,borrowId + "","","",couponId,lotteryResult);
	}
	
	/**
	 * 增加抓娃娃中奖结果
	 * @param gameId 游戏id
	 * @param user 用户
	 * @param code 机会编码
	 * @param item 娃娃编号
	 * @param lotteryItem 抽奖娃娃编号
	 * @param couponId 优惠券id
	 * @param lotteryResult 抓娃娃结果
	 * @return
	 */
	
	private AfGameResultDo addGameResult(Long gameId,AfUserDo user,String code,String item,String lotteryItem,Long couponId,String lotteryResult){
		AfGameResultDo resultDo = new AfGameResultDo();
		resultDo.setGameId(gameId);
		resultDo.setCode(code);
		resultDo.setItem(item);
		resultDo.setLotteryItem(lotteryItem);
		resultDo.setLotteryResult(couponId);
		resultDo.setResult(lotteryResult);
		resultDo.setUserAvata(user.getAvatar());
		resultDo.setUserId(user.getRid());
		resultDo.setUserName(user.getUserName());
		afGameResultDao.addGameResult(resultDo);
		return resultDo;
	}

	@Override
	public List<AfGameResultDo> getTearPacketResultByUserId(Long userId, Long borrowId) {
		return afGameResultDao.getTearPacketResultByUserId(userId, borrowId);
	}

	@Override
	public List<AfGameResultDo> getTearPacketLatestRecord() {
		return afGameResultDao.getTearPacketLatestRecord();
	}

	
}
