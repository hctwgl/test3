package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedUserService;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRateDo;
import com.ald.fanbei.api.dal.domain.DsedUserDo;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * @author CFP 2017年12月19日下午1:41:05
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/dsed-web/h5/")
public class H5ProtocolWhiteLoanController {

    @Resource
    DsedUserService dsedUserService;

    @Resource
    DsedLoanService dsedLoanService;

    @Resource
    DsedLoanPeriodsService dsedLoanPeriodsService;

    @Resource
    DsedLoanProductService dsedLoanProductService;

    protected final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());


    /**
     * &#x501f;&#x94b1;&#x534f;&#x8bae;(&#x767d;&#x9886;&#x8d37;)
     *
     * @param
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"loanProtocol"}, method = RequestMethod.GET)
    public void LoanProtocol(HttpServletRequest request, ModelMap model) {
        Long userId = NumberUtil.objToLongDefault(request.getParameter("userId"), 0);
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
        Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
        long loanId = NumberUtil.objToLongDefault(request.getParameter("loanId"), 0);
        String loanRemark = ObjectUtils.toString(request.getParameter("loanRemark"), "").toString();
        String repayRemark = ObjectUtils.toString(request.getParameter("repayRemark"), "").toString();
        DsedUserDo dsedUserDo = dsedUserService.getById(userId);
        if (dsedUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        model.put("idNumber", dsedUserDo.getIdNumber());
        model.put("realName", dsedUserDo.getRealName());
        model.put("email", dsedUserDo.getEmail());//电子邮箱
        model.put("mobile", dsedUserDo.getMobile());// 联系电话
        model.put("amountCapital", toCapital(amount.doubleValue()));//大写本金金额
        model.put("amount", amount);//借钱本金
        if (loanId > 0) {//借了钱的借钱协议
            getModelLoanId(model, nper, loanId, dsedUserDo, repayRemark, loanRemark);
        } else {//借钱前的借钱协议
            getModelNoLoanId(model, amount, nper, "个人消费", repayRemark, userId);
        }
        logger.info(JSON.toJSONString(model));
    }

    /**
     * 平台服务协议(白领贷)
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"dsedLoanPlatformServiceProtocol"}, method = RequestMethod.GET)
    public void dsedLoanPlatformServiceProtocol(HttpServletRequest request, ModelMap model) throws IOException {
        Long userId = NumberUtil.objToLongDefault(request.getParameter("userId"), 0);
        BigDecimal totalServiceFee = NumberUtil.objToBigDecimalDefault(request.getParameter("totalServiceFee"), new BigDecimal(0));
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
        Long loanId = NumberUtil.objToLongDefault(request.getParameter("loanId"), 0l);
        Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
        DsedUserDo dsedUserDo = dsedUserService.getById(userId);
        if (dsedUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        model.put("email", dsedUserDo.getEmail());//电子邮箱
        model.put("mobile", dsedUserDo.getMobile());// 联系电话
        model.put("realName", dsedUserDo.getRealName());
        DsedLoanRateDo rateDo = dsedLoanProductService.getByPrdTypeAndNper("BLD_LOAN", String.valueOf(nper));
        if (rateDo != null){
            model.put("overdueRate", rateDo.getOverdueRate());//逾期费率
            model.put("serviceRate", rateDo.getPoundageRate());//手续费率
            model.put("interestRate", rateDo.getInterestRate());//借钱利率
        }
        model.put("totalServiceFee", totalServiceFee);//手续费
        List<Object> resultList = dsedLoanPeriodsService.resolvePeriods(amount, userId, nper, "", "BLD_LOAN");
        if (resultList != null && resultList.size() > 0){
            DsedLoanDo afLoanDo = (DsedLoanDo) resultList.get(0);
            model.put("totalServiceFee", afLoanDo.getTotalServiceFee());//手续费
        }
        if (loanId > 0) {
            DsedLoanDo afLoanDo = dsedLoanService.getById(loanId);
            if (null != afLoanDo) {
                model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
            }
            Calendar c = Calendar.getInstance();
            c.setTime(afLoanDo.getGmtCreate());
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DATE);
            int year = c.get(Calendar.YEAR);
            String time = year + "年" + month + "月" + day + "日";
            model.put("time", time);// 签署日期
            model.put("totalServiceFee", afLoanDo.getTotalServiceFee());//手续费
            model.put("overdueRate", afLoanDo.getOverdueRate());//逾期费率
            model.put("serviceRate", afLoanDo.getServiceRate());//手续费率
            model.put("interestRate", afLoanDo.getInterestRate());//借钱利率
        }

    }

    private void getModelLoanId(ModelMap model, Integer nper, long loanId, DsedUserDo afUserDo, String repayRemark, String loanRemark) {
        DsedLoanDo afLoanDo = dsedLoanService.selectById(loanId);
        Calendar c = Calendar.getInstance();
        c.setTime(afLoanDo.getGmtCreate());
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DATE);
        int year = c.get(Calendar.YEAR);
        String time = year + "年" + month + "月" + day + "日";
        model.put("time", time);// 签署日期
        model.put("gmtStart", time);
        model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
        List<DsedLoanPeriodsDo> afLoanPeriodsDoList = dsedLoanPeriodsService.getLoanPeriodsByLoanId(loanId);
        if (null != afLoanPeriodsDoList && afLoanPeriodsDoList.size() > 0) {
            List<Object> array = new ArrayList<Object>();
            for (int i = 1; i <= afLoanDo.getPeriods(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                DsedLoanPeriodsDo afLoanPeriodsDo = afLoanPeriodsDoList.get(i - 1);
                c.setTime(afLoanPeriodsDo.getGmtPlanRepay());
                int periodsMonth = c.get(Calendar.MONTH) + 1;
                int periodsDay = c.get(Calendar.DATE);
                int periodsYear = c.get(Calendar.YEAR);
                String periodsTime = periodsYear + "年" + periodsMonth + "月" + periodsDay + "日";
                if (i == nper) {
                    model.put("gmtEnd", periodsTime);
                    model.put("days", periodsDay);
                }
                map.put("days", day);
                map.put("gmtPlanRepay", periodsTime);
                map.put("loanAmount", afLoanPeriodsDo.getAmount());
                map.put("periods", i);
                map.put("fee", afLoanPeriodsDo.getInterestFee().add(afLoanPeriodsDo.getServiceFee()));
                array.add(map);
            }
            model.put("nperArray", array);
        }
        model.put("repayRemark", repayRemark);//还款方式
        model.put("loanRemark", afLoanDo.getLoanRemark());//借钱用途
        model.put("totalPeriods", afLoanDo);//总借钱信息
    }

    private void getModelNoLoanId(ModelMap model, BigDecimal amount, Integer nper, String loanRemark, String repayRemark, Long userId) {
        List<Object> resultList = dsedLoanPeriodsService.resolvePeriods(amount, userId, nper, "", "DSED_LOAN");
        if (null != resultList && resultList.size() > 0) {
            //借钱总汇
            DsedLoanDo afLoanDo = (DsedLoanDo) resultList.get(0);
            Calendar c = Calendar.getInstance();
            c.setTime(afLoanDo.getGmtCreate());
            int loanMonth = c.get(Calendar.MONTH) + 1;
            int loanDay = c.get(Calendar.DATE);
            int loanYear = c.get(Calendar.YEAR);
            String gmtStart = loanYear + "年" + loanMonth + "月" + loanDay + "日";
            model.put("gmtStart", gmtStart);
            model.put("loanNo", afLoanDo.getLoanNo());//原始借款协议编号
            //借钱分期
            List<Object> array = new ArrayList<Object>();
            for (int i = 1; i <= nper; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                DsedLoanPeriodsDo afLoanPeriodsDo = (DsedLoanPeriodsDo) resultList.get(i);
                c.setTime(afLoanPeriodsDo.getGmtPlanRepay());
                int month = c.get(Calendar.MONTH) + 1;
                int day = c.get(Calendar.DATE);
                int year = c.get(Calendar.YEAR);
                String time = year + "年" + month + "月" + day + "日";
                if (i == nper) {
                    model.put("gmtEnd", time);
                    model.put("days", day);
                }
                map.put("days", day);
                map.put("gmtPlanRepay", time);
                map.put("loanAmount", afLoanPeriodsDo.getAmount());
                map.put("periods", i);
                map.put("fee", afLoanPeriodsDo.getInterestFee().add(afLoanPeriodsDo.getServiceFee()));
                array.add(map);
            }
            model.put("nperArray", array);
            model.put("repayRemark", repayRemark);//还款方式
            model.put("loanRemark", loanRemark);//借钱用途
            model.put("totalPeriods", afLoanDo);//总借钱信息
        }
    }


    public static String ToBig(int num) {
        String str[] = {"壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖", "拾"};
        return str[num - 1];
    }

    public static String toCapital(double x) {
        DecimalFormat format = new DecimalFormat("#.00");
        String str = format.format(x);
        System.out.println(str);
        String s[] = str.split("\\.");
        String temp = "";
        int ling = 0;
        int shu = 0;
        int pos = 0;
        for (int j = 0; j < s[0].length(); ++j) {
            int num = s[0].charAt(j) - '0';
            if (num == 0) {
                ling++;
                if (ling == s[0].length()) {
                    temp = "零";
                } else if (s[0].length() - j - 1 == 4) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 5 && (s[0].length() - pos - 1) <= 7) {
                        temp += "万";
                    }
                } else if (s[0].length() - j - 1 == 8) {
                    if (shu == 1 && (s[0].length() - pos - 1) >= 9 && (s[0].length() - pos - 1) <= 11) {
                        temp += "亿";
                    }
                }
            } else {
                shu++;
                int flag = 0;
                if (shu == 1) {
                    ling = 0;
                    pos = j;
                }
                if (shu == 2) {
                    flag = 1;
                    if (ling > 0) {
                        temp += "零";
                    }
                    shu = 1;
                    pos = j;
                    ling = 0;
                }
                if (s[0].length() - j - 1 == 11) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 10) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 9) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 8) {
                    temp += ToBig(num) + "亿";
                } else if (s[0].length() - j - 1 == 7) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 6) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 5) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else if (s[0].length() - j - 1 == 4) {
                    temp += ToBig(num) + "万";
                } else if (s[0].length() - j - 1 == 3) {
                    temp += ToBig(num) + "仟";
                } else if (s[0].length() - j - 1 == 2) {
                    temp += ToBig(num) + "佰";
                } else if (s[0].length() - j - 1 == 1) {
                    if (num == 1 && flag != 1)
                        temp += "拾";
                    else
                        temp += ToBig(num) + "拾";
                } else {
                    temp += ToBig(num);
                }
            }
            // System.out.println(temp);
        }
        temp += "元";
        for (int j = 0; j < s[1].length(); ++j) {
            int num = s[1].charAt(j) - '0';
            if (j == 0) {
                if (num != 0)
                    temp += ToBig(num) + "角";
                else if (num == 0 && 1 < s[1].length() && s[1].charAt(1) != '0') {
                    temp += "零";
                }
            } else if (j == 1) {
                if (num != 0)
                    temp += ToBig(num) + "分";
            }
        }
        System.out.println(temp);
        return temp;
    }

}
