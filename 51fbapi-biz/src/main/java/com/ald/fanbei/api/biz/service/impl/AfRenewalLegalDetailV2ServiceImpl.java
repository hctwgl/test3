package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import org.apache.commons.lang.StringUtils;
import org.dbunit.util.Base64;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.KuaijieOrderPayBo;
import com.ald.fanbei.api.biz.bo.KuaijieRenewalPayBo;
import com.ald.fanbei.api.biz.bo.RiskOverdueBorrowBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.biz.util.SmartAddressEngine;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowLegalRepaymentStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfRenewalDetailDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

/**
 * @Description:
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author yanghailong
 * @date 2017年12月11日
 */
@Service("afRenewalLegalDetailV2Service")
public class AfRenewalLegalDetailV2ServiceImpl extends UpsPayKuaijieServiceAbstract implements AfRenewalLegalDetailV2Service {
    @Resource
    UpsUtil upsUtil;
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    private JpushService pushService;
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    private AfUserService afUserService;
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
    AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    RiskUtil riskUtil;
    @Resource
    CollectionSystemUtil collectionSystemUtil;
    @Resource
    AfYibaoOrderDao afYibaoOrderDao;
    @Resource
    YiBaoUtility yiBaoUtility;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    ContractPdfThreadPool contractPdfThreadPool;
    @Resource
    SmsUtil smsUtil;
    @Resource
    AfBorrowLegalOrderService afBorrowLegalOrderService;
    @Resource
    AfBorrowLegalOrderCashService afBorrowLegalOrderCashService;
    @Resource
    AfRenewalLegalDetailService afRenewalLegalDetailService;
    @Resource
    AfBorrowLegalOrderRepaymentDao afBorrowLegalOrderRepaymentDao;
    @Resource
    AfBorrowLegalOrderDao afBorrowLegalOrderDao;
    @Resource
    AfGoodsDao afGoodsDao;
    @Resource
    AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
    @Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;
    @Resource
    SmartAddressEngine smartAddressEngine;
    @Resource
    AfTaskUserService afTaskUserService;

    @Override
    public Map<String, Object> createLegalRenewal(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrow, Long cardId, Long userId, String clientIp, AfUserAccountDo afUserAccountDo, Integer appVersion, Long goodsId, String deliveryUser, String deliveryPhone, String address, String bankChannel) {
	Date now = new Date();
	String repayNo = generatorClusterNo.getRenewalBorrowCashNo(now);
	final String payTradeNo = repayNo;

	String name = Constants.DEFAULT_RENEWAL_NAME_BORROW_CASH;

	// 新增还款记录
	final AfRenewalDetailDo renewalDetail = buildRenewalDetailDo(afBorrowCashDo, jfbAmount, repaymentAmount, repayNo, actualAmount, rebateAmount, capital, borrow, cardId, payTradeNo, userId, appVersion);

	// 新增订单记录
	final AfBorrowLegalOrderDo borrowLegalOrder = buildAfBorrowLegalOrderDo(afBorrowCashDo, userId, goodsId, deliveryUser, deliveryPhone, address);

	Map<String, Object> map = new HashMap<String, Object>();

	transactionTemplate.execute(new TransactionCallback<Long>() {
	    @Override
	    public Long doInTransaction(TransactionStatus status) {
		try {
		    afBorrowLegalOrderService.saveBorrowLegalOrder(borrowLegalOrder);
		    afRenewalDetailDao.addRenewalDetail(renewalDetail);

		    return 1l;
		} catch (Exception e) {
		    status.setRollbackOnly();
		    logger.info("sava record error", e);
		    throw e;
		}
	    }
	});
	// 百度智能地址
	try {
	    smartAddressEngine.setScoreAsyn(borrowLegalOrder.getAddress(), borrowLegalOrder.getBorrowId(), borrowLegalOrder.getOrderNo());
	} catch (Exception e) {
	    logger.info("smart address {}", e);
	}
	if (cardId > 0) {// 银行卡支付
	    AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(cardId);
	    KuaijieRenewalPayBo bizObject = new KuaijieRenewalPayBo(renewalDetail);
	    if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
		map = sendKuaiJieSms(bank.getRid(), payTradeNo, actualAmount, userId, afUserAccountDo.getRealName(), 
			afUserAccountDo.getIdNumber(), JSON.toJSONString(bizObject), "afRenewalLegalDetailV2Service",Constants.DEFAULT_PAY_PURPOSE, name, 
			PayOrderSource.RENEW_CASH_LEGAL_V2.getCode());
	    } else {// 代扣
		map = doUpsPay(bankChannel, bank.getRid(), payTradeNo, actualAmount, userId, afUserAccountDo.getRealName(),
			afUserAccountDo.getIdNumber(), "", JSON.toJSONString(bizObject),Constants.DEFAULT_PAY_PURPOSE, name, 
			PayOrderSource.RENEW_CASH_LEGAL_V2.getCode());
	    }
	    
	} else {
	    throw new FanbeiException("bank card pay error", FanbeiExceptionCode.BANK_CARD_PAY_ERR);
	}
	
