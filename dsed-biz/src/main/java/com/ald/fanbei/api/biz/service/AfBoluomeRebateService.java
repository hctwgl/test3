package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfRebateDo;


/**
 * 点亮活动新版Service
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:25
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeRebateService extends ParentService<AfBoluomeRebateDo, Long>{

	void addRedPacket(Long rid, Long userId) throws Exception;

	//List<AfBoluomeRebateDo> getListByUserId(Long userId);

	//Long getLightShopId(Long orderId);

	List<AfRebateDo> getRebateList(Long userId,String startTime);
	
	AfBoluomeRebateDo getLastUserRebateByUserId(Long userId);

//	AfBoluomeRebateDo getHighestNeverPopedRebate(Long userId);

	int getRebateCount(Long shopId, Long userId, String activityTime);

	AfBoluomeRebateDo getMaxUserRebateByStartIdAndEndIdAndUserId(Long startId, Long endId, Long userId);

	int getCountByUserIdAndFirstOrder(Long userId, int firstOrder, String oneYuanTime);

	List<AfBoluomeRebateDo> getListByUserId(Long userId, String startTime);



}
