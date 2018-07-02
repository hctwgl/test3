package com.ald.fanbei.api.web.apph5.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseExtBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.CouponSceneRuleBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponCateGoryType;
import com.ald.fanbei.api.common.enums.CouponScene;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.H5GgActivity;
import com.ald.fanbei.api.common.enums.H5OpenNativeType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfRecommendUserDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.NewbieTaskVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 推荐活动
 * @author caowu 2017年10月17日下午2:26:05
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/")
public class AppH5InvitationActivityController extends BaseController {

    @Resource
    AfRecommendUserService afRecommendUserService;

    @Resource
    AfUserService afUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfCouponSceneService afCouponSceneService;
    @Resource
    AfCouponService afCouponService;
    @Resource
    AfCouponCategoryService afCouponCategoryService;
    @Resource
    BoluomeUtil boluomeUtil;
    @Resource
    AfBoluomeRebateService afBoluomeRebateService;
    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    AfOrderService afOrderService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfSigninService afSigninService;
    @Resource
    JpushService jpushService;
    @Resource
    CouponSceneRuleEnginerUtil activeRuleEngineUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfShopService afShopService;
    @Resource
    AfResourceDao afResourceDao;
    String opennative = "/fanbei-web/opennative?name=";
    String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + "/fanbei-web/opennative?name="
		+ H5OpenNativeType.AppLogin.getCode();
    
