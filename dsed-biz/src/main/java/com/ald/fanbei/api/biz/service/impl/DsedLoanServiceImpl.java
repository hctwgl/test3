package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.UpsDelegatePayRespBo;
import com.ald.fanbei.api.biz.bo.XgxyPayBo;
import com.ald.fanbei.api.biz.bo.assetpush.AssetPushType;
import com.ald.fanbei.api.biz.bo.assetpush.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.bo.dsed.DsedApplyLoanBo;
import com.ald.fanbei.api.biz.service.DsedLoanPeriodsService;
import com.ald.fanbei.api.biz.service.DsedLoanProductService;
import com.ald.fanbei.api.biz.service.DsedLoanPushService;
import com.ald.fanbei.api.biz.service.DsedLoanService;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;
import com.ald.fanbei.api.biz.service.DsedResourceService;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.DsedLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.DsedLoanStatus;
import com.ald.fanbei.api.common.enums.DsedNoticeType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.DsedUserBankcardDao;
import com.ald.fanbei.api.dal.domain.DsedLoanDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.DsedLoanPushDo;
import com.ald.fanbei.api.dal.domain.DsedLoanRateDo;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.DsedResourceDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import com.alibaba.fastjson.JSON;


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
    public static final BigDecimal DAYS_OF_YEAR = BigDecimal.valueOf(360);
    public static final BigDecimal DAYS_OF_MONTH = BigDecimal.valueOf(30);

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

    @Resource
    private DsedNoticeRecordService dsedNoticeRecordService;

    @Resource
    private XgxyUtil xgxyUtil;

    @Resource
    private DsedLoanProductService dsedLoanProductService;

    @Resource
    private DsedResourceService dsedResourceService;
    
    @Resource
    private AssetSideEdspayUtil assetSideEdspayUtil;
    
    @Resource
    private DsedLoanPushService dsedLoanPushService;
    
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

            if (bankCard == null){
                throw new FanbeiException(FanbeiExceptionCode.DSED_BANK_NOT_EXIST_ERROR);
            }

            this.saveLoanRecords(bo, loanDo, periodDos, bankCard);

            try {
				Boolean isWhite=true;
				DsedResourceDo pushWhiteResource = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_WHITE.getCode());
				if (pushWhiteResource != null && pushWhiteResource.getValue3() !=null) {
					//白名单开启
					String[] whiteUserIdStrs = pushWhiteResource.getValue3().split(",");
					Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
					if(!Arrays.asList(whiteUserIds).contains(userId)){
						//不在白名单不推送
						isWhite=false;
					}
				}
				DsedResourceDo assetPushResource = dsedResourceService.getConfigByTypesAndSecType(ResourceType.ASSET_PUSH_CONF.getCode(), AfResourceSecType.ASSET_PUSH_RECEIVE.getCode());
				AssetPushType assetPushType = JSON.toJavaObject(JSON.parseObject(assetPushResource.getValue()), AssetPushType.class);
				Boolean bankIsMaintaining = bankIsMaintaining(assetPushResource);
				if (StringUtil.equals(assetPushType.getDsed(), YesNoStatus.YES.getCode())
					&&(StringUtil.equals(loanDo.getAppName(), "www")||StringUtil.equals(loanDo.getAppName(), ""))
					&&StringUtil.equals(YesNoStatus.NO.getCode(), assetPushResource.getValue3())&&isWhite&&!bankIsMaintaining) {//推送eds放款
					List<EdspayGetCreditRespBo> dsedBorrowInfo = assetSideEdspayUtil.buildDsedBorrowInfo(loanDo);
					//债权实时推送
					boolean result = assetSideEdspayUtil.dsedCurPush(dsedBorrowInfo, Constants.ASSET_SIDE_EDSPAY_FLAG,Constants.ASSET_SIDE_FANBEI_FLAG);
					if (result) {
						logger.info("dsedCurPush suceess,orderNo="+dsedBorrowInfo.get(0).getOrderNo());
						loanDo.setStatus(DsedLoanStatus.TRANSFERING.name());
						dsedLoanDao.updateById(loanDo);
						// 增加日志
						DsedLoanPushDo loanPush = buildLoanPush(loanDo.getRid(),dsedBorrowInfo.get(0).getApr(), dsedBorrowInfo.get(0).getManageFee());
						dsedLoanPushService.saveOrUpdate(loanPush);
					}
				}else{// 调用UPS打款
	                UpsDelegatePayRespBo upsResult = upsUtil.dsedDelegatePay(bo.reqParam.amount,
	                        bo.realName, bankCard.getBankCardNumber(), userId.toString(), bankCard.getMobile(),
	                        bankCard.getBankName(), bankCard.getBankCode(), Constants.DEFAULT_LOAN_PURPOSE, "02",
	                        "DSED_LOAN", loanDo.getRid().toString(),bo.idNumber);
	                loanDo.setTradeNoOut(upsResult.getOrderNo());
	                if (!upsResult.isSuccess()) {
	                    //审核通过，ups打款失败
	                    dealLoanFail(loanDo, periodDos, "UPS打款失败，" + upsResult.getRespCode());
	                    throw new FanbeiException(FanbeiExceptionCode.LOAN_UPS_DRIECT_FAIL);
	                }
	                loanDo.setStatus(DsedLoanStatus.TRANSFERING.name());
	                dsedLoanDao.updateById(loanDo);	
				}
            } catch (Exception e) {
                loanDo.setStatus(DsedLoanStatus.CLOSED.name());
                dsedLoanDao.updateById(loanDo);

                // 关闭分期记录
                closePeriods(periodDos);

                throw e;
            }

        } finally {
            this.unlockLoan(userId);
        }
    }
    
    private Boolean bankIsMaintaining(DsedResourceDo assetPushResource) {
		Boolean bankIsMaintaining=false;
		if (null != assetPushResource && StringUtil.isNotBlank(assetPushResource.getValue4())) {
			String[] split = assetPushResource.getValue4().split(",");
			String maintainStart = split[0];
			String maintainEnd = split[1];
			Date maintainStartDate =DateUtil.parseDate(maintainStart,DateUtil.DATE_TIME_SHORT);
			Date gmtCreateEndDate =DateUtil.parseDate(maintainEnd,DateUtil.DATE_TIME_SHORT);
			 bankIsMaintaining = DateUtil.isBetweenDateRange(new Date(),maintainStartDate,gmtCreateEndDate);
		}
		return bankIsMaintaining;
	}

    @Override
    public DsedLoanDo resolveLoan(BigDecimal amount, Long userId, int periods, String loanNo, String prdType) {
        DsedLoanRateDo loanRateDo = dsedLoanProductService.getByPrdTypeAndNper(prdType, periods+"");

        BigDecimal interestRate = new BigDecimal(loanRateDo.getInterestRate());
        BigDecimal poundageRate = new BigDecimal(loanRateDo.getPoundageRate());
        BigDecimal overdueRate = new BigDecimal(loanRateDo.getOverdueRate());
        BigDecimal layRate = interestRate.add(poundageRate);
        BigDecimal userLayDailyRate = layRate.divide(DAYS_OF_YEAR, 10, RoundingMode.HALF_UP);
        BigDecimal interestRatio = interestRate.divide(layRate, 10, RoundingMode.HALF_UP);
        // 设贷款额为a，月利率为i，年利率为I，还款月数为n，每月还款额为b，还款利息总和为Y
        BigDecimal a = amount;
        BigDecimal i = userLayDailyRate.multiply(DAYS_OF_MONTH);
        int n = periods;
        // 月均总还款:b ＝ a×i×（1＋i）^n÷〔（1＋i）^n－1〕
        BigDecimal totalFeePerPeriod = a.multiply(i)
                .multiply( (i.add(BigDecimal.ONE)).pow(n) )
                .divide( (i.add(BigDecimal.ONE)).pow(n).subtract(BigDecimal.ONE) , 2, RoundingMode.HALF_UP);
        // 还款总额:n * b
        BigDecimal totalFee = totalFeePerPeriod.multiply( BigDecimal.valueOf(periods) );
        // 支付总利息:n * b - a
        BigDecimal totalIncome = totalFee.subtract(a).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInterestFee = totalIncome.multiply(interestRatio).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalServiceFee = totalIncome.subtract(totalInterestFee).setScale(2, RoundingMode.HALF_UP);
        DsedLoanDo loanDo = DsedLoanDo.gen(periods, poundageRate, interestRate,overdueRate,
                amount, totalServiceFee, totalInterestFee);
        return loanDo;
    }

    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_UNCOMMITTED, timeout = 30, value = "appTransactionTemplate", rollbackFor = Exception.class)
    public void saveLoanRecords(final DsedApplyLoanBo bo, final DsedLoanDo loanDo,
                                final List<DsedLoanPeriodsDo> periodDos, final DsedUserBankcardDo bankCard) {
        try {
            DsedApplyLoanBo.ReqParam reqParam = bo.reqParam;

            loanDo.setBankCardNumber(bankCard.getBankCardNumber());
            loanDo.setBankCardName(bankCard.getBankName());
            loanDo.setIp(reqParam.ip);
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
        String key = "DSED_LOAN_LOCK_" + userId;
        long count = redisTemplate.opsForValue().increment(key, 1);
//        redisTemplate.opsForValue().set("test", "",60*10,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间

		if (count > 1) {
			throw new FanbeiException(FanbeiExceptionCode.LOAN_CONCURRENT_LIMIT);
		}

		redisTemplate.expire(key, 30, TimeUnit.SECONDS);
    }

    private void unlockLoan(Long userId) {
        String key = "DSED_LOAN_LOCK_" + userId;
        redisTemplate.delete(key);
    }

    @Override
    public void dealLoanSucc(Long loanId, String tradeNoOut) {
        final DsedLoanDo loanDo = dsedLoanDao.getById(loanId);
        String status = loanDo.getStatus();
        if(DsedLoanStatus.TRANSFERRED.name().equals(status)) {//已经处理过，防重复回调
            logger.warn("dsedLoanService DealLoanSucc, transfer has succ, repeat UPS invoke! loanId="+loanId+",tradeNoOut="+tradeNoOut);
            return;
        }

        if(DsedLoanStatus.CLOSED.name().equals(status)) { // 已失败订单，但UPS仍回调成功，日志打点记录
            logger.warn("dsedLoanService DealLoanSucc, transfer has fail, but still callback! original status= "+status+",loanId="+loanId+",tradeNoOut="+tradeNoOut);
        }

        Date cur = new Date();
        loanDo.setTradeNoOut(tradeNoOut);
        loanDo.setStatus(DsedLoanStatus.TRANSFERRED.name());
        loanDo.setArrivalAmount(loanDo.getAmount());
        loanDo.setGmtArrival(cur);
        transactionTemplate.execute(new TransactionCallback<Long>() { public Long doInTransaction(TransactionStatus status) {
            try {
                dsedLoanDao.updateById(loanDo);
                return 1L;
            } catch (Exception e) {
                logger.error("dsedLoanService dealLoanSucc update db error", e);
                throw e;
            }
        }});

        dsedNoticeRecord(loanDo, "","PAYSUCCESS");
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
            afLoanPeriodsDo.setStatus(DsedLoanPeriodStatus.CLOSED.name());
            afLoanPeriodsDo.setGmtModified(new Date());
            dsedLoanPeriodsDao.updateById(afLoanPeriodsDo);
        }
        logger.info("--->close periods");
    }

    @Override
    public void dealLoanFail(final DsedLoanDo loanDo, List<DsedLoanPeriodsDo> periodDos, String msg) {
        Date cur = new Date();
        loanDo.setStatus(DsedLoanStatus.CLOSED.name());
        loanDo.setRemark(msg);
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
        //通知用户
        dsedNoticeRecord(loanDo, msg,"PAYFAIL");
    }

    private void dsedNoticeRecord(DsedLoanDo loanDo,String msg,String status) {
        try {
            //调用西瓜信用通知接口
            DsedNoticeRecordDo noticeRecordDo = buildDsedNoticeRecord(loanDo);
            dsedNoticeRecordService.addNoticeRecord(noticeRecordDo);
            XgxyPayBo xgxyPayBo = buildXgxyPay(loanDo, msg,status);
            if(xgxyUtil.payNoticeRequest(xgxyPayBo)){
                noticeRecordDo.setRid(noticeRecordDo.getRid());
                noticeRecordDo.setGmtModified(new Date());
                dsedNoticeRecordService.updateNoticeRecordStatus(noticeRecordDo);
            }
        } catch (Exception e) {
            logger.error("dsedNoticeRecord, notify user occur error!", e); //通知过程抛出任何异常捕获，不影响主流程
        }
    }

    private XgxyPayBo buildXgxyPay(DsedLoanDo loanDo, String msg,String status) {
        XgxyPayBo  xgxyPayBo = new XgxyPayBo();
        xgxyPayBo.setTrade(loanDo.getTradeNoOut());
        xgxyPayBo.setBorrowNo(loanDo.getLoanNo());
        xgxyPayBo.setReason(msg);
        xgxyPayBo.setStatus(status);
        xgxyPayBo.setGmtArrival(loanDo.getGmtArrival());
        return xgxyPayBo;
    }

    private DsedNoticeRecordDo buildDsedNoticeRecord(DsedLoanDo loanDo) {
        DsedNoticeRecordDo noticeRecordDo = new DsedNoticeRecordDo();
        noticeRecordDo.setUserId(loanDo.getUserId());
        noticeRecordDo.setRefId(String.valueOf(loanDo.getRid()));
        noticeRecordDo.setType(DsedNoticeType.PAY.code);
        noticeRecordDo.setTimes(Constants.NOTICE_FAIL_COUNT);
        return noticeRecordDo;
    }

    @Override
    public BigDecimal getUserLayDailyRate(Long userId, String prdType) {
        return null;
    }

    @Override
    public String getCurrentLastBorrowNo(String orderNoPre) {
        return dsedLoanDao.getCurrentLastBorrowNo(orderNoPre);
    }

    @Override
    public DsedLoanDo selectById(Long loanId) {
        return null;
    }

    @Override
    public DsedLoanDo getByLoanNo(String loanNo) {
        return dsedLoanDao.getByLoanNo(loanNo);
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
	
	private DsedLoanPushDo buildLoanPush(Long rid, BigDecimal apr,BigDecimal manageFee) {
		DsedLoanPushDo loanPushDo =new DsedLoanPushDo();
		Date now = new Date();
		loanPushDo.setGmtCreate(now);
		loanPushDo.setGmtModified(now);
		loanPushDo.setLoanId(rid);
		loanPushDo.setBorrowRate(apr);
		loanPushDo.setProfitRate(manageFee);
		return loanPushDo;
	}
}