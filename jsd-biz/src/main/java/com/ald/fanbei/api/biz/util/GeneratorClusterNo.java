package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.util.DateUtil;

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
	private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

	@Resource
	private JsdBorrowCashService jsdBorrowCashService;

	@Resource
	private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;

	/**
	 * 获取现金还款编号
	 *
	 * @param bankChannel
	 * @return
	 */
	public String getRepaymentBorrowCashNo(String bankChannel) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("hq");
		if(BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
			orderSb.append("kj");
		}
		orderSb.append(dateStr).append(getOrderSeqStr(this.getRepaymentSequenceNum()));
		return orderSb.toString();
	}

	/**
	 * 获取购买搭售商品借款编号
	 *
	 * @param currDate
	 * @return
	 */
	public String geBorrowLegalOrderCashNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("lg");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowCashSequenceNum(currDate, "lg")));
		return orderSb.toString();
	}
	/**
	 * 获取订单号
	 *
	 * @param orderType
	 * @return
	 */
	public String getOrderNo(OrderType orderType) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.DEFAULT_PATTERN).substring(2);
		StringBuffer orderSb = new StringBuffer(orderType.getShortName());
		orderSb.append(dateStr).append(getOrderSeqStr(this.getOrderSequenceNum(currDate, orderType)));
		return orderSb.toString();
	}

	private int getBorrowCashSequenceNum(Date currentDate, String orderPre) {// 加锁，防止并发

		String orderNoPre = orderPre + DateUtil.getNowYearMonthDay(currentDate);
		Integer channelNum = 1;
		String lockKey = Constants.CACHEKEY_BORROWCASHNO_LOCK;
		String cacheKey = Constants.CACHEKEY_BORROWCASHNO;
		boolean isGetLock = TokenCacheUtil.getLockTryTimes(lockKey, "1",Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
		try {
			if (isGetLock) {// 获得同步锁
				channelNum = (Integer) TokenCacheUtil.getObject(cacheKey);
				if (channelNum == null) {// 缓存中无数据,从库中获取
					String borrowNo = jsdBorrowCashService.getCurrentLastBorrowNo(orderNoPre);
					channelNum = borrowNo == null ? 1: (getOrderSeqInt(borrowNo.substring(16, 20)) + 1);
				} else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String borrowNo = jsdBorrowCashService.getCurrentLastBorrowNo(orderNoPre);
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

	private int getOrderSequenceNum(Date currentDate, OrderType orderType) {// 加锁，防止并发
		Integer channelNum = 1;
		String lockKey = Constants.CACHEKEY_ORDERNO_LOCK;
		String cacheKey = Constants.CACHEKEY_ORDERNO;
		boolean isGetLock = false;
		try {
			isGetLock = TokenCacheUtil.getLockTryTimes(lockKey, "1", Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
			if (isGetLock) {// 获得同步锁
				channelNum = (Integer) TokenCacheUtil.getObject(cacheKey);
				if (channelNum == null) {// 缓存中无数据,从库中获取
					String orderNo = jsdBorrowLegalOrderService.getCurrentLastOrderNo(currentDate);
					channelNum = orderNo == null ? 1 : (getOrderSeqInt(orderNo.substring(8, 13)) + 1);
				} else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String orderNo = jsdBorrowLegalOrderService.getCurrentLastOrderNo(currentDate);
				if (orderNo != null) {
					channelNum = getOrderSeqInt(orderNo.substring(8, 13)) + 1;
				}
				return channelNum;
			}
			TokenCacheUtil.saveObject(cacheKey, channelNum,Constants.SECOND_OF_ONE_WEEK);
		} finally {
			if (isGetLock) {
				TokenCacheUtil.delCache(lockKey);
			}
		}
		return channelNum;
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
				if (channelNum == null) {// 缓存中无数据,从库中获取
					String repayNo = jsdBorrowCashRepaymentService.getCurrentLastRepayNo(orderNoPre);
					channelNum = repayNo == null ? 1 : (getOrderSeqInt(repayNo.substring(16, 20)) + 1);
				} else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String repayNo = jsdBorrowCashRepaymentService.getCurrentLastRepayNo(orderNoPre);
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

	/**
	 * 获取还款序列号
	 * @return
	 */
	private int getRepaymentSequenceNum() {
		return TokenCacheUtil.incr(Constants.CACHEKEY_REPAYCASHNO);
	}
	/**
	 * 获取续期序列号
	 * @return
	 */
	private int getRenewalSequenceNum() {
		return TokenCacheUtil.incr(Constants.CACHEKEY_RENEWALCASHNO);
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
		orderSb.append(dateStr).append(getOrderSeqStr(this.getBorrowSequenceNum()));
		return orderSb.toString();
	}

	/**
	 * 获取现金还款编号
	 *
	 * @param currDate
	 * @return
	 */
	public String getOfflineRepaymentBorrowCashNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("offline");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getOfflineRepaymentSequenceNum()));
		return orderSb.toString();
	}

	private String getOrderSeqStr(int orderIntVal) {
		return String.format("%05d", orderIntVal);
	}

	/**
	 * 还款借款序列号
	 * @return
	 */
	private int getBorrowSequenceNum() {
		return TokenCacheUtil.incr(Constants.CACHEKEY_BORROWNO);
	}

	/**
	 * 获取还款序列号
	 *
	 * @param currentDate
	 * @return
	 */
	private int getRepaymentSequenceNum(Date currentDate, String orderPre) {// 加锁，防止并发
		Integer channelNum = 1;
		String orderNoPre = orderPre + DateUtil.getNowYearMonthDay(currentDate);
		String lockKey = Constants.CACHEKEY_REPAYNO_LOCK + orderPre;
		String cacheKey = Constants.CACHEKEY_REPAYNO + orderPre;
		boolean isGetLock = TokenCacheUtil.getLockTryTimes(lockKey, "1",Integer.parseInt(ConfigProperties.get(Constants.CONFIG_KEY_LOCK_TRY_TIMES, "5")));
		try {
			if (isGetLock) {// 获得同步锁
				channelNum = (Integer) TokenCacheUtil.getObject(cacheKey);
				if (channelNum == null) {// 缓存中无数据,从库中获取
					String repayNo = jsdBorrowCashRepaymentService.getCurrentLastRepayNo(orderNoPre);
					channelNum = repayNo == null ? 1 : (getOrderSeqInt(repayNo.substring(16, 20)) + 1);
				} else {
					channelNum = channelNum + 1;
				}
			} else {// 获取锁失败，从库中取订单号
				String repayNo = jsdBorrowCashRepaymentService.getCurrentLastRepayNo(orderNoPre);
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

	/**
	 * 获还款款号
	 *
	 * @param currDate
	 * @return
	 */
	public String getRepaymentNo(Date currDate,String bankChannel) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		String header = "hk";
		if(BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
			header += "kj";
		}
		StringBuffer orderSb = new StringBuffer(header);
		orderSb.append(dateStr).append(getOrderSeqStr(this.getRepaymentSequenceNum(currDate, header)));
		return orderSb.toString();
	}
	private int getOrderSeqInt(String orderStrVal) {
		orderStrVal = orderStrVal == null ? "" : orderStrVal;
		String newStr = orderStrVal.replaceFirst("^0*", "");
		if (StringUtil.isBlank(newStr)) {
			return 1;
		}
		return Integer.parseInt(newStr);
	}

	/**
	 * 获取线下还款序列号
	 * @return
	 */
	private int getOfflineRepaymentSequenceNum() {
		return TokenCacheUtil.incr(Constants.CACHEKEY_OFFLINE_REPAYCASHNO);
	}

	/**
	 * 获取续期支付编号
	 * 
	 * @param currDate
	 * @return
	 */
	public String getJsdRenewalNo() {// 订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("xj");
		orderSb.append(dateStr).append(getOrderSeqStr(this.getRenewalSequenceNum()));
		return orderSb.toString();
	}
	
}
