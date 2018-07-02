package com.ald.fanbei.api.web.api.withhold;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserSeedService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserSeedDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月6日 下午1:20:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("getWithholdCheckApi")
public class WithholdCheckController implements ApiHandle {

	@Resource
	AfUserWithholdService afUserWithholdService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserBankcardService afUserBankCardService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserSeedService afUserSeedService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String userName = context.getUserName();
		if (userName == null || userName == "") {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		Map<String, Object> info = new HashMap<String, Object>();

		AfResourceDo resourceDo = afResourceService.getSingleResourceBytype(Constants.WITH_HOLD_SWITCH);

		if(resourceDo.getValue1().equals("Y")) {  //初步针对白名单使用开关
			AfUserSeedDo afUserSeedDo = afUserSeedService.getAfUserSeedDoByUserId(userDo.getRid());
			if(afUserSeedDo !=null) {
				info.put("isUserSeed", "Y");
			} else {
				info.put("isUserSeed", "N");
			}
		} 
		
		
		AfUserBankcardDo mainBankcard = afUserBankCardService.getUserMainBankcardByUserId(userDo.getRid());
		if (mainBankcard == null) {
			info.put("isMain", "N");
			logger.info("This is not bankCard userId=:" + userDo.getRid()); // 没有主卡
			resp.setResponseData(info);
			return resp;
		} else {
			info.put("isMain", "Y");
		}

		
		if (resourceDo != null) {
			Map<String, Object> json = (Map<String, Object>) JSONObject.parse(resourceDo.getValue3());
			String startTime1 = StringUtil.null2Str(json.get("startTime1"));
			String startTime2 = StringUtil.null2Str(json.get("startTime2"));
			String endTime1 = StringUtil.null2Str(json.get("endTime1"));
			String endTime2 = StringUtil.null2Str(json.get("endTime2"));

			Date date = new Date();// 当前时间
			DateFormat df = new SimpleDateFormat("HH:mm:ss");// 创建日期转换对象HH:mm:ss为时分秒
			String format = df.format(date);
			Date sdt1 = null;
			Date sdt2 = null;
			Date edt1 = null;
			Date edt2 = null;
			Date currTime = null;
			try {
				sdt1 = df.parse(startTime1); // 将字符串转换为date类型
				sdt2 = df.parse(startTime2);
				edt1 = df.parse(endTime1);
				edt2 = df.parse(endTime2);
				currTime = df.parse(format);
			} catch (ParseException e) {
				logger.error("DateTime is Exception: Exception= " + e);
			}
			if (currTime.getTime() > sdt1.getTime() && currTime.getTime() < edt1.getTime()) {  //如果在代扣处理中，不让用户进行操作
				info.put("isDeal", "Y");
				info.put("message", FanbeiExceptionCode.WHIT_HOLD_DEALING.getDesc());  //message 加入配置文件表中
				resp.setResponseData(info);
				return resp;
			} else {
				info.put("isDeal", "N");
			}
			if (currTime.getTime() > sdt2.getTime() && currTime.getTime() < edt2.getTime()) {	//如果在代扣处理中，不让用户进行操作
				info.put("isDeal", "Y");
				info.put("message", FanbeiExceptionCode.WHIT_HOLD_DEALING.getDesc());
				resp.setResponseData(info);
				return resp;
			} else {
				info.put("isDeal", "N");
			}
			 
		}
		resp.setResponseData(info);
		return resp;
	}

}
