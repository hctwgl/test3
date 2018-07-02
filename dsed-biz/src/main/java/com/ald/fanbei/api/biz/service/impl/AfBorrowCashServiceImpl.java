package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.NumberWordFormat;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author suweili 2017年3月24日下午5:04:43
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowCashService")
public class AfBorrowCashServiceImpl extends BaseService implements AfBorrowCashService {
    @Resource
    UpsUtil upsUtil;
    @Resource
    AfBorrowCashDao afBorrowCashDao;
    @Resource
    AfResourceService afResourceService;
    @Resource
    AfUserBankcardDao afUserBankcardDao;
    @Resource
    GeneratorClusterNo generatorClusterNo;
    @Resource
    AfRenewalDetailDao afRenewalDetailDao;
    @Resource
    TransactionTemplate transactionTemplate;
    @Resource
    AfBorrowCashService afBorrowCashService;
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    NumberWordFormat numberWordFormat;

    @Resource
    AfRecommendUserService afRecommendUserService;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    AfUserService afUserService;
    @Resource
    AfUserAccountService afUserAccountService;
    @Resource
    SmsUtil smsUtil;
    @Resource
    JpushService jpushService;
    @Resource
    AfFundSideBorrowCashService afFundSideBorrowCashService;
    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Autowired
    KafkaSync kafkaSync;
    @Resource
    ContractPdfThreadPool contractPdfThreadPool;
    @Resource
    AfUserCouponService afUserCouponService;


    @Override
    public int addBorrowCash(AfBorrowCashDo afBorrowCashDo) {
        Date currDate = new Date();
        afBorrowCashDo.setBorrowNo(generatorClusterNo.getBorrowCashNo(currDate));
        return afBorrowCashDao.addBorrowCash(afBorrowCashDo);
    }

