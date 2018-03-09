package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.GetBrandCouponCountRequestBo;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.RandomUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：getMineInfoApi
 * @author 何鑫 2017年1月20日 14:39:43
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMineInfoApiV1")
public class GetMineInfoApiV1 implements ApiHandle {
	
	private static final String AVAILABLE_NUM = "availableNum";
	private static final String DATA = "data";

	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	private AfUserService afUserService;
	@Resource
	private AfUserAuthService afUserAuthService;

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfSigninService afSigninService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	try{
		Integer appVersion = context.getAppVersion();
		Long userId = context.getUserId();
		logger.info("userId=" + userId);
		Map<String, Object> data = new HashMap<String, Object>();
		//增加运营位
		
		// 快速导航信息
		String appModel = ""; //app型号
		appModel = resp.getId();
				
		Map<String, Object> navigationInfo = getNavigationInfoWithResourceDolist(
		afResourceService.getHomeIndexListByOrderby(AfResourceType.PERSONAL_CENTER_NAVIGATION.getCode()),appModel);
		String siginUrl = "";
		AfResourceDo afResourceDo  = afResourceService.getConfigByTypesAndSecType("PERSONAL_CENTER", "SIGIN_URL");
		if(afResourceDo != null){
		    siginUrl = afResourceDo.getValue();
		}
		
				// 快速导航
		if (!navigationInfo.isEmpty()) {
						data.put("navigationInfo", navigationInfo);
		}
		List<Object> bannerList = addBannerList(requestDataVo);
		data.put("bannerList", bannerList);
		/*resp.setResponseData(data);*/
		
		
		
//		if (afUserDo == null) {
//			//throw new FanbeiException("afUserDo  is invalid", FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
//			return resp;
//		}
		data.put("siginUrl", siginUrl); 
		data.put("isLogin", "N"); 
		int coupleCount = 0;
		AfUserAccountDto userAccountInfo = null  ;
		AfUserAuthDo afUserAuthDo = null ;
		AfUserDo afUserDo   = null; 
		AfSigninDo afSigninDo  = null;
		if(userId !=null && userId >0 ){
		         afUserDo = afUserService.getUserById(userId);
		// 可用红包数量
        		 coupleCount = afUserCouponService.getUserCouponByUserNouse(userId);
        		// 账户关联信息
        		 userAccountInfo = afUserAccountService.getUserAndAccountByUserId(userId);
        		 afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);
        		 afSigninDo = afSigninService.selectSigninByUserId(userId);
        		 data.put("isLogin", "Y"); 
		}
		
		String avata = "";
		String nick = "";
		String userName = "";
		String realName = "";
		String idNumber = "";
		BigDecimal jfbAmount = new BigDecimal("0.00");
		BigDecimal rebateAmount = new BigDecimal("0.00");
		String mobile = "";
		String isPay = "";
		String vipLevel = "";
		String faceStatus = "";
		String bankcardStatus = "";
		String recommendCode = "";
		if(userAccountInfo != null){
        		 avata = userAccountInfo.getAvatar();
        		 nick = userAccountInfo.getNick();
        		 userName = userAccountInfo.getUserName();
        		 realName = userAccountInfo.getRealName();
        		 idNumber = Base64.encodeString(userAccountInfo.getIdNumber());
        		 jfbAmount = userAccountInfo.getJfbAmount();
        		 rebateAmount = userAccountInfo.getRebateAmount();
        		 recommendCode = userAccountInfo.getRecommendCode();
        		 isPay = YesNoStatus.NO.getCode();
        			if (!StringUtil.isBlank(userAccountInfo.getPassword())) {
        				isPay = YesNoStatus.YES.getCode();
        		  }
		 
		}
		data.put("plantformCouponCount", 0);
		if(afUserDo !=null){
		            mobile = afUserDo.getMobile();
        		if (appVersion < 340) {
        			data.put("couponCount", coupleCount);
        		} else {
        			dealWithVersionGT340(data, requestDataVo, context, coupleCount);
        		}
		}
		if(afUserAuthDo != null){
		    faceStatus = afUserAuthDo.getFacesStatus();
		    bankcardStatus = afUserAuthDo.getBankcardStatus();
		}
		data.put("avata", avata);
		data.put("nick",nick);
		data.put("userName", userName);
		data.put("realName", realName);
		data.put("idNumber", idNumber);
		data.put("jfbAmount", jfbAmount);
		data.put("isPayPwd", isPay);
		data.put("vipLevel", vipLevel);
		data.put("rebateAmount", rebateAmount);
		data.put("couponCount", coupleCount);
		data.put("mobile", mobile);
		data.put("faceStatus", faceStatus);
		data.put("bankcardStatus", bankcardStatus);
		data.put("recommendCode", recommendCode);
		if (appVersion  >= 345) {
			//随机生成客服电话
			String phone = randomPhone();
			data.put("customerPhone", phone);
		} 
		
		//获取签到状态
		
		 
		   // data.put("isSignin", "N");
		   
		    if (afSigninDo==null||null==afSigninDo.getGmtSeries()) {
		       data.put("isSignin", "N");

			}else{

				Date seriesTime = afSigninDo.getGmtSeries();
				if(DateUtil.isSameDay(new Date(), seriesTime)){
		        	data.put("isSignin", "Y");

				}else{
		        	 data.put("isSignin", "N");
				}
			}
		
