package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.foroutapi.service.HomeBorrowService;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.*;
import com.ald.fanbei.api.dal.domain.*;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.InterestFreeJsonBo;
import com.ald.fanbei.api.biz.third.util.TaobaoApiUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowCalculateMethod;
import com.ald.fanbei.api.common.enums.BorrowLogStatus;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.PayType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.taobao.api.domain.XItem;

/**
 * 
 * @类描述：
 * 
 * @author hexin 2017年2月9日下午4:51:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowService")
public class AfBorrowServiceImpl extends BaseService implements AfBorrowService,HomeBorrowService {

	@Resource
	AfBorrowDao afBorrowDao;

	@Resource
	TransactionTemplate transactionTemplate;

	@Resource
	AfResourceDao afResourceDao;

	@Resource
	AfUserAccountDao afUserAccountDao;

	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Resource
	AfUserAccountLogDao afUserAccountLogDao;

	@Resource
	private JpushService pushService;

	@Resource
	private AfBorrowTempDao afBorrowTempDao;

	@Resource
	private AfUserBankcardDao afUserBankcardDao;

	@Resource
	private BizCacheUtil bizCacheUtil;

	@Resource
	AfBorrowInterestDao afBorrowInterestDao;

	@Resource
	AfBorrowLogDao afBorrowLogDao;

	@Resource
	TaobaoApiUtil taobaoApiUtil;
	@Resource
	AfBorrowBillDao afBorrowBillDao;
	@Resource
	AfRepaymentDao afRepaymentDao;
	@Resource
	AfAgentOrderService afAgentOrderService;
	
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfOrderService afOrderService;
	@Resource
	AfRecommendUserDao afRecommendUserDao;
	@Resource
	AfRecommendUserService afRecommendUserService;
	@Resource
	AfUserOutDayDao afUserOutDayDao;
	@Resource
	AfOrderDao afOrderDao;
	@Resource
	ContractPdfThreadPool contractPdfThreadPool;
	@Resource
	KafkaSync kafkaSync;

	@Override
	public HashMap getUserSummary(Long userId){
		try{
//			AfResourceDo afResourceDo= afResourceDao.getSingleResourceBytype(KafkaConstants.KAFKA_OPEN);
//			if(afResourceDo!=null&&afResourceDo.getValue().equals("Y")){
//				logger.info("find data from mongo:"+userId);
//				return kafkaSync.getUserSummarySync(userId);
//			}else{
				return afBorrowDao.getUserSummary(userId);
			//}
		}catch (Exception e){
			logger.error("getUserSummary error:",e);
			return new HashMap();
		}

	}
	@Override
	public Date getReyLimitDate(String billType, Date now) {
		Date start = DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now));
		Date startDate = DateUtil.addDays(start,
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10) - 1);
		Date limitTime = DateUtil.getEndOfDate(DateUtil.addDays(start,
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_REPAY_TIME), 20) - 1));
		if (startDate.after(now)) {
			limitTime = DateUtil.addMonths(limitTime, -1);
		}
		if ("N".equals(billType)) {
			limitTime = DateUtil.addMonths(limitTime, 1);
		}
		return limitTime;
	}

	@Override
	public long dealCashApply(final AfUserAccountDo userDto, final BigDecimal money, final Long cardId) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					// 修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userDto.getUserId());
					account.setUcAmount(money);// 已取现金额=已取现金额+申请取现金额
					account.setUsedAmount(money);// 授信已使用金额=授信已使用金额+申请取现金额
					afUserAccountDao.updateUserAccount(account);
					AfBorrowDo borrow = null;
					// 信用分大于指定值
					AfResourceDo resourceInfo = afResourceDao
							.getSingleResourceBytype(Constants.RES_DIRECT_TRANS_CREDIT_SCORE);
					if (userDto.getCreditScore() >= Integer.valueOf(resourceInfo.getValue())) {
						borrow = buildBorrow(Constants.DEFAULT_BORROW_CASH_NAME, BorrowType.CASH, userDto.getUserId(),
								money, cardId, 1, money, BorrowStatus.TRANSED.getCode());
						borrow.setGmtTransed(new Date());
						// 直接打款
						afBorrowDao.addBorrow(borrow);
						afBorrowLogDao.addBorrowLog(buildBorrowLog(userDto.getUserName(), userDto.getUserId(),
								borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));
					} else {
						// 进行申请
						borrow = buildBorrow(Constants.DEFAULT_BORROW_CASH_NAME, BorrowType.CASH, userDto.getUserId(),
								money, cardId, 1, money, BorrowStatus.APPLY.getCode());
						afBorrowDao.addBorrow(borrow);
						afBorrowLogDao.addBorrowLog(buildBorrowLog(userDto.getUserName(), userDto.getUserId(),
								borrow.getRid(), BorrowLogStatus.APPLY.getCode()));
					}
					afUserAccountLogDao.addUserAccountLog(
							addUserAccountLogDo(UserAccountLogType.CASH, money, userDto.getUserId(), borrow.getRid()));

					return borrow.getRid();
				} catch (Exception e) {
					logger.info("dealCashApply error:" + e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}

	private AfBorrowLogDo buildBorrowLog(String userName, Long userId, Long borrowId, String type) {
		AfBorrowLogDo log = new AfBorrowLogDo();
		log.setCreator(userName);
		log.setBorrowId(borrowId);
		log.setUserId(userId);
		log.setType(type);
		return log;
	}

	/**
	 * 
	 * @param userId
	 * @param money
	 *            -- 借款金额
	 * @return
	 */
	private AfBorrowDo buildBorrow(String name, BorrowType type, Long userId, BigDecimal money, Long cardId, int nper,
			BigDecimal perAmount, String status) {
		Date currDate = new Date();
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setGmtCreate(currDate);
		borrow.setAmount(money);
		borrow.setType(type.getCode());
		borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
		borrow.setStatus(status);// 默认转账成功
		borrow.setName(name);
		borrow.setUserId(userId);
		borrow.setNper(nper);
		borrow.setNperAmount(perAmount);
		AfBankUserBankDto bank = afUserBankcardDao.getUserBankcardByBankId(cardId);
		borrow.setCardNumber(bank.getCardNumber());
		borrow.setCardName(bank.getBankName());

		borrow.setRemark(StringUtils.EMPTY);
		borrow.setOrderId(0l);
		borrow.setOrderNo(StringUtils.EMPTY);
		return borrow;
	}

	/**
	 * 
	 * @param name 分期名称
	 * @param type 分期类型
	 * @param userId 用户id
	 * @param amount 分期金额
	 * @param nper 分期期数
	 * @param perAmount 每期金额
	 * @param status 状态
	 * @param orderId 订单id
	 * @param orderNo 订单编号
	 * @param borrowRate 借款利率等参数
	 * @param interestFreeJson 分期规则
	 * @return
	 */
	private AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal amount, int nper,
			BigDecimal perAmount, String status, Long orderId, String orderNo, String borrowRate, String interestFreeJson) {
		
		Integer freeNper = 0;
		List<InterestFreeJsonBo> interestFreeList = StringUtils.isEmpty(interestFreeJson) ? null : JSONObject.parseArray(interestFreeJson, InterestFreeJsonBo.class);
		if (CollectionUtils.isNotEmpty(interestFreeList)) {
			for (InterestFreeJsonBo bo : interestFreeList) {
				if (bo.getNper().equals(nper)) {
					freeNper = bo.getFreeNper();
					break;
				}
			}
		}
		Date currDate = new Date();
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setGmtCreate(currDate);
		borrow.setAmount(amount);
		borrow.setType(type.getCode());
		borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
		borrow.setStatus(status);// 默认转账成功
		borrow.setName(name);
		borrow.setUserId(userId);
		borrow.setNper(nper);
		borrow.setNperAmount(perAmount);
		borrow.setCardNumber(StringUtils.EMPTY);
		borrow.setCardName("代付");
		borrow.setRemark(name);
		borrow.setOrderId(orderId);
		borrow.setOrderNo(orderNo);
		borrow.setBorrowRate(borrowRate);
		borrow.setCalculateMethod(BorrowCalculateMethod.DENG_BEN_DENG_XI.getCode());
		borrow.setFreeNper(freeNper);
		return borrow;
	}

	public String getCurrentLastBorrowNo(String orderNoPre) {
		return afBorrowDao.getCurrentLastBorrowNo(orderNoPre);
	}

	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType borrowType, BigDecimal amount, Long userId,
			Long borrowId) {
		// 增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(borrowId + "");
		accountLog.setType(borrowType.getCode());
		return accountLog;
	}

	@Override
	public long dealConsumeApply(final AfUserAccountDo userDto, final BigDecimal amount, final Long cardId,
			final Long goodsId, final String openId, final String numId, final String name, final int nper) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					// 修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUsedAmount(amount);
					account.setUserId(userDto.getUserId());
					afUserAccountDao.updateUserAccount(account);
					// 获取借款分期配置信息
					AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
					if (null == resource) {
						resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
								Constants.RES_BORROW_CONSUME);
						bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource,
								Constants.SECOND_OF_HALF_HOUR);
					}
					BigDecimal money = amount;// 借款金额
					BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN,
							BigDecimal.ZERO);
					BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX,
							BigDecimal.ZERO);
					String[] range = StringUtil.split(resource.getValue2(), ",");
					if (null != range && range.length == 2) {
						rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
						rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
					}
					JSONArray array = JSON.parseArray(resource.getValue());
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (obj.getInteger(Constants.DEFAULT_NPER) == nper) {
							BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(money, nper,
									new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd,InterestfreeCode.NO_FREE.getCode());// 总手续费
							BigDecimal perAmount = BigDecimalUtil.getConsumeAmount(money, nper,
									new BigDecimal(obj.getString(Constants.DEFAULT_RATE)).divide(
											new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
									totalPoundage);// 每期账单金额
							AfBorrowDo borrow = null;
							// 信用分大于指定值
							AfResourceDo resourceInfo = afResourceDao
									.getSingleResourceBytype(Constants.RES_DIRECT_TRANS_CREDIT_SCORE);
							if (userDto.getCreditScore() >= Integer.valueOf(resourceInfo.getValue())) {
								borrow = buildBorrow(name, BorrowType.CONSUME_TEMP, userDto.getUserId(), amount, cardId,
										nper, perAmount, BorrowStatus.TRANSED.getCode());
								borrow.setGmtTransed(new Date());
								// 新增借款信息
								afBorrowDao.addBorrow(borrow);
								// 直接打款
								afBorrowLogDao.addBorrowLog(buildBorrowLog(userDto.getUserName(), userDto.getUserId(),
										borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));
							} else {
								borrow = buildBorrow(name, BorrowType.CONSUME_TEMP, userDto.getUserId(), amount, cardId,
										nper, perAmount, BorrowStatus.APPLY.getCode());
								// 新增借款信息
								afBorrowDao.addBorrow(borrow);
								// 进行申请
								afBorrowLogDao.addBorrowLog(buildBorrowLog(userDto.getUserName(), userDto.getUserId(),
										borrow.getRid(), BorrowLogStatus.APPLY.getCode()));
							}
							String openiId = "";
							// 新增借款商品关联信息
							if (StringUtil.isBlank(openId) && StringUtil.isNotBlank(numId)) {
								Map<String, Object> params = new HashMap<String, Object>();
								params.put("numIid", numId);
								List<XItem> nTbkItemList = taobaoApiUtil.executeTbkItemSearch(params).getItems();
								if (null != nTbkItemList && nTbkItemList.size() > 0) {
									openiId = nTbkItemList.get(0).getOpenIid();
								}
							} else {
								openiId = openId;
							}
							afBorrowTempDao.addBorrowTemp(buildBorrowTemp(goodsId, openiId, numId, borrow.getRid()));

							// 新增借款日志
							afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME,
									amount, userDto.getUserId(), borrow.getRid()));
							return borrow.getRid();
						}
					}
					return 0l;
				} catch (Exception e) {
					logger.info("dealConsumeApply error:" + e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}

	@Override
	public Map<String, Object> getCurrentYearAndMonth(Date now,AfUserOutDayDo afUserOutDayDo) {
		Map<String, Object> map = new HashMap<String, Object>();
		// 账单日

		int default_out_day = 10;//NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10);
		int default_pay_day = 20;//NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_REPAY_TIME), 20);

		if(afUserOutDayDo !=null){
			default_out_day = afUserOutDayDo.getOutDay();
			default_pay_day = afUserOutDayDo.getPayDay();
		}

		Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
				default_out_day - 1);
		startDate = DateUtil.addMonths(startDate, -1);
//		logger.info("billdate compare1"+ now);
//		logger.info("billdate compare2"+ startDate);
//		if (now.before(startDate)) {// 账单日前面
//			startDate = DateUtil.addMonths(startDate, -1);
//			logger.info("billdate compare3 true");
//		}
		int billYear = 0, billMonth = 0;
		String[] billDay = DateUtil.formatDate(startDate, DateUtil.MONTH_PATTERN).split("-");
		if (billDay.length == 2) {
			billYear = NumberUtil.objToIntDefault(billDay[0], 0);
			billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
		}
		map.put(Constants.DEFAULT_YEAR, billYear);
		map.put(Constants.DEFAULT_MONTH, billMonth);

		Calendar out_datetime = Calendar.getInstance();
		out_datetime.setTime(startDate);
		out_datetime.add(Calendar.MONTH,1);
		out_datetime.set(Calendar.DAY_OF_MONTH,default_out_day);
		out_datetime.set(Calendar.HOUR_OF_DAY,0);
		out_datetime.set(Calendar.MINUTE,0);
		out_datetime.set(Calendar.SECOND,0);
		out_datetime.set(Calendar.MILLISECOND,0);

		Calendar pay_datetime = Calendar.getInstance();
		pay_datetime.setTime(startDate);
		if(default_pay_day > default_out_day) {
			pay_datetime.add(Calendar.MONTH, 1);
		}
		else{
			pay_datetime.add(Calendar.MONTH, 2);
		}
		pay_datetime.set(Calendar.DAY_OF_MONTH,default_pay_day);
		pay_datetime.set(Calendar.HOUR_OF_DAY,23);
		pay_datetime.set(Calendar.MINUTE,59);
		pay_datetime.set(Calendar.SECOND,59);
		//pay_datetime.set(Calendar.MILLISECOND,999);

		map.put(Constants.OUT_DATETIME,out_datetime.getTime());
		map.put(Constants.PAY_DATETIME,pay_datetime.getTime());
		return map;
	}

	@Override
	public int getBorrowInterestCountByBorrowId(Long borrowId) {
		return afBorrowInterestDao.getBorrowInterestCountByBorrowId(borrowId);
	}

	@Override
	public AfBorrowDo getBorrowById(Long id) {
		return afBorrowDao.getBorrowById(id);
	}

	@Override
	public Date getReyLimitDate(int year, int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year, month, 1);
		Date date = DateUtil.getEndOfDate(DateUtil.addDays(DateUtil.getFirstOfMonth(calendar.getTime()),
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_REPAY_TIME), 20) - 1));
		return date;
	}

	private AfBorrowTempDo buildBorrowTemp(Long goodsId, String openId, String numId, Long borrowId) {
		AfBorrowTempDo temp = new AfBorrowTempDo();
		temp.setBorrowId(borrowId);
		temp.setGoodsId(goodsId);
		temp.setOpenId(openId);
		temp.setNumId(numId);
		return temp;
	}

	@Override
	public AfBorrowTempDo getBorrowTempByBorrowId(Long borrowId) {
		return afBorrowTempDao.getBorrowTempByBorrowId(borrowId);
	}

	@Override
	public Map<String, Integer> getCurrentTermYearAndMonth(String type, Date now) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10) - 1);
		if (now.before(startDate)) {// 账单日前面
			startDate = DateUtil.addMonths(startDate, -2);
		} else {
			startDate = DateUtil.addMonths(startDate, -1);
		}
		if ("N".equals(type)) {
			startDate = DateUtil.addMonths(startDate, 1);
		}
		int billYear = 0, billMonth = 0;
		String[] billDay = DateUtil.formatDate(startDate, DateUtil.MONTH_PATTERN).split("-");
		if (billDay.length == 2) {
			billYear = NumberUtil.objToIntDefault(billDay[0], 0);
			billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
		}
		map.put(Constants.DEFAULT_YEAR, billYear);
		map.put(Constants.DEFAULT_MONTH, billMonth);
		return map;
	}

	@Override
	public void cashBillTransfer(final AfBorrowDo borrow, final AfUserAccountDo userDto) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					// 生成账单
					AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CASH);
					if (null == resource) {
						resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
								Constants.RES_BORROW_CASH);
						bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CASH, resource,
								Constants.SECOND_OF_HALF_HOUR);
					}
					BigDecimal money = borrow.getAmount();// 借款金额
					BigDecimal dayRate = NumberUtil.objToBigDecimalDefault(resource.getValue(), BigDecimal.ZERO);// 取现日利率
					BigDecimal serviceCharge = NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO);// 取现手续费率
					BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN,
							BigDecimal.ZERO);
					BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX,
							BigDecimal.ZERO);
					String[] range = StringUtil.split(resource.getValue2(), ",");
					if (null != range && range.length == 2) {
						rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
						rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
					}
					BigDecimal chargeAmount = money.multiply(serviceCharge);// 计算手续费
					if (rangeBegin.compareTo(chargeAmount) > 0) {
						chargeAmount = rangeBegin;
					} else if (rangeEnd.compareTo(chargeAmount) == -1) {
						chargeAmount = rangeEnd;
					}
					BigDecimal interestAmount = money.multiply(dayRate);// 日利息
					List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CASH, borrow,
							money.add(interestAmount).add(chargeAmount), BigDecimal.ZERO, interestAmount,
							BigDecimal.ZERO, chargeAmount, null);
					if (null != billList && billList.size() > 0) {
						AfBorrowBillDo cashBill = billList.get(0);
						// 生成利息日志
						afBorrowDao.addBorrowBillInfo(cashBill);
						afBorrowInterestDao.addBorrowInterest(
								buildBorrowInterest(cashBill.getRid(), interestAmount, userDto.getUserName(), money));
					}
					afBorrowDao.updateBorrowStatus(borrow.getRid(), BorrowStatus.TRANSED.getCode());
					pushService.dealBorrowCashTransfer(userDto.getUserName(), borrow.getGmtCreate());

					//#region 修改最是否己借款  add by hongzhengpei
