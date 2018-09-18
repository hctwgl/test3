package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.dal.query.ReviewLoanQuery;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
@ResponseBody
@RequestMapping("/api/reviewLoan/")
public class ReviewLoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;

    @RequestMapping(value = { "list.json" },method = RequestMethod.POST)
    public Resp<ReviewLoanQuery> numProtocol(@RequestBody JSONObject data, HttpServletRequest request){
        String status = data.getString("status");
        String searchContent = data.getString("searchContent");
        int pageIndex = data.getInteger("pageIndex");
        int pageSize = data.getInteger("pageSize");
        ReviewLoanQuery reviewLoanQuery = new ReviewLoanQuery();
        reviewLoanQuery.setSearchContent(searchContent);
        reviewLoanQuery.setStatus(status);
        reviewLoanQuery.setPageIndex(pageIndex);
        reviewLoanQuery.setPageSize(pageSize);
        reviewLoanQuery.setFull(true);
        reviewLoanQuery.setList(jsdBorrowCashService.getReviewLoanList(reviewLoanQuery));
        return Resp.succ(reviewLoanQuery, "");
    }
}
