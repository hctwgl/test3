package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.web.dto.req.LoanDetailsReq;
import com.ald.jsd.mgr.web.dto.req.ReviewLoanDetailsReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;

@Controller
@ResponseBody
@RequestMapping("/api/loan/")
public class LoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;

    @RequestMapping(value = {"list.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> list(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        loanQuery.setFull(true);
        loanQuery.setList(jsdBorrowCashService.getLoanList(loanQuery));
        return Resp.succ(loanQuery, "");
    }

    @RequestMapping(value = {"statistics.json"}, method = RequestMethod.POST)
    public Resp<HashMap<String, BigDecimal>> statistics(HttpServletRequest request) {
        HashMap<String, BigDecimal> hashMap = jsdBorrowCashService.getLoanStatistics();
        return Resp.succ(hashMap, "");
    }

    @RequestMapping(value = {"details.json"}, method = RequestMethod.POST)
    public Resp<LoanDetailsReq> details(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        LoanDetailsReq loanDetailsReq = new LoanDetailsReq();
        String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
        //借款信息
        JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        BeanUtils.copyProperties(loanDetailsReq, jsdBorrowCashDo);
        loanDetailsReq.setTerm(jsdBorrowCashDo.getType());
        loanDetailsReq.setApplyDate(jsdBorrowCashDo.getGmtCreate());
        loanDetailsReq.setUnrepayAmount(jsdBorrowCashDo.getAmount().subtract(jsdBorrowCashDo.getRepayAmount()));
        loanDetailsReq.setUnrepayInterestAmount(jsdBorrowCashDo.getInterestAmount());
        loanDetailsReq.setUnrepayOverdueAmount(jsdBorrowCashDo.getOverdueAmount());
        loanDetailsReq.setUnrepayServiceAmount(jsdBorrowCashDo.getPoundageAmount());
        //用户信息
        JsdUserDo jsdUserDo = jsdUserService.getById(jsdBorrowCashDo.getUserId());
        BeanUtils.copyProperties(loanDetailsReq, jsdUserDo);

        //是否入催
        JsdCollectionBorrowDo jsdCollectionBorrowDo = jsdCollectionBorrowService.selectByBorrowId(jsdBorrowCashDo.getRid());
        if (jsdCollectionBorrowDo == null) {
            loanDetailsReq.setIsCollection("N");
        } else {
            loanDetailsReq.setIsCollection("Y");
        }

        return Resp.succ(loanDetailsReq, "");
    }
}
