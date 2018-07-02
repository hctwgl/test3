package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserLoginService;
import com.ald.fanbei.api.biz.service.AfBoluomeUserCouponService;
import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.H5GgActivity;
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
import com.ald.fanbei.api.dal.domain.AfBoluomeUserCouponDo;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
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
	@Resource
	AfBoluomeActivityUserLoginService afBoluomeActivityUserLoginService;
	@Resource
	AfBoluomeUserCouponService afBoluomeUserCouponService;
	@Resource 
	JpushService jpushService;
	@Resource
	AfCouponCategoryService  afCouponCategoryService;
	
        private static final Logger logger = LoggerFactory.getLogger(AfBoluomeActivityServiceImpl.class);
        private static String couponUrl = null;

		@Override
	public BaseDao<AfBoluomeActivityDo, Long> getDao() {
		return afBoluomeActivityDao;
	}

	@Override
	public int ggLightActivity(AfOrderDo afOrder) {
	// TODO Auto-generated method stub
	logger.info("ggLightActivity start",afOrder);
	//该订单id 没有该用户活动返利记录。且该订单存在。
	AfOrderDo oDo =   afOrderService.getOrderInfoById(afOrder.getRid(), afOrder.getUserId());
	logger.info("getOrderInfoById oDo",oDo);
	if(oDo != null){
	       //查询是否有对应的返利记录
	        AfBoluomeActivityUserRebateDo userRebateQuery = new  AfBoluomeActivityUserRebateDo();
		userRebateQuery.setUserId(afOrder.getUserId());
		userRebateQuery.setOrderId(afOrder.getRid());
		List<AfBoluomeActivityUserRebateDo> rebateQueryResult  = afBoluomeActivityUserRebateDao.getListByCommonCondition(userRebateQuery);
		logger.info("rebateQueryResult",rebateQueryResult);
	        if(afOrder.getRid() > 0 && rebateQueryResult.size()<1){
                	//若在菠萝觅活动期间内则返利
                	AfBoluomeActivityDo activityDo = new AfBoluomeActivityDo();
                	activityDo.setStatus("O");
                	AfBoluomeActivityDo afBoluomeActivityDo =  afBoluomeActivityDao.getByCommonCondition(activityDo);
                        logger.info("afBoluomeActivityDo = ",afBoluomeActivityDo);
                	if(afBoluomeActivityDo !=null){
                        	Date startTime = afBoluomeActivityDo.getGmtStart();
                        	Date endTime = afBoluomeActivityDo.getGmtEnd();
                	        if(DateUtil.afterDay(endTime,new Date()) && DateUtil.afterDay(new Date(),startTime)){
                	    	   boluomeActivity(afOrder,afBoluomeActivityDo);
                           }
                        }
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
			ItemsMessageSet.setBoluomeActivityId(afBoluomeActivityDo.getRid());
			ItemsMessageSet.setStatus("O");
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
				//在卡片场景中
				boluomeActivitySendCoupon(afOrder,afBoluomeActivityDo);
				
			}
		}
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
	public Map<String, String> activityOffical(Long userId){
	    
	    Map<String, String> map = new HashMap<String, String>();
	    String officalText = null;
	    List<AfBoluomeActivityCouponDo> sentOldCoupons = new ArrayList<AfBoluomeActivityCouponDo>();
	    List<AfBoluomeActivityCouponDo> sentNewCoupons = new ArrayList<AfBoluomeActivityCouponDo>();
	    AfOrderDo queryCount = new AfOrderDo();
	    queryCount.setUserId(userId);
	    int orderCount = afOrderService.getOrderCountByStatusAndUserId(queryCount);
	    logger.info("orderCount = {}", orderCount);
	    // <1?
	    AfResourceDo resourceInfo = new AfResourceDo();
	    long resourceId =0;
	    String oldCouponstatus = "";
	    String newCouponstatus = "";
	    AfBoluomeActivityCouponDo queryOldCoupon = new AfBoluomeActivityCouponDo();
	    AfBoluomeActivityCouponDo queryNewCoupon = new AfBoluomeActivityCouponDo();
	    queryOldCoupon.setType("B");
	    queryNewCoupon.setType("B");
	    queryOldCoupon.setScopeApplication("INVITER");
	    queryNewCoupon.setScopeApplication("INVITEE");
	    sentOldCoupons = afBoluomeActivityCouponService.getListByCommonCondition(queryOldCoupon);
            sentNewCoupons = afBoluomeActivityCouponService.getListByCommonCondition(queryNewCoupon);
            
            if (sentOldCoupons.size() > 0) {
		    for (AfBoluomeActivityCouponDo sentOldCoupon : sentOldCoupons) {
		        resourceId = sentOldCoupon.getCouponId();
		        resourceInfo = afResourceService.getResourceByResourceId(resourceId);
			logger.info("resourceInfo = {}", resourceInfo);
			// 查询是否已有该券，有显示文案
		         oldCouponstatus = getCouponYesNoStatus(resourceInfo, userId);
		    }
	    }
            
	   if (sentNewCoupons.size() > 0) {
		    for (AfBoluomeActivityCouponDo sentNewCoupon : sentNewCoupons) {
			resourceId = sentNewCoupon.getCouponId();
		        resourceInfo = afResourceService.getResourceByResourceId(resourceId);
			logger.info("resourceInfo = {}", resourceInfo);
			// 查询是否已有该券，有显示文案
			newCouponstatus = getCouponYesNoStatus(resourceInfo, userId);
		    }
	    }
		    
	   if(orderCount<1){
			//新用户，先看有没有霸王餐券，有，则优先显示，没有，则显示新人立减券
	               if("Y".equals(oldCouponstatus)){
	        	   if (resourceInfo != null) {
				//设置文案
				    String  type = "GG_LIGHT";
				    String  secType = "GG_POP_UP_OLD";
				    AfResourceDo resourceDo =     afResourceService.getConfigByTypesAndSecType(type, secType);
					if(resourceDo!=null){
					    officalText = resourceDo.getValue();
					    map.put("officalText", officalText);
					    map.put("despotCoupon", "Y");
					}
				}
		       } else if("N".equals(oldCouponstatus)){
	        	   if("Y".equals(newCouponstatus)){
	        	       if (resourceInfo != null) {
					//设置文案
					    String  type = "GG_LIGHT";
					    String  secType = "GG_POP_UP_NEW";
					    AfResourceDo resourceDo =     afResourceService.getConfigByTypesAndSecType(type, secType);
						if(resourceDo!=null){
						    officalText = resourceDo.getValue();
						    map.put("officalText", officalText);
						//  map.put("despotCoupon", "N");
					     }
	        	       		}
	        	   	}
	        	   }
	               
	   	}
	   	if(orderCount>0){
	   	    if("Y".equals(oldCouponstatus)){
	        	   if (resourceInfo != null) {
				//设置文案
				    String  type = "GG_LIGHT";
				    String  secType = "GG_POP_UP_OLD";
				    AfResourceDo resourceDo =     afResourceService.getConfigByTypesAndSecType(type, secType);
					if(resourceDo!=null){
					    officalText = resourceDo.getValue();
					    map.put("officalText", officalText);
					    map.put("despotCoupon", "Y");
					}
				}
	   	       }
	   	    
	   	}
            
		return map;
		
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

	@Override
	public int sentNewUserBoluomeCoupon(AfUserDo afUserDo) {
	    // TODO Auto-generated method stub
	     //平台没有订单且有绑定记录时送券
	    AfOrderDo queryCount = new AfOrderDo();
	    queryCount.setUserId(afUserDo.getRid());
	    int orderCount = afOrderService.getOrderCountByStatusAndUserId(queryCount);
	    logger.info("orderCount = {}", orderCount);
	    // <1?
	    if (orderCount < 1) {
		AfBoluomeActivityUserLoginDo afBoluomeActivityUserLoginDo = new AfBoluomeActivityUserLoginDo();
	        afBoluomeActivityUserLoginDo.setUserId(afUserDo.getRid());
		List<AfBoluomeActivityUserLoginDo>  userLogin =  afBoluomeActivityUserLoginService.getListByCommonCondition(afBoluomeActivityUserLoginDo) ;
		//一条绑定记录
		if(userLogin.size()>0){
    		AfBoluomeActivityCouponDo queryCoupon = new AfBoluomeActivityCouponDo();
    		queryCoupon.setScopeApplication("INVITEE");
    		queryCoupon.setType("B");
    		List<AfBoluomeActivityCouponDo> sentCoupons = afBoluomeActivityCouponService.getListByCommonCondition(queryCoupon);
		logger.info("sentCoupons=", sentCoupons);
		if (sentCoupons.size() > 0) {
		    for (AfBoluomeActivityCouponDo sentCoupon : sentCoupons) {
			long resourceId = sentCoupon.getCouponId();
			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(resourceId);
			logger.info("resourceInfo = {}", resourceInfo);
			// 查询是否已有该券，有，则不发
			String status = getCouponYesNoStatus(resourceInfo, afUserDo);
			if ("N".equals(status)) {
			    if (resourceInfo != null) {
				PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
				bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);
				String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
				logger.info("sentBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
				JSONObject resultJson = JSONObject.parseObject(resultString);
				String code = resultJson.getString("code");
		        	 if ("0".equals(code)) {
				  //发送短信
	                	  String sendMessage = "";
	    			   //设置文案
	    		          String  type = "GG_LIGHT";
	    			  String  secType = "GG_SMS_NEW";
	    			  AfResourceDo resourceDo =   afResourceService.getConfigByTypesAndSecType(type, secType);
	    					if(resourceDo!=null){
	    					  sendMessage = resourceDo.getValue();
	    		                	  smsUtil.sendSms(afUserDo.getMobile(),sendMessage);
	    		                	  logger.info("sentBoluomeCoupon sendSms:", afUserDo.getMobile(),sendMessage);
	    			     }
	    					  return 0;
			       }
			    }
			 }
		    }
		}
	    }
	  }
	    
	    return -1;
	}
	
	
//	@Override
//	public int sentNewUserBoluomeCouponForDineDash(AfUserDo afUserDo) {
//	    //没有券且有注册时绑定的记录。
//	    //1.是否有绑定关系
//	    logger.info("sentNewUserBoluomeCouponForDineDash start afUserDo = {}", afUserDo);
//	    AfBoluomeActivityUserLoginDo userLoginDo = new AfBoluomeActivityUserLoginDo();
//	    userLoginDo.setUserId(afUserDo.getRid());
//	    List<AfBoluomeActivityUserLoginDo>  userLoginList=  afBoluomeActivityUserLoginService.getListByCommonCondition(userLoginDo);
//	    //有绑定记录
//	    if(userLoginList.size()>0){
//	    
//		  String  type = H5GgActivity.GGACTIVITY.getCode();
//		  String  secType =  H5GgActivity.BOLUOMECOUPON.getCode();
//		  AfResourceDo resourceDo =   afResourceService.getConfigByTypesAndSecType(type, secType);
//		  logger.info("sentNewUserBoluomeCouponForDineDash resourceDo = {}", resourceDo);
//		     if(resourceDo!= null){
//			long  boluomeCouponId = Long.parseLong(resourceDo.getValue()) ;
//			//2.记录表查询是否有券
//			AfBoluomeUserCouponDo userCouponDo = new AfBoluomeUserCouponDo();
//			userCouponDo.setChannel(H5GgActivity.REGISTER.getCode());
//			userCouponDo.setUserId(afUserDo.getRid());
//			userCouponDo.setCouponId(boluomeCouponId);
//			AfBoluomeUserCouponDo  userCoupon =  afBoluomeUserCouponService.getByCouponIdAndUserIdAndChannel(userCouponDo);
//			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(boluomeCouponId);
//			logger.info("sentNewUserBoluomeCouponForDineDash resourceInfo = {}", resourceInfo);
//			//无券则发券，并推送极光
//			if(userCoupon == null){
//			    if (resourceInfo != null) {
//				PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
//				bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);
//				String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
//				logger.info("sentBoluomeCoupon boluome bo = {}, resultString = {}", JSONObject.toJSONString(bo), resultString);
//				JSONObject resultJson = JSONObject.parseObject(resultString);
//				String code = resultJson.getString("code");
//		        	//发券成功，推送极光 
//				if ("0".equals(code)) {
//				    //保存记录
//				    AfBoluomeUserCouponDo boluomeUserCoupon = new AfBoluomeUserCouponDo();
//				    boluomeUserCoupon.setChannel(H5GgActivity.REGISTER.getCode());
//				    boluomeUserCoupon.setCouponId(resourceInfo.getRid());
//				    boluomeUserCoupon.setStatus(1);
//				    boluomeUserCoupon.setUserId(afUserDo.getRid());
//				    afBoluomeUserCouponService.saveRecord(boluomeUserCoupon);
//				    //推送极光
//				    jpushService.boluomeActivityMsg(afUserDo.getUserName(), H5GgActivity.GGACTIVITY.getCode(), H5GgActivity.GGSMSNEW.getCode());
//			    }
//			}
//		   }
//           }	    
//	  	    
//    }
//	    return 0;					    
//}
	@Override
	public int sentNewUserBoluomeCouponForDineDash(AfUserDo afUserDo) {
	    AfResourceDo activitySwitch =   afResourceService.getConfigByTypesAndSecType("GG_ACTIVITY","ACTIVITY_SWITCH");
	    if(activitySwitch != null){
		 String aSwitch = "";
		 String ctype = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		//线上为开启状态
		 if (Constants.INVELOMENT_TYPE_ONLINE.equals(ctype) || Constants.INVELOMENT_TYPE_TEST.equals(ctype)) {
		     aSwitch = activitySwitch.getValue();
		 } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(ctype) ){
		     aSwitch = activitySwitch.getValue1();
		 }
		if("O".equals(aSwitch)){
	         //活动期内该用户没有订单
		    logger.info("sentNewUserBoluomeCouponForDineDash start afUserDo = {}"+ JSONObject.toJSONString(afUserDo));
        	    AfResourceDo resource =   afResourceService.getConfigByTypesAndSecType(H5GgActivity.GG_ACTIVITY.getCode(),H5GgActivity.ACTIVITY_TIME.getCode() );
                	    if(resource != null){
                		SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm:ss");  
                		Date startTime = null;
                		Date endTime = null;
                		Date   curDate   =   new   Date(System.currentTimeMillis());//获取当前时间      
                		try {
                		    startTime = formatter.parse(resource.getValue());
                		    endTime   = formatter.parse(resource.getValue1());
                		} catch (ParseException e) {
                		    // TODO Auto-generated catch block
                		    e.printStackTrace();
                		}
                		 logger.info("sentNewUserBoluomeCouponForDineDash startTime = {}"+startTime+"endTime = {}"+endTime);
                	         if(DateUtil.afterDay(endTime,curDate) && DateUtil.afterDay(curDate,startTime)){
                		// 该用户是否有订单，没有，则送券
                	       
                	              AfOrderDo order = new AfOrderDo();
                		      order.setUserId(afUserDo.getRid());
                		      int queryCount = afOrderService.getOrderCountByStatusAndUserId(order);
                		      logger.info("sentNewUserBoluomeCouponForDineDash order queryCount = {}"+ queryCount+" afUserDo = {}"+JSONObject.toJSONString(afUserDo));
                		      if (queryCount <= 0) {
                			   try{
                			       int result =  sentBoluomeCouponGroup(afUserDo);
                			       logger.info("sentBoluomeCouponGroup result:"+result);
                			   }catch(Exception e){
                			       logger.error("sentBoluomeCouponGroup error:"+e);
                			   }
                			  
                        		  String  type = H5GgActivity.GG_ACTIVITY.getCode();
                        		  String  secType =  H5GgActivity.BOLUOME_COUPON.getCode();
                        		  AfResourceDo resourceDo =   afResourceService.getConfigByTypesAndSecType(type, secType);
                        		  logger.info("sentNewUserBoluomeCouponForDineDash resourceDo = {}"+ JSONObject.toJSONString(resourceDo)+"afUserDo = {}"+JSONObject.toJSONString(afUserDo));
                        		     if(resourceDo!= null){
                        			long  boluomeCouponId = 0L;
//                        			for(int i=0;i<3;i++){
//                                			 if(i==0){
                                			     boluomeCouponId = Long.parseLong(resourceDo.getValue()) ;
//                                			 }else if(i==1){
//                                			     boluomeCouponId = Long.parseLong(resourceDo.getValue2()) ;
//                                			 }else if(i==2){
//                                			     boluomeCouponId = Long.parseLong(resourceDo.getValue3()) ;
//                                			 }
                        			    
                                    			//2.记录表查询是否有券
                                    			AfBoluomeUserCouponDo userCouponDo = new AfBoluomeUserCouponDo();
                                    			userCouponDo.setChannel(H5GgActivity.REGISTER.getCode());
                                    			userCouponDo.setUserId(afUserDo.getRid());
                                    			userCouponDo.setCouponId(boluomeCouponId);
                                    			int  userCoupon =  afBoluomeUserCouponService.getByCouponIdAndUserIdAndChannel(userCouponDo);
                                    			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(boluomeCouponId);
                                    			logger.info("sentNewUserBoluomeCouponForDineDash resourceInfo = {}"+JSONObject.toJSONString(resourceInfo)+" afUserDo = {}"+JSONObject.toJSONString(afUserDo));
                                    			//无券则发券，并推送极光
                                    			if(userCoupon < 1){
                                    			    if (resourceInfo != null) {
                                    				PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
                                    				bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);
                                    				String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
                                    				logger.info("sentNewUserBoluomeCouponForDineDash afUserDo = {}"+JSONObject.toJSONString(afUserDo)+"resultString = {}"+ resultString);
                                    				JSONObject resultJson = JSONObject.parseObject(resultString);
                                    				String code = resultJson.getString("code");
                                    		        	//发券成功，推送极光 
                                    				if ("0".equals(code)) {
                                    				    //保存记录
                                    				    AfBoluomeUserCouponDo boluomeUserCoupon = new AfBoluomeUserCouponDo();
                                    				    boluomeUserCoupon.setChannel(H5GgActivity.REGISTER.getCode());
                                    				    boluomeUserCoupon.setCouponId(resourceInfo.getRid());
                                    				    boluomeUserCoupon.setStatus(1);
                                    				    boluomeUserCoupon.setUserId(afUserDo.getRid());
                                    				    afBoluomeUserCouponService.saveRecord(boluomeUserCoupon);
                                    				    //推送极光
                                        				    logger.info("sentNewUserBoluomeCouponForDineDash boluomeUserCoupon saveRecord = "+ JSONObject.toJSONString(boluomeUserCoupon));
                                        				  //  if(i==0){
                                        				      jpushService.boluomeActivityMsg(afUserDo.getUserName(), H5GgActivity.GG_ACTIVITY.getCode(), H5GgActivity.GG_SMS_NEW.getCode());
                                        				//  }
                                    				}
                                    			    }
                                    			}
                        			}
                        		     }
                		      }
                	         }
//                	    }    
			}
	    	}
	    return 0;					    
}
	
	
	 private int sentBoluomeCouponGroup(AfUserDo afUserDo) {
	           //如果用户存在该类型券，则不赠送
	                AfBoluomeUserCouponDo userCouponDo = new AfBoluomeUserCouponDo();
			userCouponDo.setChannel(H5GgActivity.REGISTER_GP.getCode());
			userCouponDo.setUserId(afUserDo.getRid());
			userCouponDo.setCouponId(null);
			int  userCouponN =  afBoluomeUserCouponService.getByCouponIdAndUserIdAndChannel(userCouponDo);
			if(userCouponN > 1){
			    logger.info("sentBoluomeCouponGroup userCouponN = "+ userCouponN+"afUserDo = "+JSONObject.toJSONString(afUserDo));
			    return 0;
			}
	     
		    int result = 0;
		    //获取优惠券组
		    String tag = "_NEW_USER_BOLUOME_COUPON_";
		    AfCouponCategoryDo  couponCategory  = afCouponCategoryService.getCouponCategoryByTag(tag);
			if(couponCategory != null){
			    	String coupons = couponCategory.getCoupons();
				JSONArray couponsArray = (JSONArray) JSONArray.parse(coupons);
				for (int i = 0; i < couponsArray.size(); i++) {
					String couponId = (String) couponsArray.getString(i);
					AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(Long.parseLong(couponId));
	            			logger.info("sentBoluomeCouponGroup resourceInfo = {},afUserDo = {}",  JSONObject.toJSONString(resourceInfo),JSONObject.toJSONString(afUserDo));
	            			if(resourceInfo !=null){
	            			AfBoluomeUserCouponDo userCoupon = new AfBoluomeUserCouponDo();
	            			userCoupon.setChannel(H5GgActivity.REGISTER_GP.getCode());
	            			userCoupon.setUserId(afUserDo.getRid());
	            			userCoupon.setCouponId(Long.parseLong(couponId));
	            			int  userCouponNum =  afBoluomeUserCouponService.getByCouponIdAndUserIdAndChannel(userCoupon);
//	            			AfResourceDo resourceInfo = afResourceService.getResourceByResourceId(boluomeCouponId);
//	            			logger.info("sentNewUserBoluomeCouponForDineDash resourceInfo = {},afUserDo = {}",  JSONObject.toJSONString(resourceInfo),JSONObject.toJSONString(afUserDo));
	            			//无券则发券，并推送极光
	            			if(userCouponNum <1){
	            			   
	            				PickBrandCouponRequestBo bo = new PickBrandCouponRequestBo();
	            				bo.setUser_id(afUserDo.getRid() + StringUtil.EMPTY);
	            				String resultString = HttpUtil.doHttpPostJsonParam(resourceInfo.getValue(), JSONObject.toJSONString(bo));
	            				logger.info("sentBoluomeCouponGroup afUserDo = {}"+ JSONObject.toJSONString(afUserDo)+" resultString = {}"+ resultString);
	            				JSONObject resultJson = JSONObject.parseObject(resultString);
	            				String code = resultJson.getString("code");
	            		        	//发券成功，推送极光 
	            				if ("0".equals(code)) {
	            				    //保存记录
	            				    AfBoluomeUserCouponDo boluomeUserCoupon = new AfBoluomeUserCouponDo();
	            				    boluomeUserCoupon.setChannel(H5GgActivity.REGISTER_GP.getCode());
	            				    boluomeUserCoupon.setCouponId(resourceInfo.getRid());
	            				    boluomeUserCoupon.setStatus(1);
	            				    boluomeUserCoupon.setUserId(afUserDo.getRid());
	            				    afBoluomeUserCouponService.saveRecord(boluomeUserCoupon);
	            				}
	            			 }
	            		}
			}
		}	    			    
	       
		    return result;
		    // TODO Auto-generated method stub
		    
		}
	
	
	 private String getCouponYesNoStatus(AfResourceDo resourceInfo, AfUserDo UserDo) {

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
				    Long userId = UserDo.getRid();
				    List<BrandActivityCouponResponseBo> activityCouponList = boluomeUtil.getActivityCouponList(uri);
				    BrandActivityCouponResponseBo bo = activityCouponList.get(0);
				    if (userId != null) {
					// 判断用户是否拥有该优惠券 或者已经被领取完毕
					if (boluomeUtil.isUserHasCoupon(uri, userId, 1) || bo.getDistributed() >= bo.getTotal()) {
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
}