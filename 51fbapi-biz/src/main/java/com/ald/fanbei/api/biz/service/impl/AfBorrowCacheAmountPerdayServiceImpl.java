/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCacheAmountPerdayService;
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

}
