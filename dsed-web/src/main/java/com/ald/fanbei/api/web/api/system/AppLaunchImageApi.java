package com.ald.fanbei.api.web.api.system;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserToutiaoDo;
import com.ald.fanbei.api.dal.domain.AppOpenLogDo;
import com.ald.fanbei.api.dal.domain.query.AfUserChangXiaoDo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

import java.security.MessageDigest;
import java.util.Date;


/**
 * app
 */
@Component("AppLaunchImageApi")
public class AppLaunchImageApi implements ApiHandle{

	private static final String RESOURCE_TYPE = "APP_LAUNCH_IMAGE";
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	TongdunUtil tongdunUtil;
	@Resource
	AfUserToutiaoService afUserToutiaoService;
	@Resource
	AfUserService afUserService;
	@Resource
	AppOpenLogService appOpenLogService;
	@Resource
	AfUserChangXiaoService afUserChangXiaoService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		addAppOpenLog(requestDataVo,context);
		ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String blackBox = ObjectUtils.toString(requestDataVo.getParams().get("blackBox"));
		String bqsBlackBox = ObjectUtils.toString(requestDataVo.getParams().get("bqsBlackBox"));
		String appVersion = context.getAppVersion()!=null?context.getAppVersion()+"":"";
		
		AfResourceDo resourceDo = afResourceService.getLaunchImageInfoByTypeAndVersion(RESOURCE_TYPE,appVersion);
		// 如果未配置部分版本
		if(resourceDo == null) {
			resourceDo = afResourceService.getLaunchImageInfoByType(RESOURCE_TYPE);
		}
		if(resourceDo == null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}
		JSONObject data = new JSONObject();
		String imageUrl = resourceDo.getPic1();
		String iPhoneXImageUrl = resourceDo.getPic2();
		data.put("imageUrl", imageUrl);
		data.put("iPhoneXImageUrl", iPhoneXImageUrl);
		data.put("advertiseUrl", resourceDo.getValue3());	

		response.setResponseData(data);
		
		//同盾处理
		try {
			if (StringUtils.isNotBlank(blackBox)) {
				tongdunUtil.activeOperate(request,requestDataVo.getId(), blackBox, CommonUtil.getIpAddr(request),appVersion, "","",true);
			}
		} catch (Exception e) {
			logger.error("appLaunchImageApi activeOperate error",e);
		}
		//头条激活处理
		ToutiaoAdOpen(requestDataVo,context);
	/*	try {
			//畅效广告平台回调处理
			ChangXiaoAdOpen(requestDataVo, context);
		}catch (Exception e) {
			logger.error("ChangXiaoAdOpen Operate error",e);
		}*/
		
