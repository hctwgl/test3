package com.ald.fanbei.api.web.h5.api.recycle;

import java.math.BigDecimal;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowRecycleRepaymentService;
import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.biz.service.AfRepaymentBorrowCashService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.biz.service.impl.AfBorrowRecycleRepaymentServiceImpl.RepayBo;
import com.ald.fanbei.api.common.enums.AfBorrowCashRepmentStatus;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfRepaymentBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.RecycleRepayDoParam;
import com.alibaba.fastjson.JSON;

/**
 * @author yanghailong
 * @类描述：回收还款
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("recycleRepayDoApi")
@Validator("recycleRepayDoParam")
public class RecycleRepayDoApi implements H5Handle {

    @Resource
    AfUserCouponService afUserCouponService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfUserBankcardService afUserBankcardService;

    @Resource
    AfBorrowRecycleRepaymentService afBorrowRecycleRepaymentService;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfRenewalDetailService afRenewalDetailService;
    @Resource
    AfBorrowCashDao afBorrowCashDao;

    @Override
	public H5HandleResponse process(Context context) {
    	String bankPayType = ObjectUtils.toString(context.getData("bankChannel"),null);
    	RepayBo bo = this.extractAndCheck(context);
        bo.remoteIp = context.getClientIp();

        String borrowProcessingNO = afRepaymentService.getProcessingRepayNo(context.getUserId());
        if (!StringUtil.isEmpty(borrowProcessingNO)) {
            throw new FanbeiException("分期还款处理中,无法进行还款操作", true);
        }

        Map<String, Object> data = this.afBorrowRecycleRepaymentService.repay(bo,bankPayType);
        
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(data);

        return resp;

    }

    private RepayBo extractAndCheck(Context context) {
        AfUserAccountDo userDo = afUserAccountService.getUserAccountByUserId(context.getUserId());
        if (userDo == null) {
            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        RepayBo bo = new RepayBo();
        bo.userId = context.getUserId();
        bo.userDo = userDo;

        RecycleRepayDoParam param = (RecycleRepayDoParam) context.getParamEntity();
        bo.repaymentAmount = param.repaymentAmount;
        bo.actualAmount = param.repaymentAmount;
        bo.payPwd = param.payPwd;
        bo.cardId = param.cardId;
        bo.borrowId = param.borrowId;

//        if (bo.cardId == -1) {// -1-微信支付，-2余额支付，>0卡支付（包含组合支付）
//            throw new FanbeiException(FanbeiExceptionCode.WEBCHAT_NOT_USERD);
//        }
//        if (bo.cardId == -3) {// -3支付宝支付
//            throw new FanbeiException(FanbeiExceptionCode.ZFB_NOT_USERD);
//        }

        if (bo.cardId <= 0) {// -1-微信支付，-2余额支付，>0卡支付（包含组合支付）
            throw new FanbeiException(FanbeiExceptionCode.BANK_REPAY_ERROR);
        }
        
        checkFrom(bo);
        checkPwdAndCard(bo);
        //checkAmount(bo);

        return bo;
    }

    private void checkFrom(RepayBo bo) {
        AfBorrowCashDo cashDo = null;
        if (bo.borrowId > 0  && (cashDo = afBorrowCashDao.getBorrowCashByrid(bo.borrowId)) == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
        }
        bo.cashDo = cashDo;

        // 检查当前 借款 是否已在处理中
        AfRepaymentBorrowCashDo rbCashDo = afRepaymentBorrowCashService.getLastRepaymentBorrowCashByBorrowId(bo.borrowId);
        if (rbCashDo != null && AfBorrowCashRepmentStatus.PROCESS.getCode().equals(rbCashDo.getStatus())) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_PROCESS_ERROR);
        }

        // 检查 当前借款 是否在续期操作中
//        AfRenewalDetailDo lastAfRenewalDetailDo = afRenewalDetailService.getRenewalDetailByBorrowId(bo.borrowId);
//        if (lastAfRenewalDetailDo != null && AfRenewalDetailStatus.PROCESS.getCode().equals(lastAfRenewalDetailDo.getStatus())) {
//            throw new FanbeiException(FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
//        }

        // 检查 用户 是否多还钱
        BigDecimal shouldRepayAmount = afBorrowRecycleRepaymentService.calculateRestAmount(cashDo);
        if (bo.repaymentAmount.compareTo(shouldRepayAmount) > 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
        }

    }

    private void checkPwdAndCard(RepayBo bo) {
        if (bo.cardId > 0) { // -1-微信支付，-3支付宝支付，-2余额支付，>0卡支付（包含组合支付）
            String finalPwd = UserUtil.getPassword(bo.payPwd, bo.userDo.getSalt());
            if (!StringUtils.equals(finalPwd, bo.userDo.getPassword())) {
                throw new FanbeiException("Password is error", FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }

            //bo.cardName = Constants.DEFAULT_USER_ACCOUNT;

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
