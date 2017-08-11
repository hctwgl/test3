package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:38:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityUserItemsDao extends BaseDao<AfBoluomeActivityUserItemsDo, Long> {

	List<Long> getItemsByActivityIdUserId(@Param("activityId")Long activityId, @Param("userId")Long userId);

	int saveBoluomeActivityUserItems(AfBoluomeActivityUserItemsDo userItemsDo);

	void deleteByRid(@Param("rid")Long rid);

    

}
