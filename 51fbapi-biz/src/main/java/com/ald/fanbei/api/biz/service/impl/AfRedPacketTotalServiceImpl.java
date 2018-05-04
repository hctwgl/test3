package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.dal.dao.AfRedPacketTotalDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 拆红包活动，用户总红包ServiceImpl
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRedPacketTotalService")
public class AfRedPacketTotalServiceImpl extends ParentServiceImpl<AfRedPacketTotalDo, Long>
		implements AfRedPacketTotalService {
	
    @Autowired
    private AfRedPacketTotalDao afRedPacketTotalDao;

	@Override
	public int getWithdrawTotalNum() {
		return afRedPacketTotalDao.getWithdrawTotalNum();
	}

	@Override
	public AfRedPacketTotalDo getTheOpening(Long userId, Integer overdueIntervalHour) {
		return afRedPacketTotalDao.getTheOpening(userId, overdueIntervalHour);
	}

	@Override
	public List<AfRedPacketTotalDo> findWithdrawList(Long userId, Integer queryNum) {
		return afRedPacketTotalDao.findWithdrawList(userId, queryNum);
	}

	@Override
	public BaseDao<AfRedPacketTotalDo, Long> getDao() {
		return afRedPacketTotalDao;
	}

}