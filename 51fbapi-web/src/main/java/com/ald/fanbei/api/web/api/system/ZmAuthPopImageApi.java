package com.ald.fanbei.api.web.api.system;

import java.util.Date;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.third.util.ZhimaUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;


@Component("zmAuthPopImageApi")
public class ZmAuthPopImageApi implements ApiHandle {

	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfUserAuthService afUserAuthService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse response = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		JSONObject data = new JSONObject();
		//如果用户已经登录且未完成芝麻相关认证，则弹窗处理
		Long userId = context.getUserId();
		if(userId!=null && userId>0){
			String envType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			//当前时间戳
			Long currMillis = System.currentTimeMillis();
			//芝麻信息认证启动页配置 val:弹窗图片地址  val2:打开开关Y/N val3:弹窗间隔时间ms
			AfResourceDo zmPopImageResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ZHIMA_VERIFY_CONFIG.getCode(), AfResourceSecType.ZHIMA_VERIFY_APP_POP_IMAGE.getCode());
			
			//从缓存中取出用户上次弹窗的时间
			Long lastAccessTime = NumberUtil.objToLongDefault(bizCacheUtil.getObject(Constants.ZM_AUTH_POP_GUIDE_CACHE_KEY+envType+userId),0L);
			//默认1小时
			Long intervalTime = NumberUtil.objToLongDefault(zmPopImageResourceDo.getValue3(),3600000);
			//非第一次请求且请求时间小于指定时间差，则不进行弹窗处理
			if(lastAccessTime>0 && (currMillis-lastAccessTime) < intervalTime){
				data.put("imageUrl", "");
				data.put("zmxyAuthUrl", "");
				response.setResponseData(data);
				return response;
			}
			AfUserAuthDo authDo = afUserAuthService.getUserAuthInfoByUserId(userId);
			AfUserAccountDto userDto = afUserAccountService.getUserAndAccountByUserId(userId);
			//芝麻信息认证相关配置 val:开放开关Y/N val1:展示 0文字1数 val2:认证逻辑 Y严格认证 N默认通过val3:分界app版本
			AfResourceDo zmConfigResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.ZHIMA_VERIFY_CONFIG.getCode(), AfResourceSecType.ZHIMA_VERIFY_RULE_CONFIG.getCode());
			//芝麻信用重新启用的版本分界
			Integer zmVersionDivision = NumberUtil.objToIntDefault(zmConfigResourceDo.getValue3(), 412);
			Date zmReAuthDatetime = DateUtil.parseDateyyyyMMddHHmmss(zmConfigResourceDo.getValue4());
			if(zmReAuthDatetime==null){
				//默认值处理
				zmReAuthDatetime = DateUtil.getStartDate();
			}
			if(context.getAppVersion() >= zmVersionDivision && YesNoStatus.YES.getCode().equals(zmPopImageResourceDo.getValue2()) 
					&& ((YesNoStatus.YES.getCode().equals(authDo.getZmStatus()) && (authDo.getZmScore()==0 || DateUtil.compareDate(zmReAuthDatetime,authDo.getGmtZm())))
					|| (YesNoStatus.NO.getCode().equals(authDo.getZmStatus()) && YesNoStatus.YES.getCode().equals(authDo.getBasicStatus())))){
				//指定版本后，弹窗打开，用户芝麻信用通过，且芝麻分为0并且芝麻认证对用户开放情况下，弹窗则有效
				data.put("imageUrl", zmPopImageResourceDo.getValue());
				String authParamUrl = ZhimaUtil.authorize(userDto.getIdNumber(), userDto.getRealName());
			    AfResourceDo zhimaNewUrl = afResourceService.getSingleResourceBytype("zhimaNewUrl");

			    if (zhimaNewUrl == null) {
			    	data.put("zmxyAuthUrl", authParamUrl);
			    } else {
			    	data.put("zmxyAuthUrl", zhimaNewUrl.getValue() + "?userId=" + AesUtil.encryptToBase64(authDo.getUserId().toString(), "123"));
			    }
			    
				response.setResponseData(data);
				//将数据存入缓存
				bizCacheUtil.saveObject(Constants.ZM_AUTH_POP_GUIDE_CACHE_KEY+envType+userId, currMillis, Constants.SECOND_OF_ONE_DAY);
				return response;
			}
		}
		//用户未登录，或者不满足弹窗条件，返回空串，代表不需要进行弹窗处理
		data.put("imageUrl", "");
		data.put("zmxyAuthUrl", "");
		response.setResponseData(data);
		return response;
	}

}
