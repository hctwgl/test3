package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.enums.BorrowVersionType;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.common.enums.JsdBorrowCashRepaymentStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;

@Component("borrowCashRepaymentBillApi")
public class BorrowCashRepaymentBillApi implements JsdH5Handle {

    @Resource
    private JsdBorrowCashService jsdBorrowCashService;

    @Resource
    private JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Override
    public JsdH5HandleResponse process(Context context) {
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        String borrowNo = ObjectUtils.toString(context.getData("borrowNo"), null);
        JsdBorrowCashDo cashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNo);
        Map<String, Object> map=null;
        if(BorrowVersionType.SELL.name().equals(cashDo.getVersion())){
            JsdBorrowLegalOrderCashDo orderCashDo=jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(cashDo.getRid());
            map = buildSELLBorrowBill(cashDo,orderCashDo);
        }else {
            map = buildBeheadBorrowBill(cashDo);
        }
        resp.setData(map);
        return resp;
    }
    private Map<String,Object> buildSELLBorrowBill(JsdBorrowCashDo cashDo,JsdBorrowLegalOrderCashDo orderCashDo){
        Map<String, Object> map = new HashMap<String, Object>();
        BigDecimal unrepayGoodsAmount = orderCashDo.getAmount().add(orderCashDo.getSumRepaidPoundage()).add(orderCashDo.getSumRepaidOverdue())
        								.add(orderCashDo.getSumRepaidInterest()).subtract(orderCashDo.getRepaidAmount());
        BigDecimal unrepayGoodsSellAmount = orderCashDo.getOverdueAmount().add(orderCashDo.getInterestAmount()).add(orderCashDo.getPoundageAmount());
        map.put("unrepayGoodsAmount",unrepayGoodsAmount);
        map.put("unrepayGoodsSellAmount",unrepayGoodsSellAmount);
        
        List<Map<String,Object>>  borrowBillDetails=new ArrayList<>();
        Map<String,Object> borrowBillDetail=new HashMap<>();
        
        String status = "AWAIT_REPAY";	// 待还款
        if(cashDo.getRepayAmount().compareTo(BigDecimal.ZERO) > 0){
        	status = "PART_REPAY";		// 部分还款
        	if(JsdBorrowCashStatus.FINISHED.name().equals(cashDo.getStatus())) {
        		status = "FINISHED";	// 已还清	
        	}
        }
        JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(cashDo.getRid());
        if(repaymentDo!=null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(repaymentDo.getStatus())){
        	status = "REPAYING";	// 还款中
        }
        borrowBillDetail.put("status", status);

        borrowBillDetail.put("overdueStatus", cashDo.getOverdueStatus());
        borrowBillDetail.put("overdueDays", cashDo.getOverdueDay());
        borrowBillDetail.put("totalPeriod","1");
        borrowBillDetail.put("period","1");
        borrowBillDetail.put("amount", cashDo.getAmount().add(orderCashDo.getAmount()));
        borrowBillDetail.put("totalAmount", jsdBorrowCashService.calcuTotalAmount(cashDo, orderCashDo));
        borrowBillDetail.put("interestAmount", BigDecimalUtil.add(cashDo.getInterestAmount(), cashDo.getSumRepaidInterest(), orderCashDo.getInterestAmount(), orderCashDo.getSumRepaidInterest()));
        borrowBillDetail.put("serviceAmount", BigDecimalUtil.add(cashDo.getPoundageAmount(), cashDo.getSumRepaidPoundage(), orderCashDo.getPoundageAmount(), orderCashDo.getSumRepaidPoundage()));
        borrowBillDetail.put("overdueAmount", BigDecimalUtil.add(cashDo.getOverdueAmount(), cashDo.getSumRepaidOverdue(), orderCashDo.getOverdueAmount(), orderCashDo.getSumRepaidOverdue()));
        borrowBillDetail.put("gmtPlanRepay", cashDo.getGmtPlanRepayment());
        
        borrowBillDetail.put("repaidAmount",cashDo.getRepayAmount().add(orderCashDo.getRepaidAmount()));
        
        borrowBillDetail.put("unrepayAmount", jsdBorrowCashService.calcuUnrepayAmount(cashDo, orderCashDo));
        borrowBillDetail.put("unrepayInterestAmount", cashDo.getInterestAmount().add(orderCashDo.getInterestAmount()));
        borrowBillDetail.put("unrepayOverdueAmount", cashDo.getOverdueAmount().add(orderCashDo.getOverdueAmount()));
        borrowBillDetail.put("unrepayServiceAmount", cashDo.getPoundageAmount().add(orderCashDo.getPoundageAmount()));
        borrowBillDetails.add(borrowBillDetail);
        map.put("borrowBillDetails",borrowBillDetails);
        logger.info("Detail BO:"+borrowBillDetail+cashDo);
        return map;
    }

    private Map<String,Object> buildBeheadBorrowBill(JsdBorrowCashDo cashDo){
        Map<String, Object> map = new HashMap<String, Object>();

        List<Map<String,Object>>  borrowBillDetails=new ArrayList<>();
        Map<String,Object> borrowBillDetail=new HashMap<>();

        String status = "AWAIT_REPAY";	// 待还款
        if(cashDo.getRepayAmount().compareTo(BigDecimal.ZERO) > 0){
            status = "PART_REPAY";		// 部分还款
            if(JsdBorrowCashStatus.FINISHED.name().equals(cashDo.getStatus())) {
                status = "FINISHED";	// 已还清
            }
        }
        JsdBorrowCashRepaymentDo repaymentDo = jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(cashDo.getRid());
        if(repaymentDo!=null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(repaymentDo.getStatus())){
            status = "REPAYING";	// 还款中
        }
        borrowBillDetail.put("status", status);

        borrowBillDetail.put("overdueStatus", cashDo.getOverdueStatus());
        borrowBillDetail.put("overdueDays", cashDo.getOverdueDay());
        borrowBillDetail.put("totalPeriod","1");
        borrowBillDetail.put("period","1");
        borrowBillDetail.put("amount", cashDo.getAmount());
        borrowBillDetail.put("totalAmount",  BigDecimalUtil.add(cashDo.getAmount(), cashDo.getInterestAmount(), cashDo.getPoundageAmount(), cashDo.getOverdueAmount(),
                cashDo.getSumRepaidInterest(), cashDo.getSumRepaidPoundage(), cashDo.getSumRepaidOverdue()));
        borrowBillDetail.put("interestAmount", BigDecimalUtil.add(cashDo.getInterestAmount(), cashDo.getSumRepaidInterest()));
        borrowBillDetail.put("serviceAmount", BigDecimalUtil.add(cashDo.getPoundageAmount(), cashDo.getSumRepaidPoundage()));
        borrowBillDetail.put("overdueAmount", BigDecimalUtil.add(cashDo.getOverdueAmount(), cashDo.getSumRepaidOverdue()));
        borrowBillDetail.put("gmtPlanRepay", cashDo.getGmtPlanRepayment());

        borrowBillDetail.put("repaidAmount",cashDo.getRepayAmount());

        borrowBillDetail.put("unrepayAmount", BigDecimalUtil.add(cashDo.getAmount(), cashDo.getInterestAmount(), cashDo.getPoundageAmount(), cashDo.getOverdueAmount(),
                cashDo.getSumRepaidInterest(), cashDo.getSumRepaidPoundage(), cashDo.getSumRepaidOverdue()).subtract(cashDo.getRepayAmount()));
        borrowBillDetail.put("unrepayInterestAmount", cashDo.getInterestAmount());
        borrowBillDetail.put("unrepayOverdueAmount", cashDo.getOverdueAmount());
        borrowBillDetail.put("unrepayServiceAmount", cashDo.getPoundageAmount());
        borrowBillDetails.add(borrowBillDetail);
        map.put("borrowBillDetails",borrowBillDetails);
        return map;
    }

}
