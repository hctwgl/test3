package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.DsedRecordMaxDo;

/**
 * 债权推送查询上限记录表Dao
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2018-07-17 09:28:08
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedRecordMaxDao extends BaseDao<DsedRecordMaxDo, Long> {

	DsedRecordMaxDo getByBusIdAndEventype(@Param("busId")String busId,@Param("eventType") String eventType);

	int deleteById(Long rid);

}
