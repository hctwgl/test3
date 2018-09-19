package com.ald.jsd.mgr.biz.service.impl;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.jsd.mgr.biz.service.MgrOfflineRepaymentService;
import com.ald.jsd.mgr.enums.RespCode;
import com.ald.jsd.mgr.web.dto.resp.Resp;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;

public class MgrOfflineRepaymentServiceImpl implements MgrOfflineRepaymentService {

    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;


    @Override
    public Resp dealOfflineRepayment(Map<String,String> data) {
        Resp resp=new Resp();
        String borrowNo= data.get("borrowNo");
        String repaymentDate=  data.get("repaymentDate");
        String channel= data.get("channel");
        String tradeNo=  data.get("tradeNo");
        String amount=  data.get("amount");
        String remark=  data.get("remark");
        if(StringUtil.isAllNotEmpty(borrowNo,channel,tradeNo,amount)){
            JsdBorrowCashDo borrowCashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
            if(borrowCashDo==null){
                return resp.fail(data,RespCode.BORROW_OFF_IS_NULL.code, RespCode.BORROW_OFF_IS_NULL.desc);
            }
            JsdBorrowLegalOrderCashDo legalOrderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
            if(repaymentDate==null){
                repaymentDate= DateUtil.getNow();
            }
            String dataId= String.valueOf(borrowCashDo.getRid()+borrowCashDo.getRenewalNum());
            jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo,legalOrderCashDo,amount,tradeNo,borrowCashDo.getUserId(), JsdRepayType.ONLINE,channel,repaymentDate,null,dataId,remark);
        }else {
            return resp.fail(data,RespCode.PARAMS_ERROR.code,RespCode.PARAMS_ERROR.desc);
        }
        return null;
    }

}