    /**
     * 活动页面的基本信息
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "activityUserInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String activityUserInfo(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        String  activityTime = null;
        String invitationCode = "";
        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        List<String> listRule = new ArrayList<String>();
        List<String> listPic = new ArrayList<String>();
        List<String> listTitle = new ArrayList<String>();
        List<String> listDesc = new ArrayList<String>();
        
        try{
            context = doWebCheck(request, true);
            if(context.isLogin()){
                afUser = afUserService.getUserByUserName(context.getUserName());
                if(afUser != null){
                    userId = afUser.getRid();
                }
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
                return resp.toString();
            }
       
      
        //查看活动规则,图片,标题,描述
        listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
        listPic=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_IMG");
        listTitle=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_TITLE");
        listDesc=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_DESCRIPTION");
        logger.info("activityUserInfo listRule: "+JSON.toJSONString(listRule));
        logger.info("activityUserInfo listPic: "+JSON.toJSONString(listPic));
        logger.info("activityUserInfo listTitle: "+JSON.toJSONString(listTitle));
        logger.info("activityUserInfo listDesc: "+JSON.toJSONString(listDesc));
        //用户的邀请码
         invitationCode=afRecommendUserService.getUserRecommendCode(userId);
        if(invitationCode.equals("0")){
            //生成邀请码
            AfUserDo userDo = new AfUserDo();
	    Long invteLong = Constants.INVITE_START_VALUE + userId;
	    String inviteCode = Long.toString(invteLong, 36);
	    userDo.setRecommendCode(inviteCode);
	    userDo.setRid(userId);
	    afUserService.updateUser(userDo);
	    invitationCode = inviteCode;
        }
     
	    AfResourceDo activityStart = new AfResourceDo();
		   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
		   activityStart = list.get(0);
		    if(activityStart !=null){
			activityTime = activityStart.getValue();
       }
        
        }catch  (Exception e) {
            logger.error("activityUserInfo error", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

        //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId,activityTime);
        DecimalFormat df = new DecimalFormat("######0.00");//金钱格式 保留两位小数
        map.put("listRule",listRule);
        map.put("listPic",listPic);
        map.put("listTitle",listTitle);
        map.put("listDesc",listDesc);
        map.put("invitationCode",invitationCode);
        map.put("sumPrizeMoney",df.format(sumPrizeMoney));
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        return ret;
    }

    
    /**
     * 获取活动页面签到信息
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getSigninInfo", produces = "application/json;charset=UTF-8", method = RequestMethod.POST)
    public String getSigninInfo(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                afUser = afUserService.getUserByUserName(context.getUserName());
                if(afUser != null){
                    userId = afUser.getRid();
                }
//            }else{
//                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), loginUrl, null);
//                return resp.toString();
            }
        }catch  (Exception e) {
            logger.error("getSigninInfo error", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
        AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
        AfCouponSceneDo afCouponSceneDo = afCouponSceneService.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
        if(afCouponSceneDo==null){
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", null);
            return resp.toString();

        }
        Integer seriesTotal = 1;

        List<CouponSceneRuleBo> ruleBoList=   afCouponSceneService.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");

        if(ruleBoList.size()==0){
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", null);
            return resp.toString();

        }

        CouponSceneRuleBo ruleBo = ruleBoList.get(0);
 	   seriesTotal= NumberUtil.objToIntDefault(ruleBo.getCondition(), 1) ;
       Map<String, Object> data = new HashMap<String, Object>();
		data.put("cycle", seriesTotal);
    	data.put("ruleSignin",ObjectUtils.toString(afCouponSceneDo.getDescription(), "").toString()  );

    	int seriesCount =0;

        if (afSigninDo==null||null==afSigninDo.getGmtSeries()) {
        	data.put("seriesCount",seriesCount);
        	data.put("isSignin", "T");

		}else{
			seriesCount = afSigninDo.getSeriesCount();

			Date seriesTime = afSigninDo.getGmtSeries();
			if(DateUtil.isSameDay(new Date(), seriesTime)){
	        	data.put("isSignin", "F");

			}else{
				if(!DateUtil.isSameDay(DateUtil.getCertainDay(-1),seriesTime)||seriesCount == seriesTotal){
					seriesCount = 0;
				}
	        	data.put("isSignin", "T");
			}

        	data.put("seriesCount", seriesCount);

		}
                AfUserDo afUserDo = afUserService.getUserById(userId);
                   String avatar = null;
                if(afUserDo != null){
                    avatar = afUserDo.getAvatar();
                }
                data.put("avatar", avatar);
                
   	     //签到优惠券
             AfCouponDo signIn = new AfCouponDo();
   	     List<AfCouponDo> signInCouponList = getCommonCouponMap(CouponScene.SIGN_IN);
           	     data.put("threshold", "");
        	     data.put("couponAmount", "");
   	     if(signInCouponList.size()>0){
   		     signIn = signInCouponList.get(0);
 		     data.put("threshold", signIn.getLimitAmount());
 		     data.put("couponAmount", signIn.getAmount());
   	      }
                
                String ret = JSON.toJSONString(data);
                return ret;
    }

    /**
     * 进行签到
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "signin", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String signin(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance(true,FanbeiExceptionCode.USER_SIGNIN_SUCCESS.getDesc(),"",null);
        AfUserDo afUser = null;
        try{
            context = doWebCheck(request, true);
            if(context.isLogin()){
                afUser = afUserService.getUserByUserName(context.getUserName());
                if(afUser != null){
                    userId = afUser.getRid();
                }
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), loginUrl, null);
                return resp.toString();
            }
        }catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/fanbei-web/signin" + context + "login error ");
			        return	 H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
        }catch  (Exception e) {
            logger.error("signin error", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

        AfSigninDo afSigninDo = afSigninService.selectSigninByUserId(userId);
	 AfCouponSceneDo afCouponSceneDo = afCouponSceneService.getCouponSceneByType(CouponSenceRuleType.SIGNIN.getCode());
       if(afCouponSceneDo==null){
	   resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", null);
           return resp.toString();
       }
       Integer cycle = 1;
       List<CouponSceneRuleBo> ruleBoList=   afCouponSceneService.getRules(CouponSenceRuleType.SIGNIN.getCode(), "signin");
       
       if(ruleBoList.size()==0){
	   resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", null);
           return resp.toString();

       }

       CouponSceneRuleBo ruleBo = ruleBoList.get(0);
       cycle= NumberUtil.objToIntDefault(ruleBo.getCondition(), 1) ;
	   
	Integer seriesCount =  1;
	Integer totalCount =  0;
	if (afSigninDo == null) {
		afSigninDo = new AfSigninDo();
		totalCount += 1;
		afSigninDo.setSeriesCount(seriesCount);
		afSigninDo.setTotalCount(totalCount);
		afSigninDo.setUserId(userId);
		if (afSigninService.addSignin(afSigninDo) > 0) {
			return resp.toString();
		}

	} else {
		Date seriesTime =null;
		if(afSigninDo.getGmtSeries()==null){
			seriesCount =1;
		}else{
			seriesTime = afSigninDo.getGmtSeries();
			if (DateUtil.isSameDay(new Date(), seriesTime)) {
			    resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.USER_SIGNIN_AGAIN_ERROR.getDesc(), "", null);
		            return resp.toString();
			}
			// 当连续签到天数小于循环周期时
			if (DateUtil.isSameDay(DateUtil.getCertainDay(-1), seriesTime) && cycle != afSigninDo.getSeriesCount()) {
				seriesCount = afSigninDo.getSeriesCount() + 1;
			}
		}
																																																																																																																					
		totalCount = afSigninDo.getTotalCount() + 1;
		
		AfSigninDo signinDo =new AfSigninDo();
		signinDo.setSeriesCount(seriesCount);
		signinDo.setTotalCount(totalCount);
		signinDo.setUserId(userId);
		signinDo.setRid(afSigninDo.getRid());

		if ( afSigninService.changeSignin(signinDo) > 0) {
			if(seriesCount == cycle){
				activeRuleEngineUtil.signin(userId);
				jpushService.getSignCycle(context.getUserName());
			}
		
			return resp.toString();

		}
	}

	resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.FAILED.getDesc(), "", null);
        return resp.toString();
    
    }

    
    
    
    /**
     * 邀请有礼活动及用户信息(新版)
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "activityAndUserInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String activityUserInfoV1(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try {
     			 context = doWebCheck(request, false);
     			 String userName = context.getUserName();
     			 userId = convertUserNameToUserId(userName);
     	            
        }catch  (Exception e) {
            logger.error("activityAndUserInfo error", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

          HashMap<String,Object> map =new HashMap<>();
          List<HashMap> hashMapList =new ArrayList<>();
          List  welfareTableList = new ArrayList();
          List  welfareExampleList = new ArrayList();
//        List  giftPackageList = new ArrayList();
//        List  preferentialList = new ArrayList();
        
        //查看活动规则,图片,标题,描述
      	List<String> listRule = bizCacheUtil.getObjectList("recommend:activity:rule");
      	if (listRule == null) {
      	    listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
      	  //并且加入redis
      	   bizCacheUtil.saveObjectListExpire("recommend:activity:rule", listRule, Constants.SECOND_OF_TEN_MINITS);
      	 }

	List<String> listPic = bizCacheUtil.getObjectList("recommend:activity:picture");
      	if (listPic == null) {
      	listPic=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_IMG");
      	  //并且加入redis
      	   bizCacheUtil.saveObjectListExpire("recommend:activity:picture", listPic, Constants.SECOND_OF_TEN_MINITS);
      	 }
         
      	List<String> listTitle = bizCacheUtil.getObjectList("recommend:activity:title");
      	if (listTitle == null) {
      	listTitle=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_TITLE");
      	  //并且加入redis
      	   bizCacheUtil.saveObjectListExpire("recommend:activity:title", listTitle, Constants.SECOND_OF_TEN_MINITS);
      	 }
        
	List<String> listDesc = bizCacheUtil.getObjectList("recommend:activity:description");
      	if (listDesc == null) {
      	listDesc=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_DESCRIPTION");
      	  //并且加入redis
      	   bizCacheUtil.saveObjectListExpire("recommend:activity:description", listDesc, Constants.SECOND_OF_TEN_MINITS);
      	 }
       
    
        
     
         String acticitySwitch = activitySwitch();
       
        
        //福利表格
         welfareTableList =  getWelfareTableList(acticitySwitch);
        //福利举例
         welfareExampleList = getWelfareExampleList(acticitySwitch);
        //大礼包
//         giftPackageList = getGiftPackageList();
        //特惠专区
//         preferentialList =  getPreferentialList();
         String  activityTime = null;
	    AfResourceDo activityStart = new AfResourceDo();
		   List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_START_TIME");
		   activityStart = list.get(0);
		    if(activityStart !=null){
			activityTime = activityStart.getValue();
        }
      
        
        map.put("listRule",listRule);
        map.put("listPic",listPic);
        map.put("listTitle",listTitle);
        map.put("listDesc",listDesc);
        map.put("welfareExampleList",welfareExampleList);
//      map.put("giftPackageList",giftPackageList);
//      map.put("preferentialList",preferentialList);
        map.put("sumPrizeMoney","0.00");
        map.put("welfareTableList",welfareTableList);
        //用户的邀请码
        String invitationCode = "";
        if(userId !=null && userId>0){
            
            //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId,activityTime);
        DecimalFormat df = new DecimalFormat("######0.00");//金钱格式 保留两位小数
        invitationCode=afRecommendUserService.getUserRecommendCode(userId);
         map.put("sumPrizeMoney",df.format(sumPrizeMoney));
        if(invitationCode.equals("0")){
            //生成邀请码
            AfUserDo userDo = new AfUserDo();
	    Long invteLong = Constants.INVITE_START_VALUE + userId;
	    String inviteCode = Long.toString(invteLong, 36);
	    userDo.setRecommendCode(inviteCode);
	    userDo.setRid(userId);
	    afUserService.updateUser(userDo);
	    invitationCode = inviteCode;
         }
        }
        map.put("invitationCode",invitationCode);
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        return ret;
    }
    
    
    /**
     * 邀请有礼页面配置信息(新版)
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "activityHomeInfo", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String activityHomeInfo(HttpServletRequest request){

        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();

        List  giftPackageList = new ArrayList<Object>();
        List  preferentialList = new ArrayList();
        String acticitySwitch = activitySwitch();
        //大礼包
         giftPackageList = getGiftPackageList(acticitySwitch);
        //特惠专区
         preferentialList =  getPreferentialList(acticitySwitch);
        map.put("giftPackageList",giftPackageList);
        map.put("preferentialList",preferentialList);
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        return ret;
    }
    
    /**
     * 邀请有礼页新手任务(新版)
     * @param request
     * @param
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "newbieTask", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String newbieTask(HttpServletRequest request){
	 FanbeiWebContext context = new FanbeiWebContext();
	 HashMap<String,Object> map =new HashMap<>();
	 List<HashMap> hashMapList =new ArrayList<>();
	 List<NewbieTaskVo>  newbieTaskList = new ArrayList<NewbieTaskVo>();
	 Long userId = -1l;
//	        H5CommonResponse resp = H5CommonResponse.getNewInstance();
//	        AfUserDo afUser = null;
	        try {
			context = doWebCheck(request, false);
			 String userName = context.getUserName();
			 userId = convertUserNameToUserId(userName);
			 boolean isLogin = false;
	                if(userId != null){
	                    isLogin = true;
	                }
			  
	  	AfResourceDo foodResource = new AfResourceDo();
	        String activitySwitch = activitySwitch();
	  	if("O".equals(activitySwitch)){
	  	
	  	foodResource =	(AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_task");
	      	if (foodResource == null) {
	      	  //并且加入redis
	      	   foodResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "FOOD");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_task", foodResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	  	}
	      	
	      	
	  	AfResourceDo authResource = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_auth");
	      	if (authResource == null) {
	      	  //并且加入redis
	      	   authResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "AUTH");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_auth", authResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	 
	  	AfResourceDo shoppingResource = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_shopping");
	      	if (shoppingResource == null) {
	      	  //并且加入redis
	      	   shoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "SHOPPING");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_shopping", shoppingResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	
	      	AfResourceDo creditShoppingResource = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_creditshopping");
	      	if (creditShoppingResource == null) {
	      	  //并且加入redis
	           creditShoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "CREDIT_SHOPPING");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_creditshopping", creditShoppingResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	
	      	AfResourceDo borrowResource = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_borrow");
	      	if (borrowResource == null) {
	      	  //并且加入redis
	      	   borrowResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "BORROW");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_borrow", borrowResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	       	AfResourceDo thirdShoppingResource = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_thridshopping");
	      	if (thirdShoppingResource == null) {
	      	  //并且加入redis
	      	   thirdShoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "THIRD_SHOPPING");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_thridshopping", thirdShoppingResource, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	
		AfResourceDo onlineTime = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:newbie_onlinetime");
	      	if (onlineTime == null) {
	      	  //并且加入redis
	      	    onlineTime = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "ONLINE_TIME");
	      	   bizCacheUtil.saveObject("recommend:activity:newbie_onlinetime", onlineTime, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	
		
		AfResourceDo oneYuanOnlineTime = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:oneyuan_onlinetime");
	      	if (oneYuanOnlineTime == null) {
	      	  //并且加入redis
	      	    oneYuanOnlineTime = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "ACTIVITY_TIME");
	      	   bizCacheUtil.saveObject("recommend:activity:oneyuan_onlinetime", onlineTime, Constants.SECOND_OF_TEN_MINITS);
	      	 }
	      	
	        String activityTime = "";
	        if(onlineTime != null){
	            activityTime = onlineTime.getValue();
	        }
	        String oneYuanTime = "";
	        if(oneYuanOnlineTime != null){
	            oneYuanTime = oneYuanOnlineTime.getValue();
	        }
	        
	  	
	        
	        //是否点外卖
	        int firstOrder = 1;
	        int rebateCount = afBoluomeRebateService.getCountByUserIdAndFirstOrder(userId,firstOrder,oneYuanTime);
	        //活动之前是否点过外卖
	        NewbieTaskVo newbieTaskForFood  = new NewbieTaskVo();
	         newbieTaskForFood =  assignment(foodResource,rebateCount, isLogin);
	        if(newbieTaskForFood !=null){
        	        if(rebateCount == 0){
        	            AfUserDo   afUser = afUserService.getUserByUserName(context.getUserName());
        	            String shopUrl = loginUrl;
        	            if(afUser !=null){
        	            AfShopDo queryShop = new AfShopDo();
        	            queryShop.setType("WAIMAI");
        	            AfShopDo shopInfo = afShopService.getShopInfoBySecType(queryShop);
        	             shopUrl = afShopService.parseBoluomeUrl(shopInfo.getShopUrl(), shopInfo.getPlatformName(), shopInfo.getType(), userId, afUser.getMobile());
        	            }
        	            newbieTaskForFood.setUrl(shopUrl);
        	        }
	        newbieTaskList.add(newbieTaskForFood);
	        }
	        
	        
	        
	        //是否信用认证，0否，1是
	        int auth = 0;
	        AfUserAuthDo afUserAuthDo  = afUserAuthService.getUserAuthInfoByUserId(userId);
	        Date riskTime = null;
	        String riskStatus = "";
	        if(afUserAuthDo !=null){
	             riskTime = afUserAuthDo.getGmtRisk();
	             riskStatus = afUserAuthDo.getRiskStatus();
	        }
	        
	       
	        
	        if("Y".equals(riskStatus)){
	            auth = 1;
	        }
	        NewbieTaskVo newbieTaskForAuth =  assignment(authResource,auth,isLogin);
	        if(newbieTaskForAuth!=null){
        	        if(afUserAuthDo != null &&"N".equals(afUserAuthDo.getBankcardStatus())){
        	            newbieTaskForAuth.setUrl("/fanbei-web/opennative?name=DO_SCAN_ID");
        	        }
        	        
        	       
        	        if(onlineTime != null && riskTime !=null){
        	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        	            try {
        			Date time  = sdf.parse(onlineTime.getValue()) ;
        			 if(riskTime.before(time) && riskStatus.equals("Y")){
        			     newbieTaskForAuth.setValue1(onlineTime.getValue1());
        			}
        		    } catch (ParseException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		    }
        	        }
        	        
        	        newbieTaskList.add(newbieTaskForAuth);
        	        logger.info("get newbieTaskForAuth  = "+JSONObject.toJSONString(newbieTaskForAuth));
	        }
	        try{
	        //商城购物返利
	        int shopShopping = 0;
	        //afOrderService.getRebateShopOrderByUserId(userId);
	        //自营商城
	       List<AfOrderDo> shopOrderList =   afOrderService.getSelfsupportOrderByUserIdOrActivityTime(userId,null);
	       int firstShopping = 0; 
	       if(shopOrderList.size()>0){
		   firstShopping =1;
	        }
	        NewbieTaskVo newbieTaskForFirstShopShopping =  assignment(shoppingResource,firstShopping,isLogin);
	        if(newbieTaskForFirstShopShopping !=null){
        	         if(firstShopping == 1){
        	             newbieTaskForFirstShopShopping.setTitle("商城首次购物返利"+shopOrderList.get(0).getRebateAmount()+"元");
        	         }
        	         newbieTaskList.add(newbieTaskForFirstShopShopping);
        	         logger.info("get newbieTaskForFirstShopShopping  = "+JSONObject.toJSONString(newbieTaskForFirstShopShopping));
	       }
	        }catch(Exception e){
	            logger.error("get newbieTaskForFirstShopShopping error= "+e);
	        }
	        //信用购物
	        try{
	        int authShopping = 0;
	       
	        //活动之前是否信用购物
	        int  activityBeforeAuthShopping  = afOrderService.getAuthShoppingByUserId(userId,activityTime);
	          //是否信用购物
	        int  isAuthShopping  = afOrderService.getAuthShoppingByUserId(userId,null);
	        

	        //活动之前没有信用购物，且现在信用购物成功
	        if(activityBeforeAuthShopping == 0 && isAuthShopping >= 1){
	            authShopping = 1;
	        }
	        NewbieTaskVo newbieTaskForAuthShopping =  assignment(creditShoppingResource,authShopping,isLogin);
	          //活动之前借过钱
	        if(newbieTaskForAuthShopping !=null){
        		 if(activityBeforeAuthShopping >= 1){
        		     authShopping = 1;
        		     newbieTaskForAuthShopping =  assignment(creditShoppingResource,authShopping,isLogin);
        		     newbieTaskForAuthShopping.setValue1(onlineTime.getValue1());
        		 }
        		 newbieTaskList.add(newbieTaskForAuthShopping);
        		  logger.info("get newbieTaskForAuthShopping  = "+JSONObject.toJSONString(newbieTaskForAuthShopping));
	        }
	        }catch(Exception e){
	            logger.error("get newbieTaskForAuthShopping error= "+e);
	        }
	        
	        try{
	        //借钱
	        int borrow = 0;
	        if(onlineTime != null){
	            activityTime = onlineTime.getValue();
	        }
	        
	        int  activityBeforeBorrow  = afBorrowCashService.getCashBorrowByUserIdAndActivity(userId,activityTime);
	        int  isBorrow  = afBorrowCashService.getCashBorrowSuccessByUserId(userId,null);
	        //活动之前没有借过钱包括CLOSED，且现在借钱成功
	        if(activityBeforeBorrow == 0 && isBorrow >= 1){
	            borrow = 1;
	        }
	        NewbieTaskVo newbieTaskForBorrow =  assignment(borrowResource,borrow,isLogin);
	          //活动之前借过钱
	        if(newbieTaskForBorrow!=null){
        		 if(activityBeforeBorrow >= 1){
        		     borrow = 1;
        		     newbieTaskForBorrow =  assignment(borrowResource,borrow,isLogin);
        		     newbieTaskForBorrow.setValue1(onlineTime.getValue1());
        		}
        		 newbieTaskList.add(newbieTaskForBorrow);
        		  logger.info("get newbieTaskForBorrow  = "+JSONObject.toJSONString(newbieTaskForBorrow));
	        }
	        }catch(Exception e){
	            logger.error("get newbieTaskList error= "+e);
	        }
	        
		 //活动期间三次商城购物。（自营）
		 //活动期间商城购物数据
	try{
		 List<AfOrderDo> acticityShopOrderList = afOrderService.getSelfsupportOrderByUserIdOrActivityTime(userId,activityTime);
		 int count = 0;
		 if(acticityShopOrderList.size()>=3){
		     count = acticityShopOrderList.size();
		}
		 NewbieTaskVo newbieTaskForThirdShopping =  assignment(thirdShoppingResource,count,isLogin);
		 if(newbieTaskForThirdShopping!=null){
        		 if(acticityShopOrderList.size()<3){
        		     newbieTaskForThirdShopping.setValue1("已购物<i>"+acticityShopOrderList.size()+"</i>次");
        		     
        		 }else{
        		         //  BigDecimal doubleAmount = shopOrderList.get(2).getRebateAmount().multiply(new BigDecimal(2) );
        		           //
        		           BigDecimal doubleAmount = new BigDecimal(0.00);
        		     	   AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
        		     	   afUserAccountLogDo.setType("DOUBLE_REBATE_CASH");
        		           afUserAccountLogDo.setUserId(userId);
        		            doubleAmount =  afUserAccountLogDao.getUserAmountByType(afUserAccountLogDo);
        		           if(doubleAmount == null){
        		               doubleAmount = new  BigDecimal(0.00);
        		           }
        		           BigDecimal allAmount = doubleAmount.add(acticityShopOrderList.get(0).getRebateAmount()).add(acticityShopOrderList.get(1).getRebateAmount());
        		           newbieTaskForThirdShopping.setValue1("已购物<i>3</i>次，第三次双倍返<i>"+doubleAmount +"</i>,累计返<i>"+allAmount+"</i>");
        		           newbieTaskForThirdShopping.setFinish(1);
        		           logger.info("get newbieTaskForThirdShopping  = "+JSONObject.toJSONString(newbieTaskForThirdShopping));
        		 }
        		 newbieTaskList.add(newbieTaskForThirdShopping);	 
              }
	}catch(Exception e){
	    logger.error("get newbieTaskForThirdShopping error = "+e);
	}
//	 }catch (FanbeiException e) {
//			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
//					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
//				Map<String, Object> data = new HashMap<>();
//				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
//						+ H5OpenNativeType.AppLogin.getCode();
//				data.put("loginUrl", loginUrl);
//				logger.error("/fanbei-web/newbieTask" + context + "login error ");
//			        return	 H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
//			}
		} catch (Exception e) {
			logger.error("/fanbei-web/newbieTask" + context + "error = {}"+e.getStackTrace());
	}
        map.put("newbieTaskList",newbieTaskList);
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        logger.info("get newbieTask hashMapList = "+JSONObject.toJSONString(hashMapList));
        return ret;
    }
    

	/**
	 * 
	 * @Title: convertUserNameToUserId @Description: @param userName @return
	 *         Long @throws
	 */
	private Long convertUserNameToUserId(String userName) {
		Long userId = null;
		if (!StringUtil.isBlank(userName)) {
			AfUserDo user = afUserService.getUserByUserName(userName);
			if (user != null) {
				userId = user.getRid();
			}

		}
		return userId;
	}
  

