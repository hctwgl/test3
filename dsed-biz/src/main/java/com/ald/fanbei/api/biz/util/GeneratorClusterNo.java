package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;

/**
 * @类描述：生成集群部署对应的编号，如订单编号中自增部分
 * @author hexin 2017年2月16日下午16:49:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("generatorClusterNo")
public class GeneratorClusterNo extends AbstractThird {

	@Resource
	TokenCacheUtil TokenCacheUtil;
	@Resource
	BizCacheUtil bizCacheUtil;
	@Resource
	DsedLoanService dsedLoanService;
	@Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;


	/**
	 * 获取借款编号
	 * 
	 * @param currDate
	 * @return
	 */
	public String getBorrowNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("jk");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowSequenceNum(currDate, "jk")));
		return orderSb.toString();
	}

	/**
	 * 获取现金还款编号
	 *
	 * @param currDate
	 * @return
	 */
	public String getRepaymentBorrowCashNo(Date currDate, String bankChannel) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		synchronized (this){
			String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
			StringBuffer orderSb = new StringBuffer("hq");
			if(BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
				orderSb.append("kj");
			}
			orderSb.append(dateStr).append(getOrderSeqStr(this.getRepaymentBorrowCacheSequenceNum(currDate, "hq")));
			return orderSb.toString();
		}
	}


	
	/**
	 * 获取贷款编号
	 * 
	 * @param currDate
	 * @return
	 */
	public String getLoanNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("dk");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowSequenceNum(currDate, "dk")));
		return orderSb.toString();
	}

	private String getOrderSeqStr(int orderIntVal) {
		return String.format("%05d", orderIntVal);
	}

	private int getOrderSeqInt(String orderStrVal) {
		orderStrVal = orderStrVal == null ? "" : orderStrVal;
		String newStr = orderStrVal.replaceFirst("^0*", "");
		if (StringUtil.isBlank(newStr)) {
			return 1;
		}
		return Integer.parseInt(newStr);
	}
	
	private int getBorrowSequenceNum(Date currentDate, String orderPre) {// 加锁，防止并发
		String orderNoPre = orderPre + DateUtil.getNowYearMonthDay(currentDate);
		Integer channelNum = 1;
		String lockKey = Constants.CACHEKEY_BORROWNO_LOCK;
		String cacheKey = Constants.CACHEKEY_BORROWNO;
		boolean isGetLock = TokenCacheUtil.getLockTryTimes(lockKey, "1",Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
		try {
			if (isGetLock) {// 获得同步锁
				channelNum = (Integer) TokenCacheUtil.getObject(cacheKey);
				if (channelNum == null) {// 缓存中无数据,从库中获取
					String borrowNo = dsedLoanService.getCurrentLastBorrowNo(orderNoPre);
					channelNum = borrowNo == null ? 1: (getOrderSeqInt(borrowNo.substring(16, 20)) + 1);
				} else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String borrowNo = dsedLoanService.getCurrentLastBorrowNo(orderNoPre);
				if (borrowNo != null) {
					channelNum = getOrderSeqInt(borrowNo.substring(16, 20)) + 1;
				}
			}
			TokenCacheUtil.saveObject(cacheKey, channelNum,Constants.SECOND_OF_ONE_WEEK);
			return channelNum;
		} finally {
			if (isGetLock) {
				TokenCacheUtil.delCache(lockKey);
			}
		}
	}

	/**
	 * 获取现金还款序列号
	 *
	 * @param currentDate
	 * @return
	 */
	private int getRepaymentBorrowCacheSequenceNum(Date currentDate,String orderPre) {// 加锁，防止并发
		Integer channelNum = 1;
		String orderNoPre = orderPre + DateUtil.getNowYearMonthDay(currentDate);
		String lockKey = Constants.CACHEKEY_REPAYCASHNO_LOCK + orderPre;
		String cacheKey = Constants.CACHEKEY_REPAYCASHNO + orderPre;
		boolean isGetLock = TokenCacheUtil.getLockTryTimes(lockKey, "1",Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
		try {
			if (isGetLock) {// 获得同步锁
				channelNum = (Integer) TokenCacheUtil.getObject(cacheKey);
				logger.info("getRepaymentBorrowCacheSequenceNum channelNum = " + channelNum);
				if(StringUtil.isBlank(channelNum+"")){
					String repayNo = dsedLoanRepaymentService.getCurrentLastRepayNo(orderNoPre);
					if (repayNo != null) {
						channelNum = getOrderSeqInt(repayNo.substring(16, 20)) + 1;
					}
				}else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String repayNo = dsedLoanRepaymentService.getCurrentLastRepayNo(orderNoPre);
				if (repayNo != null) {
					channelNum = getOrderSeqInt(repayNo.substring(16, 20)) + 1;
				}
			}
			TokenCacheUtil.saveObject(cacheKey, channelNum,Constants.SECOND_OF_ONE_WEEK);
			return channelNum;
		} finally {
			if (isGetLock) {
				TokenCacheUtil.delCache(lockKey);
			}
		}
	}
	
}
