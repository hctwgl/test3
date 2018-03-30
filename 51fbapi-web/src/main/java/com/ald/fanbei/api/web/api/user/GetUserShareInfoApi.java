package com.ald.fanbei.api.web.api.user;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfFacescoreImgService;
import com.ald.fanbei.api.biz.service.AfFacescoreRedService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
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
	@Resource
	AfFacescoreRedService afFacescoreRedService;
	@Resource
	AfFacescoreImgService afFacescoreImgService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		String source = ObjectUtils.toString(requestDataVo.getParams().get("source"));
		Map<String, Object> data = new HashMap<String, Object>();
		JSONObject json = new JSONObject();
		
	    try{
		String isHidden = "N";
		String changeImage = "N";
		String shareType = "URL";
		data.put("shareType", shareType);
		json.put("shareType", shareType);
		Long userId = context.getUserId();
		AfUserDo afUserDo =  afUserService.getUserById(userId) ;
		
//		if (userId == null || afUserDo == null) {
//			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
//		}
	
//		if (StringUtils.isBlank(source)) {
//			logger.error("getUserShareInfo source can't be empty");
//			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
//		}
//		
		//查询配置信息，如果不存在，返回 默认类型URL;
		AfResourceDo   afResource=   afResourceService.getConfigByTypesAndSecType(Constants.USER_SHARE_INFO, source);
		List<AfResourceDo>   resourceList =   afResourceService.getConfigsByTypesAndSecType(Constants.USER_SHARE_INFO_CONFIGURE, source);
		
		//获取list,随机得到一个配置？
		
		
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
	//value1:需要更换的个性配置，value2,个性配置是否展示 key:value（隐藏手机号，展示头像）
		 
		 
		//获取json,并增加属性。
	
		 JSONObject jsonStr = JSONObject.parseObject(afResource.getValue3());
		 JSONArray userInfo= jsonStr.getJSONArray("userInfo");
		 if(afUserDo != null){
		 if(userInfo != null){
		     JSONObject jOUser = userInfo.getJSONObject(0); 
		     String mobile = "";
		     mobile = changePhone(afUserDo.getUserName());
		     jOUser.put("content", mobile);//JSONObject对象中添加键值对  
		    // JSONArray user= jOUser.getJSONArray("userInfo");
		     JSONArray user= new JSONArray();
		     user.add(jOUser);
		     jsonStr.put("userInfo", user);
		   
		 }
		 }else{
		     jsonStr.remove("userInfo");
		 }
		 try{
        		 if(resourceList != null && resourceList.size() >0){
        		     for(AfResourceDo afResourceDo :resourceList){
        			 //添加配置，若是content且是是需要随机配置，否则
        			 
        			 String changeJson = afResourceDo.getValue();
        			 //所有的jsonStr的key是否有等于changeJson，有则添加JSONArray
        			 Iterator<String> sIterator = jsonStr.keySet().iterator();
        			 while (sIterator.hasNext()) {
        			     // 获得key
        			     String key = sIterator.next();
        			     if(key.equals(changeJson)){
        				 String addValue = afResourceDo.getValue2();
    				         JSONArray info= jsonStr.getJSONArray(key);
    				         JSONObject jso = JSONObject.parseObject(addValue);
    				         List<JSONObject>   list =  JSONObject.parseArray(jso.getJSONArray("configure").toString(), JSONObject.class); 
    				         if(list != null && list.size() >0){
    				         if(!"RANDOM".equals(afResourceDo.getValue1())){
        				     //添加所有
        				     for(JSONObject jo :list){
        					 info.add(jo);
        				     }
        				 }else{
        				     //随机添加一条
        				     int randomLenght = list.size();
        				     int num=(int)(Math.random() * randomLenght); 
        				     info.add(list.get(num));
        				 }
        			      }
        			    }
        			 }
        		     }
        		 }
		 }catch(Exception e){
		     logger.error("getUserShareInfoApi add configure error  e = "+ e);
		 }
		 
		 //更换的个性配置
		
		 if(afResource.getValue1() != null  && StringUtils.isNotEmpty(afResource.getValue1())){
			    try{
				List<JSONObject>   list =  JSONObject.parseArray(afResource.getValue1(), JSONObject.class);
			     if(list != null){
				JSONObject jsonObject = list.get(0);
				String image  = "";
				image = jsonObject.getString("changeConfigure");
				if("avatar".equals(image)){
					if(afUserDo != null){
				        doChangeImage(afUserDo.getRid(),jsonStr);
					}else{
						doChangeImage(null,jsonStr);
					}
				         changeImage = "Y";
				}
				if("noChange".equals(image)){
			         changeImage = "Y";
			     }
			     }
			    }catch(Exception e){
				 logger.error("getUserShareInfoApi value1 error  e = "+ e);
			    }
			     
			 }
		 if(afUserDo != null){
		 if("N".equals(changeImage)){
		     //将用户头像放入imageList 
		     List<JSONObject>   list =  JSONObject.parseArray(jsonStr.getJSONArray("imageList").toString(), JSONObject.class); 
		     afUserDo.getAvatar();
		     //获取该list(type = 'avatar')
		     JSONArray ja= new JSONArray();
		     for(JSONObject jso:list){
			 if(jso.getString("type")!= null && "avatar".equals(jso.getString("type"))){
			    //对该list加入头像，
				 if(userInfo != null){
				     String image = "";
				     image = afUserDo.getAvatar();
				     if(StringUtil.isBlank(image)){
					 //获取默认头像配置
					 AfResourceDo afResourceDo  = afResourceService.getConfigByTypesAndSecType(Constants.USER_SHARE_INFO, Constants.DEFAULT_AVATAR);
					if(afResourceDo != null){
						 image = afResourceDo.getValue();
					}
				     }
				     jso.put("image", image);//JSONObject对象中添加键值对  
				     jso.remove("type");
				     ja.add(jso);
				 }
				
			 }else{
			     ja.add(jso);  
			 }
			 
		     }
		     jsonStr.put("imageList", ja);
		     
		 }
		}
		 //
		 List<JSONObject> listRule = JSONObject.parseArray("[]", JSONObject.class);
		 jsonStr.put("hideElement", listRule);
		 if(afResource.getValue2() != null  && StringUtils.isNotEmpty(afResource.getValue2())){
		    try{
		     isHidden = "Y";
		     listRule =  JSONObject.parseArray(afResource.getValue2(), JSONObject.class);
		     
		     for(JSONObject jso:listRule){
			 if(jso.getString("entityName")== null || "".equals(jso.getString("entityName"))){
			     isHidden = "N";
			     break;
			 }
		     }
		     if("Y".equals(isHidden)){
			  jsonStr.put("hideElement", listRule); 
		     }
		     
		    }catch(Exception e){
			 logger.error("getUserShareInfoApi value2 error  e = "+ e);
		    }
		     
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
	

	private int doChangeImage(Long userId,JSONObject jsonStr) {
	    //获取数据库用户上传的最后一张图片。放入json 
	   //AfFacescoreRedDo afFacescoreRedDo = new AfFacescoreRedDo();
	 try{
		 String image = "";
		/*AfFacescoreRedDo  afFacescoreRedDo =   afFacescoreRedService.getImageUrlByUserId(userId);
	  if(afFacescoreRedDo != null && userId != null ){
		   image =  afFacescoreRedDo.getImageurl();
	   }else{*/
		   //随机一张图片
		    
		     AfFacescoreImgDo findFacescoreImg = new AfFacescoreImgDo();
		     findFacescoreImg.setIsDelete(0);
		     List<AfFacescoreImgDo> afFacescoreImglist = afFacescoreImgService.getListByCommonCondition(findFacescoreImg);
		     int randomLenght = afFacescoreImglist.size();
		     int num=(int)(Math.random() * randomLenght); 
		      image = afFacescoreImglist.get(num).getUrl();
	  // }
	   List<JSONObject>   list =  JSONObject.parseArray(jsonStr.getJSONArray("imageList").toString(), JSONObject.class); 
	     //获取该list(type = 'avatar')
	     JSONArray ja= new JSONArray();
	     for(JSONObject jso:list){
		 if(jso.getString("type")!= null && "avatar".equals(jso.getString("type"))){
		    //对该list加入头像，		 
			 if(StringUtil.isNotEmpty(image)){
			     jso.put("image", image);//JSONObject对象中添加键值对  
			     jso.remove("type");
			     ja.add(jso);
			 }
			
		 }else{
		     ja.add(jso);  
		 }
		 
	     }
	     jsonStr.put("imageList", ja);
	 }catch(Exception e) {
		 logger.error("doChangeImage error:"+e);
	 }
	   
	    return 0;
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
