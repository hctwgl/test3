package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserSeedDo;

/**
 * 种子用户信息Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-16 16:40:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserSeedDao extends BaseDao<AfUserSeedDo, Long> {

    
	/**
	 * 根据用户Id查找种子用户
	 * 
	 * **/
	AfUserSeedDo getAfUserSeedDoByUserId(long userId);
}
