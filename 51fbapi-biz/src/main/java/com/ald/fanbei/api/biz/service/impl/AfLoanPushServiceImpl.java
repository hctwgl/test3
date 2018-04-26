package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfLoanPushDao;
import com.ald.fanbei.api.dal.domain.AfLoanPushDo;
import com.ald.fanbei.api.biz.service.AfLoanPushService;



/**
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-10 16:51:38
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afLoanPushService")
public class AfLoanPushServiceImpl extends ParentServiceImpl<AfLoanPushDo, Long> implements AfLoanPushService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfLoanPushServiceImpl.class);
   
    @Resource
    private AfLoanPushDao afLoanPushDao;

		@Override
	public BaseDao<AfLoanPushDo, Long> getDao() {
		return afLoanPushDao;
	}

	@Override
	public void saveOrUpdate(AfLoanPushDo loanPushDo) {
		AfLoanPushDo loanPushTemp = afLoanPushDao.getByLoanId(loanPushDo.getLoanId());
		if (loanPushTemp == null) {
			afLoanPushDao.saveRecord(loanPushDo);
		} else {
			loanPushDo.setRid(loanPushTemp.getRid());
			afLoanPushDao.updateById(loanPushDo);
		}
	}
}