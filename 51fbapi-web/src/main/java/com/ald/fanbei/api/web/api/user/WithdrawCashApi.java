/**
 *
 */
package com.ald.fanbei.api.web.api.user;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.StringUtil;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.UserUtil;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @author suweili 2017年2月23日上午11:33:38
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("withdrawCashApi")
public class WithdrawCashApi implements ApiHandle {

    String zhifubao = "支付宝";
    String status = "APPLY";
    String zhifubaostatus = "APPLY";

    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfCashRecordService afCashRecordService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfRepaymentService afRepaymentService;
    @Resource
    AfRepaymentBorrowCashService afRepaymentBorrowCashService;
    @Resource
    AfResourceService afResourceService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long userId = context.getUserId();
        String account = ObjectUtils.toString(requestDataVo.getParams().get("account"));
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"));
        BigDecimal amount = new BigDecimal(ObjectUtils.toString(requestDataVo.getParams().get("amount")));
        String payPwd = ObjectUtils.toString(requestDataVo.getParams().get("payPwd"), "").toString();
        AfCashRecordDo afCashRecordDo = new AfCashRecordDo();

        //增加提现开关
        AfResourceDo resourceInfo = afResourceService.getSingleResourceBytype(Constants.RES_CASH_RECORD_SWITCH);
        if (resourceInfo != null && resourceInfo.getValue().equals(YesNoStatus.NO.getCode())) {
            throw new FanbeiException("cash switch is not open", FanbeiExceptionCode.CASH_LIMIT);
        }
        if (userId == null || StringUtils.isEmpty(account) || StringUtils.isEmpty(type)) {
            throw new FanbeiException("user id or account or type is  empty", FanbeiExceptionCode.PARAM_ERROR);
        }
        if (!StringUtils.equals(type, UserAccountLogType.JIFENBAO.getCode())
                && !StringUtils.equals(type, UserAccountLogType.CASH.getCode())) {
            throw new FanbeiException(" type is error", FanbeiExceptionCode.PARAM_ERROR);
        }

        AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (userAccountDo == null) {
            throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        String cashProcessingNO= afRepaymentBorrowCashService.getProcessingRepayNo(userId);
        if(StringUtil.isEmpty(cashProcessingNO)){
            String borrowProcessingNO= afRepaymentService.getProcessingRepayNo(userId);
            if(!StringUtil.isEmpty(borrowProcessingNO)){
                throw new FanbeiException("还款处理中,无法进行提现",true);
            }
        }else{
            throw new FanbeiException("还款处理中,无法进行提现",true);
        }


        AfUserBankcardDo afUserBankcardDo = null;
        if (StringUtils.equals(type, UserAccountLogType.CASH.getCode())) {
             if (userAccountDo.getRebateAmount().compareTo(amount) < 0) {
                throw new FanbeiException("apply cash amount more than account money",
                        FanbeiExceptionCode.APPLY_CASHED_AMOUNT_MORE_ACCOUNT);
            } else {
                AfResourceDo withDrawLimit = afResourceService.getSingleResourceBytype("config_withdraw_limit");
                if(withDrawLimit!=null){
                    if (amount.compareTo(new BigDecimal(withDrawLimit.getValue())) < 0) {
                        throw new FanbeiException("提现金额必须大于"+withDrawLimit.getValue(),true);
                    }
                }
                afUserBankcardDo = afUserBankcardService.getUserMainBankcardByUserId(userId);
                Long bankId = NumberUtil.objToLong(account);
                // 判断是否是本人主卡
                if (!bankId.equals(afUserBankcardDo.getRid())) {
                    throw new FanbeiException("the bank card must be main card",
                            FanbeiExceptionCode.APPLY_CASHED_BANK_ERROR);
                } else {
                    afCashRecordDo.setCardNumber(afUserBankcardDo.getCardNumber());
                    afCashRecordDo.setCardName(afUserBankcardDo.getBankName());
                }

            }
        } else if (StringUtils.equals(type, UserAccountLogType.JIFENBAO.getCode())) {
            if (amount.compareTo(new BigDecimal(2000)) < 0) {
                throw new FanbeiException("apply cash amount too samll",
                        FanbeiExceptionCode.APPLY_CASHED_AMOUNT_TOO_SMALL_JFB);
            } else if (userAccountDo.getJfbAmount().compareTo(amount) < 0) {
                throw new FanbeiException("apply cash amount more than account money",
                        FanbeiExceptionCode.APPLY_CASHED_AMOUNT_MORE_ACCOUNT);
            } else if (!CommonUtil.isMobile(account) && !CommonUtil.isEmail(account)) {
                throw new FanbeiException("apply zhifubao account error",
                        FanbeiExceptionCode.APPLY_CASHED_ZHIFUBAO_ERROR);
            } else {
                afCashRecordDo.setCardNumber(account);
                afCashRecordDo.setCardName(zhifubao);
                status = zhifubaostatus;
            }
        }

        String inputOldPwd = UserUtil.getPassword(payPwd, userAccountDo.getSalt());
        if (!StringUtils.equals(inputOldPwd, userAccountDo.getPassword())) {

            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_PAY_PASSWORD_INVALID_ERROR);
        }

        afCashRecordDo.setType(type);
        // afCashRecordDo.setAccount(account);
        afCashRecordDo.setAmount(amount);
        afCashRecordDo.setUserId(userId);
        afCashRecordDo.setStatus(status);

        if (afCashRecordService.addCashRecord(afCashRecordDo, afUserBankcardDo) > 0) {
            return resp;
        }
        throw new FanbeiException(FanbeiExceptionCode.FAILED);
    }

}
