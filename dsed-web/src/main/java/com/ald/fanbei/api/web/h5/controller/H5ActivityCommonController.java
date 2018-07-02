package com.ald.fanbei.api.web.h5.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfFacescoreImgService;
import com.ald.fanbei.api.biz.service.AfFacescoreRedService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSmsRecordService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserLoginLogService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.biz.third.util.baiqishi.BaiQiShiUtils;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.CookieUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.SmsType;
import com.ald.fanbei.api.common.enums.UserStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSmsRecordDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 
 * @author :chenqiwei
 * @version ：2017年10月30日 下午16:00:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/h5Common")
public class H5ActivityCommonController extends BaseController {

    @Resource
    AfUserService afUserService;
    @Resource
    AfUserLoginLogService afUserLoginLogService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfSmsRecordService afSmsRecordService;
    @Resource
    TongdunUtil tongdunUtil;
	@Resource
	BaiQiShiUtils baiQiShiUtils;
    @Resource
    AfOrderService afOrderService;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfFacescoreRedService afFacescoreRedService;
    @Resource 
    AfFacescoreImgService afFacescoreImgService;

    // H5活动登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String boluomeActivityLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

	String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
	String password = ObjectUtils.toString(request.getParameter("password"), "").toString();
	String tongduanToken = ObjectUtils.toString(request.getParameter("token"), "").toString();
	String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();
	AfUserDo UserDo =  new AfUserDo();
        String referer = request.getHeader("referer");  
    //    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "calling"),referer,"callingInterface");
     try{
	 
	UserDo = afUserService.getUserByUserName(userName);
	String cacheKey = Constants.BOLUOME_LOGIN_ERROR_TIMES + userName;
	int errorCount = NumberUtil.objToIntDefault((bizCacheUtil.getObject(cacheKey)), 0);
	if (errorCount < 5) {

	    if (userName == null || userName.isEmpty()) {
		return H5CommonResponse.getNewInstance(false, "请输入账号", "Login", "").toString();

	    }
	    if (password == null || password.isEmpty()) {
		return H5CommonResponse.getNewInstance(false, "请输入密码", "Login", "").toString();
	    }

	    if (UserDo == null) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_NOT_EXIST_ERROR.getDesc(), "Register", "").toString();
	    }
	    if (StringUtils.equals(UserDo.getStatus(), UserStatus.FROZEN.getCode())) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_FROZEN_ERROR.getDesc(), "Login", "").toString();
	    }
	    try {
		tongdunUtil.getPromotionLoginResult(tongduanToken, null, null, CommonUtil.getIpAddr(request), userName, userName, "");
	    } catch (Exception e) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_LOGIN_ERROR.getDesc(), "Login", null).toString();

	    }
		try {
			baiQiShiUtils.getLoginResult("h5",bsqToken,CommonUtil.getIpAddr(request),UserDo.getMobile(),UserDo.getRealName(),"","","");
		}catch (Exception e){
			logger.error("h5Common userLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
		}
	    // check password
	    String inputPassword = UserUtil.getPassword(password, UserDo.getSalt());

	    if (!StringUtils.equals(inputPassword, UserDo.getPassword())) {
		// fail count add 1
		errorCount = errorCount + 1;
		bizCacheUtil.saveObject(cacheKey, errorCount, Constants.SECOND_OF_HALF_HOUR);
		FanbeiExceptionCode code = getErrorCountCode(errorCount);
		return H5CommonResponse.getNewInstance(false, code.getDesc(), "Login", "").toString();
	    }

	    bizCacheUtil.delCache(cacheKey);
	    // save token to cache
	    String token = UserUtil.generateToken(userName);
	    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + userName;
	    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, userName, Constants.SECOND_OF_HALF_HOUR_INT);
	    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
	    bizCacheUtil.saveObject(tokenKey, token, Constants.SECOND_OF_HALF_HOUR);
	} else {
	    return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5.getDesc(), "Login", "").toString();
	}
     }catch (Exception e){
		logger.error("boluomeActivityLogin error",e.getMessage());
	}
           //该用户是否新用户，用于埋点
