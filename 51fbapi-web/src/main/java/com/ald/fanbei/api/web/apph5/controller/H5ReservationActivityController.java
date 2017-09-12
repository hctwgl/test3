package com.ald.fanbei.api.web.apph5.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfGoodsReservationService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfGoodsReservationStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.GoodsReservationWebFailStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.OrderNoUtils;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfGoodsReservationDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述:iphone8-预约
 * @author fanmanfu 创建时间：2017年9月8日 上午10:49:34
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/activity")
public class H5ReservationActivityController extends BaseController {

	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfGoodsService afGoodsService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfUserService afUserService;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	private AfGoodsReservationService afGoodsReservationService;
	
	@RequestMapping(value = "/getActivityGoods", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getActivityGoods(HttpServletRequest request) {

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(AfResourceType.ReservationActivity.getCode(), AfResourceType.Iphone8ReservationActivity.getCode());
		Map<String, Object> jsonObjRes = (Map<String, Object>) JSONObject.parse(resource.getValue3());
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startTime = null;
		Date endTime = null;
		try {
			startTime = parser.parse(StringUtil.null2Str(jsonObjRes.get("startTime")));
			endTime = parser.parse(StringUtil.null2Str(jsonObjRes.get("endTime")));
		} catch (ParseException e) {
			logger.info("get startTime is fail"+e);
		}
		Map map = new HashMap();
		Date date = new Date();
		String status="FAIL";
		String loginStatus="N";
		//判断活动是否开始
		if (DateUtil.compareDate(date, startTime)) {
			
			long days = DateUtil.getNumberOfDatesBetween(startTime, date);
			List<String> list = new ArrayList();
			if (days <= 0) {

			} else if (days == 1) {
				list.add("138****6848");
				list.add("132****8971");
				list.add("176****3627");
				list.add("158****0372");
				list.add("138****7192");
			} else if (days == 2) {
				list.add("132****8347");
				list.add("185****4274");
				list.add("176****5251");
				list.add("177****4062");
				list.add("173****6792");
			} else if (days == 3) {
				list.add("130****6037");
				list.add("186****4375");
				list.add("158****2687");
				list.add("131****4805");
				list.add("132****7437");
			} else if (days == 4) {
				list.add("158****3764");
				list.add("176****3547");
				list.add("138****4341");
				list.add("188****3302");
				list.add("130****4104");
			} else if (days == 5) {
				list.add("131****0765");
				list.add("158****0513");
				list.add("138****3276");
				list.add("130****6317");
				list.add("177****8357");
			} else {
				list.add("133****3826");
				list.add("151****0435");
				list.add("155****0576");
				list.add("150****6347");
				list.add("176****8127");
			}
			//查询预约状态
			try {
				AfUserDo userDo =null;
				String s = null;
				String appInfo = getAppInfo(request.getHeader("Referer"));
				String userName =  StringUtil.null2Str(JSON.parseObject(appInfo).get("userName"));
				userDo = afUserService.getUserByUserName(userName);
				if(userDo != null){
					loginStatus="Y";
					s = afGoodsReservationService.getGoodsReservationStatusByUserId(userDo.getRid());
					if (DateUtil.compareDate(endTime,date)) {
						if(StringUtil.isNotBlank(s)){
							status=s;
						}
					} else {
						status="N";
					}
				}
			} catch (Exception e) {
				logger.info("getActivityGoods is fail"+e);
			}
			map.put("status", status);
			map.put("loginStatus", loginStatus);
			map.put("winUsers", list);
		}
		return JsonUtil.toJSONString(map);
	}
	 private static String getAppInfo(String url) {
	        if (StringUtil.isBlank(url)) {
	            return null;
	        }
	        String result = "";
	        try {
	            Map<String, List<String>> params = new HashMap<String, List<String>>();
	            String[] urlParts = url.split("\\?");
	            if (urlParts.length > 1) {
	                String query = urlParts[1];
	                for (String param : query.split("&")) {
	                    String[] pair = param.split("=");
	                    String key = URLDecoder.decode(pair[0], "UTF-8");
	                    String value = "";
	                    if (pair.length > 1) {
	                        value = URLDecoder.decode(pair[1], "UTF-8");
	                    }

	                    List<String> values = params.get(key);
	                    if (values == null) {
	                        values = new ArrayList<String>();
	                        params.put(key, values);
	                    }
	                    values.add(value);
	                }
	            }
	            List<String> _appInfo = params.get("_appInfo");
	            if (_appInfo != null && _appInfo.size() > 0) {
	                result = _appInfo.get(0);
	            }
	            return result;
	        } catch (UnsupportedEncodingException ex) {
	            throw new AssertionError(ex);
	        }
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
