package com.ald.fanbei.api.web.api.barlyClearance;

import com.ald.fanbei.api.biz.bo.barlyClearance.AllBarlyClearanceBo;
import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author honghzengpei 2017/11/28 13:31
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("submitClearApi")
public class SubmitClearApi implements ApiHandle {

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfUserCouponService afUserCouponService;

    @Resource
    AfUserAccountService afUserAccountService;
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        Long billId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("billId")), 0L);   //0 全部结清   其它订单结清
        BigDecimal repayAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("repayAmount")),BigDecimal.ZERO);         //帐单金额
        BigDecimal rebateAmount = NumberUtil.objToBigDecimalDefault(ObjectUtils.toString(requestDataVo.getParams().get("rabteAmount")),BigDecimal.ZERO);         //余额
        Long couponId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("couponId")), 0l);
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        Long cardId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("cardId")), 0l);


        AfUserAccountDo afUserAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        AfUserCouponDto coupon = afUserCouponService.getUserCouponById(couponId);
        if(null != coupon &&!coupon.getStatus().equals(CouponStatus.NOUSE.getCode())){
            throw new FanbeiException(FanbeiExceptionCode.USER_COUPON_ERROR);
        }


        List<AllBarlyClearanceBo> list = afBorrowBillService.getAllClear(userId,billId);
        if(!checkAmount(list,repayAmount)){
            throw new FanbeiException("borrow bill not exist error", FanbeiExceptionCode.AMOUNT_COMPARE_ERROR);
        }

        BigDecimal showAmount = repayAmount;  //实际支付金额
        if(coupon!=null){
            showAmount = BigDecimalUtil.subtract(showAmount, coupon.getAmount());
        }
        if(showAmount.compareTo(BigDecimal.ZERO)<=0){
            rebateAmount = BigDecimal.ZERO;
            showAmount = BigDecimal.ZERO;
        }

        if(showAmount.compareTo(BigDecimal.ZERO)!=0){
            //余额处理
            if(rebateAmount.compareTo(BigDecimal.ZERO)>0&&showAmount.compareTo(afUserAccountDo.getRebateAmount())>0){
                showAmount = BigDecimalUtil.subtract(showAmount, afUserAccountDo.getRebateAmount());
                rebateAmount = afUserAccountDo.getRebateAmount();
            }else if(rebateAmount.compareTo(BigDecimal.ZERO)>0 &&showAmount.compareTo(afUserAccountDo.getRebateAmount())<=0){
                rebateAmount = showAmount;
                showAmount = BigDecimal.ZERO;
            }
        }

        if(cardId>0 || cardId ==-2){//支付密码验证
            String inputOldPwd = UserUtil.getPassword(payPwd, afUserAccountDo.getSalt());
            if (!StringUtils.equals(inputOldPwd, afUserAccountDo.getPassword())) {
                return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
            }
        }

        







        return null;
    }


    /**
     * 金额check
     * @param list
     * @param repayAmont
     * @return
     */
    private boolean checkAmount(List<AllBarlyClearanceBo> list , BigDecimal repayAmont){
        Boolean ret = false;
        BigDecimal allAmount = BigDecimal.ZERO;
        for(AllBarlyClearanceBo allBarlyClearanceBo : list){
            allAmount = allAmount.add(allBarlyClearanceBo.getAmount());
        }
        if(allAmount.compareTo(repayAmont) ==0){
            ret = true;
        }
        return ret;
    }
}
