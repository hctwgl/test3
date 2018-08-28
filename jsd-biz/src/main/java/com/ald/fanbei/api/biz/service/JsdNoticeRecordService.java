package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;
import com.ald.fanbei.api.dal.domain.JsdNoticeRecordDo;

/**
 * Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-28 15:43:36
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdNoticeRecordService extends ParentService<JsdNoticeRecordDo, Long>{

    int  updateNoticeRecordStatus(JsdNoticeRecordDo noticeRecordDo);

    int  updateNoticeRecordTimes(JsdNoticeRecordDo noticeRecordDo);

    int addNoticeRecord(JsdNoticeRecordDo noticeRecordDo);



}
