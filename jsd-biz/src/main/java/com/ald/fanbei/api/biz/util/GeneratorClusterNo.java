package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
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
	private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

	@Resource
	private JsdBorrowCashService jsdBorrowCashService;

	@Resource
	private JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
	
	@Resource
	BizCacheUtil bizCacheUtil;

	
	/**
	 * 获取贷款编号
	 * 
	 * @param currDate
	 * @return
	 */
	public String getLoanNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("dk");
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_BORROW_NO)));
		return orderSb.toString();
	}
	
	/**
	 * 获取购买搭售商品借款编号
	 *
	 * @param currDate
	 * @return
	 */
	public String getBorrowLegalOrderCashNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("loc");
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_ORDER_CASH_NO)));
		return orderSb.toString();
	}
	
	/**
	 * 获取订单号
	 *
	 * @param orderType
	 * @return
	 */
	public String getOrderNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.DEFAULT_PATTERN).substring(2);
		StringBuffer orderSb = new StringBuffer("lo");
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_ORDER_NO)));
		return orderSb.toString();
	}

	/**
	 * 获取线上还款编号
	 *
	 * @param bankChannel
	 * @return
	 */
	public String getRepaymentBorrowCashNo(String bankChannel) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		Date currDate = new Date();
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer();
		if(BankPayChannel.KUAIJIE.getCode().equals(bankChannel)){
			orderSb.append("kjhq");
		}else if(BankPayChannel.XIEYI.getCode().equals(bankChannel)){
			orderSb.append("xyhq");
		}
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_REPAY_NO)));
		return orderSb.toString();
	}

	/**
	 * 获取线下还款编号
	 *
	 * @param currDate
	 * @return
	 */
	public String getOfflineRepaymentBorrowCashNo(Date currDate) {// 订单号规则：6位日期_2位订单类型_5位订单序号
		String dateStr = DateUtil.formatDate(currDate, DateUtil.FULL_PATTERN);
		StringBuffer orderSb = new StringBuffer("offhq");
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_OFFLINE_REPAY_NO)));
		return orderSb.toString();
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
		orderSb.append(dateStr).append(String.format("%05d", bizCacheUtil.incr(Constants.CACHEKEY_RENEWAL_NO)));
		return orderSb.toString();
	}
	
}
