package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * H5账单Controller
 *
 * @author wangli
 * @date 2018/4/13 15:11
 */
@Controller
@RequestMapping("/fanbei-web/borrow")
public class AppH5BillController extends BaseController {

    private static final String opennative = "/fanbei-web/opennative?name=";

    // 一下账单类型只在全部待还这个功能中使用
    // 购物账单
    private static final String BILL_TYPE_BILL = "BILL";

    // 极速贷账单
    private static final String BILL_TYPE_CASH = "CASH";

    // 白领贷账单
    private static final String BILL_TYPE_LOAN = "LOAN";

    // 没有账单
    private static final String STATUS_NOBILL = "NOBILL";

    // 以下状态只在全部待还这个功能中使用
    // 还款中
    private static final String STATUS_REFUNDING = "REFUNDING";

    // 逾期
    private static final String STATUS_OVERDUE = "OVERDUE";

    // 待还
    private static final String STATUS_WAITREFUND = "WAITREFUND";

    // 下月待还
    private static final String STATUS_NEXTWAITREFUND = "NEXTWAITREFUND";

    @Autowired
    private AfUserService afUserService;

    @Autowired
    private AfUserAccountService userAccountService;

    @Autowired
    private AfBorrowBillService afBorrowBillService;

    @Autowired
    private AfBorrowCashService afBorrowCashService;

    @Autowired
    private AfRepaymentBorrowCashService afRepaymentBorrowCashService;

    @Autowired
    private AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;

    @Autowired
    private AfLoanService afLoanService;

    @Autowired
    private AfLoanRepaymentService afLoanRepaymentService;

    @Autowired
    private AfLoanPeriodsService afLoanPeriodsService;

    /**
     * 查找当月未还账单（包括购物分期账单，极速贷和白领贷）
     * 优先顺序为：还款中>逾期>本月已出>下月未出
     *
     * @author wangli
     * @date 2018/4/13 15:23
     */
    @RequestMapping(value = "/allWaitRepayments", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String findAllWaitRepaymentList(HttpServletRequest request) {
        FanbeiWebContext context = new FanbeiWebContext();

        try {
            context = doWebCheck(request, true);
            Long userId = afUserService.getUserByUserName(context.getUserName()).getRid();

            // 获取用户账号返利金额，设置账单的还款所需信息时使用
            String rebateAmount = "0.00";
            BigDecimal amount = userAccountService.getUserAccountByUserId(userId).getRebateAmount();
            if (amount != null) {
                rebateAmount = amount.setScale(2, RoundingMode.HALF_UP).toString();
            }

            List<Map<String, Object>> data = new ArrayList<>();
            data.add(getBorrowBill(userId));
            data.add(getBorrowCash(userId, rebateAmount));
            data.add(getLoan(userId, rebateAmount));

            return H5CommonResponse.getNewInstance(true, "请求成功", "", data).toString();
        } catch (FanbeiException e) {
            if (e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR)) {
                logger.error("/borrow/allWaitRepayments" + context + "login error ");

                Map<String, Object> data = new HashMap<>();
                data.put("loginUrl", ConfigProperties.get(Constants.CONFKEY_NOTIFY_HOST) + opennative +
                        H5OpenNativeType.AppLogin.getCode());
                return H5CommonResponse.getNewInstance(false, "没有登录", "", data).toString();
            }

            logger.error("/borrow/allWaitRepayments, error={}", e);
            return H5CommonResponse.getNewInstance(false, "未知错误", "", null).toString();
        }
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);
            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

