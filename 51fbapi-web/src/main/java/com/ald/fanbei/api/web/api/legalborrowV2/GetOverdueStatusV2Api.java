package com.ald.fanbei.api.web.api.legalborrowV2;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;

/**
 * @author ZJF
 * @deprecated
 * @类描述：查询用户逾期状态接口
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getOverdueStatusV2Api")
public class GetOverdueStatusV2Api implements ApiHandle {

	@Resource
	private AfResourceService afResourceService;

	@Resource
	private AfBorrowCashService afBorrowCashService;

	@Resource
	private AfBorrowBillService afBorrowBillService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Map<String, Object> data = Maps.newHashMap();

		Long userId = context.getUserId();
		String loanOverdueStatus = "N";
		String consumeOverdueStatus = "N";

		if (userId != null) {
			// 查询是否有借款逾期
			AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
			if (borrowCashDo != null) {
				if (StringUtils.equals(borrowCashDo.getOverdueStatus(), "Y")) {
					if(!StringUtils.equals("FINSH", borrowCashDo.getStatus())) {
						loanOverdueStatus = "Y";
					}
				}
			}
			// 查询是否有消费分期账单逾期
			int overdueBillCount = afBorrowBillService.countNotPayOverdueBill(userId);
			if (overdueBillCount > 0) {
				consumeOverdueStatus = "Y";
			}
		}

		data.put("loanOverdueStatus", loanOverdueStatus);
		data.put("consumeOverdueStatus", consumeOverdueStatus);
		resp.setResponseData(data);
		return resp;
	}

}
