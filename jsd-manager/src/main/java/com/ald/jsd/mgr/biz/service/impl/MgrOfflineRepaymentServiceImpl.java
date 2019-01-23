package com.ald.jsd.mgr.biz.service.impl;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.exception.BizException;
import com.ald.fanbei.api.common.exception.BizExceptionCode;
import com.ald.fanbei.api.dal.dao.JsdBorrowCashRepaymentDao;
import com.ald.fanbei.api.dal.dao.JsdCollectionRepaymentDao;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.jsd.mgr.dal.dao.MgrOperateLogDao;
import com.ald.jsd.mgr.enums.CommonReviewStatus;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;
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
    @Resource
    MgrOperateLogDao mgrOperateLogDao;
    @Resource
    private JsdCollectionRepaymentDao jsdCollectionRepaymentDao;


    @Override
    public void dealOfflineRepayment(Map<String, String> reqData, JsdRepayType repayType, String realName) {
        String borrowNo = reqData.get("borrowNo");
        Date repaymentDate = new Date(Long.parseLong(reqData.get("repaymentDate")));
        String channel = reqData.get("channel");
        String tradeNo = reqData.get("tradeNo");
        String amount = reqData.get("amount");
        String remark = reqData.get("remark");

        //根据流水号查询还款信息
        JsdCollectionRepaymentDo jsdCollectionRepaymentDo = new JsdCollectionRepaymentDo();
        jsdCollectionRepaymentDo.setTradeNo(tradeNo);
        jsdCollectionRepaymentDo = jsdCollectionRepaymentDao.getByCommonCondition(jsdCollectionRepaymentDo);
        //判断是否有同一还款流水号待审核
        if (null != jsdCollectionRepaymentDo && CommonReviewStatus.WAIT.name().equals(jsdCollectionRepaymentDo.getReviewStatus())){
            throw new BizException(BizExceptionCode.BORROW_CASH_REPAY_UNCHECKED_ERROR);
        }
        JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(borrowNo);
        if (null == borrowCashDo){
            throw new BizException(BizExceptionCode.JSD_BORROW_IS_NULL);
        }
        JsdBorrowLegalOrderCashDo legalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
//        if (null == legalOrderCashDo){
//            throw new BizException(BizExceptionCode.JSD_BORROW_IS_NULL);
//        }
        String dataId = String.valueOf(borrowCashDo.getRid() + borrowCashDo.getRenewalNum());
        jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo, legalOrderCashDo, amount, tradeNo, borrowCashDo.getUserId(), repayType, channel, repaymentDate, null, dataId, remark);
        mgrOperateLogDao.addOperateLog(realName, "线下还款：" + JSON.toJSONString(reqData));
    }
}