    // 获取购物分期账单
    private Map<String,Object> getBorrowBill(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", BILL_TYPE_BILL);
        result.put("status", STATUS_NOBILL);

        // 暂无账单
        int allBillCount = getAllBorrowBillCount(userId);
        if (allBillCount < 1) {
            result.put("status", STATUS_NOBILL);
            return result;
        }

        // 已出待还金额
        BigDecimal waitRepayAmount = getOutBorrowBillWaitRepayAmount(userId);
        String waitRepayAmountStr = waitRepayAmount.setScale(2, RoundingMode.HALF_UP).toString();

        // 还款中
        int refundingNum = afBorrowBillService.getOnRepaymentCountByUserId(userId);
        if (refundingNum > 0) {
            result.put("amount", waitRepayAmountStr);
            result.put("billDesc", "您有<span>" + refundingNum + "</span>笔还款正在处理中");
            result.put("status", STATUS_REFUNDING);
            return  result;
        }

        // 逾期月数
        int overdueMonth = afBorrowBillService.getOverduedMonthByUserId(userId);
        // 逾期
        if (overdueMonth > 0) {
            result.put("amount", waitRepayAmountStr);
            result.put("billDesc", "您有<span>" + overdueMonth + "</span>个月的逾期账单");
            result.put("status", STATUS_OVERDUE);
            return result;
        }

        // 本月出账日开始和结束范围
        Date currMonthStartOutDay =  DateUtil.addHoures(DateUtil.getFirstOfMonth(new Date()), -12);
        Date currMonthEndOutDay = DateUtil.addMonths(currMonthStartOutDay, 1);

        // 本月账单已出
        int currMonthOutBillCount = getCurrMonthBorrowBillCount(userId, currMonthStartOutDay, currMonthEndOutDay, 1);
        if (currMonthOutBillCount > 0 && waitRepayAmount.compareTo(BigDecimal.ZERO) > 0) {
            result.put("amount", waitRepayAmountStr);

            Date lastPayDay = afBorrowBillService.getLastPayDayByUserId(userId);
            result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(lastPayDay));

            result.put("status", STATUS_WAITREFUND);
            return result;
        }

