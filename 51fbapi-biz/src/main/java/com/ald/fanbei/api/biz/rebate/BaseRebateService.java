package com.ald.fanbei.api.biz.rebate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;

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

        AfUserAccountLogDo existItem= afUserAccountLogDao.getByRefAndType(orderInfo.getRid(),UserAccountLogType.REBATE_CASH.getCode());
        if(existItem!=null){
           return false;
        }
        AfUserAccountLogDo existDoubleRebate = afUserAccountLogDao.getByRefAndType(orderInfo.getRid(),UserAccountLogType.DOUBLE_REBATE_CASH.getCode());
        if(existDoubleRebate!=null){
           return false;
        }
        
        AfUserAccountDo accountInfo = new AfUserAccountDo();
        accountInfo.setUserId(orderInfo.getUserId());
        
        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setRefId(orderInfo.getRid() + StringUtils.EMPTY);
        accountLog.setUserId(orderInfo.getUserId());
        
        List<AfOrderDo> shopOrderList =   afOrderDao.getSelfsupportOrderByUserIdOrActivityTime(orderInfo.getUserId(),null);
	
        //订单首次完成，邀请有礼记录用户订单id
        if(shopOrderList.size() == 1 && OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())){
	    AfRecommendUserDo  afRecommendUserDo  = afRecommendUserService.getARecommendUserById(orderInfo.getUserId());
	     if(afRecommendUserDo != null){
		 if(afRecommendUserDo.getFirstBoluomeOrder() == null){
		     afRecommendUserDo.setFirstBoluomeOrder(orderInfo.getRid());
		     int updateRecommend = afRecommendUserService.updateRecommendUserById(afRecommendUserDo);
		 }
	     }
        }
        //自营商城活动第三单双倍返利
        if(shopOrderList.size() == 3 && OrderType.SELFSUPPORT.getCode().equals(orderInfo.getOrderType())){
                //用户账户操作
                accountInfo.setRebateAmount(rebateAmount.multiply(new BigDecimal(2)));
                accountLog.setType(UserAccountLogType.DOUBLE_REBATE_CASH.getCode());
                accountLog.setAmount(orderInfo.getRebateAmount().multiply(new BigDecimal(2)));
                //插入账户日志
                afUserAccountLogDao.addUserAccountLog(accountLog);
                //修改账户表
                afUserAccountDao.updateRebateAmount(accountInfo);
        
                AfUserAccountDo userInfo =afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId()) ;
               //返利已经到账通知
                smsUtil.sendRebate(userInfo.getUserName(), new Date(),orderInfo.getRebateAmount().multiply(new BigDecimal(2)));
                return true;  
        }
	      //用户账户操作
	       accountInfo.setRebateAmount(rebateAmount);
	       accountLog.setType(UserAccountLogType.REBATE_CASH.getCode());
	       accountLog.setAmount(orderInfo.getRebateAmount());
	        //插入账户日志
	       afUserAccountLogDao.addUserAccountLog(accountLog);
	        //修改账户表
	       afUserAccountDao.updateRebateAmount(accountInfo);
	            
	       AfUserAccountDo userInfo =afUserAccountDao.getUserAccountInfoByUserId( orderInfo.getUserId()) ;
	       //返利已经到账通知
	       smsUtil.sendRebate(userInfo.getUserName(), new Date(),orderInfo.getRebateAmount());
	     
	 
        return true;
    }
}
