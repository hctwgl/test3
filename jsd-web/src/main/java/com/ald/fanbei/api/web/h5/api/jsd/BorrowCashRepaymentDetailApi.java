package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

@Component("borrowCashRepaymentDetailApi")
public class BorrowCashRepaymentDetailApi implements JsdH5Handle {


    @Resource
    private JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;

    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");

        String borrowNo = ObjectUtils.toString(context.getData("borrowNo"), null);
        String period = ObjectUtils.toString(context.getData("period"), null);
        Long timestamp = Long.valueOf(context.getData("timestamp").toString());
        JsdBorrowCashDo cashDo=jsdBorrowCashService.getByBorrowNo(borrowNo);
        if(cashDo==null){
            throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_IS_NULL);
        }
        JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());
        BorrowAmount bo=new BorrowAmount();
        calculateAmount(bo,cashDo,orderCashDo);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("amount",bo.amount);
        map.put("remark",bo.remark);
        map.put("timestamp",timestamp);
        resp.setData(map);
        return resp;
    }

    private void calculateAmount(BorrowAmount bo,JsdBorrowCashDo cashDo, JsdBorrowLegalOrderCashDo orderCashDo){

        bo.cashAmount=BigDecimal.ZERO;
        bo.cashPoundage=BigDecimal.ZERO;
        bo.principle=BigDecimal.ZERO;
        bo.amount=BigDecimal.ZERO;
        bo.orderAmount=BigDecimal.ZERO;
        bo.orderPoundage=BigDecimal.ZERO;

        bo.cashAmount=bo.cashAmount.add(cashDo.getAmount()).add(cashDo.getOverdueAmount()).add(cashDo.getPoundage()).add(cashDo.getRateAmount()).add(cashDo.getSumOverdue())
                .add(cashDo.getSumRate()).add(cashDo.getSumRenewalPoundage()).subtract(cashDo.getRepayAmount());
        bo.cashPoundage=bo.cashPoundage.add(cashDo.getOverdueAmount()).add(cashDo.getPoundage()).add(cashDo.getRateAmount());
        bo.principle=bo.principle.add(cashDo.getAmount()).subtract(cashDo.getRepayPrinciple());
        if(orderCashDo!=null){
            bo.amount=bo.cashAmount.add(orderCashDo.getAmount()).add(orderCashDo.getOverdueAmount()).add(orderCashDo.getPoundageAmount()).add(orderCashDo.getInterestAmount())
                    .add(orderCashDo.getSumRepaidInterest()).add(orderCashDo.getSumRepaidOverdue()).add(orderCashDo.getSumRepaidPoundage()).subtract(orderCashDo.getRepaidAmount());
            bo.orderAmount=bo.orderAmount.add(orderCashDo.getAmount()).add(orderCashDo.getSumRepaidInterest()).add(orderCashDo.getSumRepaidOverdue()).add(orderCashDo.getSumRepaidPoundage()).subtract(orderCashDo.getRepaidAmount());
            bo.orderPoundage=bo.orderPoundage.add(orderCashDo.getInterestAmount()).add(orderCashDo.getOverdueAmount()).add(orderCashDo.getPoundageAmount());
        }
        bo.remark="费用明细: 1.其中含借款本金"+bo.principle+"元，利息&手续费&逾期费"+bo.cashPoundage+"元。2.商品金额为"+bo.orderAmount+"元，利息&手续费&逾期费"+bo.orderPoundage+"元。";

    }

    public class BorrowAmount{

        public BigDecimal cashAmount;
        public BigDecimal cashPoundage;
        public BigDecimal principle;
        public BigDecimal amount;
        public BigDecimal orderAmount;
        public BigDecimal orderPoundage;
        public String remark;


    }




}
