package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfTaskBrowseGoodsDaysDo;
import org.apache.ibatis.annotations.Param;

/**
 * 持续完成浏览商品数量的天数Service
 * 
 * @author luoxiao
 * @version 1.0.0 初始化
 * @date 2018-05-16 21:12:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfTaskBrowseGoodsDaysService{
    int addTaskBrowseGoodsDays(AfTaskBrowseGoodsDaysDo afTaskBrowseGoodsDaysDo);

    int updateTaskBrowseGoodsDays(Long userId, Integer continueDays);

    AfTaskBrowseGoodsDaysDo getByUserId(Long userId);
}
