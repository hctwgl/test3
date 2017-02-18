package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfOrderService;
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
	
	public String getOrderNo(OrderType orderType){//订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.DEFAULT_PATTERN).substring(2);
		StringBuffer orderSb = new StringBuffer(orderType.getShortName());
		orderSb.append(dateStr).append(getOrderSeqStr(this.getSequenceNum(currDate,orderType)));
		return orderSb.toString();
	}
	
    private int getSequenceNum(Date currentDate,OrderType orderType) {//加锁，防止并发
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
    	if(StringUtil.isBlank(orderStrVal)){
    		return 1;
    	}
    	return Integer.parseInt(newStr);
    }
    
}
