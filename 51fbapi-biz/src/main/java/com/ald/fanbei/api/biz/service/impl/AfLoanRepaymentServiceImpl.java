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

import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.CollectionSystemReqRespBo;
import com.ald.fanbei.api.biz.bo.KuaijieLoanBo;
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.bo.assetpush.ModifiedBorrowInfoVo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.service.AfLoanPeriodsService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.service.UpsPayKuaijieServiceAbstract;
import com.ald.fanbei.api.biz.third.util.AssetSideEdspayUtil;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.cuishou.CuiShouUtils;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.AfLoanRepaymentStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.BankPayChannel;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.enums.RepaymentStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.AfLoanRepaymentDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountSenceDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanPeriodsDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanRepaymentService")
public class AfLoanRepaymentServiceImpl extends UpsPayKuaijieServiceAbstract implements AfLoanRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanRepaymentServiceImpl.class);
   
    @Resource
    private GeneratorClusterNo generatorClusterNo;
    @Resource
	private AfLoanPeriodsService afLoanPeriodsService;
    @Resource
	private AfUserAccountSenceService afUserAccountSenceService;
    
    @Resource
    private AfUserAccountDao afUserAccountDao;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AfUserAccountLogDao afUserAccountLogDao;
    @Resource
	AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
    @Resource
    private AfUserCouponDao afUserCouponDao;
    @Resource
    private AfUserBankcardDao afUserBankcardDao;
	@Resource
	private AfUserAccountSenceDao afUserAccountSenceDao;
    @Resource
    private AfUserService afUserService;
    @Resource
    private UpsUtil upsUtil;
    @Resource
    private RiskUtil riskUtil;
    @Resource
    private SmsUtil smsUtil;
	@Resource
	private JpushService pushService;
    @Resource
    private AfYibaoOrderDao afYibaoOrderDao;
    @Resource
    private YiBaoUtility yiBaoUtility;
    @Resource
    private RedisTemplate<String, ?> redisTemplate;
	@Resource
    private AfTradeCodeInfoService afTradeCodeInfoService;
    @Resource
    private AfLoanRepaymentDao afLoanRepaymentDao;
    @Resource
    private AfLoanPeriodsDao afLoanPeriodsDao;
    @Resource
    private AfLoanDao afLoanDao;
    @Resource
    private CollectionSystemUtil collectionSystemUtil;
    @Resource
    private AfResourceService afResourceService;
	@Resource
	CuiShouUtils cuiShouUtils;
	@Resource
	KafkaSync kafkaSync;
	@Resource
	AssetSideEdspayUtil assetSideEdspayUtil;
	@Resource
	AfTaskUserService afTaskUserService;

    @Override
    public Map<String, Object> repay(LoanRepayBo bo, String bankPayType) {

	if (!BankPayChannel.KUAIJIE.getCode().equals(bankPayType)) {
	    lockRepay(bo.userId);
	}

	if (!bo.isAllRepay && !canRepay(bo.loanPeriodsDoList.get(0))) {
	    // 未出账时拦截按期还款
		unLockRepay(bo.userId);
	    throw new FanbeiException(FanbeiExceptionCode.LOAN_PERIOD_CAN_NOT_REPAY_ERROR);
	}

	Date now = new Date();
	String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
	if (StringUtil.equals("sysJob", bo.remoteIp)) {
	    name = Constants.BORROW_REPAYMENT_NAME_AUTO;
	}

	String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now, bankPayType);
	bo.tradeNo = tradeNo;
	bo.name = name;

	// 根据 还款金额 更新期数信息
	if (!bo.isAllRepay) { // 非提前结清
	    List<AfLoanPeriodsDo> loanPeriods = getLoanPeriodsIds(bo.loanId, bo.repaymentAmount);
	    bo.loanPeriodsIds.clear();
	    bo.loanPeriodsDoList.clear();
	    for (AfLoanPeriodsDo afLoanPeriodsDo : loanPeriods) {
		bo.loanPeriodsIds.add(afLoanPeriodsDo.getRid());
		bo.loanPeriodsDoList.add(afLoanPeriodsDo);
	    }
	}

	// 增加还款记录
	generateRepayRecords(bo);

	// 还款操作
	return doRepay(bo, bo.loanRepaymentDo, bankPayType);

    }
	
	/**
	 * @Description:  增加还款记录
	 * @return  void
	 */
	private void generateRepayRecords(LoanRepayBo bo) {
    	Date now = new Date();
    	String tradeNo = bo.tradeNo;
    	String name = bo.name;
		
    	AfLoanRepaymentDo loanRepaymentDo = buildRepayment(BigDecimal.ZERO, bo.repaymentAmount, tradeNo, now, bo.actualAmount, bo.couponId, 
				bo.userCouponDto != null?bo.userCouponDto.getAmount():null, bo.rebateAmount, bo.loanId, bo.cardId, bo.outTradeNo, name, bo.userId,bo.repayType,bo.cardNo,bo.loanPeriodsDoList,bo.loanDo.getPrdType(),bo.isAllRepay);
    	
    	afLoanRepaymentDao.saveRecord(loanRepaymentDo);
		
    	bo.loanRepaymentDo = loanRepaymentDo;
		
		logger.info("Repay.add repayment finish,name="+ name +",tradeNo="+tradeNo+",borrowRepayment="+ JSON.toJSONString(loanRepaymentDo));
    }
    
	private AfLoanRepaymentDo buildRepayment(BigDecimal jfbAmount, BigDecimal repaymentAmount, String repayNo, Date gmtCreate, BigDecimal actualAmount, 
			Long userCouponId, BigDecimal couponAmount, BigDecimal rebateAmount, Long loanId, Long cardId, String payTradeNo, String name, Long userId, 
			String repayType, String cardNo, List<AfLoanPeriodsDo> loanPeriodsDoList, String prdType, boolean isAllRepay) {

		AfLoanRepaymentDo loanRepay = new AfLoanRepaymentDo();
		loanRepay.setUserId(userId);
		loanRepay.setLoanId(loanId);
		loanRepay.setName(name);
		loanRepay.setRepayAmount(repaymentAmount);
		loanRepay.setActualAmount(actualAmount);
		loanRepay.setStatus(AfLoanRepaymentStatus.APPLY.name());
		loanRepay.setTradeNo(repayNo);
		loanRepay.setTradeNoOut(payTradeNo);
		loanRepay.setUserCouponId(userCouponId);
		loanRepay.setCouponAmount(couponAmount);
		loanRepay.setUserAmount(rebateAmount);
		
		if(isAllRepay) {	// 提前还款
			loanRepay.setPreRepayStatus("Y");
		} else {
			loanRepay.setPreRepayStatus("N");
		}
		
		String repayPeriods = "";
		for (int i = 0; i < loanPeriodsDoList.size(); i++) {
			if(i == loanPeriodsDoList.size()-1){
				repayPeriods += loanPeriodsDoList.get(i).getRid();
			} else {
				repayPeriods += loanPeriodsDoList.get(i).getRid()+",";
			}
		}

		loanRepay.setRepayPeriods(repayPeriods);
		
		loanRepay.setPrdType(prdType);
		loanRepay.setGmtCreate(gmtCreate);
		loanRepay.setCardNo("");
		if (cardId == -2) {
			loanRepay.setCardName(Constants.DEFAULT_USER_ACCOUNT);
		} else if (cardId == -1) {
			loanRepay.setCardName(Constants.DEFAULT_WX_PAY_NAME);
		} else if (cardId == -3) {
			loanRepay.setCardName(Constants.DEFAULT_ZFB_PAY_NAME);
		} else if (cardId == -4) {
			if (cardNo == null) {
				cardNo = "";
			}
			loanRepay.setCardNo(cardNo);
			if ("alipay".equals(repayType)) {
				loanRepay.setCardName("支付宝");
			} else if ("bank".equals(repayType)) {
				loanRepay.setCardName("银行卡");
			} else {
				loanRepay.setCardName("线下还款");
			}
		} else {
			AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
			if(bank == null) throw new FanbeiException(FanbeiExceptionCode.USER_BANKCARD_NOT_EXIST_ERROR);
			loanRepay.setCardNo(bank.getCardNumber());
			loanRepay.setCardName(bank.getBankName());
		}

		return loanRepay;
	}
	
	
    /**
     * @Description:  还款操作
     * @return  void
     */
    private Map<String, Object> doRepay(LoanRepayBo bo, AfLoanRepaymentDo repayment, String bankChannel) {
	Map<String, Object> resultMap = new HashMap<String, Object>();
	if (bo.cardId > 0) {// 银行卡支付
	    AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
	    KuaijieLoanBo bizObject = new KuaijieLoanBo(repayment, bo);
	    if (BankPayChannel.KUAIJIE.getCode().equals(bankChannel)) {// 快捷支付
		repayment.setStatus(RepaymentStatus.SMS.getCode());
		resultMap = sendKuaiJieSms(bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(),
			bo.userDo.getIdNumber(), JSON.toJSONString(bizObject), "afLoanRepaymentService", Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
	    } else {// 代扣
		resultMap = doUpsPay(bankChannel, bank.getRid(), bo.tradeNo, bo.actualAmount, bo.userId, bo.userDo.getRealName(),
			bo.userDo.getIdNumber(), "", JSON.toJSONString(bizObject), Constants.DEFAULT_PAY_PURPOSE, bo.name, PayOrderSource.REPAY_LOAN.getCode());
	    }
	} else if (bo.cardId == -2) {// 余额支付
	    dealRepaymentSucess(bo.tradeNo, "");
	    resultMap = getResultMap(bo, null);
	}

	return resultMap;
    }

    @Override
    protected void daikouConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {
	KuaijieLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieLoanBo.class);
	if (kuaijieLoanBo.getRepayment() != null) {
	    changLoanRepaymentStatus(null, AfLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
	}
    }

    @Override
    protected void kuaijieConfirmPre(String payTradeNo, String bankChannel, String payBizObject) {

    }
    
    @Override
    protected void quickPaySendSmmSuccess(String payTradeNo, String payBizObject, UpsCollectRespBo respBo)
    {
	KuaijieLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieLoanBo.class);
	if (kuaijieLoanBo.getRepayment() != null) {
	    changLoanRepaymentStatus(null, AfLoanRepaymentStatus.SMS.name(), kuaijieLoanBo.getRepayment().getRid());
	}
    }

    @Override
    protected Map<String, Object> upsPaySuccess(String payTradeNo, String bankChannel, String payBizObject, UpsCollectRespBo respBo, String cardNo) {
	KuaijieLoanBo kuaijieLoanBo = JSON.parseObject(payBizObject, KuaijieLoanBo.class);
	if (kuaijieLoanBo.getRepayment() != null) {
	    changLoanRepaymentStatus(null, AfLoanRepaymentStatus.PROCESSING.name(), kuaijieLoanBo.getRepayment().getRid());
	}
	return getResultMap(kuaijieLoanBo.getBo(),respBo);
    }

    @Override
    protected void roolbackBizData(String payTradeNo, String payBizObject, String errorMsg, UpsCollectRespBo respBo) {
	if (StringUtils.isNotBlank(payBizObject)) {
	    // 处理业务数据
	    if (StringUtil.isNotBlank(respBo.getRespCode())) {
		dealRepaymentFail(payTradeNo, respBo.getTradeNo(), true, errorMsg);
	    } else {
		dealRepaymentFail(payTradeNo, respBo.getTradeNo(), false, "");
	    }
	} else {
	    // 未获取到缓存数据，支付订单过期
	    throw new FanbeiException(FanbeiExceptionCode.UPS_CACHE_EXPIRE);
	}
    }

    private Map<String, Object> getResultMap(LoanRepayBo bo, UpsCollectRespBo respBo)
    {
	Map<String, Object> data = Maps.newHashMap();
	data.put("rid", bo.loanId);
	data.put("amount", bo.repaymentAmount.setScale(2, RoundingMode.HALF_UP));
	data.put("gmtCreate", new Date());
	data.put("status", AfLoanRepaymentStatus.SUCC.name());
	if(bo.userCouponDto != null) {
		data.put("couponAmount", bo.userCouponDto.getAmount());
	}
	if(bo.rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
		data.put("userAmount", bo.rebateAmount);
	}
	data.put("actualAmount", bo.actualAmount);
	data.put("cardName", bo.cardName);
	data.put("cardNumber", bo.cardNo);
	data.put("repayNo", bo.tradeNo);
	data.put("jfbAmount", BigDecimal.ZERO);
	if(respBo!=null)
	{
	    data.put("resp", respBo);
	    data.put("outTradeNo", respBo.getTradeNo());
	}

	return data;
    }

   	/**
   	 * @Description: 还款状态修改
   	 * @return  long
   	 */
   	private long changLoanRepaymentStatus(String outTradeNo, String status, Long rid) {
   		AfLoanRepaymentDo loanRepay = new AfLoanRepaymentDo();
   		loanRepay.setStatus(status);
   		loanRepay.setTradeNoOut(outTradeNo);
   		loanRepay.setRid(rid);
   		loanRepay.setGmtModified(new Date());
        return afLoanRepaymentDao.updateById(loanRepay);
    }
    
   	
    /**
     * 还款失败后调用
     */
    @Override
	public void dealRepaymentFail(String tradeNo, String outTradeNo,boolean isNeedMsgNotice,String errorMsg) {
		final AfLoanRepaymentDo loanRepaymentDo = afLoanRepaymentDao.getRepayByTradeNo(tradeNo);
        logger.info("dealRepaymentFail process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo 
        		+ ",isNeedMsgNotice=" + isNeedMsgNotice + ",errorMsg=" + errorMsg 
        		+ ",borrowRepayment=" + JSON.toJSONString(loanRepaymentDo));
        
        if ((loanRepaymentDo != null && AfLoanRepaymentStatus.SUCC.name().equals(loanRepaymentDo.getStatus()) )) { // 检查交易流水 对应记录数据库中是否已经处理
            return;
        }
        
        if(loanRepaymentDo != null) {
        	changLoanRepaymentStatus(outTradeNo, AfLoanRepaymentStatus.FAIL.name(), loanRepaymentDo.getRid());
		}
        
        // 解锁还款
     	unLockRepay(loanRepaymentDo.getUserId());
        
		if(isNeedMsgNotice){
			//用户信息及当日还款失败次数校验
			int errorTimes = 0;
			AfUserDo afUserDo = afUserService.getUserById(loanRepaymentDo.getUserId());
			//如果是代扣，不校验次数
			String payType = loanRepaymentDo.getName();
			//模版数据map处理
			Map<String,String> replaceMapData = new HashMap<String, String>();
			replaceMapData.put("errorMsg", errorMsg);
			//还款失败短信通知
			boolean isCashOverdue = false;
			if(StringUtil.isNotBlank(payType)&&payType.indexOf("代扣")>-1){
				AfLoanPeriodsDo afLoanPeriodsDo = afLoanPeriodsService.getOneByLoanId(loanRepaymentDo.getLoanId());
				//判断是否逾期，逾期不发短信
				try{
					if (StringUtils.equals("Y",afLoanPeriodsDo.getOverdueStatus())) {
						isCashOverdue = true;
					}
				}catch(Exception ex){
					logger.info("dealRepaymentFalse isCashOverdue error", ex);
				}
				if(isCashOverdue){
					logger.info("loanCash overdue withhold false orverdue,mobile=" + afUserDo.getMobile() + "errorMsg:" + errorMsg);
				}else{
					smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
				}
			}else{
				errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(loanRepaymentDo.getUserId());
				smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_FAIL.getCode());
				String title = "本次还款支付失败";
				String content = "非常遗憾，本次还款失败：&errorMsg，您可更换银行卡或采用其他还款方式。";
				content = content.replace("&errorMsg",errorMsg);
				pushService.pushUtil(title,content,afUserDo.getMobile());
			}
		}
        
	}
    
    
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
		final AfLoanRepaymentDo repaymentDo = afLoanRepaymentDao.getRepayByTradeNo(tradeNo);
        dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo,null,null,null);
    }
    
    
	/**
	 * 还款成功后调用
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfLoanRepaymentDo repaymentDo, String repayChannel, Long collectionRepaymentId, final List<HashMap> periodsList) {
    	try {
    		lock(tradeNo);
    		
            logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) );
            
            preCheck(repaymentDo, tradeNo);
			repaymentDo.setRepayChannel(repayChannel);	// 还款渠道
            final LoanRepayDealBo LoanRepayDealBo = new LoanRepayDealBo();
            LoanRepayDealBo.curTradeNo = tradeNo;
            LoanRepayDealBo.curOutTradeNo = outTradeNo;
            LoanRepayDealBo.repaymentDo = repaymentDo;
            
            long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus status) {
                    try {
                    	dealLoanRepay(LoanRepayDealBo, repaymentDo,periodsList);
                		
                    	// 最后一期还完后， 修改loan状态FINSH
                    	dealLoanStatus(LoanRepayDealBo);
                    	
                		dealSum(LoanRepayDealBo);
                		
                        dealCouponAndRebate(LoanRepayDealBo, repaymentDo);
                        
                        doAccountLog(LoanRepayDealBo);
                        
                        return 1L;
                    } catch (Exception e) {
                        status.setRollbackOnly();
                        logger.info("dealRepaymentSucess error", e);
                        throw e;
                    }
                }

            });

            if (resultValue == 1L) {

            	nofityRisk(LoanRepayDealBo);

				notifyUserBySms(LoanRepayDealBo);
				if (collectionRepaymentId != null){
					repaymentDo.setRemark(String.valueOf(collectionRepaymentId));
				}
				cuiShouUtils.syncCuiShou(repaymentDo);
				try{
					kafkaSync.syncEvent(repaymentDo.getUserId(), KafkaConstants.SYNC_USER_BASIC_DATA,true);
					kafkaSync.syncEvent(repaymentDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
				}catch (Exception e){
					logger.info("消息同步失败:",e);
				}
            }
    		
    	}finally {
    		unLock(tradeNo);
    		
    		// 解锁还款
    		unLockRepay(repaymentDo.getUserId());
		}
    }
    

    /**
     * @Description: 检查是否已经处理
     * @return  void
     */
    private void preCheck(AfLoanRepaymentDo repaymentDo, String tradeNo) {
    	// 检查交易流水 对应记录数据库中是否已经处理
        if ( repaymentDo != null && AfLoanRepaymentStatus.SUCC.name().equals(repaymentDo.getStatus()) ) {
        	throw new FanbeiException("preCheck,repayment has been dealed!");
        }
        
        /* start 易宝支付侵入逻辑 */
        AfYibaoOrderDo afYibaoOrderDo = afYibaoOrderDao.getYiBaoOrderByOrderNo(tradeNo);
        if (afYibaoOrderDo != null) {
            if (afYibaoOrderDo.getStatus().intValue() == 1) {
            	throw new FanbeiException("preCheck,afYibaoOrderDo.status == 1,break!");
            } else {
                afYibaoOrderDao.updateYiBaoOrderStatus(afYibaoOrderDo.getId(), 1);
            }
        }
        /* end 易宝支付侵入逻辑 */
    }

	
    /**
	 * 需在事务管理块中调用此函数!
	 * @param loanRepayDealBo
	 * @param repaymentDo
	 */
	private void dealLoanRepay(LoanRepayDealBo loanRepayDealBo, AfLoanRepaymentDo repaymentDo,List<HashMap> periodsList) {
		if(repaymentDo == null) return;
		
		AfLoanDo loanDo = afLoanDao.getById(repaymentDo.getLoanId());
		loanRepayDealBo.curRepayAmoutStub = repaymentDo.getRepayAmount();
		loanRepayDealBo.curRebateAmount = repaymentDo.getUserAmount();
		loanRepayDealBo.curSumRebateAmount = loanRepayDealBo.curSumRebateAmount.add(repaymentDo.getUserAmount());
		loanRepayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		loanRepayDealBo.curSumRepayAmount = loanRepayDealBo.curSumRepayAmount.add(repaymentDo.getRepayAmount());
		loanRepayDealBo.curCardName = repaymentDo.getCardName();
		loanRepayDealBo.curCardNo = repaymentDo.getCardNo();
		loanRepayDealBo.curName = repaymentDo.getName();
		
		loanRepayDealBo.loanDo = loanDo;
		loanRepayDealBo.overdueDay = loanDo.getOverdueDays();
		loanRepayDealBo.loanNo = loanDo.getLoanNo();
		loanRepayDealBo.refId += repaymentDo.getLoanId();
		loanRepayDealBo.userId = loanDo.getUserId();
		
		List<AfLoanPeriodsDo> loanPeriodsDoList = new ArrayList<AfLoanPeriodsDo>();

		if(repaymentDo.getPreRepayStatus().equals("Y")) {	// 提前还款
			loanRepayDealBo.isAllRepay = true; 
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
			for (int i = 0; i < repayPeriodsIds.length; i++) {
				// 获取分期信息
				AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				if(loanPeriodsDo!=null){	// 提前还款,已出账的分期借款,还款金额=分期本金+手续费+利息（+逾期费）
					loanPeriodsDoList.add(loanPeriodsDo);
					BigDecimal repayAmount = BigDecimal.ZERO;
					if(canRepay(loanPeriodsDo)){
						BigDecimal reductionAmount = BigDecimal.ZERO;
						if (periodsList != null && periodsList.size() > 0){
							for (HashMap map:periodsList) {
								if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
									reductionAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount"))) );
								}
							}
						}
						dealLoanRepayOverdue(loanRepayDealBo, loanPeriodsDo, loanDo,reductionAmount);		//逾期费
						dealLoanRepayPoundage(loanRepayDealBo, loanPeriodsDo);		//手续费
						dealLoanRepayInterest(loanRepayDealBo, loanPeriodsDo);		//利息
						
						repayAmount = calculateRestAmount(loanPeriodsDo.getRid());
						loanPeriodsDo.setRepayAmount(loanPeriodsDo.getRepayAmount().add(repayAmount));
						
					}else{		// 提前还款,未出账的分期借款,还款金额=分期本金
						repayAmount = loanPeriodsDo.getAmount();
						loanPeriodsDo.setRepayAmount(repayAmount);
					}
					
					loanPeriodsDo.setPreRepayStatus("Y"); 	// 提前还款
					loanPeriodsDo.setRepayId(repaymentDo.getRid());
					loanPeriodsDo.setStatus(AfLoanPeriodStatus.FINISHED.name());
					loanPeriodsDo.setGmtLastRepay(new Date());
					loanPeriodsDo.setGmtModified(new Date());
					
					loanRepayDealBo.curRepayAmoutStub = BigDecimalUtil.subtract(loanRepayDealBo.curRepayAmoutStub, repayAmount);
				}
				afLoanPeriodsDao.updateById(loanPeriodsDo);
			}
		}else if(repaymentDo.getPreRepayStatus().equals("N")) {		// 按期还款（部分还款）
			loanRepayDealBo.isAllRepay = false;
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
			for (int i = 0; i < repayPeriodsIds.length; i++) {
				
				AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				if(loanPeriodsDo!=null){
					BigDecimal reductionAmount = BigDecimal.ZERO;
					if (periodsList != null && periodsList.size() > 0){
						for (HashMap map:periodsList) {
							if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
								reductionAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount"))));
							}
						}
					}
					loanPeriodsDoList.add(loanPeriodsDo);
					dealLoanRepayOverdue(loanRepayDealBo, loanPeriodsDo, loanDo,reductionAmount);		//逾期费
					dealLoanRepayPoundage(loanRepayDealBo, loanPeriodsDo);		//手续费
					dealLoanRepayInterest(loanRepayDealBo, loanPeriodsDo);		//利息
					dealLoanRepayIfFinish(loanRepayDealBo, repaymentDo, loanPeriodsDo,reductionAmount);	//修改借款分期状态
				}
				afLoanPeriodsDao.updateById(loanPeriodsDo);
			}
		}
		
		loanRepayDealBo.loanPeriodsDoList = loanPeriodsDoList;
		
        changLoanRepaymentStatus(loanRepayDealBo.curOutTradeNo, AfLoanRepaymentStatus.SUCC.name(), repaymentDo.getRid());
        
	}
	
	private void dealLoanStatus(LoanRepayDealBo loanRepayDealBo) {
		int nper = loanRepayDealBo.loanPeriodsDoList.size();
		AfLoanPeriodsDo loanPeriodsDo = loanRepayDealBo.loanPeriodsDoList.get(nper-1);
		if(loanPeriodsDo.getNper() == loanRepayDealBo.loanDo.getPeriods() && AfLoanPeriodStatus.FINISHED.name().equals(loanPeriodsDo.getStatus())) {
			// 最后一期结清， 修改loan状态FINISHED
			AfLoanDo loanDo = new AfLoanDo();
			loanDo.setRid(loanRepayDealBo.loanDo.getRid());
			loanDo.setStatus(AfLoanStatus.FINISHED.name());
			loanDo.setGmtModified(new Date());
			loanDo.setGmtFinish(new Date());
			afLoanDao.updateById(loanDo);
			try {
				boolean isBefore = DateUtil.isBefore(new Date(),DateUtil.addDays(loanPeriodsDo.getGmtPlanRepay(), -1) );
				if (isBefore) {
					if (assetSideEdspayUtil.isPush(loanRepayDealBo.loanDo)) {
						List<ModifiedBorrowInfoVo> modifiedLoanInfo = assetSideEdspayUtil.buildModifiedInfo(loanRepayDealBo.loanDo,1);
						boolean result = assetSideEdspayUtil.transModifiedBorrowInfo(modifiedLoanInfo,Constants.ASSET_SIDE_EDSPAY_FLAG, Constants.ASSET_SIDE_FANBEI_FLAG);
						if (result) {
							logger.info("trans modified loan Info success,loanId="+loanRepayDealBo.loanDo.getRid());
						}else{
							assetSideEdspayUtil.transFailRecord(loanRepayDealBo.loanDo, modifiedLoanInfo);
						}
					}
				}
			} catch (Exception e) {
				logger.error("preFinishNotifyEds error="+e);
			}
			
			loanRepayDealBo.loanDo.setStatus(AfLoanStatus.FINISHED.name());
			
			afUserAccountSenceService.syncLoanUsedAmount(loanPeriodsDo.getUserId(), SceneType.valueOf(loanPeriodsDo.getPrdType()), loanRepayDealBo.loanDo.getAmount().negate());
		}
	}
	
	private void dealSum(LoanRepayDealBo LoanRepayDealBo){
		
		for (AfLoanPeriodsDo loanPeriodsDo : LoanRepayDealBo.loanPeriodsDoList) {
			
			LoanRepayDealBo.sumLoanAmount = LoanRepayDealBo.sumLoanAmount.add(loanPeriodsDo.getAmount());	// 借款本金
			LoanRepayDealBo.sumRepaidAmount = LoanRepayDealBo.sumRepaidAmount.add(loanPeriodsDo.getRepayAmount());	// 还款总额
			LoanRepayDealBo.sumInterest = LoanRepayDealBo.sumInterest.add(loanPeriodsDo.getInterestFee()).add(loanPeriodsDo.getRepaidInterestFee());	// 利息总额
			LoanRepayDealBo.sumPoundage = LoanRepayDealBo.sumPoundage.add(loanPeriodsDo.getServiceFee()).add(loanPeriodsDo.getRepaidServiceFee());	// 手续费总额
			LoanRepayDealBo.sumOverdueAmount = LoanRepayDealBo.sumOverdueAmount.add(loanPeriodsDo.getOverdueAmount()).add(loanPeriodsDo.getRepaidOverdueAmount());	// 逾期费总额
			LoanRepayDealBo.sumIncome = LoanRepayDealBo.sumIncome.add(LoanRepayDealBo.sumPoundage).add(LoanRepayDealBo.sumOverdueAmount).add(LoanRepayDealBo.sumInterest);// 总收入
			LoanRepayDealBo.sumAmount = LoanRepayDealBo.sumLoanAmount.add(LoanRepayDealBo.sumIncome);	// 借款产生总额
		}
	}
	
    
	/**
     * 处理优惠卷 和 账户余额
     * @param LoanRepayDealBo
     */
    private void dealCouponAndRebate(LoanRepayDealBo LoanRepayDealBo, AfLoanRepaymentDo repaymentDo) {
    	
    	AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(LoanRepayDealBo.userId);
    	
    	if(LoanRepayDealBo.curSumRebateAmount != null && LoanRepayDealBo.curSumRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
        	accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(LoanRepayDealBo.curSumRebateAmount));
        	afUserAccountDao.updateOriginalUserAccount(accountInfo);

			// add by luoxiao for 边逛边赚，增加零钱明细
			afTaskUserService.addTaskUser(accountInfo.getUserId(), UserAccountLogType.REPAYMENT.getName(), LoanRepayDealBo.curSumRebateAmount.multiply(new BigDecimal(-1)));
			// end by luoxiao
    	}
    	
    	if(LoanRepayDealBo.curUserCouponId != null && LoanRepayDealBo.curUserCouponId > 0) {
            afUserCouponDao.updateUserCouponSatusUsedById(LoanRepayDealBo.curUserCouponId);// 优惠券设置已使用
    	}
    	
    	// 解锁还款
		unLockRepay(LoanRepayDealBo.userId);
    }
    
    /**
     * @Description: 增加日志
     * @return  void
     */
    private void doAccountLog(LoanRepayDealBo LoanRepayDealBo) {
    	AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
    	accountLog.setAmount(LoanRepayDealBo.curSumRepayAmount);
		accountLog.setUserId(LoanRepayDealBo.userId);
		accountLog.setRefId(LoanRepayDealBo.refId);
		accountLog.setType(LoanRepayDealBo.loanDo.getPrdType());
        afUserAccountLogDao.addUserAccountLog(accountLog);	//增加日志
    }

	
	/**
	 * @Description: 分期记录逾期费处理
	 * @return  void
	 */
	private void dealLoanRepayOverdue(LoanRepayDealBo LoanRepayDealBo, AfLoanPeriodsDo loanPeriodsDo, AfLoanDo loanDo,BigDecimal reductionAmount) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;
		
		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = loanPeriodsDo.getOverdueAmount();
		
        if (repayAmount.compareTo(overdueAmount) > 0) {
        	loanPeriodsDo.setRepaidOverdueAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidOverdueAmount(), overdueAmount).subtract(reductionAmount));
        	loanPeriodsDo.setOverdueAmount(BigDecimal.ZERO);
        	LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
        	LoanRepayDealBo.curRepayOverdue = overdueAmount;
        	
        	if(loanDo.getOverdueAmount().compareTo(BigDecimal.ZERO)>0){
        		loanDo.setOverdueAmount(BigDecimal.ZERO);
        		afLoanDao.updateById(loanDo);
        	}
        } else {
        	loanPeriodsDo.setRepaidOverdueAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidOverdueAmount(), repayAmount).subtract(reductionAmount));
        	loanPeriodsDo.setOverdueAmount(overdueAmount.subtract(repayAmount).add(reductionAmount));
        	LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
        	LoanRepayDealBo.curRepayOverdue = repayAmount;
        	
        	if(loanDo.getOverdueAmount().compareTo(BigDecimal.ZERO)>0){
        		loanDo.setOverdueAmount(loanDo.getOverdueAmount().subtract(repayAmount));
        		afLoanDao.updateById(loanDo);
        	}
        }
	}
	
	/**
	 * @Description: 分期记录手续费处理
	 * @return  void
	 */
	private void dealLoanRepayPoundage(LoanRepayDealBo LoanRepayDealBo, AfLoanPeriodsDo loanPeriodsDo) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;
		
		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal poundageAmount = loanPeriodsDo.getServiceFee();
		
        if (repayAmount.compareTo(poundageAmount) > 0) {
        	loanPeriodsDo.setRepaidServiceFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidServiceFee(), poundageAmount));
        	loanPeriodsDo.setServiceFee(BigDecimal.ZERO);
            LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(poundageAmount);
            LoanRepayDealBo.curRepayService = poundageAmount;
        } else {
        	loanPeriodsDo.setRepaidServiceFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidServiceFee(), repayAmount));
        	loanPeriodsDo.setServiceFee(poundageAmount.subtract(repayAmount));
        	LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
        	LoanRepayDealBo.curRepayService = repayAmount;
        }
	}
	
	/**
	 * @Description: 分期记录利息处理
	 * @return  void
	 */
	private void dealLoanRepayInterest(LoanRepayDealBo LoanRepayDealBo, AfLoanPeriodsDo loanPeriodsDo) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;
		
		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal rateAmount = loanPeriodsDo.getInterestFee();
		
        if (repayAmount.compareTo(rateAmount) > 0) {
        	loanPeriodsDo.setRepaidInterestFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidInterestFee(), rateAmount));
        	loanPeriodsDo.setInterestFee(BigDecimal.ZERO);
            LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(rateAmount);
            LoanRepayDealBo.curRepayInterest = rateAmount;
        } else {
        	loanPeriodsDo.setRepaidInterestFee(BigDecimalUtil.add(loanPeriodsDo.getRepaidInterestFee(), repayAmount));
        	loanPeriodsDo.setInterestFee(rateAmount.subtract(repayAmount));
        	LoanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
        	LoanRepayDealBo.curRepayInterest = repayAmount;
        }
	}
	
	/**
	 * @Description: 分期记录 完成处理
	 * @return  void
	 */
	private void dealLoanRepayIfFinish(LoanRepayDealBo loanRepayDealBo, AfLoanRepaymentDo repaymentDo, AfLoanPeriodsDo loanPeriodsDo,BigDecimal reductionAmount) {
		
		// 所有需还金额
		BigDecimal sumAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(), 
				loanPeriodsDo.getOverdueAmount(), loanPeriodsDo.getRepaidOverdueAmount(),
				loanPeriodsDo.getInterestFee(), loanPeriodsDo.getRepaidInterestFee(),
				loanPeriodsDo.getServiceFee(), loanPeriodsDo.getRepaidServiceFee());
		
		// 当笔还款处理的逾期费手续费利息
		BigDecimal curRepayOSI = BigDecimalUtil.add(loanRepayDealBo.curRepayOverdue,loanRepayDealBo.curRepayService,loanRepayDealBo.curRepayInterest);
		
		// 本期需还金额
		BigDecimal calculateRestAmount = calculateRestAmount(loanPeriodsDo.getRid());
		BigDecimal repayAmount = loanRepayDealBo.curRepayAmoutStub;

		if(repaymentDo.getRepayAmount().compareTo(calculateRestAmount) >= 0){	// 针对多期已出账 的部分还款
			if(repayAmount.compareTo(calculateRestAmount.subtract(curRepayOSI)) >= 0){
				loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(loanPeriodsDo.getRepayAmount(),calculateRestAmount));
				loanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(calculateRestAmount.subtract(curRepayOSI));
			}else {
				loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(loanPeriodsDo.getRepayAmount(),repayAmount.add(curRepayOSI)));
				loanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
			}
		}else{
			if(loanPeriodsDo.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
				loanPeriodsDo.setRepayAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidInterestFee(),
						loanPeriodsDo.getRepaidOverdueAmount(),loanPeriodsDo.getRepaidServiceFee(),repayAmount));
			}else {
				loanPeriodsDo.setRepayAmount(loanPeriodsDo.getRepayAmount().add(repaymentDo.getRepayAmount()));
			}
			loanRepayDealBo.curRepayAmoutStub = BigDecimal.ZERO;
		}
		
		loanRepayDealBo.curRepayOverdue = BigDecimal.ZERO;
		loanRepayDealBo.curRepayService = BigDecimal.ZERO;
		loanRepayDealBo.curRepayInterest = BigDecimal.ZERO;
		
		// 所有已还金额
		BigDecimal allRepayAmount = loanPeriodsDo.getRepayAmount();
		
		BigDecimal minus = allRepayAmount.subtract(sumAmount); //容许多还一块钱，兼容离线还款 场景
		logger.info("dealRepaymentSucess process dealLoanRepayIfFinish allRepayAmount="+allRepayAmount+",minus="+minus+",loanPeriodsDo="+ JSONObject.toJSONString(loanPeriodsDo)+",repaymentDo="+JSONObject.toJSONString(repaymentDo)+",loanRepayDealBo="+JSONObject.toJSONString(loanRepayDealBo));
		if (minus.compareTo(BigDecimal.ZERO) >= 0 && minus.compareTo(BigDecimal.ONE) <= 0) {
			loanPeriodsDo.setStatus(AfLoanPeriodStatus.FINISHED.name());
        } else if (minus.compareTo(BigDecimal.ZERO) < 0) {	// 部分还款
        	loanPeriodsDo.setStatus(AfLoanPeriodStatus.PART_REPAY.name());
        }
		loanPeriodsDo.setRepayId(repaymentDo.getRid());
		loanPeriodsDo.setGmtLastRepay(new Date());
		loanPeriodsDo.setGmtModified(new Date());
	}

	
   	
	
	/**
     * 线下还款
     * 
     * @param restAmount 
     */
	@Override
	public void offlineRepay(AfLoanDo loanDo, String loanNo, 
				String repayType, String repayAmount,
				String restAmount, String outTradeNo, String isBalance,String repayCardNum,String repayChannel,String isAdmin,boolean isAllRepay,Long repaymentId,List<HashMap> periodsList) {
		
		LoanRepayBo bo = buildLoanRepayBo(loanDo, loanNo, repayType, repayAmount, restAmount, outTradeNo, isBalance, repayCardNum, repayChannel, isAdmin,repaymentId,periodsList);
		// TODO 分期信息
		if (isAllRepay){//提前结清
			bo.isAllRepay = true;
			List<AfLoanPeriodsDo> loanPeriodsDoList = afLoanPeriodsDao.getNoRepayListByLoanId(loanDo.getRid());
			bo.loanPeriodsDoList = loanPeriodsDoList;
		}else {//按期还款
			bo.isAllRepay = false;
			List<AfLoanPeriodsDo> loanPeriodsDoList = getLoanPeriodsIds(bo.loanId, bo.repaymentAmount);
			bo.loanPeriodsDoList = loanPeriodsDoList;
		}
		checkOfflineRepayment(bo, repayAmount, outTradeNo);

		generateRepayRecords(bo);

		CuiShouUtils.setAfLoanRepaymentDo(bo.loanRepaymentDo);

		dealRepaymentSucess(bo.tradeNo, null, bo.loanRepaymentDo,repayChannel,bo.collectionRepaymentId,bo.periodsList);

	}

	private LoanRepayBo buildLoanRepayBo(AfLoanDo loanDo, String loanNo, 
			String repayType, String repayAmount,
			String restAmount, String outTradeNo, String isBalance,String repayCardNum,String repayChannel,String isAdmin,Long repaymentId,List<HashMap> periodsList){
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = loanDo.getUserId();
		bo.userDo = afUserAccountDao.getUserAccountInfoByUserId(bo.userId);
		
		bo.cardId = (long) -4;
		bo.repaymentAmount = NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO);
		bo.actualAmount =  bo.repaymentAmount;
		bo.loanId = loanDo.getRid();
		bo.loanDo = loanDo;
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		if (isAdmin != null && "Y".equals(isAdmin)){
			bo.name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;//财务线下打款
		}else {
			bo.name = Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE;//催收线下打款
		}
		bo.outTradeNo = outTradeNo;
		bo.cardNo = repayCardNum;
		bo.repayType = repayType;
		bo.collectionRepaymentId = repaymentId;
		if (periodsList != null && periodsList.size() > 0){
			bo.periodsList = periodsList;
			BigDecimal reductionAmount = BigDecimal.ZERO;
			BigDecimal actualAmount = BigDecimal.ZERO;
			for (HashMap map:periodsList) {
				reductionAmount = reductionAmount.add(BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount")))));
				actualAmount = actualAmount.add(BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("repayAmount")))));
			}
			bo.repaymentAmount = actualAmount.add(reductionAmount);
			bo.reductionAmount = reductionAmount;
			bo.actualAmount = actualAmount;
		}
		return bo;
	}
	
	private void checkOfflineRepayment(LoanRepayBo bo, String offlineRepayAmount ,String outTradeNo) {
		if(afLoanRepaymentDao.getLoanRepaymentByTradeNo(outTradeNo) != null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
		}
		calculateOfflineRestAmount(bo);
		/*BigDecimal restAmount=BigDecimal.ZERO;
		if(bo.isAllRepay){		// 提前结清
			restAmount = calculateAllRestAmount(bo.loanId);
		}else{		// 按期还款
			restAmount = calculateBillRestAmount(bo.loanId);
		}
		BigDecimal offlineRepayAmountYuan = NumberUtil.objToBigDecimalDivideOnehundredDefault(offlineRepayAmount, BigDecimal.ZERO);
		// 因为有用户会多还几分钱，所以加个安全金额限制，当还款金额 > 用户应还金额+1元 时，返回错误
		if (offlineRepayAmountYuan.compareTo(restAmount.add(BigDecimal.ONE)) > 0) {
			logger.warn("CheckOfflineRepayment error, offlineRepayAmount="+ offlineRepayAmount +", restAmount="+ restAmount);
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
		}*/
	}


	private void notifyUserBySms(LoanRepayDealBo repayDealBo) {
		logger.info("notifyUserBySms info begin,sumAmount="+repayDealBo.sumAmount+",curSumRepayAmount="+repayDealBo.curSumRepayAmount+",sumRepaidAmount="+repayDealBo.sumRepaidAmount);
		try {
			AfUserDo afUserDo = afUserService.getUserById(repayDealBo.userId);
			sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(),repayDealBo );
		} catch (Exception e) {
			logger.error("Sms notify user error, userId:" + repayDealBo.userId + ",nowRepayAmount:" + repayDealBo.curSumRepayAmount + ",notRepayMoney" + repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount), e);
		}
	}

	/**
	 * 代扣现金贷还款成功短信发送
	 * @param mobile
	 * @param nowRepayAmount
	 */
	private boolean sendRepaymentBorrowCashWithHold(String mobile,BigDecimal nowRepayAmount){
		//模版数据map处理
		Map<String,String> replaceMapData = new HashMap<String, String>();
		replaceMapData.put("nowRepayAmountStr", nowRepayAmount+"");
		return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_SUCCESS.getCode());
	}


	/**
	 * 用户手动现金贷还款成功短信发送
	 * @param mobile
	 * @param
	 */
	private boolean sendRepaymentBorrowCashWarnMsg(String mobile,LoanRepayDealBo repayDealBo){
		//模版数据map处理
		Map<String,String> replaceMapData = new HashMap<String, String>();
		BigDecimal repayMoney = repayDealBo.curSumRepayAmount;
		BigDecimal notRepayMoney = BigDecimal.ZERO;
		replaceMapData.put("repayMoney", repayMoney+"");
		if(repayDealBo.isAllRepay){
			notRepayMoney = repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount.add(repayDealBo.sumPoundage).add(repayDealBo.sumInterest));
			replaceMapData.put("remainAmount", notRepayMoney+"");
			return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS.getCode());
		} else if (notRepayMoney==null || notRepayMoney.compareTo(BigDecimal.ZERO)<=0) {
			notRepayMoney = repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount);
			replaceMapData.put("remainAmount", notRepayMoney+"");
			String title = "恭喜您，借款已还清！";
			String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
			content = content.replace("&repayMoney",repayMoney.toString());
			pushService.pushUtil(title,content,mobile);
			return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS.getCode());
		} else {
			notRepayMoney = repayDealBo.sumAmount.subtract(repayDealBo.sumRepaidAmount);
			replaceMapData.put("remainAmount", notRepayMoney+"");
			String title = "部分还款成功！";
			String content = "本次成功还款&repayMoney元，剩余待还金额&remainAmount元，请继续保持良好的信用习惯哦。";
			content = content.replace("&repayMoney",repayMoney.toString());
			content = content.replace("&remainAmount",notRepayMoney.toString());
			pushService.pushUtil(title,content,mobile);
			return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS_REMAIN.getCode());
		}
	}
	
    private void nofityRisk(LoanRepayDealBo LoanRepayDealBo) {
    	String cardNo = LoanRepayDealBo.curCardNo;
    	cardNo = StringUtils.isNotBlank(cardNo)?cardNo:String.valueOf(System.currentTimeMillis());
    	
        /**------------------------------------fmai风控提额begin------------------------------------------------*/
        try {
            if ( AfLoanStatus.FINISHED.name().equals(LoanRepayDealBo.loanDo.getStatus()) ) {
            	String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                int overdueCount = 0;
                if (StringUtil.equals("Y", LoanRepayDealBo.loanDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                riskUtil.raiseQuota(LoanRepayDealBo.userId.toString(), 
                			LoanRepayDealBo.loanNo, SceneType.BLD_LOAN.getCode(), riskOrderNo, 
                			LoanRepayDealBo.sumLoanAmount,
                			LoanRepayDealBo.sumIncome, 
                			LoanRepayDealBo.overdueDay, overdueCount, LoanRepayDealBo.overdueDay, LoanRepayDealBo.loanDo.getPeriods());
            }
        } catch (Exception e) {
            logger.error("notifyRisk.raiseQuota error！", e);
        }
        /**------------------------------------fmai风控提额end--------------------------------------------------*/

        //会对逾期的借款还款，向催收平台同步还款信息
        if (DateUtil.compareDate(new Date(), LoanRepayDealBo.loanPeriodsDoList.get(0).getGmtPlanRepay()) ){
            try {
                CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(
                		LoanRepayDealBo.curTradeNo,
                		LoanRepayDealBo.loanNo,
                		LoanRepayDealBo.curCardNo,
                		LoanRepayDealBo.curCardName,
                        DateUtil.formatDateTime(new Date()),
                        LoanRepayDealBo.curOutTradeNo,
                        LoanRepayDealBo.curSumRepayAmount,
                        LoanRepayDealBo.sumAmount.subtract(LoanRepayDealBo.sumRepaidAmount).setScale(2, RoundingMode.HALF_UP), //未还的
                        LoanRepayDealBo.sumAmount.setScale(2, RoundingMode.HALF_UP),	
                        LoanRepayDealBo.sumOverdueAmount,
                		LoanRepayDealBo.sumRepaidAmount,
                		LoanRepayDealBo.sumInterest,false);
                logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
            } catch (Exception e) {
                logger.error("向催收平台同步还款信息失败", e);
            }
        }else{
			logger.info("collection consumerRepayment not push,borrowCashId="+LoanRepayDealBo.loanDo.getRid());
		}
    }
	
	
	// -----------------------------------------------------------------------------------------
	
	/**
	 * 锁住目标流水号的还款，防止重复回调
	 */
	private void lock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
        long count = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 30, TimeUnit.SECONDS);
        if (count != 1) {
            throw new FanbeiException(FanbeiExceptionCode.UPS_REPEAT_NOTIFY);
        }
	}	
	
	private void unLock(String tradeNo) {
		String key = tradeNo + "_success_legalRepay";
		redisTemplate.delete(key);
	}
	
	
	/**
	 * 锁住还款
	 */
	private void lockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
        long count = redisTemplate.opsForValue().increment(key, 1);
        redisTemplate.expire(key, 300, TimeUnit.SECONDS);
        if (count != 1) {
            throw new FanbeiException(FanbeiExceptionCode.LOAN_REPAY_PROCESS_ERROR);
        }
	}	
	
	private void unLockRepay(Long userId) {
		String key = userId + "_success_loanRepay";
		redisTemplate.delete(key);
	}
	
	public static class LoanRepayBo{
		public Long userId;
		
		/* request字段 */
		public BigDecimal repaymentAmount = BigDecimal.ZERO;	// 还款金额
		public BigDecimal actualAmount = BigDecimal.ZERO; 
		public BigDecimal rebateAmount = BigDecimal.ZERO; //可选字段
		public BigDecimal reductionAmount = BigDecimal.ZERO; //可选字段
		public List<HashMap> periodsList;
		public String payPwd;
		public Long cardId;
		public Long couponId;			//可选字段
		public Long loanId;			
		public List<Long> loanPeriodsIds = new ArrayList<Long>();
		public Long collectionRepaymentId;
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfLoanRepaymentDo loanRepaymentDo;
		public AfLoanDo loanDo;
		public List<AfLoanPeriodsDo> loanPeriodsDoList = new ArrayList<AfLoanPeriodsDo>();	//借款分期
		public AfUserCouponDto userCouponDto; 	//可选字段
		public AfUserAccountDo userDo;
		public String remoteIp;
		public String name;
		public String repayType;
		public boolean isAllRepay = false;	// 是否是提前还款（默认false为按期还款）
		/* biz 业务处理字段 */
		
		/* Response字段 */
		public String cardName;		//交易卡名称
		public String cardNo;		//交易卡号
		public String outTradeNo; 	//资金方放交易流水号
		public String tradeNo;		//我方交易流水号
		/* Response字段 */
		
		/* 错误码区域 */
		public Exception e;
		
	}
	
	public static class LoanRepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curRebateAmount; 	//当前还款使用的账户余额
		BigDecimal curSumRebateAmount = BigDecimal.ZERO;//当前还款使用的账户余额总额
		Long curUserCouponId;			//当前还款使用的还款优惠卷id
		BigDecimal curSumRepayAmount = BigDecimal.ZERO;	//当前还款总额
		String curCardNo;				//当前还款卡号
		String curCardName;				//当前还款卡别名
		String curName;					//当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;
		BigDecimal curRepayService; 	//当笔还款手续费
		BigDecimal curRepayOverdue; 	//当笔还款逾期费费
		BigDecimal curRepayInterest; 	//当笔还款手续费费
		
		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额（包含所有费用）
		BigDecimal sumLoanAmount = BigDecimal.ZERO;	//对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO;		//对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO;		//对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO;	//对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO;			//对应借款我司产生的总收入
		
		AfLoanDo loanDo;							//借款
		List<AfLoanPeriodsDo> loanPeriodsDoList;							//借款分期
		AfLoanRepaymentDo repaymentDo;
		long overdueDay = 0;							//对应借款的逾期天数
		String loanNo;								//借款流水号
    	String refId = "";								//还款的id串
    	Long userId ;									//目标用户id
    	boolean isAllRepay = false;	// 是否是提前还款（默认false为按期还款）
	}

	@Override
	public AfLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId) {
		return afLoanRepaymentDao.getProcessLoanRepaymentByLoanId(loanId);
	}

	@Override
	public int getProcessLoanRepaymentNumByLoanId(Long loanId) {
		return afLoanRepaymentDao.getProcessLoanRepaymentNumByLoanId(loanId);
	}

	/**
     * 计算本期需还金额
     */
    @Override
	public BigDecimal calculateRestAmount(Long loanPeriodsId) {
		BigDecimal restAmount = BigDecimal.ZERO;
		AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsDao.getById(loanPeriodsId);
		
		if(loanPeriodsDo!=null){
			restAmount = BigDecimalUtil.add(restAmount,loanPeriodsDo.getAmount(),
					loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
					loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
					loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
					.subtract(loanPeriodsDo.getRepayAmount());
		}
		
		return restAmount;
	}

	/**
	 * 计算已出账需还金额
	 */
	@Override
	public BigDecimal calculateBillRestAmount(Long loanId) {
		BigDecimal allRestAmount = BigDecimal.ZERO;

		List<AfLoanPeriodsDo> noRepayList = afLoanPeriodsDao.getNoRepayListByLoanId(loanId);

		for (AfLoanPeriodsDo loanPeriodsDo : noRepayList) {
			if(canRepay(loanPeriodsDo)) { // 已出账
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount(),
						loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
						loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
						loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
						.subtract(loanPeriodsDo.getRepayAmount());
			}
		}

		return allRestAmount;
	}

    /**
     * 计算提前还款需还金额
     */
	public void calculateOfflineRestAmount(LoanRepayBo bo) {
		List<AfLoanPeriodsDo> noRepayList = afLoanPeriodsDao.getNoRepayListByLoanId(bo.loanId);
		List<HashMap> periodsList = bo.periodsList;
		for (AfLoanPeriodsDo loanPeriodsDo : noRepayList) {
			BigDecimal restAmount = BigDecimal.ZERO;
			if(canRepay(loanPeriodsDo)) { // 已出账
				restAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),
						loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
						loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
						loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
						.subtract(loanPeriodsDo.getRepayAmount());
			}else { // 未出账， 提前还款时不用还手续费和利息
				if (!bo.isAllRepay ){//判断是否为按期还款，按期还款不能提前还未出账账单
					for (HashMap map:bo.periodsList) {
						if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
						}
					}
				}
				restAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount());
			}
			if (Long.parseLong(String.valueOf(periodsList.get(periodsList.size()-1).get("id"))) != loanPeriodsDo.getRid()){
				for (HashMap map:bo.periodsList) {
					if (Long.parseLong(String.valueOf(map.get("id"))) == loanPeriodsDo.getRid()){
						BigDecimal repayAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("repayAmount"))) ).add(BigDecimal.valueOf(Double.parseDouble(String.valueOf(map.get("reductionAmount"))) ));
						if (repayAmount.compareTo(restAmount.add(BigDecimal.ONE)) > 0 || repayAmount.compareTo(restAmount.subtract(BigDecimal.ONE)) < 0 ){
							logger.warn("calculateOfflineRestAmount error, offlineRepayAmount="+ repayAmount +", restAmount="+ restAmount);
							throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
						}
					}
				}
			}
		}
	}

	/**
	 * 计算提前还款需还金额
	 */
	@Override
	public BigDecimal calculateAllRestAmount(Long loanId) {

		BigDecimal allRestAmount = BigDecimal.ZERO;

		List<AfLoanPeriodsDo> noRepayList = afLoanPeriodsDao.getNoRepayListByLoanId(loanId);

		for (AfLoanPeriodsDo loanPeriodsDo : noRepayList) {

			if(canRepay(loanPeriodsDo)) { // 已出账
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount(),
						loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
						loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
						loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
						.subtract(loanPeriodsDo.getRepayAmount());

			}else { // 未出账， 提前还款时不用还手续费和利息
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount());
			}

		}

		return allRestAmount;
	}

	/**
     * @return true: 已出账；false： 未出账
     */
	@Override
	public boolean canRepay(AfLoanPeriodsDo loanPeriodsDo) {
		boolean flag = false;
		Date now = new Date();
		Date plan = loanPeriodsDo.getGmtPlanRepay();
		
//		Integer remindDay = afLoanProductDao.getRemindDayByLoanPeriodsId(loanPeriodsDo.getRid());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(plan);
//		calendar.add(Calendar.DAY_OF_YEAR, -remindDay);
		calendar.add(Calendar.MONTH, -1);

		Date startTime = calendar.getTime();
		
		if(now.after(startTime)){ // 已出账
			flag = true;
		}

		return flag;
	}

	
	
	/**
	 * @Description:  根据还款金额，匹配实际需还的期数信息
	 * @return  List<AfLoanPeriodsDo>
	 */
	public List<AfLoanPeriodsDo> getLoanPeriodsIds(Long loanId, BigDecimal repaymentAmount){
		List<AfLoanPeriodsDo> loanPeriodsIds = new ArrayList<AfLoanPeriodsDo>();
		
		AfLoanPeriodsDo loanPeriodDo = afLoanPeriodsDao.getLastActivePeriodByLoanId(loanId);
		logger.info("AfLoanRepaymentServiceImpl getLoanPeriodsIds loanPeriodDo =>{}",JSONObject.toJSONString(loanPeriodDo)+",loanId="+loanId+",repaymentAmount="+repaymentAmount);
		// 最多可还期数(还款金额/（每期需还本金+手续费+利息）+1)
		BigDecimal restAmount = BigDecimal.ZERO;
		Integer nper = loanPeriodDo.getNper();
		
		for (int i = 0; i < (loanPeriodDo.getPeriods() - nper + 1); i++) {
			if(repaymentAmount.compareTo(BigDecimal.ZERO)>0){
				// 根据 loanId&期数  获取分期信息
				AfLoanPeriodsDo nextLoanPeriodDo = afLoanPeriodsDao.getPeriodByLoanIdAndNper(loanId, nper+i);
				// 当前期需还金额
				restAmount = calculateRestAmount(nextLoanPeriodDo.getRid());
				// 更新还款金额
				repaymentAmount = repaymentAmount.subtract(restAmount);
				
				loanPeriodsIds.add(nextLoanPeriodDo);
			}
		}
		
		return loanPeriodsIds;
	}

	public static class RepayDealBo {
		BigDecimal curRepayAmoutStub; 	//当前还款额变化句柄
		BigDecimal curRebateAmount; 	//当前还款使用的账户余额
		BigDecimal curSumRebateAmount = BigDecimal.ZERO;//当前还款使用的账户余额总额
		Long curUserCouponId;			//当前还款使用的还款优惠卷id
		BigDecimal curSumRepayAmount = BigDecimal.ZERO;	//当前还款总额
		String curCardNo;				//当前还款卡号
		String curCardName;				//当前还款卡别名
		String curName;					//当前还款名称，用来识别自动代付还款
		String curTradeNo;
		String curOutTradeNo;

		BigDecimal sumRepaidAmount = BigDecimal.ZERO;	//对应借款的还款总额
		BigDecimal sumAmount = BigDecimal.ZERO;			//对应借款总额（包含所有费用）
		BigDecimal sumBorrowAmount = BigDecimal.ZERO;	//对应借款总额（不包含其他费用）
		BigDecimal sumInterest = BigDecimal.ZERO;		//对应借款的利息总额
		BigDecimal sumPoundage = BigDecimal.ZERO;		//对应借款的手续费总额
		BigDecimal sumOverdueAmount = BigDecimal.ZERO;	//对应借款的逾期费总额
		BigDecimal sumIncome = BigDecimal.ZERO;			//对应借款我司产生的总收入

		AfBorrowCashDo cashDo;							//借款
		long overdueDay = 0;							//对应借款的逾期天数
		String borrowNo;								//借款流水号
		String refId = "";								//还款的id串
		Long userId ;									//目标用户id
	}

	@Override
	public List<AfLoanRepaymentDo> listDtoByLoanId(Long loanId) {
		return afLoanRepaymentDao.listDtoByLoanId(loanId);
	}

}
