package com.ald.jsd.mgr.web.controller;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.dto.LoanDto;
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
        List<LoanDto> list = jsdBorrowCashService.getRepayList(loanQuery);
        if (null != list && list.size() > 0) {
            for (LoanDto loanDto : list) {
                if ("Y".equals(loanDto.getOverdueStatus()) && "TRANSFERRED".equals(loanDto.getStatus())) {
                    //逾期 判断是否续借，续借根据预计还款时间判断状态
                    if ("Y".equals(loanDto.getIsRenewal())) {
                        //续借
                        if (System.currentTimeMillis() < loanDto.getGmtPlanRepayment().getTime()){
                            //未到预计还款时间，待还款
                            loanDto.setStatus("TRANSFERRED");
                        } else{
                            //超过预计还款时间，逾期
                            loanDto.setStatus("OVERDUE");
                        }
                    } else if ("N".equals(loanDto.getIsRenewal())) {
                        //没有续借  已逾期
                        loanDto.setStatus("OVERDUE");
                    }
                }
            }
        }
        loanQuery.setList(list);
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