    private NewbieTaskVo assignment(AfResourceDo resource,int count,boolean isLogin) {

	NewbieTaskVo  newbieTaskVo = new NewbieTaskVo();
			if(resource ==null){
			    return null;
			}
        	String titleName = resource.getValue();
        	String buttonName = resource.getTypeDesc();
        	String urlName = resource.getDescription();
        	if(titleName == null || buttonName == null || urlName == null){
        		return null;
        	}
        	String title[] = titleName.split("@");  
        	String button[] = buttonName.split("@");  
        	String url[] = urlName.split("@");  
        	
        	newbieTaskVo.setFinish(0);
        	newbieTaskVo.setTitle(title[0]);
        	newbieTaskVo.setButton(button[0]);
        	newbieTaskVo.setUrl(loginUrl);
        	if(isLogin){
        	  newbieTaskVo.setUrl(url[0]);
        	}
        	if(count>0){
        	        newbieTaskVo.setFinish(1);
        	        newbieTaskVo.setTitle(title[1]);
        	        newbieTaskVo.setButton(button[1]);
        	        if(isLogin){
        	          newbieTaskVo.setUrl(url[1]);
        	        }
        	        newbieTaskVo.setValue1(resource.getValue4());
        	        return newbieTaskVo;
                }
        	newbieTaskVo.setValue1(resource.getValue1());
        	newbieTaskVo.setValue2(resource.getValue2());
        	newbieTaskVo.setValue3(resource.getValue3());
	

        	return newbieTaskVo;
    }

