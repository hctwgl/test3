package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAdvertiseDo;
import com.ald.fanbei.api.dal.domain.dto.AfAdvertiseDto;

/**
 * 定向广告规则Dao
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAdvertiseDao extends BaseDao<AfAdvertiseDo, Long> {

	//AfAdvertiseDto getDirectionalRecommend(Long userId);

	int getDirectionalRecommendCount(@Param("userId") Long userId,@Param("queryConditions")String queryConditions);

	List<AfAdvertiseDto> getDirectionalRecommendInfo(@Param("positionCode") String positionCode);

    int  updateClickCountById(@Param("rid")Long rid);

}
