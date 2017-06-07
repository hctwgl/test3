package com.ald.fanbei.api.web.api.repayment;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfLimitDetailInfoVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 
 *@类描述：获取明细详情Api
 *@author 何鑫 2017年2月23日  17:19:34
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLimitDetailInfoApi")
public class GetLimitDetailInfoApi implements ApiHandle{

	@Resource
	private AfBorrowService afBorrowService;
	
	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfRepaymentService afRepaymentService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {

		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long refId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("refId")), 0l);
		String type = ObjectUtils.toString(requestDataVo.getParams().get("type"),"");
		AfLimitDetailInfoVo info;
		if(type.equals(UserAccountLogType.REPAYMENT.getCode())){
			AfRepaymentDo repayment = afRepaymentService.getRepaymentById(refId);
			if(null == repayment){
				throw new FanbeiException("repayment detail not exist error", FanbeiExceptionCode.REPAYMENT_DETAIL_NOT_EXIST_ERROR);
			}
			info = getLimitDetailInfoVo(repayment);
		}else{
			AfBorrowDo borrow = afBorrowService.getBorrowById(refId);
			if(null == borrow){
				throw new FanbeiException("borrow detail not exist error", FanbeiExceptionCode.BORROW_DETAIL_NOT_EXIST_ERROR);
			}
			info = getLimitDetailInfoVo(borrow);
		}
		resp.setResponseData(info);
		return resp;
	}
	
	private AfLimitDetailInfoVo getLimitDetailInfoVo(AfBorrowDo borrow){
		AfLimitDetailInfoVo detailInfo = new AfLimitDetailInfoVo();
		detailInfo.setAmount(borrow.getAmount());
		detailInfo.setNper(borrow.getNper());
		detailInfo.setNperRepayment(borrow.getNperRepayment());
		detailInfo.setRepayPrinAmount(borrow.getRepayPrinAmount());
		detailInfo.setNumber(borrow.getBorrowNo());
		detailInfo.setGmtCreate(borrow.getGmtCreate());
		detailInfo.setCardName(borrow.getCardName());
		detailInfo.setCardNo(StringUtil.getLastString(borrow.getCardNumber(), 4));
		detailInfo.setStatus(borrow.getStatus());
		if(BorrowType.CASH.getCode().equals(borrow.getType())||BorrowType.TOCASH.getCode().equals(borrow.getType())){
			detailInfo.setBorrowDay((int) ((DateUtil.getStartOfDate(DateUtil.addDays(new Date(), 1)).getTime() - DateUtil.getStartOfDate(borrow.getGmtCreate()).getTime()) / (1000*3600*24)));
			detailInfo.setPayAmount(afBorrowBillService.getBorrowBillByBorrowId(borrow.getRid()));
		}else{
			detailInfo.setBorrowDetail(new StringBuffer(borrow.getName()).append(borrow.getOrderNo()).toString());

		}
		List<AfBorrowBillDo> BorrowBillList = afBorrowBillService.getAllBorrowBillByBorrowId(borrow.getRid());
		BigDecimal payAmount = new BigDecimal(BigDecimal.ZERO.intValue());	//计息分期金额
		BigDecimal freePerAmount = new BigDecimal(BigDecimal.ZERO.intValue());//免息分期金额
		if(CollectionUtil.isEmpty(BorrowBillList)){
			payAmount = borrow.getNperAmount();
		}else{
			for(AfBorrowBillDo afBorrowBillDo : BorrowBillList){
				String IsFreeInterest = afBorrowBillDo.getIsFreeInterest();
				if(Constants.ISFREEINTEREST_Y.equals(IsFreeInterest)){
					freePerAmount.add(afBorrowBillDo.getBillAmount());
				}else{
					payAmount.add(afBorrowBillDo.getBillAmount());
				}
			}
		}
		detailInfo.setPerAmount(payAmount);
		detailInfo.setFreePerAmount(freePerAmount);
		return detailInfo;
	}
	
	private AfLimitDetailInfoVo getLimitDetailInfoVo(AfRepaymentDo repayment){
		AfLimitDetailInfoVo detailInfo = new AfLimitDetailInfoVo();
		detailInfo.setActualAmount(repayment.getActualAmount());
		detailInfo.setAmount(repayment.getRepaymentAmount());
		detailInfo.setCardName("");
		detailInfo.setCardNo("");
		detailInfo.setCouponAmount(repayment.getCouponAmount());
		detailInfo.setGmtCreate(repayment.getGmtCreate());
		detailInfo.setName(repayment.getName());
		detailInfo.setNumber(repayment.getRepayNo());
		detailInfo.setRebateAmount(repayment.getRebateAmount());
		detailInfo.setCardName(repayment.getCardName());
		detailInfo.setCardNo(StringUtil.getLastString(repayment.getCardNumber(), 4));
		detailInfo.setStatus(repayment.getStatus());
		detailInfo.setJfbAmount(repayment.getJfbAmount().intValue());
		
		return detailInfo;
	}
}
