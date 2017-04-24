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
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：借钱首页
 * @author suweili 2017年3月24日上午11:34:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashHomeInfoApi")
public class GetBorrowCashHomeInfoApi extends GetBorrowCashBase implements ApiHandle {

	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		List<Object> bannerList = getBannerObjectWithResourceDolist(
				afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
		Map<String, Object> data = new HashMap<String, Object>();
		

		Map<String, Object> rate = getObjectWithResourceDolist(list);
		
		
		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());
		BigDecimal bankDouble = new BigDecimal(rate.get("bankDouble").toString());
		BigDecimal bankService = bankRate.multiply(bankDouble).divide(new BigDecimal(360), 6, RoundingMode.HALF_UP);

		data.put("bankDoubleRate",bankService.toString());
		data.put("poundageRate",rate.get("poundage"));
		data.put("overdueRate",rate.get("overduePoundage"));

		data.put("maxAmount", rate.get("maxAmount"));
		data.put("minAmount", rate.get("minAmount"));
		data.put("borrowCashDay", rate.get("borrowCashDay"));
		data.put("lender", rate.get("lender"));

		data.put("bannerList",bannerList);
		resp.setResponseData(data);
		return resp;
	}

	

}