//					if(borrow.getStatus().equals("TRANSED")) {
//						afRecommendUserService.updateRecommendByBorrow(borrow.getUserId(), borrow.getGmtCreate());
//					}

					//#endregion

				} catch (Exception e) {
//					logger.info("create cashBill error:", e);
					status.setRollbackOnly();
					throw  new FanbeiException(FanbeiExceptionCode.BORROW_ERROR,e);
				}
				return null;
			}
		});
		kafkaSync.syncEvent(borrow.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
	}

	/**
	 * 借款分期账单
	 * 
	 * @param borrow
	 *            --借款信息
	 * @param totalAmount
	 *            --本息手续费总计
	 * @param interestAmount
	 *            --利息总计
	 * @param poundageAmount
	 *            --手续费总计
	 * @param monthRate
	 *            --月利率
	 * @return
	 */
	private List<AfBorrowBillDo> buildBorrowBill(BorrowType borrowType, AfBorrowDo borrow, BigDecimal perAmount,
			BigDecimal totalAmount, BigDecimal interestAmount, BigDecimal monthRate, BigDecimal poundageAmount,
			BorrowBillStatus billStatus) {
		List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
		Date now = new Date();// 当前时间
		BigDecimal billAmount = perAmount;
		BigDecimal money = borrow.getAmount();// 借款金额
		AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(borrow.getUserId());

		AfBorrowBillDo afBorrowBillLast = afBorrowBillDao.getLastOutBill(borrow.getUserId());
		int out_day = 10;
		int pay_day = 20;
		if(afUserOutDayDo !=null){
			out_day = afUserOutDayDo.getOutDay();
			pay_day = afUserOutDayDo.getPayDay();
		}

		Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
				out_day - 1);
		if(startDate.before(now)){
			now = DateUtil.addMonths(now,1);
		}

		if(afBorrowBillLast !=null){
			if(getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue() != getYearMonth(afBorrowBillLast.getGmtPayTime()).intValue()){
				//己出，且跨月
				Date d=  getNowOutDay(now,out_day);
				if(pay_day<out_day){
					//只比较出帐日
					if( getYearMonth(d).intValue() <= getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue()){
						now = DateUtil.addMonths(now,1);
						logger.info("billMonth plus 1 first now:"+now );
					}
				}
				else{
					//比较还款日
					int oldPayDate = getYearMonth(afBorrowBillLast.getGmtPayTime()).intValue();
					int newPayDate = getYearMonth(now).intValue();
					if( oldPayDate>=newPayDate){
						Integer [] _oldPay =  getYearMonthV(afBorrowBillLast.getGmtPayTime());
						Calendar c = Calendar.getInstance();
						c.setTime(now);
						c.set(Calendar.YEAR,_oldPay[0]);
						c.set(Calendar.MONTH,_oldPay[1]-1);
						now = c.getTime();
						now = DateUtil.addMonths(now,1);
						logger.info("billMonth plus 1 first now:"+now );
					}
				}

			}else {
				//只比较出帐日
				Date d = getNowOutDay(now, out_day);
				if (getYearMonth(d).intValue() <= getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue()) {
					now = DateUtil.addMonths(now, 1);
					logger.info("billMonth plus 1 first now:" + now);
				}
			}
		}

		logger.info("check billMonth time "+now);
		logger.info("check billMonth time yearAndMonth "+getYearMonth(now));

		// 计算本息总计
		for (int i = 1; i <= borrow.getNper(); i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setBorrowNo(borrow.getBorrowNo());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(borrow.getGmtCreate());
			Map<String, Object> timeMap = getCurrentYearAndMonth(now,afUserOutDayDo);
			bill.setBillYear((Integer) timeMap.get(Constants.DEFAULT_YEAR));
			bill.setBillMonth((Integer) timeMap.get(Constants.DEFAULT_MONTH));
			bill.setGmtOutDay((Date) timeMap.get(Constants.OUT_DATETIME));
			bill.setGmtPayTime((Date) timeMap.get(Constants.PAY_DATETIME));
			bill.setNper(borrow.getNper());
			bill.setBillNper(i);
			if (borrowType.equals(BorrowType.CASH)) {
				bill.setPrincipleAmount(borrow.getAmount());
				bill.setInterestAmount(interestAmount);
				bill.setPoundageAmount(poundageAmount);
				bill.setStatus(BorrowBillStatus.NO.getCode());
				bill.setType(BorrowType.CASH.getCode());
				bill.setBillAmount(billAmount);
			} else {
				BigDecimal perPoundageAmount = poundageAmount.divide(new BigDecimal(borrow.getNper()), 2,
						BigDecimal.ROUND_HALF_UP);// 当月手续费
				BigDecimal perInterest = money.multiply(monthRate).setScale(2, BigDecimal.ROUND_HALF_UP);// 本月利息
				bill.setInterestAmount(perInterest);
				bill.setPoundageAmount(perPoundageAmount);
				if (i < bill.getNper()) {
					bill.setBillAmount(billAmount);
				} else {
					bill.setBillAmount(totalAmount);
				}
				totalAmount = totalAmount.subtract(billAmount);
				bill.setPrincipleAmount(bill.getBillAmount().subtract(perInterest).subtract(perPoundageAmount));// 本金  = 账单金额 -本月利息 -手续费
				money = money.subtract(bill.getPrincipleAmount());// 期初余额-本金
				bill.setStatus(billStatus.getCode());
				bill.setType(BorrowType.CONSUME.getCode());
			}
			list.add(bill);
			now = DateUtil.addMonths(now, 1);
		}
		return list;
	}
	
	/**
	 * 
	 * @param borrow 借款信息
	 * @return
	 */
	public List<AfBorrowBillDo> buildBorrowBillForNewInterest(AfBorrowDo borrow, String payType) {
		List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
		Date now = new Date();// 当前时间
		//生成时间
		logger.info("check now time"+now);
		Integer nper = borrow.getNper();
		Integer freeNper = borrow.getFreeNper();
		String borrowRate = borrow.getBorrowRate();
		BigDecimal money = borrow.getAmount();// 借款金额
		
		//拿到日利率快照Bo
		BorrowRateBo borrowRateBo =  BorrowRateBoUtil.parseToBoFromDataTableStr(borrowRate);
		BigDecimal mouthRate = borrowRateBo.getRate().divide(new BigDecimal(Constants.MONTH_OF_YEAR), 8,
				BigDecimal.ROUND_HALF_UP);//月利率
		//每期本金
		BigDecimal principleAmount = money.divide(new BigDecimal(borrow.getNper()), 2, RoundingMode.DOWN);
		//第一期本金
		BigDecimal firstPrincipleAmount =  getFirstPrincipleAmount(money, principleAmount, nper);
		//每期利息
		BigDecimal interestAmount = money.multiply(mouthRate);
		//每期手续费
		BigDecimal poundageAmount = BigDecimalUtil.getPerPoundage(money, borrow.getNper(), borrowRateBo.getPoundageRate(), borrowRateBo.getRangeBegin(), borrowRateBo.getRangeEnd(), freeNper);

		AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(borrow.getUserId());


		AfBorrowBillDo afBorrowBillLast = afBorrowBillDao.getLastOutBill(borrow.getUserId());
		int out_day = 10;
		int pay_day = 20;
		if(afUserOutDayDo !=null){
			out_day = afUserOutDayDo.getOutDay();
			pay_day = afUserOutDayDo.getPayDay();
		}

		Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
				out_day - 1);
		if(startDate.before(now)){
			now = DateUtil.addMonths(now,1);
		}

		if(afBorrowBillLast !=null){
			if(getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue() != getYearMonth(afBorrowBillLast.getGmtPayTime()).intValue()){
				//己出，且跨月
				Date d=  getNowOutDay(now,out_day);
				if(pay_day<out_day){
					//只比较出帐日
					if( getYearMonth(d).intValue() <= getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue()){
						now = DateUtil.addMonths(now,1);
						logger.info("billMonth plus 1 first now:"+now );
					}
				}
				else{
					//比较还款日
					int oldPayDate = getYearMonth(afBorrowBillLast.getGmtPayTime()).intValue();
					int newPayDate = getYearMonth(now).intValue();
					if( oldPayDate>=newPayDate){
						Integer [] _oldPay =  getYearMonthV(afBorrowBillLast.getGmtPayTime());
						Calendar c = Calendar.getInstance();
						c.setTime(now);
						c.set(Calendar.YEAR,_oldPay[0]);
						c.set(Calendar.MONTH,_oldPay[1]-1);
						now = c.getTime();
						now = DateUtil.addMonths(now,1);
						logger.info("billMonth plus 1 first now:"+now );
					}
				}

			}else {
				//只比较出帐日
				Date d = getNowOutDay(now, out_day);
				if (getYearMonth(d).intValue() <= getYearMonth(afBorrowBillLast.getGmtOutDay()).intValue()) {
					now = DateUtil.addMonths(now, 1);
					logger.info("billMonth plus 1 first now:" + now);
				}
			}
		}
		logger.info("check billMonth time "+now);
		logger.info("check billMonth time yearAndMonth "+getYearMonth(now));



		for (int i = 1; i <= nper; i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setBorrowNo(borrow.getBorrowNo());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(borrow.getGmtCreate());
			Map<String, Object> timeMap = getCurrentYearAndMonth(now,afUserOutDayDo);
			bill.setBillYear((Integer) timeMap.get(Constants.DEFAULT_YEAR));
			bill.setBillMonth((Integer) timeMap.get(Constants.DEFAULT_MONTH));
			bill.setGmtOutDay((Date) timeMap.get(Constants.OUT_DATETIME));
			bill.setGmtPayTime((Date) timeMap.get(Constants.PAY_DATETIME));
			bill.setNper(borrow.getNper());
			bill.setBillNper(i);
			if(borrow.getType().equals(BorrowType.LEASE.getCode())){
				bill.setInterestAmount(BigDecimal.ZERO);
				bill.setIsFreeInterest(YesNoStatus.YES.getCode());
				bill.setPoundageAmount(BigDecimal.ZERO);
				bill.setPrincipleAmount(borrow.getNperAmount());
				bill.setBillAmount(borrow.getNperAmount());
				bill.setType(BorrowType.LEASE.getCode());
			}
			else {
				if (i <= freeNper) {
					bill.setInterestAmount(BigDecimal.ZERO);
					bill.setIsFreeInterest(YesNoStatus.YES.getCode());
					bill.setPoundageAmount(BigDecimal.ZERO);
				} else {
					bill.setInterestAmount(interestAmount);
					bill.setIsFreeInterest(YesNoStatus.NO.getCode());
					bill.setPoundageAmount(poundageAmount);
				}
				if (i == 1) {
					bill.setPrincipleAmount(firstPrincipleAmount);
				} else {
					bill.setPrincipleAmount(principleAmount);
				}
				bill.setBillAmount(BigDecimalUtil.add(bill.getInterestAmount(),bill.getPoundageAmount(),bill.getPrincipleAmount()));
				if (StringUtil.equals(payType, PayType.COMBINATION_PAY.getCode())) {
					bill.setStatus(BorrowBillStatus.FORBIDDEN.getCode());
				} else {
					bill.setStatus(BorrowBillStatus.NO.getCode());
				}
				bill.setType(BorrowType.CONSUME.getCode());
			}
			list.add(bill);
			now = DateUtil.addMonths(now, 1);
		}
		return list;
	}

	private Integer getYearMonth(Date date){
		String[] billDay1 = DateUtil.formatDate(date, DateUtil.MONTH_PATTERN).split("-");
		return Integer.parseInt(billDay1[0]+billDay1[1]);
	}
	private Integer[] getYearMonthV(Date date){
		String[] billDay1 = DateUtil.formatDate(date, DateUtil.MONTH_PATTERN).split("-");
		Integer [] out = new Integer[2];
		out[0] = Integer.parseInt(billDay1[0]);
		out[1] = Integer.parseInt(billDay1[1]);
		return out;
	}

	private Date getPayDate(int day,Date time){
		Calendar c = Calendar.getInstance();
		c.setTime(time);
		c.set(Calendar.DAY_OF_MONTH,day);
		c.set(Calendar.HOUR_OF_DAY,23);
		c.set(Calendar.MINUTE,59);
		c.set(Calendar.SECOND,59);
		return c.getTime();
	}

	private Date getNowOutDay(Date d, int day){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH,day);
		c.set(Calendar.HOUR_OF_DAY,0);
		c.set(Calendar.MINUTE,0);
		c.set(Calendar.SECOND,0);
		c.set(Calendar.MILLISECOND,0);
		return c.getTime();
	}

	/**
	 * 获取第一期分期金额
	 * @param amount 本金
	 * @param principleAmount 每期金额
	 * @param nper分期数
	 * @return
	 */
	private BigDecimal getFirstPrincipleAmount(BigDecimal amount, BigDecimal principleAmount, Integer nper) {
		//剩余期数本金之和
		BigDecimal tempAmount = principleAmount.multiply(new BigDecimal(nper - 1));
		return amount.subtract(tempAmount);
	}

	private AfBorrowInterestDo buildBorrowInterest(Long billId, BigDecimal interest, String creator, BigDecimal money) {
		AfBorrowInterestDo borrow = new AfBorrowInterestDo();
		// 利息日志
		borrow.setGmtCreate(new Date());
		borrow.setBillId(billId);
		borrow.setInterest(interest);
		borrow.setCreator(creator);
		borrow.setPrincipleAmount(money);
		return borrow;
	}

	@Override
	public void consumeBillTransfer(final AfBorrowDo borrow, final AfUserAccountDo userDto) {
		transactionTemplate.execute(new TransactionCallback<Object>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					// 获取借款分期配置信息
					AfResourceDo resource = (AfResourceDo) bizCacheUtil.getObject(Constants.CACHEKEY_BORROW_CONSUME);
					if (null == resource) {
						resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
								Constants.RES_BORROW_CONSUME);
						bizCacheUtil.saveObject(Constants.CACHEKEY_BORROW_CONSUME, resource,
								Constants.SECOND_OF_HALF_HOUR);
					}
					BigDecimal money = borrow.getAmount();// 借款金额
					BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN,
							BigDecimal.ZERO);
					BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX,
							BigDecimal.ZERO);
					String[] range = StringUtil.split(resource.getValue2(), ",");
					if (null != range && range.length == 2) {
						rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
						rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
					}

					JSONArray array = JSON.parseArray(resource.getValue());
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj = array.getJSONObject(i);
						if (obj.getInteger(Constants.DEFAULT_NPER) == borrow.getNper()) {
							BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(money, borrow.getNper(),
									new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd,InterestfreeCode.NO_FREE.getCode());// 总手续费
							BigDecimal perAmount = BigDecimalUtil.getConsumeAmount(money, borrow.getNper(),
									new BigDecimal(obj.getString(Constants.DEFAULT_RATE)).divide(
											new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
									totalPoundage);// 每期账单金额
							// 总账单金额
							BigDecimal totalBillAMount = BigDecimalUtil.getConsumeTotalAmount(money, borrow.getNper(),
									new BigDecimal(obj.getString(Constants.DEFAULT_RATE)).divide(
											new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
									totalPoundage);
							List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CONSUME, borrow, perAmount,
									totalBillAMount, BigDecimal.ZERO,
									new BigDecimal(obj.getString("rate")).divide(
											new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
									totalPoundage, BorrowBillStatus.FORBIDDEN);
							// 新增借款账单
							afBorrowDao.addBorrowBill(billList);
							afBorrowDao.updateBorrowStatus(borrow.getRid(), BorrowStatus.TRANSED.getCode());
							pushService.dealBorrowConsumeTransfer(userDto.getUserName(), borrow.getName());

							//#region 修改最是否己借款  add by hongzhengpei
//							if(borrow.getStatus().equals("TRANSED")) {
//								afRecommendUserService.updateRecommendByBorrow(borrow.getUserId(), borrow.getGmtCreate());
//							}
							//#endregion
						}
					}
				} catch (Exception e) {
					logger.info("create consume bill error:", e);
				}
				return null;
			}
		});
		kafkaSync.syncEvent(borrow.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);

	}

	@Override
	public int updateBorrowStatus(Long id, String status) {
		return afBorrowDao.updateBorrowStatus(id, status);
	}
	


	@Override
	public AfBorrowDo getBorrowByOrderId(Long orderId) {
		return afBorrowDao.getBorrowByOrderId(orderId);
	}

	@Override
	public BigDecimal calculateBorrowAmount(Long borrowId, BigDecimal refundAmount, boolean refundByUser) {
		// 借款金额+借款金额*退款日利率*（退款日期-借款日期+1）*（0,1）- 退款金额- 已还账单和 + 优惠和
		logger.info(" calculateBorrowRefundAmount begin borrowId = {}", borrowId);
		AfBorrowDo borrowInfo = afBorrowDao.getBorrowById(borrowId);
		// 借款金额
		BigDecimal borrowAmount = borrowInfo.getAmount();

		Calendar c1 = Calendar.getInstance();
		c1.setTime(borrowInfo.getGmtCreate());
		Calendar c2 = Calendar.getInstance();
		c2.setTime(new Date());
		// 借款天数
		long days = DateUtil.getNumberOfDaysBetween(c1, c2) + 1;

		AfResourceDo refundResourceInfo = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,
				Constants.RES_REFUND_RATE);
		// 退款利率
		BigDecimal refundRate = new BigDecimal(refundResourceInfo.getValue());
		// 已经还账单和-优惠和
		BigDecimal repaymentAndCouponAmount = calculateRepaymentAndCouponAmount(borrowId);


		//modifed by hongzhengpei  郑洪军说不收退款利率钱。有录音为证，但录音只保存7天。
		return BigDecimalUtil.add(borrowAmount,  BigDecimal.ZERO,
				BigDecimalUtil.multiply(refundAmount, new BigDecimal("-1")), BigDecimalUtil.multiply(repaymentAndCouponAmount, new BigDecimal("-1")));

