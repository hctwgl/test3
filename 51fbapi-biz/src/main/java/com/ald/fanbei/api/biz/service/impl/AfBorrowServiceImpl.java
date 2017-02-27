package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.JpushService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfBorrowInterestDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfBorrowInterestDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserAccountDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午4:51:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowService")
public class AfBorrowServiceImpl extends BaseService implements AfBorrowService{

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
	AfBorrowInterestDao afBorrowInterestDao;
	
	@Resource
	private JpushService pushService;
	
	@Override
	public Date getReyLimitDate(Date now){
    	Date startDate = DateUtil.addDays(DateUtil.getFirstOfMonth(now), 
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10)-1);
		Date limitTime = DateUtil.getEndOfDate(DateUtil.addDays(DateUtil.getFirstOfMonth(now), 
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_REPAY_TIME), 20)-1));
		if(startDate.after(now)){
			limitTime = DateUtil.addMonths(limitTime, -1);
		}
		return limitTime;
    }

	@Override
	public long dealCashApply(final AfUserAccountDto userDto,final BigDecimal money,
			final Long cardId) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					Date now = new Date();
					//TODO 转账处理
					pushService.dealBorrowCashTransfer(userDto.getUserName(), now);
					//修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userDto.getUserId());
					account.setUcAmount(money);//已取现金额=已取现金额+申请取现金额
					account.setUsedAmount(money);//授信已使用金额=授信已使用金额+申请取现金额
					afUserAccountDao.updateUserAccount(account);					
					AfBorrowDo borrow =  buildBorrow(Constants.DEFAULT_BORROW_CASH_NAME,BorrowType.CASH,userDto.getUserId(), money,cardId,null,null,1,money);
					afBorrowDao.addBorrow(borrow);
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CASH,money, userDto.getUserId(), borrow.getRid()));
					
					//生成账单
					AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,Constants.RES_BORROW_CASH);
					if(null == resource){
						throw new Exception("现金借款利率配置不能为空");
					}else{
						BigDecimal money = borrow.getAmount();//借款金额
						BigDecimal dayRate = NumberUtil.objToBigDecimalDefault(resource.getValue(), BigDecimal.ZERO);//取现日利率
						BigDecimal serviceCharge = NumberUtil.objToBigDecimalDefault(resource.getValue1(), BigDecimal.ZERO);//取现手续费率
						BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
						BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
						String[] range = StringUtil.split(resource.getValue2(), ",");
						if(null != range && range.length==2){
							rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
							rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
						}
						BigDecimal chargeAmount = money.multiply(serviceCharge);//计算手续费
						if(rangeBegin.compareTo(chargeAmount)>0){
							chargeAmount = rangeBegin;
						}else if(rangeEnd.compareTo(chargeAmount)==-1){
							chargeAmount = rangeEnd;
						}
						BigDecimal interestAmount = money.multiply(dayRate);//日利息
						List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CASH,borrow,money.add(interestAmount).add(chargeAmount),interestAmount,BigDecimal.ZERO,chargeAmount);
						if(null!=billList&&billList.size()>0){
							AfBorrowBillDo cashBill = billList.get(0);
							//生成利息日志
							afBorrowDao.addBorrowBillInfo(cashBill);
							afBorrowInterestDao.addBorrowInterest(buildBorrowInterest(cashBill.getRid(), interestAmount,userDto.getUserName(),money));
						}
					}
					return borrow.getRid();
				} catch (Exception e) {
					logger.info("dealCashApply error:"+e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}
	
	/**
	 * 
	 * @param userId
	 * @param money -- 借款金额
	 * @return
	 */
	private AfBorrowDo buildBorrow(String name,BorrowType type,Long userId,BigDecimal money,Long cardId,Long goodsId,String openId,int nper,BigDecimal perAmount){
		Date currDate = new Date();
		AfBorrowDo borrow = new AfBorrowDo();
		borrow.setGmtCreate(currDate);
		borrow.setAmount(money);
		borrow.setType(type.getCode());
		borrow.setBorrowNo(generatorClusterNo.getBorrowNo(currDate));
		borrow.setStatus(BorrowStatus.AGREE.getCode());//默认审核通过
		borrow.setName(name);
		borrow.setUserId(userId);
		borrow.setNper(nper);
		borrow.setBankId(cardId);
		borrow.setGoodsId(goodsId);
		borrow.setOpenId(openId);
		borrow.setNperAmount(perAmount);
		return borrow;
	}
	
	/**
	 * 借款分期账单
	 * @param borrow --借款信息
	 * @param totalAmount --本息总计
	 * @param interestAmount --利息总计
	 * @param poundageAmount --手续费总计
	 * @param monthRate --月利率
	 * @return
	 */
	private List<AfBorrowBillDo> buildBorrowBill(BorrowType borrowType,AfBorrowDo borrow,BigDecimal totalAmount,BigDecimal interestAmount,BigDecimal monthRate,BigDecimal poundageAmount){
		List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
		Date now = new Date();//当前时间
		BigDecimal billAmount = totalAmount.divide(new BigDecimal(borrow.getNper()),2,BigDecimal.ROUND_HALF_UP);
		//计算本息总计
		for (int i = 1; i <= borrow.getNper(); i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setBorrowNo(borrow.getBorrowNo());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(borrow.getGmtCreate());
			Map<String,Integer> timeMap = getCurrentYearAndMonth("", now);
			bill.setBillYear(timeMap.get("year"));
			bill.setBillMonth(timeMap.get("month"));
			bill.setNper(borrow.getNper());
			bill.setBillNper(i);
			if(borrowType.equals(BorrowType.CASH)){
				bill.setPrincipleAmount(borrow.getAmount());
				bill.setInterestAmount(interestAmount);
				bill.setPoundageAmount(poundageAmount);
				bill.setStatus(BorrowBillStatus.NO.getCode());
				bill.setType(BorrowType.CASH.getCode());
				bill.setBillAmount(billAmount);
			}else{
				BigDecimal perPoundageAmount = poundageAmount.divide(new BigDecimal(borrow.getNper()),2,BigDecimal.ROUND_HALF_UP);//当月手续费
				BigDecimal perInterest = totalAmount.multiply(monthRate).setScale(2, BigDecimal.ROUND_HALF_UP);//本月利息
				bill.setInterestAmount(perInterest);
				bill.setPoundageAmount(perPoundageAmount);
				totalAmount = totalAmount.subtract(billAmount);
				if(i<borrow.getNper()){
					bill.setBillAmount(billAmount);
				}else{
					bill.setBillAmount(totalAmount.add(billAmount));
				}
				bill.setPrincipleAmount(bill.getBillAmount().subtract(perInterest).subtract(perPoundageAmount));//本金 = 账单金额 -本月利息 -手续费
				bill.setStatus(BorrowBillStatus.FORBIDDEN.getCode());
				bill.setType(BorrowType.CONSUME.getCode());
			}
			list.add(bill);
			now = DateUtil.addMonths(now, 1);
		}
		return list;
	}
	
	@Override
	public String getCurrentLastBorrowNo(Date current) {
		return null;
	}
	
	private AfUserAccountLogDo addUserAccountLogDo(UserAccountLogType borrowType,BigDecimal amount,Long userId,Long borrowId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(borrowId+"");
		accountLog.setType(borrowType.getCode());
		return accountLog;
	}
	
	private AfBorrowInterestDo buildBorrowInterest(Long billId,BigDecimal interest,String creator,BigDecimal money){
		AfBorrowInterestDo borrow = new AfBorrowInterestDo();
		//利息日志
		borrow.setBillId(billId);
		borrow.setInterest(interest);
		borrow.setCreator(creator);
		borrow.setPrincipleAmount(money);
		return borrow;
	}
	

	@Override
	public long dealConsumeApply(final AfUserAccountDto userDto,final BigDecimal amount,
			final Long cardId,final Long goodsId,final String openId,final String name,final int nper) {
		return transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					//TODO 转账处理
					pushService.dealBorrowConsumeTransfer(userDto.getUserName(), name);
					//修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userDto.getUserId());
					account.setUsedAmount(amount);
					afUserAccountDao.updateUserAccount(account);
					//获取借款分期配置信息
					AfResourceDo resource = afResourceDao.getConfigByTypesAndSecType(Constants.RES_BORROW_RATE,Constants.RES_BORROW_CONSUME);
					if(null == resource){
						throw new Exception("分期利率配置不能为空");
					}else{
						BigDecimal money = amount;//借款金额
						BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
						BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
						String[] range = StringUtil.split(resource.getValue2(), ",");
						if(null != range && range.length==2){
							rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
							rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
						}
						JSONObject obj = JSON.parseObject(resource.getValue());
						BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(money, 
								nper,new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd);//总手续费
						BigDecimal perAmount =  BigDecimalUtil.getConsumeAmount(money, nper, 
								new BigDecimal(obj.getString(nper+"")).divide(new BigDecimal(Constants.MONTH_OF_YEAR),
										8,BigDecimal.ROUND_HALF_UP), totalPoundage);//每期账单金额
						AfBorrowDo borrow =  buildBorrow(name,BorrowType.CONSUME_TEMP,userDto.getUserId(), amount,cardId,goodsId,openId,nper,perAmount);
						afBorrowDao.addBorrow(borrow);//新增借款信息
						//新增借款日志
						afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(UserAccountLogType.CONSUME,amount, userDto.getUserId(), borrow.getRid()));
						
						List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CONSUME,borrow,perAmount.multiply(new BigDecimal(borrow.getNper())),
								BigDecimal.ZERO,new BigDecimal(obj.getString(borrow.getNper()+"")).divide(new BigDecimal(Constants.MONTH_OF_YEAR),
										8,BigDecimal.ROUND_HALF_UP),totalPoundage);
						//TODO 转账处理
						
						//新增借款账单
						afBorrowDao.addBorrowBill(billList);
						return borrow.getRid();
					}
				} catch (Exception e) {
					logger.info("dealCashApply error:"+e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
	}

	@Override
	public Map<String,Integer> getCurrentYearAndMonth(String type,Date now) {
		Map<String,Integer> map = new HashMap<String,Integer>();
		//账单日
		Date startDate = DateUtil.addDays(DateUtil.getFirstOfMonth(now), 
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10)-1);
		if(now.before(startDate)){//账单日前面
			startDate = DateUtil.addMonths(startDate,-1);
		}
		if("N".equals(type)){
			startDate = DateUtil.addMonths(startDate,
					1);
		}
		int billYear=0,billMonth=0;
		String[] billDay = DateUtil.formatDate(startDate, DateUtil.MONTH_PATTERN).split("-");
		if(billDay.length==2){
			billYear = NumberUtil.objToIntDefault(billDay[0], 0);
			billMonth = NumberUtil.objToIntDefault(billDay[1], 0);
		}
		map.put("year", billYear);
		map.put("month", billMonth);
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
}
