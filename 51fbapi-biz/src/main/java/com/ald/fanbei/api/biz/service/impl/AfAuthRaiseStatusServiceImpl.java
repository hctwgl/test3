package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.Date;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAuthRaiseStatusService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.enums.LoanType;
import com.ald.fanbei.api.common.enums.RaiseStatus;
import com.ald.fanbei.api.dal.dao.AfAuthRaiseStatusDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;

/**
 * 贷款业务ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-02-06 17:58:14 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afAuthRaiseStatusService")
public class AfAuthRaiseStatusServiceImpl extends ParentServiceImpl<AfAuthRaiseStatusDo, Long> implements AfAuthRaiseStatusService {

    private static final Logger logger = LoggerFactory.getLogger(AfAuthRaiseStatusServiceImpl.class);

    @Autowired
    BizCacheUtil bizCacheUtil;

    @Resource
    private AfAuthRaiseStatusDao afAuthRaiseStatusDao;

    @Override
    public BaseDao<AfAuthRaiseStatusDo, Long> getDao() {
	return afAuthRaiseStatusDao;
    }

    @Override
    public AfAuthRaiseStatusDo buildAuthRaiseStatusDo(Long userId, String authType, String prdType, String raiseStatus, BigDecimal amount, Date gmtFinish) {
	AfAuthRaiseStatusDo raiseStatusDo = new AfAuthRaiseStatusDo();
	raiseStatusDo.setAuthType(authType);
	raiseStatusDo.setPrdType(prdType);
	raiseStatusDo.setUserId(userId);
	raiseStatusDo.setRaiseStatus(raiseStatus);
	raiseStatusDo.setAmount(amount);
	raiseStatusDo.setGmtFinish(gmtFinish);
	return raiseStatusDo;
    }

    @Override
    public void saveOrUpdateRaiseStatus(AfAuthRaiseStatusDo raiseStatus) {
	saveOrUpdateRaiseStatus(raiseStatus, true);
    }

    private void saveOrUpdateRaiseStatus(AfAuthRaiseStatusDo raiseStatus, boolean isUpdate) {
	AfAuthRaiseStatusDo delegateRaiseStatus = new AfAuthRaiseStatusDo();
	delegateRaiseStatus.setAuthType(raiseStatus.getAuthType());
	delegateRaiseStatus.setPrdType(raiseStatus.getPrdType());
	delegateRaiseStatus.setUserId(raiseStatus.getUserId());
	delegateRaiseStatus.setRaiseStatus(raiseStatus.getRaiseStatus());
	delegateRaiseStatus.setGmtFinish(raiseStatus.getGmtFinish());
	delegateRaiseStatus.setAmount(raiseStatus.getAmount());
	AfAuthRaiseStatusDo existRaiseStatusDo = afAuthRaiseStatusDao.getByPrdTypeAndAuthType(raiseStatus.getPrdType(), raiseStatus.getAuthType(), raiseStatus.getUserId());
	if (existRaiseStatusDo != null) {
	    logger.info("saveOrUpdateRaiseStatus exist:" + existRaiseStatusDo.toString() + ",isUpdate:" + isUpdate);
	    if (isUpdate) {
		delegateRaiseStatus.setRid(existRaiseStatusDo.getRid());
		afAuthRaiseStatusDao.updateById(delegateRaiseStatus);
	    }
	} else {
	    String raiseKey = raiseStatus.getPrdType() + raiseStatus.getAuthType() + raiseStatus.getUserId();
	    if (bizCacheUtil.getObject(raiseKey) == null) {
		bizCacheUtil.saveObject(raiseKey, raiseStatus.getAuthType(), 5);
		logger.info("saveOrUpdateRaiseStatus save:" + delegateRaiseStatus.toString());
		afAuthRaiseStatusDao.saveRecord(delegateRaiseStatus);
	    }
	}
    }

    @Override
    public void initRaiseStatus(Long userId, String authType) {
	AfAuthRaiseStatusDo bldRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.BLD_LOAN.getCode(), RaiseStatus.N.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(bldRaiseStatus, false);
	AfAuthRaiseStatusDo xjdRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.CASH.getCode(), RaiseStatus.N.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(xjdRaiseStatus, false);

    }

    @Override
    public void initCreditRaiseStatus(Long userId, String authType) {
	AfAuthRaiseStatusDo bldRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.BLD_LOAN.getCode(), RaiseStatus.Y.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(bldRaiseStatus, false);
	AfAuthRaiseStatusDo xjdRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.CASH.getCode(), RaiseStatus.N.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(xjdRaiseStatus, false);
    }

    @Override
    public void initZhengxinRaiseStatus(Long userId, String authType) {
	this.initCreditRaiseStatus(userId, authType);
    }

    @Override
    public void initOnlinebankRaiseStatus(Long userId, String authType) {
	AfAuthRaiseStatusDo bldRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.BLD_LOAN.getCode(), RaiseStatus.N.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(bldRaiseStatus, false);
	AfAuthRaiseStatusDo xjdRaiseStatus = this.buildAuthRaiseStatusDo(userId, authType, LoanType.CASH.getCode(), RaiseStatus.Y.getCode(), BigDecimal.ZERO, new Date());
	this.saveOrUpdateRaiseStatus(xjdRaiseStatus, false);

    }

    @Override
    public AfAuthRaiseStatusDo getByPrdTypeAndAuthType(String prdType, String authType, Long userId) {

	return afAuthRaiseStatusDao.getByPrdTypeAndAuthType(prdType, authType, userId);
    }
}