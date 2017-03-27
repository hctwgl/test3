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

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月25日下午1:07:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBorrowCashListApi")
public class GetBorrowCashListApi implements ApiHandle {
	Integer pageNoCount = 20;
	@Resource
	AfBorrowCashService afBorrowCashService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		List<AfBorrowCashDo> list = afBorrowCashService.getBorrowCashListByUserId(userId, (pageNo - 1) * pageNoCount);
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> borrowList = new ArrayList<Object>();
		for (AfBorrowCashDo afBorrowCashDo : list) {
			borrowList.add(objectWithAfBorrowCashDo(afBorrowCashDo));
		}
		data.put("cashList", borrowList);
		resp.setResponseData(data);
		return resp;
	}

	public Map<String, Object> objectWithAfBorrowCashDo(AfBorrowCashDo afBorrowCashDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("rid", afBorrowCashDo.getRid());
		data.put("amount", afBorrowCashDo.getAmount());
		data.put("gmtCreate", afBorrowCashDo.getGmtCreate());
		data.put("status", afBorrowCashDo.getStatus());

		return data;

	}

}
