package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
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

    // 购物账单
    private static final String BILL_TYPE_BILL = "BILL";

    // 极速贷账单
    private static final String BILL_TYPE_CASH = "CASH";

    // 白领贷账单
    private static final String BILL_TYPE_LOAN = "LOAN";

    // 没有账单
    private static final String STATUS_NOBILL = "NOBILL";

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

            // 测试
           /* String userName = request.getParameter("userName");
            Long userId = afUserService.getUserByUserName(userName).getRid();*/

            List<Map<String, Object>> data = new ArrayList<>();
            data.add(getBorrowBill(userId));
            data.add(getBorrowCash(userId));
            data.add(getLoan(userId));

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
        }
        return null;
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

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        // 本月账单所属年
        int currBillYear = cal.get(Calendar.YEAR);
        // 本月账单所属月
        int currBillMonth = cal.get(Calendar.MONTH) + 1;
        // 已出账待还款金额
        BigDecimal waitRepaymentOutMoney = afBorrowBillService.getMonthlyBillByStatusNew(userId, currBillYear,
                currBillMonth, BorrowBillStatus.NO.getCode());
        // 逾期金额
        BigDecimal overdueAmount = afBorrowBillService
                .getMonthlyBillByStatusNewV1(userId, BorrowBillStatus.OVERDUE.getCode());
        // 逾期月数
        int overdueMonth = afBorrowBillService.getOverduedMonthByUserId(userId);

        // 还款中
        BigDecimal refundingAmount = afBorrowBillService.getMonthlyBillByStatusNew(userId, currBillYear,
                currBillMonth, BorrowBillStatus.DEALING.getCode());
        if (refundingAmount.compareTo(BigDecimal.ZERO) > 0) {
            /*BigDecimal amount = waitRepaymentOutMoney.subtract(refundingAmount);
            result.put("amount", amount.compareTo(BigDecimal.ZERO) > 0 ?
                    amount.setScale(2, RoundingMode.HALF_UP).toString() : "0.00");*/
            result.put("amount", waitRepaymentOutMoney.setScale(2, RoundingMode.HALF_UP).toString());

            int refundingNum = afBorrowBillService.getOnRepaymentCountByUserId(userId);
            result.put("billDesc", "您有<span>" + refundingNum + "</span>笔还款正在处理中");
            result.put("status", STATUS_REFUNDING);
            return  result;
        }
        // 逾期
        if (overdueAmount.compareTo(BigDecimal.ZERO) > 0) {
            result.put("amount", overdueAmount.add(waitRepaymentOutMoney)
                    .setScale(2, RoundingMode.HALF_UP).toString());
            result.put("billDesc", "您有<span>" + overdueAmount + "</span>个月的逾期账单");
            result.put("status", STATUS_OVERDUE);
            return result;
        }
        // 本月已出
        AfBorrowBillDo currMonthChildBill = getOneChildBill(userId, 1, currBillYear, currBillMonth);
        if (currMonthChildBill != null) {
            result.put("amount", overdueAmount.add(waitRepaymentOutMoney)
                    .setScale(2, RoundingMode.HALF_UP).toString());
            result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(currMonthChildBill.getGmtPayTime()));
            result.put("status", STATUS_WAITREFUND);
            return result;
        } else if (overdueMonth > 0) {
            // 无本月已出，但有逾期待还
            result.put("amount", overdueAmount);

            AfBorrowBillDo latestOverdueBill = afBorrowBillService.getLatestOverdueBorrowBillInfoByUserId(userId);
            result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(latestOverdueBill.getGmtPayTime()));

            result.put("status", STATUS_WAITREFUND);
            return result;
        }
        // 下月未出账单
        AfBorrowBillDo nextMonthBill = getNextMonthNotOutBorrowBill(userId);
        if (nextMonthBill != null) {
            result.put("amount", nextMonthBill.getBillAmount());
            result.put("billDesc", "将于" + DateUtil.formatMonthAndDay(nextMonthBill.getGmtOutDay()) + "出账");
            result.put("status", STATUS_NEXTWAITREFUND);
            return result;
        }

        // 暂无账单
        result.put("status", STATUS_NOBILL);
        return result;
    }

    // 获取极速贷账单
    private Map<String,Object> getBorrowCash(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", BILL_TYPE_CASH);

        // 查询最早的待还记录
        AfBorrowCashDo borrowCashDo = afBorrowCashService.getNowTransedBorrowCashByUserId(userId);
        if (borrowCashDo == null) {
            // 没有最早的待还，查询最后一笔借款信息
            borrowCashDo = afBorrowCashService.getBorrowCashByUserIdDescById(userId);
        }

        // 暂无
        if (borrowCashDo == null || borrowCashDo.getStatus().equals(AfBorrowCashStatus.finsh.getCode())) {
            result.put("status", STATUS_NOBILL);
            return result;
        }

        // 计算待还金额
        BigDecimal allAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumOverdue(),
                borrowCashDo.getOverdueAmount(), borrowCashDo.getRateAmount(), borrowCashDo.getSumRate(),
                borrowCashDo.getPoundage(), borrowCashDo.getSumRenewalPoundage());
        String waitRepaymentAmount = BigDecimalUtil.subtract(allAmount, borrowCashDo.getRepayAmount())
                .setScale(2, RoundingMode.HALF_UP).toString();
        result.put("amount", waitRepaymentAmount);

        // 还款中
        BigDecimal repayingMoney = afRepaymentBorrowCashService
                .getRepayingTotalAmountByBorrowId(borrowCashDo.getRid());
        BigDecimal repayingOrderMoney = afBorrowLegalOrderRepaymentDao
                .getOrderRepayingTotalAmountByBorrowId(borrowCashDo.getRid());
        repayingMoney = repayingMoney.add(repayingOrderMoney);
        if (repayingMoney.compareTo(BigDecimal.ZERO) > 0) {
            int repayingNum = afRepaymentBorrowCashService.getRepayingTotalNumByBorrowId(borrowCashDo.getRid());
            result.put("billDesc", "您有<span>" + repayingNum + "</span>笔还款正在处理中");
            result.put("status", STATUS_REFUNDING);
            return result;
        }
        // 逾期 如果预计还款日在今天之前，且为待还款状态，则已逾期
        Date now = DateUtil.getEndOfDate(new Date());
        if (StringUtils.equals(borrowCashDo.getStatus(), "TRANSED")
                && borrowCashDo.getGmtPlanRepayment().before(now)) {
            result.put("billDesc", "<i>逾期将产生逾期费，并上报征信，\n请尽快还款</i>");
            result.put("status", STATUS_OVERDUE);
            return result;
        }
        // 待还未逾期
        result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(borrowCashDo.getGmtPlanRepayment()));
        result.put("status", STATUS_WAITREFUND);
        return result;
    }

    // 获取白领贷账单
    private Map<String, Object> getLoan(Long userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("type", BILL_TYPE_LOAN);

        AfLoanDo lastLoanDo = afLoanService.getLastByUserIdAndPrdType(userId, LoanType.BLD_LOAN.getCode());
        // 暂无
        if (lastLoanDo == null || AfLoanStatus.FINISHED.name().equals(lastLoanDo.getStatus())
                || AfLoanStatus.CLOSED.name().equals(lastLoanDo.getStatus())) {
            result.put("status", STATUS_NOBILL);
            return result;
        }
        // 本月待还  TODO:确认本月待还怎么取
        AfLoanPeriodsDo currMonthPeriodsDo = afLoanPeriodsService.getCurrMonthPeriod(lastLoanDo.getRid());
        if (currMonthPeriodsDo == null) {
            result.put("status", STATUS_NOBILL);
            return result;
        }

        // 待还金额
        BigDecimal waitRepaymentAmount = afLoanPeriodsService.calcuRestAmount(currMonthPeriodsDo);

        // 还款中
        if (currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.REPAYING.getCode())) {
            result.put("amount", waitRepaymentAmount);

            int processNum = afLoanRepaymentService.getProcessLoanRepaymentNumByLoanId(lastLoanDo.getRid());
            result.put("billDesc", "您有<span>" + processNum + "</span>还款正在处理中");

            result.put("status", STATUS_REFUNDING);
            return result;
        }
        // 已逾期
        if (currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.AWAIT_REPAY.getCode())
                && currMonthPeriodsDo.getOverdueStatus().equals(YesNoStatus.YES.getCode())) {
            result.put("amount", waitRepaymentAmount);
            result.put("billDesc", "<i>（含逾期费" + currMonthPeriodsDo.getOverdueAmount().setScale(2, RoundingMode.HALF_UP)
                    + "元）</i>\n最后还款日 " + DateUtil.formatDateForPatternWithHyhen(currMonthPeriodsDo.getGmtPlanRepay()));
            result.put("status", STATUS_OVERDUE);
            return result;
        }
        // 待还未逾期
        if (currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.AWAIT_REPAY.getCode()) ||
                currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.PART_REPAY.getCode())) {
            result.put("amount", waitRepaymentAmount);
            result.put("billDesc", "最后还款日 " + DateUtil.formatDateForPatternWithHyhen(
                    currMonthPeriodsDo.getGmtPlanRepay()));
            result.put("status", STATUS_WAITREFUND);
            return result;
        }
        // 本月已结清，下月还款时间还没开始
        if (currMonthPeriodsDo.getStatus().equals(AfLoanPeriodStatusNew.FINISHED.getCode())) {
            List<AfLoanPeriodsDo> periodsDos = afLoanPeriodsService.listCanRepayPeriods(lastLoanDo.getRid());
            Iterator<AfLoanPeriodsDo> it = periodsDos.iterator();
            while (it.hasNext()) {
                AfLoanPeriodsDo e = it.next();
                if (e.getRid().equals(currMonthPeriodsDo.getRid())) {
                    if (it.hasNext()) {
                        AfLoanPeriodsDo nextMonthPeriodDos = it.next();
                        if (nextMonthPeriodDos.getGmtPlanRepay().after(new Date())) {
                            result.put("amount", "0.00");
                            result.put("billDesc", "提前结清可减免手续费哦！");
                            result.put("status", STATUS_NEXTWAITREFUND);
                            return result;
                        }
                    }
                }
            }
        }

        return result;
    }

    // 获取一个本月已出账单
    private AfBorrowBillDo getOneChildBill(Long userId, int isOut, int billYear, int billMonth) {
        AfBorrowBillQuery query = new AfBorrowBillQuery();
        query.setPageSize(1);
        query.setBillYear(billYear);
        query.setBillMonth(billMonth);
        query.setUserId(userId);
        query.setIsOut(isOut);
        List<AfBorrowBillDo> list = afBorrowBillService.getMonthBillList(query);
        return list.size() > 0 ? list.get(0) : null;
    }

    // 获取下个月未出账单
    private AfBorrowBillDo getNextMonthNotOutBorrowBill(Long userId) {
        AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
        query.setUserId(userId);
        query.setIsOut(0);

        Date strOutDay = DateUtil.getFirstOfMonth(new Date());
        strOutDay = DateUtil.addHoures(strOutDay, -12);
        strOutDay = DateUtil.addMonths(strOutDay, 1);
        Date endOutDay = DateUtil.addMonths(strOutDay, 1);
        query.setOutDayStr(strOutDay);
        query.setOutDayEnd(endOutDay);
        List<AfBorrowBillDo> nextMonthBills = afBorrowBillService.getUserBillListByQuery(query);

        return nextMonthBills.size() > 0 ? nextMonthBills.get(0) : null;
    }
}