//	    AfOrderDo queryCount = new AfOrderDo();
//	    queryCount.setUserId(UserDo.getRid());
//	    String orderCount = String.valueOf(afOrderService.getOrderCountByStatusAndUserId(queryCount));
//	    logger.info("orderCount = {}", orderCount);
//	    doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"),referer,orderCount,userName);
	return H5CommonResponse.getNewInstance(true, "登录成功", "", "").toString();
    }

   

    // 提交活动注册并登陆
    @ResponseBody
    @RequestMapping(value = "/commitRegisterLogin", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    public String commitActivityRegisterLogin(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {
	 String resultStr = "";
	 String referer = request.getHeader("referer"); 
	// doMaidianLog(request, H5CommonResponse.getNewInstance(true, "calling"),referer,"callingInterface");
	try {
	    String moblie = ObjectUtils.toString(request.getParameter("registerMobile"), "").toString();
	    String verifyCode = ObjectUtils.toString(request.getParameter("smsCode"), "").toString();
	    String passwordSrc = ObjectUtils.toString(request.getParameter("password"), "").toString();
	    String recommendCode = ObjectUtils.toString(request.getParameter("recommendCode"), "").toString();
	    String token = ObjectUtils.toString(request.getParameter("token"), "").toString();
	    String bsqToken = ObjectUtils.toString(request.getParameter("bsqToken"), "").toString();

	    AfUserDo eUserDo = afUserService.getUserByUserName(moblie);
	    if (eUserDo != null) {
		return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_ACCOUNT_EXIST.getDesc(), "", null).toString();

	    }
	    AfSmsRecordDo smsDo = afSmsRecordService.getLatestByUidType(moblie, SmsType.REGIST.getCode());
	    if (smsDo == null) {
		logger.error("sms record is empty");
		resultStr = H5CommonResponse.getNewInstance(false, "手机号与验证码不匹配", "", null).toString();
		return resultStr;
	    }

	    String realCode = smsDo.getVerifyCode();
	    if (!StringUtils.equals(verifyCode, realCode)) {
		logger.error("verifyCode is invalid");
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ERROR.getDesc(), "", null).toString();
		return resultStr;
	    }
	    if (smsDo.getIsCheck() == 1) {
		logger.error("verifyCode is already invalid");
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_ALREADY_ERROR.getDesc(), "", null).toString();
		return resultStr;
	    }
	    // 判断验证码是否过期
	    if (DateUtil.afterDay(new Date(), DateUtil.addMins(smsDo.getGmtCreate(), Constants.MINITS_OF_HALF_HOUR))) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_REGIST_SMS_OVERDUE.getDesc(), "", null).toString();
		return resultStr;

	    }
	    try {
		tongdunUtil.getPromotionResult(token, null, null, CommonUtil.getIpAddr(request), moblie, moblie, "");
	    } catch (Exception e) {
		resultStr = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.TONGTUN_FENGKONG_REGIST_ERROR.getDesc(), "", null).toString();
		return resultStr;
	    }
		try {
			baiQiShiUtils.getRegistResult("h5",bsqToken,CommonUtil.getIpAddr(request),moblie,"","","","");
		}catch (Exception e){
			logger.error("h5Common commitRegisterLogin baiQiShiUtils getRegistResult error => {}",e.getMessage());
		}
	    // 更新为已经验证
	    afSmsRecordService.updateSmsIsCheck(smsDo.getRid());

	    String salt = UserUtil.getSalt();

	    // modify by luoxiao 避免passwordSrc 为空出现NullPointerException异常
		String password = "";
	    if(StringUtils.isNotEmpty(passwordSrc)){
			password = UserUtil.getPassword(passwordSrc, salt);
		}
		// end by luoxiao

	    AfUserDo userDo = new AfUserDo();
	    userDo.setSalt(salt);
	    userDo.setUserName(moblie);
	    userDo.setMobile(moblie);
	    userDo.setNick("");
	    userDo.setPassword(password);
	    userDo.setRecommendId(0l);
	    //邀请码
	    if (!StringUtils.isBlank(recommendCode)) {
		AfUserDo userRecommendDo = afUserService.getUserByRecommendCode(recommendCode);
		userDo.setRecommendId(userRecommendDo.getRid());
	    }
	    long userId = afUserService.addUser(userDo);

	    Long invteLong = Constants.INVITE_START_VALUE + userId;
	    String inviteCode = Long.toString(invteLong, 36);
	    userDo.setRecommendCode(inviteCode);
	    afUserService.updateUser(userDo);
	    // 获取邀请分享地址
	    String appDownLoadUrl = "";
