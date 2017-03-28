/**
 * 
 */
package com.ald.fanbei.api.web.api.repaycash;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午8:52:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRepayCashInfoApi")
public class GetRepayCashInfoApi implements ApiHandle {

	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("repayId"),0l) ;	
		AfRepaymentBorrowCashDo cashDo = afRepaymentBorrowCashService.getRepaymentBorrowCashByrid(rid);
		if(cashDo==null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_CASH_REPAY_NOT_EXIST_ERROR);

		}
		resp.setResponseData( objectWithAfRepaymentBorrowCashDo(cashDo));
		return resp;
	}
	public Map<String, Object> objectWithAfRepaymentBorrowCashDo(AfRepaymentBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getRepaymentAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());
		data.put("couponAmount", afBorrowCashDo.getCouponAmount());
		data.put("userAmount", afBorrowCashDo.getRebateAmount());
		data.put("actualAmount", afBorrowCashDo.getActualAmount());
		data.put("cardName", afBorrowCashDo.getCardName());
		data.put("cardNumber", afBorrowCashDo.getCardNumber());
		data.put("repayNo", afBorrowCashDo.getRepayNo());

		return data;

	}
}
