package com.ald.fanbei.api.web.api.bill;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowTempDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBillDetailInfoVo;

/**
 * 
 *@类描述：GetBillDetailInfoApi账单详情
 *@author 何鑫 2017年2月21日  17:25:48
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBillDetailInfoApi")
public class GetBillDetailInfoApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfBorrowService afBorrowService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfOrderService afOrderService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long billId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("billId")), 0);
		AfBorrowBillDto billDto = afBorrowBillService.getBorrowBillDtoById(billId);
		if(null == billDto){
			throw new FanbeiException("borrow bill not exist error", FanbeiExceptionCode.BORROW_BILL_NOT_EXIST_ERROR);
		}
		AfBillDetailInfoVo billVo = getBorrowBillVo(billDto);
		resp.setResponseData(billVo);
		return resp;
	}
	
	private AfBillDetailInfoVo getBorrowBillVo(AfBorrowBillDto billDto){
		AfBillDetailInfoVo vo = new AfBillDetailInfoVo();
		vo.setBillAmount(billDto.getBillAmount());
		vo.setBillId(billDto.getRid());
		vo.setBillNper(billDto.getBillNper());
		vo.setBorrowAmount(billDto.getPrincipleAmount());
		vo.setBorrowNo(billDto.getBorrowNo());
		vo.setRefId(billDto.getRepaymentId()+"");
		if(BorrowType.CASH.getCode().equals(billDto.getType())){
			vo.setBorrowType(BorrowType.CASH.getCode());
			int count = afBorrowService.getBorrowInterestCountByBorrowId(billDto.getRid());
			vo.setInterestDay(count-billDto.getOverdueDays());//总利息天数-逾期利息天数
		}else if(BorrowType.CONSUME.getCode().equals(billDto.getType())){
			vo.setBorrowType(BorrowType.CONSUME.getCode());
			vo.setInterestDay(0);
			vo.setOrderId(billDto.getOrderId());
			vo.setOrderNo(billDto.getOrderNo());
			if(billDto.getOrderId()>0){
				AfOrderDo order = afOrderService.getOrderInfoById(billDto.getOrderId(), billDto.getUserId());
				vo.setGoodsId(order.getGoodsId());
			}else{
				AfBorrowTempDo temp = afBorrowService.getBorrowTempByBorrowId(billDto.getBorrowId());
				if(null != temp){
					vo.setGoodsId(temp.getGoodsId());
				}
			}
		}
		if(billDto.getOverdueStatus().equals(YesNoStatus.YES.getCode())
				&& billDto.getStatus().equals(BorrowBillStatus.NO.getCode())){
			vo.setBillStatus(BorrowBillStatus.OVERDUE.getCode());
		}else{
			vo.setBillStatus(billDto.getStatus());
		}
		vo.setOverdueDay(billDto.getOverdueDays());
		vo.setGmtBorrow(billDto.getGmtBorrow());
		vo.setInterestAmount(billDto.getInterestAmount());
		vo.setName(billDto.getName());
		vo.setNper(billDto.getNper());
		vo.setOverdueAmount(billDto.getOverdueInterestAmount());
		vo.setPoundageAmount(billDto.getPoundageAmount());
		vo.setOverduePoundageAmount(billDto.getOverduePoundageAmount());
		vo.setRefundDate(afBorrowService.getReyLimitDate(billDto.getBillYear(),billDto.getBillMonth()));
		return vo;
	}
}