		return response;
	}

	private void addAppOpenLog(RequestDataVo requestDataVo, FanbeiContext context) {
		try {
			String userName = context.getUserName();
			String id = ObjectUtils.toString(requestDataVo.getId(), "");
			String uuid = ObjectUtils.toString(requestDataVo.getParams().get("uuid"),"");
			String phoneType =  "";
			if(id.startsWith("i")){
				phoneType = "ios";
			}else if(id.startsWith("a")){
				phoneType = "android";
			}
			String appVersion = context.getAppVersion()!=null?context.getAppVersion()+"":"";
			AppOpenLogDo appOpenLogDo = new AppOpenLogDo();
			appOpenLogDo.setAppVersion(appVersion);
			appOpenLogDo.setPhoneType(phoneType);
			appOpenLogDo.setUserName(userName);
			appOpenLogDo.setGmtCreate(new Date());
			appOpenLogDo.setUuid(uuid);
			appOpenLogService.saveRecord(appOpenLogDo);
		}catch (Exception e){
			logger.error("addAppOpenLog:catch error",e);
		}
	}

	/**
	 * 头条激活处理
	 * @param requestDataVo
	 * @param context
	 */
	private void ToutiaoAdOpen(RequestDataVo requestDataVo, FanbeiContext context) {
		try {
			String userName = context.getUserName();
			AfUserDo afUserDo = afUserService.getUserByUserName(userName);
			if(afUserDo!=null){
				return;
			}
			String imei = ObjectUtils.toString(requestDataVo.getParams().get("IMEI"), null);
			String androidId = ObjectUtils.toString(requestDataVo.getParams().get("AndroidID"), null);
			String idfa = ObjectUtils.toString(requestDataVo.getParams().get("IDFA"), null);
			String imeiMd5="";
			logger.error("toutiaoopen para:"+imei+","+androidId+","+idfa);
			if(StringUtils.isNotBlank(imei)){
				imeiMd5=getMd5(imei);
			}
			if(StringUtils.isNotBlank(imei)||StringUtils.isNotBlank(androidId)||StringUtils.isNotBlank(idfa)){
				AfUserToutiaoDo tdo = afUserToutiaoService.getUserOpen(imeiMd5,androidId,idfa);
				if(tdo!=null){
					String callbackUrl = tdo.getCallbackUrl();
					if(callbackUrl.indexOf("&event_type")==-1){
						callbackUrl+="&event_type=0";
					}
					String result= HttpUtil.doGet(callbackUrl,20);
					if(result.indexOf("success")>-1){
						Long rid = tdo.getRid();
						Long userIdToutiao = context.getUserId()==null?-1l:context.getUserId();
						String userNameToutiao = context.getUserName()==null?"":context.getUserName();
						afUserToutiaoService.uptUserOpen(rid,userIdToutiao,userNameToutiao);
					}
					logger.error("toutiaoopen:update success,isopen=1,callbacr_url="+callbackUrl+",result="+result);
				}
			}
		}catch (Exception e){
			logger.error("toutiaoopen:catch error",e.getMessage());
		}
	}


	/**
	 * 畅效广告平台回调处理，如果当前用户是平台的老用户则不回调，否则告知畅效广告平台当前用户为其导流用户
	 * @param requestDataVo
	 * @param context
	 */
	private void ChangXiaoAdOpen(RequestDataVo requestDataVo, FanbeiContext context) {
		try {
			String userName = context.getUserName();//用户名不能为空
			AfUserDo afUserDo = afUserService.getUserByUserName(userName);
			logger.error("ChangXiaoAddOpen userName=" + userName + " ,afUserDo=" + afUserDo);
			if(afUserDo != null){//平台老用户，不进行回调
				logger.error("ChangXiaoAddOpen userName=" + userName + "afUserDo != null");
				return;
			}
			String imei = ObjectUtils.toString(requestDataVo.getParams().get("IMEI"), null);
			String idfa = ObjectUtils.toString(requestDataVo.getParams().get("IDFA"), null);
			logger.error("changxiao open para:imei="+imei+","+",idfa="+idfa);
			AfUserChangXiaoDo userDo =  new AfUserChangXiaoDo();
			userDo.setImei(imei);
			userDo.setIdfa(idfa);
			if(StringUtils.isNotEmpty(idfa)){
				AfUserChangXiaoDo tdo = afUserChangXiaoService.getUserOpen(userDo);//用户未启动过
				if(tdo != null){
					String callbackUrl = tdo.getCallbackUrl();
					String result= HttpUtil.doGet(callbackUrl,20);
					logger.debug("ChangXiaoAdOpen callbackUrl=" + callbackUrl + " ,result=" + result);
					if(result.equals("OK")){
						afUserChangXiaoService.updateUserOpen(tdo);
						logger.error("changxiao open:update success,isopen=1,callbacr_url="+callbackUrl+",result="+result);
					}
				}else{
					logger.error("changxiao open:userName has open");
				}
			}
		}catch (Exception e){
			logger.error("changxiao open:catch error",e.getMessage());
		}
	}




	/**
	 * 用于获取一个String的md5值
	 * @param str
	 * @return
	 */
	public static String getMd5(String str) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");

		byte[] bs = md5.digest(str.getBytes());
		StringBuilder sb = new StringBuilder(40);
		for(byte x:bs) {
			if((x & 0xff)>>4 == 0) {
				sb.append("0").append(Integer.toHexString(x & 0xff));
			} else {
				sb.append(Integer.toHexString(x & 0xff));
			}
		}
		return sb.toString();
	}

}
