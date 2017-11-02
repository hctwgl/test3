package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.query.AfBusinessAccessRecordQuery;

/**
 * 业务访问记录Service
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-07-19 16:26:32
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBusinessAccessRecordsService extends ParentService<AfBusinessAccessRecordsDo, Long>{

	/**
	 * 判断用户今天是否签到
	 * @param userId
	 * @return
	 */
	boolean checkIsSignToday(Long userId);

	/**
	 * 签到
	 * @param gameDo
	 * @param userId
	 */
	void doSign(AfGameDo gameDo, Long userId);
	
	/**
	 * 借贷超市签到天数
	 * @param query
	 * @return
	 */
	int getSignDays(AfBusinessAccessRecordQuery query);

	int getSignCountToday(Long userId);
}