//	    AfResourceDo resourceCodeDo = afResourceService.getSingleResourceBytype(AfResourceType.AppDownloadUrl.getCode());
//	    if (resourceCodeDo != null) {
//		appDownLoadUrl = resourceCodeDo.getValue();
//	    }
	    resultStr = H5CommonResponse.getNewInstance(true, "注册成功", appDownLoadUrl, null).toString();
	    // save token to cache 记住登录状态
            String  newtoken = UserUtil.generateToken(moblie);
	    String tokenKey = Constants.H5_CACHE_USER_TOKEN_COOKIES_KEY + moblie;
	    CookieUtil.writeCookie(response, Constants.H5_USER_NAME_COOKIES_KEY, moblie, Constants.SECOND_OF_HALF_HOUR_INT);
	    CookieUtil.writeCookie(response, Constants.H5_USER_TOKEN_COOKIES_KEY, token, Constants.SECOND_OF_HALF_HOUR_INT);
	    bizCacheUtil.saveObject(tokenKey, newtoken, Constants.SECOND_OF_HALF_HOUR);
	    //埋点
	    //doMaidianLog(request, H5CommonResponse.getNewInstance(true, "succ"),referer);
	    return resultStr;

	} catch (FanbeiException e) {
	    logger.error("commitRegister fanbei exception" + e.getMessage());
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
	    return resultStr;
	} catch (Exception e) {
	    logger.error("commitRegister exception", e);
	    resultStr = H5CommonResponse.getNewInstance(false, "失败", "", null).toString();
	    return resultStr;
	} finally {

	}

    }

    
    
    public FanbeiExceptionCode getErrorCountCode(Integer errorCount) {
	if (errorCount == 0) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_ZERO;
	} else if (errorCount == 1) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_FIRST;
	} else if (errorCount == 2) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_SECOND;
	} else if (errorCount == 3) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_THIRD;
	} else if (errorCount == 4) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_FOURTH;
	} else if (errorCount == 5) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_FIFTH;
	} else if (errorCount == 6) {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5;
	} else {
	    return FanbeiExceptionCode.USER_PASSWORD_ERROR_GREATER_THAN5;
	}
    }


	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
	return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
	return null;
    }
    @ResponseBody
    @RequestMapping(value = "/getUserShareInfo", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	public String getGoodsList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> data = new HashMap<String, Object>();
		FanbeiWebContext context = doWebCheck(request, false);
		
			String userName = context.getUserName();
			//AfUserDo user = afUserService.getUserByUserName(userName);
			//Long userId = user == null ? -1 : user.getRid();
			String source = ObjectUtils.toString(request.getParameter("source"), "").toString();
			JSONObject json = new JSONObject();
			
		    try{
			String isHidden = "N";
			String changeImage = "N";
			String shareType = "URL";
			data.put("shareType", shareType);
			json.put("shareType", shareType);
			//Long userId = context.getUserId();
			//AfUserDo afUserDo =  afUserService.getUserById(userId) ;
			AfUserDo afUserDo =  afUserService.getUserByUserName(userName) ;
			
//			if (userId == null || afUserDo == null) {
//				throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
//			}
		
//			if (StringUtils.isBlank(source)) {
//				logger.error("getUserShareInfo source can't be empty");
//				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.PARAM_ERROR);
//			}
//			
			//查询配置信息，如果不存在，返回 默认类型URL;
			AfResourceDo   afResource=   afResourceService.getConfigByTypesAndSecType(Constants.USER_SHARE_INFO, source);
			List<AfResourceDo>   resourceList =   afResourceService.getConfigsByTypesAndSecType(Constants.USER_SHARE_INFO_CONFIGURE, source);
			
			//获取list,随机得到一个配置？
			
			if(afResource == null){
				logger.info("getUserShareInfoApi afResourceList is null source = " + source);
				return H5CommonResponse.getNewInstance(true, "未找到配置，默认链接",null,data).toString();
				 //resp.setResponseData(data);
			     
			       // return resp;
			}
			//预发线上区分
			 String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
			 logger.info("getUserShareInfoApi and type = {}"+ type);
			//线上
			 if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
			   if(afResource.getValue4().equals("C")){
				   logger.info("getUserShareInfoApi afResource value4 is close");
				   return H5CommonResponse.getNewInstance(true, "配置已关闭，默认链接",null,data).toString();
			        
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
			     JSONArray userMesage= new JSONArray();
			     userMesage.add(jOUser);
			     jsonStr.put("userInfo", userMesage);
			   
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
			
			
			return H5CommonResponse.getNewInstance(true, "获取分享信息成功", null, json).toString();
		} catch (Exception e) {
			logger.error("/h5Common/getUserShareInfo" + context + "error = {}", e);
			return H5CommonResponse.getNewInstance(false, "获取分享信息表失败,默认链接",null,json).toString();
		}
	}


private int doChangeImage(Long userId,JSONObject jsonStr) {
    //获取数据库用户上传的最后一张图片。放入json 
   //AfFacescoreRedDo afFacescoreRedDo = new AfFacescoreRedDo();
 try{
	 
	 String image = "";
//   AfFacescoreRedDo  afFacescoreRedDo =   afFacescoreRedService.getImageUrlByUserId(userId);
//   if(afFacescoreRedDo != null && userId != null ){
//	   image =  afFacescoreRedDo.getImageurl();
//   }else{
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
