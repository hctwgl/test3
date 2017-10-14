package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.BoluomeActivityRuleBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BoluomeCouponResponseParentBo;
import com.ald.fanbei.api.biz.bo.BrandActivityCouponResponseBo;
import com.ald.fanbei.api.biz.bo.PickBrandCouponRequestBo;
import com.ald.fanbei.api.biz.bo.ThirdResponseBo;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityCouponService;
import com.ald.fanbei.api.biz.service.AfBoluomeActivityService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityItemsDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserItemsDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserLoginDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeActivityUserRebateDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityCouponDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserRebateDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.query.AfBoluomeActivityUserRebateQuery;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;



/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:35:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBoluomeActivityService")
public class AfBoluomeActivityServiceImpl extends ParentServiceImpl<AfBoluomeActivityDo, Long> implements AfBoluomeActivityService {
        @Resource
	private AfOrderDao orderDao;
	@Resource
	private AfUserCouponDao afUserCouponDao;
	@Resource
	private  AfUserAccountDao afUserAccountDao;
	@Resource
	private AfUserDao afUserDao;
	@Resource
	AfUserAccountLogDao afUserAccountLogDao;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	BoluomeUtil boluomeUtil;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBoluomeActivityUserLoginDao afBoluomeActivityUserLoginDao;
	@Resource
	AfBoluomeActivityUserRebateDao afBoluomeActivityUserRebateDao;
	@Resource
	AfShopService afShopService;
	@Resource
	AfBoluomeActivityItemsDao afBoluomeActivityItemsDao;
	@Resource
	AfBoluomeActivityCouponService afBoluomeActivityCouponService;
	@Resource
	AfBoluomeActivityUserItemsDao afBoluomeActivityUserItemsDao;
	@Resource
	AfBoluomeActivityDao afBoluomeActivityDao;
	@Resource
	private SmsUtil smsUtil;
	@Resource
	AfOrderService afOrderService;
        private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityServiceImpl.class);
        private static String couponUrl = null;

		@Override
	public BaseDao<AfBoluomeActivityDo, Long> getDao() {
		return afBoluomeActivityDao;
	}

	@Override
	public int ggLightActivity(AfOrderDo afOrder) {
		    // TODO Auto-generated method stub
            
	//若在菠萝觅活动期间内则返利
	AfBoluomeActivityDo activityDo = new AfBoluomeActivityDo();
	activityDo.setStatus("O");
	AfBoluomeActivityDo afBoluomeActivityDo =  afBoluomeActivityDao.getByCommonCondition(activityDo);
	if(afBoluomeActivityDo !=null){
        	Date startTime = afBoluomeActivityDo.getGmtCreate();
        	Date endTime = afBoluomeActivityDo.getGmtEnd();
	        if(DateUtil.afterDay(endTime,afOrder.getGmtCreate()) && DateUtil.afterDay(afOrder.getGmtCreate(),startTime)){
	    	   boluomeActivity(afOrder,afBoluomeActivityDo);
          }
    }
	return 0;
  }
	/**
	 * 菠萝觅活动返利
	 * @param afOrder
	 * @return
	 */
	public int boluomeActivity(final AfOrderDo afOrder,AfBoluomeActivityDo afBoluomeActivityDo){
		
	    
	        //登陆者消费就返利和返卡片
	         Long userId = afOrder.getUserId();
//		 查询商城id
//		 AfShopDo afShopDo =new AfShopDo();
//		 afShopDo.setType(afOrder.getSecType());
//		 AfShopDo shop =  afShopService.getShopInfoBySecTypeOpen(afShopDo);
	         String platformName = afOrder.getOrderType(); //BOLUOME
	         String type = afOrder.getSecType();    //JIUDIAN
	         String serviceProvider = afOrder.getServiceProvider();  //CTRIP
	         //如果是话费和流量，则通过chongzhi取查询
		    if("HUAFEI".equals(type)||"LIULIANG".equals(type)){
			type ="CHONGZHI";
		    }
		 AfShopDo shop = afShopService.getShopByPlantNameAndTypeAndServiceProvider(platformName, type,  serviceProvider);
		logger.info("shop",shop);
		if (shop != null) {
			Long shopId = shop.getRid();
			//根据shopId查询卡片信息
			AfBoluomeActivityItemsDo ItemsMessageSet = new AfBoluomeActivityItemsDo();
			ItemsMessageSet.setRefId(shopId);
			// ItemsMessageSet.setBoluomeActivityId(userLoginRecord.getBoluomeActivityId());
			AfBoluomeActivityItemsDo afBoluomeActivityItemsDo  =  afBoluomeActivityItemsDao.getItemsInfo(ItemsMessageSet);
			if(afBoluomeActivityItemsDo!= null){
				//规则判断
				BigDecimal actualAmount = afOrder.getActualAmount();
				BigDecimal minConsumption=new BigDecimal(afBoluomeActivityItemsDo.getRuleJson());
				int res = actualAmount.compareTo(minConsumption);
				if(res==1 || res == 0){
					//添加自己返利记录
					AfBoluomeActivityUserRebateDo ownRebate =new AfBoluomeActivityUserRebateDo();
					ownRebate.setUserId(userId);
					//根据useid获取username
					AfUserDo afUserDo  = afUserDao.getUserById(userId);
					ownRebate.setUserName(afUserDo.getUserName());
					ownRebate.setOrderId(afOrder.getRid());//id还是orderNo?
					ownRebate.setRebate(afOrder.getRebateAmount());
					Date nowTime = new Date();
					ownRebate.setGmtCreate(nowTime);
					ownRebate.setGmtModified(nowTime);
					ownRebate.setBoluomeActivityId(afBoluomeActivityItemsDo.getBoluomeActivityId());
					afBoluomeActivityUserRebateDao.saveRecord(ownRebate);
					logger.info("activityUserRebate",ownRebate);
					//添加卡片信息
					AfBoluomeActivityUserItemsDo userItemsDo = new AfBoluomeActivityUserItemsDo();
					userItemsDo.setBoluomeActivityId(afBoluomeActivityItemsDo.getBoluomeActivityId());
					userItemsDo.setItemsId(afBoluomeActivityItemsDo.getRid());
					userItemsDo.setSourceId((long) -1);
					userItemsDo.setStatus("NORMAL");
					userItemsDo.setUserId(userId);
					userItemsDo.setUserName(afUserDo.getUserName());
					userItemsDo.setGmtSended(nowTime);
					afBoluomeActivityUserItemsDao.saveRecord(userItemsDo);
					logger.info("activityUserItems",userItemsDo);

					//给他人返利
					//在订单创建之前的绑定的最后一个用户
					AfBoluomeActivityUserLoginDo queryUserLoginRecord  = new AfBoluomeActivityUserLoginDo();
					queryUserLoginRecord.setUserId(userId);
					queryUserLoginRecord.setGmtCreate(afOrder.getGmtCreate());
					//AfBoluomeActivityUserLoginDo userLoginRecord = afBoluomeActivityUserLoginDao.getUserLoginRecordByUserId(userId);
					AfBoluomeActivityUserLoginDo userLoginRecord = afBoluomeActivityUserLoginDao.getUserLoginRecord(queryUserLoginRecord);
					logger.info("userLoginRecord",userLoginRecord);
					
					if(userLoginRecord!=null){
						//最后绑定时间，和当前下单时间
						Date lastTime = userLoginRecord.getGmtCreate();
						Date orderTime = afOrder.getGmtCreate();
						AfBoluomeActivityUserRebateQuery userRebateQuery = new  AfBoluomeActivityUserRebateQuery();
						userRebateQuery.setLastTime(lastTime);
						userRebateQuery.setOrderTime(orderTime);
						userRebateQuery.setUserId(userId);
						userRebateQuery.setRefUserId(userLoginRecord.getRefUserId());
						//查询时间内是否有对应的返利记录
						AfBoluomeActivityUserRebateQuery RebateQueryResult  = afBoluomeActivityUserRebateDao.getRebateCountNumber(userRebateQuery);
						logger.info("activityUserRebateQuery ",RebateQueryResult);
						if(RebateQueryResult.getFanLiRecordTime()<1){
							//进行返利
							AfBoluomeActivityUserRebateDo refMessage = new AfBoluomeActivityUserRebateDo();
							refMessage.setUserId(userId);
							refMessage.setUserName(afUserDo.getUserName());
							refMessage.setRefUserId(userLoginRecord.getRefUserId());
							refMessage.setBoluomeActivityId(userLoginRecord.getBoluomeActivityId());
							refMessage.setRefOrderId(afOrder.getRid());//id还是orderNo?
							refMessage.setInviteRebate(afOrder.getRebateAmount());
							refMessage.setGmtCreate(nowTime);
							refMessage.setGmtModified(nowTime);
							afBoluomeActivityUserRebateDao.saveRecord(refMessage);
							logger.info("afBoluomeActivityUserRebateDao ",refMessage);
							//更新账户金额
							AfUserAccountDo refAccountInfo = new AfUserAccountDo();
							refAccountInfo.setRebateAmount(afOrder.getRebateAmount());
							refAccountInfo.setUserId(userLoginRecord.getRefUserId());
							afUserAccountService.updateUserAccount(refAccountInfo);
							logger.info("refAccountInfo ",refAccountInfo);
							//add log
							AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(userLoginRecord.getRefUserId());
							if(accountInfo!=null){
							AfUserAccountLogDo accountLog = buildUserAccount(accountInfo.getRebateAmount(), userLoginRecord.getRefUserId(), afOrder.getRid(), AccountLogType.REBATE);
							if(accountLog!=null){
							    afUserAccountLogDao.addUserAccountLog(accountLog);
								}
							}
						}
					}
				}
			}
		}
		//在活动中的几个场景？
		boluomeActivitySendCoupon(afOrder,afBoluomeActivityDo);
		return 0;
	}
	
	/**
	 * 菠萝觅活动送券
	 * @param afOrder
	 * @return
	 */
	public int boluomeActivitySendCoupon(final AfOrderDo afOrder,AfBoluomeActivityDo afBoluomeActivityDo){
	        logger.info("boluomeActivitySendCoupon begin , afOrder = {}", afOrder);
		//在订单创建之前绑定的最后一个用户
		AfBoluomeActivityUserLoginDo queryUserLoginRecord  = new AfBoluomeActivityUserLoginDo();
		queryUserLoginRecord.setUserId(afOrder.getUserId());
		queryUserLoginRecord.setGmtCreate(afOrder.getGmtCreate());
		
		AfBoluomeActivityUserLoginDo userLoginRecord = afBoluomeActivityUserLoginDao.getUserLoginRecord(queryUserLoginRecord);
		logger.info("userLoginRecord = {}", userLoginRecord);
		//若是被邀请而产生消费行为
		if(userLoginRecord!=null){
		  //消费者只完成第一笔(新用户)
		 AfOrderDo queryCount = new AfOrderDo();
		 queryCount.setUserId(userLoginRecord.getUserId());
		 queryCount.setOrderStatus("FINISHED");
		 int  orderCount =  orderDao.getOrderCountByStatusAndUserId(queryCount);
	         logger.info("orderCount = {}", orderCount);
		//<=1?
		  if(orderCount<=1){
		    //将这条记录设置标记
		    if(!"N".equals(userLoginRecord.getBindingFlag()) && !"Y".equals(userLoginRecord.getBindingFlag())){
		    AfBoluomeActivityUserLoginDo setBidingFlag = new AfBoluomeActivityUserLoginDo();
		    setBidingFlag.setBindingFlag("Y");
		    setBidingFlag.setRid(userLoginRecord.getRid());
		    int updateRusult =  afBoluomeActivityUserLoginDao.updateById(setBidingFlag);
		    if(updateRusult>0){
			//查询邀请者的记录，是否大于n,若大于等于n，送券，并置为N，
			BoluomeActivityRuleBo ruleBo = new BoluomeActivityRuleBo();
			//json转对象
			 String ruleJson = afBoluomeActivityDo.getActivityRule();
			 JSONArray jsStr = JSONArray.parseArray(ruleJson);
			 Object o=jsStr.get(0);
			 ruleBo = JSONObject.parseObject(o.toString(), BoluomeActivityRuleBo.class);
			 logger.info("ruleBo = {}", ruleBo);
		         int n = ruleBo.getNum();
			 long  refId = userLoginRecord.getRefUserId();
		         int sum =   afBoluomeActivityUserLoginDao.getFlagCountByRefUserId(refId);
		         logger.info("sum = {}", sum);
			 if(sum>= n){
			     sentBoluomeCoupon(userLoginRecord);
			  }
		    	}
		     }
		    }
		}
	    return 0;
	    }

	
	private int sentBoluomeCoupon(final AfBoluomeActivityUserLoginDo userLoginRecord) {
	    logger.info("sentBoluomeCoupon begin , userLoginRecord = {}", userLoginRecord);
	    long couponUserId = userLoginRecord.getRefUserId();
	    String flag = null;
	    //优惠券表查券，limit 0, 1
	    AfBoluomeActivityCouponDo  queryCoupon =new AfBoluomeActivityCouponDo();
	    queryCoupon.setScopeApplication("INVITER");
	    queryCoupon.setType("B");
	    List<AfBoluomeActivityCouponDo>    sentCoupons =     afBoluomeActivityCouponService.getListByCommonCondition(queryCoupon);
	    logger.info("sentCoupons=",sentCoupons);
	    if(sentCoupons.size()>0){
	    for(AfBoluomeActivityCouponDo sentCoupon:sentCoupons){
	    long resourceId = sentCoupon.getCouponId();
	    AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(resourceId);
	    logger.info("resourceInfo = {}", resourceInfo);
	    if(resourceInfo!=null){
	    PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
	    bo.setUser_id(couponUserId + StringUtil.EMPTY);
	  
	    String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
	    logger.info("sentBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo),
				resultString);
		JSONObject resultJson = JSONObject.parseObject(resultString);
		String code = resultJson.getString("code");
        		if ("0".equals(code)) {
        		  flag = "CHANGE";
        		    //发送短信
                	  String sendMessage = "";
    			   //设置文案
    		          String  type = "GG_LIGHT";
    			  String  secType = "GG_SMS_OLD";
    			  AfResourceDo resourceDo =   afResourceService.getConfigByTypesAndSecType(type, secType);
    					if(resourceDo!=null){
    					  sendMessage = resourceDo.getValue();
    					  AfUserDo user =   afUserDao.getUserById(userLoginRecord.getRefUserId());
    		                	    if(user!=null){
    		                	        smsUtil.sendSms(user.getMobile(),sendMessage);
    		                	    }
    					}
    				}
                	    
                	    
//                	    float money = 0;
//                	    String rString = resultJson.getString("data");
//                	    JSONArray jsStr = JSONArray.parseArray(rString);
//   			    Object o=jsStr.get(0);
//        		    BoluomeCouponResponseBo BoluomeCouponResponseBo = JSONObject.parseObject(o.toString(), BoluomeCouponResponseBo.class);
//        		    money = BoluomeCouponResponseBo.getValue();
//                	    sendMessage="恭喜您获得一次吃霸王餐的机会,"+money+"元代金券已发放您的账户";
                	  
                    
	    	}
	    }
	   }
	    logger.info("updateBidingFlag=",flag);
	    if("CHANGE".equals(flag)){
	    //送完券，解除绑定关系biding_flag
        	    AfBoluomeActivityUserLoginDo updateBidingflag = new AfBoluomeActivityUserLoginDo();
        	    updateBidingflag.setBindingFlag("N");
        	    updateBidingflag.setRefUserId(userLoginRecord.getRefUserId());
        	    int result =    afBoluomeActivityUserLoginDao.updateBindingFlagIsN(updateBidingflag);
        	    logger.info("updateBidingFlagIsN result=",result);
        	
	    }
	    return 0;
	    // TODO Auto-generated method stub
	}
	private AfUserAccountLogDo buildUserAccount(BigDecimal amount,Long userId,Long orderId, AccountLogType logType){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(orderId+"");
		accountLog.setType(logType.getCode());
		return accountLog;
	}
	public String activityOffical(Long userId){
	    String officalText = null;
	    // 如果该用户在平台没有订单，则送券
	    AfOrderDo queryCount = new AfOrderDo();
	    queryCount.setUserId(userId);
	    int orderCount = afOrderService.getOrderCountByStatusAndUserId(queryCount);
	    logger.info("orderCount = {}", orderCount);
	    // <1?
	    AfBoluomeActivityCouponDo queryCoupon = new AfBoluomeActivityCouponDo();
	    queryCoupon.setType("B");
	    if (orderCount < 1) {
		//新用户
		queryCoupon.setScopeApplication("INVITEE");
	    }else{
		//老用户
		queryCoupon.setScopeApplication("INVITER");
	    }
		
		List<AfBoluomeActivityCouponDo> sentCoupons = afBoluomeActivityCouponService.getListByCommonCondition(queryCoupon);
		logger.info("sentCoupons=", sentCoupons);
		if (sentCoupons.size() > 0) {
		    for (AfBoluomeActivityCouponDo sentCoupon : sentCoupons) {
			long resourceId = sentCoupon.getCouponId();
			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(resourceId);
			logger.info("resourceInfo = {}", resourceInfo);
			// 查询是否已有该券，有显示文案
			String status = getCouponYesNoStatus(resourceInfo, userId);
			if(status!= null){
			    if ("Y".equals(status)) {
				if (resourceInfo != null) {
				//设置文案
				    String  type = "GG_LIGHT";
				    String  secType = "";
				    if(orderCount<1){
					secType = "GG_POP_UP_NEW";
				    }else{
					secType = "GG_POP_UP_OLD";
				    }
				AfResourceDo resourceDo =     afResourceService.getConfigByTypesAndSecType(type, secType);
					if(resourceDo!=null){
					    officalText = resourceDo.getValue();
					}
				}
			    }
			}
		}
	    }
		return officalText;
	    
	}
	 private String getCouponYesNoStatus(AfResourceDo resourceInfo,  Long userId) {

		String uri = resourceInfo.getValue();
		String[] pieces = uri.split("/");
		if (pieces.length > 9) {
		    String app_id = pieces[6];
		    String campaign_id = pieces[8];
		    String user_id = "0";
		    // 获取boluome的券的内容
		    String url = getCouponUrl() + "?" + "app_id=" + app_id + "&user_id=" + user_id + "&campaign_id=" + campaign_id;
		    String reqResult = HttpUtil.doGet(url, 10);
		    if (!StringUtil.isBlank(reqResult)) {
			ThirdResponseBo thirdResponseBo = JSONObject.parseObject(reqResult, ThirdResponseBo.class);
			if (thirdResponseBo != null && "0".equals(thirdResponseBo.getCode())) {
			    List<BoluomeCouponResponseParentBo> listParent = JSONArray.parseArray(thirdResponseBo.getData(), BoluomeCouponResponseParentBo.class);
			    if (listParent != null && listParent.size() > 0) {
				BoluomeCouponResponseParentBo parentBo = listParent.get(0);
				if (parentBo != null) {
				    String activityCoupons = parentBo.getActivity_coupons();
				    String result = activityCoupons.substring(1, activityCoupons.length() - 1);
				    String replacement = "," + "\"sceneId\":" + resourceInfo.getRid() + "}";
				    String rString = result.replaceAll("}", replacement);
				    // 字符串转为json对象
				    BoluomeCouponResponseBo BoluomeCouponResponseBo = JSONObject.parseObject(rString, BoluomeCouponResponseBo.class);
				
				    List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil.getActivityCouponList(uri);
				    BrandActivityCouponResponseBo bo = activityCouponList.get(0);
				    if (userId != null) {
					// 判断用户是否拥有该优惠券 或者已经被领取完毕
					if (boluomeUtil.isUserHasCoupon(uri, userId, 1) ) {
					    // BoluomeCouponResponseBo.setIsHas(YesNoStatus.YES.getCode());
					    //
					    return YesNoStatus.YES.getCode();

					} else {
					    // BoluomeCouponResponseBo.setIsHas(YesNoStatus.NO.getCode());
					    return YesNoStatus.NO.getCode();
					}
				    }

				}
			    }
			}
		    }
		}
		return null;
	    }
	 /**
	     * 
	     * @说明：获得用户优惠券列表
	     * @param: @return
	     * @return: String
	     */
	    private static String getCouponUrl() {
		if (couponUrl == null) {
		    couponUrl = ConfigProperties.get(Constants.CONFKEY_BOLUOME_COUPON_URL);
		    return couponUrl;
		}
		return couponUrl;
	    }
}