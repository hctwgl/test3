package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;

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
		// 从缓存中取数据
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.BORROWCASH_ACTIVITYS_TYPR, Constants.BORROWCASH_ACTIVITYS_SECTYPR);
		String Time = (String) bizCacheUtil.getObject("Start_Time");
		String winAmount = (String) bizCacheUtil.getObject("winAmount");
		if (winAmount == null) {
			bizCacheUtil.saveObject("winAmount", resource.getValue1(), 60 * 60 * 24 * 10);
			winAmount = resource.getValue1();
		}
		Map<String, String>  Amount = new HashMap<String, String>();
		int amount = Integer.parseInt(resource.getValue1());
		Amount.put("Amount1",amount+"");
		Amount.put("Amount2",(amount+100)+"");
		Amount.put("Amount3",(amount+200)+"");
		Amount.put("Amount4",(amount+300)+"");
		Amount.put("Amount5",(amount+400)+"");
		if (StringUtil.isBlank(Time)) {
			Date date1 = null;
			try {
				String date = resource.getValue();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date1 = sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// 把活动的时间经过处理再传给前端
			if (date1 != null) {
				String d1 = DateUtil.formatAndMonthAndDay(date1);
				Date d2 = DateUtil.addDays(date1, 1);
				String date2 = DateUtil.formatAndMonthAndDay(d2);
				Date d3 = DateUtil.addDays(d2, 1);
				String date3 = DateUtil.formatAndMonthAndDay(d3);
				Date d4 = DateUtil.addDays(d3, 1);
				String date4 = DateUtil.formatAndMonthAndDay(d4);
				Date d5 = DateUtil.addDays(d4, 1);
				String date5 = DateUtil.formatAndMonthAndDay(d5);
				Map<String, String> date = new HashMap<String, String>();
				date.put("date1", d1);
				date.put("date2", date2);
				date.put("date3", date3);
				date.put("date4", date4);
				date.put("date5", date5);
				date.put("dates", resource.getValue());
				bizCacheUtil.saveObject("Start_Time", JsonUtil.toJSONString(date), 60 * 60 * 24 * 7);
				model.addAllAttributes(date);
				
			}
			model.addAllAttributes(Amount);
			model.addAttribute("winAmount", winAmount);
			model.addAttribute("winAmounts", Integer.parseInt(winAmount) - 100);
			return "fanbei-web/activity/billion";
		}
		Map<String, String> date = JSONObject.parseObject(Time, Map.class);
		model.addAllAttributes(Amount);
		model.addAllAttributes(date);
		model.addAttribute("winAmount", winAmount);
		model.addAttribute("winAmounts", Integer.parseInt(winAmount) - 100);
		return "fanbei-web/activity/billion";
	}

	@RequestMapping(value = "/borrowCashActivities", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String borrowCashActivities() {
		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.BORROWCASH_ACTIVITYS_TYPR, Constants.BORROWCASH_ACTIVITYS_SECTYPR);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date1 = null;
		Date date2 = null;
		try {
			date1 = simpleDateFormat.parse(resource.getValue());
			date2 = simpleDateFormat.parse(resource.getValue2());
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		// long timeStemp = date.getTime();
		Date currentDate = new Date();
		Map<String, Object> map = new HashMap();
		map.put("startrTime", date1);
		map.put("currentDate", currentDate);
		map.put("endTime", date2);

		BigDecimal sumAmount = null;
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
		char[] split = (df.format(sumAmount) + "").toCharArray();
		map.put("amount", split);
		String jsonString = JsonUtil.toJSONString(map);
		H5CommonResponse response = H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", jsonString);
		return JsonUtil.toJSONString(response);
	}

	@RequestMapping(value = "/getWinUsers", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getWinUsers() {
		String users = null;
		try {
		List<Object> user =  JSONArray.parseArray((String)bizCacheUtil.getObject("win_user"));
		List<String> list =new ArrayList<String>();	
		for (Object string : user) {
				String str = (String) string;
				String ss = str.substring(0,str.length()-(str.substring(3)).length())+"****"+str.substring(7); 
				list.add(ss);
		}
			users = JsonUtil.toJSONString(list);
		} catch (Exception e) {
			logger.info("getWinUsers redis get is fail" + e);
		}
		return users;
	}
	
	@RequestMapping(value = "/getWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getWinUser() {
		String users = null;
		try {
			Map<String,String> map = JSONObject.parseObject((String) bizCacheUtil.getObject("winAmount_Win_User"), Map.class);
			Map<String,String> maps = new HashMap<String,String>();
			for (Entry<String, String> entry : map.entrySet()) {
				List<Object> user = JSONArray.parseArray(entry.getValue());
				List<String> list =new ArrayList<String>();	
				for (Object string : user) {
					String str = (String) string;
					String ss = str.substring(0,str.length()-(str.substring(3)).length())+"****"+str.substring(7); 
					list.add(ss);
				}
				maps.put(entry.getKey(), JsonUtil.toJSONString(list));
			}
			users=JsonUtil.toJSONString(maps);
		} catch (Exception e) {
			logger.info("getWinUser redis get is fail" + e);
		}
		return users;
	}

	@RequestMapping(value = "/getBillionWinUser", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getBillionWinUser() {
		String amount = null;
		try {
			String str = (String) bizCacheUtil.getObject("Billion_Win_User");
			String ss = str.substring(0,str.length()-(str.substring(3)).length())+"****"+str.substring(7); 
			amount =JsonUtil.toJSONString(ss);
		} catch (Exception e) {
			logger.error("getBillionWinUser redis get is fail");
		}
		return amount;
	}

	@RequestMapping(value = "/delCache", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String delCache() {
		bizCacheUtil.delCache("winAmount");
		bizCacheUtil.delCache("Start_Time");
		bizCacheUtil.delCache("winAmount_Win_User");
		bizCacheUtil.delCache("win_user");
		bizCacheUtil.delCache("Billion_Win_User");
		bizCacheUtil.delCache("BorrowCash_Sum_Amount");
		logger.info("delCache is SUCCESS,endDate = " + new Date());
		return null;
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
