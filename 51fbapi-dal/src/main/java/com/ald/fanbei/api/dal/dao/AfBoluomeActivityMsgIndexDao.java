package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityMsgIndexDo;

/**
 * 点亮活动新版Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-17 11:51:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityMsgIndexDao extends BaseDao<AfBoluomeActivityMsgIndexDo, Long> {

    AfBoluomeActivityMsgIndexDo getByUserId(@Param("userId") Long userId);

    

}