package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.JsdNoticeRecordDao;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;



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