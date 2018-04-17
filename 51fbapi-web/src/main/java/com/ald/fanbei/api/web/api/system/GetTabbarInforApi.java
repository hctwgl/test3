package com.ald.fanbei.api.web.api.system;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.CheckVersionBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;


@Component("getTabbarInforApi")
public class GetTabbarInforApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfOrderService afOrderService;
	@Resource
	BizCacheUtil bizCacheUtil;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfResourceDo> resourceList = afResourceService.getResourceListByTypeOrderBy(AfResourceType.HomeTabbar.getCode());
		Map<String, Object> tabbarInfor= getObjectWithResourceDolist(context,resourceList,requestDataVo);
		//resp.setResponseData(tabbarInfor);

		//鏌ヨ鐢ㄦ埛鐧诲綍鍚庢湭鏀粯璁㈠崟鏁�
		Long userId = context.getUserId();
		if(userId != null){
			//鏌ョ湅redis涓槸鍚︽湁鐧诲綍鏃堕棿,鏈夎鏄庝笉鏄涓�娆＄櫥褰�,鍒ゆ柇涓庝笂娆＄櫥褰曟椂闂存槸鍚︽槸鍚屼竴澶�
			String getTime = (String)bizCacheUtil.getObject(Constants.NO_FINISH_ORDER + userId);
			//璁板綍鐢ㄦ埛褰撳墠鐧诲綍鏃堕棿
			String nowLogin = DateUtil.getShortNow(new Date());
			//涓嶆槸绗竴娆＄櫥褰�
			if(getTime != null){
				//褰撳墠鐧诲綍鏃堕棿鍜宺edis涓瓨鍌ㄧ殑鏃堕棿鏄惁鏄悓涓�澶�
				if(!nowLogin.equals(getTime)){
					//鏍规嵁鐢ㄦ埛id鏌ヨ鐢ㄦ埛鏈敮浠樿鍗曟暟
					int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
					bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,Constants.SECOND_OF_ONE_DAY);
					tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
					resp.setResponseData(tabbarInfor);
					return resp;
				}else {
					tabbarInfor.put("noFinishOrderCount",0);
					if("Y".equals((String)bizCacheUtil.getObject(Constants.FIRST_TIME + userId))){
						bizCacheUtil.saveObject(Constants.FIRST_TIME+userId,nowLogin,0);
						int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
						tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
					}
					//bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,0);
					resp.setResponseData(tabbarInfor);
					return resp;
				}
			}
			//绗竴娆＄櫥褰�
			//璁板綍鐢ㄦ埛褰撳墠鐧诲綍鏃堕棿,瀛樺埌redis涓�
			bizCacheUtil.saveObject(Constants.NO_FINISH_ORDER+userId,nowLogin,Constants.SECOND_OF_ONE_DAY);
			//鏍规嵁鐢ㄦ埛id鏌ヨ鐢ㄦ埛鏈敮浠樿鍗曟暟
			int noFinishOrderCount = afOrderService.getNoFinishOrderCount(userId);
			if(noFinishOrderCount == 0){
				bizCacheUtil.saveObject(Constants.FIRST_TIME+userId,"Y",Constants.SECOND_OF_ONE_DAY);
			}
			tabbarInfor.put("noFinishOrderCount",noFinishOrderCount);
			resp.setResponseData(tabbarInfor);
			return resp;
		}
		tabbarInfor.put("noFinishOrderCount",0);
		resp.setResponseData(tabbarInfor);
		return resp;
	}
	
	private Map<String, Object> getObjectWithResourceDolist(FanbeiContext context,List<AfResourceDo> tabbarlist,RequestDataVo requestDataVo) {
		//鏄惁浣跨敤鍚庡彴浼犳潵鐨勫浘鐗囷紝鍚︾殑璇濊彍鍗曞浘鐗囩敤app鏈湴鍥剧墖
		AfResourceDo useImgDo = afResourceService
				.getConfigByTypesAndSecType(AfResourceType.IS_USE_IMG.getCode(), AfResourceSecType.IS_USE_IMG.getCode());
				//app 鍦╝ppstore 瀹℃牳淇℃伅锛屽鏋滆繕鏈鏍革紝鍒欑敤鍚庡彴浼犵殑鍥剧墖
		AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_IS_FOR_AUTH);
		boolean use = this.useImg(useImgDo, requestDataVo, context);
		Map<String, Object> index = new HashMap<String, Object>();
		for (AfResourceDo afResourceDo : tabbarlist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("title", afResourceDo.getName());
			if(use){
			data.put("imageUrl", afResourceDo.getValue());
			}
			data.put("titleColor", afResourceDo.getValue1());
			
			if(StringUtils.equals(afResourceDo.getSecType(), "HOME_NOMAL")){
				index.put("homeNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "HOME_SELECTED")){
				index.put("homeSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BRAND_NOMAL")){
				index.put("brandNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BRAND_SELECTED")){
				index.put("brandSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "STAGING_NOMAL")){
					handleIosStaging(context,requestDataVo,data,resourceInfo);
				index.put("stagingNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "STAGING_SELECTED")){
				handleIosStaging(context,requestDataVo,data,resourceInfo);
				index.put("stagingSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "MAIN_NOMAL")){
				index.put("mainNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "MAIN_SELECTED")){
				index.put("mainSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_NOMAL")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowNomal", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_SELECTED")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowSelected", data);
			}
			if(StringUtils.equals(afResourceDo.getSecType(), "BORROW_HIGHLIGHT")){
				handleIosBorow(context,requestDataVo,data,resourceInfo);
				index.put("borrowHighLight", data);
			}
			
		}

		return index;
	}
	/**
	 * 閽堝IOS 鍦╝ppStore瀹℃牳涓椂锛屽鐞嗗簳閮╰ab鍊熼挶妯″潡涓烘悳鍛�
	 * @param context
	 * @param requestDataVo
	 * @param data
	 */
	private void handleIosBorow(FanbeiContext context,RequestDataVo requestDataVo,Map<String, Object> data,AfResourceDo resourceInfo) {
		
		 Map<String, Object> params = requestDataVo.getParams();
	        String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
	        if (resourceInfo == null) {
	        	return;
	        } 
		 //闇�瑕佹墦寮�涓轰簡瀹℃牳鐨勭浉鍏崇増鏈�
        //VALUE鏄负浜咺OS瀹℃牳
        if(requestDataVo.getId().startsWith("i")) {
        	String iosCheckVersion = resourceInfo.getValue();
        	if (!StringUtils.isBlank(iosCheckVersion)) {
        		List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "鎼滃憲" : data.get("title"));
        	}
        } else {
        //VALUE2鏄负浜咥ndroid瀹℃牳
        	String androidCheckVersion = resourceInfo.getValue2();
        	if (!StringUtils.isBlank(androidCheckVersion)) {
        		List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
        		CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
        		data.put("title", array.contains(desVersion) ? "鎼滃憲" : data.get("title"));
        	}
        }
	}

	private void handleIosStaging(FanbeiContext context,RequestDataVo requestDataVo,Map<String, Object> data,AfResourceDo resourceInfo) {

		Map<String, Object> params = requestDataVo.getParams();
		String channelCode = ObjectUtils.toString(params.get("channelCode"), null);
		if (resourceInfo == null) {
        	return;
        } 
		//闇�瑕佹墦寮�涓轰簡瀹℃牳鐨勭浉鍏崇増鏈�
		//VALUE鏄负浜咺OS瀹℃牳
		if(requestDataVo.getId().startsWith("i")) {
			String iosCheckVersion = resourceInfo.getValue();
			if (!StringUtils.isBlank(iosCheckVersion)) {
				List<CheckVersionBo> array = JSONArray.parseArray(iosCheckVersion, CheckVersionBo.class);
				CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
				data.put("title", array.contains(desVersion) ? "鏈嶅姟" : data.get("title"));
			}
		} else {
			//VALUE2鏄负浜咥ndroid瀹℃牳
			String androidCheckVersion = resourceInfo.getValue2();
			if (!StringUtils.isBlank(androidCheckVersion)) {List<CheckVersionBo> array = JSONArray.parseArray(androidCheckVersion, CheckVersionBo.class);
				CheckVersionBo desVersion = new CheckVersionBo(channelCode, context.getAppVersion());
				data.put("title", array.contains(desVersion) ? "鏈嶅姟" : data.get("title"));
			}
		}
	}
	
	/**
	 * 鏄惁浣跨敤鍚庡彴浼犳潵鐨勫浘鐗囧仛鑿滃崟鏍忕殑鍥剧墖
	 * @param useImgDo 鏄惁浣跨敤鍚庡彴浼犲浘鐗�
	 * @return true:鐢ㄥ悗鍙扮殑 锛宖alse:鐢╝pp鏈湴鐨�
	 */
	private boolean useImg(AfResourceDo useImgDo,RequestDataVo requestDataVo,FanbeiContext context){
		boolean use = true;
		if(useImgDo!=null){
			 use = "1".equals(useImgDo.getValue())?true:false;
		}
		return use;
	}

}
