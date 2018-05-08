package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
@RequestMapping("/fanbei-web/h5/")
public class AppH5ProtocolRecycleController extends BaseController {

    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfUserService afUserService;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    AfRescourceLogService afRescourceLogService;
    @Resource
    AfBorrowRecycleOrderService borrowRecycleOrderService;

    /**
     * 回收协议
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"goodsRecoverProtocol"}, method = RequestMethod.GET)
    public void goodsRecoverProtocol(HttpServletRequest request, ModelMap model) throws IOException {
        String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
        String goodsName = ObjectUtils.toString(request.getParameter("goodsName"), "").toString();
        String goodsModel = ObjectUtils.toString(request.getParameter("goodsModel"), "").toString();
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
        BigDecimal riskDailyRate = NumberUtil.objToBigDecimalDefault(request.getParameter("riskDailyRate"), new BigDecimal(0));
        long borrowId = NumberUtil.objToLongDefault(request.getParameter("borrowId"), 0);
        Integer type = NumberUtil.objToIntDefault(request.getParameter("type"), 0);
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_RECYCLE_INFO_LEGAL_NEW.getCode());
        Map<String, Object> map = afResourceService.getRateInfo(afResourceDo.getValue2(), String.valueOf(type),"borrow","BORROW_RECYCLE_INFO_LEGAL_NEW");
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        model.put("idNumber", accountDo.getIdNumber());
        model.put("realName", accountDo.getRealName());
        model.put("email", afUserDo.getEmail());//电子邮箱
        model.put("mobile", afUserDo.getUserName());// 联系电话
        model.put("amountCapital", toCapital(amount.doubleValue()));//大写本金金额
        model.put("amount", amount);//借钱本金
        model.put("type", type);//借钱本金
        model.put("goodsName", goodsName);//借钱本金
        model.put("goodsModel",goodsModel);
        if (borrowId > 0) {//借了钱的借钱协议
            getRecycleProtocolWithBorrowId(model, borrowId);
        } else {//借钱前的借钱协议
            getRecycleProtocolWithWoutBorrowId(model, (Double) map.get("overdueRate"),riskDailyRate);
        }
    }
    private void getRecycleProtocolWithBorrowId(ModelMap model,Long borrowId) {
        AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);
        AfBorrowRecycleOrderDo recycleOrderDo = borrowRecycleOrderService.getBorrowRecycleOrderByBorrowId(borrowId);
        model.put("riskDailyRate",borrowCashDo.getRiskDailyRate());
        model.put("overdueRate",recycleOrderDo.getOverdueRate().divide(BigDecimal.valueOf(360),2,BigDecimal.ROUND_HALF_UP));
        model.put("gmtCreate",recycleOrderDo.getGmtCreate());
        model.put("borrowNo",borrowCashDo.getBorrowNo());
        model.put("goodsName", recycleOrderDo.getGoodsName());//借钱本金
        model.put("goodsModel",JSON.parseObject(recycleOrderDo.getPropertyValue()).get("goodsModel"));
        AfResourceDo companyInfo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_COMPANY_NAME.getCode(), AfResourceSecType.BORROW_CASH_COMPANY_NAME.getCode());
        model.put("lender", companyInfo.getValue());// 出借人
    }
    private void getRecycleProtocolWithWoutBorrowId(ModelMap model,Double overdueRate,BigDecimal riskDailyRate) {
        model.put("overdueRate",new BigDecimal(overdueRate).divide(BigDecimal.valueOf(360),2,BigDecimal.ROUND_HALF_UP));
        model.put("riskDailyRate",riskDailyRate);
        AfResourceDo companyInfo = afResourceService.getConfigByTypesAndSecType(ResourceType.BORROW_CASH_COMPANY_NAME.getCode(), AfResourceSecType.BORROW_CASH_COMPANY_NAME.getCode());
        model.put("lender", companyInfo.getValue());// 出借人
    }

    /**
     * 平台服务协议(白领贷)
     *
     * @param request
     * @param model
     * @throws IOException
     */
    @RequestMapping(value = {"recyclePlatformServiceProtocol"}, method = RequestMethod.GET)
    public void recyclePlatformServiceProtocol(HttpServletRequest request, ModelMap model) throws IOException {
        String userName = ObjectUtils.toString(request.getParameter("userName"), "").toString();
        BigDecimal totalServiceFee = NumberUtil.objToBigDecimalDefault(request.getParameter("totalServiceFee"), new BigDecimal(0));
        BigDecimal amount = NumberUtil.objToBigDecimalDefault(request.getParameter("amount"), new BigDecimal(0));
        Long borrowId = NumberUtil.objToLongDefault(request.getParameter("loanId"), 0l);
        Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
        AfUserDo afUserDo = afUserService.getUserByUserName(userName);
        if (afUserDo == null) {
            logger.error("user not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }
        Long userId = afUserDo.getRid();
        AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(userId);
        if (accountDo == null) {
            logger.error("account not exist" + FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
            throw new FanbeiException(FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
        }

        model.put("email", afUserDo.getEmail());//电子邮箱
        model.put("mobile", afUserDo.getUserName());// 联系电话
        model.put("realName", accountDo.getRealName());
        model.put("totalServiceFee", totalServiceFee);//手续费
        /*List<Object> resultList = afLoanPeriodsService.resolvePeriods(amount, userId, nper, "", "BLD_LOAN");
        if (resultList != null && resultList.size() > 0){
            AfLoanDo afLoanDo = (AfLoanDo) resultList.get(0);
            model.put("totalServiceFee", afLoanDo.getTotalServiceFee());//手续费
        }
        if (loanId > 0) {
            AfLoanDo afLoanDo = afLoanService.getById(loanId);
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
            getSeal(model, afUserDo, accountDo);
        }
*/
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

    public Map<String, Object> getObjectWithResourceDolist(List<AfResourceDo> list, Long borrowId) {
        Map<String, Object> data = new HashMap<String, Object>();
        AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(borrowId);

        for (AfResourceDo afResourceDo : list) {
            if (StringUtils.equals(afResourceDo.getType(), AfResourceType.borrowRate.getCode())) {
                if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashRange.getCode())) {

                    data.put("maxAmount", afResourceDo.getValue());
                    data.put("minAmount", afResourceDo.getValue1());

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashBaseBankDouble.getCode())) {
                    data.put("bankDouble", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashBaseBankDouble.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankDouble", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashPoundage.getCode())) {
                    data.put("poundage", afResourceDo.getValue());

                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashPoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("poundage", borrow.getValue());
                        }
                    }

                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BorrowCashOverduePoundage.getCode())) {
                    data.put("overduePoundage", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BorrowCashOverduePoundage.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("overduePoundage", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.BaseBankRate.getCode())) {
                    data.put("bankRate", afResourceDo.getValue());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.BaseBankRate.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("bankRate", borrow.getValue());
                        }
                    }
                } else if (StringUtils.equals(afResourceDo.getSecType(), AfResourceSecType.borrowCashLender.getCode())) {
                    data.put("lender", afResourceDo.getValue());
                    data.put("lenderIdNumber", afResourceDo.getValue1());
                    if (afBorrowCashDo != null) {
                        AfResourceLogDo logDo = afRescourceLogService.selectResourceLogTypeAndSecType(AfResourceType.borrowRate.getCode(), AfResourceSecType.borrowCashLender.getCode(), afBorrowCashDo.getGmtCreate());
                        if (logDo != null) {
                            AfResourceDo borrow = JSON.parseObject(logDo.getOldJson(), AfResourceDo.class);
                            data.put("lender", borrow.getValue());
                            data.put("lenderIdNumber", borrow.getValue1());

                        }
                    }
                }
            } else {
                if (StringUtils.equals(afResourceDo.getType(), AfResourceSecType.BorrowCashDay.getCode())) {
                    data.put("borrowCashDay", afResourceDo.getValue());

                }
            }
        }

        // rate.put("overduePoundage", data.get("overduePoundage"));
        // rate.put("bankService", bankService);
        // rate.put("poundage", data.get("poundage"));
        // rate.put("maxAmount", data.get("maxAmount"));
        // rate.put("minAmount", data.get("minAmount"));
        // rate.put("borrowCashDay", data.get("borrowCashDay"));

        return data;

    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        // TODO Auto-generated method stub
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
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        // TODO Auto-generated method stub
        return null;
    }

}
