/**
 * 
 */
package com.ald.fanbei.api.web.api.repaycash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
 * @author suweili 2017年3月28日下午8:34:33
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRepayCashListApi")
public class GetRepayCashListApi implements ApiHandle {

	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"),0l) ;
		List<AfRepaymentBorrowCashDo> list = afRepaymentBorrowCashService.getRepaymentBorrowCashByBorrowId(rid);
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> repayList = new ArrayList<Object>();
		
		for (AfRepaymentBorrowCashDo afBorrowCashDo : list) {
			repayList.add(objectWithAfRepaymentBorrowCashDo(afBorrowCashDo));
		}
		data.put("repayList", repayList);
		resp.setResponseData(data);
		return resp;
	}
	public Map<String, Object> objectWithAfRepaymentBorrowCashDo(AfRepaymentBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getRepaymentAmount());
		data.put("gmtRepay", afBorrowCashDo.getGmtCreate());
		data.put("repayDec", afBorrowCashDo.getName());
		data.put("status", afBorrowCashDo.getStatus());
		return data;

	}
}
