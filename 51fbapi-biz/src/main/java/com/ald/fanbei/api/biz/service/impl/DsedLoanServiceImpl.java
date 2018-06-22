package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 借款ServiceImpl
 *
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:48:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("dsedLoanService")
public class DsedLoanServiceImpl extends ParentServiceImpl<DsedLoanDo, Long> implements DsedLoanService {

    private static final Logger logger = LoggerFactory.getLogger(DsedLoanServiceImpl.class);

    @Resource
    private DsedLoanDao dsedLoanDao;

    @Resource
    private RedisTemplate<String, ?> redisTemplate;

    @Resource
    private GeneratorClusterNo generatorClusterNo;

    @Resource
    private DsedLoanPeriodsService dsedLoanPeriodsService;

    @Resource
    private DsedUserBankcardDao dsedUserBankcardDao;

    @Resource
    private DsedLoanPeriodsDao dsedLoanPeriodsDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private UpsUtil upsUtil;

    @Override
    public BaseDao<DsedLoanDo, Long> getDao() {
        return dsedLoanDao;
    }

    @Override
    public void doLoan(DsedApplyLoanBo bo) {
        Long userId = bo.userId;
        this.lockLoan(userId);

        try {
            // 解析分期
            final DsedApplyLoanBo.ReqParam reqParam = bo.reqParam;
            String loanNo = generatorClusterNo.getLoanNo(new Date());
            final List<Object> objs = dsedLoanPeriodsService.resolvePeriods(reqParam.amount, userId, reqParam.periods, loanNo, bo.reqParam.prdType);
            final DsedLoanDo loanDo = (DsedLoanDo) objs.remove(0);
            final List<DsedLoanPeriodsDo> periodDos = new ArrayList<>();
            for (Object o : objs) {
                periodDos.add((DsedLoanPeriodsDo) o);
            }

            final DsedUserBankcardDo bankCard = dsedUserBankcardDao.getUserMainBankcardByUserId(userId);

            this.saveLoanRecords(bo, loanDo, periodDos, bankCard);

            try {
                // 调用UPS打款
                UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(bo.reqParam.amount,
                        bo.realName, bankCard.getCardNumber(), userId.toString(), bankCard.getMobile(),
                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
                        UserAccountLogType.DSED_LOAN.getCode(), loanDo.getRid().toString(),bo.idNumber);
                loanDo.setTradeNoOut(upsResult.getOrderNo());
                if (!upsResult.isSuccess()) {
                    //审核通过，ups打款失败
                    dealLoanFail(loanDo, periodDos, upsResult.getRespCode());
                    throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
                }
                loanDo.setStatus(AfLoanStatus.TRANSFERING.name());
                dsedLoanDao.updateById(loanDo);
                // 增加日志
//				afUserAccountLogDao.addUserAccountLog(BuildInfoUtil.buildUserAccountLogDo(UserAccountLogType.LOAN, loanDo.getAmount(), userId, loanDo.getRid()));
            } catch (Exception e) {
                loanDo.setStatus(AfLoanStatus.CLOSED.name());
                dsedLoanDao.updateById(loanDo);

                // 关闭分期记录
                closePeriods(periodDos);

                throw e;
            }

            //贷款成功 通知用户
            try {
                String bankNumber = bankCard.getCardNumber();
                String lastBankCode = bankNumber.substring(bankNumber.length() - 4);
//				smsUtil.sendloanCashCode(userAccount.getUserName(), lastBankCode);
//				jpushService.pushUtil(Documents.LOAN_SUCC_TITLE, String.format(Documents.LOAN_SUCC_MSG, lastBankCode), userAccount.getUserName());
            } catch (Exception e) {
                logger.error("DoLoan success, notify user occur error!", e); //通知过程抛出任何异常捕获，不影响主流程
            }
        } finally {
            this.unlockLoan(userId);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED, timeout = 30, value = "appTransactionTemplate", rollbackFor = Exception.class)
    public void saveLoanRecords(final DsedApplyLoanBo bo, final DsedLoanDo loanDo,
                                final List<DsedLoanPeriodsDo> periodDos, final DsedUserBankcardDo bankCard) {
        try {
            DsedApplyLoanBo.ReqParam reqParam = bo.reqParam;

            loanDo.setCardNo(bankCard.getCardNumber());
            loanDo.setCardName(bankCard.getBankName());
            loanDo.setIp(reqParam.ip);
            loanDo.setAddress(reqParam.address);
            loanDo.setProvince(reqParam.province);
            loanDo.setCity(reqParam.city);
            loanDo.setCounty(reqParam.county);
            loanDo.setLatitude(new BigDecimal(reqParam.latitude));
            loanDo.setLongitude(new BigDecimal(reqParam.longitude));
            loanDo.setRemark(reqParam.remark);
            loanDo.setLoanRemark(reqParam.loanRemark);
            loanDo.setRepayRemark(reqParam.repayRemark);
            loanDo.setAppName(reqParam.appName);
            dsedLoanDao.saveRecord(loanDo);

            Long loanId = loanDo.getRid();
            for (DsedLoanPeriodsDo dsedLoanPeriodsDo : periodDos) {
                dsedLoanPeriodsDo.setLoanId(loanId);
                dsedLoanPeriodsDao.saveRecord(dsedLoanPeriodsDo);
            }
        } catch (Exception e) {
            logger.error("saveLoanRecords,DB error", e);
            throw e;
        }
    }


    /**
     * 同一时刻每个用户只允许发生一笔借款操作
     */
    private void lockLoan(Long userId) {
        String key = "LOAN_LOCK_" + userId;
        long count = redisTemplate.opsForValue().increment(key, 1);
//        redisTemplate.opsForValue().set("test", "",60*10,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间

		if (count > 1) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_CONCURRENT_LIMIT);
		}

		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
    }

    private void unlockLoan(Long userId) {
        String key = "LOAN_LOCK_" + userId;
        redisTemplate.delete(key);
    }

    @Override
    public void dealLoanSucc(Long loanId, String tradeNoOut) {

    }

    @Override
    public void dealLoanFail(Long loanId, String tradeNoOut, String msgOut) {
        DsedLoanDo loanDo = dsedLoanDao.getById(loanId);
        loanDo.setTradeNoOut(tradeNoOut);
        List<DsedLoanPeriodsDo> periodDos = dsedLoanPeriodsDao.listByLoanId(loanDo.getRid());

        dealLoanFail(loanDo, periodDos, msgOut);
    }

    /**
     * 关闭 借款分期记录
     */
    private void closePeriods(List<DsedLoanPeriodsDo> periodDos) {
        for (DsedLoanPeriodsDo afLoanPeriodsDo : periodDos) {
            afLoanPeriodsDo.setStatus(AfLoanPeriodStatus.CLOSED.name());
            afLoanPeriodsDo.setGmtModified(new Date());
            dsedLoanPeriodsDao.updateById(afLoanPeriodsDo);
        }
        logger.info("--->close periods");
    }

    private void dealLoanFail(final DsedLoanDo loanDo, List<DsedLoanPeriodsDo> periodDos, String msg) {
        Date cur = new Date();
        loanDo.setStatus(AfLoanStatus.CLOSED.name());
        loanDo.setRemark("UPS打款失败，" + msg);
        loanDo.setGmtClose(cur);
        loanDo.setGmtModified(cur);
        logger.info("--->close loan UPS打款失败:loanId=" + loanDo.getRid());
        // 关闭分期记录
        closePeriods(periodDos);

        transactionTemplate.execute(new TransactionCallback<Long>() {
            public Long doInTransaction(TransactionStatus status) {
                try {
                    dsedLoanDao.updateById(loanDo);
                    return 1L;
                } catch (Exception e) {
                    logger.error("dealLoanSucc update db error", e);
                    throw e;
                }
            }
        });
    }

    @Override
    public BigDecimal getUserLayDailyRate(Long userId, String prdType) {
        return null;
    }

    @Override
    public DsedLoanDo selectById(Long loanId) {
        return null;
    }

    @Override
    public DsedLoanDo getByLoanNo(String loanNo) {
        return null;
    }

    @Override
    public DsedLoanDo getByUserId(Long userId) {
        return dsedLoanDao.getByUserId(userId);
    }

    @Override
    public DsedLoanDo getLastByUserIdAndPrdType(Long userId, String prdType) {
        return null;
    }


	@Override
	public int updateByLoanId(DsedLoanDo loanDo) {
		return dsedLoanDao.updateByLoanId(loanDo);
	}
}