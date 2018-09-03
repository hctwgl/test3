package com.ald.fanbei.api.web.h5.api.jsd;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.jsd.JsdApplyBorrowCashBo;
import com.ald.fanbei.api.biz.bo.jsd.JsdGoodsInfoBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdUserBankcardService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.biz.third.util.OriRateUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.JsdBorrowCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.enums.JsdBorrowLegalOrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.web.common.Context;
import com.ald.fanbei.api.web.common.JsdH5Handle;
import com.ald.fanbei.api.web.common.JsdH5HandleResponse;
import com.ald.fanbei.api.web.validator.Validator;
import com.ald.fanbei.api.web.validator.bean.ApplyBorrowCashParam;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;

/**
 * @author Jiang Rongbo 2017年3月25日下午1:06:18
 * @类描述：申请借钱
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("applyBorrowCashApi")
@Validator("applyBorrowCashParam")
public class ApplyBorrowCashApi implements JsdH5Handle {

    
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
    JsdBorrowLegalOrderService jsdBorrowLegalOrderService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    
    @Resource
    UpsUtil upsUtil;
    @Resource
    NumberWordFormat numberWordFormat;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    OriRateUtil oriRateUtil;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    // [end]
    
    @Override
    public JsdH5HandleResponse process(Context context) {
    	this.lock(context.getUserId());
    	
        JsdH5HandleResponse resp = new JsdH5HandleResponse(200, "成功");
        JsdApplyBorrowCashBo applyCashBo = this.resolveParam(context);
        
        BigDecimal goodsAmount = BigDecimal.ZERO;
        BigDecimal borrowAmount = applyCashBo.getAmount();
        BigDecimal oriRate = getRiskOriRate(applyCashBo.getOpenId());
        
        BigDecimal profitAmount = this.resolveProfit(borrowAmount, applyCashBo.getTerm(), oriRate);
        if (profitAmount != null) {
            goodsAmount = profitAmount;
        }
        
        JsdGoodsInfoBo jsdGoodsInfoBo = applyCashBo.getJsdGoodsInfoBo();
        
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        JsdUserBankcardDo mainCard = jsdUserBankcardService.getByBankNo(applyCashBo.getBankNo());
        try {
            // 判断用户是否有借款未完成
            boolean borrowFlag = jsdBorrowCashService.isCanBorrowCash(applyCashBo.getUserId());
            if (!borrowFlag) {
                return new JsdH5HandleResponse(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
            }
            // 主借款
            final JsdBorrowCashDo jsdBorrowCashDo = buildBorrowCashDo(borrowAmount, applyCashBo.getTerm(), mainCard, applyCashBo.getUserId(), rateInfoDo, oriRate,applyCashBo.getProductNo());

            // 搭售商品订单
            final JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo = buildBorrowLegalOrder(borrowAmount, applyCashBo.getUserId(), 0l, jsdGoodsInfoBo.getGoodsName());

            // 订单借款
            final JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = buildBorrowLegalOrderCashDo(
                    goodsAmount, applyCashBo.getTerm(), applyCashBo.getUserId(), 0l, BigDecimal.ZERO, 0l, BigDecimal.ZERO, applyCashBo.getLoanRemark(),
                    applyCashBo.getRepayRemark(), rateInfoDo);

            Long borrowId = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus arg0) {
                    jsdBorrowCashService.saveRecord(jsdBorrowCashDo);
                    Long borrowId = jsdBorrowCashDo.getRid();
                    jsdBorrowLegalOrderDo.setBorrowId(borrowId);
                    jsdBorrowLegalOrderService.saveRecord(jsdBorrowLegalOrderDo);// 新增搭售商品订单
                    Long orderId = jsdBorrowLegalOrderDo.getRid();
                    jsdBorrowLegalOrderCashDo.setBorrowId(borrowId);
                    jsdBorrowLegalOrderCashDo.setBorrowLegalOrderId(orderId);
                    jsdBorrowLegalOrderCashService.saveRecord(jsdBorrowLegalOrderCashDo);
                    return borrowId;
                }
            });
            if (borrowId == null) {// 生成借款信息失败
                return new JsdH5HandleResponse(FanbeiExceptionCode.ADD_JSD_BORROW_CASH_INFO_FAIL);
            }
            final JsdBorrowCashDo cashDoForUpdate = new JsdBorrowCashDo();
            cashDoForUpdate.setRid(borrowId);
            try {
                delegatePay(applyCashBo.getUserId(), borrowId, applyCashBo.getBankNo(), jsdBorrowLegalOrderDo, jsdBorrowLegalOrderCashDo);
                return resp;
            } catch (Exception e) {
                logger.error("apply legal borrow cash  error", e);
                cashDoForUpdate.setStatus(JsdBorrowCashStatus.CLOSED.getCode());// 关闭借款
                cashDoForUpdate.setRemark("Exception when delegatePay!");
                jsdBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderCashStatus.CLOSED.getCode());// 关闭搭售商品订单
                jsdBorrowLegalOrderCashDo.setStatus(JsdBorrowLegalOrderStatus.CLOSED.getCode());// 关闭搭售商品借款

                transactionTemplate.execute(new TransactionCallback<String>() {
                    @Override
                    public String doInTransaction(TransactionStatus ts) {
                        jsdBorrowCashService.updateById(cashDoForUpdate);
                        jsdBorrowLegalOrderCashService.updateById(jsdBorrowLegalOrderCashDo);
                        jsdBorrowLegalOrderService.updateById(jsdBorrowLegalOrderDo);
                        return "success";
                    }
                });
                throw new FanbeiException(FanbeiExceptionCode.DELEGATEPAY_DIRECT_FAIL);
            }
        } finally {
            this.unLock(context.getUserId());
        }

    }
    
    public JsdApplyBorrowCashBo resolveParam(Context context) {
    	ApplyBorrowCashParam param = (ApplyBorrowCashParam) context.getParamEntity();
    	
        JsdApplyBorrowCashBo applyCashBo = new JsdApplyBorrowCashBo();
        applyCashBo.setOpenId(context.getOpenId());
        applyCashBo.setUserId(context.getUserId());
        
        applyCashBo.setAmount(param.getAmount());
        applyCashBo.setBankNo(param.getBankNo());
        applyCashBo.setIsTying(param.getIsTying());
        applyCashBo.setBorrowNo(param.getBorrowNo());
        applyCashBo.setLoanRemark(param.getLoanRemark());
        applyCashBo.setProductNo(param.getProductNo());
        applyCashBo.setRepayRemark(param.getRepayRemark());
        applyCashBo.setTerm(param.getTerm());
        applyCashBo.setTyingType(param.getTyingType());
        applyCashBo.setUnit(param.getUnit());
        applyCashBo.setJsdGoodsInfoBo(param.getGoodsInfo());
        
        return applyCashBo;
    }

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

    private BigDecimal resolveProfit(BigDecimal borrowAmount, String borrowType, BigDecimal oriRate) {
        BigDecimal borrowDay = BigDecimal.ZERO;
        borrowDay = BigDecimal.valueOf(Integer.parseInt(borrowType));

        // 查询新利率配置
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
        BigDecimal newRate = null;
        if (rateInfoDo != null) {
            String borrowRate = rateInfoDo.getValue();
            Map<String, Object> rateInfo = this.getRateInfo(borrowRate, borrowType, "borrow");
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
        JsdResourceDo rateInfoDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG,
                Constants.JSD_RATE_INFO);
        String oneDay = "";
        String twoDay = "";
        if (null != rateInfoDo) {
            oneDay = rateInfoDo.getTypeDesc().split(",")[0];
            twoDay = rateInfoDo.getTypeDesc().split(",")[1];
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
    
    /**
     * 执行打款
     * 
     * @param userId
     * @param borrowId
     * @param bankNo
     * @param jsdBorrowLegalOrderDo
     * @param jsdBorrowLegalOrderCashDo
     */
    private void delegatePay(Long userId, Long borrowId, String bankNo,
                             final JsdBorrowLegalOrderDo jsdBorrowLegalOrderDo, final JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo) {
        final JsdBorrowCashDo cashDo = new JsdBorrowCashDo();
        JsdUserDo afUserDo = jsdUserService.getById(userId);
        final JsdBorrowCashDo afBorrowCashDo = jsdBorrowCashService.getById(borrowId);
        cashDo.setRid(afBorrowCashDo.getRid());

        JsdUserBankcardDo card = jsdUserBankcardService.getByBankNo(bankNo);

        // 打款
        UpsDelegatePayRespBo upsResult = upsUtil.jsdDelegatePay(afBorrowCashDo.getArrivalAmount(),
                afUserDo.getRealName(), card.getBankCardNumber(), userId + "", card.getMobile(),
                card.getBankName(), card.getBankCode(), Constants.DEFAULT_BORROW_PURPOSE, "02",
                "JSD_LOAN", afBorrowCashDo.getRid() + "", afUserDo.getIdNumber());
        Integer day = borrowTime(afBorrowCashDo.getType());
        Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(cashDo.getGmtArrival());
        Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
        cashDo.setGmtPlanRepayment(repaymentDay);
        if (!upsResult.isSuccess()) {
            // 打款款失败，更新状态
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


    /**
     * 构建 主借款
     * @param amount
     * @param type
     * @param jsdUserBankcardDo
     * @param userId
     * @param rateInfoDo
     * @param oriRate
     * @param productNo
     * @return
     */
    private JsdBorrowCashDo buildBorrowCashDo(BigDecimal amount, String type,
                                             JsdUserBankcardDo jsdUserBankcardDo, Long userId,
                                             JsdResourceDo rateInfoDo, BigDecimal oriRate,String productNo) {
        String oneDay = "";
        String twoDay = "";
        if (null != rateInfoDo) {
            oneDay = rateInfoDo.getTypeDesc().split(",")[0];
            twoDay = rateInfoDo.getTypeDesc().split(",")[1];
        }
        Integer day = NumberUtil.objToIntDefault(type, 0);
        // 计算手续费和利息
        String borrowRate = rateInfoDo.getValue();
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
        afBorrowCashDo.setProductNo(productNo);
        afBorrowCashDo.setBorrowNo(generatorClusterNo.getLoanNo(new Date()));
        afBorrowCashDo.setRepayPrinciple(BigDecimal.ZERO);
        return afBorrowCashDo;
    }
    
    /**
     * 构建 商品订单
     * @param goodsAmount
     * @param userId
     * @param borrowId
     * @param goodsName
     * @return
     */
    private JsdBorrowLegalOrderDo buildBorrowLegalOrder(BigDecimal goodsAmount, Long userId, Long borrowId,
                                                       String goodsName) {
        JsdBorrowLegalOrderDo afBorrowLegalOrderDo = new JsdBorrowLegalOrderDo();
        afBorrowLegalOrderDo.setUserId(userId);
        afBorrowLegalOrderDo.setBorrowId(borrowId);
        afBorrowLegalOrderDo.setPriceAmount(goodsAmount);
        afBorrowLegalOrderDo.setGoodsName(goodsName);
        afBorrowLegalOrderDo.setStatus(JsdBorrowLegalOrderStatus.UNPAID.getCode());
        String orderCashNo = generatorClusterNo.getOrderNo(OrderType.LEGAL);
        afBorrowLegalOrderDo.setOrderNo(orderCashNo);
        return afBorrowLegalOrderDo;
    }
    
    /**
     * 构建 商品借款 
     * @param goodsAmount
     * @param type
     * @param userId
     * @param orderId
     * @param poundage
     * @param borrowId
     * @param overdueAmount
     * @param borrowRemark
     * @param refundRemark
     * @param rateInfoDo
     * @return
     */
    private JsdBorrowLegalOrderCashDo buildBorrowLegalOrderCashDo(BigDecimal goodsAmount, String type, Long userId,
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

        BigDecimal poundageAmount = new BigDecimal(serviceRate / 100).multiply(new BigDecimal(day)).multiply(goodsAmount).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP);
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
        afBorrowLegalOrderCashDo.setOverdueStatus(YesNoStatus.NO.getCode());
        
        // 获取借款天数
        Integer planRepayDays = NumberUtil.objToIntDefault(type, 0);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, planRepayDays - 1);
        afBorrowLegalOrderCashDo.setPlanRepayDays(planRepayDays);
        afBorrowLegalOrderCashDo.setGmtPlanRepay(cal.getTime());
        String cashNo = generatorClusterNo.geBorrowLegalOrderCashNo(new Date());
        afBorrowLegalOrderCashDo.setCashNo(cashNo);
        return afBorrowLegalOrderCashDo;
    }

    private void lock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
        if (!bizCacheUtil.getLock30Second(lockKey, "1")) {
        	throw new FanbeiException(FanbeiExceptionCode.JSD_BORROW_CASH_STATUS_ERROR);
        }
    }
    private void unLock(Long userId) {
    	String lockKey = Constants.CACHEKEY_APPLY_BORROW_CASH_LOCK + userId;
    	bizCacheUtil.delCache(lockKey);
    }
    
    /**
     * 借款时间
     */
    private int borrowTime(final String type) {
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
