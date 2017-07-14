package com.ald.fanbei.api.biz.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAftersaleApplyService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfAftersaleApplyDao;
import com.ald.fanbei.api.dal.domain.AfAftersaleApplyDo;



/**
 * 售后申请ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-08 16:15:30
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAftersaleApplyService")
public class AfAftersaleApplyServiceImpl implements AfAftersaleApplyService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAftersaleApplyServiceImpl.class);
   
    @Resource
    private AfAftersaleApplyDao afAftersaleApplyDao;

	@Override
	public int saveRecord(AfAftersaleApplyDo afAftersaleApplyDo) {
		return afAftersaleApplyDao.saveRecord(afAftersaleApplyDo);
	}

	@Override
	public int updateById(AfAftersaleApplyDo afAftersaleApplyDo) {
		return afAftersaleApplyDao.updateById(afAftersaleApplyDo);
	}

	@Override
	public AfAftersaleApplyDo getById(Long id) {
		return afAftersaleApplyDao.getById(id);
	}

	@Override
	public AfAftersaleApplyDo getByOrderId(Long orderId) {
		return afAftersaleApplyDao.getByOrderId(orderId);
	}
	
	@Override
	public AfAftersaleApplyDo getByOrderIdAndNotClose(Long orderId) {
		return afAftersaleApplyDao.getByOrderIdAndNotClose(orderId);
	}

	@Override
	public String getCurrentLastApplyNo(Date current) {
		Date startDate = DateUtil.getStartOfDate(current);
		Date endDate = DateUtil.getEndOfDate(current);
		return afAftersaleApplyDao.getCurrentLastApplyNo(startDate, endDate);
	}
}