        // 下月未出账
        boolean isSetSuccess = false;
        if (currMonthOutBillCount > 0) {
            isSetSuccess = fillNotOutBorrowBillInfo(result, userId, currMonthStartOutDay, currMonthEndOutDay);
        } else {
            Date nextMonthStartOutDay = DateUtil.addMonths(currMonthStartOutDay, 1);
            Date nextMonthEndOutDay = DateUtil.addMonths(currMonthEndOutDay, 1);
            isSetSuccess = fillNotOutBorrowBillInfo(result, userId, nextMonthStartOutDay, nextMonthEndOutDay);
        }
        if (isSetSuccess) {
            result.put("status", STATUS_NEXTWAITREFUND);
            return result;
        } else {
            return result;
        }
    }

    // 获取极速贷账单
    private Map<String,Object> getBorrowCash(Long userId, String rebateAmount) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", BILL_TYPE_CASH);

        // 查询最早的待还记录
        AfBorrowCashDo lastBorrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
        if (lastBorrowCashDo == null) {
            // 没有最早的待还，查询最后一笔借款信息
            lastBorrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
        }

        // 暂无
        if (lastBorrowCashDo == null || lastBorrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
            result.put("status", STATUS_NOBILL);
            return result;
        }

        // 计算待还金额
        String waitRepaymentAmountStr = afBorrowCashService.calculateLegalRestAmount(lastBorrowCashDo)
                .setScale(2, RoundingMode.HALF_UP).toString();
        result.put("amount", waitRepaymentAmountStr);

        // 还款所需信息
        result.put("borrowId", lastBorrowCashDo.getRid().toString());
        result.put("shouldRepaymentAmount", waitRepaymentAmountStr);
        result.put("rebateAmount", rebateAmount);
        // 极速贷和白领贷走了相同接口，所以传默认值
        result.put("loanPeriodsId", "");
        result.put("loanRepaymentType", "");
        result.put("periodsUnChargeAmount", "");

        // 还款中
        BigDecimal repayingMoney = afRepaymentBorrowCashService
                .getRepayingTotalAmountByBorrowId(lastBorrowCashDo.getRid());
        /*BigDecimal repayingOrderMoney = afBorrowLegalOrderRepaymentDao
                .getOrderRepayingTotalAmountByBorrowId(lastBorrowCashDo.getRid());
        repayingMoney = repayingMoney.add(repayingOrderMoney);*/
        if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
            int repayingNum = afRepaymentBorrowCashService.getRepayingTotalNumByBorrowId(lastBorrowCashDo.getRid());
            result.put("billDesc", "您有<span>" + repayingNum + "</span>笔还款正在处理中");
            result.put("status", STATUS_REFUNDING);
            return result;
        }

        // 逾期 如果预计还款日在今天之前，且为待还款状态，则已逾期
        Date now = DateUtil.getEndOfDate(new Date());
        if (StringUtils.equals(lastBorrowCashDo.getStatus(), AfBorrowCashStatus.transed.getCode())
                && lastBorrowCashDo.getGmtPlanRepayment().before(now)) {
            result.put("billDesc", "<i>逾期将产生逾期费,并上报征信,请尽快还款</i>");
            result.put("status", STATUS_OVERDUE);
            return result;
        }

        // 待还未逾期
        result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(lastBorrowCashDo.getGmtPlanRepayment()));
        result.put("status", STATUS_WAITREFUND);
        return result;
    }

    // 获取白领贷账单
    private Map<String, Object> getLoan(Long userId, String rebateAmount) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", BILL_TYPE_LOAN);
        result.put("status", STATUS_NOBILL);

        AfLoanDo lastLoanDo = afLoanService.getLastByUserIdAndPrdType(userId, LoanType.BLD_LOAN.getCode());

        // 暂无
        if (lastLoanDo == null || AfLoanStatus.FINISHED.name().equals(lastLoanDo.getStatus())
                || AfLoanStatus.CLOSED.name().equals(lastLoanDo.getStatus())) {
            result.put("status", STATUS_NOBILL);
            return result;
        }

        // 待还分期
        List<AfLoanPeriodsDo> waitRepayPeriods = afLoanPeriodsService.listCanRepayPeriods(lastLoanDo.getRid());
        // 待还金额
        BigDecimal waitRepayAmount = BigDecimal.ZERO;
        // 是否逾期
        boolean isOverdue = false;
        // 逾期金额
        BigDecimal overdueAmount = BigDecimal.ZERO;
        // 待还款最后还款日
        Date gmtLastPlanRepay = null;
        // 逾期最后还款日
        Date overdueGmtLastPlanRepay = null;
        // 待还期数id
        String loanPeriodsId = "";
        for (int i = 0; i < waitRepayPeriods.size(); i++) {
            AfLoanPeriodsDo e = waitRepayPeriods.get(i);

            waitRepayAmount = waitRepayAmount.add(afLoanPeriodsService.calcuRestAmount(e));
            gmtLastPlanRepay = e.getGmtPlanRepay();
            if(YesNoStatus.YES.getCode().equals(e.getOverdueStatus())) {
                isOverdue = true;
                overdueAmount = overdueAmount.add(e.getOverdueAmount());
                overdueGmtLastPlanRepay = e.getGmtLastRepay();
            }

            loanPeriodsId += e.getRid().toString();
            if (i < waitRepayPeriods.size() - 1) {
                loanPeriodsId += ",";
            }
        }
        String waitRepayAmountStr = waitRepayAmount.setScale(2, RoundingMode.HALF_UP).toString();

        // 还款所需信息
        result.put("borrowId", lastLoanDo.getRid().toString());
        result.put("shouldRepaymentAmount", waitRepayAmountStr);
        result.put("rebateAmount", rebateAmount);
        result.put("loanPeriodsId", loanPeriodsId);
        result.put("loanRepaymentType", "commonSettle");

        // 还款中
        AfLoanRepaymentDo processLoanRepayment = afLoanRepaymentService.getProcessLoanRepaymentByLoanId(lastLoanDo.getRid());
        if (processLoanRepayment != null) {
            result.put("amount", waitRepayAmountStr);

            int processNum = afLoanRepaymentService.getProcessLoanRepaymentNumByLoanId(lastLoanDo.getRid());
            result.put("billDesc", "您有<span>" + processNum + "</span>还款正在处理中");

            result.put("status", STATUS_REFUNDING);
            return result;
        }

        // 逾期
        if (isOverdue) {
            result.put("amount", waitRepayAmountStr);
            result.put("billDesc", "<i>(含逾期费" + overdueAmount.setScale(2, RoundingMode.HALF_UP)
                    + "元)</i><br/>最后还款日 " + DateUtil.formatDateForPatternWithHyhen(overdueGmtLastPlanRepay));
            result.put("status", STATUS_OVERDUE);
            return result;
        }

        // 待还未逾期
        if (waitRepayPeriods.size() > 0) {
            result.put("amount", waitRepayAmountStr);
            result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(gmtLastPlanRepay));
            result.put("status", STATUS_WAITREFUND);
            return result;
        } else {
            // 本月已结清，下月还款时间还没开始
            if(lastLoanDo.getStatus().equals(AfLoanStatus.TRANSFERRED.name())){
                result.put("amount", "0.00");
                result.put("billDesc", "提前结清可减免手续费哦!");
                result.put("status", STATUS_NEXTWAITREFUND);

                result.put("loanRepaymentType", "forwardSettle");
                BigDecimal unChargeAmount = BigDecimal.ZERO;
                List<AfLoanPeriodsDo> unps = afLoanPeriodsService.listUnChargeRepayPeriods(lastLoanDo.getRid());
                for(AfLoanPeriodsDo p : unps) {
                    unChargeAmount = unChargeAmount.add(p.getAmount());
                }
                result.put("periodsUnChargeAmount",
                        unChargeAmount.setScale(2, RoundingMode.HALF_UP).toString());
                return result;
            }
        }

        return result;
    }

    // 填充未出账账单信息
    private boolean fillNotOutBorrowBillInfo(Map<String, Object> result, Long userId, Date startOutDay, Date endOutDay) {
        AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
        query.setUserId(userId);
        query.setIsOut(0);
        query.setOutDayStr(startOutDay);
        query.setOutDayEnd(endOutDay);

        BigDecimal notOutMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
        List<AfBorrowBillDo> list = afBorrowBillService.getUserBillListByQuery(query);
        if (notOutMoney.equals(BigDecimal.ZERO) || list.size() == 0) {
            return false;
        }

        result.put("amount", notOutMoney.setScale(2, RoundingMode.HALF_UP).toString());
        result.put("billDesc",  "将于" + DateUtil.formatMonthAndDay(list.get(0).getGmtOutDay()) + "出账");
        return true;
    }

    // 获取本月购物账单数量
    private int getCurrMonthBorrowBillCount(Long userId, Date currMonthStartOutDay, Date currMonthEndOutDay, int isOut) {
        AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
        query.setUserId(userId);
        query.setIsOut(isOut);
        query.setOutDayStr(currMonthStartOutDay);
        query.setOutDayEnd(currMonthEndOutDay);
        return afBorrowBillService.countBillByQuery(query);
    }

    // 获取购物账单已出待还金额
    private BigDecimal getOutBorrowBillWaitRepayAmount(Long userId) {
        AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
        query.setUserId(userId);
        query.setIsOut(1);
        query.setStatus(BorrowBillStatus.NO.getCode());
        return afBorrowBillService.getUserBillMoneyByQuery(query);
    }

    // 获取所有购物账单数量
    private int getAllBorrowBillCount(Long userId) {
        AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
        query.setUserId(userId);
        return afBorrowBillService.countBillByQuery(query);
    }
}
