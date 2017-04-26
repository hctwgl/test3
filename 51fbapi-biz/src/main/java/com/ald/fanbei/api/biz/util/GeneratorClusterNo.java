package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderRefundService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 *@类描述：生成集群部署对应的编号，如订单编号中自增部分
 * @author hexin 2017年2月16日下午16:49:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("generatorClusterNo")
public class GeneratorClusterNo {
	
	@Resource
	TokenCacheUtil TokenCacheUtil;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfBorrowService afBorrowService;
	@Resource
	AfRepaymentService afRepaymentService;
	@Resource
	AfOrderRefundService afOrderRefundService;
	
	//获取订单号
	public String getOrderNo(OrderType orderType){//订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.DEFAULT_PATTERN).substring(2);
		StringBuffer orderSb = new StringBuffer(orderType.getShortName());
		orderSb.append(dateStr).append(getOrderSeqStr(this.getOrderSequenceNum(currDate,orderType)));
		return orderSb.toString();
	}
	
    private int getOrderSequenceNum(Date currentDate,OrderType orderType) {//加锁，防止并发
        Integer channelNum = 1;
        String lockKey = Constants.CACHEKEY_ORDERNO_LOCK;
        String cacheKey = Constants.CACHEKEY_ORDERNO;
        
        try{
            if(TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")))){//获得同步锁
                channelNum = (Integer)TokenCacheUtil.getObject(cacheKey);
                if(channelNum == null){//缓存中无数据,从库中获取
                	String orderNo = afOrderService.getCurrentLastOrderNo(currentDate, orderType.getCode());
                    channelNum = orderNo == null?1:(getOrderSeqInt(orderNo.substring(8, 13))+1);
                }else{
                    channelNum = channelNum + 1;
                }
            }else{//获取锁失败，从库中取订单号
            	String orderNo = afOrderService.getCurrentLastOrderNo(currentDate, orderType.getCode());
                if(orderNo != null){
                    channelNum = getOrderSeqInt(orderNo.substring(8, 13))+1;
                }
                return channelNum;
            }
            TokenCacheUtil.saveObject(cacheKey, channelNum, 0l);
        }finally{
        	TokenCacheUtil.delCache(lockKey);
        }
        return channelNum;
    }
    
    private String getOrderSeqStr(int orderIntVal){
    	return String.format("%05d", orderIntVal); 
    }
    
    private int getOrderSeqInt(String orderStrVal){
    	orderStrVal = orderStrVal == null?"":orderStrVal;
    	String newStr = orderStrVal.replaceFirst("^0*", "");  
    	if(StringUtil.isBlank(newStr)){
    		return 1;
    	}
    	return Integer.parseInt(newStr);
    }
    
    //获取支付号
  	public String getOrderPayNo(Date currDate){//支付号规则：14位日期_5位订单序号
  		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
  		StringBuffer orderSb = new StringBuffer("fk");
  		orderSb.append(dateStr).append(getOrderSeqStr(this.getOrderPaySequenceNum(currDate)));
  		return orderSb.toString();
  	}
  	
  	private int getOrderPaySequenceNum(Date currentDate) {//加锁，防止并发
        Integer channelNum = 1;
        String lockKey = Constants.CACHEKEY_ORDER_PAY_NO_LOCK;
        String cacheKey = Constants.CACHEKEY_ORDER_PAY_NO;
        
        try{
            if(TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")))){//获得同步锁
                channelNum = (Integer)TokenCacheUtil.getObject(cacheKey);
                if(channelNum == null){//缓存中无数据,从库中获取
                	String payNo= afOrderService.getCurrentLastPayNo(currentDate);
                    channelNum = payNo == null?1:(getOrderSeqInt(payNo.substring(16, 20))+1);
                }else{
                    channelNum = channelNum + 1;
                }
            }else{//获取锁失败，从库中取订单号
            	String payNo = afOrderService.getCurrentLastPayNo(currentDate);
                if(payNo != null){
                    channelNum = getOrderSeqInt(payNo.substring(16, 21))+1;
                }
                return channelNum;
            }
            TokenCacheUtil.saveObject(cacheKey, channelNum, 0l);
        }finally{
        }
        TokenCacheUtil.delCache(lockKey);
        return channelNum;
    }
  	
  	//获取退款编号
  	public String getRefundNo(Date currDate){//退款编号规则：6位日期_2位订单类型_5位退款序号
  		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
  		StringBuffer orderRefundSb = new StringBuffer("tk");
  		orderRefundSb.append(dateStr).append(getOrderSeqStr(this.getRefundSequenceNum(currDate)));
  		return orderRefundSb.toString();
  	}
  	
