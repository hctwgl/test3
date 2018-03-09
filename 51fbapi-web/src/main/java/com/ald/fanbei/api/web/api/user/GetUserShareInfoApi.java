package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;


/**
 * 
 * @类描述：
 * @author chenqiwei 2018年3月3日下午1:48:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getUserShareInfoApi")
public class GetUserShareInfoApi implements ApiHandle {
	
	@Resource
	AfUserService afUserService;
	@Resource
	AfResourceService afResourceService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String source = ObjectUtils.toString(requestDataVo.getParams().get("source"));
		Map<String, Object> data = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		
	    try{
		String shareType = "URL";
		data.put("shareType", shareType);
		json.put("shareType", shareType);
		Long userId = context.getUserId();
		AfUserDo afUserDo =  afUserService.getUserById(userId) ;
		
		if (userId == null || afUserDo == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
	
		if (StringUtils.isBlank(source)) {
			logger.error("getUserShareInfo source can't be empty");
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
		}
		
		//查询配置信息，如果不存在，返回 默认类型URL;
		AfResourceDo   afResource=   afResourceService.getConfigByTypesAndSecType(Constants.USER_SHARE_INFO, source);
		if(afResource == null){
			resp.setResponseData(data);
		        logger.info("getUserShareInfoApi afResourceList is null source = " + source);
		        return resp;
		}
		//预发线上区分
		 String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		 logger.info("getUserShareInfoApi and type = {}"+ type);
		//线上
		 if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
		   if(afResource.getValue4().equals("C")){
		       resp.setResponseData(data);
		        logger.info("getUserShareInfoApi afResource value4 is close");
		        return resp;
		   }
		 }
	
		//获取json,并增加属性。
		 JSONObject jsonStr = JSONObject.parseObject(afResource.getValue3());
		 JSONArray userInfo= jsonStr.getJSONArray("userInfo");
		 if(userInfo != null){
		     JSONObject jOUser = userInfo.getJSONObject(0); 
		     String mobile = "";
		     mobile = changePhone(afUserDo.getUserName());
		     jOUser.put("mobile", mobile);//JSONObject对象中添加键值对  
		     jsonStr.put("userInfo", jOUser);
		 }
		 jsonStr.put("shareType", "IMAGE");
		 json = JSONObject.parseObject(jsonStr.toString());	
		
		}catch(Exception e){
		    logger.error("getUserShareInfoApi error  e = "+ e+" source = "+ source);
		}
	          logger.info("getUserShareInfoApi  data = "+ JSON.toJSONString(json) );
	    resp.setResponseData(json);
	    return resp;
	}
	

	private String changePhone(String userName) {
		String newUserName = "";
		if (!StringUtil.isBlank(userName)) {
			newUserName = userName.substring(0, 3);
			newUserName = newUserName + "****";
			newUserName = newUserName + userName.substring(7, 11);
		}
		return newUserName;
	}
	
}
