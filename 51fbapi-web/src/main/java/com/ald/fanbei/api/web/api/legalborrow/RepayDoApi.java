package com.ald.fanbei.api.web.api.legalborrow;

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
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepayFromEnum;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @author zjf
 * @类描述：参考 {@link com.ald.fanbei.api.web.api.repaycash.GetConfirmRepayInfoApi}
 * 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("repayDoApi")
public class RepayDoApi implements ApiHandle {
	
	BigDecimal showAmount;
	
	@Resource
	AfUserCouponService afUserCouponService;
	@Resource
	AfUserAccountService afUserAccountService;
	@Resource
	AfUserBankcardService afUserBankcardService;
	@Resource
	AfRepaymentBorrowCashService afRepaymentBorrowCashService;

	@Resource
	AfBorrowCashService afBorrowCashService;
	@Resource
	AfRenewalDetailService afRenewalDetailService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		Long userId = context.getUserId();
		AfUserAccountDo userDto = afUserAccountService.getUserAccountByUserId(userId);
		if (userDto == null) {
			throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		
		String remoteIp = CommonUtil.getIpAddr(request);
		
		RepayBo bo = extractAndCheckParams(requestDataVo, userDto);
		
		return repay(requestDataVo, userDto, remoteIp, bo);
		
	}
	
	private ApiHandleResponse repay(RequestDataVo requestDataVo, AfUserAccountDo userDto, String remoteIp, RepayBo bo) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		
		if(AfBorrowLegalRepayFromEnum.INDEX.name().equalsIgnoreCase(bo.from)) {
			
			
		}else if (AfBorrowLegalRepayFromEnum.BORROW.name().equalsIgnoreCase(bo.from)) {
			//repayBorrowCash();
		}else if(AfBorrowLegalRepayFromEnum.ORDER.name().equalsIgnoreCase(bo.from)) {
			//repayOrderCash();
		}
		// TODO 还款借款
		// TODO 还款订单欠款
		
