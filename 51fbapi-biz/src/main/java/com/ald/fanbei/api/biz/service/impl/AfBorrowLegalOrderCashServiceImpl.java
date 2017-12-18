package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.util.GeneratorClusterNo;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderCashDo;

/**
 * ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-10 10:26:01 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBorrowLegalOrderCashService")
public class AfBorrowLegalOrderCashServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderCashDo, Long>
		implements AfBorrowLegalOrderCashService {

	@Resource
	private AfBorrowLegalOrderCashDao afBorrowLegalOrderCashDao;
	@Resource
	GeneratorClusterNo generatorClusterNo;

	@Override
	public BaseDao<AfBorrowLegalOrderCashDo, Long> getDao() {
		return afBorrowLegalOrderCashDao;
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowLegalOrderId(Long rid) {
		// TODO Auto-generated method stub
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowLegalOrderId(rid);
	}

	@Override
	public int saveBorrowLegalOrderCash(AfBorrowLegalOrderCashDo afBorrowLegalOrderCashDo) {
		String cashNo = generatorClusterNo.geBorrowLegalOrderCashNo(new Date());
		afBorrowLegalOrderCashDo.setCashNo(cashNo);
		return afBorrowLegalOrderCashDao.saveRecord(afBorrowLegalOrderCashDo);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowId(Long borrowId) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowId(borrowId);
	}

	@Override
	public AfBorrowLegalOrderCashDo getBorrowLegalOrderCashByBorrowIdNoStatus(Long rid) {
		return afBorrowLegalOrderCashDao.getBorrowLegalOrderCashByBorrowIdNoStatus(rid);
	}
}