package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfRecordMaxDo;

/**
 * 债权推送查询上限记录表Dao
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-04-11 18:10:45
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRecordMaxDao extends BaseDao<AfRecordMaxDo, Long> {

	AfRecordMaxDo getByBusIdAndEventype(@Param("busId")String busId,@Param("eventType")String eventType);
	
	int deleteById(Long id);

}
