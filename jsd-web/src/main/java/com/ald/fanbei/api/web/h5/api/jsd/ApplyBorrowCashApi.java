package com.ald.fanbei.api.web.h5.api.jsd;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.jsd.JsdApplyBorrowCashBo;
import com.ald.fanbei.api.biz.bo.jsd.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.OriRateUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.DsedH5Handle;
import com.ald.fanbei.api.web.common.DsedH5HandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.validator.Validator;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowCashApi")
@Validator("applyBorrowCashParam")
public class ApplyBorrowCashApi implements DsedH5Handle {

    protected final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志
    // [start] 依赖注入
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdUserBankcardService jsdUserBankcardService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    UpsUtil upsUtil;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    OriRateUtil oriRateUtil;

    // [end]
    @Override
    public DsedH5HandleResponse process(Context context) {

        DsedH5HandleResponse resp = new DsedH5HandleResponse(200, "成功");
        JsdApplyBorrowCashBo borrowCashBo = getParam(context);
        if (StringUtils.isBlank(borrowCashBo.getOpenId()) || StringUtils.isBlank(borrowCashBo.getProductNo()) || StringUtils.isBlank(borrowCashBo.getAmount())
                || StringUtils.isBlank(borrowCashBo.getTerm()) || StringUtils.isBlank(borrowCashBo.getIsTying()) || StringUtils.isBlank(borrowCashBo.getTyingType())
                ) {
            return new DsedH5HandleResponse(FanbeiExceptionCode.JSD_PARAMS_ERROR);
        }
        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal borrowAmount = NumberUtil.objToBigDecimalDefault(borrowCashBo.getAmount(), BigDecimal.ZERO);
        BigDecimal oriRate = getRiskOriRate(borrowCashBo.getOpenId());
        try {
            BigDecimal profitAmount = getTiedGoodsInfo(borrowAmount, borrowCashBo.getTerm(), oriRate);
            if (profitAmount != null) {
                goodsAmount = profitAmount;
            }
        } catch (Exception e) {
            logger.error("get tied goods info error , msg => {}", e.getMessage());
        }
        JsdGoodsInfoBo jsdGoodsInfoBo = borrowCashBo.getJsdApplyBorrowCashBo();
        // 判断用户借款金额是否在后台配置的金额范围内
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG,
                Constants.JSD_RATE_INFO);

        JsdUserBankcardDo mainCard = jsdUserBankcardService.getByBankNo(borrowCashBo.getBankNo());

        String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + borrowCashBo.getUserId();
        boolean isGetLock = bizCacheUtil.getLock30Second(lockKey, "1");

        if (!isGetLock) {
            return new DsedH5HandleResponse(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
        }

        try {
            // 判断用户是否有借款未完成
            boolean borrowFlag = jsdBorrowCashService.isCanBorrowCash(borrowCashBo.getUserId());
            if (!borrowFlag) {
                return new DsedH5HandleResponse(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
            }
            final JsdBorrowCashDo jsdBorrowCashDo = buildBorrowCashDo(borrowAmount, borrowCashBo.getTerm(), mainCard,
                    borrowCashBo.getUserId(), rateInfoDo, oriRate);

            // 搭售商品订单
            final JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = buildBorrowLegalOrder(borrowAmount, borrowCashBo.getUserId(),
                    0l, jsdGoodsInfoBo.getGoodsName());

            // 订单借款
            final JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = buildBorrowLegalOrderCashDo(
                    goodsAmount, borrowCashBo.getTerm(), borrowCashBo.getUserId(), 0l, BigDecimal.ZERO, 0l, BigDecimal.ZERO, borrowCashBo.getLoanRemark(),
                    borrowCashBo.getRepayRemark(), rateInfoDo);

            Long borrowId = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus arg0) {
                    jsdBorrowCashService.saveRecord(jsdBorrowCashDo);
                    Long borrowId = jsdBorrowCashDo.getRid();
                    jsdBorrowLegalOrderDo.setBorrowId(borrowId);
                    // 新增搭售商品订单
                    jsdBorrowLegalOrderService.saveRecord(jsdBorrowLegalOrderDo);
                    Long orderId = jsdBorrowLegalOrderDo.getRid();
                    jsdBorrowLegalOrderCashDo.setBorrowId(borrowId);
                    jsdBorrowLegalOrderCashDo.setBorrowLegalOrderId(orderId);
                    jsdBorrowLegalOrderCashService.saveRecord(jsdBorrowLegalOrderCashDo);
                    return borrowId;
                }
            });
            // 生成借款信息失败
            if (borrowId == null) {
                return new DsedH5HandleResponse(FanbeiExceptionCode.ADD_JSD_BORROW_CASH_INFO_FAIL);
            }
            // 借过款的放入缓存，借钱按钮不需要高亮显示
            bizCacheUtil.saveRedistSetOne(Constants.HAVE_BORROWED, String.valueOf(borrowCashBo.getUserId()));
            final JsdBorrowCashDo cashDo = new JsdBorrowCashDo();
            cashDo.setRid(borrowId);
            try {
                String cardNo = mainCard.getBankCardNumber();
                // 主卡号不能为空
                if (StringUtils.isEmpty(cardNo)) {
                    return new DsedH5HandleResponse(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
                }
                delegatePay(borrowCashBo.getUserId(), borrowId, borrowCashBo.getBankNo(),
                        jsdBorrowLegalOrderDo, jsdBorrowLegalOrderCashDo);
                // 加入借款埋点信息,来自哪个包等
//                doMaidianLog(request, afBorrowCashDo, requestDataVo, context);
                return resp;
            } catch (Exception e) {
                logger.error("apply legal borrow cash  error", e);
                // 关闭借款
                cashDo.setStatus(JsdBorrowCashStatus.CLOSED.getCode());
                // 关闭搭售商品订单
                jsdBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.getCode());
                // 关闭搭售商品借款
                jsdBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.getCode());

