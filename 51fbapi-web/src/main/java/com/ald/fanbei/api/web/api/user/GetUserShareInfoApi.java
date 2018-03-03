package com.ald.fanbei.api.web.api.user;

import java.util.ArrayList;
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
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;


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
		
	       try{
		String shareType = "URL";
		data.put("shareType", shareType);
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
		List<AfResourceDo>   afResourceList =   afResourceService.getConfigsListByTypesAndSecType(Constants.USER_SHARE_INFO, source);
		if(afResourceList == null || afResourceList.size() < 1){
			resp.setResponseData(data);
		        logger.info("getUserShareInfoApi afResourceList is null source = " + source);
		        return resp;
		}
		//存在则循环该list,得到不同value下的数据
	        List<Map<String, Object>> contentList = new ArrayList<Map<String, Object>>();
		for(AfResourceDo afResource:afResourceList){
		    String type = afResource.getValue();
		    if(type.equals("image")){
			data.put("image", afResource.getValue3());
		    }
		    if(type.equals("userInfo")){
			String userName = changePhone(afUserDo.getUserName());
			Map<String, Object> userInfo = new HashMap<String, Object>();
			userInfo.put("mobile", userName);
			userInfo.put("size", afResource.getValue4());
			userInfo.put("colour", afResource.getValue1());
			putPosition(userInfo,afResource);
			data.put("userInfo", userInfo);
			
		    }
		    if(type.equals("qrCode")){
			Map<String, Object> qrCode = new HashMap<String, Object>();
			qrCode.put("url", afResource.getValue3());
			putPosition(qrCode,afResource);
			data.put("qrCode", qrCode);
			
		    }
		    if(type.equals("avatar")){
			Map<String, Object> avatar = new HashMap<String, Object>();
			avatar.put("avatar", afUserDo.getAvatar());
			putPosition(avatar,afResource);
			data.put("avatar", avatar);
		    }
		    if(type.equals("contentList")){
			Map<String, Object> contentMap = new HashMap<String, Object>();
			contentMap.put("content", afResource.getValue3());
			contentMap.put("size", afResource.getValue4());
			contentMap.put("colour", afResource.getValue1());
			putPosition(contentMap,afResource);
			contentList.add(contentMap);
		    }
		   
		}
		     data.put("contentList", contentList);
		}catch(Exception e){
		    logger.error("getUserShareInfoApi error  e = "+ e+" source = "+ source);
		}
	       logger.info("getUserShareInfoApi  data = "+ JSON.toJSONString(data) );
	       
	    resp.setResponseData(data);
	    return resp;
	}
	
	private void putPosition(Map<String, Object> info, AfResourceDo afResource) {
	    // TODO Auto-generated method stub
	    String positionX = "";
	    String positionY = "";
	    try{
        	    String positions =  afResource.getValue2();
        	    String position[] = positions.split(",");  
        	    if(position.length == 2){
                	    positionX  = position[0];
                	    positionY  = position[1];
        	    }
        	    
        	    info.put("positionX", position[0] );
        	    info.put("positionY", position[1]);
	    }catch(Exception e){
		logger.error("putPosition error  e = "+ e);
	    }
	    info.put("positionX", positionX);
	    info.put("positionY",positionY);
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
