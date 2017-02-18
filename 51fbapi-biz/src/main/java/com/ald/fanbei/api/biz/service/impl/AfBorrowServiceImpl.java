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
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月9日下午4:51:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowService")
public class AfBorrowServiceImpl implements AfBorrowService{

	@Resource
	AfBorrowDao afBorrowDao;
	
	@Resource
	TransactionTemplate transactionTemplate;
	
	@Resource
	AfResourceDao afResourceDao;
	
	@Override
	public int dealWithTransferSuccess(final Long borrowId) {
		
		return transactionTemplate.execute(new TransactionCallback<Integer>() {

			@Override
			public Integer doInTransaction(TransactionStatus status) {
				try {
					AfBorrowDo borrow = afBorrowDao.getBorrowById(borrowId);
					//
					List<AfResourceDo> list = afResourceDao.getConfigByTypes(new StringBuffer("BORROW_").append(borrow.getType()).toString());
					if(null == list || list.size()==0){
						throw new Exception("利率配置不能为空");
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
						afBorrowDao.updateBorrow(borrowId, interestAmount, chargeAmount);
						List<AfBorrowBillDo> billList = buildBorrowBill(borrow);
						afBorrowDao.addBorrowBill(billList);
					}
					return 1;
				} catch (Exception e) {
					status.setRollbackOnly();
					return 0;
				}
			}
		});
	}

	private List<AfBorrowBillDo> buildBorrowBill(AfBorrowDo borrow){
		List<AfBorrowBillDo> list = new ArrayList<AfBorrowBillDo>();
		Date now = new Date();//当前时间
		for (int i = 1; i <= borrow.getNper(); i++) {
			AfBorrowBillDo bill = new AfBorrowBillDo();
			bill.setUserId(borrow.getUserId());
			bill.setBorrowId(borrow.getRid());
			bill.setName(borrow.getName());
			bill.setGmtBorrow(now);
			bill.setBillMonth(DateUtil.formatDate(now, DateUtil.MONTH_PATTERN));
			bill.setBillNper(new StringBuffer("").append(i).append("/").append(borrow.getNper()).toString());
			bill.setBillAmount(borrow.getPerAmount());
			bill.setStatus("N");
			bill.setOverdueStatus("N");
			list.add(bill);
			DateUtil.addMonths(now, 1);
		}
		return list;
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
}
