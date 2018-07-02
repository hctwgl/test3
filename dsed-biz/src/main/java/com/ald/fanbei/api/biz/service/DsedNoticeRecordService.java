package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;

import java.util.List;

/**
 * 通知任务Service
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:40:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedNoticeRecordService extends ParentService<DsedNoticeRecordDo, Long>{

    List<DsedNoticeRecordDo>  getAllFailNoticeRecord();


    int  updateNoticeRecordStatus(DsedNoticeRecordDo noticeRecordDo);

    int  updateNoticeRecordTimes(DsedNoticeRecordDo noticeRecordDo);

    int addNoticeRecord(DsedNoticeRecordDo noticeRecordDo);

}
