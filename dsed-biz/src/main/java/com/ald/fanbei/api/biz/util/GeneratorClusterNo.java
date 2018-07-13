package com.ald.fanbei.api.biz.util;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.DsedLoanRepaymentService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
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
	BizCacheUtil bizCacheUtil;
	@Resource
	DsedLoanService dsedLoanService;
	@Resource
	DsedLoanRepaymentService dsedLoanRepaymentService;

	/**
	 * 获取现金还款编号
	 *
	 * @param currDate
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
	 * @return
	 */
	private int getRepaymentSequenceNum() {
		return TokenCacheUtil.incr(Constants.CACHEKEY_REPAYCASHNO);
	}
	
}