//        return BigDecimalUtil.add(borrowAmount, refundByUser ? BigDecimalUtil.multiply(borrowAmount, new BigDecimal(days), refundRate) : BigDecimal.ZERO,
//                BigDecimalUtil.multiply(refundAmount, new BigDecimal("-1")), BigDecimalUtil.multiply(repaymentAndCouponAmount, new BigDecimal("-1")));


	}


	/**
	 * 计算该笔账单在还款中的实际还款金额
	 *
	 * @param repayment
	 * @param billInfo
	 * @return
	 */
	private BigDecimal calculateRepaymentCouponAmount(AfRepaymentDo repayment, AfBorrowBillDo billInfo,BigDecimal allAmount) {
		logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}", new Object[]{repayment, billInfo});
		BigDecimal couponAmount = repayment.getCouponAmount();
		BigDecimal rate = BigDecimalUtil.divide(billInfo.getBillAmount(), repayment.getRepaymentAmount());
//        BigDecimal result = billInfo.getBillAmount().subtract(BigDecimalUtil.multiply(rate, couponAmount));

		BigDecimal actualAmount = BigDecimalUtil.add(repayment.getActualAmount(),repayment.getRebateAmount());
		BigDecimal result = billInfo.getBillAmount().multiply(actualAmount);
//		BigDecimal result = billInfo.getBillAmount().multiply(repayment.getActualAmount());
		result = BigDecimalUtil.divide(result,allAmount);

		//result = result.subtract(BigDecimalUtil.multiply(rate, couponAmount));

		logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{rate, billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
		return result;
	}


	/**
	 * 计算该笔账单在还款中的实际还款金额
	 *
	 * @param repayment
	 * @param billInfo
	 * @return
	 */
	private BigDecimal calculateRepaymentCouponAmount1(AfRepaymentDo repayment, AfBorrowBillDo billInfo,BigDecimal allAmount) {
		logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}", new Object[]{repayment, billInfo});
		//BigDecimal rate1 = BigDecimalUtil.divide(billInfo.getBillAmount(),allAmount);
		BigDecimal actualAmount = BigDecimalUtil.add(repayment.getActualAmount(),repayment.getRebateAmount());
		BigDecimal result = billInfo.getBillAmount().multiply(actualAmount);
