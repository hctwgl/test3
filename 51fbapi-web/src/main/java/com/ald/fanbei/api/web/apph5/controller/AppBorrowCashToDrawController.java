package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/** 
 * @类描述:
 * @author fanmanfu 创建时间：2017年9月1日 下午4:12:38 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/activity/")
public class AppBorrowCashToDrawController extends BaseController {

	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfBorrowCashService afBorrowCashService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private JpushService jpushService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private SmsUtil smsUtil;
	
	@RequestMapping(value = "/toActivitiesPage", produces = "text/html;charset=UTF-8")
	public String toActivitiesPage(Model model) {
		//从缓存中取数据
		String Time = (String) bizCacheUtil.getObject("Start_Time");
		 String winAmount =(String) bizCacheUtil.getObject("winAmount");
		 if(winAmount == null){
			 bizCacheUtil.saveObject("winAmount","600", 60 * 60 * 24*10);
			 winAmount="600";
		 }
		if(StringUtil.isBlank(Time)){
			AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.BORROWCASH_ACTIVITYS_TYPR, Constants.BORROWCASH_ACTIVITYS_SECTYPR);
			Date date1=null;   
			try {
			    	String date = resource.getValue();
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");  
					date1 = sdf.parse(date);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			//把活动的时间经过处理再传给前端
			if(date1 != null){
				String d1 = DateUtil.formatAndMonthAndDay(date1);
				Date d2 = DateUtil.addDays(date1,1);
				String date2 = DateUtil.formatAndMonthAndDay(d2);
				Date d3 = DateUtil.addDays(d2,1);
				String date3 = DateUtil.formatAndMonthAndDay(d3);
				Date d4 = DateUtil.addDays(d3,1);
				String date4 = DateUtil.formatAndMonthAndDay(d4);
				Date d5 = DateUtil.addDays(d4,1);
				String date5 = DateUtil.formatAndMonthAndDay(d5);
				Map<String,String> date=new HashMap<String,String>();
				date.put("date1", d1);
				date.put("date2", date2);
				date.put("date3", date3);
				date.put("date4", date4);
				date.put("date5", date5);
				date.put("dates", resource.getValue());
				bizCacheUtil.saveObject("Start_Time", JsonUtil.toJSONString(date), 60 * 60 * 24*7);
				//model.addAllAttributes(date);
			}
			model.addAttribute("winAmount", winAmount);
			model.addAttribute("winAmounts",Integer.parseInt(winAmount)-100);
			return "fanbei-web/activity/billion";
		}
		Map<String,String> date = JSONObject.parseObject(Time, Map.class);
		model.addAllAttributes(date);
		model.addAttribute("winAmount", winAmount);
		model.addAttribute("winAmounts",Integer.parseInt(winAmount)-100);
		return "fanbei-web/activity/billion";
	}

	@RequestMapping(value = "/borrowCashActivities", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String borrowCashActivities() {
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.BORROWCASH_ACTIVITYS_TYPR, Constants.BORROWCASH_ACTIVITYS_SECTYPR);
		 SimpleDateFormat simpleDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     Date date1 = null;
	     Date date2 = null;
		try {
			date1 = simpleDateFormat .parse(resource.getValue());
			date2 = simpleDateFormat .parse(resource.getValue2());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
	     //long timeStemp = date.getTime();
	     Map<String, Object> map=new HashMap();
	     map.put("startrTime", date1);
	     map.put("endTime", date2);
	    
		BigDecimal sumAmount=null;
		DecimalFormat df = new DecimalFormat("0");
		try {
			sumAmount = (BigDecimal) bizCacheUtil.getObject("BorrowCash_Sum_Amount");
		} catch (Exception e) {
			logger.info("borrowCashActivities redis get is fail" + e);
		}
		if (sumAmount == null) {
			sumAmount = afBorrowCashService.getBorrowCashSumAmount();
		}
		try {
			bizCacheUtil.saveObject("BorrowCash_Sum_Amount", sumAmount, 60 * 60 * 24 * 7);
		} catch (Exception e) {
			logger.info("borrowCashActivities redis save is fail" + e);
		}
		char[] split = (df.format(sumAmount)+"").toCharArray();
		map.put("amount", split);
		String jsonString = JsonUtil.toJSONString(map);
		H5CommonResponse response = H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(), "",jsonString );
		return JsonUtil.toJSONString(response);
	}

	@RequestMapping(value = "/randomUser",method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String randomUser(HttpServletRequest request) {
		String winAmount = request.getParameter("winAmount");
		
		List<String> users = afBorrowCashService.getRandomUser(); // 得到中奖用户id
		List<String> userNames = afUserService.getUserNameByUserId(users); // 得到中奖用户user_name
		List<String> list1 = afBorrowCashService.getNotRandomUser(users);// 得到当天未中奖用户id
		List<String> list2 = afUserService.getUserNameByUserId(list1); //得到未中奖用户user_name
		String jsonString = JsonUtil.toJSONString(userNames);
		
	
		// 每日中奖用户推送
		for (String userName : userNames) {
			try {
				jpushService.pushBorrowCashActivitys(userName, winAmount, "Win");
			} catch (Exception e) {
				logger.info(userName + "pushBorrowCashActivitys is fail," + e);
			}
		}
		// 每日除中奖用户外全部用户推送
		for (String userName : list2) {
			try {
				jpushService.pushBorrowCashActivitys(userName, winAmount, "notWin");
			} catch (Exception e) {
				logger.info(userName + "pushBorrowCashActivitys is fail," + e);
			}
		}
		// 发送短信
		for (String userName : userNames) {
			try {
				smsUtil.sendBorrowCashActivitys(userName, "哇！幸运值爆棚的你在“破十五亿”活动中获得" + winAmount + "元现金红包，快去查收惊喜吧。回T退订");
			} catch (Exception e) {
				logger.info("sendBorrowCashActivitys " + userName + " is fails," + e);
			}
		}
		// 给用户账号打钱*
		int amount = Integer.parseInt(winAmount);
		if(amount <= 1100){
			try {
				afUserAccountService.updateBorrowCashActivity(amount, users);
			} catch (FanbeiException e) {
				logger.info("sendBorrowCashActivitys is fails," + e);
			}
		}
		//传给前端一个开奖金额
		String winamount =(String) bizCacheUtil.getObject("winAmount");
		bizCacheUtil.saveObject("winAmount",Integer.parseInt(winamount)+100+"", 60*60*24*10);
		// 中奖用户存入缓存
		String userJson = (String)bizCacheUtil.getObject("winAmount_Win_User");
		if(StringUtil.isBlank(userJson)){
			try {
				Map<String, Object> map=new HashMap();
				map.put(winAmount, jsonString);
				bizCacheUtil.saveObject("winAmount_Win_User", JsonUtil.toJSONString(map), 60 * 60 * 24 * 7);
			} catch (Exception e) {
				logger.info("randomUser winAmount_Win_User redis save is fail," + jsonString + "" + e);
			}
			return jsonString;
		}
		try {
			Map<String, Object> map=new HashMap();
			map = JSONObject.parseObject(userJson);
			map.put(winAmount, jsonString);
			bizCacheUtil.saveObject("winAmount_Win_User", JsonUtil.toJSONString(map), 60 * 60 * 24 * 7);
		} catch (Exception e) {
			logger.info("randomUser winAmount_Win_User redis save is fail," + jsonString + "" + e);
		}
		return jsonString;

	}

	@RequestMapping(value = "/getWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getWinUser() {
		String users=null;
		try{
			users = (String) bizCacheUtil.getObject("winAmount_Win_User");
		}catch(Exception e){
			logger.info("getWinUser redis get is fail"+e);
		}
			return users;
	}

	@RequestMapping(value = "/getBillionWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBillionWinUser() {
		String amount = null;
		try {
			amount = (String) bizCacheUtil.getObject("Billion_Win_User");
		} catch (Exception e) {
			logger.error("getBillionWinUser redis get is fail");
		}
		return amount;
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
