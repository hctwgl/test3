package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.util.NumberWordFormat;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBorrowLegalOrderDeatilVo;
import com.google.common.collect.Maps;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：查询用户订单记录
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalOrderDetailApi")
public class GetBorrowLegalOrderDetailApi implements ApiHandle {

	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
	@Resource
	AfGoodsService afGoodsService;
	@Resource
	AfUserAccountService afUserAccountService;
	
	@Resource 
	AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Resource
	NumberWordFormat numberWordFormat;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();
		resp.setResponseData(data);
		Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));
		// 判断用户是否登录
		Long userId = context.getUserId();
		if (userId == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR);
		}
		AfBorrowLegalOrderDeatilVo afBorrowLegalOrderDeatilVo = new AfBorrowLegalOrderDeatilVo();
		AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderService.getLastBorrowLegalOrderById(orderId);

		AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowLegalOrderId(orderId);

		if (afBorrowLegalOrderCashDo != null) {
			// FIXME 计算待还金额
			BigDecimal amount = afBorrowLegalOrderCashDo.getAmount();
			BigDecimal interestAmount = afBorrowLegalOrderCashDo.getInterestAmount();
			BigDecimal poundageAmount = afBorrowLegalOrderCashDo.getPoundageAmount();
			BigDecimal repainAmount = afBorrowLegalOrderCashDo.getRepaidAmount();
			BigDecimal overdueAmount = afBorrowLegalOrderCashDo.getOverdueAmount();
			BigDecimal sumRepaidInterest = afBorrowLegalOrderCashDo.getSumRepaidInterest();
			BigDecimal sumRepaidOverdue = afBorrowLegalOrderCashDo.getSumRepaidOverdue();
			BigDecimal sumRepaidPoundage = afBorrowLegalOrderCashDo.getSumRepaidPoundage();
			BigDecimal totalAmount = BigDecimalUtil.add(amount, interestAmount, poundageAmount, overdueAmount, sumRepaidInterest, sumRepaidOverdue, sumRepaidPoundage);
			BigDecimal returnAmount = totalAmount.subtract(repainAmount);
			if (returnAmount.compareTo(BigDecimal.ZERO) < 0) {
				returnAmount = BigDecimal.ZERO;
			}
			afBorrowLegalOrderDeatilVo.setReturnAmount(returnAmount);
			afBorrowLegalOrderDeatilVo.setGmtPlanRepay(afBorrowLegalOrderCashDo.getGmtPlanRepay());
			afBorrowLegalOrderDeatilVo.setType(String.valueOf(numberWordFormat.borrowTime(afBorrowLegalOrderCashDo.getType())));
			afBorrowLegalOrderDeatilVo.setPoundageAmount(sumRepaidPoundage.add(poundageAmount));
			afBorrowLegalOrderDeatilVo.setInterestAmount(sumRepaidInterest.add(interestAmount));
			afBorrowLegalOrderDeatilVo.setGmtPlanRepay(afBorrowLegalOrderCashDo.getGmtPlanRepay());
			afBorrowLegalOrderDeatilVo.setOverdueDay(afBorrowLegalOrderCashDo.getOverdueDay());
			afBorrowLegalOrderDeatilVo.setOverdueAmount(sumRepaidOverdue.add(overdueAmount));
			afBorrowLegalOrderDeatilVo.setBorrowId(afBorrowLegalOrderCashDo.getBorrowId());
			afBorrowLegalOrderDeatilVo.setBorrowStatus(afBorrowLegalOrderCashDo.getStatus());
			afBorrowLegalOrderDeatilVo.setGmtLastRepayment(afBorrowLegalOrderCashDo.getGmtLastRepayment());
		}
		if (afBorrowLegalOrderDo != null) {
			afBorrowLegalOrderDeatilVo.setDeliveryPhone(afBorrowLegalOrderDo.getDeliveryPhone());
			afBorrowLegalOrderDeatilVo.setDeliveryUser(afBorrowLegalOrderDo.getDeliveryUser());
			afBorrowLegalOrderDeatilVo.setAddress(afBorrowLegalOrderDo.getAddress());
			afBorrowLegalOrderDeatilVo.setGoodsName(afBorrowLegalOrderDo.getGoodsName());
			afBorrowLegalOrderDeatilVo.setPriceAmount(afBorrowLegalOrderDo.getPriceAmount());
			afBorrowLegalOrderDeatilVo.setOrderNo(afBorrowLegalOrderDo.getOrderNo());
			afBorrowLegalOrderDeatilVo.setLogisticsInfo(afBorrowLegalOrderDo.getLogisticsInfo());
			afBorrowLegalOrderDeatilVo.setGmtCreate(afBorrowLegalOrderDo.getGmtCreate());
			afBorrowLegalOrderDeatilVo.setGmtDeliver(afBorrowLegalOrderDo.getGmtDeliver());
			afBorrowLegalOrderDeatilVo.setStatus(afBorrowLegalOrderDo.getStatus());

			// 获取商品ID
			Long goodsId = afBorrowLegalOrderDo.getGoodsId();
			AfGoodsDo goodsDo = afGoodsService.getGoodsById(goodsId);
			if (goodsDo != null) {
				afBorrowLegalOrderDeatilVo.setGoodsIcon(goodsDo.getGoodsIcon());
			}

			// 查询用户余额信息
			AfUserAccountDo accountInfo = afUserAccountService.getUserAccountByUserId(userId);
			if (accountInfo != null) {
				afBorrowLegalOrderDeatilVo.setUserAmount(accountInfo.getRebateAmount());
			}

			// 还款处理中金额处理
			String existRepayingMoney = YesNoStatus.NO.getCode();
			BigDecimal repayingMoney = BigDecimal.valueOf(0.00);
			BigDecimal repayingOrderMoney = BigDecimal.ZERO;
			// 如果借款记录存在，统计还款处理中金额
			repayingMoney = afRepaymentBorrowCashService.getRepayingTotalAmountByBorrowId(afBorrowLegalOrderDo.getBorrowId());
			repayingOrderMoney = afBorrowLegalOrderRepaymentDao.getOrderRepayingTotalAmountByBorrowId(afBorrowLegalOrderDo.getBorrowId());
			repayingMoney = repayingMoney.add(repayingOrderMoney);
			if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
				existRepayingMoney = YesNoStatus.YES.getCode();
			}
			afBorrowLegalOrderDeatilVo.setExistRepayingMoney(existRepayingMoney);
		}

		data.put("afBorrowLegalOrderDeatilVo", afBorrowLegalOrderDeatilVo);
		resp.setResponseData(data);
		return resp;
	}

}