    /**
     * 借款成功
     *
     * @param afBorrowCashDo
     * @return
     */
    public int borrowSuccess(final AfBorrowCashDo afBorrowCashDo) {
        int resultValue = 0;
        resultValue = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus transactionStatus) {
                logger.info("borrowSuccess--begin");
                Date currDate = new Date(System.currentTimeMillis());
                afBorrowCashDo.setGmtArrival(currDate);
                Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
//                Integer day = NumberUtil
//                        .objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(afBorrowCashDo.getGmtArrival());
                Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
                afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                afBorrowCashDao.updateBorrowCash(afBorrowCashDo);

                logger.info("borrowSuccess--end");
                // fmf 借钱抽奖活动借款金额加入缓存
                BigDecimal amount = (BigDecimal) bizCacheUtil.getObject("BorrowCash_Sum_Amount");
                if (amount.compareTo(new BigDecimal(1500000000)) == -1
                        || amount.compareTo(new BigDecimal(1500000000)) == 0) {
                    amount = amount.add(afBorrowCashDo.getAmount());
                    if (amount.compareTo(new BigDecimal(1500000000)) == 1) {
                        logger.info("1500000000 is win,user_id= " + afBorrowCashDo.getUserId());
                        List<String> users = new ArrayList<String>();
                        users.add(afBorrowCashDo.getUserId() + "");
                        List<String> userName = afUserService.getUserNameByUserId(users);
                        // 保存破十亿中奖用户
                        bizCacheUtil.saveObject("Billion_Win_User", userName.get(0), 60 * 60 * 24 * 7);
                        logger.info("1500000000 is win,user_name= " + userName.get(0));
                    }
                    bizCacheUtil.saveObject("BorrowCash_Sum_Amount", amount, 60 * 60 * 24 * 7);
                } else {
                    amount = amount.add(afBorrowCashDo.getAmount());
                    bizCacheUtil.saveObject("BorrowCash_Sum_Amount", amount, 60 * 60 * 24 * 7);
                }
                return 1;
            }
        });

        if (resultValue == 1) {
            kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_USER_BASIC_DATA, true);
            kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE, true);
            contractPdfThreadPool.PlatformServiceProtocolPdf(afBorrowCashDo.getRid(), afBorrowCashDo.getType(),
                    afBorrowCashDo.getPoundage(), afBorrowCashDo.getUserId());// 生成凭据纸质帐单
            contractPdfThreadPool.createGoodsInstalmentProtocolPdf(afBorrowCashDo.getRid(), afBorrowCashDo.getType(),
                    afBorrowCashDo.getUserId());// 生成凭据纸质帐单
        }

        if (resultValue == 1) {
            try {
                int rr = afRecommendUserService.updateRecommendByBorrow(afBorrowCashDo.getUserId(),
                        afBorrowCashDo.getGmtCreate());
                logger.info("updateRecommendUser=" + rr + "");
            } catch (Exception e) {
                logger.info(
                        "afRecommendUserService.updateRecommendByBorrow error，borrowCashId=" + afBorrowCashDo.getRid(),
                        e);
            }
            try {
                //是否是第一次借款,给该用户送优惠券（还款券）
                String tag = CouponCateGoryType._FIRST_LOAN_.getCode();
                String sourceType = CouponActivityType.FIRST_LOAN.getCode();

                int countNum = afUserCouponService.getUserCouponByUserIdAndCouponCource(afBorrowCashDo.getUserId(), sourceType);
                //该用户是否拥有该类型优惠券
                if (countNum > 0) {
                    return 0;
                }
                HashMap map = afBorrowCashDao.getBorrowCashByRemcommend(afBorrowCashDo.getUserId());
                logger.info("setnt first loan coupon userId=" + afBorrowCashDo.getUserId());

                Long count = (Long) map.get("count");
                logger.info("setnt first loan coupon count=" + count + "userId=" + afBorrowCashDo.getUserId());
                if (count > 1)
                    return 0;

                //第一次借款
                String msg = afUserCouponService.sentUserCouponGroup(afBorrowCashDo.getUserId(), tag, sourceType);
                logger.info("first loan sent coupon msg = " + msg + " afBorrowCashDo = " + JSONObject.toJSONString(afBorrowCashDo));
            } catch (Exception e) {
                logger.error("first borrow sentUserCoupon error", e);
            }
            AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(
                    ResourceType.FUND_SIDE_BORROW_CASH.getCode(),
                    AfResourceSecType.FUND_SIDE_BORROW_CASH_ONOFF.getCode());
            if (resourceDo != null && "1".equals(resourceDo.getValue())) {
                // 业务处理成功,和资金方关联处理添加
                logger.info("borrowSuccess ,begin rela fund site info,borrowCashId:" + afBorrowCashDo.getRid());
                boolean matchResult = afFundSideBorrowCashService.matchFundAndBorrowCash(afBorrowCashDo.getRid());
                if (matchResult) {
                    logger.info(
                            "borrowSuccess ,end rela fund site info success,borrowCashId:" + afBorrowCashDo.getRid());
                } else {
                    logger.info("borrowSuccess ,end rela fund site info fail,borrowCashId:" + afBorrowCashDo.getRid());
                }
            } else {
                // 资金方开关关闭，跳过关联
                logger.info("borrowSuccess ,rela fund site info is off,and jump it ,borrowCashId:"
                        + afBorrowCashDo.getRid());
            }
        }
        return resultValue;
    }


    /**
     * 借款成功
     *
     * @param afBorrowCashDo
     * @return
     */
    public int borrowSuccessForNew(final AfBorrowCashDo afBorrowCashDo) {
        int resultValue = 0;
        resultValue = transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus transactionStatus) {
                logger.info("borrowSuccess--begin");
                Date currDate = new Date(System.currentTimeMillis());
                afBorrowCashDo.setGmtArrival(currDate);
                Integer day = numberWordFormat.borrowTime(afBorrowCashDo.getType());
//                Integer day = NumberUtil
//                        .objToIntDefault(AfBorrowCashType.findRoleTypeByName(afBorrowCashDo.getType()).getCode(), 7);
                Date arrivalEnd = DateUtil.getEndOfDatePrecisionSecond(afBorrowCashDo.getGmtArrival());
                Date repaymentDay = DateUtil.addDays(arrivalEnd, day - 1);
                afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
                afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
                logger.info("borrowSuccess--end");
                return 1;
            }
        });

        if (resultValue == 1) {
            kafkaSync.syncEvent(afBorrowCashDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE, true);
            logger.info("contractPdfThreadPool PlatformServiceProtocolPdf start afBorrowCashDo =" + JSONObject.toJSONString(afBorrowCashDo));
            contractPdfThreadPool.PlatformServiceProtocolPdf(afBorrowCashDo.getRid(), afBorrowCashDo.getType(),
                    afBorrowCashDo.getPoundage(), afBorrowCashDo.getUserId());// 生成凭据纸质帐单

            contractPdfThreadPool.createGoodsInstalmentProtocolPdf(afBorrowCashDo.getRid(), afBorrowCashDo.getType(),
                    afBorrowCashDo.getUserId());// 生成凭据纸质帐单
        }

        if (resultValue == 1) {
            try {
                int rr = afRecommendUserService.updateRecommendByBorrow(afBorrowCashDo.getUserId(),
                        afBorrowCashDo.getGmtCreate());
                logger.info("updateRecommendUser=" + rr + "");
            } catch (Exception e) {
                logger.info(
                        "afRecommendUserService.updateRecommendByBorrow error，borrowCashId=" + afBorrowCashDo.getRid(),
                        e);
            }

            try {
                //是否是第一次借款,给该用户送优惠券（还款券）

                String tag = CouponCateGoryType._FIRST_LOAN_.getCode();
                String sourceType = CouponActivityType.FIRST_LOAN.getCode();

                int countNum = afUserCouponService.getUserCouponByUserIdAndCouponCource(afBorrowCashDo.getUserId(), sourceType);
                //该用户是否拥有该类型优惠券
                if (countNum > 0) {
                    return 0;
                }
                HashMap map = afBorrowCashDao.getBorrowCashByRemcommend(afBorrowCashDo.getUserId());

                String log = String.format("setnt first loan coupon userId = %s", afBorrowCashDo.getUserId());
                logger.info(log);
                Long count = (Long) map.get("count");
                log = log + String.format("count =  %s", count);
                logger.info(log);
                if (count > 1)
                    return 0;

                //第一次借款
                String msg = afUserCouponService.sentUserCouponGroup(afBorrowCashDo.getUserId(), tag, sourceType);
                log = log + String.format("msg =  %s", msg);
                logger.info(log);
                log = log + String.format("afBorrowCashDo =  %s", JSONObject.toJSONString(afBorrowCashDo));
                logger.info(log);
            } catch (Exception e) {
                logger.error("first borrow sentUserCouponGroup error", e);
            }

            AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(
                    ResourceType.FUND_SIDE_BORROW_CASH.getCode(),
                    AfResourceSecType.FUND_SIDE_BORROW_CASH_ONOFF.getCode());
            if (resourceDo != null && "1".equals(resourceDo.getValue())) {
                // 业务处理成功,和资金方关联处理添加
                logger.info("borrowSuccess ,begin rela fund site info,borrowCashId:" + afBorrowCashDo.getRid());
                boolean matchResult = afFundSideBorrowCashService.matchFundAndBorrowCash(afBorrowCashDo.getRid());
                if (matchResult) {
                    logger.info(
                            "borrowSuccess ,end rela fund site info success,borrowCashId:" + afBorrowCashDo.getRid());
                } else {
                    logger.info("borrowSuccess ,end rela fund site info fail,borrowCashId:" + afBorrowCashDo.getRid());
                }
            } else {
                // 资金方开关关闭，跳过关联
                logger.info("borrowSuccess ,rela fund site info is off,and jump it ,borrowCashId:"
                        + afBorrowCashDo.getRid());
            }
        }

        return resultValue;
    }

    /**
     * 借款成功
     *
     * @param afBorrowCashDo
     * @return
     */
    public void borrowFail(final Long borrowId, String tradeNoOut, String msgOut) {
        final AfBorrowCashDo afBorrowCashDo = afBorrowCashDao.getBorrowCashByrid(borrowId);
        afBorrowCashDo.setReviewDetails(tradeNoOut);

        Date cur = new Date();
        afBorrowCashDo.setStatus(AfBorrowCashStatus.closed.getCode());
        afBorrowCashDo.setRemark("UPS打款失败，" + msgOut);
        afBorrowCashDo.setGmtModified(cur);
        afBorrowCashDo.setGmtClose(cur);

        transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus transactionStatus) {
                afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
                afUserAccountSenceService.syncLoanUsedAmount(afBorrowCashDo.getUserId(), SceneType.CASH, afBorrowCashDo.getAmount().negate());
                return 1;
            }
        });
    }


    @Override
    public int updateBorrowCash(final AfBorrowCashDo afBorrowCashDo) {
        afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
        return 1;

    }

    @Override
    public AfBorrowCashDo getBorrowCashByUserId(Long userId) {
        return afBorrowCashDao.getBorrowCashByUserId(userId);
    }

    @Override
    public AfBorrowCashDo getDealingCashByUserId(Long userId) {
        return afBorrowCashDao.getDealingCashByUserId(userId);
    }

    @Override
    public List<AfBorrowCashDo> getBorrowCashListByUserId(Long userId, Integer start) {
        return afBorrowCashDao.getBorrowCashListByUserId(userId, start);
    }

    @Override
    public AfBorrowCashDo getBorrowCashByrid(Long rid) {
        return afBorrowCashDao.getBorrowCashByrid(rid);
    }

    @Override
    public String getCurrentLastBorrowNo(String orderNoPre) {
        return afBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
    }

    @Override
    public AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo) {
        return afBorrowCashDao.getBorrowCashByRishOrderNo(rishOrderNo);
    }

    @Override
    public AfBorrowCashDo getUserDayLastBorrowCash(Long userId) {
        Date startTime = DateUtil.getToday();
        Date endTime = DateUtil.getTodayLast();
        return afBorrowCashDao.getUserDayLastBorrowCash(userId, startTime, endTime);
    }

    @Override
    public Integer getSpecBorrowCashNums(Long userId, String reviewStatus, Date startTime) {
        return afBorrowCashDao.getSpecBorrowCashNums(userId, reviewStatus, startTime);
    }

    @Override
    public boolean isCanBorrowCash(Long userId) {
        List<AfBorrowCashDo> notFinishBorrowList = afBorrowCashDao.getBorrowCashByStatusNotInFinshAndClosed(userId);
        return notFinishBorrowList.isEmpty();

    }

    @Override
    public AfBorrowCashDo getNowTransedBorrowCashByUserId(Long userId) {
        return afBorrowCashDao.getNowTransedBorrowCashByUserId(userId);
    }

    @Override
    public int getBorrowNumByUserId(Long userId) {
        return afBorrowCashDao.getBorrowNumByUserId(userId);
    }

    @Override
    public AfBorrowCashDo getNowUnfinishedBorrowCashByUserId(Long userId) {
        return afBorrowCashDao.getNowUnfinishedBorrowCashByUserId(userId);
    }

    @Override
    public AfBorrowCashDo getBorrowCashInfoByBorrowNo(String borrowNo) {
        return afBorrowCashDao.getBorrowCashInfoByBorrowNo(borrowNo);
    }

    @Override
    public List<AfBorrowCashDo> getRiskRefuseBorrowCash(Long userId, Date gmtStart, Date gmtEnd) {
        return afBorrowCashDao.getRiskRefuseBorrowCash(userId, gmtStart, gmtEnd);
    }

    @Override
    public List<String> getBorrowedUserIds() {
        return afBorrowCashDao.getBorrowedUserIds();
    }

    @Override
    public BigDecimal getBorrowCashSumAmount() {
        return afBorrowCashDao.getBorrowCashSumAmount();
    }

    @Override
    public List<String> getRandomUser() {
        return afBorrowCashDao.getRandomUser();
    }

    @Override
    public List<String> getNotRandomUser(List<String> userId) {
        return afBorrowCashDao.getNotRandomUser(userId);
    }

    @Override
    public int updateBalancedDate(AfBorrowCashDo afBorrowCashDo) {
        return afBorrowCashDao.updateBalancedDate(afBorrowCashDo);
    }

    @Override
    public int getCurrDayTransFailTimes(Long userId) {
        return afBorrowCashDao.getCurrDayTransFailTimes(userId);
    }

    @Override
    public int updateAfBorrowCashService(AfBorrowCashDo afBorrowCashDo) {
        return afBorrowCashDao.updateAfBorrowCashService(afBorrowCashDo);
    }

    @Override
    public int updateAuAmountByRid(long rid, BigDecimal auAmount) {
        return afBorrowCashDao.updateAuAmountByRid(rid, auAmount);
    }

    @Override
    public int updateBorrowCashLock(Long borrowId) {
        return afBorrowCashDao.updateBorrowCashLock(borrowId);
    }

    @Override
    public int updateBorrowCashUnLock(Long borrowId) {
        return afBorrowCashDao.updateBorrowCashUnLock(borrowId);
    }

    @Override
    public AfBorrowCashDo getBorrowCashByStatus(Long userId) {
        return afBorrowCashDao.getBorrowCashByStatus(userId);
    }

    @Override
    public int updateAfBorrowCashPlanTime(Long userId) {
        return afBorrowCashDao.updateAfBorrowCashPlanTime(userId);
    }

    @Override
    public List<AfBorrowCashDo> getListByUserId(Long userId, Long rows) {
        return afBorrowCashDao.getListByUserId(userId, rows);
    }

    @Override
    public AfBorrowCashDo getBorrowCashByUserIdDescById(Long userId) {
        return afBorrowCashDao.getBorrowCashByUserIdDescById(userId);
    }

    @Override
    public AfBorrowCashDo getBorrowCashInfoByBorrowNoV1(String borrowNo) {
        return afBorrowCashDao.getBorrowCashInfoByBorrowNoV1(borrowNo);
    }


    @Override
    public BigDecimal calculateLegalRestAmount(AfBorrowCashDo cashDo) {
        BigDecimal restAmount = BigDecimal.ZERO;
        if (cashDo != null) {
            restAmount = BigDecimalUtil.add(restAmount, cashDo.getAmount(),
                    cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
                    cashDo.getRateAmount(), cashDo.getSumRate(),
                    cashDo.getPoundage(), cashDo.getSumRenewalPoundage())
                    .subtract(cashDo.getRepayAmount());
        }
        return restAmount;
    }

    @Override
    public BigDecimal calculateLegalRestOverdue(AfBorrowCashDo cashDo) {
        BigDecimal overdueAmount = BigDecimal.ZERO;
        if (cashDo != null) {
            overdueAmount = BigDecimalUtil.add(overdueAmount,
                    cashDo.getOverdueAmount(), cashDo.getSumOverdue(),
                    cashDo.getRateAmount(), cashDo.getSumRate(),
                    cashDo.getPoundage(), cashDo.getSumRenewalPoundage());
        }
        return overdueAmount;
    }


    @Override
    public int getCashBorrowSuccessByUserId(Long userId, String activityTime) {
        // TODO Auto-generated method stub
        return afBorrowCashDao.getCashBorrowSuccessByUserId(userId, activityTime);
    }

    @Override
    public int getCashBorrowByUserIdAndActivity(Long userId, String activityTime) {
        // TODO Auto-generated method stub
        return afBorrowCashDao.getCashBorrowByUserIdAndActivity(userId, activityTime);
    }

    @Override
    public boolean haveDealingBorrowCash(Long userId) {
        return afBorrowCashDao.tuchDealingBorrowCash(userId) != null;
    }
}
