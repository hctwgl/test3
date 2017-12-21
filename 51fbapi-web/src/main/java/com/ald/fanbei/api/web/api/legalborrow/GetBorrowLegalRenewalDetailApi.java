package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * @类描述：
 * 
 * @author fumeiai 2017年05月20日下午1:56:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowLegalRenewalDetailApi")
public class GetBorrowLegalRenewalDetailApi implements ApiHandle {

	@Resource
	AfRenewalDetailService afRenewalDetailService;

	@Resource
	AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("renewalId"), 0l);

		AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailService.getRenewalDetailByRenewalId(rid);
		if (afRenewalDetailDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}
		AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
				.getLastOrderCashByBorrowId(afRenewalDetailDo.getBorrowId());
		Map<String, Object> data = new HashMap<>();
		if (afBorrowLegalOrderCashDo == null){
			data = objectWithAfRenewalDetailDo(afRenewalDetailDo);
		}else {
			data = objectWithAfRenewalDetailDo(afRenewalDetailDo, afBorrowLegalOrderCashDo);
		}

		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> objectWithAfRenewalDetailDo(AfRenewalDetailDo afRenewalDetailDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afRenewalDetailDo.getRid());
		data.put("renewalAmount", afRenewalDetailDo.getRenewalAmount());//续期本金
		data.put("priorInterest", afRenewalDetailDo.getPriorInterest());//上期利息
		data.put("priorOverdue", afRenewalDetailDo.getPriorOverdue());//上期滞纳金
		data.put("nextPoundage", afRenewalDetailDo.getNextPoundage());//下期手续费
		data.put("cardName", afRenewalDetailDo.getCardName());//支付方式（卡名称）
		data.put("tradeNo", afRenewalDetailDo.getTradeNo());//支付编号
		data.put("gmtCreate", afRenewalDetailDo.getGmtCreate().getTime());//创建时间
		data.put("renewalNo", afRenewalDetailDo.getPayTradeNo());//续借编号
		data.put("capital", afRenewalDetailDo.getCapital());//续借本金
		data.put("type","old");
		return data;
	}

	public Map<String, Object> objectWithAfRenewalDetailDo(AfRenewalDetailDo afRenewalDetailDo,
			AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		BigDecimal lastRepaidamount = new BigDecimal(0);
		BigDecimal amount = new BigDecimal(0);
		BigDecimal goodsAmount = BigDecimal.ZERO;
		if (afBorrowLegalOrderCashDo != null) {
			amount = afBorrowLegalOrderCashDo.getAmount();
			BigDecimal sumRepaidInterest = afBorrowLegalOrderCashDo.getSumRepaidInterest();
			BigDecimal sumRepaidOverdue = afBorrowLegalOrderCashDo.getSumRepaidOverdue();
			BigDecimal sumRepaidPoundage = afBorrowLegalOrderCashDo.getSumRepaidPoundage();
			BigDecimal repaidAmount = afBorrowLegalOrderCashDo.getRepaidAmount();
			lastRepaidamount = amount.add(sumRepaidInterest).add(sumRepaidOverdue).add(sumRepaidPoundage)
					.subtract(repaidAmount);
			goodsAmount = afBorrowLegalOrderCashDo.getAmount();
		}
		data.put("rid", afRenewalDetailDo.getRid());
		data.put("renewalAmount", afRenewalDetailDo.getRenewalAmount());// 续期本金
		data.put("amount",afRenewalDetailDo.getActualAmount());// 续期金额
		data.put("priorInterest", afRenewalDetailDo.getPriorInterest().add(afBorrowLegalOrderCashDo.getSumRepaidInterest()));// 上期利息
		data.put("priorOverdue", afRenewalDetailDo.getPriorOverdue().add(afBorrowLegalOrderCashDo.getSumRepaidOverdue()));// 上期滞纳金
		data.put("priorPoundage", afRenewalDetailDo.getPriorPoundage().add(afBorrowLegalOrderCashDo.getSumRepaidPoundage()));// 上期手续费
		data.put("capital", afRenewalDetailDo.getCapital());// 本金还款部分
		data.put("cardName", afRenewalDetailDo.getCardName());// 支付方式（卡名称）
		data.put("tradeNo", afRenewalDetailDo.getTradeNo());// 支付编号
		data.put("gmtCreate", afRenewalDetailDo.getGmtCreate().getTime());// 创建时间
		data.put("renewalNo", afRenewalDetailDo.getPayTradeNo());// 续借编号
		data.put("lastRepaidamount", lastRepaidamount);// 上期待还金额
		data.put("goodsAmount", goodsAmount);// 分期商品金额
		data.put("type","new");
		return data;
	}

}