    private List<AfResourceDo> getWelfareTableList(String acticitySwitch){
        List<AfResourceDo>  welfareTableList = new ArrayList<AfResourceDo>();
        
        AfResourceDo firstAward = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:table_firstaward");
      	if (firstAward == null) {
      	  //并且加入redis
       	   firstAward = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "FIRST_AWARD");
      	   bizCacheUtil.saveObject("recommend:activity:table_firstaward", firstAward, Constants.SECOND_OF_TEN_MINITS);
      	 }
        AfResourceDo secondAward = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:table_secondaward");
      	if (secondAward == null) {
      	  //并且加入redis
           secondAward = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "SECOND_AWARD");
      	   bizCacheUtil.saveObject("recommend:activity:table_secondaward", secondAward, Constants.SECOND_OF_TEN_MINITS);
      	 }
      	
      	 AfResourceDo preferential = new AfResourceDo();
      	 if("O".endsWith(acticitySwitch)){
      	  preferential = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:table_preferential");
      	  if (preferential == null) {
       	  //并且加入redis
       	   preferential = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "PREFERENTIAL");
       	   bizCacheUtil.saveObject("recommend:activity:table_preferential", preferential, Constants.SECOND_OF_TEN_MINITS);
       	 }
      	 }
      	if(firstAward !=null){
      	welfareTableList.add(firstAward);
      	}
      	if(secondAward !=null){
      	 welfareTableList.add(secondAward);
      	}
      	if(preferential !=null){
      	welfareTableList.add(preferential);
      	}
        
	return welfareTableList;
}
    
    
    private List<AfResourceDo> getWelfareExampleList(String acticitySwitch){
        List<AfResourceDo>  welfareExampleList = new ArrayList<AfResourceDo>();
        AfResourceDo auth = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:example_auth");
       	if (auth == null) {
       	  //并且加入redis
       	   auth = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "AUTH");
       	   bizCacheUtil.saveObject("recommend:activity:example_auth", auth, Constants.SECOND_OF_TEN_MINITS);
       	 }
        
       	AfResourceDo borrow = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:example_borrow");
       	if (borrow == null) {
       	  //并且加入redis
       	   borrow = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "BORROW");
       	   bizCacheUtil.saveObject("recommend:activity:example_borrow", borrow, Constants.SECOND_OF_TEN_MINITS);
       	 }
        
	AfResourceDo shop = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:example_shop");
       	if (shop == null) {
       	  //并且加入redis
       	   shop = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "SHOP");
       	   bizCacheUtil.saveObject("recommend:activity:example_shop", shop, Constants.SECOND_OF_TEN_MINITS);
       	 }
       	AfResourceDo stroll =  new AfResourceDo();
       	if("O".endsWith(acticitySwitch)){
       		stroll = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:example_stroll");
       	if (stroll == null) {
       	  //并且加入redis
       	   stroll = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "STROLL");
       	   bizCacheUtil.saveObject("recommend:activity:example_stroll", stroll, Constants.SECOND_OF_TEN_MINITS);
       	 }
       	}
       	
       	if(auth!=null){
       	 welfareExampleList.add(auth);
       	}
       	if(borrow!=null){
       	 welfareExampleList.add(borrow);
         }
       	if(shop!=null){
       	 welfareExampleList.add(shop);
        }
	if(stroll!=null){
	    welfareExampleList.add(stroll);
       }
    
	return welfareExampleList;
    }
    
    private List<AfResourceDo>  getPreferentialList(String activitySwitch){
	         List<AfResourceDo>  preferentialList = new ArrayList<AfResourceDo>();
	         
	         
	         
	         AfResourceDo superValue = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:preferential_supervalue");
	         if (superValue == null) {
	         //并且加入redis
	             superValue = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "SUPER_VALUE");
	            bizCacheUtil.saveObject("recommend:activity:preferential_supervalue", superValue, Constants.SECOND_OF_TEN_MINITS);
	         }
	         AfResourceDo fullCut = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:preferential_fullcut");
	         if (fullCut == null) {
	         //并且加入redis
	             fullCut = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "FULL_CUT");
	            bizCacheUtil.saveObject("recommend:activity:preferential_fullcut", fullCut, Constants.SECOND_OF_TEN_MINITS);
	         }
	         AfResourceDo shop = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:preferential_shop");
	         if (shop == null) {
		         //并且加入redis
	             shop = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "SHOP");
		     bizCacheUtil.saveObject("recommend:activity:preferential_shop", shop, Constants.SECOND_OF_TEN_MINITS);
		 }
	         AfResourceDo stroll = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:preferential_stroll");
	         if("O".equals(activitySwitch)){
	         if (stroll == null) {
		         //并且加入redis
	             stroll = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "STROLL");
		     bizCacheUtil.saveObject("recommend:activity:preferential_stroll", stroll, Constants.SECOND_OF_TEN_MINITS);
		 }
	         }
	         if(superValue !=null){
	             preferentialList.add(superValue);
	         }
                 if(fullCut !=null){
                     preferentialList.add(fullCut);      
                 }
                 if(shop !=null){
                     preferentialList.add(shop);
                 }
                 if(stroll !=null){
                     preferentialList.add(stroll);  
                 }
	      
	        return preferentialList;
  }
    
    private List<HashMap<String, Object>> getGiftPackageList(String acticitySwitch){
	
	     List<HashMap<String, Object>>  giftPackageList = new ArrayList<HashMap<String, Object>>();
	     HashMap<String,Object> map1 =new HashMap<>();
	     HashMap<String,Object> map2 =new HashMap<>();
	     HashMap<String,Object> map3 =new HashMap<>();
	     List<HashMap<String, Object>>  list1 = new ArrayList<HashMap<String, Object>>();
	     List<HashMap<String, Object>>  list2 = new ArrayList<HashMap<String, Object>>();
	     List<HashMap<String, Object>>  list3 = new ArrayList<HashMap<String, Object>>();
	     //实名认证
	     
        	     AfResourceDo resourceDo = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:type:name");
        	     if (resourceDo == null) {
        		         //并且加入redis
        	             resourceDo = afResourceService.getConfigByTypesAndSecType("RECOMMEND_COUPON", "TYPE_NAME");
        		     bizCacheUtil.saveObject("recommend:activity:type:name", resourceDo, Constants.SECOND_OF_TEN_MINITS);
        	     }
		
	     
	     List<AfCouponDo> authCoupon = getCommonCouponMap(CouponScene.CREDIT_AUTH);
	     String value = null;
	     String value1 = null;
	     String value2 = null;
	     String value3 = null;
	     String value4 = null;
	    
	     if(resourceDo != null){ 
		 value = resourceDo.getValue();
		 value1 = resourceDo.getValue1();
		 value2 = resourceDo.getValue2();
		 value3 = resourceDo.getValue3();
		 value4 = resourceDo.getValue4();
	     }
	     
	    
	     
	     //首次借钱，首次购物
	     AfCouponDo  firstLoan = new AfCouponDo();
	     firstLoan =  getcouponForCategoryTag(CouponCateGoryType._FIRST_LOAN_.getCode());
	     AfCouponDo  firstShopping = new AfCouponDo();
		     
	    firstShopping = getcouponForCategoryTag(CouponCateGoryType._FIRST_SHOPPING_.getCode());
	     
	     
	     if(firstLoan !=null){
        	     HashMap<String,Object> loanMap =new HashMap<>();
        	     loanMap.put("threshold", firstLoan.getLimitAmount());
        	     loanMap.put("couponAmount",firstLoan.getAmount());
        	     loanMap.put("couponName", value3);
        	     list1.add(loanMap);
	     }
	     if(firstShopping !=null){
        	     HashMap<String,Object> shoppingMap =new HashMap<>();
        	     shoppingMap.put("threshold", firstShopping.getLimitAmount());
        	     shoppingMap.put("couponAmount",firstShopping.getAmount());
        	     shoppingMap.put("couponName", value3);
        	     list1.add(shoppingMap);
	     }
	    
	     if(authCoupon!= null && authCoupon.size()>0){
		 for(int i=0;i<authCoupon.size();i++){
        		 AfCouponDo  coupon = authCoupon.get(i);
        		 HashMap<String,Object> map =new HashMap<>();
        		 map.put("threshold", coupon.getLimitAmount());
        		 map.put("couponAmount",coupon.getAmount());
        		 map.put("couponName",value3);
        		 list1.add(map);
		 }
	     }
	     map1.put("couponTitle", value);
	     map1.put("couponList",list1 );
	     giftPackageList.add(map1);
	     
	     //注册(自营商城),首单爆品
	//     List<AfCouponDo> rigsetCoupon = getCommonCouponMap(CouponScene.REGIST);
	     List<AfCouponDo> rigsetCoupon = getcouponListForCategoryTag(CouponCateGoryType._FIRST_SINGLE_.getCode());
	     
	     if(rigsetCoupon.size()>0){
		for(int i=0;i<rigsetCoupon.size();i++){
		    AfCouponDo shop = rigsetCoupon.get(i);
		     HashMap<String,Object> shopMap =new HashMap<>();
		     shopMap.put("threshold", shop.getLimitAmount());
		     shopMap.put("couponAmount",shop.getAmount());
		     shopMap.put("couponName", value4);
		     list2.add(shopMap);
		}
		     map2.put("couponTitle", value1);
		     map2.put("couponList",list2 );
		     giftPackageList.add(map2);
	     }
	    
	     if("O".equals(acticitySwitch)){
        	     List<BoluomeCouponResponseExtBo> bolumeCouponList =  boluomeCouponList();
        	     if(bolumeCouponList.size()>0){
                	     for(BoluomeCouponResponseExtBo boluomeCoupon:bolumeCouponList){
                		      HashMap<String,Object> ggMap =new HashMap<>();
                		      ggMap.put("threshold", boluomeCoupon.getThreshold());
                		      ggMap.put("couponAmount",boluomeCoupon.getValue());
                		      ggMap.put("couponName",boluomeCoupon.getCouponName() );
                		      list3.add(ggMap);
                	     }
        	    
        	     if(list3 !=null){
        		 map3.put("couponTitle", value2); 
        		 map3.put("couponList", list3); 
        	     }
        	      giftPackageList.add(map3);
        	     }
	     }
	     
	     return giftPackageList;
    }
    
   
    private  List<AfCouponDo>  getCommonCouponMap(CouponScene scene) {

	List<Long> couponIds = afCouponSceneService.getCounponIds(scene);

	List<AfCouponDo> couponList = null;

	if (CollectionUtils.isNotEmpty(couponIds)) {
		couponList = afCouponService.listCouponByIds(couponIds);
	}


	return couponList;
}

    private AfCouponDo getcouponForCategoryTag(String tag){
	AfCouponDo afCouponDo = new AfCouponDo();
	AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
	if(couponCategory !=null){
        	String coupons = couponCategory.getCoupons();
        	JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
        
        	//List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();
        
        	if (couponsArray.size()>0) {
        		String couponId = (String) couponsArray.getString(0);
        	        afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
        	}
	}else{
	    return null; 
	}
	return afCouponDo;
    }
    private List<AfCouponDo> getcouponListForCategoryTag(String tag){
   	AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
   	List<AfCouponDo> afCouponList = new ArrayList<AfCouponDo>();
   	if(couponCategory !=null){
   	String coupons = couponCategory.getCoupons();
   	JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
   	
           //List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();
           if (couponsArray.size()>0) {
           	        for(int i = 0; i< couponsArray.size(); i++){
           	                AfCouponDo afCouponDo = new AfCouponDo();
                   		String couponId = (String) couponsArray.getString(i);
                   	        afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
                   	        afCouponList.add(afCouponDo);
           	        }
           	}
   	}
   	return afCouponList;
       }
    /**
	 * 
	 * @说明：获得用户优惠券列表
	 * @param: @return
	 * @return: String
	 */
	private static String couponUrl = null;

	private static String getCouponUrl() {
		if (couponUrl == null) {
			couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
			return couponUrl;
		}
		return couponUrl;
	}
    private List<BoluomeCouponResponseExtBo> boluomeCouponList(){
	 List<BoluomeCouponResponseExtBo> boluomeCouponList = new ArrayList<>();

	 AfResourceDo resourceDo = (AfResourceDo)bizCacheUtil.getObject("recommend:activity:boluome_coupon");
         if (resourceDo == null) {
	         //并且加入redis
             resourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "BOLUOME_COUPON");
	     bizCacheUtil.saveObject("recommend:activity:boluome_coupon", resourceDo, Constants.SECOND_OF_TEN_MINITS);
	 }
         List<String> bList = new ArrayList<>();
  
	if (resourceDo != null) {
	   bList.add(resourceDo.getValue());
	}
	 List<String> rigsetCouponIdList = getBoluomecouponIdListForCategoryTag(CouponCateGoryType._NEW_USER_BOLUOME_COUPON_.getCode());
         if(rigsetCouponIdList != null && rigsetCouponIdList.size() >0 ){
             bList.addAll(rigsetCouponIdList);
         }
		
