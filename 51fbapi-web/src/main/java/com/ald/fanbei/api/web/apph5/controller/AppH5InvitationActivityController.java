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
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeRebateService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfCouponSceneService;
import com.ald.fanbei.api.biz.service.AfCouponService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.util.CouponSceneRuleEnginerUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.CouponScene;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponSceneDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfSigninDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfRecommendUserDto;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
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
        }catch  (Exception e) {
            logger.error("activityUserInfo error", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }

        HashMap<String,Object> map =new HashMap<>();
        List<HashMap> hashMapList =new ArrayList<>();
        //查看活动规则,图片,标题,描述
        List listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
        List listPic=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_IMG");
        List listTitle=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_TITLE");
        List listDesc=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_DESCRIPTION");
        //用户的邀请码
        String invitationCode=afRecommendUserService.getUserRecommendCode(userId);
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
        
        //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId);
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
            }else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
                return resp.toString();
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
        AfUserDo afUser = null;
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
        	   afUser = afUserService.getUserByUserName(context.getUserName());
                if(afUser != null){
                    userId = afUser.getRid();
                }
            } else{
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
               return resp.toString();
            }
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
        List listRule=afRecommendUserService.getActivityRule("RECOMMEND_RULE");
        List listPic=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_IMG");
        List listTitle=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_TITLE");
        List listDesc=afRecommendUserService.getActivityRule("RECOMMEND_SHARED_DESCRIPTION");
    
        
        //用户的邀请码
        String invitationCode=afRecommendUserService.getUserRecommendCode(userId);
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
       
        
        //福利表格
         welfareTableList =  getWelfareTableList();
        //福利举例
         welfareExampleList = getWelfareExampleList();
        //大礼包
//         giftPackageList = getGiftPackageList();
        //特惠专区
//         preferentialList =  getPreferentialList();
         
       //用户的总共奖励金额
        double sumPrizeMoney=afRecommendUserService.getSumPrizeMoney(userId);
        DecimalFormat df = new DecimalFormat("######0.00");//金钱格式 保留两位小数
        
        
        map.put("listRule",listRule);
        map.put("listPic",listPic);
        map.put("listTitle",listTitle);
        map.put("listDesc",listDesc);
        map.put("invitationCode",invitationCode);
        map.put("welfareTableList",welfareTableList);
        map.put("welfareExampleList",welfareExampleList);
