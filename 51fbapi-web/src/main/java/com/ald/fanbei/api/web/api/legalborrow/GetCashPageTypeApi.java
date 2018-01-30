package com.ald.fanbei.api.web.api.legalborrow;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 郭帅强 2017年12月12日 16:46:23
 * @类描述：判断是否是新版借款页面
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashPageTypeApi")
public class GetCashPageTypeApi implements ApiHandle {

	@Resource
	private AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	private AfBorrowCashService afBorrowCashService;

	@Resource
	private AfResourceService afResourceService;

	private static final String RESOURCE_TYPE = "BORROW_BACK";

	private static final String SEC_TYPE = "BORROW_BACK_RESULT";

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		Map<String, Object> data = new HashMap<>();
		resp.setResponseData(data);
		Long userId = context.getUserId();
		String pageType = "old";
		AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(RESOURCE_TYPE, SEC_TYPE);
		String isBack = afResourceDo.getValue();
		if (userId == null && StringUtils.equalsIgnoreCase("false", isBack)) {
			pageType = "new";
		} else if (userId == null && StringUtils.equalsIgnoreCase("true", isBack)) {
			pageType = "old";
		} else if (userId != null && StringUtils.equalsIgnoreCase("false", isBack)) {
			// 不回退的情况
			// 获取最后一笔借款
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
			if (afBorrowCashDo == null) {
				pageType = "new";
			} else {
				// 判断借款状态是否为完成或关闭
				String status = afBorrowCashDo.getStatus();
				if (StringUtils.equalsIgnoreCase("FINSH", status) || StringUtils.equalsIgnoreCase("CLOSED", status)) {
					pageType = "new";
				} else {
					// 查询用户是否有订单借款信息
					AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
							.getBorrowLegalOrderCashByBorrowIdNoStatus(afBorrowCashDo.getRid());
					if (afBorrowLegalOrderCashDo != null) {
						pageType = "new";
					} else {
						pageType = "old";
					}
				}
			}

		} else if (userId != null && StringUtils.equalsIgnoreCase("true", isBack)) {
			// 回退的情况
			// 获取最后一笔借款
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);

			if (afBorrowCashDo == null) {
				pageType = "old";
			} else {
				// 查询用户是否有订单借款信息
				AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
						.getBorrowLegalOrderCashByBorrowIdNoClosed(afBorrowCashDo.getRid());
				// 判断借款状态是否为完成或关闭
				String status = afBorrowCashDo.getStatus();
				if ((StringUtils.equalsIgnoreCase("FINSH", status))) {
					if (afBorrowLegalOrderCashDo == null) {
						pageType = "old";
					} else if (afBorrowLegalOrderCashDo != null
							&& StringUtils.equalsIgnoreCase(afBorrowLegalOrderCashDo.getStatus(), "FINISHED")) {
						pageType = "old";
					} else {
						pageType = "new";
					}
				} else if (StringUtils.equalsIgnoreCase("CLOSED", status)) {
					pageType = "old";
				} else {
					if (afBorrowLegalOrderCashDo != null) {
						pageType = "new";
					} else {
						pageType = "old";
					}

				}
			}
		}

		data.put("pageType", pageType);
		return resp;
	}
}
