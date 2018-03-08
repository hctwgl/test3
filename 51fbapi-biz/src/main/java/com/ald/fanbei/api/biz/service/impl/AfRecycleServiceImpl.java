package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.third.util.RecycleUtil;
import com.ald.fanbei.api.common.enums.CouponSenceRuleType;
import com.ald.fanbei.api.common.enums.CouponStatus;
import com.ald.fanbei.api.common.enums.CouponType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.recycle.AfRecycleOrderType;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

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

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 有得卖推单，订单添加事物
     *
     * @param afRecycleQuery  实体对象
     * @return 返回整数
     */
    @Override
    public Integer addRecycleOrder(final AfRecycleQuery afRecycleQuery) {
        transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus transactionStatus) {
                Integer result = afRecycleDao.addRecycleOrder(afRecycleQuery);
                //回调有得卖确认接口
                Map<String,String> map = new HashMap<>();
                map.put("userId", String.valueOf(afRecycleQuery.getUserId()));
                map.put("orderId", afRecycleQuery.getRefOrderId());
                map.put("payType", String.valueOf(afRecycleQuery.getPayType()));
                map.put("settlePrice", String.valueOf(afRecycleQuery.getSettlePrice()));
                map.put("partnerId", afRecycleQuery.getPartnerId());
                String sign = SignUtil.signForYdm(RecycleUtil.createLinkString(map), RecycleUtil.PRIVATE_KEY);
                map.put("sign", sign);
                String postResult = HttpUtil.post(RecycleUtil.CALLBACK_BASE_URL + RecycleUtil.YDM_CALLBACK_URL, map);
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
                    if (null == amount) {//用户账号信息不存在,则需要添加一条账号信息
                        //根据用户Id查找用户名
                        AfUserDo afUserDo = afUserDao.getUserById(userId);
                        //给用户的返现金额
                        BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);
                        AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                        afUserAccountDo.setUserId(userId);
                        afUserAccountDo.setUserName(afUserDo.getUserName());
                        afUserAccountDo.setRebateAmount(rebateAmount);
                        afUserAccountDao.addUserAccount(afUserAccountDo);
                        //有得卖账户减钱操作
                        recycleTradeSave(afRecycleQuery, afRecycleRatioDo, settlePrice, rebateAmount);
                    } else {//直接往账号上添加金额 金额 = 订单金额 *（1 + 返现比例）
                        BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);
                        AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                        afUserAccountDo.setRebateAmount(rebateAmount);
                        afUserAccountDo.setUserId(afRecycleQuery.getUserId());
                        afUserAccountDao.updateUserAccount(afUserAccountDo);
                        //有得卖账户减钱操作
                        recycleTradeSave(afRecycleQuery, afRecycleRatioDo, settlePrice, rebateAmount);
                    }
                } else {
                    // code = "ERR06"
                    logger.error("addRecycleOrder callBack,param=" + map.toString()  + ", errorCode=" + (jsonObject == null ? null : jsonObject.getString("code")));
                }
                return result;
            }

            private void recycleTradeSave(final AfRecycleQuery afRecycleQuery, AfRecycleRatioDo afRecycleRatioDo, BigDecimal settlePrice, BigDecimal rebateAmount) {
                AfRecycleTradeDo afRecycleTradeDo = afRecycleTradeDao.getLastRecord();
                AfRecycleTradeDo newAfRecycleTradeDo = new AfRecycleTradeDo();
                newAfRecycleTradeDo.setGmtCreate(new Date());
                newAfRecycleTradeDo.setGmtModified(new Date());
                newAfRecycleTradeDo.setRatio(afRecycleRatioDo.getRatio());
                newAfRecycleTradeDo.setRefId(afRecycleQuery.getRid());
                newAfRecycleTradeDo.setRemainAmount(afRecycleTradeDo.getRemainAmount().subtract(settlePrice));
                newAfRecycleTradeDo.setReturnAmount(rebateAmount);
                newAfRecycleTradeDo.setTradeAmount(settlePrice.add(rebateAmount));
                newAfRecycleTradeDo.setType(1);
                afRecycleTradeDao.saveRecord(newAfRecycleTradeDo);
            }
        });
        return 1;
    }

    @Override
    public AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery) {
        return afRecycleDao.getRecycleOrder(afRecycleQuery);
    }

    /**
     *  翻倍兑换业务
     * @param uid 用户id
     * @param exchangeAmount 需要兑换的金额
     * @param remainAmount 有得卖账户剩余总额
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
                AfResourceDo afResourceDo = afResourceDao.getMaxThresholdOfDoubleExchange(ResourceType.MAX_THRESHOLD_OF_DOUBLE_EXCHANGE.getCode());
                if(null != afResourceDo && org.apache.commons.lang.StringUtils.isNotBlank(afResourceDo.getValue())){
                    BigDecimal maxThreshold = BigDecimal.valueOf(Integer.valueOf(afResourceDo.getValue().trim()));
                    if(needExchangeAmount.compareTo(maxThreshold) == 1) {
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
                couponInfo.setAmount(needExchangeAmount);
                couponInfo.setQuota(-1L);//优惠券发放总数 -1不限
                couponInfo.setQuotaAlready(1);//已经发放数量
                couponInfo.setLimitAmount(RecycleUtil.LIMIT_AMOUNT);//最小限制金额,50元
                couponInfo.setLimitCount(RecycleUtil.LIMIT_COUNT);//每个人限制领取张数
                couponInfo.setGmtStart(new Date());
                couponInfo.setGmtEnd(DateUtil.getFinalDate());
                couponInfo.setType(CouponType.FULLVOUCHER.getCode());//满减券
                couponInfo.setUseRule("");//使用须知
                couponInfo.setStatus("O");//优惠券状态【O：开启,C:关闭 】
                couponInfo.setIsGlobal(0);// '是否为全场通用券,0表示全场券,1表示活动券'
                couponInfo.setShopUrl("");
                couponInfo.setExpiryType("R");
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
