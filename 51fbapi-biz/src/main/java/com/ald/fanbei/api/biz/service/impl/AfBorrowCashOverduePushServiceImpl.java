package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowCashOverduePushDao;
import com.ald.fanbei.api.dal.domain.AfBorrowCashOverduePushDo;
import com.ald.fanbei.api.biz.service.AfBorrowCashOverduePushService;



/**
 * 逾期债权推送的现金贷拓展表ServiceImpl
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-03-27 16:33:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowCashOverduePushService")
public class AfBorrowCashOverduePushServiceImpl extends ParentServiceImpl<AfBorrowCashOverduePushDo, Long> implements AfBorrowCashOverduePushService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowCashOverduePushServiceImpl.class);
   
    @Resource
    private AfBorrowCashOverduePushDao afBorrowCashOverduePushDao;

		@Override
	public BaseDao<AfBorrowCashOverduePushDo, Long> getDao() {
		return afBorrowCashOverduePushDao;
	}


	@Override
	public AfBorrowCashOverduePushDo getBorrowCashOverduePushByBorrowId(Long id) {
		AfBorrowCashOverduePushDo overduePushDo = new AfBorrowCashOverduePushDo();
		overduePushDo.setBorrowId(id);
		return afBorrowCashOverduePushDao.getByCommonCondition(overduePushDo);
	}
}