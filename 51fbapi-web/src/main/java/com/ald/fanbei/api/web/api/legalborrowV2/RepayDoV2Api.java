package com.ald.fanbei.api.web.api.legalborrowV2;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalRepaymentV2Service;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.impl.AfBorrowLegalRepaymentV2ServiceImpl.RepayBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.AfRenewalDetailStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.BorrowLegalRepayDoV2Param;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;

/**
 * @author zjf
 * @类描述：重构并参考 {@link com.ald.fanbei.api.web.api.repaycash.GetConfirmRepayInfoApi}
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("repayDoV2Api")
@Validator("legalRepayDoV2Param")
public class RepayDoV2Api implements ApiHandle {

    BigDecimal showAmount;

    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfUserBankcardService afUserBankcardService;

    @Resource
    AfBorrowLegalRepaymentV2Service afBorrowLegalRepaymentV2Service;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    AfBorrowCashDao afBorrowCashDao;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
    	String bankPayType = ObjectUtils.toString(requestDataVo.getParams().get("bankChannel"),null);    	
    	RepayBo bo = this.extractAndCheck(requestDataVo, context.getUserId());
        bo.remoteIp = CommonUtil.getIpAddr(request);

        String borrowProcessingNO = afRepaymentService.getProcessingRepayNo(context.getUserId());
        if (!StringUtil.isEmpty(borrowProcessingNO)) {
            throw new FanbeiException("分期还款处理中,无法进行还款操作", true);
        }

        Map<String, Object> data = this.afBorrowLegalRepaymentV2Service.repay(bo,bankPayType);
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(data);

        return resp;

    }

    private RepayBo extractAndCheck(RequestDataVo requestDataVo, Long userId) {
        AfUserAccountDo userDo = afUserAccountService.getUserAccountByUserId(userId);
        if (userDo == null) {
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        RepayBo bo = new RepayBo();
        bo.userId = userId;
        bo.userDo = userDo;

        BorrowLegalRepayDoV2Param param = (BorrowLegalRepayDoV2Param) requestDataVo.getParamObj();
        bo.repaymentAmount = param.repaymentAmount;
        bo.rebateAmount = param.rebateAmount;
        bo.actualAmount = param.actualAmount;
        bo.payPwd = param.payPwd;
        bo.cardId = param.cardId;
        bo.couponId = param.couponId;
        bo.borrowId = param.borrowId;

        if (bo.cardId == -1) {// -1-微信支付，-2余额支付，>0卡支付（包含组合支付）
            throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
        }
        if (bo.cardId == -3) {// -3支付宝支付
            throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
        }

        checkFrom(bo);
        checkPwdAndCard(bo);
        checkAmount(bo);

        return bo;
    }

    private void checkFrom(RepayBo bo) {
        AfBorrowCashDo cashDo = null;
        if (bo.borrowId > 0
                && (cashDo = afBorrowCashDao.getBorrowCashByrid(bo.borrowId)) != null) {
        } else {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        }
        bo.cashDo = cashDo;

        // 检查当前 借款 是否已在处理中
        AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(bo.borrowId);
        if (rbCashDo != null && AfBorrowCashRepmentStatus.PROCESS.getCode().equals(rbCashDo.getStatus())) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
        }

        // 检查 当前借款 是否在续期操作中
        AfRenewalDetailDo lastAfRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(bo.borrowId);
        if (lastAfRenewalDetailDo != null && AfRenewalDetailStatus.PROCESS.getCode().equals(lastAfRenewalDetailDo.getStatus())) {
            throw new FanbeiException(FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
        }

        // 检查 用户 是否多还钱
        BigDecimal shouldRepayAmount = afBorrowLegalRepaymentV2Service.calculateRestAmount(cashDo);
        if (bo.repaymentAmount.compareTo(shouldRepayAmount) > 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
        }

    }

    private void checkPwdAndCard(RepayBo bo) {
        if (bo.cardId == -2 || bo.cardId > 0) { // -1-微信支付，-3支付宝支付，-2余额支付，>0卡支付（包含组合支付）
            String finalPwd = UserUtil.getPassword(bo.payPwd, bo.userDo.getSalt());
            if (!StringUtils.equals(finalPwd, bo.userDo.getPassword())) {
                throw new FanbeiException("Password is error", FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }

            bo.cardName = Constants.DEFAULT_USER_ACCOUNT;

            if (bo.cardId > 0) {
                AfUserBankcardDo card = afUserBankcardService.getUserBankcardById(bo.cardId);
                if (null == card) {
                    throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
                }
                
                //还款金额是否大于银行单笔限额
		afUserBankcardService.checkUpsBankLimit(card.getBankCode(),card.getBankChannel(), bo.actualAmount);
		
                bo.cardName = card.getBankName();
                bo.cardNo = card.getCardNumber();
            }
        }
    }

    private void checkAmount(RepayBo bo) {
        AfUserCouponDto userCouponDto = afUserCouponService.getUserCouponById(bo.couponId);
        bo.userCouponDto = userCouponDto;
        if (null != userCouponDto && !userCouponDto.getStatus().equals(CouponStatus.NOUSE.getCode())) {
            logger.error("extractAndCheckParams.coupon" + JSON.toJSONString(userCouponDto));
            throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
        }

        //用户账户余额校验添加
        if (bo.userDo.getRebateAmount().compareTo(bo.rebateAmount) < 0) {
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_MONEY_LESS);
        }

        BigDecimal calculateAmount = bo.repaymentAmount;

        // 使用优惠券结算金额
        if (userCouponDto != null) {
            calculateAmount = BigDecimalUtil.subtract(bo.repaymentAmount, userCouponDto.getAmount());
            if (calculateAmount.compareTo(BigDecimal.ZERO) <= 0) {
                logger.info(bo.userDo.getUserName() + "coupon repayment");
                bo.rebateAmount = BigDecimal.ZERO;
                calculateAmount = BigDecimal.ZERO;
            }
        }

        // 余额处理
        if (bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(bo.userDo.getRebateAmount()) > 0) {
            calculateAmount = BigDecimalUtil.subtract(calculateAmount, bo.userDo.getRebateAmount());
            bo.rebateAmount = bo.userDo.getRebateAmount();
        } else if (bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0 && calculateAmount.compareTo(bo.userDo.getRebateAmount()) <= 0) {
            bo.rebateAmount = calculateAmount;
            calculateAmount = BigDecimal.ZERO;
        }

        //如果用户选择余额支付，则actualAmount必须为0，否则抛异（支付漏洞）
        if (bo.cardId == -2 && bo.actualAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REBATE_ERROR);
        }

        // 对比
        if (bo.actualAmount.compareTo(calculateAmount) != 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
        }

    }

}
