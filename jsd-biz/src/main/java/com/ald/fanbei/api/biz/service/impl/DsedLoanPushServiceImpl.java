package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedLoanPushDao;
import com.ald.fanbei.api.dal.domain.DsedLoanPushDo;
import com.ald.fanbei.api.biz.service.DsedLoanPushService;



/**
 * 都市e贷的实时债权推送拓展表ServiceImpl
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-16 16:54:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedLoanPushService")
public class DsedLoanPushServiceImpl extends ParentServiceImpl<DsedLoanPushDo, Long> implements DsedLoanPushService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedLoanPushServiceImpl.class);
   
    @Resource
    private DsedLoanPushDao dsedLoanPushDao;

		@Override
	public BaseDao<DsedLoanPushDo, Long> getDao() {
		return dsedLoanPushDao;
	}
	
	@Override
	public void saveOrUpdate(DsedLoanPushDo loanPushDo) {
		DsedLoanPushDo loanPushTemp = dsedLoanPushDao.getByLoanId(loanPushDo.getLoanId());
		if (loanPushTemp == null) {
			dsedLoanPushDao.saveRecord(loanPushDo);
		} else {
			loanPushDo.setRid(loanPushTemp.getRid());
			dsedLoanPushDao.updateById(loanPushDo);
		}
	}
}