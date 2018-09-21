package com.ald.jsd.mgr.biz.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.jsd.mgr.biz.service.MgrOfflineRepaymentService;

@Component
public class MgrOfflineRepaymentServiceImpl implements MgrOfflineRepaymentService {

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;


    @Override
    public void dealOfflineRepayment(Map<String,String> reqData, JsdRepayType repayType) {
        String borrowNo= reqData.get("borrowNo");
        Date repaymentDate=new Date(Long.parseLong(reqData.get("repaymentDate")));
        String channel= reqData.get("channel");
        String tradeNo=  reqData.get("tradeNo");
        String amount=  reqData.get("amount");
        String remark=  reqData.get("remark");
        JsdBorrowCashDo borrowCashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
        JsdBorrowLegalOrderCashDo legalOrderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
        String dataId= String.valueOf(borrowCashDo.getRid()+borrowCashDo.getRenewalNum());
        jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo,legalOrderCashDo,amount,tradeNo,borrowCashDo.getUserId(), repayType, channel,repaymentDate,null,dataId,remark);
    }
}
