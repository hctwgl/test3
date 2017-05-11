/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午5:04:43
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowCashService")
public class AfBorrowCashServiceImpl extends BaseService implements AfBorrowCashService {
	@Resource
	GeneratorClusterNo generatorClusterNo;
	@Resource
	AfBorrowCashDao afBorrowCashDao;

	@Override
	public int addBorrowCash(AfBorrowCashDo afBorrowCashDo) {
		Date currDate = new Date();

		afBorrowCashDo.setBorrowNo(generatorClusterNo.getBorrowCashNo(currDate));
		return afBorrowCashDao.addBorrowCash(afBorrowCashDo);
	}

	@Override
	public int updateBorrowCash(AfBorrowCashDo afBorrowCashDo) {
		return afBorrowCashDao.updateBorrowCash(afBorrowCashDo);
	}

	@Override
	public AfBorrowCashDo getBorrowCashByUserId(Long userId) {
		return afBorrowCashDao.getBorrowCashByUserId(userId);
	}

	@Override
	public List<AfBorrowCashDo> getBorrowCashListByUserId(Long userId, Integer start) {
		return afBorrowCashDao.getBorrowCashListByUserId(userId, start);
	}

	@Override
	public AfBorrowCashDo getBorrowCashByrid(Long rid) {
		return afBorrowCashDao.getBorrowCashByrid(rid);
	}

	@Override
	public String getCurrentLastBorrowNo(String orderNoPre) {
		return afBorrowCashDao.getCurrentLastBorrowNo(orderNoPre);
	}

	@Override
	public AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo) {
		return afBorrowCashDao.getBorrowCashByRishOrderNo(rishOrderNo);
	}

	@Override
	public AfBorrowCashDo getUserDayLastBorrowCash(Long userId) {
		Date startTime = DateUtil.getToday();
		Date endTime = DateUtil.getTodayLast();
		return afBorrowCashDao.getUserDayLastBorrowCash(userId,startTime,endTime);
	}
}
