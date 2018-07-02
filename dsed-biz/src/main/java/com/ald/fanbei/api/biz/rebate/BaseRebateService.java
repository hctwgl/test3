package com.ald.fanbei.api.biz.rebate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.AfTaskUserService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.alibaba.fastjson.JSONObject;

public abstract class BaseRebateService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfOrderDao afOrderDao;
    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfTaskUserService afTaskUserService;
    /**
     * 是否可以进行返利的前置数据验证
     *
     * @return
     */
    protected boolean valid(AfOrderDo afOrderDo) {
        if (afOrderDo == null) {
            logger.error("404,can not found this order!");
            return false;
        }
        //只有那几种状态能进行返利,基础验证
        if (
                afOrderDo.getStatus().equals(OrderStatus.CLOSED.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.AGENCYCOMPLETED.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.DEAL_REFUNDING.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.DEALING.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.NEW.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.PAYFAIL.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.REBATED.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.REVIEW.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.WAITING_REFUND.getCode()) ||
                        afOrderDo.getStatus().equals(OrderStatus.NEW.getCode())) {
            logger.error("invalid order state:"+ afOrderDo.getStatus());
            return false;
        }
        return true;
    }

    /**
     * 执行返利操作
     *
     * @return
     */
    protected abstract boolean rebate(AfOrderDo afOrderDo);

    /**
     * 取消返利 执行返利逆向操作
     *
     * @return
     */
    protected abstract boolean reverse(AfOrderDo afOrderDo);

    /**
     * 取消返利 执行返利逆向操作
     *
     * @return
     */
    protected  boolean addRebateAmount(BigDecimal rebateAmount,AfOrderDo orderInfo){

        AfUserAccountLogDo existItem= afUserAccountLogDao.getByRefAndType(orderInfo.getRid().toString(),UserAccountLogType.REBATE_CASH.getCode());
        String logs = String.format("selfsupport order existItem = %s",JSONObject.toJSONString(existItem));
       	logger.info(logs);
        if(existItem!=null){
           return false;
        }
        AfUserAccountLogDo existDoubleRebate = afUserAccountLogDao.getByRefAndType(orderInfo.getRid().toString(),UserAccountLogType.DOUBLE_REBATE_CASH.getCode());
        logs =  logs + String.format("selfsupport order existDoubleRebate = %s",JSONObject.toJSONString(existDoubleRebate));
       	logger.info(logs);
        if(existDoubleRebate!=null){
           return false;
        }
        
        AfUserAccountDo accountInfo = new AfUserAccountDo();
        accountInfo.setUserId(orderInfo.getUserId());
        
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setRefId(orderInfo.getRid() + StringUtils.EMPTY);
        accountLog.setUserId(orderInfo.getUserId());
        String log = String.format("selfsupport order rebate start = %s",JSONObject.toJSONString(accountLog));
       	logger.info(log);
        
        try{
            
            	    AfResourceDo newbileTaskKTime = new AfResourceDo();
            	    String activityTime = "";
            	    newbileTaskKTime = afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "ONLINE_TIME");
            	    if(newbileTaskKTime !=null){
            		activityTime = newbileTaskKTime.getValue();
            	    }
                    List<AfOrderDo> shopOrderList =   afOrderDao.getSelfsupportOrderByUserIdOrActivityTime(orderInfo.getUserId(),null);
                    List<AfOrderDo> shopOrderListForTime =   afOrderDao.getSelfsupportOrderByUserIdOrActivityTime(orderInfo.getUserId(),activityTime);
                    log = log +  String.format("selfsupport first order rebate shopOrderList start = %s",JSONObject.toJSONString(shopOrderList),"and shopOrderList.size()=",shopOrderList.size());
                    //订单首次完成，邀请有礼记录用户订单id
                    if(shopOrderList.size() <= 1 && OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())){
               
            	    AfRecommendUserDo  afRecommendUserDo  = afRecommendUserService.getARecommendUserById(orderInfo.getUserId());
            	     if(afRecommendUserDo != null){
            		 if(afRecommendUserDo.getFirstSelfsupportOrder() == null){
            		     afRecommendUserDo.setFirstSelfsupportOrder(orderInfo.getRid());
            		     int updateRecommend = afRecommendUserService.updateRecommendUserById(afRecommendUserDo);
            		      log = log +  String.format("selfsupport first order rebate orderInfo = %s",JSONObject.toJSONString(orderInfo));
            		      logger.info(log);
            		      log =log + String.format("updateRecommend result =  %s", updateRecommend);
            		      logger.info(log);
            	             
            		
            	     }
                    }
                    }
                    log =  log + String.format("selfsupport double rebate statr = %s",JSONObject.toJSONString(shopOrderListForTime),"and shopOrderListForTime.size()=",shopOrderListForTime.size());
                    logger.info(log);
                    //自营商城活动第三单双倍返利
                    int num =0;
                    num = shopOrderListForTime.size();
                    if(1<num && num < 4){
                	for(int i = 0;i<num;i++){
                	    if(shopOrderListForTime.get(i).getRid().longValue() == orderInfo.getRid().longValue()){
                		num = num - 1;
                	    }
                	}
                    }
                    
                    if(num == 2 && OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())){
                	log =  log + String.format("selfsupport double rebate orderInfo = %s",JSONObject.toJSONString(orderInfo));
            	     logger.info(log);
            	       BigDecimal max = new BigDecimal(30.00);
            	       BigDecimal doubleAmount = rebateAmount;
            	       BigDecimal doubleAmount1 = orderInfo.getRebateAmount();
            	       //读取配置
            	     AfResourceDo afresourceDo =   afResourceService.getConfigByTypesAndSecType("RECOMMEND_MEWBIE_TASK", "DOUBLE_REBATE_LIMIT");
            	     if(afresourceDo != null){
            		 max = new BigDecimal(afresourceDo.getValue());
            	     }
            	      if( rebateAmount.compareTo(max) == 1){
            		  doubleAmount =  rebateAmount.add(max);
            		  doubleAmount1 =  orderInfo.getRebateAmount().add(max);
            	      } else {
            		  doubleAmount = rebateAmount.multiply(new BigDecimal(2));
            		  doubleAmount1 = orderInfo.getRebateAmount().multiply(new BigDecimal(2));
            	      }
            	     
                           //用户账户操作
                            accountInfo.setRebateAmount(doubleAmount);
                            accountLog.setType(UserAccountLogType.DOUBLE_REBATE_CASH.getCode());
                            accountLog.setAmount(doubleAmount1);
                            //插入账户日志
                            afUserAccountLogDao.addUserAccountLog(accountLog);
                            //修改账户表
                            afUserAccountDao.updateRebateAmount(accountInfo);

                            // add by luoxiao for 边逛边赚，增加零钱明细
                            afTaskUserService.addTaskUser(orderInfo.getUserId(), UserAccountLogType.DOUBLE_REBATE_CASH.getName(), doubleAmount);
                            // end by luoxiao
                    
                            AfUserAccountDo userInfo =afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId()) ;
                           //返利已经到账通知
                            smsUtil.sendRebate(userInfo.getUserName(), new Date(),doubleAmount1);
                            return true;  
                  }
             }catch(Exception e){
        	logger.error("selfSupport rebate error"+ e);
             }
	      //用户账户操作
	       accountInfo.setRebateAmount(rebateAmount);
	       accountLog.setType(UserAccountLogType.REBATE_CASH.getCode());
	       accountLog.setAmount(orderInfo.getRebateAmount());
	        //插入账户日志
	       afUserAccountLogDao.addUserAccountLog(accountLog);
	        //修改账户表
	       afUserAccountDao.updateRebateAmount(accountInfo);

            // add by luoxiao for 边逛边赚，增加零钱明细
            afTaskUserService.addTaskUser(orderInfo.getUserId(), UserAccountLogType.REBATE_CASH.getName(), orderInfo.getRebateAmount());
            // end by luoxiao
	            
	       AfUserAccountDo userInfo =afUserAccountDao.getUserAccountInfoByUserId( orderInfo.getUserId()) ;
	       //返利已经到账通知
	       smsUtil.sendRebate(userInfo.getUserName(), new Date(),orderInfo.getRebateAmount());
	       log =log + String.format("selfsupport order rebate afUserAccountLogDao = %s", accountLog);
	 
        return true;
    }
}
