package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chefeipeng 2017年01月8日 10:46:23
 * @类描述：判断是否是新版借款页面
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCashPageTypeV2Api")
public class GetCashPageTypeV2Api implements ApiHandle {

	@Resource
	private AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;

	@Resource
	private AfBorrowCashService afBorrowCashService;

	@Resource
	private AfResourceService afResourceService;

	@Resource
	AfBorrowLegalOrderService afBorrowLegalOrderService;
	private static final String RESOURCE_TYPE = "BORROWTWO_BACK";

	private static final String SEC_TYPE = "BORROWTWO_BACK_RESULT";

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
			pageType = "V2";
		} else if (userId == null && StringUtils.equalsIgnoreCase("true", isBack)) {
			pageType = "old";
		} else if (userId != null && StringUtils.equalsIgnoreCase("false", isBack)) {
			// 不回退的情况
			// 获取最后一笔借款
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
			if (afBorrowCashDo == null) {
				pageType = "V2";
			} else {
				// 判断借款状态是否为完成或关闭
				String status = afBorrowCashDo.getStatus();
				if (StringUtils.equalsIgnoreCase("FINSH", status) || StringUtils.equalsIgnoreCase("CLOSED", status)) {
					pageType = "V2";
				} else {
					//查询用户是否有订单
					AfBorrowLegalOrderDo borrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
					if(null != borrowLegalOrder){
						// 查询用户是否有订单借款信息
						AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
								.getBorrowLegalOrderCashByBorrowIdNoStatus(afBorrowCashDo.getRid());
						if (afBorrowLegalOrderCashDo != null) {
							pageType = "V1";
						} else {
							pageType = "V2";
						}
					}else {
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
				//查询用户是否有订单
				AfBorrowLegalOrderDo borrowLegalOrder = afBorrowLegalOrderService.getLastBorrowLegalOrderByBorrowId(afBorrowCashDo.getRid());
				// 查询用户是否有订单借款信息
				AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = afBorrowLegalOrderCashService
						.getBorrowLegalOrderCashByBorrowIdNoStatus(afBorrowCashDo.getRid());
				String status = afBorrowCashDo.getStatus();
				if (StringUtils.equalsIgnoreCase("CLOSED", status)) {
					pageType = "old";
				}else if(StringUtils.equalsIgnoreCase("FINSH", status)){
					if (borrowLegalOrder == null) {
						pageType = "old";
					} else if (borrowLegalOrder != null) {
						if(null == afBorrowLegalOrderCashDo){
							pageType = "old";
						}else if(afBorrowLegalOrderCashDo != null && StringUtils.equalsIgnoreCase(afBorrowLegalOrderCashDo.getStatus(), "FINISHED")){
							pageType = "old";
						}else{
							pageType = "V2";
						}
					}
				}else{
					if(null == borrowLegalOrder){
						pageType = "old";
					}else if(null != borrowLegalOrder){
						if(null != afBorrowLegalOrderCashDo){
							pageType = "V2";
						}else{
							pageType = "V1";
						}
					}
				}
			}
		}

		data.put("pageType", pageType);
		return resp;
	}
}
