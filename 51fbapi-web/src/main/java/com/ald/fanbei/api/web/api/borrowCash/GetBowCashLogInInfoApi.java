/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowCashType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午3:48:48
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBowCashLogInInfoApi")
public class GetBowCashLogInInfoApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	AfResourceService afResourceService;
	@Resource
	AfBorrowCashService afBorrowCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		List<Object> bannerList = getBannerObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> rate = getObjectWithResourceDolist(list);
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		if (afBorrowCashDo == null) {
			data.put("status", "DEFAULT");
		} else {
			data.put("status", afBorrowCashDo.getStatus());

			if(StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.noFinsh.getCode())){
				data.put("status", AfBorrowCashStatus.transed.getCode());

			}else if(StringUtils.equals(afBorrowCashDo.getStatus(), AfBorrowCashStatus.transedfail.getCode())){
				data.put("status", AfBorrowCashStatus.waitTransed.getCode());

			}
			data.put("amount", afBorrowCashDo.getAmount());
			data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getOverdueAmount());
			BigDecimal returnAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			data.put("returnAmount", returnAmount);
			data.put("paidAmount", afBorrowCashDo.getRepayAmount());
			data.put("overdueAmount", afBorrowCashDo.getOverdueAmount());
			data.put("overdueDay", afBorrowCashDo.getOverdueDay());
			Integer day = NumberUtil.objToIntDefault( AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
			data.put("type", AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode());			
			data.put("repaymentDay", DateUtil.addDays(afBorrowCashDo.getGmtArrival(), day));
			data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
			data.put("reviewStatus", afBorrowCashDo.getReviewStatus());
			data.put("overdueStatus", afBorrowCashDo.getOverdueStatus());
			data.put("rid", afBorrowCashDo.getRid());
		}
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService =bankRate.multiply(bankDouble).divide(new BigDecimal( 360),6,RoundingMode.HALF_UP);

		data.put("bankDoubleRate",bankService.toString());
		data.put("poundageRate",rate.get("poundage"));
		data.put("overdueRate",rate.get("overduePoundage"));
		data.put("maxAmount", rate.get("maxAmount"));
		data.put("minAmount", rate.get("minAmount"));
		data.put("borrowCashDay", rate.get("borrowCashDay"));
		data.put("bannerList",bannerList);

		resp.setResponseData(data);
		return resp;
	}

}
