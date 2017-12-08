package com.ald.fanbei.api.web.api.repayment;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfRepaymentService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author honghzengpei 2017/9/4 20:44
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 * @deprecated 见 {@link SubmitRepaymentByYiBaoApi}
 */
@Component("submitRepaymentV1Api")
public class SubmitRepaymentV1Api implements ApiHandle {

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
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        BigDecimal repaymentAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repaymentAmount")), BigDecimal.ZERO);    //还款总金额
        BigDecimal actualAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("actualAmount")), BigDecimal.ZERO);           //实际支付金额
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
        BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rebateAmount")), BigDecimal.ZERO);           //使用余额--反现
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();


//        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
//        if (afUserAccountDo == null) {
//            throw new FanbeiException("Account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
//        }
//
//        if(cardId>0){//支付密码验证
//            String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
//            if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
//                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
//            }
//        }
//
//        //todo hzp 判断是否存在还款中的帐单
//        //return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.BORROW_BILL_IS_REPAYING);
//
//        AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
//        if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
//            throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
//        }
//
//        BigDecimal showAmount;  //计算用户需要支付的金额
//        showAmount = repaymentAmount;
//        //使用优惠券结算金额
//        if(coupon!=null){
//            showAmount = BigDecimalUtil.subtract(repaymentAmount, coupon.getAmount());
//        }
//        //优惠券金额大于还款金额其他数据处理
//        if(showAmount.compareTo(BigDecimal.ZERO)<=0){
//            logger.info(afUserAccountDo.getUserName()+"coupon repayment");
//            rebateAmount = BigDecimal.ZERO;
//            showAmount = BigDecimal.ZERO;
//        }
//
//        //余额处理
//        if(showAmount.compareTo(BigDecimal.ZERO)>0 && rebateAmount.compareTo(BigDecimal.ZERO)>0){
//            if(afUserAccountDo.getRebateAmount().compareTo(showAmount)>0){
//                rebateAmount =afUserAccountDo.getRebateAmount().subtract(showAmount);
//                showAmount = BigDecimal.ZERO;
//            }
//            else{
//                rebateAmount = afUserAccountDo.getRebateAmount();
//                showAmount = showAmount.subtract(afUserAccountDo.getRebateAmount());
//            }
//        }
//        if(actualAmount.compareTo(showAmount)!=0){
//            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
//        }
//        Map<String,Object> map;


        return resp;
    }

    /**
     * 查看是否存在还款中的账单
     * @param borrowBillList
     * @return
     */
    private boolean constainsRepayingBill(List<AfBorrowBillDo> borrowBillList) {
        if (CollectionUtil.isEmpty(borrowBillList)) {
            return false;
        }
        boolean constainRepaying = false;
        for (AfBorrowBillDo borrowBillInfo : borrowBillList) {
            if (BorrowBillStatus.DEALING.getCode().equals(borrowBillInfo.getStatus())) {
                constainRepaying = true;
                break;
            }
        }
        return constainRepaying;
    }
}
