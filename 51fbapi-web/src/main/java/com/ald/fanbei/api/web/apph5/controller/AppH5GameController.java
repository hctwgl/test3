package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfGameAwardService;
import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.biz.service.AfGameConfService;
import com.ald.fanbei.api.biz.service.AfGameFivebabyService;
import com.ald.fanbei.api.biz.service.AfGameResultService;
import com.ald.fanbei.api.biz.service.AfGameService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
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
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
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
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAuthService afUserAuthService;
	@Resource
	AfUserAccountService afUserAccountService;
	
	String  opennative = "/fanbei-web/opennative?name=";
	
	@RequestMapping(value = "initGame", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String initGame(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();

		FanbeiWebContext context = new FanbeiWebContext();
		try{
			Long userId = -1l;
			AfUserDo afUser = null;
			context = doWebCheck(request, false);
			if(context.isLogin()){
				afUser = afUserService.getUserByUserName(context.getUserName());
				if(afUser != null){
					userId = afUser.getRid();
				}
			}
			//游戏配置
			AfGameDo gameDo = afGameService.getByCode("catch_doll");
			List<AfGameConfDo> gameConfDo = afGameConfService.getByGameCode("catch_doll");
			
			//最近20条抓娃娃中奖列表
			List<AfGameResultDo> latestResultList= afGameResultService.getLatestRecord();
			//最近20条发奖列表
			List<AfGameAwardDo> latestAwardList= afGameAwardService.getLatestAwards();
			
			AfGameFivebabyDo fivebabyDo = null;
			List<AfGameChanceDo> gameChanceList = null;
			AfGameAwardDo awardDo = null;
			if(userId > 0l){
				//获取中奖想信息
				fivebabyDo = afGameFivebabyService.getByUserId(userId);
				//用户抓娃娃机会
				gameChanceList = afGameChanceService.getByUserId(gameDo.getRid(),userId, DateUtil.formatDate(new Date(), DateUtil.DEFAULT_PATTERN.substring(2)));
				//用户中奖情况
				awardDo = afGameAwardService.getByUserId(userId);
			}
			AfGameInitVo initResult = this.buildGameInitVo(gameDo, gameConfDo, latestResultList, latestAwardList, gameChanceList, fivebabyDo, awardDo, afUser);
			resp = H5CommonResponse.getNewInstance(true, "初始化成功", "", initResult);
		}catch(FanbeiException e){
			resp = H5CommonResponse.getNewInstance(false, "初始化失败", "", e.getErrorCode().getDesc());
			logger.error("fb初始化失败" + context,e);
		}catch(Exception e){
			resp = H5CommonResponse.getNewInstance(false, "初始化失败", "", "");
			logger.error("fb初始化失败" + context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	@RequestMapping(value = "submitGameResult", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String submitGameResult(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();

		FanbeiWebContext context = new FanbeiWebContext();
		Map<String,Object> resultData = new HashMap<String, Object>(); 
		try{
			context = doWebCheck(request, true);
			String result = request.getParameter("result");
			String item = request.getParameter("item");
			String code = request.getParameter("code");
			if(StringUtil.isEmpty(result) || StringUtil.isEmpty(code)){
				return H5CommonResponse.getNewInstance(false, "参数异常", "", "").toString();
			}
			if(StringUtil.isBlank(item)){
				item = "0";
			}
	
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
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
			
			resp = H5CommonResponse.getNewInstance(true, "成功", "", resultData);
			}catch(FanbeiException e){
				resp = H5CommonResponse.getNewInstance(false, "抽奖失败", "", e.getErrorCode().getDesc());
				logger.error("fb抽奖失败"+context,e);
			}catch(Exception e){
				resp = H5CommonResponse.getNewInstance(false, "抽奖失败", "", "");
				logger.error("抽奖失败"+context,e);
			}finally{
				Calendar calEnd = Calendar.getInstance();
				doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
			}
			return resp.toString();
	}
	
	@RequestMapping(value = "submitContract", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String submitContract(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		Map<String,Object> resultData = new HashMap<String, Object>(); 
		FanbeiWebContext context = new FanbeiWebContext();
		try{
			context = doWebCheck(request, true);
			String name = request.getParameter("name");
			String mobilePhone = request.getParameter("mobilePhone");
			String address = request.getParameter("address");
			if(StringUtil.isEmpty(context.getAppInfo()) || StringUtil.isEmpty(name) || StringUtil.isEmpty(mobilePhone) || StringUtil.isEmpty(address)){
				resp = H5CommonResponse.getNewInstance(false, "参数异常", "", "");
				return resp.toString();
			}
			
			JSONObject contractsObj = new JSONObject();
			contractsObj.put("name", name);
			contractsObj.put("mobilePhone", mobilePhone);
			contractsObj.put("address", address);
			
			AfUserDo userInfo = afUserService.getUserByUserName(context.getUserName());
			afGameAwardService.updateContact(userInfo.getRid(), contractsObj.toString());
			resp = H5CommonResponse.getNewInstance(true, "提交成功", "", resultData);
		}catch(FanbeiException e){
			resp =  H5CommonResponse.getNewInstance(false, "提交失败", "", e.getErrorCode().getDesc());
			logger.error("提交失败"+context,e);
		}catch(Exception e){
			resp =  H5CommonResponse.getNewInstance(false, "提交失败", "", "");
			logger.error("提交失败"+context,e);
		}finally{
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp,context.getAppInfo(), calEnd.getTimeInMillis()-calStart.getTimeInMillis(),context.getUserName());
		}
		return resp.toString();
	}
	
	
	private AfGameInitVo buildGameInitVo(AfGameDo gameDo,List<AfGameConfDo> gameConfDo,List<AfGameResultDo> latestResultList,List<AfGameAwardDo> latestAwardList,List<AfGameChanceDo> gameChanceList,AfGameFivebabyDo fivebabyDo,AfGameAwardDo awardDo,AfUserDo afUser){
		AfGameInitVo gameInitVo = new AfGameInitVo();
		
		AfResourceDo clientRate = afResourceService.getLocalByType(Constants.RES_GAME_CATCH_DOLL_CLIENT_RATE).get(0);
		gameInitVo.setClientRate(Integer.parseInt(clientRate.getValue()));
		//游戏信息
		if(gameDo != null){
			gameInitVo.setTitle(gameDo.getTitle());
			gameInitVo.setRule(gameDo.getGameRule());
			gameInitVo.setIsLogin(afUser !=null?"Y":"N");
			gameInitVo.setRecommendCode(afUser != null?afUser.getRecommendCode():"");
		}
		
		//设置下一个开奖时间
		gameInitVo.setGmtCurrent(System.currentTimeMillis());
		Long gmtOpen = 0l;
		Long currentTime = new Date().getTime();
		for(AfGameConfDo item:gameConfDo){
			if("P".equals(item.getType())){
				if(item.getGmtSend().getTime() > currentTime && (gmtOpen == 0l || (gmtOpen > item.getGmtSend().getTime()))){
					gmtOpen = item.getGmtSend().getTime();
				}
			}
		}
		gameInitVo.setGmtOpen(gmtOpen);
		
		//用户机会信息
		if(gameChanceList != null){
			int chanceCount = 0;
			List<String> chanceCodesList = new ArrayList<String>();
			for(AfGameChanceDo item:gameChanceList){
				chanceCount = chanceCount + (item.getTotalCount() - item.getUsedCount());
				if(item.getTotalCount() - item.getUsedCount() > 0){
					chanceCodesList.addAll(CommonUtil.turnStringToList(item.getCodes(), ","));
				}
			}
			gameInitVo.setChanceCount(chanceCount);
			gameInitVo.setChanceCodes(StringUtil.turnListToStr(chanceCodesList, ","));
		}
		
		//是否被抽中将
		gameInitVo.setIsFinish(fivebabyDo == null?"N":fivebabyDo.getIsFinish());
		gameInitVo.setIsAward(fivebabyDo == null?"N":fivebabyDo.getIsAward());
		
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
				if(couponDo != null) {
					if(CouponType.CASH.getCode().equals(couponDo.getType())){
						msg = StringUtil.appendStrs("获得",couponDo.getAmount(),"元现金");
					}else if(CouponType.FULLVOUCHER.getCode().equals(couponDo.getType())){
						msg = StringUtil.appendStrs("获得满",couponDo.getLimitAmount(),"减",couponDo.getAmount(),"元的满减券");
					}else if(CouponType.REPAYMENT.getCode().equals(couponDo.getType())){
						msg = StringUtil.appendStrs("获得满",couponDo.getLimitAmount(),"减",couponDo.getAmount(),"元的还款券");
					}
				}
				awardUser.setMsg(msg);
				awardList.add(awardUser);
			}
		}
		gameInitVo.setAwardList(awardList);
		

		//最近20个中实物奖列表
		List<AfGameInitVo.AwardUserVo> entityAwardList = new LinkedList<AfGameInitVo.AwardUserVo>();
		if(latestAwardList != null){
			List<AfResourceDo> entityAwards = afResourceService.getLocalByType(Constants.RES_GAME_AWARD_OF_CATCH_DOLL);
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
				if(entityAwardMap.get(item.getAwardId()+"") == null){
					itemVo.setMsg("获得幸运奖"); 
				}else{
					itemVo.setMsg(entityAwardMap.get(item.getAwardId()+"").getValue2()); 
				}
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
	
	@RequestMapping(value = "tearPacketAwardList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String tearPacketAwardList(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject jsonObj = new JSONObject();
		try{
			List<AfGameResultDo> latestResultList = afGameResultService.getTearPacketLatestRecord();
			List awordList = new ArrayList();
			if(latestResultList != null && latestResultList.size() > 0) {
				for(AfGameResultDo  gameResultDo : latestResultList) {
					Map tmpMap = new HashMap();
					String mobile = gameResultDo.getUserName();
					if(mobile != null && mobile.length() == 11) {
						mobile = mobile.substring(0,3) + "xxxx" + mobile.substring(7, mobile.length());
					}
					tmpMap.put("mobile", mobile);
					Long couponId = gameResultDo.getLotteryResult();
					AfCouponDo afCouponDo = afCouponService.getCouponInfoById(couponId);
					if(afCouponDo == null) continue;
					tmpMap.put("prizeName", afCouponDo.getName());
					awordList.add(tmpMap);
				}
				jsonObj.put("awordList", awordList);
			}
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",jsonObj).toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
		}
	}
	
	
	
	@RequestMapping(value = "tearRiskPacketActivity", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String tearRiskPacketActivity(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HashMap<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = new FanbeiWebContext();
		try{
			context = doWebCheck(request, false);
			String userName = context.getUserName();
			AfUserDo userDo = afUserService.getUserByUserName(userName);
			if(userDo != null) {
				Long userId = userDo.getRid();
				AfUserAuthDo userAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
				AfUserAccountDo userAccount =  afUserAccountService.getUserAccountByUserId(userId);
				String idNumber = userAccount.getIdNumber();
				String realName = userAccount.getRealName();
				data.put("idNumber", idNumber);
				data.put("realName", realName);
				if(userAuthDo != null) {
					String riskStatus = userAuthDo.getRiskStatus();
					if("A".equals(riskStatus)){
						String realnameStatus = userAuthDo.getRealnameStatus();
						String bankcardStatus = userAuthDo.getBankcardStatus();
						String facesStatus = userAuthDo.getFacesStatus();
						if("N".equals(facesStatus)) {
							data.put("status", "A1");
						}else if("N".equals(bankcardStatus)) {
							data.put("status", "A2");
						} else if("Y".equals(realnameStatus)
								&& "Y".equals(bankcardStatus)) {
							data.put("status", "A3");
						}
					} else if("P".equals(riskStatus)) {
						data.put("status", "A4");
					} else if("N".equals(riskStatus) || "Y".equals(riskStatus)){
						
						if(userAccount != null) {
							BigDecimal auAmount = userAccount.getAuAmount();
							BigDecimal usedAmount = userAccount.getUsedAmount();
							BigDecimal remainAmount = auAmount.subtract(usedAmount);
							if(remainAmount.compareTo(new BigDecimal(500)) >= 0) {
								data.put("status", "B");
							} else {
								// 补充认证信息
								String alipayStatus = userAuthDo.getAlipayStatus();
								String creditStatus = userAuthDo.getCreditStatus();
								String jinpoStatus = userAuthDo.getJinpoStatus();
								String fundStatus = userAuthDo.getFundStatus();
								if("Y".equals(alipayStatus) && "Y".equals(creditStatus)
										&& "Y".equals(jinpoStatus) && "Y".equals(fundStatus)){
									data.put("status", "D");
								} else {
									data.put("status", "C");
								}
							}
						}
					}
				}
			} else {
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative + H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
			}
			
			return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(),"",data).toString();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
		}
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();
            
            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
	}

	@Override
	public  BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		return null;
	}
	
}
