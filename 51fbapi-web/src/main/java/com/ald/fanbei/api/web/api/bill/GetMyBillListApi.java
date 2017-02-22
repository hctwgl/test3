package com.ald.fanbei.api.web.api.bill;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：GetMyBillHomeInfoApi
 *@author 何鑫 2017年2月21日  10:19:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMyBillListApi")
public class GetMyBillListApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfBorrowService afBorrowService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		List<AfBorrowTotalBillDo> billList = afBorrowBillService.getUserFullBillList(userId);
		Map<String,Object> map = getBillList(billList);
		resp.setResponseData(map);
		return resp;
	}
	
	private Map<String,Object> getBillList(List<AfBorrowTotalBillDo> billList){
		List<Map<String,Object>> monthList = new ArrayList<Map<String,Object>>();
		TreeMap<Integer, Object> treemap = new TreeMap<Integer, Object>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		int year = 0;
		for (int i=0;i<billList.size();i++) {
			AfBorrowTotalBillDo afBorrowBillDo = billList.get(i);
			Map<String,Object> dataMap = new HashMap<String,Object>();
			dataMap.put("billMonth", String.format("%2d", afBorrowBillDo.getBillMonth()));
			dataMap.put("billAmount", afBorrowBillDo.getBillAmount());
			if(afBorrowBillDo.getStatus().contains(BorrowBillStatus.NO.getCode())){
				dataMap.put("billStatus", BorrowBillStatus.NO.getCode());
			}else{
				dataMap.put("billStatus", BorrowBillStatus.YES.getCode());
			}
			if(year!=0&&year!=afBorrowBillDo.getBillYear()){
				monthList = new ArrayList<Map<String,Object>>();
			}
			year = afBorrowBillDo.getBillYear();
			monthList.add(dataMap);
			treemap.put(afBorrowBillDo.getBillYear(), monthList);
		}
		resultMap.put("billYear", treemap.descendingMap());
		return resultMap;
	}
	
}
