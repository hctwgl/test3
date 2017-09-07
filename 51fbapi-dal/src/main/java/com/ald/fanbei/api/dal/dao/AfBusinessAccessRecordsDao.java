package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.query.AfBusinessAccessRecordQuery;

/**
 * 业务访问记录Dao
 * 
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-19 16:26:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBusinessAccessRecordsDao extends BaseDao<AfBusinessAccessRecordsDo, Long> {

	/**
	 * 用户今天（0点起算）借贷超市埋点 remark为4的数量。remark为0还没签到
	 * @param userId
	 * @return
	 */
	int getSignCountToday(@Param("userId") Long userId);
	
	/**
	 * 获取签到过的借贷超市id列表
	 * @return
	 */
	List<String> getSignSuperMarketIds(AfBusinessAccessRecordQuery query);

}
