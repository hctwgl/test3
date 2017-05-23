package com.ald.fanbei.api.web.api.repayment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * 
 *@类描述：还款提交Api
 *@author 何鑫 2017年2月18日  16:46:23
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitRepaymentApi")
public class SubmitRepaymentApi implements ApiHandle{

	@Resource
	private AfBorrowBillService afBorrowBillService;
	
	@Resource
	private AfUserCouponService afUserCouponService;
	
	@Resource
	private AfRepaymentService afRepaymentService;
	
	@Resource
	private AfUserAccountService afUserAccountService;
	
	@Resource
	private AfUserBankcardService afUserBankcardService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);
		String billIds = ObjectUtils.toString(requestDataVo.getParams().get("billIds"));
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
		BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
		Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		BigDecimal jfbAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("jfbAmount")), BigDecimal.ZERO);

		AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
		if (afUserAccountDo == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		
		if(cardId>0){//支付密码验证
			String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
			if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
				return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
			}
		}
		
		if(StringUtil.isEmpty(billIds)){
			throw new FanbeiException("borrow bill not exist error", FanbeiExceptionCode.BORROW_BILL_NOT_EXIST_ERROR);
		}
		
		AfBorrowBillDo billDo = afBorrowBillService.getBillAmountByIds(billIds);
		if(billDo.getCount()==0 ||repaymentAmount.compareTo(billDo.getBillAmount())!=0){
			logger.info("repaymentAmount="+repaymentAmount+",billDo="+billDo);
			throw new FanbeiException("borrow bill update error", FanbeiExceptionCode.BORROW_BILL_UPDATE_ERROR);
		}
		AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
		if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}
		Map<String,Object> map;
		if(cardId==-2){//余额支付
			map = afRepaymentService.createRepayment(jfbAmount,repaymentAmount, actualAmount,coupon, rebateAmount, billIds, 
					cardId,userId,billDo,"",afUserAccountDo);
			resp.addResponseData("refId", map.get("refId"));
			resp.addResponseData("type", map.get("type"));
		}else if(cardId==-1){//微信支付
			map = afRepaymentService.createRepayment(jfbAmount,repaymentAmount, actualAmount,coupon, rebateAmount, billIds, 
					cardId,userId,billDo,"",afUserAccountDo);
			resp.setResponseData(map);
		}else if(cardId>0){//银行卡支付
			AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(cardId);
			if(null == card){
				throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			map = afRepaymentService.createRepayment(jfbAmount,repaymentAmount, actualAmount,coupon, rebateAmount, billIds, 
					cardId,userId,billDo,request.getRemoteAddr(),afUserAccountDo);
			//代收
			UpsCollectRespBo upsResult = (UpsCollectRespBo) map.get("resp");
			if(!upsResult.isSuccess()){
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			Map<String,Object> newMap = new HashMap<String,Object>();
			newMap.put("outTradeNo", upsResult.getOrderNo());
			newMap.put("tradeNo", upsResult.getTradeNo());
			newMap.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
			newMap.put("refId", map.get("refId"));
			newMap.put("type", map.get("type"));
			resp.setResponseData(newMap);
		}
		return resp;
	}
	
}
