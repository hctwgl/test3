package com.ald.fanbei.api.web.third.controller;

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionUpdateResqBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueOrderDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类现描述：和催收平台互调
 * @author chengkang 2017年8月5日 下午16:59:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/collection")
public class CollectionController {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	CollectionSystemUtil collectionSystemUtil;

	@Resource
	AfBorrowCashService borrowCashService;

	@Resource
	AfBorrowService afBorrowService;

	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Resource
	AfBorrowBillService afBorrowBillService;

	@Resource
	AfIdNumberService idNumberService;

	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	AfBorrowCashOverdueService afBorrowCashOverdueService;

	@Resource
	AfContractPdfService afContractPdfService;

	@Resource
	private AfContractPdfCreateService afContractPdfCreateService;
	/**
	 * 用户通过催收平台还款，经财务审核通过后，系统自动调用此接口向51返呗推送,返呗记录线下还款信息
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/offlineRepayment" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionOperatorNotifyRespBo offlineRepayment(HttpServletRequest request, HttpServletResponse response) {
		String data = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("deal offlineRepayment begin,sign=" + sign + ",data=" + data + ",timestamp=" + timestamp);
		CollectionOperatorNotifyRespBo notifyRespBo = collectionSystemUtil.offlineRepaymentNotify(timestamp, data, sign);
		return notifyRespBo;
	}

	/**
	 * 催收平台获取借款记录信息用于数据同步刷新
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/findBorrowCashByBorrowNoV1" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo findBorrowCashByBorrowNoV1(HttpServletRequest request, HttpServletResponse response){
		String data = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign1 = ObjectUtils.toString(request.getParameter("sign"));

		logger.info("findBorrowCashByBorrowNo data="+data+",timestamp="+timestamp+",sign1="+sign1+"");

		//解析参数
		JSONObject obj = JSON.parseObject(data);
        String borrowNo = obj.getString("borrowNo");
        String time = obj.getString("repay_time");
        Date date = null;
		try {
			date = DateUtil.stringToDate(time);
		} catch (ParseException e1) {
			e1.printStackTrace();
			logger.error("findBorrowCashByBorrowNo stringToDate is fail" + e1);
		}

		Map<String,String> map=new HashMap<String,String>();
		CollectionUpdateResqBo updteBo=new CollectionUpdateResqBo();
	
	
		try {
			AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
			if (afBorrowCashDo == null) {
				logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null,borrowNo=" + borrowNo);
				updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
				return updteBo;
			}

			
			String sign2 = DigestUtil.MD5(afBorrowCashDo.getBorrowNo());
			if (StringUtil.equals(sign1, sign2)) {// 验签成功
				BigDecimal amount = afBorrowCashDo.getAmount();
				BigDecimal rateAmount = afBorrowCashDo.getRateAmount();
				BigDecimal overdueAmount = afBorrowCashDo.getOverdueAmount();
				BigDecimal repayAmount = afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount()).add(afBorrowCashDo.getOverdueAmount()).add(afBorrowCashDo.getSumRate()).add(afBorrowCashDo.getSumOverdue()).setScale(2, RoundingMode.HALF_UP);
				BigDecimal restAmount = afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount()).add(afBorrowCashDo.getOverdueAmount()).add(afBorrowCashDo.getSumRate()).add(afBorrowCashDo.getSumOverdue()).subtract(afBorrowCashDo.getRepayAmount()).setScale(2, RoundingMode.HALF_UP);

				BigDecimal repayAmountSum = afBorrowCashDo.getRepayAmount();

				// 查询订单借款
				AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
				if (afBorrowLegalOrderCashDo != null) {
					overdueAmount = overdueAmount.add(afBorrowLegalOrderCashDo.getOverdueAmount());
					amount = amount.add(afBorrowLegalOrderCashDo.getAmount());
					rateAmount = rateAmount.add(afBorrowLegalOrderCashDo.getInterestAmount());
					restAmount = restAmount.add(afBorrowLegalOrderCashDo.getAmount()).add(afBorrowLegalOrderCashDo.getInterestAmount()).add(afBorrowLegalOrderCashDo.getOverdueAmount()).add(afBorrowLegalOrderCashDo.getPoundageAmount()).add(afBorrowLegalOrderCashDo.getSumRepaidInterest())
							.add(afBorrowLegalOrderCashDo.getSumRepaidOverdue()).add(afBorrowLegalOrderCashDo.getSumRepaidPoundage()).subtract(afBorrowLegalOrderCashDo.getRepaidAmount());

					repayAmount = repayAmount.add(afBorrowLegalOrderCashDo.getAmount()).add(afBorrowLegalOrderCashDo.getInterestAmount()).add(afBorrowLegalOrderCashDo.getOverdueAmount()).add(afBorrowLegalOrderCashDo.getPoundageAmount()).add(afBorrowLegalOrderCashDo.getSumRepaidInterest())
							.add(afBorrowLegalOrderCashDo.getSumRepaidOverdue()).add(afBorrowLegalOrderCashDo.getSumRepaidPoundage());

					repayAmountSum = repayAmountSum.add(afBorrowLegalOrderCashDo.getRepaidAmount());
				}

				if (afBorrowLegalOrderCashDo != null) {
					if (AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus()) && AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(afBorrowLegalOrderCashDo.getStatus())) {
						overdueAmount = BigDecimal.ZERO;
						repayAmount = repayAmountSum;
						restAmount = BigDecimal.ZERO;
					}
				} else {
					if (AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus())) {
						overdueAmount = BigDecimal.ZERO;
						repayAmount = repayAmountSum;
						restAmount = BigDecimal.ZERO;
					}
				}

				// 如果预计还款日在今天之前，且为待还款状态，则已逾期，逾期天数=现在减去预计还款日
				// 区分商品借款逾期 和 借款逾期
				long overdueDay = 0;
				Date nowDate = new Date();
				if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED") && afBorrowCashDo.getGmtPlanRepayment().before(nowDate)) {
					overdueDay = DateUtil.getNumberOfDatesBetween(afBorrowCashDo.getGmtPlanRepayment(), nowDate);
				} else if (afBorrowLegalOrderCashDo != null && StringUtils.equals("AWAIT_REPAY", afBorrowLegalOrderCashDo.getStatus()) && afBorrowLegalOrderCashDo.getGmtPlanRepay().before(nowDate)) {
					// 如果借款没有逾期，判断商品借款是否逾期
					overdueDay = DateUtil.getNumberOfDatesBetween(afBorrowLegalOrderCashDo.getGmtPlanRepay(), nowDate);
				}

				map.put("consumer_no", afBorrowCashDo.getUserId() + "");
				map.put("borrow_id", afBorrowCashDo.getRid() + "");
				map.put("borrow_no", afBorrowCashDo.getBorrowNo());
				map.put("card_name", afBorrowCashDo.getCardName());
				map.put("card_number", afBorrowCashDo.getCardNumber());
				map.put("gmt_arrival", DateUtil.formatDateTime(afBorrowCashDo.getGmtArrival()));
				map.put("type", afBorrowCashDo.getType());
				map.put("amount", amount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
				map.put("rate_amount", rateAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
				map.put("overdue_amount", overdueAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
				map.put("repay_amount", repayAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
				map.put("rest_amount", restAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
				map.put("overdue_day", overdueDay + "");
				map.put("renewal_num", afBorrowCashDo.getRenewalNum() + "");
				map.put("repay_amount_sum", repayAmountSum + "");
				map.put("status", afBorrowCashDo.getStatus());
				map.put("gmt_plan_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtPlanRepayment()));

				//根据催收传来的时间计算应还金额
				long numberOfDatesBetween = DateUtil.getNumberOfDatesBetween(afBorrowCashDo.getGmtPlanRepayment(),date);
				if(numberOfDatesBetween<1){   //没有逾期时计算应还金额
					BigDecimal actual_amount= BigDecimalUtil.add(afBorrowCashDo.getAmount(),afBorrowCashDo.getRateAmount(),afBorrowCashDo.getSumRate(),afBorrowCashDo.getOverdueAmount(),afBorrowCashDo.getSumOverdue()).subtract(afBorrowCashDo.getRepayAmount());
					map.put("actual_amount", actual_amount.multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				} else {   //逾期时计算应还金额
					BigDecimal amounts = afBorrowCashOverdueService.getAfBorrowCashOverdueDoByBorrowId(afBorrowCashDo.getRid(), numberOfDatesBetween);
					BigDecimal actual_amount= BigDecimalUtil.add(afBorrowCashDo.getAmount(),afBorrowCashDo.getRateAmount(),afBorrowCashDo.getSumRate(),amounts).subtract(afBorrowCashDo.getRepayAmount());
					map.put("actual_amount", actual_amount.multiply(BigDecimalUtil.ONE_HUNDRED)+"");
				}
				//

				map.put("majiabao_name", StringUtil.null2Str(afBorrowCashDo.getMajiabaoName()));
				if (StringUtil.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
					map.put("gmt_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtModified()) + "");
				} else {
					map.put("gmt_repayment", "");
				}
				String jsonString = JsonUtil.toJSONString(map);
				updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
				updteBo.setData(jsonString);
				return updteBo;
			} else {
				updteBo.setCode(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getMsg());
				return updteBo;
			}
		} catch (Exception e) {
			logger.error("error message " + e);
			updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
			return updteBo;
		}
	}
		
		/**
		 * 催收平台获取借款记录信息用于数据同步刷新
		 * 
		 * @param request
		 * @param response
		 * @return
		 */
		@RequestMapping(value = { "/findBorrowCashByBorrowNo" }, method = RequestMethod.POST)
		@ResponseBody
		public CollectionUpdateResqBo findBorrowCashByBorrowNo(HttpServletRequest request, HttpServletResponse response) {
			String borrowNo = ObjectUtils.toString(request.getParameter("data"));
			String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
			String sign1 = ObjectUtils.toString(request.getParameter("sign"));
			logger.info("findBorrowCashByBorrowNo data=" + borrowNo + ",timestamp=" + timestamp + ",sign1=" + sign1 + "");

			Map<String, String> map = new HashMap<String, String>();
			CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
			try {
				AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
				if (afBorrowCashDo == null) {
					logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null,borrowNo=" + borrowNo);
					updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
					updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
					return updteBo;
				}

				
				String sign2 = DigestUtil.MD5(afBorrowCashDo.getBorrowNo());
				if (StringUtil.equals(sign1, sign2)) {// 验签成功
					BigDecimal amount = afBorrowCashDo.getAmount();
					BigDecimal rateAmount = afBorrowCashDo.getRateAmount();
					BigDecimal overdueAmount = afBorrowCashDo.getOverdueAmount();
					BigDecimal repayAmount = afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount()).add(afBorrowCashDo.getOverdueAmount()).add(afBorrowCashDo.getSumRate()).add(afBorrowCashDo.getSumOverdue()).setScale(2, RoundingMode.HALF_UP);
					BigDecimal restAmount = afBorrowCashDo.getAmount().add(afBorrowCashDo.getRateAmount()).add(afBorrowCashDo.getOverdueAmount()).add(afBorrowCashDo.getSumRate()).add(afBorrowCashDo.getSumOverdue()).subtract(afBorrowCashDo.getRepayAmount()).setScale(2, RoundingMode.HALF_UP);

					BigDecimal repayAmountSum = afBorrowCashDo.getRepayAmount();

					// 查询订单借款
					AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
					if (afBorrowLegalOrderCashDo != null) {
						overdueAmount = overdueAmount.add(afBorrowLegalOrderCashDo.getOverdueAmount());
						amount = amount.add(afBorrowLegalOrderCashDo.getAmount());
						rateAmount = rateAmount.add(afBorrowLegalOrderCashDo.getInterestAmount());
						restAmount = restAmount.add(afBorrowLegalOrderCashDo.getAmount()).add(afBorrowLegalOrderCashDo.getInterestAmount()).add(afBorrowLegalOrderCashDo.getOverdueAmount()).add(afBorrowLegalOrderCashDo.getPoundageAmount()).add(afBorrowLegalOrderCashDo.getSumRepaidInterest())
								.add(afBorrowLegalOrderCashDo.getSumRepaidOverdue()).add(afBorrowLegalOrderCashDo.getSumRepaidPoundage()).subtract(afBorrowLegalOrderCashDo.getRepaidAmount());

						repayAmount = repayAmount.add(afBorrowLegalOrderCashDo.getAmount()).add(afBorrowLegalOrderCashDo.getInterestAmount()).add(afBorrowLegalOrderCashDo.getOverdueAmount()).add(afBorrowLegalOrderCashDo.getPoundageAmount()).add(afBorrowLegalOrderCashDo.getSumRepaidInterest())
								.add(afBorrowLegalOrderCashDo.getSumRepaidOverdue()).add(afBorrowLegalOrderCashDo.getSumRepaidPoundage());

						repayAmountSum = repayAmountSum.add(afBorrowLegalOrderCashDo.getRepaidAmount());
					}

					if (afBorrowLegalOrderCashDo != null) {
						if (AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus()) && AfBorrowLegalOrderCashStatus.FINISHED.getCode().equals(afBorrowLegalOrderCashDo.getStatus())) {
							overdueAmount = BigDecimal.ZERO;
							repayAmount = repayAmountSum;
							restAmount = BigDecimal.ZERO;
						}
					} else {
						if (AfBorrowCashStatus.finsh.getCode().equals(afBorrowCashDo.getStatus())) {
							overdueAmount = BigDecimal.ZERO;
							repayAmount = repayAmountSum;
							restAmount = BigDecimal.ZERO;
						}
					}

					// 如果预计还款日在今天之前，且为待还款状态，则已逾期，逾期天数=现在减去预计还款日
					// 区分商品借款逾期 和 借款逾期
					long overdueDay = 0;
					Date nowDate = new Date();
					if (StringUtils.equals(afBorrowCashDo.getStatus(), "TRANSED") && afBorrowCashDo.getGmtPlanRepayment().before(nowDate)) {
						overdueDay = DateUtil.getNumberOfDatesBetween(afBorrowCashDo.getGmtPlanRepayment(), nowDate);
					} else if (afBorrowLegalOrderCashDo != null && StringUtils.equals("AWAIT_REPAY", afBorrowLegalOrderCashDo.getStatus()) && afBorrowLegalOrderCashDo.getGmtPlanRepay().before(nowDate)) {
						// 如果借款没有逾期，判断商品借款是否逾期
						overdueDay = DateUtil.getNumberOfDatesBetween(afBorrowLegalOrderCashDo.getGmtPlanRepay(), nowDate);
					}

					map.put("consumer_no", afBorrowCashDo.getUserId() + "");
					map.put("borrow_id", afBorrowCashDo.getRid() + "");
					map.put("borrow_no", afBorrowCashDo.getBorrowNo());
					map.put("card_name", afBorrowCashDo.getCardName());
					map.put("card_number", afBorrowCashDo.getCardNumber());
					map.put("gmt_arrival", DateUtil.formatDateTime(afBorrowCashDo.getGmtArrival()));
					map.put("type", afBorrowCashDo.getType());
					map.put("amount", amount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("rate_amount", rateAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("overdue_amount", overdueAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("repay_amount", repayAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("rest_amount", restAmount.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("overdue_day", overdueDay + "");
					map.put("renewal_num", afBorrowCashDo.getRenewalNum() + "");
					map.put("repay_amount_sum", repayAmountSum.multiply(BigDecimalUtil.ONE_HUNDRED) + "");
					map.put("status", afBorrowCashDo.getStatus());
					map.put("gmt_plan_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtPlanRepayment()));
					map.put("majiabao_name", StringUtil.null2Str(afBorrowCashDo.getMajiabaoName()));
					if (StringUtil.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.finsh.getCode())) {
						map.put("gmt_repayment", DateUtil.formatDateTime(afBorrowCashDo.getGmtModified()) + "");
					} else {
						map.put("gmt_repayment", "");
					}
					String jsonString = JsonUtil.toJSONString(map);
					updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
					updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
					updteBo.setData(jsonString);
					return updteBo;
				} else {
					updteBo.setCode(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getCode());
					updteBo.setMsg(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getMsg());
					return updteBo;
				}
			} catch (Exception e) {
				logger.error("error message " + e);
				updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
				return updteBo;
			}
		}


	/**
	 * 催收平台获取用户身份证图片和人脸识别信息
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/findUserInfoByConsumerNo" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo findUserInfoByConsumerNo(HttpServletRequest request, HttpServletResponse response) {
		String data = ObjectUtils.toString(request.getParameter("data"));
		JSONObject obj = JSON.parseObject(data);
		Long userId = NumberUtil.objToLongDefault(obj.getString("consumerNo"), 0L);
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign1 = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("findUserInfoByConsumerNo data=" + userId + ",timestamp=" + timestamp + ",sign1=" + sign1 + "");
		Map<String, String> map = new HashMap<String, String>();
		CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
		try {
			String sign2 = DigestUtil.MD5(data);
			if (StringUtil.equals(sign1, sign2)) {// 验签成功
				AfIdNumberDo afIdNumberDo = idNumberService.selectUserIdNumberByUserId(userId);
				if (afIdNumberDo == null) {
					logger.error("findUserInfoByConsumerNo afIdNumberDo is null,userId:" + userId);
					map.put("id_front_url", "");
					map.put("id_behind_url", "");
					map.put("face_url", "");
				} else {
					map.put("id_front_url", afIdNumberDo.getIdFrontUrl());
					map.put("id_behind_url", afIdNumberDo.getIdBehindUrl());
					map.put("face_url", afIdNumberDo.getFaceUrl());
				}
				String jsonString = JsonUtil.toJSONString(map);
				updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
				updteBo.setData(jsonString);
				return updteBo;
			} else {
				logger.info("request sign fail");
				updteBo.setCode(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR.getMsg());
				return updteBo;
			}
		} catch (Exception e) {
			logger.error("findUserInfoByConsumerNo error : error message " + e);
			updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
			return updteBo;
		}
	}

	/**
	 * 催收平台平账接口
	 *
	 * @param borrowNo
	 * 
	 * @return
	 */
	@RequestMapping(value = { "/updateBalancedDate" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo updateBalancedDate(HttpServletRequest request, HttpServletResponse response) {
		String borrowNo = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));

		logger.info("updateBalancedDate data=" + borrowNo + ",timestamp=" + timestamp + ",sign1=" + sign + "");

		AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
		CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
		if (afBorrowCashDo == null) {
			logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null,borrowNo=" + borrowNo);
			updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST.getMsg());
			return updteBo;
		}
		String sign1 = DigestUtil.MD5(afBorrowCashDo.getBorrowNo());
		if (StringUtil.equals(sign, sign1)) { // 验签成功
			// 查询订单借款 FIXME
			AfBorrowLegalOrderCashDo legalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(afBorrowCashDo.getRid());
			if (legalOrderCashDo != null) {
				// 新借款处理
				if(legalOrderCashDo.getRepaidAmount().compareTo(legalOrderCashDo.getAmount()) >= 0) {
					legalOrderCashDo.setOverdueAmount(BigDecimal.ZERO);
					legalOrderCashDo.setSumRepaidOverdue(legalOrderCashDo.getRepaidAmount().subtract(legalOrderCashDo.getAmount()));
					legalOrderCashDo.setStatus(AfBorrowLegalOrderCashStatus.FINISHED.getCode());
					afBorrowLegalOrderCashService.updateLegalOrderCashBalanced(legalOrderCashDo);
				} else {
					// 已还款金额小于借款金额，不能平账
					logger.error("repayAmount<amount Balanced is fail,borrowNo=" + borrowNo);
					updteBo.setCode(FanbeiThirdRespCode.COLLECTION_NOT_Balanced.getCode());
					updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_NOT_Balanced.getMsg());
					return updteBo;
				}

			}
			// 老的借款处理
			if (afBorrowCashDo.getRepayAmount().compareTo(afBorrowCashDo.getAmount()) >= 0) {
				// 平账
				afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO);
				afBorrowCashDo.setSumOverdue(afBorrowCashDo.getRepayAmount().subtract(afBorrowCashDo.getAmount()));
				afBorrowCashDo.setStatus(AfBorrowCashStatus.finsh.getCode());
				borrowCashService.updateBalancedDate(afBorrowCashDo);
				logger.info("repayAmount>=amount Balanced is success,borrowNo=" + borrowNo + ",repayAmount=" + afBorrowCashDo.getRepayAmount() + ",amount=" + afBorrowCashDo.getAmount());

				updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
				return updteBo;
			} else {
				// 已还款金额小于借款金额，不能平账
				logger.error("repayAmount<amount Balanced is fail,borrowNo=" + borrowNo);
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_NOT_Balanced.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_NOT_Balanced.getMsg());
				return updteBo;
			}


		} else {
			logger.error("sign and sign is fail,borrowNo=" + borrowNo);
			updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
			return updteBo;
		}
	}

	/**
	 * 催收平台查询borrowId接口
	 *
	 * @param borrowNo
	 *
	 * @return
	 */
	@RequestMapping(value = { "/getBorrowIdByNo" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo getBorrowIdByNo(HttpServletRequest request, HttpServletResponse response) {
		String borrowNo = ObjectUtils.toString(request.getParameter("data"));
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));

		logger.info("getBorrowIdByNo data=" + borrowNo + ",timestamp=" + timestamp + ",sign1=" + sign + "");

		AfBorrowCashDo afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
		CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
		if (afBorrowCashDo == null) {
			logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null");
			updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST.getMsg());
			return updteBo;
		}
		String sign1 = DigestUtil.MD5(afBorrowCashDo.getBorrowNo());
		if (StringUtil.equals(sign, sign1)) { // 验签成功
			updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
			Map<String, String> map = new HashMap<String, String>();
			map.put("borrowId", afBorrowCashDo.getRid().toString());
			String jsonString = JsonUtil.toJSONString(map);
			updteBo.setData(jsonString);
		} else {
			logger.info("sign and sign is fail");
			updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
			return updteBo;
		}
		return updteBo;
	}


	/**
	 * 催收平台查询pdf合同协议
	 * @param data

	 * @return
	 */
	@RequestMapping(value = { "/getContractProtocolPdf"}, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo getContractProtocolPdf(HttpServletRequest request, HttpServletResponse response){
		String data = ObjectUtils.toString(request.getParameter("data"));
		JSONObject object = JSON.parseObject(data);
		String borrowNo = object.getString("borrowNo");
		int type = NumberUtil.objToIntDefault(object.getString("type"),0);//1:现金借款协议 2:旧版商品分期协议 3:续借协议 4：
		String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
		String sign = ObjectUtils.toString(request.getParameter("sign"));
		logger.info("getContractProtocolPdf id="+borrowNo+",timestamp="+timestamp+",sign1="+sign+",type="+type);
		CollectionUpdateResqBo updteBo=new CollectionUpdateResqBo();
		Long id = 0l;
		AfBorrowDo afBorrowDo = null;
		AfBorrowCashDo afBorrowCashDo = null;
		if (type == 1){//旧版现金借款协议
			afBorrowCashDo = borrowCashService.getBorrowCashInfoByBorrowNo(borrowNo);
			if (afBorrowCashDo == null){
				logger.error("getContractProtocolPdf afBorrowCashDo is null,no =>{}",borrowNo);
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST.getMsg());
				return updteBo;
			}
			id = afBorrowCashDo.getRid();
		}else if (type == 2){//旧版商品分期协议
			afBorrowDo = afBorrowService.getBorrowInfoByBorrowNo(borrowNo);
			if (afBorrowDo == null){
				logger.error("getContractProtocolPdf afBorrowDo is null,no =>{}",borrowNo);
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST.getMsg());
				return updteBo;
			}
			id = afBorrowDo.getRid();
		}else {
			logger.error("getContractProtocolPdf type is error,type =>{}",type);
			updteBo.setCode(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getMsg());
			return updteBo;
		}
		AfContractPdfDo afContractPdfDo = afContractPdfService.getContractPdfDoByTypeAndTypeId(id,(byte)type);
		if(afContractPdfDo == null) {
			if (type == 1){
				afContractPdfCreateService.protocolCashLoan(afBorrowCashDo.getRid(),afBorrowCashDo.getAmount(),afBorrowCashDo.getUserId());
			}else if (type == 2){
				afContractPdfCreateService.protocolInstalment(afBorrowDo.getUserId(),afBorrowDo.getNper(),afBorrowDo.getAmount(),afBorrowDo.getRid());
			}
			afContractPdfDo = afContractPdfService.getContractPdfDoByTypeAndTypeId(id,(byte)type);
			if (afContractPdfDo == null){
				logger.error("getContractProtocolPdf afContractPdfDo is null,id =>{}",id);
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_CONTRACT_PDF_CREATE_ERROR.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_CONTRACT_PDF_CREATE_ERROR.getMsg());
				return updteBo;
			}
		}
		String sign1=DigestUtil.MD5(data);
		if (StringUtil.equals(sign, sign1)) {	// 验签成功
			updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
			Map<String,String> map=new HashMap<String,String>();
			map.put("contractPdfUrl",afContractPdfDo.getContractPdfUrl());
			String jsonString = JsonUtil.toJSONString(map);
			updteBo.setData(jsonString);
		} else {
			logger.info("sign and sign is fail");
			updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
			return updteBo;
		}
		return updteBo;
	}

	/**
	 * 催收平台获取借款记录信息用于数据同步刷新bill
	 *
	 * @author yuyue
	 * @Time 2017年9月19日 上午10:08:33
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/findBorrowBillByBillNo" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo findBorrowBillByBillNo(HttpServletRequest request, HttpServletResponse response) {
		CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
		try {
			String sign = ObjectUtils.toString(request.getParameter("sign"));
			List<Long> billIds = JSON.parseArray(request.getParameter("data"), Long.class);
			if (billIds == null || billIds.size() < 0) {
				logger.info("findBorrowBillByBillNo error :" + FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getMsg());
				updteBo.setCode(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getMsg());
				return updteBo;
			}
			logger.info("findBorrowBillByBillNo data=" + billIds + ",sign=" + sign + "");
			byte[] salt = DigestUtil.decodeHex("5b3d654201bab83c");
			byte[] pd = DigestUtil.digestString(billIds.get(0).toString().getBytes("UTF-8"), salt, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			String checkSign = DigestUtil.encodeHex(pd);
			if (!StringUtil.equals(sign, checkSign)) {
				logger.info("findBorrowBillByBillNo sign and sign is fail");
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
				return updteBo;
			}
			List<AfOverdueOrderDto> orderList = afBorrowBillService.getOverdueDataToRiskByBillIds(billIds);
			if (orderList == null || orderList.size() < 1) {
				logger.error("findBorrowBillByBorrowNo afBorrowCashDo is null billIds = " + billIds);
				updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
				return updteBo;
			}
			pd = DigestUtil.digestString(orderList.get(0).getOrderNo().getBytes("UTF-8"), salt, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			sign = DigestUtil.encodeHex(pd);
			String jsonString = JsonUtil.toJSONString(orderList);
			updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
			updteBo.setData(jsonString);
			updteBo.setSign(sign);
			return updteBo;
		} catch (Exception e) {
			logger.error("findBorrowBillByBillNo error : error message " + e);
			updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
			return updteBo;
		}
	}

	/**
	 * 催收平台根据userId获取借款记录信息
	 *
	 * @author caowu
	 * @Time 2017年10月9日 下午17:20:33
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = { "/findBorrowBillByConsumerNo" }, method = RequestMethod.POST)
	@ResponseBody
	public CollectionUpdateResqBo findBorrowBillByConsumerNo(HttpServletRequest request, HttpServletResponse response) {
		CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
		try {
			String sign = ObjectUtils.toString(request.getParameter("sign"));
			Long consumerNo = NumberUtil.objToLong(request.getParameter("consumerNo"));
			if (NumberUtil.isNotValidForLong(consumerNo)) {
				logger.info("findBorrowBillByConsumerNo error :" + FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getMsg());
				updteBo.setCode(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST.getMsg());
				return updteBo;
			}
			logger.info("findBorrowBillByConsumerNo data=" + consumerNo + ",sign=" + sign + "");
			byte[] salt = DigestUtil.decodeHex("5b3d654201bab83c");
			byte[] pd = DigestUtil.digestString(consumerNo.toString().getBytes("UTF-8"), salt, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			String checkSign = DigestUtil.encodeHex(pd);
			if (!StringUtil.equals(sign, checkSign)) {
				logger.info("findBorrowBillByConsumerNo sign and sign is fail");
				updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
				return updteBo;
			}
			List<AfOverdueOrderDto> orderList = afBorrowBillService.getOverdueDataToRiskByConsumerNo(consumerNo);
			if (orderList == null || orderList.size() < 1) {
				logger.error("findBorrowBillByConsumerNo afBorrowCashDo is null");
				updteBo.setCode(FanbeiThirdRespCode.FAILED.getCode());
				updteBo.setMsg(FanbeiThirdRespCode.FAILED.getMsg());
				return updteBo;
			}
			pd = DigestUtil.digestString(orderList.get(0).getOrderNo().getBytes("UTF-8"), salt, Constants.DEFAULT_DIGEST_TIMES, Constants.SHA1);
			sign = DigestUtil.encodeHex(pd);
			String jsonString = JsonUtil.toJSONString(orderList);
			updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
			updteBo.setData(jsonString);
			updteBo.setSign(sign);
			return updteBo;
		} catch (Exception e) {
			logger.error("findBorrowBillByConsumerNo error : error message " + e);
			updteBo.setCode(FanbeiThirdRespCode.SYSTEM_ERROR.getCode());
			updteBo.setMsg(FanbeiThirdRespCode.SYSTEM_ERROR.getMsg());
			return updteBo;
		}
	}
}
