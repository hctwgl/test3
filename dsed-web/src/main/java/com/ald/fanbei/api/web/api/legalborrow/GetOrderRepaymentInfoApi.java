package com.ald.fanbei.api.web.api.legalborrow;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author 郭帅强 2017年12月12日 16:46:23
 * @类描述：判断是否是新版借款页面
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOrderRepaymentInfoApi")
public class GetOrderRepaymentInfoApi implements ApiHandle {

	@Resource
	private AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	private AfBorrowCashService afBorrowCashService;

	@Resource
	AfResourceService afResourceService;

	@Resource
	AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Map<String, Object> data = new HashMap<>();
		Long orderId = NumberUtil.objToLong(requestDataVo.getParams().get("orderId"));

		AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
				.getBorrowLegalOrderCashByBorrowLegalOrderId(orderId);
		List<Map<String, Object>> repayList = Lists.newArrayList();
		BigDecimal repayAmount = BigDecimal.ZERO;

		if (afBorrowLegalOrderCashDo != null) {
			List<AfBorrowLegalOrderRepaymentDo> repaymentDoList = afBorrowLegalOrderRepaymentDao
					.getRepaymentByOrderCashId(afBorrowLegalOrderCashDo.getRid());

			for (AfBorrowLegalOrderRepaymentDo repaymentDo : repaymentDoList) {
				Map<String, Object> repayInfoMap = Maps.newHashMap();
				BigDecimal amount = repaymentDo.getRepayAmount();
				BigDecimal couponAmount = repaymentDo.getCouponAmount();
				String status = repaymentDo.getStatus();
				repayInfoMap.put("repayNo", repaymentDo.getTradeNo());
				repayInfoMap.put("repayMode", repaymentDo.getName());
				repayInfoMap.put("status", status);
				repayInfoMap.put("gmtCreate", repaymentDo.getGmtCreate());
				repayInfoMap.put("amount", amount);
				repayInfoMap.put("cardName", repaymentDo.getCardName());
				repayInfoMap.put("cardNo", repaymentDo.getCardNo());
				repayInfoMap.put("couponAmount", couponAmount);
				repayInfoMap.put("userAmount", repaymentDo.getRebateAmount());
				//  增加实际支付金额字段
				repayInfoMap.put("actualAmount", repaymentDo.getActualAmount());
				if (StringUtils.equals("Y", status)) {
					repayAmount = repayAmount.add(amount);
				}

				repayList.add(repayInfoMap);
			}
		}
		data.put("repayAmount", repayAmount);
		data.put("repayList", repayList);

		resp.setResponseData(data);
		return resp;
	}
}