//		BigDecimal result = billInfo.getBillAmount().multiply(repayment.getActualAmount());
		result = BigDecimalUtil.divide(result,allAmount);
		logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}", new Object[]{ billInfo.getBillAmount(), repayment.getRepaymentAmount(), result});
		return result;
	}

	@Resource
	AfBorrowBillService afBorrowBillService;
	/**
	 * 计算还款金额以及优惠金额
	 * 
	 * @param borrowId
	 * @return
	 */
	private BigDecimal calculateRepaymentAndCouponAmount(Long borrowId) {
		List<AfBorrowBillDo> repaymentedBillList = afBorrowBillDao.getBillListByBorrowIdAndStatus(borrowId,
				BorrowBillStatus.YES.getCode());
		BigDecimal totalAmount = BigDecimal.ZERO;
		if (CollectionUtils.isEmpty(repaymentedBillList)) {
			// 没有还款记录
			return totalAmount;
		} else {
			logger.info("billList = {}", repaymentedBillList);
			List<Long> repaymentIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList,
					new Converter<AfBorrowBillDo, Long>() {
						@Override
						public Long convert(AfBorrowBillDo source) {
							return source.getRepaymentId();
						}
					});
			logger.info("repaymentIds = {}", repaymentIds);
			List<Long> billIds = CollectionConverterUtil.convertToListFromList(repaymentedBillList,
					new Converter<AfBorrowBillDo, Long>() {
						@Override
						public Long convert(AfBorrowBillDo source) {
							return source.getRid();
						}
					});

			List<AfRepaymentDo> repaymentList = afRepaymentDao.getRepaymentListByIds(repaymentIds);
			for (AfRepaymentDo repayment : repaymentList) {
				List<Long> repaymentBillLists = CollectionConverterUtil
						.convertToListFromArray(repayment.getBillIds().split(","), new Converter<String, Long>() {
							@Override
							public Long convert(String source) {
								return Long.parseLong(source);
							}
						});

				List<AfBorrowBillDo> listDo = afBorrowBillService.getBorrowBillByIds(repaymentBillLists);
				BigDecimal allAmount = BigDecimal.ZERO;
				for (AfBorrowBillDo afBorrowBillDo : listDo){
					allAmount = allAmount.add(afBorrowBillDo.getBillAmount());
				}

				for (Long billId : billIds) {
					if (repaymentBillLists.contains(billId)) {
						AfBorrowBillDo billInfo = getBillFromList(repaymentedBillList, billId);
						if (repayment.getUserCouponId() == 0l) {
							// 没有优惠券,则按照账单金额来
							//totalAmount = billInfo.getBillAmount();
							totalAmount = BigDecimalUtil.add(totalAmount,calculateRepaymentCouponAmount1(repayment,billInfo,allAmount));
							continue;
						} else {
							// 有优惠券
							totalAmount = BigDecimalUtil.add(totalAmount,calculateRepaymentCouponAmount1(repayment,billInfo,allAmount));
							//修改退款时有优惠倦的逻辑，洪军要改的
							//totalAmount = BigDecimalUtil.add(totalAmount, calculateRepaymentCouponAmount(repayment, billInfo));
							
//							if (repaymentBillLists.indexOf(billId) != repaymentBillLists.size() - 1) {
//								// 不是最后一个记录，则按照百分比计算
//								logger.info(" is not last one");
//								totalAmount = BigDecimalUtil.add(totalAmount,
//										calculateRepaymentCouponAmount(repayment, billInfo));
//								continue;
//							} else {
//								// 如果是最后一个，则先减去前面的还款记录
//								BigDecimal tempAmount = BigDecimal.ZERO;
//								logger.info(" is last one");
//								List<AfBorrowBillDo> tempBillList = afBorrowBillDao
//										.getBillListByIds(repaymentBillLists);
//								// 只有一个
//								if (repaymentBillLists.size() == 1) {
//									totalAmount = calculateRepaymentCouponAmount(repayment, billInfo);
//								} else {
//									for (int i = 0; i < repaymentBillLists.size() - 1; i++) {
//										AfBorrowBillDo tempBillInfo = getBillFromList(tempBillList,
//												repaymentBillLists.get(i));
//										tempAmount = BigDecimalUtil.add(tempAmount,
//												calculateRepaymentCouponAmount(repayment, tempBillInfo));
//									}
//									BigDecimal finalRepaymentActualAmount = BigDecimalUtil.add(
//											repayment.getActualAmount(),
//											BigDecimalUtil.divide(repayment.getJfbAmount(), new BigDecimal("100")),
//											repayment.getRebateAmount());
//									totalAmount = BigDecimalUtil.add(totalAmount,
//											BigDecimalUtil.subtract(finalRepaymentActualAmount, tempAmount));
//								}
//								continue;
//							}
						}
					}
				}
			}
			return totalAmount;
		}
	}

	/**
	 * 计算该笔账单在还款中的实际还款金额
	 * 
	 * @param repayment
	 * @param billInfo
	 * @return
	 */
	private BigDecimal calculateRepaymentCouponAmount(AfRepaymentDo repayment, AfBorrowBillDo billInfo) {
		logger.info("calculateRepaymentCouponAmount begin  repayment = {}, billInfo = {}",
				new Object[] { repayment, billInfo });
		BigDecimal couponAmount = repayment.getCouponAmount();
		BigDecimal rate = BigDecimalUtil.divide(billInfo.getBillAmount(), repayment.getRepaymentAmount());
		BigDecimal result = billInfo.getBillAmount().subtract(BigDecimalUtil.multiply(rate, couponAmount));
		logger.info("rate = {}, billAmount = {} repaymentAmount = {} result = {}",
				new Object[] { rate, billInfo.getBillAmount(), repayment.getRepaymentAmount(), result });
		return result;
	}

	private AfBorrowBillDo getBillFromList(List<AfBorrowBillDo> billList, Long billId) {
		if (CollectionUtils.isEmpty(billList)) {
			return null;
		}
		for (AfBorrowBillDo billInfo : billList) {
			if (billInfo.getRid().equals(billId)) {
				return billInfo;
			}
		}
		return null;
	}

	@Override
	public AfBorrowDo getBorrowByOrderIdAndStatus(Long orderId, String status) {
		return afBorrowDao.getBorrowByOrderIdAndStatus(orderId, status);
	}

	@Override
	public long dealAgentPayClose(final AfUserAccountDo userDto, final BigDecimal amount, final Long orderId) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					// 修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUsedAmount(amount);
					account.setUserId(userDto.getUserId());
					afUserAccountDao.updateUserAccount(account);

					return 1l;
				} catch (Exception e) {
					logger.info("dealAgentPayClose error:" + e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}
	@Resource
	AfBorrowExtendDao afBorrowExtendDao;

	@Override
	public Long dealAgentPayBorrowAndBill(final AfBorrowDo borrow, final Long userId, final String userName, final BigDecimal amount,final String payType,final String orderType) {
		Long resultValue = 0L;
		resultValue =  transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
//					if (!StringUtil.equals(payType, PayType.COMBINATION_PAY.getCode())) {
//						afBorrowDao.updateBorrowStatus(borrow.getRid(), BorrowStatus.TRANSED.getCode());
//					}
//					// 直接打款
//					afBorrowLogDao.addBorrowLog(buildBorrowLog(userName, userId, borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));
//
//					// 新增借款日志
//					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME, amount, userId, borrow.getRid()));




//					if(!(orderType.equals(OrderType.AGENTBUY.getCode()) ||orderType.equals(OrderType.BOLUOME.getCode()) || orderType.equals(OrderType.BOLUOMECP.getCode()))){
//					if(!(orderType.equals(OrderType.BOLUOME.getCode()) || orderType.equals(OrderType.BOLUOMECP.getCode()))){

						AfBorrowExtendDo afBorrowExtendDo = new AfBorrowExtendDo();
						afBorrowExtendDo.setId(borrow.getRid());
						afBorrowExtendDo.setInBill(1);
						afBorrowExtendDao.updateBorrowExtend(afBorrowExtendDo);
						List<AfBorrowBillDo> billList = buildBorrowBillForNewInterest(borrow, payType);
						afBorrowDao.addBorrowBill(billList);
//					}

					return borrow.getRid();

				} catch (Exception e) {
					logger.info("dealAgentPayConsumeRisk error:" + e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});

		/*if(resultValue > 0L){
			try {
				afContractPdfCreateService.protocolInstalment(borrow.getUserId(),borrow.getNper(),borrow.getAmount(),borrow.getRid());
				logger.info("protocolInstalment finish，borrowId=" + borrow.getRid());
			} catch (Exception e) {
				logger.error("protocolInstalment error，borrowId=" + borrow.getRid(),e);
			}
		}*/
		if (0l !=resultValue){
			kafkaSync.syncEvent(borrow.getUserId(),KafkaConstants.SYNC_SCENE_ONE,true);
			afBorrowDao.updateBorrowVersion(borrow.getRid(),1);
//			contractPdfThreadPool.protocolInstalmentPdf(borrow.getUserId(),borrow.getNper(),borrow.getAmount(),borrow.getRid());
		}
        return resultValue;
	}


	public Long updateBorrowStatus(AfBorrowDo borrow,String userName,long userId){
		//if (!StringUtil.equals(payType, PayType.COMBINATION_PAY.getCode())) {
			afBorrowDao.updateBorrowStatus(borrow.getRid(), BorrowStatus.TRANSED.getCode());
		//}
		// 直接打款
		afBorrowLogDao.addBorrowLog(buildBorrowLog(userName, userId, borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));

		// 新增借款日志
		afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME, borrow.getAmount(), userId, borrow.getRid()));
		return  borrow.getRid();
	}



	
	@Override
	public Long dealAgentPayBorrowAndBill(final Long userId, final String userName, final BigDecimal amount,final String name,
			final Integer nper, final Long orderId,final String orderNo, final String borrowRate,final String interestFreeJson,final boolean isBack) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					AfBorrowDo borrow = buildAgentPayBorrow(name, BorrowType.TOCONSUME, userId,
							amount, nper, BigDecimal.ZERO, BorrowStatus.TRANSED.getCode(), orderId, orderNo,borrowRate, interestFreeJson);
					// 新增借款信息
					afBorrowDao.addBorrow(borrow);
					// 直接打款
					afBorrowLogDao.addBorrowLog(buildBorrowLog(userName, userId, borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));
					// 新增借款日志
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME, amount, userId, borrow.getRid()));

					List<AfBorrowBillDo> billList = buildBorrowBillForNewInterest(borrow, PayType.AGENT_PAY.getCode());
					afBorrowDao.addBorrowBill(billList);

					return borrow.getRid();

				} catch (Exception e) {
					logger.info("dealAgentPayConsumeRisk error: {}",e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}
	
	@Override
	public int getBorrowNumByUserId(Long userId) {
		return afBorrowDao.getBorrowNumByUserId(userId);
	}

	@Override
	public Long dealAgentPayConsumeRisk(final AfUserAccountDo userDto, final BigDecimal amount,final String name,
			final int nper, final Long orderId,final String orderNo, final Integer totalNper) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					Integer realTotalNper = totalNper == null ? nper : totalNper;

					JSONObject borrowRate = borrowRateWithOrder(orderId,realTotalNper);
					
					
					BigDecimal money = amount;//借款金额
					
					
					BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(money, nper,
							borrowRate.getBigDecimal("poundageRate") ,borrowRate.getBigDecimal("rangeBegin") ,borrowRate.getBigDecimal("rangeEnd") );// 总手续费
					BigDecimal perAmount = BigDecimalUtil.getConsumeAmount(money, nper,
							new BigDecimal(borrowRate.getString(Constants.DEFAULT_RATE)).divide(
									new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
							totalPoundage);// 每期账单金额
					AfBorrowDo borrow = buildAgentPayBorrow(name, BorrowType.TOCONSUME, userDto.getUserId(),
							amount, nper, perAmount, BorrowStatus.TRANSED.getCode(), orderId, orderNo);
					// 新增借款信息
					afBorrowDao.addBorrow(borrow);
					// 直接打款
					afBorrowLogDao.addBorrowLog(buildBorrowLog(userDto.getUserName(), userDto.getUserId(),
							borrow.getRid(), BorrowLogStatus.TRANSED.getCode()));
					// 新增借款日志
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME,
							amount, userDto.getUserId(), borrow.getRid()));

					// 总账单金额
					BigDecimal totalBillAMount = BigDecimalUtil.getConsumeTotalAmount(money, borrow.getNper(),
							new BigDecimal(borrowRate.getString(Constants.DEFAULT_RATE)).divide(
									new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
							totalPoundage);
					List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CONSUME, borrow, perAmount,
							totalBillAMount, BigDecimal.ZERO,
							new BigDecimal(borrowRate.getString("rate")).divide(
									new BigDecimal(Constants.MONTH_OF_YEAR), 8, BigDecimal.ROUND_HALF_UP),
							totalPoundage, BorrowBillStatus.NO);
					// 新增借款账单
					afBorrowDao.addBorrowBill(billList);

					return borrow.getRid();

				} catch (Exception e) {
					logger.info("dealAgentPayConsumeRisk error:" + e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
		
	}

	/**
	 * 
	 * @param userId
	 * @param money
	 *            -- 借款金额
	 * @return
	 */
	private AfBorrowDo buildAgentPayBorrow(String name, BorrowType type, Long userId, BigDecimal money, int nper,
			BigDecimal perAmount, String status, Long orderId, String orderNo) {
		Date currDate = new Date();
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setGmtCreate(currDate);
		borrow.setAmount(money);
		borrow.setType(type.getCode());
		borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
		borrow.setStatus(status);// 默认转账成功
		borrow.setName(name);
		borrow.setUserId(userId);
		borrow.setNper(nper);
		borrow.setNperAmount(perAmount);
		borrow.setCardNumber(StringUtils.EMPTY);
		borrow.setCardName("代付");
		borrow.setRemark(name);
		borrow.setOrderId(orderId);
		borrow.setOrderNo(orderNo);
		return borrow;
	}
	
	public JSONObject borrowRateWithOrder(Long orderId,Integer nper){
		AfOrderDo order = afOrderService.getOrderById(orderId);

		JSONObject borrowRate =null;

		if(StringUtils.equals(order.getOrderType(), OrderType.AGENTBUY.getCode())&&order.getNper()>0){
			AfAgentOrderDo agentOrderDo = afAgentOrderService.getAgentOrderByOrderId(orderId);

			
			borrowRate = JSON.parseObject(agentOrderDo.getBorrowRate()) ;
		}
		if(borrowRate==null){
			borrowRate = afResourceService.borrowRateWithResourceOld(nper);
		}
		return borrowRate;

	}

	@Override
	public AfBorrowDo getBorrowInfoByBorrowNo(String borrowNo) {
		return afBorrowDao.getBorrowInfoByBorrowNo(borrowNo);
	}

	@Override
	public int addBorrowBill(List<AfBorrowBillDo> billList){
		return afBorrowDao.addBorrowBill(billList);
	}

	public List<AfBorrowBillDo> getBorrowBillList(String status, Long userId){
		return afBorrowBillDao.getBorrowBillList(status,userId);
	}

	public List<AfBorrowBillDo> getBorrowBillListY( Long userId,  Integer billYear, Integer billMonth){
		return  afBorrowBillDao.getBorrowBillListY(userId,billYear,billMonth);
	}


	public HashMap addHomeBorrow(final Long orderId,final int nper, final Long userId,Date date) {
		int borrowNper = nper;
		Date now = date;
		if(nper == 5 || nper ==11){
			now = DateUtil.addMonths(now,1);
			if(nper ==5){
				borrowNper = 6;
			}else{
				borrowNper = 12;
			}
		}
		AfOrderDo afOrderDo = afOrderDao.getOrderById(orderId);
		AfBorrowDo borrow = afOrderService.buildAgentPayBorrow(afOrderDo.getGoodsName(), BorrowType.HOME_CONSUME, userId, afOrderDo.getActualAmount(),
				borrowNper, BorrowStatus.APPLY.getCode(), orderId, afOrderDo.getOrderNo(), afOrderDo.getBorrowRate(), afOrderDo.getInterestFreeJson(),
				afOrderDo.getOrderType(), Constants.ORDER_TYPE_TENEMENT);

		borrow.setNper(nper);

		List<AfBorrowBillDo> list = buildBorrowBillForNewInterestForHouse(borrow,PayType.AGENT_PAY.getCode(),now);
		HashMap<String ,Object> map = new HashMap<String,Object>();
		map.put("borrow",borrow);
		map.put("bill",list);
		//return JSON.toJSONString(map);
		return map;
	}
	
	/**
     * 适配规则利率（1，2，3，6，9，12期）
     * @param realNper
     * @return
     */
    private int getTempNperForBorrowRate(int realNper){
    	int tempNper = realNper;
    	if(Constants.FOUR == realNper || Constants.FIVE == realNper){
    		tempNper = Constants.THREE;
    	}
    	else if(Constants.SEVEN == realNper || Constants.EIGHT == realNper){
    		tempNper = Constants.SIX;
    	}
    	else if(Constants.TEN == realNper || Constants.ELEVEN == realNper){
    		tempNper = Constants.NINE;
    	}
    	
    	return tempNper;
    }

	public int addHomeBorrow(final Long orderId,final int nper){
		return 1;
	}


	/**
	 *
	 * @param borrow 借款信息
	 * @return
	 */
	public List<AfBorrowBillDo> buildBorrowBillForNewInterestForHouse(AfBorrowDo borrow, String payType,Date now) {
		List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
		//Date now = new Date();// 当前时间
		//生成时间
		logger.info("check now time"+now);
		Integer nper = borrow.getNper();
		Integer freeNper = borrow.getFreeNper();
		String borrowRate = borrow.getBorrowRate();
		BigDecimal money = borrow.getAmount();// 借款金额

		//拿到日利率快照Bo
		BorrowRateBo borrowRateBo =  BorrowRateBoUtil.parseToBoFromDataTableStr(borrowRate);
		BigDecimal mouthRate = borrowRateBo.getRate().divide(new BigDecimal(Constants.MONTH_OF_YEAR), 8,
				BigDecimal.ROUND_HALF_UP);//月利率
		//每期本金
		BigDecimal principleAmount = money.divide(new BigDecimal(borrow.getNper()), 2, RoundingMode.DOWN);
		//第一期本金
		BigDecimal firstPrincipleAmount =  getFirstPrincipleAmount(money, principleAmount, nper);
		//每期利息
		BigDecimal interestAmount = money.multiply(mouthRate);
		//每期手续费
		BigDecimal poundageAmount = BigDecimalUtil.getPerPoundage(money, borrow.getNper(), borrowRateBo.getPoundageRate(), borrowRateBo.getRangeBegin(), borrowRateBo.getRangeEnd(), freeNper);

		AfUserOutDayDo afUserOutDayDo =  afUserOutDayDao.getUserOutDayByUserId(borrow.getUserId());


//		AfBorrowBillDo afBorrowBillLast = afBorrowBillDao.getLastOutBill(borrow.getUserId());
//		int out_day = 10;
//		int pay_day = 20;
//		if(afUserOutDayDo !=null){
//			out_day = afUserOutDayDo.getOutDay();
//			pay_day = afUserOutDayDo.getPayDay();
//		}

//		Date startDate = DateUtil.addDays(DateUtil.getStartOfDate(DateUtil.getFirstOfMonth(now)),
//				out_day - 1);
//		if(startDate.before(now)){
//			now = DateUtil.addMonths(now,1);
//		}

		Date __now = new Date();
		for (int i = 1; i <= nper; i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setBorrowNo(borrow.getBorrowNo());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(borrow.getGmtCreate());
			Map<String, Object> timeMap = getCurrentYearAndMonth(now,afUserOutDayDo);
			bill.setBillYear((Integer) timeMap.get(Constants.DEFAULT_YEAR));
			bill.setBillMonth((Integer) timeMap.get(Constants.DEFAULT_MONTH));
			bill.setGmtOutDay((Date) timeMap.get(Constants.OUT_DATETIME));
			bill.setGmtPayTime((Date) timeMap.get(Constants.PAY_DATETIME));
			bill.setNper(borrow.getNper());
			bill.setBillNper(i);
			if (i <= freeNper) {
				bill.setInterestAmount(BigDecimal.ZERO);
				bill.setIsFreeInterest(YesNoStatus.YES.getCode());
				bill.setPoundageAmount(BigDecimal.ZERO);
			} else {
				bill.setInterestAmount(interestAmount);
				bill.setIsFreeInterest(YesNoStatus.NO.getCode());
				bill.setPoundageAmount(poundageAmount);
			}
			if (i == 1) {
				bill.setPrincipleAmount(firstPrincipleAmount);
			} else {
				bill.setPrincipleAmount(principleAmount);
			}
			bill.setBillAmount(BigDecimalUtil.add(bill.getInterestAmount(),bill.getPoundageAmount(),bill.getPrincipleAmount()));
			if (StringUtil.equals(payType, PayType.COMBINATION_PAY.getCode())) {
				bill.setStatus(BorrowBillStatus.FORBIDDEN.getCode());
			} else {
				bill.setStatus(BorrowBillStatus.NO.getCode());
			}
			bill.setType(BorrowType.HOME_CONSUME.getCode());
			if(bill.getGmtOutDay().compareTo(__now)<=0){
				bill.setIsOut(1);
			}
			list.add(bill);
			now = DateUtil.addMonths(now, 1);
		}
		return list;
	}

	@Override
	public List<AfBorrowDto> getUserNotInBorrow(Long userId) {
		return afBorrowDao.getUserNotInBorrow(userId);
	}

	@Override
	public int getUserNotInBorrowCount(Long userId) {
		return afBorrowDao.getUserNotInBorrowCount(userId);
	}

	@Override
	public BigDecimal getUserNotInBorrowMoney(Long userId) {
		return afBorrowDao.getUserNotInBorrowMoney(userId);
	}

	@Override
	public Integer countNperRepaymentByBorrowId(Long rid) {
		return afBorrowDao.countNperRepaymentByBorrowId(rid);
	}
	@Override
	public AfBorrowDto getBorrowInfoById(Long borrowId) {
		return afBorrowDao.getBorrowInfoById(borrowId);
	}
	
	@Override
	public AfBorrowDo getOverdueBorrowInfoByUserId(Long userId) {
		// TODO Auto-generated method stub
		return afBorrowDao.getOverdueBorrowInfoByUserId(userId);
	}
}
