package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTaskUserDo;

import java.util.List;

/**
 * 分类运营位配置Service
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 16:02:55
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskUserService {
    List<AfTaskUserDo> isDailyTaskList(Long userId,List<Long> list);

    List<AfTaskUserDo> isNotDailyTaskList(Long userId,List<Long> list);

    int updateNotDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);

    int updateDailyByTaskIdAndUserId(AfTaskUserDo afTaskUserDo);


}
