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
    public void dealOfflineRepayment(Map<String,String> data) {
        String borrowNo= data.get("borrowNo");
        Date repaymentDate=new Date(Long.parseLong(data.get("repaymentDate")));
        String channel= data.get("channel");
        String tradeNo=  data.get("tradeNo");
        String amount=  data.get("amount");
        String remark=  data.get("remark");
        JsdBorrowCashDo borrowCashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
        JsdBorrowLegalOrderCashDo legalOrderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
        String dataId= String.valueOf(borrowCashDo.getRid()+borrowCashDo.getRenewalNum());
        jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo,legalOrderCashDo,amount,tradeNo,borrowCashDo.getUserId(), JsdRepayType.ONLINE,channel,repaymentDate,null,dataId,remark);
    }
}