		return resp;
	}
	
	private ApiHandleResponse repayBorrowCash(String requestDataId, AfUserAccountDo userDto, String remoteIp, RepayBo bo) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataId, FanbeiExceptionCode.SUCCESS);
		Map<String, Object> map = null;
		Long cardId = bo.cardId;
		
		if (cardId == -2) {		// 余额支付
			map = afRepaymentBorrowCashService.createRepayment(BigDecimal.ZERO,
					bo.repaymentAmount, bo.actualAmount, bo.coupon, bo.rebateAmount,
					bo.borrowId, cardId, bo.userId, remoteIp, userDto);

			resp.addResponseData("refId", map.get("refId"));
			resp.addResponseData("type", map.get("type"));
		} else if (cardId > 0) {// 银行卡支付
			AfUserBankcardDo card = afUserBankcardService
					.getUserBankcardById(cardId);
			if (null == card) {
				throw new FanbeiException(
						FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			}
			map = afRepaymentBorrowCashService.createRepayment(BigDecimal.ZERO,
					bo.repaymentAmount, bo.actualAmount, bo.coupon, bo.rebateAmount,
					bo.borrowId, cardId, bo.userId, remoteIp, userDto);

			validThirdReqExistFanbeiError(map);
			
			// 代收
			UpsCollectRespBo upsResult = null;
			if (map.get("resp") != null && map.get("resp") instanceof UpsCollectRespBo) {
				upsResult = (UpsCollectRespBo) map.get("resp");
			}

			if (upsResult == null || !upsResult.isSuccess()) {
				throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("outTradeNo", upsResult.getOrderNo());
			newMap.put("tradeNo", upsResult.getTradeNo());
			newMap.put("cardNo", Base64.encodeString(upsResult.getCardNo()));
			newMap.put("refId", map.get("refId"));
			newMap.put("type", map.get("type"));

			resp.setResponseData(newMap);
		} else if (cardId == -1) {// 微信支付
			return new ApiHandleResponse(requestDataId, FanbeiExceptionCode.WEBCHAT_NOT_USERD);
		} else if (cardId == -3) {// 支付宝支付
			return new ApiHandleResponse(requestDataId, FanbeiExceptionCode.ZFB_NOT_USERD);
		} 
		
		// 在返回前进行返呗内部异常捕获校验并向用户反馈
		validThirdReqExistFanbeiError(map);
		// 向客户端反馈结果
		return resp;
	}
	
	private void repayOrderCash(RequestDataVo requestDataVo, AfUserAccountDo userDto, String remoteIp, RepayBo bo) {
		return;
	}
	
	private RepayBo extractAndCheckParams(RequestDataVo requestDataVo, AfUserAccountDo userDto) {
		Long userId = userDto.getUserId();
		BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);

		Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
		Long borrowId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowId")), 0l);
		String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
		Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")),0l);

		
		/* start 检查参数 */
		if (borrowId == 0) throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
		// 判断当前借款是否已在处理中
		AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(borrowId);
		if(rbCashDo == null || AfBorrowCashRepmentStatus.PROCESS.getCode().equals(rbCashDo.getStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
		}
		
		// 判断是否存在续期处理中的记录,防止续期和还款交叉,导致最后记录更新失败
		AfRenewalDetailDo lastAfRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(borrowId);
		if (lastAfRenewalDetailDo == null || AfRenewalDetailStatus.PROCESS.getCode().equals(lastAfRenewalDetailDo.getStatus())) {
			throw new FanbeiException(FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
		}

		// -1-微信支付，-3支付宝支付，-2余额支付，>0卡支付（包含组合支付）
		if (cardId == -2 || cardId > 0) { 
			String finalPwd = UserUtil.getPassword(payPwd, userDto.getSalt());
			if (!StringUtils.equals(finalPwd, userDto.getPassword())) {
				throw new FanbeiException("Password is error",FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
			}
		}

		AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
		if (null == coupon || !coupon.getStatus().equals(CouponStatus.NOUSE.getCode())) {
			logger.error("extractAndCheckParams.coupon" + JSON.toJSONString(coupon));
			throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
		}

		AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
		if (afBorrowCashDo != null) {
			BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), 	//借款总额
							afBorrowCashDo.getSumOverdue(), 						//累计滞纳金
							afBorrowCashDo.getOverdueAmount(),						//逾期费
							afBorrowCashDo.getRateAmount(),							//利率费
							afBorrowCashDo.getSumRate());							//累计利息

			BigDecimal temAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount());
			if (temAmount.compareTo(repaymentAmount) < 0) {
				throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
			}
		}
		
		//用户账户余额校验添加
		BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);
        if(userDto.getRebateAmount().compareTo(rebateAmount)<0){
        	throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_MONEY_LESS);
        }
		/* end 检查参数 */
		
		
		/* start 后端再次检查用户实际支付金额 是否匹配  */
		BigDecimal calculateAmount = repaymentAmount;
		
		// 使用优惠券结算金额
		if (coupon != null) {
			calculateAmount = BigDecimalUtil.subtract(repaymentAmount, coupon.getAmount());
			if (calculateAmount.compareTo(BigDecimal.ZERO) <= 0) {
				logger.info(userDto.getUserName() + "coupon repayment");
				rebateAmount = BigDecimal.ZERO;
				calculateAmount = BigDecimal.ZERO;
			}
		}
		
		// 余额处理
		if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(userDto.getRebateAmount()) > 0) {
			calculateAmount = BigDecimalUtil.subtract(calculateAmount, userDto.getRebateAmount());
			rebateAmount = userDto.getRebateAmount();
		} else if (rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(userDto.getRebateAmount()) <= 0) {
			rebateAmount = calculateAmount;
			calculateAmount = BigDecimal.ZERO;
		}
		
		// 对比
		BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);
		if (actualAmount.compareTo(calculateAmount) != 0) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
		}
		/* end 后端再次检查用户实际支付金额 是否匹配  */
		
		
		Long borrowOrderId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("borrowOrderId")), 0l);
		String from = ObjectUtils.toString(requestDataVo.getParams().get("from"), "").toString();
		RepayBo bo = new RepayBo();
		bo.userId = userId;
		bo.repaymentAmount = repaymentAmount;
		bo.rebateAmount = rebateAmount;
		bo.actualAmount = actualAmount;
		bo.payPwd = payPwd;
		bo.cardId = cardId;
		bo.couponId = couponId;
		bo.borrowId = borrowId;
		bo.borrowOrderId = borrowOrderId;
		bo.from = from;
		
		bo.coupon = coupon;
		
		return null;
	}
	
	private void validThirdReqExistFanbeiError(Map<String, Object> map) {
		if (map != null
				&& map.get(Constants.THIRD_REQ_EXCEP_KEY) != null
				&& (map.get(Constants.THIRD_REQ_EXCEP_KEY) instanceof FanbeiException)) {
			FanbeiException reqExp = (FanbeiException) map
					.get(Constants.THIRD_REQ_EXCEP_KEY);
			logger.error("validThirdReqExistFanbeiError exist error and throw,reqExpmessage="
					+ reqExp.getErrorCode() != null ? reqExp.getErrorCode()
					.getDesc() : "");
			throw reqExp;
		}
	}
	
	static class RepayBo{
		Long userId;
		
		/* request字段 */
		BigDecimal repaymentAmount;
		BigDecimal actualAmount;
		BigDecimal rebateAmount; //可选字段
		String payPwd;
		Long cardId;
		Long couponId;
		Long borrowId;
		Long borrowOrderId; //可选字段
		String from;
		/* request字段 */
		
		/* biz 业务处理字段 */
		AfUserCouponDto coupon; //可选字段
		AfUserAccountDo userDto;
		
	}
}
