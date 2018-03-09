/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCacheAmountPerdayDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCacheAmountPerdayDo;

/**
 * @author chenjinhu
 *
 */
@Service("afBorrowCacheAmountPerdayService")
public class AfBorrowCacheAmountPerdayServiceImpl implements AfBorrowCacheAmountPerdayService {

	@Resource
	AfBorrowCacheAmountPerdayDao afBorrowCacheAmountPerdayDao;
	
	@Override
	public int addBorrowCacheAmountPerday(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo) {
		return afBorrowCacheAmountPerdayDao.addBorrowCacheAmountPerday(borrowCacheAmountPerdayDo);
	}

	@Override
	public int updateBorrowCacheAmount(AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo) {
		return afBorrowCacheAmountPerdayDao.updateBorrowCacheAmount(borrowCacheAmountPerdayDo);
	}

	@Override
	public AfBorrowCacheAmountPerdayDo getSigninByDay(Integer day) {
		AfBorrowCacheAmountPerdayDo borrowCacheAmountPerdayDo = new AfBorrowCacheAmountPerdayDo();
		borrowCacheAmountPerdayDo.setDay(day);
		return afBorrowCacheAmountPerdayDao.getSigninByDay(borrowCacheAmountPerdayDo);
	}
	
	@Override
	public void record() {
		int currentDay = Integer.parseInt(DateUtil.getNowYearMonthDay());
		AfBorrowCacheAmountPerdayDo currentAmount = this.getSigninByDay(currentDay);
		if (currentAmount == null) {
			AfBorrowCacheAmountPerdayDo temp = new AfBorrowCacheAmountPerdayDo();
			temp.setAmount(new BigDecimal(0));
			temp.setDay(currentDay);
			temp.setNums(0l);
			addBorrowCacheAmountPerday(temp);
			currentAmount = temp;
		}
	}

}
