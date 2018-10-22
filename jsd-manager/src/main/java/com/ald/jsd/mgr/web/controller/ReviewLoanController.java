package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.dal.dao.MgrOperateLogDao;
import com.ald.jsd.mgr.web.Sessions;
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
    @Resource
    JsdBorrowLegalOrderInfoService jsdBorrowLegalOrderInfoService;
    @Resource
    MgrOperateLogDao mgrOperateLogDao;

    @RequestMapping(value = {"list.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> list(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
        loanQuery.setFull(true);
        loanQuery.setList(jsdBorrowCashService.getReviewLoanList(loanQuery));
        return Resp.succ(loanQuery, "");
    }

    @RequestMapping(value = {"statistics.json"}, method = RequestMethod.POST)
    public Resp<HashMap<String, BigDecimal>> statistics(HttpServletRequest request) {
        HashMap<String, BigDecimal> hashMap = jsdBorrowCashService.getReviewLoanStatistics();
        return Resp.succ(hashMap, "");
    }

    @RequestMapping(value = {"review.json"}, method = RequestMethod.POST)
    public Resp<String> review(@RequestBody JSONArray jsonArray, HttpServletRequest request) {
        jsdBorrowCashService.updateReviewStatusByXgNo(jsonArray);
        mgrOperateLogDao.addOperateLog(Sessions.getRealname(request), "借款审核：" + jsonArray.toJSONString());
        return Resp.succ("成功", "");
    }

    @RequestMapping(value = {"details.json"}, method = RequestMethod.POST)
    public Resp<ReviewLoanDetailsReq> details(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        ReviewLoanDetailsReq reviewLoanDetailsReq = new ReviewLoanDetailsReq();
        String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
        //借款信息
        JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        BeanUtils.copyProperties(reviewLoanDetailsReq, jsdBorrowCashDo);
        reviewLoanDetailsReq.setTerm(jsdBorrowCashDo.getType());
        reviewLoanDetailsReq.setApplyDate(jsdBorrowCashDo.getGmtCreate());
        reviewLoanDetailsReq.setLoanRemark(jsdBorrowCashDo.getBorrowRemark());
        reviewLoanDetailsReq.setInterestAmount(reviewLoanDetailsReq.getInterestAmount().add(jsdBorrowCashDo.getSumRepaidInterest()));
        reviewLoanDetailsReq.setPoundageAmount(reviewLoanDetailsReq.getPoundageAmount().add(jsdBorrowCashDo.getSumRepaidPoundage()));
        //授信额度
        JsdUserAuthDo jsdUserAuthDo = jsdUserAuthService.getByUserId(jsdBorrowCashDo.getUserId());
        if (jsdUserAuthDo != null) {
            reviewLoanDetailsReq.setRiskAmount(jsdUserAuthDo.getRiskAmount());
        }
        //用户信息
        JsdUserDo jsdUserDo = jsdUserService.getById(jsdBorrowCashDo.getUserId());
        reviewLoanDetailsReq.setRealName(jsdUserDo.getRealName());
        reviewLoanDetailsReq.setMobile(jsdUserDo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
        reviewLoanDetailsReq.setIdNumber(jsdUserDo.getIdNumber());
        reviewLoanDetailsReq.setGender(jsdUserDo.getGender());
        if (StringUtil.isNotBlank(jsdUserDo.getBirthday())) {
            String year = jsdUserDo.getBirthday().substring(0, 4);
            String currentYear = DateUtil.getYear(new Date());
            reviewLoanDetailsReq.setAge(String.valueOf(Integer.valueOf(currentYear) - Integer.valueOf(year)));
        }
        //商品订单信息
        JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = jsdBorrowLegalOrderCashService.getFirstOrderCashByBorrowId(jsdBorrowCashDo.getRid());
        if (jsdBorrowLegalOrderCashDo == null) {
            reviewLoanDetailsReq.setGoodsInterestAmount(BigDecimal.ZERO);
            reviewLoanDetailsReq.setGoodsPoundageAmount(BigDecimal.ZERO);
        } else {
            reviewLoanDetailsReq.setGoodsInterestAmount(jsdBorrowLegalOrderCashDo.getInterestAmount());
            reviewLoanDetailsReq.setGoodsPoundageAmount(jsdBorrowLegalOrderCashDo.getPoundageAmount());
        }
        JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = jsdBorrowLegalOrderService.getFirstOrderByBorrowId(jsdBorrowCashDo.getRid());
        reviewLoanDetailsReq.setGoodsName(jsdBorrowLegalOrderDo.getGoodsName());
        reviewLoanDetailsReq.setGoodsPrice(jsdBorrowLegalOrderDo.getPriceAmount());
        JsdBorrowLegalOrderInfoDo jsdBorrowLegalOrderInfoDo = jsdBorrowLegalOrderInfoService.getByBorrowId(jsdBorrowCashDo.getRid());
        reviewLoanDetailsReq.setAddress(jsdBorrowLegalOrderInfoDo == null ? "" : jsdBorrowLegalOrderInfoDo.getAddress());
        return Resp.succ(reviewLoanDetailsReq, "");
    }
}
