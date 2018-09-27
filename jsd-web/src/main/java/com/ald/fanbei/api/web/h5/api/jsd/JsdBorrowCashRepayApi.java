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
import com.ald.fanbei.api.biz.service.impl.JsdBorrowCashRepaymentServiceImpl.RepayRequestBo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdRenewalDetailStatus;
import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
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

        RepayRequestBo bo = this.extractAndCheck(context, userId);
        bo.userDo = jsdUserDo;
        bo.remoteIp = context.getClientIp();
        Map<String, Object> hashMap=jsdBorrowCashRepaymentService.repay(bo,bo.payType);
        resp.setData(hashMap);
        return resp;
    }



    private RepayRequestBo extractAndCheck(Context context, Long userId) {
    	RepayRequestBo bo = new RepayRequestBo();
        bo.userId = userId;
        BorrowCashRepayDoParam param = (BorrowCashRepayDoParam) context.getParamEntity();
        bo.amount = param.amount;
        bo.borrowNo = param.borrowNo;
        bo.bankNo = param.bankNo;
        bo.period = param.period;
        bo.repayNo=param.repayNo;
        bo.name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
        checkPwdAndCard(bo);
        checkFrom(bo);
        return bo;
    }

    private void checkPwdAndCard(RepayRequestBo bo) {
        HashMap<String,Object> map = jsdUserBankcardService.getBankByBankNoAndUserId(bo.userId,bo.bankNo);
        if (null == map) {
            throw new BizException(BizExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        //还款金额是否大于银行单笔限额
        bo.cardName = map.get("bankName").toString();
        bo.payType = map.get("bankChannel").toString();
    }

    private void checkFrom(RepayRequestBo bo) {
        JsdBorrowCashRepaymentDo cashRepaymentDo=jsdBorrowCashRepaymentService.getByTradeNoXgxy(bo.repayNo);
        JsdBorrowLegalOrderRepaymentDo legalOrderRepaymentDo=jsdBorrowLegalOrderRepaymentService.getByTradeNoXgxy(bo.repayNo);
       if(cashRepaymentDo!=null&&legalOrderRepaymentDo!=null){
          throw new BizException(BizExceptionCode.JSD_REPAY_REPAY_ERROR);
       }
      JsdBorrowCashDo cashDo= jsdBorrowCashService.getByTradeNoXgxy(bo.borrowNo);
      if(cashDo  == null ){
    	  throw new BizException("borrow cash not exist",BizExceptionCode.BORROW_CASH_NOT_EXIST_ERROR);
      }
      bo.borrowId=cashDo.getRid();
      if(!StringUtils.equals(cashDo.getStatus(), JsdBorrowCashStatus.TRANSFERRED.name())){
            throw new BizException("borrow stats is not transfered",BizExceptionCode.BORROW_STATS_IS_NOT_TRANSFERRED);
      }
      //检查处理中 还款 商品还款  续期
      JsdBorrowCashRepaymentDo repaymentDo= jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(cashDo.getRid());
      if(repaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(repaymentDo.getStatus())) {
            throw new BizException(BizExceptionCode.LOAN_REPAY_PROCESS_ERROR);
      }
      JsdBorrowLegalOrderRepaymentDo orderRepaymentDo=jsdBorrowLegalOrderRepaymentService.getLastByBorrowId(cashDo.getRid());
      if(orderRepaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(orderRepaymentDo.getStatus())) {
           throw new BizException(BizExceptionCode.LEGAL_REPAY_PROCESS_ERROR);
      }
      JsdBorrowCashRenewalDo renewalDo= jsdBorrowCashRenewalService.getLastJsdRenewalByBorrowId(cashDo.getRid());
        if (renewalDo != null && JsdRenewalDetailStatus.PROCESS.getCode().equals(renewalDo.getStatus())) {
            throw new BizException(BizExceptionCode.HAVE_A_PROCESS_RENEWAL_DETAIL);
        }
       //检查用户是否多还钱
        JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(bo.borrowId);
        BigDecimal shouldRepayAmount = jsdBorrowLegalOrderCashService.calculateLegalRestAmount(cashDo, orderCashDo);
        if(bo.amount.compareTo(shouldRepayAmount) > 0) {
            throw new BizException(BizExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
        }


    }



}