//      map.put("giftPackageList",giftPackageList);
//      map.put("preferentialList",preferentialList);
        map.put("sumPrizeMoney",df.format(sumPrizeMoney));
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

        List  giftPackageList = new ArrayList();
        List  preferentialList = new ArrayList();
        //大礼包
         giftPackageList = getGiftPackageList();
        //特惠专区
         preferentialList =  getPreferentialList();
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
	                 
	            }   else{   
	                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getDesc(), "", null);
	               return resp.toString();
	            }
	        }catch  (Exception e) {
	            logger.error("activityHomeInfo error", e);
	            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
	            return resp.toString();
	        }
	        HashMap<String,Object> map =new HashMap<>();
	        List<HashMap> hashMapList =new ArrayList<>();
	        List  newbieTaskList = new ArrayList();
	        AfResourceDo foodResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "FOOD");
	        AfResourceDo authResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "AUTH");
	        AfResourceDo shoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "SHOPPING");
	        AfResourceDo creditShoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "CREDIT_SHOPPING");
	        AfResourceDo borrowResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "BORROW");
	        AfResourceDo thirdShoppingResource = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "THIRD_SHOPPING");
	        
	        //是否点外卖
	        int firstOrder = 1;
	        int rebateCount = afBoluomeRebateService.getCountByUserIdAndFirstOrder(userId,firstOrder);
	        
	        AfResourceDo onlineTime = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "ONLINE_TIME");
	        NewbieTaskVo newbieTaskForFood =  assignment(foodResource,rebateCount);
	        newbieTaskList.add(newbieTaskForFood);
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
	        NewbieTaskVo newbieTaskForAuth =  assignment(authResource,auth);
	       
	        if(onlineTime != null && riskTime !=null){
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
	            try {
			Date time  = sdf.parse(onlineTime.getValue()) ;
			 if(riskTime.before(time)){
			     newbieTaskForAuth.setValue4(onlineTime.getValue1());
			}
		    } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
	        }
	        newbieTaskList.add(newbieTaskForAuth);
	        //商城购物返利
	        int shopShopping = 0;
	        //afOrderService.getRebateShopOrderByUserId(userId);
	        //自营商城
	       List<AfOrderDo> shopOrderList =   afOrderService.getShopOrderByUserIdOrActivityTime(userId,null);
	       int firstShopping = 0; 
	       if(shopOrderList.size()>0){
		   firstShopping =1;
	        }
	        NewbieTaskVo newbieTaskForFirstShopShopping =  assignment(shoppingResource,firstShopping);
	        if(firstShopping == 1){
	            newbieTaskForFirstShopShopping.setTitle("商城购物返利"+shopOrderList.get(0).getRebateAmount()+"元");
	        }
	        newbieTaskList.add(newbieTaskForFirstShopShopping);
	        
	        String activityTime = "";
	        //信用购物
	        int authShopping = 0;
	        if(onlineTime != null){
	            activityTime = onlineTime.getValue();
	        }
	        //活动之前是否信用购物
	        int  activityBeforeAuthShopping  = afOrderService.getAuthShoppingByUserId(userId,activityTime);
	          //是否信用购物
	        int  isAuthShopping  = afOrderService.getAuthShoppingByUserId(userId,null);
	        

	        //活动之前没有信用购物，且现在信用购物成功
	        if(activityBeforeAuthShopping == 0 && isAuthShopping >= 1){
	            authShopping = 1;
	        }
	        NewbieTaskVo newbieTaskForAuthShopping =  assignment(creditShoppingResource,authShopping);
	          //活动之前借过钱
		 if(activityBeforeAuthShopping == 1){
		     newbieTaskForAuthShopping.setValue4(onlineTime.getValue1());
		}
		 newbieTaskList.add(newbieTaskForAuthShopping);
	        
	        //借钱
	        int borrow = 0;
	        if(onlineTime != null){
	            activityTime = onlineTime.getValue();
	        }
	        //活动之前是否借款成功，
	        int  activityBeforeBorrow  = afBorrowCashService.getCashBorrowSuccessByUserId(userId,activityTime);
	        int  isBorrow  = afBorrowCashService.getCashBorrowSuccessByUserId(userId,null);
	        //活动之前没有借过钱，且现在借钱成功
	        if(activityBeforeBorrow == 0 && isBorrow >= 1){
	            borrow = 1;
	        }
	        NewbieTaskVo newbieTaskForBorrow =  assignment(borrowResource,borrow);
	          //活动之前借过钱
		 if(activityBeforeBorrow >= 1){
		     newbieTaskForBorrow.setValue4(onlineTime.getValue1());
		}
		 newbieTaskList.add(newbieTaskForBorrow);
		 
		 //活动期间三次商城购物。（自营）
		 //活动期间商城购物数据
		 
		 List<AfOrderDo> acticityShopOrderList = afOrderService.getShopOrderByUserIdOrActivityTime(userId,activityTime);
		 int count = 0;
		 if(acticityShopOrderList.size()>=3){
		     count = acticityShopOrderList.size();
		}
		 NewbieTaskVo newbieTaskForThirdShopping =  assignment(thirdShoppingResource,count);
		 if(acticityShopOrderList.size()<3){
		     newbieTaskForThirdShopping.setValue1("已购物<i>"+acticityShopOrderList.size()+"</i>次");
		     
		 }else{
		           BigDecimal doubleAmount = shopOrderList.get(2).getRebateAmount().multiply(new BigDecimal(2) );
		           BigDecimal allAmount = doubleAmount.add(acticityShopOrderList.get(0).getRebateAmount()).add(acticityShopOrderList.get(1).getRebateAmount());
		           newbieTaskForThirdShopping.setValue1("已购物<i>3</i>次，第三次双倍返<i>"+doubleAmount +"</i>,累计返<i>"+allAmount+"</i>");
		           newbieTaskForThirdShopping.setFinish(1);
		 }
		 newbieTaskList.add(newbieTaskForThirdShopping);	 
		 
        map.put("newbieTaskList",newbieTaskList);
        hashMapList.add(map);
        String ret = JSON.toJSONString(hashMapList);
        return ret;
    }
    
    
    

    private NewbieTaskVo assignment(AfResourceDo resource,int count) {
	NewbieTaskVo  newbieTaskVo = new NewbieTaskVo();
	String titleName = resource.getValue();
	String buttonName = resource.getTypeDesc();
	String urlName = resource.getDescription();
	String title[] = titleName.split("/");  
	String button[] = buttonName.split("/");  
	String url[] = urlName.split("/");  
	
	newbieTaskVo.setFinish(0);
	newbieTaskVo.setTitle(title[0]);
	newbieTaskVo.setButton(button[0]);
	newbieTaskVo.setUrl(url[0]);
	if(count>0){
	        newbieTaskVo.setFinish(1);
	        newbieTaskVo.setTitle(title[1]);
	        newbieTaskVo.setButton(button[1]);
	        newbieTaskVo.setUrl(url[1]);
	        newbieTaskVo.setValue1(resource.getValue4());
	        return newbieTaskVo;
        }
	newbieTaskVo.setValue1(resource.getValue1());
	newbieTaskVo.setValue2(resource.getValue2());
	newbieTaskVo.setValue3(resource.getValue3());
	
	return newbieTaskVo;
    }

    private List getWelfareTableList(){
        List  welfareTableList = new ArrayList();
        AfResourceDo firstAward = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "FIRST_AWARD");
        AfResourceDo secondAward = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "SECOND_AWARD");
        AfResourceDo preferential = afResourceService.getConfigByTypesAndSecType("RECOMMEND_TABLE", "PREFERENTIAL");
        welfareTableList.add(firstAward);
        welfareTableList.add(secondAward);
        welfareTableList.add(preferential);
	return welfareTableList;
}
    
    
    private List getWelfareExampleList(){
        List  welfareExampleList = new ArrayList();
        AfResourceDo auth = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "AUTH");
        AfResourceDo borrow = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "BORROW");
        AfResourceDo shop = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "SHOP");
        AfResourceDo stroll = afResourceService.getConfigByTypesAndSecType("RECOMMEND_EXAMPLE", "STROLL");
        welfareExampleList.add(auth);
        welfareExampleList.add(borrow);
        welfareExampleList.add(shop);
        welfareExampleList.add(stroll);
	return welfareExampleList;
    }
    
    private List  getPreferentialList(){
	         List  preferentialList = new ArrayList();
	        AfResourceDo superValue = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "SUPER_VALUE");
	        AfResourceDo fullCut = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "FULL_CUT");
	        AfResourceDo shop = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "SHOP");
	        AfResourceDo stroll = afResourceService.getConfigByTypesAndSecType("RECOMMEND_PREFERENTIAL", "STROLL");
	        preferentialList.add(superValue);
	        preferentialList.add(fullCut);
	        preferentialList.add(shop);
	        preferentialList.add(stroll);
	        return preferentialList;
  }
    
    private List getGiftPackageList(){
	     List  giftPackageList = new ArrayList();
	     List  titleList = new ArrayList();
	     HashMap<String,Object> map1 =new HashMap<>();
	     HashMap<String,Object> map2 =new HashMap<>();
	     HashMap<String,Object> map3 =new HashMap<>();
	     HashMap<String,Object> titleMap =new HashMap<>();
	     List  list1 = new ArrayList();
	     List  list2 = new ArrayList();
	     List  list3 = new ArrayList();
	     //实名认证
	     List<AfCouponDo> authCoupon = getCommonCouponMap(CouponScene.CREDIT_AUTH);
	     AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("RECOMMEND_COUPON", "TYPE_NAME");
	     String value = "借钱";
	     String value1 = "商城";
	     String value2 = "吃完住行";
	     String value3 = "还款优惠";
	     String value4 = "自营商城";
	    
	     if(resourceDo != null){
		 value = resourceDo.getValue();
		 value1 = resourceDo.getValue1();
		 value2 = resourceDo.getValue2();
		 value3 = resourceDo.getValue3();
		 value4 = resourceDo.getValue4();
	     }
	     
	    
	     //首次借钱，首次购物
	     AfCouponDo  firstLoan = getcouponForCategoryTag("_FIRST_LOAN_");
	     AfCouponDo  firstShopping = getcouponForCategoryTag("_FIRST_SHOPPING_");
	     HashMap<String,Object> loanMap =new HashMap<>();
	     loanMap.put("threshold", firstLoan.getLimitAmount());
	     loanMap.put("couponAmount",firstLoan.getAmount());
	     loanMap.put("couponName", value3);
	     HashMap<String,Object> shoppingMap =new HashMap<>();
	     shoppingMap.put("threshold", firstShopping.getLimitAmount());
	     shoppingMap.put("couponAmount",firstShopping.getAmount());
	     shoppingMap.put("couponName", value3);
	     list1.add(loanMap);
	     list1.add(shoppingMap);
	     
	     if(authCoupon.size()>0){
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
	     
	     //注册(自营商城)
	     List<AfCouponDo> rigsetCoupon = getCommonCouponMap(CouponScene.REGIST);
	     if(rigsetCoupon.size()>0){
		for(int i=0;i<rigsetCoupon.size();i++){
		    AfCouponDo shop = rigsetCoupon.get(i);
		     HashMap<String,Object> shopMap =new HashMap<>();
		     shopMap.put("threshold", shop.getLimitAmount());
		     shopMap.put("couponAmount",shop.getAmount());
		     shopMap.put("couponName", value4);
		     list2.add(shopMap);
		}
	     }
	     map2.put("couponTitle", value1);
	     map2.put("couponList",list2 );
	     giftPackageList.add(map2);
	     
	     List<BoluomeCouponResponseExtBo> bolumeCouponList =  boluomeCouponList();
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
	AfCouponCategoryDo couponCategory = afCouponCategoryService.getCouponCategoryByTag(tag);
	String coupons = couponCategory.getCoupons();
	JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
	AfCouponDo afCouponDo = new AfCouponDo();
	//List<AfCouponDouble12Vo> couponVoList = new ArrayList<AfCouponDouble12Vo>();

	if (couponsArray.size()>0) {
		String couponId = (String) couponsArray.getString(0);
	        afCouponDo = afCouponService.getCouponById(Long.parseLong(couponId));
	}
	return afCouponDo;
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

	AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY", "BOLUOME_COUPON");
	if (resourceDo != null) {
		List<String> bList = new ArrayList<>();
		bList.add(resourceDo.getValue());
		bList.add(resourceDo.getValue2());
		bList.add(resourceDo.getValue3());
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
	}
	return boluomeCouponList;
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
