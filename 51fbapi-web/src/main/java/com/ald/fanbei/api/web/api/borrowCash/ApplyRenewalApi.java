package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author fumeiai 2017年5月17日下午20:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyRenewalApi")
public class ApplyRenewalApi implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);

		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(rid);
		if (afBorrowCashDo == null) {
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
		}

		Map<String, Object> data = objectWithAfBorrowCashDo(afBorrowCashDo);
		
		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(afBorrowCashDo.getUserId());
		
		data.put("rebateAmount", userDto.getRebateAmount());
		data.put("jfbAmount", userDto.getJfbAmount());
		
		resp.setResponseData(data);

		return resp;
	}

	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();

		AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY);
		BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数
		AfResourceDo poundageResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BORROW_CASH_POUNDAGE);
		BigDecimal borrowCashPoundage = new BigDecimal(poundageResource.getValue());// 借钱手续费率（日）
		//未还金额
		BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate());
		BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
		// 本期手续费  = 未还金额 * 续期天数 * 借钱手续费率（日）
		BigDecimal poundage = waitPaidAmount.multiply(allowRenewalDay).multiply(borrowCashPoundage).setScale(2, RoundingMode.HALF_UP);
		// 续期应缴费用(利息+手续费+滞纳金)
		BigDecimal renewalPayAmount = afBorrowCashDo.getRateAmount().add(poundage).add(afBorrowCashDo.getOverdueAmount());

		data.put("rid", afBorrowCashDo.getRid());
		data.put("rateAmount", afBorrowCashDo.getRateAmount());// 上期利息
		data.put("overdueAmount", afBorrowCashDo.getOverdueAmount());// 滞纳金
		data.put("poundage", poundage);// 本期手续费
		data.put("renewalPayAmount", renewalPayAmount);
		data.put("renewalAmount", waitPaidAmount);// 续期金额
		data.put("renewalDay", allowRenewalDay);// 续期天数
		return data;
	}

}
