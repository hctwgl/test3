package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfLoanPeriodStatus;
import com.ald.fanbei.api.common.enums.AfLoanRepaymentStatus;
import com.ald.fanbei.api.common.enums.AfLoanStatus;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.PayOrderSource;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.dao.AfLoanPeriodsDao;
import com.ald.fanbei.api.dal.dao.AfLoanProductDao;
import com.ald.fanbei.api.dal.dao.AfRepaymentBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.dao.AfUserBankcardDao;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.dao.AfYibaoOrderDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfLoanRepaymentDao;
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
import com.ald.fanbei.api.biz.bo.UpsCollectRespBo;
import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfTradeCodeInfoService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.UpsUtil;
import com.ald.fanbei.api.biz.third.util.yibaopay.YiBaoUtility;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.timevale.tech.sdk.network.d;



/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-01-19 16:50:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanRepaymentService")
public class AfLoanRepaymentServiceImpl extends ParentServiceImpl<AfLoanRepaymentDo, Long> implements AfLoanRepaymentService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanRepaymentServiceImpl.class);
   
    @Resource
    private AfRepaymentBorrowCashDao afRepaymentBorrowCashDao;
    @Resource
    private GeneratorClusterNo generatorClusterNo;
    @Resource
    private AfUserAccountDao afUserAccountDao;
    @Resource
    private AfBorrowCashService afBorrowCashService;
    @Resource
    private TransactionTemplate transactionTemplate;
    @Resource
    private AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    private AfUserCouponDao afUserCouponDao;
    @Resource
    private JpushService pushService;
    @Resource
    private AfUserBankcardDao afUserBankcardDao;
    @Resource
    private AfResourceService afResourceService;
    @Resource
    private AfUserBankcardService afUserBankcardService;
    @Resource
    private AfUserService afUserService;
    @Resource
    private UpsUtil upsUtil;
    @Resource
    private RiskUtil riskUtil;
    @Resource
    private SmsUtil smsUtil;
    @Resource
    private CollectionSystemUtil collectionSystemUtil;
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
    private AfLoanProductDao afLoanProductDao;

	@Override
	public void repay(LoanRepayBo bo) {
		if(!bo.isAllRepay && !canRepay(bo.loanPeriodsDoList.get(0))){
			// 未出账时拦截按期还款
			throw new FanbeiException(FanbeiExceptionCode.LOAN_PERIOD_CAN_NOT_REPAY_ERROR);
		}
		
		Date now = new Date();
		String name = Constants.DEFAULT_REPAYMENT_NAME_BORROW_CASH;
		if(StringUtil.equals("sysJob",bo.remoteIp)){
			name = Constants.BORROW_REPAYMENT_NAME_AUTO;
		}
		
		String tradeNo = generatorClusterNo.getRepaymentBorrowCashNo(now);
		bo.tradeNo = tradeNo;
		bo.name = name;
		
		// 增加还款记录
		generateRepayRecords(bo);
		
		// 还款操作
		doRepay(bo, bo.loanRepaymentDo);
		
	}
	
	/**
	 * @Description:  增加还款记录
	 * @return  void
	 */
	private void generateRepayRecords(LoanRepayBo bo) {
    	Date now = new Date();
    	String tradeNo = bo.tradeNo;
    	String name = bo.name;
		
    	AfLoanRepaymentDo loanRepaymentDo = buildRepayment(BigDecimal.ZERO, bo.repayAmount, tradeNo, now, bo.actualAmount, bo.couponId, 
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
			if(i == loanPeriodsDoList.size()){
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
			loanRepay.setCardNo(bank.getCardNumber());
			loanRepay.setCardName(bank.getBankName());
		}

		return loanRepay;
	}
	
	
    /**
     * @Description:  还款操作
     * @return  void
     */
    private void doRepay(LoanRepayBo bo, AfLoanRepaymentDo repayment) {
		if (bo.cardId > 0) {// 银行卡支付
			AfUserBankDto bank = afUserBankcardDao.getUserBankInfo(bo.cardId);
			UpsCollectRespBo respBo = upsUtil.collect(bo.tradeNo, bo.actualAmount, bo.userId.toString(), 
						bo.userDo.getRealName(), bank.getMobile(), bank.getBankCode(),
						bank.getCardNumber(), bo.userDo.getIdNumber(), Constants.DEFAULT_PAY_PURPOSE, bo.name, "02", PayOrderSource.REPAY_LOAN.getCode());
			
			logger.info("doRepay,ups respBo="+JSON.toJSONString(respBo));
			if(repayment != null) {
				changLoanRepaymentStatus(respBo.getTradeNo(), AfLoanRepaymentStatus.PROCESSING.name(), repayment.getRid());
			}
			if (!respBo.isSuccess()) {
				if(StringUtil.isNotBlank(respBo.getRespCode())){
					dealRepaymentFail(bo.tradeNo, respBo.getTradeNo(), true, afTradeCodeInfoService.getRecordDescByTradeCode(respBo.getRespCode()));
				}else{
					dealRepaymentFail(bo.tradeNo, respBo.getTradeNo(), false, "");
				}
				throw new FanbeiException(FanbeiExceptionCode.BANK_CARD_PAY_ERR);
			}
			
			bo.outTradeNo = respBo.getTradeNo();
		} else if (bo.cardId == -2) {// 余额支付
			dealRepaymentSucess(bo.tradeNo, "");
		}
		
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
        
		/*if(isNeedMsgNotice){
			//用户信息及当日还款失败次数校验
			int errorTimes = 0;
			AfUserDo afUserDo = afUserService.getUserById(loanRepaymentDo.getUserId());
			//如果是代扣，不校验次数
			String payType = loanRepaymentDo.getName();
			//模版数据map处理
			Map<String,String> replaceMapData = new HashMap<String, String>();
			replaceMapData.put("errorMsg", errorMsg);
			//还款失败短信通知
			if(StringUtil.isNotBlank(payType)&&payType.indexOf("代扣")>-1){
				smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_WITHHOLD_FAIL.getCode());
			}else{
				errorTimes = afRepaymentBorrowCashDao.getCurrDayRepayErrorTimesByUser(loanRepaymentDo.getUserId());
				smsUtil.sendConfigMessageToMobile(afUserDo.getMobile(), replaceMapData, errorTimes, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_BORROWCASH_FAIL.getCode());
				String title = "本次还款支付失败";
				String content = "非常遗憾，本次还款失败：&errorMsg，您可更换银行卡或采用其他还款方式。";
				content = content.replace("&errorMsg",errorMsg);
				pushService.pushUtil(title,content,afUserDo.getMobile());
			}
		}*/
		
	}
    
    
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo) {
		final AfLoanRepaymentDo repaymentDo = afLoanRepaymentDao.getRepayByTradeNo(tradeNo);
        dealRepaymentSucess(tradeNo, outTradeNo, repaymentDo,null);
    }
    
    
	/**
	 * 还款成功后调用
	 * @param tradeNo 我方交易流水
	 * @param outTradeNo 资金方交易流水
	 * @return
	 */
	@Override
    public void dealRepaymentSucess(String tradeNo, String outTradeNo, final AfLoanRepaymentDo repaymentDo,String operator) {
    	try {
    		lock(tradeNo);
    		
            logger.info("dealRepaymentSucess process begin, tradeNo=" + tradeNo + ",outTradeNo=" + outTradeNo + ",borrowRepayment=" + JSON.toJSONString(repaymentDo) );
            
            preCheck(repaymentDo, tradeNo);
//			repaymentDo.setOperator(operator);还款人
            final LoanRepayDealBo LoanRepayDealBo = new LoanRepayDealBo();
            LoanRepayDealBo.curTradeNo = tradeNo;
            LoanRepayDealBo.curOutTradeNo = outTradeNo;
            LoanRepayDealBo.repaymentDo = repaymentDo;
            
            long resultValue = transactionTemplate.execute(new TransactionCallback<Long>() {
                @Override
                public Long doInTransaction(TransactionStatus status) {
                    try {
                    	dealLoanRepay(LoanRepayDealBo, repaymentDo);	// TODO
                		
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
//            	notifyUserBySms(LoanRepayDealBo);
//            	nofityRisk(LoanRepayDealBo);
            }
    		
    	}finally {
    		unLock(tradeNo);
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
	 * @param LoanRepayDealBo
	 * @param repaymentDo
	 */
	private void dealLoanRepay(LoanRepayDealBo LoanRepayDealBo, AfLoanRepaymentDo repaymentDo) {
		if(repaymentDo == null) return;
		
		AfLoanDo loanDo = afLoanDao.getById(repaymentDo.getLoanId());
		LoanRepayDealBo.curRepayAmoutStub = repaymentDo.getRepayAmount();
		LoanRepayDealBo.curRebateAmount = repaymentDo.getUserAmount();
		LoanRepayDealBo.curSumRebateAmount = LoanRepayDealBo.curSumRebateAmount.add(repaymentDo.getUserAmount());
		LoanRepayDealBo.curUserCouponId = repaymentDo.getUserCouponId();
		LoanRepayDealBo.curSumRepayAmount = LoanRepayDealBo.curSumRepayAmount.add(repaymentDo.getRepayAmount());
		LoanRepayDealBo.curCardName = repaymentDo.getCardName();
		LoanRepayDealBo.curCardNo = repaymentDo.getCardNo();
		LoanRepayDealBo.curName = repaymentDo.getName();
		
		LoanRepayDealBo.loanDo = loanDo;
		LoanRepayDealBo.overdueDay = loanDo.getOverdueDays();
		LoanRepayDealBo.loanNo = loanDo.getLoanNo();
		LoanRepayDealBo.refId += repaymentDo.getLoanId();
		LoanRepayDealBo.userId = loanDo.getUserId();
		
		List<AfLoanPeriodsDo> loanPeriodsDoList = new ArrayList<AfLoanPeriodsDo>();

		if(repaymentDo.getPreRepayStatus().equals("Y")) {
			LoanRepayDealBo.isAllRepay = true; // 提前还款
			String[] repayPeriodsIds = repaymentDo.getRepayPeriods().split(",");
			for (int i = 0; i < repayPeriodsIds.length; i++) {
				// 获取分期信息
				AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsDao.getById(Long.parseLong(repayPeriodsIds[i]));
				loanPeriodsDoList.add(loanPeriodsDo);
				if(loanPeriodsDo!=null){
					if(canRepay(loanPeriodsDo)){
						dealLoanRepayOverdue(LoanRepayDealBo, loanPeriodsDo);		//逾期费
						dealLoanRepayPoundage(LoanRepayDealBo, loanPeriodsDo);		//手续费
						dealLoanRepayInterest(LoanRepayDealBo, loanPeriodsDo);		//利息
					}
					// TODO
					dealLoanAllRepayIfFinish(LoanRepayDealBo, repaymentDo, loanPeriodsDo);	//修改借款分期状态
				}
				afLoanPeriodsDao.updateById(loanPeriodsDo);
			}
		}else {
			LoanRepayDealBo.isAllRepay = false;	// 按期还款
			AfLoanPeriodsDo loanPeriodsDo = afLoanPeriodsDao.getById(Long.parseLong(repaymentDo.getRepayPeriods()));
			loanPeriodsDoList.add(loanPeriodsDo);
			if(loanPeriodsDo!=null){
				dealLoanRepayOverdue(LoanRepayDealBo, loanPeriodsDo);		//逾期费
				dealLoanRepayPoundage(LoanRepayDealBo, loanPeriodsDo);		//手续费
				dealLoanRepayInterest(LoanRepayDealBo, loanPeriodsDo);		//利息
				dealLoanRepayIfFinish(LoanRepayDealBo, repaymentDo, loanPeriodsDo);	//修改借款分期状态
			}
			afLoanPeriodsDao.updateById(loanPeriodsDo);
		}
			
		LoanRepayDealBo.loanPeriodsDoList = loanPeriodsDoList;
		
        changLoanRepaymentStatus(LoanRepayDealBo.curOutTradeNo, AfLoanRepaymentStatus.SUCC.name(), repaymentDo.getRid());
        
	}
	
	private void dealLoanStatus(LoanRepayDealBo LoanRepayDealBo) {
		int nper = LoanRepayDealBo.loanPeriodsDoList.size();
		if(nper > 1 || (nper == 1 && LoanRepayDealBo.loanPeriodsDoList.get(0).getNper() == LoanRepayDealBo.loanDo.getPeriods())) {
			// 提前还款 || 最后一期结清， 修改loan状态FINISHED
			AfLoanDo loanDo = new AfLoanDo();
			loanDo.setRid(LoanRepayDealBo.loanDo.getRid());
			loanDo.setStatus(AfLoanStatus.FINISHED.name());
			loanDo.setGmtModified(new Date());
			loanDo.setGmtFinish(new Date());
			afLoanDao.updateById(loanDo);
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
    	if (AfLoanRepaymentStatus.SUCC.equals(repaymentDo.getStatus())) {
    		accountInfo.setUsedAmount(accountInfo.getUsedAmount().subtract(repaymentDo.getUserAmount()));
    	}
    	
    	if(LoanRepayDealBo.curSumRebateAmount != null && LoanRepayDealBo.curSumRebateAmount.compareTo(BigDecimal.ZERO) > 0) {// 授权账户可用金额变更
            accountInfo.setRebateAmount(accountInfo.getRebateAmount().subtract(LoanRepayDealBo.curSumRebateAmount));
    	}
    	afUserAccountDao.updateOriginalUserAccount(accountInfo);
    	
    	if(LoanRepayDealBo.curUserCouponId != null && LoanRepayDealBo.curUserCouponId > 0) {
    		afUserCouponDao.updateUserCouponSatusUsedById(LoanRepayDealBo.curUserCouponId);// 优惠券设置已使用
    	}
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
	private void dealLoanRepayOverdue(LoanRepayDealBo LoanRepayDealBo, AfLoanPeriodsDo loanPeriodsDo) {
		if(LoanRepayDealBo.curRepayAmoutStub.compareTo(BigDecimal.ZERO) == 0) return;
		
		BigDecimal repayAmount = LoanRepayDealBo.curRepayAmoutStub;
		BigDecimal overdueAmount = loanPeriodsDo.getOverdueAmount();
		
        if (repayAmount.compareTo(overdueAmount) > 0) {
        	loanPeriodsDo.setRepaidOverdueAmount(BigDecimalUtil.add(loanPeriodsDo.getRepaidOverdueAmount(), overdueAmount));
        	loanPeriodsDo.setOverdueAmount(BigDecimal.ZERO);
            LoanRepayDealBo.curRepayAmoutStub = repayAmount.subtract(overdueAmount);
        } else {
        	throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
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
        } else {
        	throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
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
        } else {
        	throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT__ERROR);
        }
	}
	
	/**
	 * @Description: 分期记录 完成处理
	 * @return  void
	 */
	private void dealLoanRepayIfFinish(LoanRepayDealBo LoanRepayDealBo, AfLoanRepaymentDo repaymentDo, AfLoanPeriodsDo loanPeriodsDo) {
		
		BigDecimal allRepayAmount = loanPeriodsDo.getRepayAmount().add(repaymentDo.getRepayAmount());
		loanPeriodsDo.setRepayAmount(allRepayAmount);

		BigDecimal sumAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(), 
				loanPeriodsDo.getOverdueAmount(), loanPeriodsDo.getRepaidOverdueAmount(),
				loanPeriodsDo.getInterestFee(), loanPeriodsDo.getRepaidInterestFee(),
				loanPeriodsDo.getServiceFee(), loanPeriodsDo.getRepaidServiceFee());
		
		BigDecimal minus = allRepayAmount.subtract(sumAmount); //容许多还一块钱，兼容离线还款 场景
		if (minus.compareTo(BigDecimal.ZERO) >= 0 && minus.compareTo(BigDecimal.ONE) <= 0) {
			loanPeriodsDo.setStatus(AfLoanPeriodStatus.FINISHED.name());
			loanPeriodsDo.setGmtLastRepay(new Date());
        }
		loanPeriodsDo.setGmtModified(new Date());
	}

	
	/**
	 * @Description: 提前还款 分期记录 完成处理
	 * @return  void
	 */
	private void dealLoanAllRepayIfFinish(LoanRepayDealBo LoanRepayDealBo, AfLoanRepaymentDo repaymentDo, AfLoanPeriodsDo loanPeriodsDo) {	// TODO
		
		BigDecimal allRepayAmount = loanPeriodsDo.getRepayAmount().add(repaymentDo.getRepayAmount());
		loanPeriodsDo.setRepayAmount(loanPeriodsDo.getAmount());
		
		BigDecimal sumAmount = BigDecimalUtil.add(loanPeriodsDo.getAmount(), 
				loanPeriodsDo.getOverdueAmount(), loanPeriodsDo.getRepaidOverdueAmount(),
				loanPeriodsDo.getInterestFee(), loanPeriodsDo.getRepaidInterestFee(),
				loanPeriodsDo.getServiceFee(), loanPeriodsDo.getRepaidServiceFee());
		
		BigDecimal minus = allRepayAmount.subtract(sumAmount); //容许多还一块钱，兼容离线还款 场景
		if (minus.compareTo(BigDecimal.ZERO) >= 0 && minus.compareTo(BigDecimal.ONE) <= 0) {
			loanPeriodsDo.setStatus(AfLoanPeriodStatus.FINISHED.name());
			loanPeriodsDo.setGmtLastRepay(new Date());
		}
		loanPeriodsDo.setGmtModified(new Date());
	}

	// -----------------------------------------------------------------------------------------
	
   	
	/**
     * 线下还款
     * 
     * @param restAmount 
     */
	@Override
	public void offlineRepay(AfBorrowCashDo cashDo, String borrowNo, 
				String repayType, String repayTime, String repayAmount,
				String restAmount, String outTradeNo, String isBalance,String repayCardNum,String operator,String isAdmin) {
		checkOfflineRepayment(cashDo, repayAmount, outTradeNo);
		
		LoanRepayBo bo = new LoanRepayBo();
		bo.userId = cashDo.getUserId();
		bo.userDo = afUserAccountDao.getUserAccountInfoByUserId(bo.userId);
		
		bo.cardId = (long) -4;
		bo.repayAmount = NumberUtil.objToBigDecimalDivideOnehundredDefault(repayAmount, BigDecimal.ZERO);
		bo.actualAmount =  bo.repayAmount;
		bo.loanId = cashDo.getRid();
		
		bo.tradeNo = generatorClusterNo.getOfflineRepaymentBorrowCashNo(new Date());
		if (isAdmin != null && "Y".equals(isAdmin)){
			bo.name = Constants.BORROW_REPAYMENT_NAME_OFFLINE;//财务线下打款
		}else {
			bo.name = Constants.COLLECTION_BORROW_REPAYMENT_NAME_OFFLINE;//催收线下打款
		}
		bo.outTradeNo = outTradeNo;
		bo.cardNo = repayCardNum;
		bo.repayType = repayType;
		generateRepayRecords(bo);

		//dealRepaymentSucess(bo.tradeNo, null, bo.loanRepaymentDo,operator); TODO
		
	}
	
    
    private void notifyUserBySms(LoanRepayDealBo LoanRepayDealBo) {
    	logger.info("notifyUserBySms info begin,sumAmount="+LoanRepayDealBo.sumAmount+",curSumRepayAmount="+LoanRepayDealBo.curSumRepayAmount+",sumRepaidAmount="+LoanRepayDealBo.sumRepaidAmount);
        try {
            AfUserDo afUserDo = afUserService.getUserById(LoanRepayDealBo.userId);
            if(LoanRepayDealBo.curName.equals("代扣付款")){
                sendRepaymentBorrowCashWithHold(afUserDo.getMobile(), LoanRepayDealBo.sumAmount);
            }else{
            	sendRepaymentBorrowCashWarnMsg(afUserDo.getMobile(), LoanRepayDealBo.curSumRepayAmount, LoanRepayDealBo.sumAmount.subtract(LoanRepayDealBo.sumRepaidAmount));
            }
        } catch (Exception e) {
            logger.error("Sms notify user error, userId:" + LoanRepayDealBo.userId + ",nowRepayAmount:" + LoanRepayDealBo.curSumRepayAmount + ",notRepayMoney" + LoanRepayDealBo.sumAmount.subtract(LoanRepayDealBo.sumRepaidAmount), e);
        }
    }
    
    
    /**
     * 代扣现金贷还款成功短信发送
     * @param mobile
     * @param nowRepayAmountStr
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
     * @param nowRepayAmountStr
     */
    private boolean sendRepaymentBorrowCashWarnMsg(String mobile,BigDecimal repayMoney,BigDecimal notRepayMoney){
    	//模版数据map处理
    	Map<String,String> replaceMapData = new HashMap<String, String>();
 		replaceMapData.put("repayMoney", repayMoney+"");
 		replaceMapData.put("remainAmount", notRepayMoney+"");
         if (notRepayMoney==null || notRepayMoney.compareTo(BigDecimal.ZERO)<=0) {
			 String title = "恭喜您，借款已还清！";
			 String content = "您的还款已经处理完成，成功还款&repayMoney元。信用分再度升级，给您点个大大的赞！";
			 content = content.replace("&repayMoney",repayMoney.toString());
			 pushService.pushUtil(title,content,mobile);
         	return smsUtil.sendConfigMessageToMobile(mobile, replaceMapData, 0, AfResourceType.SMS_TEMPLATE.getCode(), AfResourceSecType.SMS_REPAYMENT_SUCCESS.getCode());
         } else {
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
    	
    	try {//涉及运算,放在内部传输数据
            String riskOrderNo = riskUtil.getOrderNo("tran", cardNo.substring(cardNo.length() - 4, cardNo.length()));
            JSONArray details = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("borrowNo", LoanRepayDealBo.loanNo);
            obj.put("amount", LoanRepayDealBo.sumLoanAmount);
            obj.put("repayment", LoanRepayDealBo.sumRepaidAmount);
            obj.put("income", LoanRepayDealBo.sumIncome);
            obj.put("interest", LoanRepayDealBo.sumInterest);
            obj.put("overdueAmount", LoanRepayDealBo.sumOverdueAmount);
            obj.put("overdueDay", LoanRepayDealBo.overdueDay);
            details.add(obj);
            riskUtil.transferBorrowInfo(LoanRepayDealBo.userId.toString(), "50", riskOrderNo, details);
        } catch (Exception e) {
            logger.error("还款时给风控传输数据出错", e);
        }
    	
        /**------------------------------------fmai风控提额begin------------------------------------------------*/
        try {
            if ( AfLoanStatus.FINISHED.name().equals(LoanRepayDealBo.loanDo.getStatus()) ) {
            	String riskOrderNo = riskUtil.getOrderNo("rise", cardNo.substring(cardNo.length() - 4, cardNo.length()));
                int overdueCount = 0;
                if (StringUtil.equals("Y", LoanRepayDealBo.loanDo.getOverdueStatus())) {
                    overdueCount = 1;
                }
                riskUtil.raiseQuota(LoanRepayDealBo.userId.toString(), 
                			LoanRepayDealBo.loanNo, "50", riskOrderNo, 
                			LoanRepayDealBo.sumLoanAmount,
                			LoanRepayDealBo.sumIncome, 
                			LoanRepayDealBo.overdueDay, overdueCount);
            }
        } catch (Exception e) {
            logger.error("notifyRisk.raiseQuota error！", e);
        }
        /**------------------------------------fmai风控提额end--------------------------------------------------*/

        /*//会对逾期的借款还款，向催收平台同步还款信息
        if (DateUtil.compareDate(new Date(), LoanRepayDealBo.loanDo.getGmtPlanRepayment()) ){
            try {
                CollectionSystemReqRespBo respInfo = collectionSystemUtil.consumerRepayment(
                		LoanRepayDealBo.curTradeNo,
                		LoanRepayDealBo.borrowNo,
                		LoanRepayDealBo.curCardNo,
                		LoanRepayDealBo.curCardName,
                        DateUtil.formatDateTime(new Date()),
                        LoanRepayDealBo.curOutTradeNo,
                        LoanRepayDealBo.curSumRepayAmount,
                        LoanRepayDealBo.sumAmount.subtract(LoanRepayDealBo.sumRepaidAmount).setScale(2, RoundingMode.HALF_UP), //未还的
                        LoanRepayDealBo.sumAmount.setScale(2, RoundingMode.HALF_UP),	
                        LoanRepayDealBo.sumOverdueAmount,
                		LoanRepayDealBo.sumRepaidAmount,
                		LoanRepayDealBo.sumInterest);
                logger.info("collection consumerRepayment req success, respinfo={}", respInfo);
            } catch (Exception e) {
                logger.error("向催收平台同步还款信息失败", e);
            }
        }else{
			logger.info("collection consumerRepayment not push,borrowCashId="+LoanRepayDealBo.loanDo.getRid());
		}*/
    }
	
	
	private void checkOfflineRepayment(AfBorrowCashDo cashDo, String offlineRepayAmount ,String outTradeNo) {
		if(afRepaymentBorrowCashDao.getRepaymentBorrowCashByTradeNo(null, outTradeNo) != null) {
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_REPEAT_ERROR);
		}
		
//		BigDecimal restAmount = calculateRestAmount(cashDo); TODO
		BigDecimal restAmount=BigDecimal.ZERO;
		BigDecimal offlineRepayAmountYuan = NumberUtil.objToBigDecimalDivideOnehundredDefault(offlineRepayAmount, BigDecimal.ZERO);
		// 因为有用户会多还几分钱，所以加个安全金额限制，当还款金额 > 用户应还金额+1元 时，返回错误
		if (offlineRepayAmountYuan.compareTo(restAmount.add(BigDecimal.ONE)) > 0) {
			logger.warn("CheckOfflineRepayment error, offlineRepayAmount="+ offlineRepayAmount +", restAmount="+ restAmount);
			throw new FanbeiException(FanbeiExceptionCode.BORROW_CASH_REPAY_AMOUNT_MORE_BORROW_ERROR);
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
	
	
	public static class LoanRepayBo{
		public Long userId;
		
		/* request字段 */
		public BigDecimal repayAmount = BigDecimal.ZERO;	// 还款金额
		public BigDecimal actualAmount = BigDecimal.ZERO; 
		public BigDecimal rebateAmount = BigDecimal.ZERO; //可选字段
		public String payPwd;			
		public Long cardId;
		public Long couponId;			//可选字段
		public Long loanId;			
		public Long loanPeriodsId;			
		/* request字段 */
		
		/* biz 业务处理字段 */
		public AfLoanRepaymentDo loanRepaymentDo;
		public AfLoanDo loanDo;
		public List<AfLoanPeriodsDo> loanPeriodsDoList;	//借款分期
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
	public BaseDao<AfLoanRepaymentDo, Long> getDao() {
		return null;
	}

	@Override
	public AfLoanRepaymentDo getProcessLoanRepaymentByLoanId(Long loanId) {
		return afLoanRepaymentDao.getProcessLoanRepaymentByLoanId(loanId);
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
						loanPeriodsDo.getInterestFee(),loanPeriodsDo.getServiceFee(),loanPeriodsDo.getOverdueAmount());
		}
		
		return restAmount;
	}

    /**
     * 计算提前还款需还金额
     */
	@Override
	public BigDecimal calculateAllRestAmount(Long loanId) {
		
		Date nowDate = new Date();
		BigDecimal allRestAmount = BigDecimal.ZERO;

		List<AfLoanPeriodsDo> noRepayList = afLoanPeriodsDao.getNoRepayListByLoanId(loanId);
		
		for (AfLoanPeriodsDo loanPeriodsDo : noRepayList) {
			
			if(canRepay(loanPeriodsDo)) { // 已出账
				allRestAmount = BigDecimalUtil.add(allRestAmount,loanPeriodsDo.getAmount(),
						loanPeriodsDo.getInterestFee(),loanPeriodsDo.getServiceFee(),loanPeriodsDo.getOverdueAmount());
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

}