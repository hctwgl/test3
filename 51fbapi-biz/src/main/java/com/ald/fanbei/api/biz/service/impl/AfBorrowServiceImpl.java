package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.ald.fanbei.api.biz.service.AfBorrowService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.BorrowStatus;
import com.ald.fanbei.api.common.enums.BorrowType;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
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
	
	@Override
	public int dealCashWithTransferSuccess(final Long borrowId) {
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfBorrowDo borrow = afBorrowDao.getBorrowById(borrowId);
					//
					List<AfResourceDo> list = afResourceDao.getConfigByTypes(new StringBuffer("BORROW_").append(borrow.getType()).toString());
					if(null == list || list.size()==0){
						throw new Exception("现金借款利率配置不能为空");
					}else{
						AfResourceDo resource = list.get(0);
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
						afBorrowDao.addBorrowBill(billList);
					}
					return 1;
				} catch (Exception e) {
					logger.info("dealWithTransferSuccess error:"+e);
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

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
		long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					//修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userDto.getUserId());
					account.setUcAmount(money);//已取现金额=已取现金额+申请取现金额
					account.setUsedAmount(money);//授信已使用金额=授信已使用金额+申请取现金额
					afUserAccountDao.updateUserAccount(account);					
					AfBorrowDo borrow =  buildBorrow(Constants.DEFAULT_BORROW_CASH_NAME,BorrowType.CASH,userDto.getUserId(), money,cardId,null,null,1);
					afBorrowDao.addBorrow(borrow);
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(BorrowType.CASH,money, userDto.getUserId(), borrow.getRid()));
					return borrow.getRid();
				} catch (Exception e) {
					logger.info("dealCashApply error:"+e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
		if(result>0l){
			//TODO 转账处理
			//生成账单
			dealCashWithTransferSuccess(result);
		}
		return result;
	}
	
	/**
	 * 
	 * @param userId
	 * @param money -- 借款金额
	 * @return
	 */
	private AfBorrowDo buildBorrow(String name,BorrowType type,Long userId,BigDecimal money,Long cardId,Long goodsId,String openId,int nper){
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
		Date gmtBorrow = new Date();//借款到账时间
		BigDecimal billAmount = totalAmount.divide(new BigDecimal(borrow.getNper()),2,BigDecimal.ROUND_HALF_UP);
		//计算本息总计
		for (int i = 1; i <= borrow.getNper(); i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setBorrowNo(borrow.getBorrowNo());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(gmtBorrow);
			String[] billMonth = getBillYearAndMonth(now);
			if(billMonth.length==2){
				bill.setBillYear(billMonth[0]);
				bill.setBillMonth(billMonth[1]);
			}else{
				bill.setBillYear("");
				bill.setBillMonth("");
			}
			bill.setNper(borrow.getNper());
			bill.setBillNper(i);
			bill.setBillAmount(billAmount);
			if(borrowType.equals(BorrowType.CASH)){
				bill.setPrincipleAmount(borrow.getAmount());
				bill.setInterestAmount(interestAmount);
				bill.setPoundageAmount(poundageAmount);
			}else{
				BigDecimal perPoundageAmount = poundageAmount.divide(new BigDecimal(borrow.getNper()),2,BigDecimal.ROUND_HALF_UP);//当月手续费
				BigDecimal perInterest = totalAmount.multiply(monthRate).setScale(2, BigDecimal.ROUND_HALF_UP);//本月利息
				bill.setInterestAmount(perInterest);
				bill.setPoundageAmount(perPoundageAmount);
				bill.setPrincipleAmount(billAmount.subtract(perInterest).subtract(perPoundageAmount));//本金 = 账单金额 -本月利息 -手续费
				totalAmount = totalAmount.subtract(billAmount);
			}
			list.add(bill);
			now = DateUtil.addMonths(now, 1);
		}
		return list;
	}
	
	private String[] getBillYearAndMonth(Date now){
    	Date startDate = DateUtil.addDays(DateUtil.getFirstOfMonth(now), 
				NumberUtil.objToIntDefault(ConfigProperties.get(Constants.CONFKEY_BILL_CREATE_TIME), 10)-1);
		if(startDate.before(now)){
			return DateUtil.formatDate(now, DateUtil.MONTH_PATTERN).split("-");
		}else{
			return DateUtil.formatDate(DateUtil.addMonths(now, -1), DateUtil.MONTH_PATTERN).split("-");
		}
    }

	@Override
	public String getCurrentLastBorrowNo(Date current) {
		return null;
	}
	
	private AfUserAccountLogDo addUserAccountLogDo(BorrowType borrowType,BigDecimal amount,Long userId,Long borrowId){
		//增加account变更日志
		AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
		accountLog.setAmount(amount);
		accountLog.setUserId(userId);
		accountLog.setRefId(borrowId+"");
		accountLog.setType(borrowType.getCode());
		return accountLog;
	}

	@Override
	public long dealConsumeApply(final AfUserAccountDto userDto,final BigDecimal amount,
			final Long cardId,final Long goodsId,final String openId,final String name,final int nper) {
		long result = transactionTemplate.execute(new TransactionCallback<Long>() {
			@Override
			public Long doInTransaction(TransactionStatus status) {
				try {
					//修改用户账户信息
					AfUserAccountDo account = new AfUserAccountDo();
					account.setUserId(userDto.getUserId());
					account.setUsedAmount(amount);
					afUserAccountDao.updateUserAccount(account);					
					AfBorrowDo borrow =  buildBorrow(name,BorrowType.CONSUME_TEMP,userDto.getUserId(), amount,cardId,goodsId,openId,nper);
					afBorrowDao.addBorrow(borrow);
					afUserAccountLogDao.addUserAccountLog(addUserAccountLogDo(BorrowType.CONSUME,amount, userDto.getUserId(), borrow.getRid()));
					return borrow.getRid();
				} catch (Exception e) {
					logger.info("dealCashApply error:"+e);
					status.setRollbackOnly();
					return 0l;
				}
			}
		});
		if(result>0l){
			//TODO 转账处理
			//生成账单
			dealConsumeWithTransferSuccess(result);
		}
		return result;
	}

	@Override
	public int dealConsumeWithTransferSuccess(final Long borrowId) {
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfBorrowDo borrow = afBorrowDao.getBorrowById(borrowId);
					//获取借款分期配置信息
					AfResourceDo resource = afResourceDao.getSingleResourceBytype(Constants.RES_BORROW_CONSUME);
					if(null == resource){
						throw new Exception("分期利率配置不能为空");
					}else{
						BigDecimal money = borrow.getAmount();//借款金额
						BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
						BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
						String[] range = StringUtil.split(resource.getValue2(), ",");
						if(null != range && range.length==2){
							rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
							rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
						}
						JSONObject obj = JSON.parseObject(resource.getValue());
						BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(money, 
								borrow.getNper(),new BigDecimal(resource.getValue1()), rangeBegin, rangeEnd);//总手续费
						BigDecimal perAmount =  BigDecimalUtil.getConsumeAmount(money, borrow.getNper(), 
								new BigDecimal(obj.getString(borrow.getNper()+"")).divide(new BigDecimal(Constants.MONTH_OF_YEAR),
										8,BigDecimal.ROUND_HALF_UP), totalPoundage);//每期账单金额
						List<AfBorrowBillDo> billList = buildBorrowBill(BorrowType.CONSUME,borrow,perAmount.multiply(new BigDecimal(borrow.getNper())),
								BigDecimal.ZERO,new BigDecimal(obj.getString(borrow.getNper()+"")).divide(new BigDecimal(Constants.MONTH_OF_YEAR),
										8,BigDecimal.ROUND_HALF_UP),totalPoundage);
						afBorrowDao.addBorrowBill(billList);
					}
					return 1;
				} catch (Exception e) {
					logger.info("dealConsumeWithTransferSuccess error:"+e);
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}
}