//		bList.add(resourceDo.getValue2());
//		bList.add(resourceDo.getValue3());
		if (bList != null && bList.size() > 0) {
			for (String resouceIdStr : bList) {
				Long resourceId = Long.parseLong(resouceIdStr);
				AfResourceDo couponResourceDo = afResourceService.getResourceByResourceId(resourceId);
				logger.info("boluomeCoupon  resourceId = {},couponResourceDo = {}", resourceId,
						couponResourceDo);
				if (couponResourceDo != null) {
					String uri = couponResourceDo.getValue();
					String[] pieces = uri.split("/");
					if (pieces.length > 9) {
						String app_id = pieces[6];
						String campaign_id = pieces[8];
						String user_id = "0";
						// 获取boluome的券的内容
						String url = getCouponUrl() + "?" + "app_id=" + app_id + "&user_id=" + user_id
								+ "&campaign_id=" + campaign_id;
						String reqResult = HttpUtil.doGet(url, 10);
						logger.info("invition getCouponUrl reqResult = {}", reqResult);
						if (!StringUtil.isBlank(reqResult)) {
							ThirdResponseBo thirdResponseBo = JSONObject.parseObject(reqResult,
									ThirdResponseBo.class);
							if (thirdResponseBo != null && "0".equals(thirdResponseBo.getCode())) {
								List<BoluomeCouponResponseParentBo> listParent = JSONArray.parseArray(
										thirdResponseBo.getData(), BoluomeCouponResponseParentBo.class);
								if (listParent != null && listParent.size() > 0) {
									BoluomeCouponResponseParentBo parentBo = listParent.get(0);
									if (parentBo != null) {
										String activityCoupons = parentBo.getActivity_coupons();
										String result = activityCoupons.substring(1,
												activityCoupons.length() - 1);
										String replacement = "," + "\"sceneId\":" + resourceId + "}";
										String rString = result.replaceAll("}", replacement);
										// 字符串转为json对象
										BoluomeCouponResponseExtBo BoluomeCouponResponseExtBo = JSONObject
												.parseObject(rString, BoluomeCouponResponseExtBo.class);
//										List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil
//												.getActivityCouponList(uri);
										BoluomeCouponResponseExtBo.setCouponName(couponResourceDo.getName());
										
										boluomeCouponList.add(BoluomeCouponResponseExtBo);
										
									}
								}
							}
						}
					}
				}
			}
	}
	return boluomeCouponList;
    }
    
    private List<String> getBoluomecouponIdListForCategoryTag(String tag){
    List<String> boluomeCouponIdList = new ArrayList<>();
    AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(tag);
	if(couponCategory != null){
	    	String coupons = couponCategory.getCoupons();
		JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
		for (int i = 0; i < couponsArray.size(); i++) {
			String couponId = (String) couponsArray.getString(i);
			boluomeCouponIdList.add(couponId);
    		}
	}
	return boluomeCouponIdList;
}	    			    


    
    
    
    private String activitySwitch(){
	 String acticitySwitch = "";
	 AfResourceDo  afResourceDo  =  afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "RECOMMEND_NEWBIE_TASK");
	 String ctype = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
	//线上为开启状态
	  if(afResourceDo != null){
               if (Constants.INVELOMENT_TYPE_ONLINE.equals(ctype) || Constants.INVELOMENT_TYPE_TEST.equals(ctype)) {
        	       acticitySwitch = afResourceDo.getValue();
        	    } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(ctype) ){
        	       acticitySwitch = afResourceDo.getValue1();
        	  }
                }
	  return acticitySwitch;
    }
    
    
    /**
     * 奖励详细查询
     * @param request
     * @param currentPage
     * @param type
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "rewardQuery", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String rewardQuery(HttpServletRequest request,String type,Integer currentPage, Integer pageSize){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        //Long userId = 73772l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        try {
		context = doWebCheck(request, true);
		 String userName = context.getUserName();
		 userId = convertUserNameToUserId(userName);
        }catch (FanbeiException e) {
			if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
					|| e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
				Map<String, Object> data = new HashMap<>();
				String loginUrl = ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative
						+ H5OpenNativeType.AppLogin.getCode();
				data.put("loginUrl", loginUrl);
				logger.error("/fanbei-web/rewardQuery" + context + "login error ");
			        return	 H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
			}
        }catch  (Exception e) {
            logger.error("commitChannelRegister", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
        if(currentPage==null){
            currentPage=1;
        }
        String ret = null;
        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        List<AfRecommendUserDto> rewardQueryList =new ArrayList<>();
        if("1".equals(type)||"2".equals(type)){
            rewardQueryList=afRecommendUserService.rewardQuery(userId,type,currentPage,pageSize);
            Integer count =afRecommendUserService.rewardQueryCount(userId,type);
            map.put("rewardQueryList",rewardQueryList);
            map.put("count",count);
            map.put("currentPage",currentPage);
            hashMapList.add(map);
            ret =JSON.toJSONString(hashMapList);
        }else{
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null);
            ret =resp.toString();
        }
        return ret;
    }


    /**
     *点击分享的时候 插入数据
     * @param request
     * @param shareWith
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "shareActivity", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String shareActivity(HttpServletRequest request,String shareWith){
        FanbeiWebContext context = new FanbeiWebContext();
        Long userId = -1l;
        //Long userId = 55555l;
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        AfUserDo afUser = null;
        Integer type=null;
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                afUser = afUserService.getUserByUserName(context.getUserName());
                if(afUser != null){
                    userId = afUser.getRid();
                }
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
                return resp.toString();
            }
        }catch(Exception e) {
            logger.error("commitChannelRegister", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
        if("sharewithWEIXIN_CIRCLE".equals(shareWith)){
            type=0;
        }else if("sharewithWEIXIN".equals(shareWith)){
            type=1;
        }else if("sharewithQZONE".equals(shareWith)){
            type=2;
        }else if("sharewithQRCODE".equals(shareWith)){
            type=3;
        }else{
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null);
            return resp.toString();
        }
        String invitationCode=afRecommendUserService.getUserRecommendCode(userId);
        String uuid = UUID.randomUUID().toString(); //获取UUID并转化为String对象
        uuid = uuid.replace("-", "");
        if(afRecommendUserService.insertShareWithData(uuid,userId,type,invitationCode)>0){
            resp = H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", null);
        }else{
            resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.CALCULATE_SHA_256_ERROR.getDesc(), "", null);
        }
        return resp.toString();
    }



    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return null;
    }
}
