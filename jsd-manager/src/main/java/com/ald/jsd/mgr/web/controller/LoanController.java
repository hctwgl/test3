package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;

@Controller
@ResponseBody
@RequestMapping("/api/loan/")
public class LoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;

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
}
