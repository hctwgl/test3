
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserLoginDo;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-08-01 10:39:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBoluomeActivityUserLoginDao extends BaseDao<AfBoluomeActivityUserLoginDo, Long> {



	AfBoluomeActivityUserLoginDo getUserLoginRecordByRefUserId(Long refUserId);

	AfBoluomeActivityUserLoginDo getUserLoginRecordByUserId(Long userId);

	AfBoluomeActivityUserLoginDo getUserLoginRecord(AfBoluomeActivityUserLoginDo queryUserLoginRecord);

	int getFlagCountByRefUserId(long refId);
	int updateBindingFlagIsN(AfBoluomeActivityUserLoginDo updateBidingflag);
	Integer getBindingNum(@Param("activityId")Long activityId, @Param("refUserId")Long refUserId);

	Long findRefUserId(@Param("userId")Long userId);

	List<AfBoluomeActivityUserLoginDo> getByRefUserIdAndActivityId(@Param("userId") Long userId,@Param("activityId") Long activityId);

	int saveUserLoginInfo(AfBoluomeActivityUserLoginDo afBoluomeActivityUserLogin);

    

}
