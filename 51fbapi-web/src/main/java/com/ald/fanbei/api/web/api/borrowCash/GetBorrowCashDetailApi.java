/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午1:07:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashDetailApi")
public class GetBorrowCashDetailApi extends GetBorrowCashBase implements ApiHandle {
	
	
	@Resource
	AfBorrowCashService afBorrowCashService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();	
		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
		if(afBorrowCashDo==null){
			return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);

		}
		
		Map<String, Object>  data = objectWithAfBorrowCashDo(afBorrowCashDo);
		resp.setResponseData(data);;
		
		return resp;
	}
	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());
		data.put("arrivalAmount", afBorrowCashDo.getArrivalAmount());
		data.put("rejectReason", afBorrowCashDo.getReviewDetails());
		data.put("serviceAmount", BigDecimalUtil.add(afBorrowCashDo.getRateAmount(), afBorrowCashDo.getPoundage()));
		data.put("gmtArrival", afBorrowCashDo.getGmtArrival());
		data.put("borrowNo", afBorrowCashDo.getBorrowNo());
		data.put("bankCard", afBorrowCashDo.getCardNumber());
		data.put("bankName", afBorrowCashDo.getCardName());
//		data.put("gmtClose", value)
		return data;

	}

}
