/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowCashOverdueService;
import com.ald.fanbei.api.dal.dao.AfBorrowCashOverdueDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashOverdueDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月28日下午5:43:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowCashOverdueService")
public class AfBorrowCashOverdueServiceIImpl implements AfBorrowCashOverdueService {

	@Resource
	AfBorrowCashOverdueDao afBorrowCashOverdueDao;

	@Override
	public int addBorrowCashOverdue(AfBorrowCashOverdueDo afBorrowCashOverdueDo) {
		return afBorrowCashOverdueDao.addBorrowCashOverdue(afBorrowCashOverdueDo);
	}

	
	@Override
	public int updateBorrowCashOverdue(AfBorrowCashOverdueDo afBorrowCashOverdueDo) {
		return afBorrowCashOverdueDao.updateBorrowCashOverdue(afBorrowCashOverdueDo);
	}

	
	@Override
	public List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByBorrowId(Long borrowId) {
		return afBorrowCashOverdueDao.getBorrowCashOverdueListByBorrowId(borrowId);
	}

	
	@Override
	public List<AfBorrowCashOverdueDo> getBorrowCashOverdueListByUserId(Long userId) {
		return afBorrowCashOverdueDao.getBorrowCashOverdueListByUserId(userId);
	}

	
	@Override
	public AfBorrowCashOverdueDo getBorrowCashOverdueByrid(Long rid) {
		return afBorrowCashOverdueDao.getBorrowCashOverdueByrid(rid);
	}

}
