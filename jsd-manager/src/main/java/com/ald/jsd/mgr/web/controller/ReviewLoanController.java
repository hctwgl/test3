package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.query.ReviewLoanQuery;
import com.ald.jsd.mgr.web.dto.req.ReviewLoanDetailsReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONArray;
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
@RequestMapping("/api/reviewLoan/")
public class ReviewLoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdUserAuthService jsdUserAuthService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;

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

    @RequestMapping(value = {"details.json"}, method = RequestMethod.POST)
    public Resp<ReviewLoanDetailsReq> details(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        ReviewLoanDetailsReq reviewLoanDetailsReq = new ReviewLoanDetailsReq();
        String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
        //借款信息
        JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        BeanUtils.copyProperties(jsdBorrowCashDo, reviewLoanDetailsReq);
        reviewLoanDetailsReq.setTerm(jsdBorrowCashDo.getType());
        reviewLoanDetailsReq.setApplyDate(jsdBorrowCashDo.getGmtCreate());
        //授信额度
        JsdUserAuthDo jsdUserAuthDo = jsdUserAuthService.getByUserId(jsdBorrowCashDo.getUserId());
        reviewLoanDetailsReq.setRiskAmount(jsdUserAuthDo.getRiskAmount());
        //用户信息
        JsdUserDo jsdUserDo = jsdUserService.getById(jsdBorrowCashDo.getUserId());
        BeanUtils.copyProperties(jsdUserDo, reviewLoanDetailsReq);
        if (StringUtil.isNotBlank(jsdUserDo.getBirthday())) {
            String year = jsdUserDo.getBirthday().substring(0, 4);
            String currentYear = DateUtil.getYear(new Date());
            reviewLoanDetailsReq.setAge(String.valueOf(Integer.valueOf(currentYear) - Integer.valueOf(year)));
        }
        //商品订单信息
        JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getLastOrderCashByBorrowId(jsdBorrowCashDo.getRid());
        reviewLoanDetailsReq.setGoodsInterestAmount(jsdBorrowLegalOrderCashDo.getInterestAmount());
        reviewLoanDetailsReq.setGoodsPoundageAmount(jsdBorrowLegalOrderCashDo.getPoundageAmount());
        JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getById(jsdBorrowLegalOrderCashDo.getBorrowLegalOrderId());
        reviewLoanDetailsReq.setGoodsName(jsdBorrowLegalOrderDo.getGoodsName());
        reviewLoanDetailsReq.setAddress(jsdBorrowLegalOrderDo.getAddress());
        reviewLoanDetailsReq.setGoodsPrice(jsdBorrowLegalOrderDo.getPriceAmount());
        return Resp.succ(reviewLoanDetailsReq, "");
    }
}
