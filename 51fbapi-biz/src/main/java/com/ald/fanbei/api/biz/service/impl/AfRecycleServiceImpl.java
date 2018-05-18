package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.biz.third.util.RecycleUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.enums.recycle.AfRecycleOrderType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @discribe： 有得卖  回收业务
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRecycleService")
public class AfRecycleServiceImpl implements AfRecycleService {
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private AfRecycleDao afRecycleDao;
    @Autowired
    private AfUserAccountDao afUserAccountDao;
    @Autowired
    private AfUserDao afUserDao;
    @Autowired
    private AfUserCouponDao afUserCouponDao;
    @Autowired
    private AfCouponDao afCouponDao;
    @Autowired
    private AfRecycleTradeDao afRecycleTradeDao;
    @Autowired
    private AfResourceDao afResourceDao;
    @Autowired
    private SmsUtil smsUtil;
    @Autowired
    private BizCacheUtil bizCacheUtil;
    @Resource
    AfTaskUserService afTaskUserService;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 有得卖推单，订单添加事物
     *
     * @param afRecycleQuery 实体对象
     * @return 返回整数
     */
    @Override
    public Integer addRecycleOrder(final AfRecycleQuery afRecycleQuery) {
        return transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus transactionStatus) {
                if (null != afRecycleQuery && AfRecycleOrderType.CONFIRM_PAY.getCode().equals(afRecycleQuery.getStatus())) {
                    afRecycleDao.addRecycleOrder(afRecycleQuery);//添加订单记录
                    //回调有得卖确认接口
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", String.valueOf(afRecycleQuery.getUserId()));
                    map.put("orderId", afRecycleQuery.getRefOrderId());
                    map.put("payType", String.valueOf(afRecycleQuery.getPayType()));
                    map.put("settlePrice", String.valueOf(afRecycleQuery.getSettlePrice()));
                    map.put("partnerId", afRecycleQuery.getPartnerId());
                    String sign = SignUtil.signForYdm(RecycleUtil.createLinkString(map), RecycleUtil.PRIVATE_KEY);
                    map.put("sign", sign);
                    String baseUrl = RecycleUtil.CALLBACK__BASE_URL_ONLINE;
                    if (StringUtils.isNotBlank(afRecycleQuery.getUrl())) {
                        baseUrl = afRecycleQuery.getUrl();
                    } else {
                        if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE), Constants.INVELOMENT_TYPE_TEST)) {
                            baseUrl = RecycleUtil.CALLBACK_BASE_URL_TEST;
                        }
                    }
                    logger.info("=========================================" + baseUrl + "/fanbei/ydm/addRecycleOrder,baseUrl=" + baseUrl);
                    String postResult = HttpUtil.post(baseUrl + RecycleUtil.YDM_CALLBACK_URL, map);//向有得卖进行握手
                    logger.info("=========================================" + baseUrl + "/fanbei/ydm/addRecycleOrder,resp=" + postResult);
                    JSONObject jsonObject = JSONObject.parseObject(postResult);
                    if (null != jsonObject && StringUtils.equals("1", jsonObject.getString("code"))) {//返回成功
                        //给用户账号添加回收订单金额
                        AfRecycleRatioDo afRecycleRatioDo = afRecycleDao.getRecycleReturnRatio();
                        Long userId = afRecycleQuery.getUserId();
                        BigDecimal settlePrice = afRecycleQuery.getSettlePrice();

                        //修改订单状态为已完成
                        afRecycleQuery.setStatus(AfRecycleOrderType.FINISH.getCode());
                        afRecycleDao.updateRecycleOrder(afRecycleQuery);

                        BigDecimal amount = afUserAccountDao.getAuAmountByUserId(userId);//查找用户账号信息
                        BigDecimal remainAmount;
                        BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice).setScale(2, 2);//取2位小数
                        if (null == amount) {//用户账号信息不存在,则需要添加一条账号信息
                            //根据用户Id查找用户名
                            AfUserDo afUserDo = afUserDao.getUserById(userId);
                            if (null != afUserDo) {
                                //给用户的返现金额
                                AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                                afUserAccountDo.setUserId(userId);
                                afUserAccountDo.setUserName(afUserDo.getUserName());
                                afUserAccountDo.setRebateAmount(rebateAmount);
                                afUserAccountDao.addUserAccount(afUserAccountDo);

                                // add by luoxiao for 边逛边赚，增加零钱明细
                                afTaskUserService.addTaskUser(userId, UserAccountLogType.REBATE.getName(), rebateAmount);
                                // end by luoxiao

                                //有得卖账户减钱操作
                                remainAmount = recycleTradeSave(afRecycleQuery, afRecycleRatioDo, settlePrice, rebateAmount);
                            } else {
                                throw new FanbeiException(userId + " 不存在");
                            }
                        } else {//用户账号信息存在,直接往账号上添加金额 金额 = 订单金额 *（1 + 返现比例）
                            //根据用户Id查找用户名
                            AfUserDo afUserDo = afUserDao.getUserById(userId);
                            if (null != afUserDo) {
                                AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                                afUserAccountDo.setRebateAmount(rebateAmount);
                                afUserAccountDo.setUserId(afRecycleQuery.getUserId());
                                afUserAccountDao.updateUserAccount(afUserAccountDo);

                                // add by luoxiao for 边逛边赚，增加零钱明细
                                afTaskUserService.addTaskUser(userId, UserAccountLogType.REBATE.getName(), rebateAmount);
                                // end by luoxiao

                                //有得卖账户减钱操作
                                remainAmount = recycleTradeSave(afRecycleQuery, afRecycleRatioDo, settlePrice, rebateAmount);
                            } else {
                                throw new FanbeiException(userId + " 不存在");
                            }
                        }
                        doSmsNotice(afRecycleQuery.getUserId(), remainAmount, settlePrice, rebateAmount);//是否需要短信通知
                    } else {
                        logger.error("addRecycleOrder callBack,param=" + map.toString() + ", errorCode=" + (jsonObject == null ? null : jsonObject.getString("code")));
                        return -1;
                    }
                }
                return 1;
            }

        });
    }


    /**
     * 添加交易记录并且从供应商账号扣除订单金额
     *
     * @param afRecycleQuery
     * @param afRecycleRatioDo
     * @param settlePrice
     * @param rebateAmount
     * @return 供应商账号余额
     */
    private BigDecimal recycleTradeSave(final AfRecycleQuery afRecycleQuery, AfRecycleRatioDo afRecycleRatioDo, BigDecimal settlePrice, BigDecimal rebateAmount) {
        AfRecycleTradeDo afRecycleTradeDo = afRecycleTradeDao.getLastRecord();
        AfRecycleTradeDo newAfRecycleTradeDo = new AfRecycleTradeDo();
        newAfRecycleTradeDo.setGmtCreate(new Date());
        newAfRecycleTradeDo.setGmtModified(new Date());
        newAfRecycleTradeDo.setRatio(afRecycleRatioDo.getRatio());
        newAfRecycleTradeDo.setRefId(afRecycleQuery.getRid());
        newAfRecycleTradeDo.setRemainAmount(afRecycleTradeDo.getRemainAmount().subtract(settlePrice));
        newAfRecycleTradeDo.setReturnAmount(rebateAmount.subtract(settlePrice));
        newAfRecycleTradeDo.setTradeAmount(settlePrice);
        newAfRecycleTradeDo.setType(1);
        afRecycleTradeDao.saveRecord(newAfRecycleTradeDo);
        return newAfRecycleTradeDo.getRemainAmount();
    }

    /**
     * 是否需要短信通知
     *
     * @param userId       用户id
     * @param remainAmount 有得卖账号余额
     * @param settlePrice  订单总额
     * @param rebateAmount 返现总额
     */
    private void doSmsNotice(Long userId, BigDecimal remainAmount, BigDecimal settlePrice, BigDecimal rebateAmount) {
        try {
            Object needNotice = bizCacheUtil.getObject(RecycleUtil.RECYCLE_AMOUNT_WARN);//查找redis中是否有过报警通知的记录
            if (null == needNotice || (null != needNotice && "Y".equals(needNotice))) {
                BigDecimal minThreshold = RecycleUtil.RECYCLE_AMOUNT_MIN_THRESHOLD;
                AfResourceDo afResourceDo = afResourceDao.getConfigByType(RecycleUtil.RECYCLE_AMOUNT_MIN_THRESHOLD_KEY);
                if (null != afResourceDo && StringUtils.isNotBlank(afResourceDo.getValue())) {//未配置最小报警阈值
                    minThreshold = BigDecimal.valueOf(Integer.valueOf(afResourceDo.getValue().trim()));
                }
                //通知运营人员
                if (remainAmount.compareTo(minThreshold) == -1) {
                    bizCacheUtil.saveObject(RecycleUtil.RECYCLE_AMOUNT_WARN, "N", -1);
                    AfResourceDo mobileResourceDo = afResourceDao.getConfigByType(RecycleUtil.RECYCLE_AMOUNT_MIN_THRESHOLD_MOBILE_KEY);//查找需要账户预警通知的手机号
                    if (null != mobileResourceDo && StringUtils.isNotBlank(mobileResourceDo.getValue())) {
                        String[] mobiles = mobileResourceDo.getValue().split(",");
                        if (null != mobiles && mobiles.length > 0) {
                            for (int i = 0; i < mobiles.length; i++) {
                                smsUtil.sendRecycleWarn(mobiles[i], remainAmount);
                            }
                        }
                    }
                }
            }
            //通知当前用户
            AfUserDo afUserDo = afUserDao.getUserById(userId);
            if (null != afUserDo && StringUtils.isNotBlank(afUserDo.getMobile())) {
                smsUtil.sendRecycleRebate(afUserDo.getMobile(), settlePrice, rebateAmount);
            }
        } catch (Exception e) {
            logger.error("recycle doSmsNotice,error=" + e);
            e.printStackTrace();
        }
    }


    @Override
    public AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery) {
        return afRecycleDao.getRecycleOrder(afRecycleQuery);
    }

    /**
     * 翻倍兑换业务
     *
     * @param uid            用户id
     * @param exchangeAmount 需要兑换的金额
     * @param remainAmount   有得卖账户剩余总额
     * @return 兑换后的金额和翻倍数
     */
    @Override
    public Map<String, Object> addExchange(final Long uid, final Integer exchangeAmount, final BigDecimal remainAmount) {
        final Map<String, Object> map = new HashMap<>();
        transactionTemplate.execute(new TransactionCallback<Void>() {
            @Override
            public Void doInTransaction(TransactionStatus transactionStatus) {
                List<AfRecycleRatioDo> list = afRecycleDao.getRecycleExchangeRatio();
                BigDecimal ratio = RecycleUtil.getExchangeRatio(list);
                BigDecimal needExchangeAmount = ratio.multiply(BigDecimal.valueOf(exchangeAmount));//翻倍后的金额
                //判断当前的兑换总额是否超过了系统设置的最高阈值，若操作则直接使用最大阈值
                AfResourceDo afResourceDo = afResourceDao.getConfigByType(ResourceType.MAX_THRESHOLD_OF_DOUBLE_EXCHANGE.getCode());
                if (null != afResourceDo && org.apache.commons.lang.StringUtils.isNotBlank(afResourceDo.getValue())) {
                    BigDecimal maxThreshold = BigDecimal.valueOf(Integer.valueOf(afResourceDo.getValue().trim()));
                    if (needExchangeAmount.compareTo(maxThreshold) == 1) {
                        needExchangeAmount = maxThreshold;
                    }
                }
                AfUserAccountDo afUserAccountDo = new AfUserAccountDo(uid, BigDecimal.valueOf(exchangeAmount));
                afUserAccountDao.reduceRebateAmount(afUserAccountDo);//用户账号扣除返现金额
                //增加券信息
                AfCouponDo couponInfo = new AfCouponDo();
                couponInfo.setModifier("system");
                couponInfo.setCreator("system");
                couponInfo.setName(RecycleUtil.COUPON_NAME);//券名称
                couponInfo.setAmount(BigDecimal.valueOf(Math.ceil(needExchangeAmount.doubleValue())));
                couponInfo.setQuota(-1L);//优惠券发放总数 -1不限
                couponInfo.setQuotaAlready(1);//已经发放数量
                couponInfo.setLimitAmount(RecycleUtil.LIMIT_AMOUNT);//使用的最小限制金额,50元
                couponInfo.setLimitCount(RecycleUtil.LIMIT_COUNT);//每个人限制领取张数
                couponInfo.setGmtStart(new Date());
                couponInfo.setGmtEnd(DateUtil.getFinalDate());
                couponInfo.setType(CouponType.FULLVOUCHER.getCode());//满减券
                couponInfo.setUseRule("");//使用须知
                couponInfo.setStatus("O");//优惠券状态【O：开启,C:关闭 】
                couponInfo.setIsGlobal(0);// '是否为全场通用券,0表示全场券,1表示活动券'
                couponInfo.setShopUrl("");
                couponInfo.setExpiryType("R");
                couponInfo.setUseRange("");
                afCouponDao.addCoupon(couponInfo);
                Long couponId = couponInfo.getRid();
                //将券分配给当前兑换用户
                AfUserCouponDo userCoupon = new AfUserCouponDo(uid, couponId, CouponStatus.NOUSE.getCode(), CouponSenceRuleType.DOUBLE_EXCHANGE.getCode(), new Date(), DateUtil.getFinalDate());
                afUserCouponDao.addUserCoupon(userCoupon);// 插入

                map.put("ratio", ratio);
                map.put("amount", Math.ceil(needExchangeAmount.doubleValue()));//向上取整
                return null;
            }
        });
        return map;
    }

}
