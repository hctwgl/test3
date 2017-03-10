package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBillHomeListVo;
import com.ald.fanbei.api.web.vo.AfBillHomeVo;

/**
 * 
 *@类描述：GetMyBillHomeInfoApi
 *@author 何鑫 2017年2月21日  10:19:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMyBillHomeInfoApi")
public class GetMyBillHomeInfoApi implements ApiHandle{

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
		String billType = ObjectUtils.toString(requestDataVo.getParams().get("billType"));
		Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		//还款日期
		Date now =new Date();
		Date repayDate = afBorrowService.getReyLimitDate(now);
		Map<String,Integer> map = afBorrowService.getCurrentTermYearAndMonth(billType,now);
		AfBillHomeVo homeVo = getBillHomeVo(afBorrowBillService.getMonthlyBillByStatus(userId, map.get(Constants.DEFAULT_YEAR), 
				map.get(Constants.DEFAULT_MONTH), YesNoStatus.NO.getCode()), map.get(Constants.DEFAULT_YEAR), map.get(Constants.DEFAULT_MONTH),repayDate,pageNo,userId);
		resp.setResponseData(homeVo);
		return resp;
	}
	
	private AfBillHomeVo getBillHomeVo(BigDecimal repaymentAmount,int billYear,int billMonth,
			Date repayDate,Integer pageNo,Long userId){
		AfBorrowBillQuery query = new AfBorrowBillQuery();
		query.setPageNo(pageNo);
		query.setUserId(userId);
		query.setBillMonth(billMonth);
		query.setBillYear(billYear);
		List<AfBorrowBillDo> billList =  afBorrowBillService.getMonthBillList(query);
		AfBillHomeVo vo = new AfBillHomeVo();
		List<AfBillHomeListVo> list =new ArrayList<AfBillHomeListVo>();
		boolean flag = false;//是否逾期
		for (AfBorrowBillDo afBorrowBillDo : billList) {
			AfBillHomeListVo listVo = new AfBillHomeListVo();
			listVo.setBiilId(afBorrowBillDo.getRid());
			listVo.setBillAmount(afBorrowBillDo.getBillAmount());
			listVo.setBillNper(afBorrowBillDo.getBillNper());
			if(afBorrowBillDo.getOverdueStatus().equals(YesNoStatus.YES.getCode())
					&&afBorrowBillDo.getStatus().equals(BorrowBillStatus.NO.getCode())){
				listVo.setBillStatus(BorrowBillStatus.OVERDUE.getCode());
				flag = true;
			}else{
				listVo.setBillStatus(afBorrowBillDo.getStatus());
			}
			listVo.setBorrowNo(afBorrowBillDo.getBorrowNo());
			listVo.setGmtCreate(afBorrowBillDo.getGmtBorrow());
			listVo.setName(afBorrowBillDo.getName());
			listVo.setNper(afBorrowBillDo.getNper());
			listVo.setType(afBorrowBillDo.getType());
			list.add(listVo);
		}
		vo.setBillList(list);
		vo.setRepayAmount(repaymentAmount);
		vo.setBillMonth(String.format("%02d", billMonth));
		vo.setBillYear(billYear+"");
		vo.setBillCount(afBorrowBillService.getUserMonthlyBillTotalCount(billYear, billMonth, userId));
		vo.setPageNo(pageNo);
		vo.setRepayDay(repayDate);
		if(repaymentAmount.compareTo(BigDecimal.ZERO)==0){
			vo.setRepayStatus(BorrowBillStatus.YES.getCode());
		}else{
			if(flag){
				vo.setRepayStatus(BorrowBillStatus.OVERDUE.getCode());
			}else{
				vo.setRepayStatus(BorrowBillStatus.NO.getCode());
			}
		}
		BigDecimal hasAmount = afBorrowBillService.getMonthlyBillByStatus(userId, billYear, billMonth, YesNoStatus.YES.getCode());
		vo.setHaspayAmount(hasAmount);
		return vo;
	}
}
