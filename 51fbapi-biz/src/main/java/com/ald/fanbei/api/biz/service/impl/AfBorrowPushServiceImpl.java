package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfBorrowPushDao;
import com.ald.fanbei.api.dal.domain.AfBorrowPushDo;
import com.ald.fanbei.api.biz.service.AfBorrowPushService;



/**
 * 菠萝觅订单详情ServiceImpl
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-03-01 18:59:28
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afBorrowPushService")
public class AfBorrowPushServiceImpl extends ParentServiceImpl<AfBorrowPushDo, Long> implements AfBorrowPushService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfBorrowPushServiceImpl.class);
   
    @Resource
    private AfBorrowPushDao afBorrowPushDao;

		@Override
	public BaseDao<AfBorrowPushDo, Long> getDao() {
		return afBorrowPushDao;
	}

	@Override
	public void saveOrUpdate(AfBorrowPushDo borrowPush) {
		AfBorrowPushDo borrowPushTemp = afBorrowPushDao.getByBorrowId(borrowPush.getBorrowId());
		if (borrowPushTemp == null) {
			afBorrowPushDao.saveRecord(borrowPush);
		} else {
			borrowPush.setRid(borrowPushTemp.getRid());
			afBorrowPushDao.updateById(borrowPush);
		}
	}
}