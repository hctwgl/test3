package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
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
@RequestMapping("/app/activity/")
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
		bizCacheUtil.delCache("Start_Time");
		String Time = (String) bizCacheUtil.getObject("Start_Time");
		 String winAmount =(String) bizCacheUtil.getObject("winAmount");
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
				bizCacheUtil.saveObject("Start_Time", JsonUtil.toJSONString(date), 60 * 60 * 24);
				model.addAllAttributes(date);
			}
			model.addAttribute("winAmount", winAmount);
			return "fanbei-web/activity/billion";
		}
		Map<String,String> date = JSONObject.parseObject(Time, Map.class);
		model.addAllAttributes(date);
		model.addAttribute("winAmount", winAmount);
		return "fanbei-web/activity/billion";
	}

	@RequestMapping(value = "/borrowCashActivities", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String borrowCashActivities() {
		bizCacheUtil.delCache("winAmount");
		bizCacheUtil.saveObject("winAmount","600", 60 * 60 * 24*30);
		BigDecimal sumAmount=null;
		try {
			sumAmount = (BigDecimal) bizCacheUtil.getObject("BorrowCash_Sum_Amount");
			if (sumAmount != null) {
				char[] split = (sumAmount.stripTrailingZeros()+"").toCharArray();
				String jsonString = JsonUtil.toJSONString(split);
				H5CommonResponse response = H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(), "",jsonString );
				return JsonUtil.toJSONString(response);
				
			}
		} catch (Exception e) {
			logger.info("borrowCashActivities redis get is fail" + e);
		}
		sumAmount = afBorrowCashService.getBorrowCashSumAmount();
		try {
			bizCacheUtil.saveObject("BorrowCash_Sum_Amount", sumAmount, 60 * 60 * 24 * 7);
		} catch (Exception e) {
			logger.info("borrowCashActivities redis save is fail" + e);
		}
		char[] split = (sumAmount.stripTrailingZeros()+"").toCharArray();
		String jsonString = JsonUtil.toJSONString(split);
		H5CommonResponse response = H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.SUCCESS.getDesc(), "",jsonString );
		return JsonUtil.toJSONString(response);
	}

	@RequestMapping(value = "/randomUser",method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String randomUser(HttpServletRequest request, ModelMap model) {
		String winAmount = request.getParameter("winAmount");
		
		List<String> users = afBorrowCashService.getRandomUser(); // 得到中奖用户id
		List<String> list1 = afBorrowCashService.getNotRandomUser(users);// 得到当天未中奖用户id
		List<String> userNames = afUserService.getUserNameByUserId(users); // 得到中奖用户user_name
		List<String> list2 = afUserService.getUserNameByUserId(list1);
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
		// 给用户账号打钱
		int amount = Integer.parseInt(winAmount);
		try {
			afUserAccountService.updateBorrowCashActivity(amount, users);
		} catch (FanbeiException e) {
			logger.info("sendBorrowCashActivitys is fails," + e);
		}
		bizCacheUtil.saveObject("winAmount",amount+100+"", 60);
		// 中奖用户存入缓存
		try {
			bizCacheUtil.saveObject(winAmount + "_Win_User", jsonString, 60 * 60 * 24 * 7);
		} catch (Exception e) {
			logger.info("randomUser redis save is fail," + jsonString + "" + e);
		}
		
		return jsonString;
	}

	@RequestMapping(value = "/getWinUser",  produces = "text/html;charset=UTF-8")
	@ResponseBody
	public void getWinUser(Model model) {
		try {
			String users = (String) bizCacheUtil.getObject("600_Win_User");
			List<String> list = JSONObject.parseObject(users, List.class);
			model.addAttribute("Six_Hundred", list);
		} catch (Exception e) {
			logger.error("600_Win_User get is fail");
		}

		try {
			String users = (String) bizCacheUtil.getObject("700_Win_User");
			List<String> list = JSONObject.parseObject(users, List.class);
			model.addAttribute("Seven_Hundred", list);
		} catch (Exception e) {
			logger.error("700_Win_User get is fail");
		}

		try {
			String users = (String) bizCacheUtil.getObject("800_Win_User");
			List<String> list = JSONObject.parseObject(users, List.class);
			model.addAttribute("Eight_Hundred", list);
		} catch (Exception e) {
			logger.error("800_Win_User get is fail");
		}

		try {
			String users = (String) bizCacheUtil.getObject("900_Win_User");
			List<String> list = JSONObject.parseObject(users, List.class);
			model.addAttribute("Nine_Hundred", list);
		} catch (Exception e) {
			logger.error("900_Win_User get is fail");
		}

		try {
			String users = (String) bizCacheUtil.getObject("1000_Win_User");
			List<String> list = JSONObject.parseObject(users, List.class);
			model.addAttribute("Thousand", list);
		} catch (Exception e) {
			logger.error("1000_Win_User get is fail");
		}

	}

	@RequestMapping(value = "/getBillionWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBillionWinUser() {
		String amount = null;
		try {
			amount = (String) bizCacheUtil.getObject("Billion_Win_User");
		} catch (Exception e) {
			logger.error("Billion_Win_User get is fail");
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
