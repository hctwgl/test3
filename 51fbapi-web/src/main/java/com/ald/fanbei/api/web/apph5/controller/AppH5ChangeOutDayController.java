package com.ald.fanbei.api.web.apph5.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfRedPacketPoolService.Redpacket;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfUserOutDayDao;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserOutDayDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
* @ClassName: AppH5ChangeOutDayController 
* @Description: 用户修改账单日
* @author yuyue
* @date 2017年11月6日 下午1:17:34 
*
 */
@Controller
@RequestMapping("/fanbei-web/changeOutDay")
public class AppH5ChangeOutDayController extends BaseController{
	@Resource
	AfUserService afUserService;
	
	@Resource
	AfUserOutDayDao afUserOutDayDao;
	
	@Resource
	AfBorrowBillService afBorrowBillService;
	
	@RequestMapping(value = "/getUserOutDay", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String getUserOutDay(HttpServletRequest request) throws IOException {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
    	try{
    		context = doWebCheck(request, true);
    		AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
    		Map<String, Object> data = new HashMap<String, Object>();
    		if (afUser != null && afUser.getRid() != null) {
    			AfUserOutDayDo userOutDay = afUserOutDayDao.getUserOutDayByUserId(afUser.getRid());
    			if (userOutDay != null && userOutDay.getId() != null) {
        			data.put("outDay", userOutDay.getOutDay());
        			data.put("payDay", userOutDay.getPayDay());
				}else {
					data.put("outDay", 10);
					data.put("payDay", 20);
				}
    			data.put("avatar", afUser.getAvatar());
    			data.put("userName", afUser.getUserName());
    			data.put("nick", afUser.getNick());
			}
    		resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
    	} catch (Exception e){
    		logger.error(e.getMessage(), e);
    		resp = H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString());
    	}
		return resp.toString();
    }

	@RequestMapping(value = "/getOutDayList", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String getOutDayList(HttpServletRequest request) throws IOException {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
    	try{
    		context = doWebCheck(request, true);
    		AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
    		Map<String, Object> data = new HashMap<String, Object>();
    		if (afUser != null && afUser.getRid() != null) {
    			data.put("avatar", afUser.getAvatar());
    			data.put("userName", afUser.getUserName());
    			data.put("nick", afUser.getNick());
    			AfUserOutDayDo userOutDay = afUserOutDayDao.getUserOutDayByUserId(afUser.getRid());
    			List<Map<String, Integer>> outDayList = new ArrayList<Map<String,Integer>>();
    			if (userOutDay != null && userOutDay.getId() != null) {
        			Calendar calendar = Calendar.getInstance();
        			Calendar outDayCalendar = Calendar.getInstance();
        			outDayCalendar.setTime(userOutDay.getGmtModify());
        			if (calendar.get(Calendar.YEAR) == outDayCalendar.get(Calendar.YEAR)) {
        				// 判断是否是新用户，新用户logCount = 0
        				int logCount = afUserOutDayDao.countUserOutDayLogByUserId(afUser.getRid());
        				if (logCount > 0) {
        					data.put("outDay", userOutDay.getOutDay());
        					data.put("payDay", userOutDay.getPayDay());
        					resp = H5CommonResponse.getNewInstance(true, "2","",data);
        					return resp.toString();
						}
					}
        			data.put("outDay", userOutDay.getOutDay());
					data.put("payDay", userOutDay.getPayDay());
        			outDayList = this.getOutDayList(userOutDay.getOutDay());
				}else {
					data.put("outDay", 10);
					data.put("payDay", 20);
					outDayList = this.getOutDayList(10);
				}
    			int countNotPayOverdueBill = afBorrowBillService.countNotPayOverdueBill(afUser.getRid());
    			if (countNotPayOverdueBill > 0) {
    				resp = H5CommonResponse.getNewInstance(true, "1","",data);
    				return resp.toString();
    			}
    			data.put("outDayList", outDayList);
			}
    		resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
    	} catch (Exception e){
    		logger.error(e.getMessage(), e);
    		resp = H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString());
    	}
		return resp.toString();
    }
	
	@RequestMapping(value = "/updateOutDay", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
    public String updateOutDay(HttpServletRequest request) throws IOException {
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
    	try{
    		int outDay = NumberUtil.objToIntDefault(request.getParameter("outDay"), -1);
    		int payDay = NumberUtil.objToIntDefault(request.getParameter("payDay"), -1);
    		Map<String, Object> data = new HashMap<String, Object>();
    		if (outDay < 0) {
    			resp = H5CommonResponse.getNewInstance(false, "出账日错误");
				return resp.toString();
			}
    		if (payDay < 0) {
    			resp = H5CommonResponse.getNewInstance(false, "还款日日错误");
    			return resp.toString();
    		}
    		context = doWebCheck(request, true);
    		AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
    		if (afUser != null && afUser.getRid() != null) {
    			data.put("avatar", afUser.getAvatar());
    			data.put("userName", afUser.getUserName());
    			data.put("nick", afUser.getNick());
    			data.put("outDay", outDay);
    			data.put("payDay", payDay);
    			AfUserOutDayDo userOutDay = afUserOutDayDao.getUserOutDayByUserId(afUser.getRid());
    			if(userOutDay != null && userOutDay.getId() != null) {
    				Calendar calendar = Calendar.getInstance();
        			Calendar outDayCalendar = Calendar.getInstance();
        			outDayCalendar.setTime(userOutDay.getGmtModify());
        			if (calendar.get(Calendar.YEAR) == outDayCalendar.get(Calendar.YEAR)) {
        				// 判断是否是新用户，新用户logCount = 0
        				int logCount = afUserOutDayDao.countUserOutDayLogByUserId(afUser.getRid());
        				if (logCount > 0) {
        					data.put("outDay", userOutDay.getOutDay());
        					data.put("payDay", userOutDay.getPayDay());
        					resp = H5CommonResponse.getNewInstance(true, "1","",data);
        					return resp.toString();
						}
					}
    				if (outDay == userOutDay.getOutDay()) {
    					resp = H5CommonResponse.getNewInstance(false, "出账日重复");
    					return resp.toString();
					}
    				if (afBorrowBillService.updateUserOutDay(afUser.getRid(),outDay,payDay) < 1) {
    					logger.error("addUserOutDay false , userId = " + afUser.getRid() + " ,outDay =" + outDay + " ,payDay =" + payDay);
    					resp = H5CommonResponse.getNewInstance(false, "修改失败");
					}
    				
    			}else {
    				if (afBorrowBillService.addUserOutDay(afUser.getRid(),outDay,payDay) < 1) {
    					logger.error("updateUserOutDay false , userId = " + afUser.getRid() + " ,outDay =" + outDay + " ,payDay =" + payDay);
    					resp = H5CommonResponse.getNewInstance(false, "修改失败");
					}
    			}
    			resp = H5CommonResponse.getNewInstance(true, "修改成功", "", data);
    		} 
    	}catch (Exception e){
    		logger.error(e.getMessage(), e);
    		resp = H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString());
    	}
		return resp.toString();
    }
	
    private List<Map<String, Integer>> getOutDayList(Integer outDay) {
    	List<Map<String, Integer>> outDayList = new ArrayList<Map<String,Integer>>();
    	for (int i = 1; i < 29; i++) {
			if (outDay == i || i == 19 || i == 20) {
				continue;
			}
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("outDay", i);
			int j = i + 10;
			if (j > 30) {
				j = j - 30;
			}
			map.put("payDay", j);
			outDayList.add(map);
		}
		return outDayList;
	}

	@Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
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
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

    
    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

}