	return map;
    }

    @Override
    protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo) {
	KuaijieRenewalPayBo kuaijieRenewalPayBo = JSON.parseObject(payBizObject, KuaijieRenewalPayBo.class);
	if (kuaijieRenewalPayBo.getRenewalDetail() != null) {
	    dealChangStatus(payTradeNo, payTradeNo, AfBorrowLegalRepaymentStatus.SMS.getCode(), kuaijieRenewalPayBo.getRenewalDetail().getRid());
	}
    }
    
    @Override
    protected void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
    	KuaijieRenewalPayBo kuaijieRenewalPayBo = JSON.parseObject(payBizObject, KuaijieRenewalPayBo.class);
    	if (kuaijieRenewalPayBo.getRenewalDetail() != null) {
    	    dealChangStatus(payTradeNo, payTradeNo, AfBorrowLegalRepaymentStatus.PROCESS.getCode(), kuaijieRenewalPayBo.getRenewalDetail().getRid());
    	}
    }
    
    @Override
    protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
	KuaijieRenewalPayBo kuaijieRenewalPayBo = JSON.parseObject(payBizObject, KuaijieRenewalPayBo.class);
	if (kuaijieRenewalPayBo.getRenewalDetail() != null) {
	    dealChangStatus(payTradeNo, payTradeNo, AfBorrowLegalRepaymentStatus.PROCESS.getCode(), kuaijieRenewalPayBo.getRenewalDetail().getRid());
	}	
    }

    @Override
    protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
    	KuaijieRenewalPayBo kuaijieRenewalPayBo = JSON.parseObject(payBizObject, KuaijieRenewalPayBo.class);

        Map<String, Object> resulMap = new HashMap<String, Object>();
        resulMap.put("outTradeNo", respBo.getOrderNo());
        resulMap.put("tradeNo", respBo.getTradeNo());
        resulMap.put("cardNo", Base64.encodeString(cardNo));
        resulMap.put("refId", kuaijieRenewalPayBo.getRenewalDetail().getRid());
        resulMap.put("type", UserAccountLogType.RENEWAL_PAY.getCode());
        
        return resulMap;
    }

    @Override
    protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
	
	    dealLegalRenewalFail(payTradeNo, payTradeNo, errorMsg);
    }

    long dealChangStatus(final String outTradeNo, final String tradeNo, final String status, final Long renewalDetailId) {

	AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
	if (afYibaoOrderDo != null) {
	    if (afYibaoOrderDo.getStatus().intValue() == 1) {
		return 1L;
	    } else {
		if (status.equals("N")) {
		    afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 2);
		}
	    }
	}

	transactionTemplate.execute(new TransactionCallback<Long>() {
	    @Override
	    public Long doInTransaction(TransactionStatus t) {
		try {

		    // 更新还款记录
		    AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByRenewalId(renewalDetailId);
		    afRenewalDetailDo.setStatus(status);
		    afRenewalDetailDo.setTradeNo(tradeNo);
		    afRenewalDetailDo.setRid(renewalDetailId);
		    afRenewalDetailDo.setGmtModified(new Date());
		    afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);

		    if (status.equals("N")) {
			// 获取新增的订单
			AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getLastBorrowLegalOrderByBorrowId(afRenewalDetailDo.getBorrowId());

			// 关闭新增订单记录
			if (borrowLegalOrderDo.getStatus().equals("UNPAID")) { // 只对新增订单操作
			    borrowLegalOrderDo.setStatus("CLOSED");
			    borrowLegalOrderDo.setGmtModified(new Date());
			    afBorrowLegalOrderDao.updateById(borrowLegalOrderDo);
			}
		    }

		    return 1l;
		} catch (Exception e) {
		    t.setRollbackOnly();
		    logger.info("sava record error", e);
		    return 0l;
		}
	    }
	});
	return 1L;
    }

    @Override
    public long dealLegalRenewalFail(String outTradeNo, String tradeNo, String errorMsg) {
	AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);

	if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
	    return 0l;
	}
	long result = dealChangStatus(outTradeNo, tradeNo, AfBorrowLegalRepaymentStatus.NO.getCode(), afRenewalDetailDo.getRid());

	AfUserDo userDo = afUserService.getUserById(afRenewalDetailDo.getUserId());
	try {
	    pushService.repayRenewalFail(userDo.getUserName());
	} catch (Exception e) {
	    logger.error("dealLegalRenewalFail push exception.", e);
	}
	// fmf_add 续借失败短信通知
	// 模版数据map处理
	Map<String, String> replaceMapData = new HashMap<String, String>();
	replaceMapData.put("errorMsg", errorMsg);
	// 用户信息及当日还款失败次数校验
	int errorTimes = afRenewalDetailDao.getCurrDayRepayErrorTimes(afRenewalDetailDo.getUserId());
	try {
	    smsUtil.sendConfigMessageToMobile(userDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RENEWAL_DETAIL_FAIL.getCode());
	    String title = "本次续借支付失败";
	    String content = "续借支付失败：&errorMsg。";
	    content = content.replace("&errorMsg", errorMsg);
	    pushService.pushUtil(title, content, userDo.getMobile());
	} catch (Exception e) {
	    logger.error("sendRenewalFailWarnMsg is Fail.", e);
	}

	return result;
    }

    @Resource
    CuiShouUtils cuiShouUtils;

    @Override
    public long dealLegalRenewalSucess(final String outTradeNo, final String tradeNo) {

	final String key = outTradeNo + "_success_repayCash_renewal";
	long count = redisTemplate.opsForValue().increment(key, 1);
	redisTemplate.expire(key, 30, TimeUnit.SECONDS);
	if (count != 1) {
	    return -1;
	}
	try {
	    // 获取新增本金续借记录
	    final AfRenewalDetailDo afRenewalDetailDo = afRenewalDetailDao.getRenewalDetailByPayTradeNo(outTradeNo);
	    // 获取新增的订单
	    final AfBorrowLegalOrderDo borrowLegalOrderDo = afBorrowLegalOrderDao.getLastBorrowLegalOrderByBorrowId(afRenewalDetailDo.getBorrowId());

	    logger.info("afRenewalLegalDetailService : afRenewalDetailDo=" + afRenewalDetailDo + "; borrowLegalOrderDo=" + borrowLegalOrderDo);
	    if (YesNoStatus.YES.getCode().equals(afRenewalDetailDo.getStatus())) {
		redisTemplate.delete(key);
		return 0l;
	    }

	    long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
		@Override
		public Long doInTransaction(TransactionStatus status) {
		    try {
			AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(outTradeNo);
			if (afYibaoOrderDo != null) {
			    if (afYibaoOrderDo.getStatus().intValue() == 1) {
				return 0L;
			    } else {
				afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 1);
			    }
			}

			// 更新新增订单状态为待发货
			borrowLegalOrderDo.setStatus("AWAIT_DELIVER"); // 待发货
			borrowLegalOrderDo.setGmtModified(new Date());
			afBorrowLegalOrderDao.updateById(borrowLegalOrderDo);

			// 更新续期记录为续期成功
			afRenewalDetailDo.setStatus(AfBorrowLegalRepaymentStatus.YES.getCode());
			afRenewalDetailDo.setTradeNo(tradeNo);
			afRenewalDetailDo.setGmtModified(new Date());
			afRenewalDetailDao.updateRenewalDetail(afRenewalDetailDo);

			// 借款记录
			AfBorrowCashDo afBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());

			// 续期本金
			BigDecimal waitPaidAmount = afRenewalDetailDo.getRenewalAmount();
			// 查询新利率配置
			AfResourceDo rateInfoDo = afResourceService.getConfigByTypesAndSecType(Constants.BORROW_RATE, Constants.BORROW_CASH_INFO_LEGAL_NEW);
			// 借款利率
			BigDecimal newRate = null;
			// 借款手续费率
			BigDecimal newServiceRate = null;

			if (rateInfoDo != null) {
			    String borrowRate = rateInfoDo.getValue2();
			    JSONArray array = JSONObject.parseArray(borrowRate);
			    double rate = 0;
			    double serviceRate = 0;
			    for (int i = 0; i < array.size(); i++) {
				JSONObject info = array.getJSONObject(i);
				String borrowTag = info.getString("borrowTag");
				if (StringUtils.equals("INTEREST_RATE", borrowTag)) {
				    rate = info.getDouble("borrowFirstType");
				}
				if (StringUtils.equals("SERVICE_RATE", borrowTag)) {
				    serviceRate = info.getDouble("borrowFirstType");
				}
			    }
			    newRate = BigDecimal.valueOf(rate / 100);
			    newServiceRate = BigDecimal.valueOf(serviceRate / 100);
			} else {
			    throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_RATE_ERROR);
			}
			BigDecimal rateAmount = BigDecimalUtil.multiply(waitPaidAmount, newRate, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));
			BigDecimal poundage = BigDecimalUtil.multiply(waitPaidAmount, newServiceRate, new BigDecimal(afRenewalDetailDo.getRenewalDay()).divide(new BigDecimal(Constants.ONE_YEAY_DAYS), 6, RoundingMode.HALF_UP));

			Date gmtPlanRepayment = afBorrowCashDo.getGmtPlanRepayment();
			Date now = new Date(System.currentTimeMillis());

			// 如果预计还款时间在今天之后，则在原预计还款时间的基础上加上续期天数，否则在今天的基础上加上续期天数，作为新的预计还款时间
			if (gmtPlanRepayment.after(now)) {
			    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(gmtPlanRepayment, afRenewalDetailDo.getRenewalDay()));
			    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
			} else {
			    Date repaymentDay = DateUtil.getEndOfDatePrecisionSecond(DateUtil.addDays(now, afRenewalDetailDo.getRenewalDay()));
			    afBorrowCashDo.setGmtPlanRepayment(repaymentDay);
			}

			afBorrowCashDo.setRepayAmount(BigDecimalUtil.add(afBorrowCashDo.getRepayAmount(), afRenewalDetailDo.getPriorInterest(), afRenewalDetailDo.getPriorOverdue(), afRenewalDetailDo.getPriorPoundage(), afRenewalDetailDo.getCapital())); // 累计已还款金额
			afBorrowCashDo.setSumOverdue(afBorrowCashDo.getSumOverdue().add(afBorrowCashDo.getOverdueAmount())); // 累计滞纳金
			afBorrowCashDo.setOverdueAmount(BigDecimal.ZERO); // 滞纳金置0
			afBorrowCashDo.setSumRate(afBorrowCashDo.getSumRate().add(afBorrowCashDo.getRateAmount())); // 累计利息
			afBorrowCashDo.setRateAmount(rateAmount); // 利息改成本次续期金额的利息
			afBorrowCashDo.setSumRenewalPoundage(afBorrowCashDo.getSumRenewalPoundage().add(afRenewalDetailDo.getPriorPoundage())); // 累计续期手续费
			afBorrowCashDo.setPoundage(poundage);
			afBorrowCashDo.setRenewalNum(afBorrowCashDo.getRenewalNum() + 1); // 累计续期次数
			afBorrowCashService.updateBorrowCash(afBorrowCashDo);

			// 授权账户可用金额变更
			AfUserAccountDo account = new AfUserAccountDo();
			account.setUserId(afRenewalDetailDo.getUserId());
			account.setJfbAmount(afRenewalDetailDo.getJfbAmount().multiply(new BigDecimal(-1)));
			account.setRebateAmount(afRenewalDetailDo.getRebateAmount().multiply(new BigDecimal(-1)));
			afUserAccountDao.updateUserAccount(account);

			afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.RENEWAL_PAY, afRenewalDetailDo.getRebateAmount(), afRenewalDetailDo.getUserId(), afRenewalDetailDo.getRid()));

			// add by luoxiao for 边逛边赚，增加零钱明细
			afTaskUserService.addTaskUser(afRenewalDetailDo.getUserId(),UserAccountLogType.RENEWAL_PAY.getName(), afRenewalDetailDo.getRebateAmount().multiply(new BigDecimal(-1)));
			// end by luoxiao

			// 续借成功发送短信和消息通知
			AfResourceDo resourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_RENEWAL_DETAIL_SUCCESS.getCode());
			if (null != resourceDo) {
			    AfUserDo afUserDo = afUserService.getUserById(afBorrowCashDo.getUserId());
			    if (null != afUserDo) {
				String content = resourceDo.getValue();
				Calendar c = Calendar.getInstance();
				c.setTime(afBorrowCashDo.getGmtPlanRepayment());
				int month = c.get(Calendar.MONTH) + 1;
				int day = c.get(Calendar.DATE);
				content = content.replace("M", month + "");
				content = content.replace("D", day + "");
				smsUtil.sendMessageToMobile(afUserDo.getUserName(), content);
				String title = "恭喜您，续借成功";
				String msgcontent = "恭喜，您已经续借成功，最新还款日为M月D日，请按时还款，保持良好信用。";
				msgcontent = msgcontent.replace("M", month + "");
				msgcontent = msgcontent.replace("D", day + "");
				pushService.pushUtil(title, msgcontent, afUserDo.getUserName());
			    }
			}
			return 1l;
		    } catch (Exception e) {
			status.setRollbackOnly();
			logger.info("dealRenewalSucess error", e);
			return 0l;
		    }
		}
	    });

	    if (resultValue == 1L) {
		// 续期成功,推送等抽出
		logger.info("续期成功，推送消息和向催收平台同步进入borrowCashId:" + afRenewalDetailDo.getBorrowId() + ",afRenewalDetailDoId:" + afRenewalDetailDo.getRid());

		AfBorrowCashDo currAfBorrowCashDo = afBorrowCashService.getBorrowCashByrid(afRenewalDetailDo.getBorrowId());
		AfUserDo userDo = afUserService.getUserById(currAfBorrowCashDo.getUserId());
		try {
		    // pushService.repayRenewalSuccess(userDo.getUserName());
		    logger.info("续期成功，推送消息成功outTradeNo:" + outTradeNo);
		} catch (Exception e) {
		    logger.error("续期成功，推送消息异常outTradeNo:" + outTradeNo, e);
		}

		// 当续期成功时,同步逾期天数为0
		dealWithSynchronizeOverduedOrder(currAfBorrowCashDo);

		try {
		    if (currAfBorrowCashDo.getOverdueStatus().equals("Y") || currAfBorrowCashDo.getOverdueDay() > 0) {
			CollectionSystemReqRespBo respInfo = collectionSystemUtil.renewalNotify(currAfBorrowCashDo.getBorrowNo(), afRenewalDetailDo.getPayTradeNo(), afRenewalDetailDo.getRenewalDay(), (afRenewalDetailDo.getNextPoundage().multiply(BigDecimalUtil.ONE_HUNDRED)) + "");
			logger.info("collection renewalNotify req success, respinfo={}", respInfo);
		    } else {
			logger.info("collection renewalNotify req unPush, renewalNo=" + afRenewalDetailDo.getPayTradeNo());
		    }
		} catch (Exception e) {
		    logger.error("向催收平台同步续期信息", e);
		}
		cuiShouUtils.syncXuqi(currAfBorrowCashDo);
	    }
	    if (resultValue == 1L) {
		// 生成续期凭据
		contractPdfThreadPool.protocolRenewalPdf(afRenewalDetailDo.getUserId(), afRenewalDetailDo.getBorrowId(), afRenewalDetailDo.getRid(), afRenewalDetailDo.getRenewalDay(), afRenewalDetailDo.getRenewalAmount());
	    }

	    return resultValue;
	} finally {
	    redisTemplate.delete(key);
	}
    }

    /**
     * 同步逾期订单
     * 
     * @param borrowCashInfo
     */
    private void dealWithSynchronizeOverduedOrder(AfBorrowCashDo borrowCashInfo) {
	String identity = System.currentTimeMillis() + StringUtils.EMPTY;
	String orderNo = riskUtil.getOrderNo("over", identity.substring(identity.length() - 4, identity.length()));
	List<RiskOverdueBorrowBo> boList = new ArrayList<RiskOverdueBorrowBo>();
	boList.add(parseOverduedBorrowBo(borrowCashInfo.getBorrowNo(), 0, null));
	logger.info("dealWithSynchronizeOverduedOrder begin orderNo = {} , boList = {}", orderNo, boList);
	try {
	    riskUtil.batchSychronizeOverdueBorrow(orderNo, boList);
	} catch (Exception e) {
	    logger.error("续借成功时给风控传输数据出错", e);
	}

	logger.info("dealWithSynchronizeOverduedOrder completed");
    }

    private RiskOverdueBorrowBo parseOverduedBorrowBo(String borrowNo, Integer overdueDay, Integer overduetimes) {
	RiskOverdueBorrowBo bo = new RiskOverdueBorrowBo();
	bo.setBorrowNo(borrowNo);
	bo.setOverdueDays(overdueDay);
	bo.setOverdueTimes(overduetimes);
	return bo;
    }

    private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType type, BigDecimal amount, Long userId, Long renewalDetailId) {
	// 增加account变更日志
	AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
	accountLog.setAmount(amount);
	accountLog.setUserId(userId);
	accountLog.setRefId(renewalDetailId + "");
	accountLog.setType(type.getCode());
	return accountLog;
    }

    /**
     * @Description: 本金还款记录
     * @return AfRenewalDetailDo
     * @data 2017年12月14日
     */
    private AfRenewalDetailDo buildRenewalDetailDo(AfBorrowCashDo afBorrowCashDo, BigDecimal jfbAmount, BigDecimal repaymentAmount, String tradeNo, BigDecimal actualAmount, BigDecimal rebateAmount, BigDecimal capital, Long borrowId, Long cardId, String payTradeNo, Long userId, Integer appVersion) {

	AfResourceDo resource = afResourceService.getConfigByTypesAndSecType(Constants.RES_RENEWAL_DAY_LIMIT, Constants.RES_ALLOW_RENEWAL_DAY_NEW);
	BigDecimal allowRenewalDay = new BigDecimal(resource.getValue());// 允许续期天数

	BigDecimal borrowCashPoundage = afBorrowCashDo.getPoundageRate();
	AfResourceDo baseBankRateResource = afResourceService.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE, Constants.RES_BASE_BANK_RATE);
	BigDecimal baseBankRate = new BigDecimal(baseBankRateResource.getValue());// 央行基准利率

	// 上期借款手续费
	BigDecimal borrowPoundage = afBorrowCashDo.getPoundage();
	// 上期借款利息
	BigDecimal borrowRateAmount = afBorrowCashDo.getRateAmount();

	// 续借本金（总）
	BigDecimal allAmount = BigDecimalUtil.add(afBorrowCashDo.getAmount(), afBorrowCashDo.getSumOverdue(), afBorrowCashDo.getSumRate(), afBorrowCashDo.getSumRenewalPoundage());
	// 续期金额 = 续借本金（总） - 借款已还金额 - 续借需要支付本金
	BigDecimal waitPaidAmount = BigDecimalUtil.subtract(allAmount, afBorrowCashDo.getRepayAmount()).subtract(capital);

	// 借款续期记录
	AfRenewalDetailDo afRenewalDetailDo = new AfRenewalDetailDo();
	afRenewalDetailDo.setBorrowId(borrowId);
	afRenewalDetailDo.setStatus(AfBorrowLegalRepaymentStatus.APPLY.getCode());
	afRenewalDetailDo.setGmtPlanRepayment(afBorrowCashDo.getGmtPlanRepayment()); // 原预计还款时间
	afRenewalDetailDo.setRenewalAmount(waitPaidAmount); // 续期本金
	afRenewalDetailDo.setPriorInterest(borrowRateAmount); // 上期利息
	afRenewalDetailDo.setPriorOverdue(afBorrowCashDo.getOverdueAmount()); // 上期滞纳金
	afRenewalDetailDo.setPriorPoundage(borrowPoundage); // 上期手续费
	afRenewalDetailDo.setNextPoundage(BigDecimal.ZERO); // 下期手续费
	afRenewalDetailDo.setJfbAmount(jfbAmount); // 集分宝个数
	afRenewalDetailDo.setRebateAmount(rebateAmount); // 账户余额
	afRenewalDetailDo.setPayTradeNo(payTradeNo); // 平台提供给三方支付的交易流水号
	afRenewalDetailDo.setTradeNo(tradeNo); // 第三方的交易流水号
	afRenewalDetailDo.setRenewalDay(allowRenewalDay.intValue()); // 续期天数
	afRenewalDetailDo.setUserId(userId);
	afRenewalDetailDo.setActualAmount(actualAmount);
	afRenewalDetailDo.setPoundageRate(borrowCashPoundage); // 借钱手续费率（日）
	afRenewalDetailDo.setBaseBankRate(baseBankRate); // 央行基准利率
	afRenewalDetailDo.setCardNumber("");
	afRenewalDetailDo.setCapital(capital);

	if (cardId == -2) {
	    afRenewalDetailDo.setCardName(Constants.DEFAULT_USER_ACCOUNT);
	} else if (cardId == -1) {
	    afRenewalDetailDo.setCardName(Constants.DEFAULT_WX_PAY_NAME);
	} else if (cardId == -3) {
	    afRenewalDetailDo.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
	} else {
	    AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
	    afRenewalDetailDo.setCardNumber(bank.getCardNumber());
	    afRenewalDetailDo.setCardName(bank.getBankName());
	}
	logger.info("buildRenewalDetailDo :", afRenewalDetailDo);
	return afRenewalDetailDo;
    }

    /**
     * @Description: 订单记录
     * @return AfBorrowLegalOrderDo
     * @data 2017年12月14日
     */
    private AfBorrowLegalOrderDo buildAfBorrowLegalOrderDo(AfBorrowCashDo afBorrowCashDo, Long userId, Long goodsId, String deliveryUser, String deliveryPhone, String address) {
	// 新增订单记录
	AfGoodsDo goodsDo = afGoodsDao.getGoodsById(goodsId);
	if (goodsDo == null) {
	    throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_GOOD_NOT_EXIST_ERROR);
	}
	AfBorrowLegalOrderDo borrowLegalOrder = new AfBorrowLegalOrderDo();
	borrowLegalOrder.setBorrowId(afBorrowCashDo.getRid());
	borrowLegalOrder.setGoodsId(goodsId);
	borrowLegalOrder.setDeliveryUser(deliveryUser);
	borrowLegalOrder.setDeliveryPhone(deliveryPhone);
	borrowLegalOrder.setAddress(address);
	borrowLegalOrder.setUserId(userId);
	borrowLegalOrder.setStatus("UNPAID");// 未支付
	borrowLegalOrder.setPriceAmount(goodsDo.getSaleAmount());
	borrowLegalOrder.setGoodsName(goodsDo.getName());
	logger.info("buildAfBorrowLegalOrderDo :", borrowLegalOrder);
	return borrowLegalOrder;
    }

    @Override
    public AfRenewalDetailDo getLastRenewalDetailByBorrowId(Long rid) {
	return afRenewalDetailDao.getLastRenewalDetailByBorrowId(rid);
    }
}
