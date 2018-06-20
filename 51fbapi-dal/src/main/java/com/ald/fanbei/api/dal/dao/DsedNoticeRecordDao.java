package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedNoticeRecordDo;

import java.util.List;

/**
 * 通知任务Dao
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:40:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedNoticeRecordDao extends BaseDao<DsedNoticeRecordDo, Long> {

    List<DsedNoticeRecordDo> getAllFailNoticeRecord();

    /**
     * 更改成功状态
     * @param noticeRecordDo
     * @return
     */
    int updateNoticeRecordStatus(DsedNoticeRecordDo noticeRecordDo);

    /**
     * 更改失败次数
     * @param noticeRecordDo
     * @return
     */
    int updateNoticeRecordTimes(DsedNoticeRecordDo noticeRecordDo);

    /**
     * 增加通知记录
     * @param noticeRecordDo
     * @return
     */
    int addNoticeRecord(DsedNoticeRecordDo noticeRecordDo);


}
