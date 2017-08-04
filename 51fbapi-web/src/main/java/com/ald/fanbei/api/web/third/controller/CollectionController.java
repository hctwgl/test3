package com.ald.fanbei.api.web.third.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;

/**
 * @类现描述：
 * @author hexin 2017年3月24日 下午16:59:39
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/collection")
public class CollectionController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	CollectionSystemUtil collectionSystemUtil;
	
	/**
	 * 用户通过催收平台还款，经财务审核通过后，系统自动调用此接口向51返呗推送,返呗记录线下还款信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/offlineRepayment" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionOperatorNotifyRespBo offlineRepayment (HttpServletRequest request, HttpServletResponse response) {
		String data = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("deal offlineRepayment begin,sign=" + sign + ",data=" + data + ",timestamp=" + timestamp);
		CollectionOperatorNotifyRespBo notifyRespBo = collectionSystemUtil.offlineRepaymentNotify(timestamp, data, sign);
		return notifyRespBo;
	}
	
	@RequestMapping(value = { "/consumerRepayment" }, method = RequestMethod.GET)
	@ResponseBody
	public RiskRespBo consumerRepayment (HttpServletRequest request, HttpServletResponse response) {
		String repayNo = "hq20170804164757197803";
		String borrowNo = "jq2017071711190300535";
		String cardNumber = "6212261202021718508";
		String cardName = "工商银行";
		String tradeNO="4004842001201708044450536351";
		String repayTime="2017-08-04 16:48:00";
		BigDecimal amount = BigDecimal.valueOf(123);
		BigDecimal restAmount = BigDecimal.valueOf(2314);
		BigDecimal repayAmount = BigDecimal.valueOf(12);
		BigDecimal overdueAmount = BigDecimal.valueOf(32);
		BigDecimal repayAmountSum = BigDecimal.valueOf(23);
		BigDecimal rateAmount = BigDecimal.valueOf(213);
		
		RiskRespBo notifyRespBo = collectionSystemUtil.consumerRepayment(repayNo, borrowNo, cardNumber, cardName,repayTime,tradeNO, amount, restAmount, repayAmount, overdueAmount, repayAmountSum, rateAmount);
		return notifyRespBo;
	}
	
	@RequestMapping(value = { "/renewalNotify" }, method = RequestMethod.GET)
	@ResponseBody
	public RiskRespBo renewalNotify (HttpServletRequest request, HttpServletResponse response) {
		String borrowNo = "jq2017071711190300535";
		String renewalNo = "hq20170804164757197803";
		String renewalAmount="321";
		Integer renewalNum = 1;
		RiskRespBo notifyRespBo = collectionSystemUtil.renewalNotify(borrowNo, renewalNo, renewalNum,renewalAmount);
		return notifyRespBo;
	}
}