  	private int getRefundSequenceNum(Date currentDate) {//加锁，防止并发
        Integer channelNum = 1;
        String lockKey = Constants.CACHEKEY_REFUND_NO_LOCK;
        String cacheKey = Constants.CACHEKEY_REFUND_NO;
        
        try{
            if(TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")))){//获得同步锁
                channelNum = (Integer)TokenCacheUtil.getObject(cacheKey);
                if(channelNum == null){//缓存中无数据,从库中获取
                	String refundNo = afOrderRefundService.getCurrentLastRefundNo(currentDate);
                    channelNum = refundNo == null?1:(getOrderSeqInt(refundNo.substring(16, 20))+1);
                }else{
                    channelNum = channelNum + 1;
                }
            }else{//获取锁失败，从库中取订单号
            	String refundNo = afOrderRefundService.getCurrentLastRefundNo(currentDate);
                if(refundNo != null){
                    channelNum = getOrderSeqInt(refundNo.substring(16, 20))+1;
                }
                return channelNum;
            }
            TokenCacheUtil.saveObject(cacheKey, channelNum, 0l);
        }finally{
        	TokenCacheUtil.delCache(lockKey);
        }
        return channelNum;
    }
    
    //获取借款号
  	public String getBorrowNo(Date currDate){//订单号规则：6位日期_2位订单类型_5位订单序号
  		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
  		StringBuffer orderSb = new StringBuffer("jk");
  		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowSequenceNum(currDate)));
  		return orderSb.toString();
  	}
  
 
    //获取借	钱号
  	public String getBorrowCashNo(Date currDate){//订单号规则：6位日期_2位订单类型_5位订单序号
  		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
  		StringBuffer orderSb = new StringBuffer("jq");
  		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowSequenceNum(currDate)));
  		return orderSb.toString();
  	}
      private int getBorrowSequenceNum(Date currentDate) {//加锁，防止并发
          Integer channelNum = 1;
          String lockKey = Constants.CACHEKEY_BORROWNO_LOCK;
          String cacheKey = Constants.CACHEKEY_BORROWNO;
          
          try{
              if(TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")))){//获得同步锁
                  channelNum = (Integer)TokenCacheUtil.getObject(cacheKey);
                  if(channelNum == null){//缓存中无数据,从库中获取
                  	String borrowNo = afBorrowService.getCurrentLastBorrowNo(currentDate);
                      channelNum = borrowNo == null?1:(getOrderSeqInt(borrowNo.substring(16, 20))+1);
                  }else{
                      channelNum = channelNum + 1;
                  }
              }else{//获取锁失败，从库中取订单号
              	String borrowNo = afBorrowService.getCurrentLastBorrowNo(currentDate);
                  if(borrowNo != null){
                      channelNum = getOrderSeqInt(borrowNo.substring(16, 20))+1;
                  }
                  return channelNum;
              }
              TokenCacheUtil.saveObject(cacheKey, channelNum, 0l);
          }finally{
          	TokenCacheUtil.delCache(lockKey);
          }
          return channelNum;
      }
      
    //获取借款号
	public String getRepaymentNo(Date currDate){//订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("hk");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getRepaymentSequenceNum(currDate)));
		return orderSb.toString();
	}
	public String getRepaymentBorrowCashNo(Date currDate){//订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("hq");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getRepaymentSequenceNum(currDate)));
		return orderSb.toString();
	}
    private int getRepaymentSequenceNum(Date currentDate) {//加锁，防止并发
        Integer channelNum = 1;
        String lockKey = Constants.CACHEKEY_REPAYNO_LOCK;
        String cacheKey = Constants.CACHEKEY_REPAYNO;
        
        try{
            if(TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")))){//获得同步锁
                channelNum = (Integer)TokenCacheUtil.getObject(cacheKey);
                if(channelNum == null){//缓存中无数据,从库中获取
                	String repayNo = afRepaymentService.getCurrentLastRepayNo(currentDate);
                    channelNum = repayNo == null?1:(getOrderSeqInt(repayNo.substring(16, 20))+1);
                }else{
                    channelNum = channelNum + 1;
                }
            }else{//获取锁失败，从库中取订单号
            	String repayNo = afRepaymentService.getCurrentLastRepayNo(currentDate);
                if(repayNo != null){
                    channelNum = getOrderSeqInt(repayNo.substring(16, 20))+1;
                }
                return channelNum;
            }
            TokenCacheUtil.saveObject(cacheKey, channelNum, 0l);
        }finally{
        	TokenCacheUtil.delCache(lockKey);
        }
        return channelNum;
    }
}
