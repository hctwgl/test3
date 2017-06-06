package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGameAwardService;
import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.service.AfGameFivebabyService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfGameAwardDo;
import com.ald.fanbei.api.dal.domain.AfGameChanceDo;
import com.ald.fanbei.api.dal.domain.AfGameConfDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.AfGameFivebabyDo;
import com.ald.fanbei.api.dal.domain.AfGameResultDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGameInitVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
/**
 * 
 *@类现描述：游戏相关接口
 *@author chenjinhu 2017年6月3日 下午5:56:53
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5GameController  extends BaseController{
	
	@Resource
	AfGameService afGameService;
	@Resource
	AfGameResultService afGameResultService;
	@Resource
	AfGameFivebabyService afGameFivebabyService;
	@Resource
	AfGameChanceService afGameChanceService;
	@Resource
	AfGameConfService afGameConfService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGameAwardService afGameAwardService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfCouponService afCouponService;
	
	
	/**
	 * 游戏初始化接口、获取游戏界面需要的相关参数，包括用户剩余的机会次数，游戏信息，游戏中奖情况等
	 * @param request
	 * @param model
	 * @throws IOException
	 */
	@RequestMapping(value = { "indexPage" }, method = RequestMethod.GET)
	public void goodsListModel(HttpServletRequest request, ModelMap model) throws IOException {
		logger.info("统计进入游戏次数");//TODO
	}
	
	@RequestMapping(value = "initGame", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String initGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try{
			Long userId = 1l;//TODO 
			
			//游戏配置
			AfGameDo gameDo = afGameService.getByCode("catch_doll");
			List<AfGameConfDo> gameConfDo = afGameConfService.getByGameId(gameDo.getRid());
			
			//最近20条抓娃娃中奖列表
			List<AfGameResultDo> latestResultList= afGameResultService.getLatestRecord();
			//最近20条发奖列表
			List<AfGameAwardDo> latestAwardList= afGameAwardService.getLatestAwards();
			
			AfGameFivebabyDo fivebabyDo = null;
			List<AfGameChanceDo> gameChanceList = null;
			AfGameAwardDo awardDo = null;
			if(userId > 0l){//TODO 判断用户登录
				//获取中奖想信息
				fivebabyDo = afGameFivebabyService.getByUserId(userId);
				//用户抓娃娃机会
				gameChanceList = afGameChanceService.getByUserId(gameDo.getRid(),userId, DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN.substring(2)));
				//用户中奖情况
				awardDo = afGameAwardService.getByUserId(userId);
			}
			AfGameInitVo initResult = this.buildGameInitVo(gameDo, gameConfDo, latestResultList, latestAwardList, gameChanceList, fivebabyDo, awardDo, userId>0l);
			return H5CommonResponse.getNewInstance(true, "获取成功", "", initResult).toString();
		}finally{
			logger.info("日志");//TODO
		}
	}
	
	@RequestMapping(value = "submitGameResult", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String submitGameResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String,Object> resultData = new HashMap<String, Object>(); 
		try{
			String appInfotext = ObjectUtils.toString(request.getParameter("_appInfo"), "").toString();
			String result = request.getParameter("result");
			String item = request.getParameter("item");
			String code = request.getParameter("code");
			if(StringUtil.isEmpty(appInfotext) || StringUtil.isEmpty(result) || StringUtil.isEmpty(code)){
				return H5CommonResponse.getNewInstance(false, "参数异常", "", "").toString();
			}
			if(StringUtil.isBlank(item)){
				item = "0";
			}
	
			JSONObject appInfo = JSON.parseObject(appInfotext);
			String userName = ObjectUtils.toString(appInfo.get("userName"), "").toString();
			AfUserDo userInfo = afUserService.getUserByUserName(userName);
			AfGameResultDo gameResult = afGameResultService.dealWithResult(userInfo, result, item, code);
			if(gameResult == null || "N".equals(gameResult.getResult())){
				resultData.put("lotteryResult", "N");
			}else{
				resultData.put("lotteryResult", "Y");
				AfCouponDo couponDo = afCouponService.getCouponById(gameResult.getLotteryResult());
				resultData.put("awardType", couponDo.getType());
				resultData.put("amount", couponDo.getAmount());
				resultData.put("limitAmount", couponDo.getLimitAmount());
				if("D".equals(couponDo.getExpiryType())){//固定天数
					Date current = new Date();
					resultData.put("gmtEnd", DateUtil.addDays(current, couponDo.getValidDays()));
				}else{//固定时间范围
					resultData.put("gmtEnd", couponDo.getGmtEnd());
				}
				
			}
			
			return H5CommonResponse.getNewInstance(true, "成功", "", resultData).toString();
		}finally{
			logger.info("日志");//TODO
		}
	}
	
	@RequestMapping(value = "submitContract", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String submitContract(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String,Object> resultData = new HashMap<String, Object>(); 
		try{
			String appInfotext = ObjectUtils.toString(request.getParameter("_appInfo"), "").toString();
			String name = request.getParameter("name");
			String mobilePhone = request.getParameter("mobilePhone");
			String address = request.getParameter("address");
			if(StringUtil.isEmpty(appInfotext) || StringUtil.isEmpty(name) || StringUtil.isEmpty(mobilePhone) || StringUtil.isEmpty(address)){
				return H5CommonResponse.getNewInstance(false, "参数异常", "", "").toString();
			}
			JSONObject appInfo = JSON.parseObject(appInfotext);
			String userName = ObjectUtils.toString(appInfo.get("userName"), "").toString();
			
			JSONObject contractsObj = new JSONObject();
			contractsObj.put("name", name);
			contractsObj.put("mobilePhone", mobilePhone);
			contractsObj.put("address", address);
			
			AfUserDo userInfo = afUserService.getUserByUserName(userName);
			afGameAwardService.updateContact(userInfo.getRid(), contractsObj.toString());
			return H5CommonResponse.getNewInstance(true, "获取成功", "", resultData).toString();
		}finally{
			logger.info("日志");//TODO
		}
	}
	
	
	private AfGameInitVo buildGameInitVo(AfGameDo gameDo,List<AfGameConfDo> gameConfDo,List<AfGameResultDo> latestResultList,List<AfGameAwardDo> latestAwardList,List<AfGameChanceDo> gameChanceList,AfGameFivebabyDo fivebabyDo,AfGameAwardDo awardDo,boolean isLogin){
		AfGameInitVo gameInitVo = new AfGameInitVo();
		
		//游戏信息
		if(gameDo != null){
			gameInitVo.setTitle(gameDo.getTitle());
			gameInitVo.setRule(gameDo.getGameRule());
			gameInitVo.setIsLogin(isLogin?"Y":"N");
		}
		
		//设置下一个开奖时间
		gameInitVo.setGmtCurrent(System.currentTimeMillis());
		Long gmtOpen = 0l;
		for(AfGameConfDo item:gameConfDo){
			if("P".equals(item.getType())){
				if(gmtOpen == 0l || gmtOpen > item.getGmtSend().getTime()){
					gmtOpen = item.getGmtSend().getTime();
				}
			}
		}
		gameInitVo.setGmtOpen(gmtOpen);
		
		//用户机会信息
		if(gameChanceList != null){
			int chanceCount = 0;
			String chanceCodes = "";
			for(AfGameChanceDo item:gameChanceList){
				chanceCount = chanceCount + (item.getTotalCount() - item.getUsedCount());
				if(item.getTotalCount() > 0){
					chanceCodes = chanceCodes + "," + item.getCodes();
				}
			}
			gameInitVo.setChanceCount(chanceCount);
			gameInitVo.setChanceCodes(chanceCodes);
		}
		
		//是否被抽中将
		gameInitVo.setIsFinish(awardDo != null?"Y":"N");
		gameInitVo.setIsAward(awardDo != null?"Y":"N");
		
		//抽中5娃情况
		if(fivebabyDo != null){
			gameInitVo.setItem1Count(fivebabyDo.getItem1Count());
			gameInitVo.setItem2Count(fivebabyDo.getItem2Count());
			gameInitVo.setItem3Count(fivebabyDo.getItem3Count());
			gameInitVo.setItem4Count(fivebabyDo.getItem4Count());
			gameInitVo.setItem5Count(fivebabyDo.getItem5Count());
		}
		
		//最近20个抓娃娃中奖列表
		List<AfGameInitVo.AwardUserVo> awardList = new LinkedList<AfGameInitVo.AwardUserVo>();
		if(latestResultList != null){
			for(AfGameResultDo item:latestResultList){
				AfGameInitVo.AwardUserVo awardUser = new AfGameInitVo().new AwardUserVo();
				awardUser.setAvatar(item.getUserAvata());
				awardUser.setUserName(item.getUserName().substring(0,3)+"****"+item.getUserName().substring(7));
				AfCouponDo couponDo = afCouponService.getCouponById(item.getLotteryResult());
				String msg = "";
				if(CouponType.CASH.getCode().equals(couponDo.getType())){
					msg = StringUtil.appendStrs("获得",couponDo.getAmount(),"元现金");
				}else if(CouponType.FULLVOUCHER.getCode().equals(couponDo.getType())){
					msg = StringUtil.appendStrs("获得满",couponDo.getLimitAmount(),"减",couponDo.getAmount(),"元的满减券");
				}else if(CouponType.REPAYMENT.getCode().equals(couponDo.getType())){
					msg = StringUtil.appendStrs("获得满",couponDo.getLimitAmount(),"减",couponDo.getAmount(),"元的还款券");
				}
				awardUser.setMsg(msg);
				awardList.add(awardUser);
			}
		}
		gameInitVo.setAwardList(awardList);
		

		//最近20个中实物奖列表
		List<AfGameInitVo.AwardUserVo> entityAwardList = new LinkedList<AfGameInitVo.AwardUserVo>();
		if(latestAwardList != null){
			List<AfResourceDo> entityAwards = afResourceService.getResourceListByType(Constants.RES_GAME_AWARD_OF_CATCH_DOLL);
			Map<String,AfResourceDo> entityAwardMap = CollectionConverterUtil.convertToMapFromList(entityAwards, new Converter<AfResourceDo, String>() {
				@Override
				public String convert(AfResourceDo source) {
					return source.getRid()+"";
				}
			});
			for(AfGameAwardDo item:latestAwardList){
				AfGameInitVo.AwardUserVo itemVo = new AfGameInitVo().new AwardUserVo();
				itemVo.setAvatar(item.getUserAvata());
				itemVo.setUserName(item.getUserName().substring(0,3)+"****"+item.getUserName().substring(7));
				itemVo.setMsg(entityAwardMap.get(item.getAwardId()+"").getValue2()); 
				entityAwardList.add(itemVo);
			}
		}
		gameInitVo.setEntityAwardList(entityAwardList);
		
		if(awardDo != null){//奖品信息
			AfGameInitVo.AwardInfoVo awardInfo = new AfGameInitVo().new AwardInfoVo();
			awardInfo.setAwardIcon(awardDo.getAwardIcon());
			awardInfo.setAwardId(awardDo.getAwardId());
			awardInfo.setAwardName(awardDo.getAwardName());
			awardInfo.setIsSubmitContacts(StringUtil.isBlank(awardDo.getContacts())?"N":"Y");
			awardInfo.setType(awardDo.getAwardType());
			gameInitVo.setAwardInfo(awardInfo);
		}
		
		return gameInitVo;
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
	}

}
