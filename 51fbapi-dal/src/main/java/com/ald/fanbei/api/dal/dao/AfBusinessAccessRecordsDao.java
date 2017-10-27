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
	 * 获取用户点击的过借贷超市的个数
	 * @return
	 */
	List<String> getSignedSuperMarketList(AfBusinessAccessRecordQuery query);
	
	/**
	 * 获取用户今天访问的借贷超市的个数(不包含访问过的借贷超市)
	 * @param superMarketIds(访问过的借贷超市)
	 * @return
	 */
	int getVisitCountToday(@Param("superMarketIds") List<String> superMarketIds,@Param("userId") Long userId);
	
	/**
	 * 获取四个借贷超市的访问记录id
	 * @param signedMarketIds
	 * @return
	 */
	List<String> getFourMarketIds(@Param("superMarketIds")List<String> signedMarketIds,@Param("userId") Long userId);
	/**
	 * 获取需要修为为签到状态的id
	 * @param superMarketIds
	 * @return
	 */
	List<String> getNeedSignIds(@Param("superMarketIds") List<String> superMarketIds,@Param("userId") Long userId);
	
	
	
	/**
	 * 批量修改为签到状态
	 * @param ids
	 */
	void batchSetSignByIds(@Param("ids") List<String> ids);
	

	/**
	 * 获取签到活动期间总共remark为sign的个数，为计算签到天数
	 * @param query
	 * @return
	 */
	int getTotalSignCount(AfBusinessAccessRecordQuery query);

	List<AfBusinessAccessRecordsDo> getTotalSign();

	//获取签到活动期间总共remark为sign的list
	List<AfBusinessAccessRecordsDo> getTotalSignList(AfBusinessAccessRecordQuery query);
}
