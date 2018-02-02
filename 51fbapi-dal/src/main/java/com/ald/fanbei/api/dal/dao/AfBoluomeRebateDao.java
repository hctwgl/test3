package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeRebateDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.AfRebateDo;


/**
 * 点亮活动新版Dao
 * 
 * @author gaojibin_temple
 * @version 1.0.0 初始化
 * @date 2017-11-13 17:28:25
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeRebateDao extends BaseDao<AfBoluomeRebateDo, Long> {

	//int checkOrderTimes(@Param("userId")Long userId);

	AfShopDo getShopInfoByOrderId(@Param("orderId") long orderId);

	//List<AfBoluomeRebateDo> getListByUserId(@Param("userId")Long userId);

	Long getLightShopId(@Param("orderId")Long orderId);

	List<AfRebateDo> getRebateList(@Param("userId")Long userId,@Param("startTime")String startTime);

	String getScence(@Param("orderId")Long orderId);
	
	AfBoluomeRebateDo getLastUserRebateByUserId(@Param("userId") Long userId);

	AfBoluomeRebateDo getHighestNeverPopedRebate(@Param("userId")Long userId);

	int getRebateNumByOrderId(@Param("orderId")Long orderId);

	int getRebateCount(@Param("shopId")Long shopId, @Param("userId")Long userId,@Param("activityTime") String activityTime);

	AfBoluomeRebateDo getMaxUserRebateByStartIdAndEndIdAndUserId(@Param("startId")Long startId,@Param("endId") Long endId,@Param("userId") Long userId);

	int getCountByUserIdAndFirstOrder(@Param("userId")Long userId,@Param("firstOrder") int firstOrder,@Param("oneYuanTime") String oneYuanTime);


	int checkOrderTimes(@Param("userId")Long userId,@Param("activityTime") String activityTime);

	List<AfBoluomeRebateDo> getListByUserId(@Param("userId")Long userId,@Param("startTime")String startTime);


    

}
