package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfBorrowBillService;
import com.ald.fanbei.api.dal.dao.AfBorrowBillDao;
import com.ald.fanbei.api.dal.domain.AfBorrowBillDo;
import com.ald.fanbei.api.dal.domain.AfBorrowTotalBillDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowBillDto;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月17日下午21:52:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afBorrowBillService")
public class AfBorrowBillServiceImpl implements AfBorrowBillService{

	@Resource
	private AfBorrowBillDao afBorrowBillDao;
	
	@Override
	public List<AfBorrowBillDo> getMonthBillList(AfBorrowBillQuery query) {
		return afBorrowBillDao.getMonthBillList(query);
	}
	
	@Override
	public BigDecimal getMonthlyBillByStatus(Long userId, int billYear,
			int billMonth, String status) {
		BigDecimal amount = afBorrowBillDao.getMonthlyBillByStatus(userId, billYear, billMonth, status);
		return amount==null?BigDecimal.ZERO:amount;
	}
	
	@Override
	public List<AfBorrowTotalBillDo> getUserFullBillList(Long userId) {
		return afBorrowBillDao.getUserFullBillList(userId);
	}

	@Override
	public AfBorrowBillDto getBorrowBillById(Long rid) {
		return afBorrowBillDao.getBorrowBillById(rid);
	}

}
