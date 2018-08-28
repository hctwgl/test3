package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;



/**
 * ServiceImpl
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-28 15:43:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("jsdNoticeRecordService")
public class JsdNoticeRecordServiceImpl extends ParentServiceImpl<JsdNoticeRecordDo, Long> implements JsdNoticeRecordService {
	
    private static final Logger logger = LoggerFactory.getLogger(JsdNoticeRecordServiceImpl.class);
   
    @Resource
    private JsdNoticeRecordDao jsdNoticeRecordDao;

		@Override
	public BaseDao<JsdNoticeRecordDo, Long> getDao() {
		return jsdNoticeRecordDao;
	}

	@Override
	public int updateNoticeRecordStatus(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao. updateNoticeRecordStatus(noticeRecordDo);
	}

	@Override
	public int updateNoticeRecordTimes(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao.updateNoticeRecordTimes( noticeRecordDo);
	}

	@Override
	public int addNoticeRecord(JsdNoticeRecordDo noticeRecordDo) {
		return jsdNoticeRecordDao.addNoticeRecord(noticeRecordDo);
	}
}