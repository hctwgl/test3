package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderRepaymentService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.BorrowCashRepayBo;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.BorrowCashRepayDoParam;

@Component("jsdBorrowCashRepayApi")
@Validator("borrowCashRepayDoParam")
public class JsdBorrowCashRepayApi implements JsdH5Handle {

    @Resource
    private JsdUserBankcardService jsdUserBankcardService;

    @Resource
    private JsdUserService jsdUserService;

    @Resource
    private  JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private  JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    private JsdBorrowCashRenewalService jsdBorrowCashRenewalService;

    @Resource
    private JsdBorrowLegalOrderRepaymentService jsdBorrowLegalOrderRepaymentService;
    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        Long userId = context.getUserId();
        JsdUserDo jsdUserDo = jsdUserService.getById(userId);

        BorrowCashRepayBo bo = this.extractAndCheck(context, userId);
        bo.userDo = jsdUserDo;
        bo.remoteIp = context.getClientIp();
        this.jsdBorrowCashRepaymentService.repay(bo,bo.payType);

        Map<String, Object> hashMap = new HashMap<String, Object>();
        String repaySMS="N";
        if(BankPayChannel.KUAIJIE.getCode().equals(bo.payType)){
            repaySMS="Y";
        }
        hashMap.put("repaySMS",repaySMS);
        resp.setData(hashMap);
        return resp;
    }



    private BorrowCashRepayBo extractAndCheck(Context context, Long userId) {
        BorrowCashRepayBo bo = new BorrowCashRepayBo();
        bo.userId = userId;
        BorrowCashRepayDoParam param = (BorrowCashRepayDoParam) context.getParamEntity();
        bo.amount = param.amount;
        bo.borrowNo = param.borrowNo;
        bo.bankNo = param.bankNo;
        bo.period = param.period;
        bo.repayNo=param.repayNo;
        checkPwdAndCard(bo);
        checkFrom(bo);
        return bo;
    }

    private void checkPwdAndCard(BorrowCashRepayBo bo) {
        HashMap<String,Object> map = jsdUserBankcardService.getBankByBankNoAndUserId(bo.userId,bo.bankNo);
        if (null == map) {
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        //还款金额是否大于银行单笔限额
//		dsedUserBankcardService.checkUpsBankLimit(map.get("bankCode").toString(), map.get("bankChannel").toString(), bo.amount);
        bo.cardName = map.get("bankName").toString();
        bo.payType=map.get("bankChannel").toString();
    }

    private void checkFrom(BorrowCashRepayBo bo) {
      JsdBorrowCashDo cashDo= jsdBorrowCashService.getByBorrowNo(bo.borrowNo);
      bo.borrowId=cashDo.getRid();
      if(cashDo  == null ){
            throw new FanbeiException("borrow cash not exist",FanbeiExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
      }
      if(!StringUtils.equals(cashDo.getStatus(), JsdBorrowCashStatus.TRANSFERRED.code)){
            throw new FanbeiException("borrow stats is not transfered",FanbeiExceptionCode.BORROW_STATS_IS_NOT_TRANSFERRED);
      }
      //检查处理中 还款 商品还款  续期
      JsdBorrowCashRepaymentDo repaymentDo= jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(cashDo.getRid());
      if(repaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(repaymentDo.getStatus())) {
            throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
        }
      JsdBorrowLegalOrderRepaymentDo orderRepaymentDo=jsdBorrowLegalOrderRepaymentService.getLastByBorrowId(cashDo.getRid());
      if(orderRepaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(orderRepaymentDo.getStatus())) {
           throw new FanbeiException(FanbeiExceptionCode.LEGAL_REPAY_PROCESS_ERROR);
      }
      JsdBorrowCashRenewalDo renewalDo= jsdBorrowCashRenewalService.getLastJsdRenewalByBorrowId(cashDo.getRid());
        if (renewalDo != null && JsdRenewalDetailStatus.PROCESS.getCode().equals(renewalDo.getStatus())) {
            throw new FanbeiException(FanbeiExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
        }
       //检查用户是否多还钱
        JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
        BigDecimal shouldRepayAmount = jsdBorrowLegalOrderCashService.calculateLegalRestAmount(cashDo, orderCashDo);
        if(bo.amount.compareTo(shouldRepayAmount) > 0) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
        }


    }



}