                // 如果属于非爱上街自定义异常，比如风控请求504等，则把风控状态置为待审核，同时添加备注说明，保证用户不会因为此原因进入借贷超市页面
                transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus ts) {
                        jsdBorrowCashService.updateById(cashDo);
                        jsdBorrowLegalOrderCashService.updateById(jsdBorrowLegalOrderCashDo);
                        jsdBorrowLegalOrderService.updateById(jsdBorrowLegalOrderDo);
                        return "success";
                    }
                });
                throw new FanbeiException(FanbeiExceptionCode.ADD_JSD_BORROW_CASH_INFO_FAIL);
            }
        } finally {
            bizCacheUtil.delCache(lockKey);
        }

    }

    public JsdApplyBorrowCashBo getParam(Context context) {
        JsdApplyBorrowCashBo borrowCashBo = new JsdApplyBorrowCashBo();
        borrowCashBo.setOpenId(context.getOpenId());
        borrowCashBo.setUserId(context.getUserId());
        borrowCashBo.setAmount(ObjectUtils.toString(context.getData("amount")));
        borrowCashBo.setBankNo(ObjectUtils.toString(context.getData("bankNo")));
        borrowCashBo.setIsTying(ObjectUtils.toString(context.getData("isTying")));
        borrowCashBo.setBorrowNo(ObjectUtils.toString(context.getData("borrowNo")));
        borrowCashBo.setLoanRemark(ObjectUtils.toString(context.getData("loanRemark")));
        borrowCashBo.setProductNo(ObjectUtils.toString(context.getData("productNo")));
        borrowCashBo.setRepayRemark(ObjectUtils.toString(context.getData("repayRemark")));
        borrowCashBo.setTerm(ObjectUtils.toString(context.getData("term")));
        borrowCashBo.setTyingType(ObjectUtils.toString(context.getData("tyingType")));
        borrowCashBo.setUnit(ObjectUtils.toString(context.getData("unit")));
        borrowCashBo.setJsdApplyBorrowCashBo((JsdGoodsInfoBo) context.getData("goodsInfo"));
        return borrowCashBo;
    }

    ;

    private BigDecimal getRiskOriRate(String openId) {
        BigDecimal oriRate = BigDecimal.valueOf(0.001);

        try {
            String poundageRate = oriRateUtil.getOriRateNoticeRequest(openId);
            if(StringUtils.isBlank(poundageRate)) {
                poundageRate = "0.001";
            }
            oriRate = new BigDecimal(poundageRate);
        } catch (Exception e) {
            logger.info(openId + "从西瓜信用获取分层用户额度失败：" + e);
        }
        return oriRate;
    }

    private BigDecimal getTiedGoodsInfo(BigDecimal borrowAmount, String borrowType, BigDecimal oriRate) {

        BigDecimal borrowDay = BigDecimal.ZERO;
        borrowDay = BigDecimal.valueOf(Integer.parseInt(borrowType));

        // 查询新利率配置
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG,
                Constants.JSD_RATE_INFO);
        BigDecimal newRate = null;
        if (rateInfoDo != null) {
            String borrowRate = rateInfoDo.getValue2();
            Map<String, Object> rateInfo = getRateInfo(borrowRate, borrowType, "borrow");
            double totalRate = (double) rateInfo.get("totalRate");
            newRate = BigDecimal.valueOf(totalRate / 100);
        } else {
            newRate = BigDecimal.valueOf(0.36);
        }
        newRate = newRate.divide(BigDecimal.valueOf(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        BigDecimal profitAmount = oriRate.subtract(newRate).multiply(borrowAmount).multiply(borrowDay);
        if (profitAmount.compareTo(BigDecimal.ZERO) <= 0) {
            profitAmount = BigDecimal.ZERO;
        }
        return profitAmount;
    }

    private Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag) {
        JsdResourceDo afResourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.BORROW_RATE.getCode(), AfResourceSecType.BORROW_CASH_INFO_LEGAL_NEW.getCode());
        String oneDay = "";
        String twoDay = "";
        if (null != afResourceDo) {
            oneDay = afResourceDo.getTypeDesc().split(",")[0];
            twoDay = afResourceDo.getTypeDesc().split(",")[1];
        }
        Map<String, Object> rateInfo = Maps.newHashMap();
        double serviceRate = 0;
        double interestRate = 0;
        JSONArray array = JSONObject.parseArray(borrowRate);
        double totalRate = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String borrowTag = info.getString(tag + "Tag");
            if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    interestRate = info.getDouble(tag + "FirstType");
                    totalRate += interestRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    interestRate = info.getDouble(tag + "SecondType");
                    totalRate += interestRate;
                }
            }
            if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "FirstType");
                    totalRate += serviceRate;
                } else if (StringUtils.equals(twoDay, borrowType)) {
                    serviceRate = info.getDouble(tag + "SecondType");
                    totalRate += serviceRate;
                }
            }

        }
        rateInfo.put("serviceRate", serviceRate);
        rateInfo.put("interestRate", interestRate);
        rateInfo.put("totalRate", totalRate);
        return rateInfo;
    }

    private void delegatePay(Long userId, Long borrowId, String bankNo,
                             final JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo, final JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo) {
        final JsdBorrowCashDo cashDo = new JsdBorrowCashDo();
        Date currDate = new Date();
        JsdUserDo afUserDo = jsdUserService.getById(userId);
        final JsdBorrowCashDo afBorrowCashDo = jsdBorrowCashService.getById(borrowId);
        cashDo.setRid(afBorrowCashDo.getRid());

        JsdUserBankcardDo card = jsdUserBankcardService.getByBankNo(bankNo);

        List<String> whiteIdsList = new ArrayList<String>();
        final int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
        // 打款
        UpsDelegatePayRespBo upsResult = upsUtil.jsdDelegatePay(afBorrowCashDo.getArrivalAmount(),
                afUserDo.getRealName(), card.getBankCardNumber(), userId + "", card.getMobile(),
                card.getBankName(), card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
                "JSD_LOAN", afBorrowCashDo.getRid() + "", afUserDo.getIdNumber());
//			Integer day = NumberUtil
//					.objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
        Integer day = borrowTime(afBorrowCashDo.getType());
        Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(cashDo.getGmtArrival());
        Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
        cashDo.setGmtPlanRepayment(repaymentDay);
        if (!upsResult.isSuccess()) {
            // 大款失败，更新状态
            logger.info("upsResult error:" + FanbeiExceptionCode.BANK_CARD_PAY_ERR);
            cashDo.setStatus(JsdBorrowCashStatus.TRANSEDFAIL.getCode());
            // 关闭订单
            jsdBorrowLegalOrderDo.setStatus(JsdBorrowCashStatus.CLOSED.getCode());
            jsdBorrowLegalOrderDo.setClosedDetail("transed fail");
            // 更新订单借钱状态为失败
            jsdBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.getCode());

        }

        transactionTemplate.execute(new TransactionCallback<String>() {
            @Override
            public String doInTransaction(TransactionStatus status) {
                // 更新借款状态
                jsdBorrowCashService.updateById(cashDo);
                // 更新订单状态
                jsdBorrowLegalOrderService.updateById(jsdBorrowLegalOrderDo);
                // 更新订单借款状态
                jsdBorrowLegalOrderCashService.updateById(jsdBorrowLegalOrderCashDo);
                return "success";
            }
        });
    }


    public JsdBorrowLegalOrderCashDo buildBorrowLegalOrderCashDo(BigDecimal goodsAmount, String type, Long userId,
                                                                 Long orderId, BigDecimal poundage, Long borrowId, BigDecimal overdueAmount, String borrowRemark,
                                                                 String refundRemark, JsdResourceDo rateInfoDo) {
        Integer day = NumberUtil.objToIntDefault(type, 0);
        String oneDay = "";
        String twoDay = "";
        if (null != rateInfoDo) {
            oneDay = rateInfoDo.getTypeDesc().split(",")[0];
            twoDay = rateInfoDo.getTypeDesc().split(",")[1];
        }
        // 计算手续费和利息
        String borrowRate = rateInfoDo.getValue3();
        JSONArray array = JSONObject.parseArray(borrowRate);
        double interestRate = 0; // 利率
        double serviceRate = 0; // 手续费率
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String consumeTag = info.getString("consumeTag");
            if (StringUtils.equals("INTEREST_RATE", consumeTag)) {
                if (StringUtils.equals(oneDay, type)) {
                    interestRate = info.getDouble("consumeFirstType");
                } else if (StringUtils.equals(twoDay, type)) {
                    interestRate = info.getDouble("consumeSecondType");
                }
            } else if (StringUtils.equals("SERVICE_RATE", consumeTag)) {
                if (StringUtils.equals(oneDay, type)) {
                    serviceRate = info.getDouble("consumeFirstType");
                } else if (StringUtils.equals(twoDay, type)) {
                    serviceRate = info.getDouble("consumeSecondType");
                }
            }
        }
        BigDecimal rateAmount = new BigDecimal(interestRate / 100).multiply(new BigDecimal(day)).multiply(goodsAmount)
                .divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

        BigDecimal poundageAmount = new BigDecimal(serviceRate / 100).multiply(new BigDecimal(day))
                .multiply(goodsAmount).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
        JsdBorrowLegalOrderCashDo afBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
        afBorrowLegalOrderCashDo.setAmount(goodsAmount);
        afBorrowLegalOrderCashDo.setType(type);
        afBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderCashStatus.APPLYING.getCode());
        afBorrowLegalOrderCashDo.setUserId(userId);
        afBorrowLegalOrderCashDo.setPoundageRate(BigDecimal.valueOf(serviceRate));
        afBorrowLegalOrderCashDo.setInterestRate(BigDecimal.valueOf(interestRate));
        afBorrowLegalOrderCashDo.setBorrowLegalOrderId(orderId);
        afBorrowLegalOrderCashDo.setBorrowId(borrowId);
        afBorrowLegalOrderCashDo.setOverdueAmount(overdueAmount);
        afBorrowLegalOrderCashDo.setBorrowRemark(borrowRemark);
        afBorrowLegalOrderCashDo.setRefundRemark(refundRemark);
        afBorrowLegalOrderCashDo.setInterestAmount(rateAmount);
        afBorrowLegalOrderCashDo.setPoundageAmount(poundageAmount);
        afBorrowLegalOrderCashDo.setOverdueStatus("N");
        // 获取借款天数
        Integer planRepayDays = NumberUtil.objToIntDefault(type, 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, planRepayDays - 1);
        afBorrowLegalOrderCashDo.setPlanRepayDays(planRepayDays);
        afBorrowLegalOrderCashDo.setGmtPlanRepay(cal.getTime());
        return afBorrowLegalOrderCashDo;
    }

    public JsdBorrowLegalOrderDo buildBorrowLegalOrder(BigDecimal goodsAmount, Long userId, Long borrowId,
                                                       String goodsName) {
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(userId);
        afBorrowLegalOrderDo.setBorrowId(borrowId);
        afBorrowLegalOrderDo.setPriceAmount(goodsAmount);
        afBorrowLegalOrderDo.setGoodsName(goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        return afBorrowLegalOrderDo;
    }

    public JsdBorrowCashDo buildBorrowCashDo(BigDecimal amount, String type,
                                             JsdUserBankcardDo jsdUserBankcardDo, Long userId,
                                             JsdResourceDo rateInfoDo, BigDecimal oriRate) {
        String oneDay = "";
        String twoDay = "";
        if (null != rateInfoDo) {
            oneDay = rateInfoDo.getTypeDesc().split(",")[0];
            twoDay = rateInfoDo.getTypeDesc().split(",")[1];
        }
/*		List<AfResourceDo> list = afResourceService.selectBorrowHomeConfigByAllTypes();
		Map<String, Object> rate = getObjectWithResourceDolist(list);

		this.checkSwitch(rate, currentDay);*/
//		BigDecimal bankRate = new BigDecimal(rate.get("bankRate").toString());

        Integer day = NumberUtil.objToIntDefault(type, 0);
        // 计算手续费和利息
        String borrowRate = rateInfoDo.getValue2();
        JSONArray array = JSONObject.parseArray(borrowRate);
        double interestRate = 0; // 利率
        double serviceRate = 0; // 手续费率
        for (int i = 0; i < array.size(); i++) {
            JSONObject info = array.getJSONObject(i);
            String borrowTag = info.getString("borrowTag");
            if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, type)) {
                    interestRate = info.getDouble("borrowFirstType");
                } else if (StringUtils.equals(twoDay, type)) {
                    interestRate = info.getDouble("borrowSecondType");
                }
            } else if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
                if (StringUtils.equals(oneDay, type)) {
                    serviceRate = info.getDouble("borrowFirstType");
                } else if (StringUtils.equals(twoDay, type)) {
                    serviceRate = info.getDouble("borrowSecondType");
                }
            }
        }
        // FIXME
        BigDecimal rateAmount = new BigDecimal(interestRate / 100).multiply(amount).multiply(new BigDecimal(day))
                .divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

        BigDecimal poundageAmount = new BigDecimal(serviceRate / 100).multiply(amount).multiply(new BigDecimal(day))
                .divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);

        JsdBorrowCashDo afBorrowCashDo = new JsdBorrowCashDo();
        afBorrowCashDo.setAmount(amount);
        afBorrowCashDo.setCardName(jsdUserBankcardDo.getBankName());
        afBorrowCashDo.setCardNumber(jsdUserBankcardDo.getBankCardNumber());
        afBorrowCashDo.setType(type);
        afBorrowCashDo.setStatus(JsdBorrowCashStatus.APPLY.getCode());
        afBorrowCashDo.setUserId(userId);
        afBorrowCashDo.setRateAmount(rateAmount);
        afBorrowCashDo.setPoundage(poundageAmount);
        // 到账金额和借款金额相同
        afBorrowCashDo.setArrivalAmount(amount);
        afBorrowCashDo.setPoundageRate(new BigDecimal(serviceRate));
        afBorrowCashDo.setRiskDailyRate(oriRate);
        return afBorrowCashDo;
    }

    /**
     * 计算最多能计算多少额度 150取100 250.37 取200
     *
     * @param usableAmount
     * @return
     */
    private BigDecimal calculateMaxAmount(BigDecimal usableAmount) {
        // 可使用额度
        Integer amount = usableAmount.intValue();
        return new BigDecimal(amount / 100 * 100);
    }

    /**
     * 记录埋点相关日志日志
     *
     * @param request
     * @param cashDo
     * @param requestDataVo
     * @param context
     */
    private void doMaidianLog(HttpServletRequest request, JsdBorrowCashDo cashDo, RequestDataVo requestDataVo,
                              FanbeiContext context) {
        try {
            // 获取可变参数
            String ext1 = cashDo.getBorrowNo();
            String ext2 = cashDo.getUserId() + "";
            String ext3 = cashDo.getAmount() + "";
            String ext4 = context.getAppVersion() + "";
            maidianLog.info(com.ald.fanbei.api.common.util.StringUtil.appendStrs("	",
                    DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
                    com.ald.fanbei.api.common.util.StringUtil.judgeClientDeviceFlag(requestDataVo.getId()), "	rmtIP=",
                    CommonUtil.getIpAddr(request), "	userName=", context.getUserName(), "	", 0, "	",
                    request.getRequestURI(), "	", cashDo.getRid() + "", "	",
                    DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "jsdUserBorrowCashApply", "	", ext1,
                    "	", ext2, "	", ext3, "	", ext4,
                    "	reqD=", "appFlag:" + requestDataVo.getId() + ",appVersion:" + context.getAppVersion()
                            + ",userId=" + cashDo.getUserId() + ",cashAmount:" + cashDo.getAmount(),
                    "	resD=", "null"));
        } catch (Exception e) {
            logger.error("jsdUserBorrowCashApply maidian logger error", e);
        }
    }

    /**
     * 借款时间
     *
     * @param
     * @return
     */
    public int borrowTime(final String type) {
        Integer day;
        if (isNumeric(type)) {
            day = Integer.parseInt(type);
        } else {
            day = numberWordFormat.parse(type.toLowerCase());
        }
        return day;
    }

    /**
     * 是否是数字字符串
     *
     * @param type
     * @return
     */
    private boolean isNumeric(String type) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(type);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
