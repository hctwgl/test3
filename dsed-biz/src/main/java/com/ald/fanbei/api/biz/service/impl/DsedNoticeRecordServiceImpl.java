package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.DsedNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.biz.service.DsedNoticeRecordService;

import java.util.List;


/**
 * 通知任务ServiceImpl
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:40:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("dsedNoticeRecordService")
public class DsedNoticeRecordServiceImpl extends ParentServiceImpl<DsedNoticeRecordDo, Long> implements DsedNoticeRecordService {
	
    private static final Logger logger = LoggerFactory.getLogger(DsedNoticeRecordServiceImpl.class);
   
    @Resource
    private DsedNoticeRecordDao dsedNoticeRecordDao;

		@Override
	public BaseDao<DsedNoticeRecordDo, Long> getDao() {
		return dsedNoticeRecordDao;
	}

	@Override
	public List<DsedNoticeRecordDo> getAllFailNoticeRecord() {
		return dsedNoticeRecordDao.getAllFailNoticeRecord();
	}

	@Override
	public int updateNoticeRecordStatus(DsedNoticeRecordDo noticeRecordDo) {
		return dsedNoticeRecordDao.updateNoticeRecordStatus(noticeRecordDo);
	}

	@Override
	public int updateNoticeRecordTimes(DsedNoticeRecordDo noticeRecordDo) {
		return dsedNoticeRecordDao.updateNoticeRecordTimes(noticeRecordDo);
	}

	@Override
	public int addNoticeRecord(DsedNoticeRecordDo noticeRecordDo) {
		return dsedNoticeRecordDao.addNoticeRecord( noticeRecordDo);
	}
}