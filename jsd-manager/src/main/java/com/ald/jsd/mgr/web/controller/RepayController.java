package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.biz.service.MgrOfflineRepaymentService;
import com.ald.jsd.mgr.web.Sessions;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ResponseBody
@RequestMapping("/api/repay/")
public class RepayController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    MgrOfflineRepaymentService mgrOfflineRepaymentService;
    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    @RequestMapping(value = {"list.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> list(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        loanQuery.setFull(true);
        loanQuery.setList(jsdBorrowCashService.getRepayList(loanQuery));
        return Resp.succ(loanQuery, "");
    }

    @RequestMapping(value = {"statistics.json"}, method = RequestMethod.POST)
    public Resp<HashMap<String, BigDecimal>> statistics(HttpServletRequest request) {
        HashMap<String, BigDecimal> hashMap = jsdBorrowCashService.getRepayStatistics();
        return Resp.succ(hashMap, "");
    }

    @RequestMapping(value = {"offline.json"}, method = RequestMethod.POST)
    public Resp<?> offline(@RequestBody Map<String, String> data, HttpServletRequest request) {
        mgrOfflineRepaymentService.dealOfflineRepayment(data, JsdRepayType.OFFLINE, Sessions.getRealname(request));
        return Resp.succ();
    }

    @RequestMapping(value = {"record.json"}, method = RequestMethod.POST)
    public Resp<List<JsdBorrowCashRepaymentDo>> record(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
        String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
        List<JsdBorrowCashRepaymentDo> list = jsdBorrowCashRepaymentService.getByBorrowTradeNoXgxy(tradeNoXgxy);
        return Resp.succ(list, "");
    }
}
