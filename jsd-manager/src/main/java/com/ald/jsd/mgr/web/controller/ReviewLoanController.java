package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.dal.query.ReviewLoanQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONArray;
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

@Controller
@ResponseBody
@RequestMapping("/api/reviewLoan/")
public class ReviewLoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;

    @RequestMapping(value = {"list.json"}, method = RequestMethod.POST)
    public Resp<ReviewLoanQuery> list(@RequestBody ReviewLoanQuery reviewLoanQuery, HttpServletRequest request) {
        reviewLoanQuery.setFull(true);
        reviewLoanQuery.setList(jsdBorrowCashService.getReviewLoanList(reviewLoanQuery));
        return Resp.succ(reviewLoanQuery, "");
    }

    @RequestMapping(value = {"statistics.json"}, method = RequestMethod.POST)
    public Resp<HashMap<String, BigDecimal>> statistics(HttpServletRequest request) {
        HashMap<String, BigDecimal> hashMap = jsdBorrowCashService.getReviewLoanStatistics();
        return Resp.succ(hashMap, "");
    }

    @RequestMapping(value = {"review.json"}, method = RequestMethod.POST)
    public Resp<String> review(@RequestBody JSONArray jsonArray, HttpServletRequest request) {
        jsdBorrowCashService.updateReviewStatusByXgNo(jsonArray);
        return Resp.succ("成功", "");
    }
}
