package com.ald.fanbei.api.dal.dao;


import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfFacescoreShareCountDo;

/**
 * 颜值测试红包分享次数记录实体类Dao
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-19 09:39:51
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFacescoreShareCountDao extends BaseDao<AfFacescoreShareCountDo, Long> {

	AfFacescoreShareCountDo getByUserId(@Param(value="userId")Long userId);

	void addRecord(AfFacescoreShareCountDo shareCountDo2);

    

}
