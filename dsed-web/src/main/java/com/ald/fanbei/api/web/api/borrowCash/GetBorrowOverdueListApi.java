/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowCashOverdueService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashOverdueDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * @author suweili 2017年3月25日下午2:15:06
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowOverdueListApi")
public class GetBorrowOverdueListApi implements ApiHandle {


	@Resource
	AfBorrowCashOverdueService afBorrowCashOverdueService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long rid = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"),0l) ;
		 List<AfBorrowCashOverdueDo> list = afBorrowCashOverdueService.getBorrowCashOverdueListByBorrowId(rid);
			List<Object> overdueList = new ArrayList<Object>();
			Map<String, Object> data = new HashMap<String, Object>();

		 for (AfBorrowCashOverdueDo afBorrowCashOverdueDo : list) {
			 overdueList.add(objectWithAfBorrowCashOverdueDo(afBorrowCashOverdueDo));
		 }
		 data.put("overdueList", overdueList);
		 resp.setResponseData(data);
		return resp;
	}
	public Map<String, Object> objectWithAfBorrowCashOverdueDo(AfBorrowCashOverdueDo borrowCashOverdueDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", borrowCashOverdueDo.getRid());
		data.put("amount", borrowCashOverdueDo.getCurrentAmount());
		data.put("gmtCreate", borrowCashOverdueDo.getGmtCreate());
		data.put("overDueAmount", borrowCashOverdueDo.getInterest());

		return data;

	}
	

}