	  resp.setResponseData(data);
	}catch(Exception e){
	    logger.info("user/getMineInfo error e = "+e);
	}
	return resp;
 }
	private List<Object> addBannerList(RequestDataVo requestDataVo){
	 //String resourceType =  ObjectUtils.toString(requestDataVo.getParams().get("type"), "").toString();
	 String resourceType =  Constants.PERSONAL_CENTER_BANNER;
	 String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
	 logger.info("getDrainageBannerListApi and type = {}", type);
	 List<AfResourceDo> bannerList1 = new ArrayList<AfResourceDo>();
	//线上为开启状态
	 if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
	 bannerList1 = afResourceService
			.getResourceHomeListByTypeOrderBy(resourceType);
	 }
	 else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
	//预发不区分状态
	 bannerList1 =  afResourceService
			.getResourceHomeListByTypeOrderByOnPreEnv(resourceType);
	 }
	logger.info("getDrainageBannerListApi and bannerList1 = {}", bannerList1);
	List<Object> bannerList = getObjectWithResourceDolist(bannerList1);
	//resp.addResponseData("bannerList", bannerList);
	
	return bannerList;
}
private List<Object> getObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
	List<Object> bannerList = new ArrayList<Object>();
	
	for (AfResourceDo afResourceDo : bannerResclist) {
	Map<String, Object> data = new HashMap<String, Object>();
	data.put("imageUrl", afResourceDo.getValue());
	data.put("titleName", afResourceDo.getName());
	data.put("type", afResourceDo.getValue1());
	data.put("content", afResourceDo.getValue2());
	data.put("sort", afResourceDo.getSort());
	
	bannerList.add(data);
	
	}
	return bannerList;
	}

	
	private Map<String, Object> getNavigationInfoWithResourceDolist(List<AfResourceDo> navResclist,String appModel) {
		Map<String, Object> navigationInfo = new HashMap<String, Object>();
		List<Object> navigationList = new ArrayList<Object>();
		int navCount = navResclist.size();
		for (int i = 0; i < navCount; i++) {
			// 如果配置大于4个，小于8个，则只显示4个
		      
			if (navCount >= 4 && navCount < 8) {
				if (i >= 4) {
					break;
				}
			} else if (navCount >= 8) {
				// 如果配置大于等于8个，则只显示8个
				if (i >= 8) {
					break;
				}
			}else if(navCount < 4 ){
			    break;
			}
			AfResourceDo afResourceDo = navResclist.get(i);
			String secType = afResourceDo.getSecType();
			Map<String, Object> dataMap = new HashMap<String, Object>();
			dataMap.put("imageUrl", afResourceDo.getValue());
			dataMap.put("titleName", afResourceDo.getName());
			dataMap.put("type", secType);
			//dataMap.put("content", afResourceDo.getValue2());
			dataMap.put("sort", afResourceDo.getSort());
			dataMap.put("color", afResourceDo.getValue3());
			//
			String content = "";
			String model =  appModel.substring(0, 1);
			if(model.equalsIgnoreCase("a")){
			    content = afResourceDo.getPic1();
			}
			if(model.equalsIgnoreCase("i")){
			    content = afResourceDo.getPic2();
			}
			if(afResourceDo.getSecType().equals("NAVIGATION_NATIVE")){
			    dataMap.put("content", content);
			}else{
			    dataMap.put("content", afResourceDo.getValue2());
			}
			
			navigationList.add(dataMap);
		}
		navigationInfo.put("navigationList", navigationList);
		return navigationInfo;
	}

	
	/**
	 * 随机生成一个客服电话号码
	 * Author:苏伟丽
	 **/
	private String randomPhone() {
		 String targetPhone = "0571-86803811";
		 AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(AfResourceType.APPCONSUMERPHONE.getCode());
		 if(resourceInfo != null && StringUtil.isNotBlank(resourceInfo.getValue())){
			 String[] phoneArry = resourceInfo.getValue().split(",");
			 if(phoneArry!= null && phoneArry.length > 0){
				 targetPhone = RandomUtil.getRandomElement(phoneArry);
				 if(StringUtil.isNotBlank(targetPhone)){
					return targetPhone;
				 }
			 }
		 }
		 return targetPhone;
	}

	private void dealWithVersionGT340(Map<String, Object> resultData, RequestDataVo requestDataVo, FanbeiContext context, int coupleCount) {
		GetBrandCouponCountRequestBo bo = new GetBrandCouponCountRequestBo();
		bo.setUserId(context.getUserId()+StringUtils.EMPTY);
		String resultString = HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_BOLUOME_API_URL) + "/api/promotion/get_coupon_num", JSONObject.toJSONString(bo));
		JSONObject resultJson = JSONObject.parseObject(resultString);
		if (resultJson == null || !"0".equals(resultJson.getString("code"))) {
			resultData.put("couponCount", coupleCount);
			resultData.put("brandCouponCount", 0);
		} else {
			JSONObject data = resultJson.getJSONObject(DATA);
			Integer brandCouponCount = data.getInteger(AVAILABLE_NUM);
			resultData.put("couponCount", coupleCount + brandCouponCount);
		}
		resultData.put("plantformCouponCount", coupleCount);
	}

}
