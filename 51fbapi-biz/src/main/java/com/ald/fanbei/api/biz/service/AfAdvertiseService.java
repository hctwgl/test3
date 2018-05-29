package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfAdvertiseDo;
import com.ald.fanbei.api.dal.domain.dto.AfAdvertiseDto;

/**
 * 定向广告规则Service
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAdvertiseService extends ParentService<AfAdvertiseDo, Long>{
	//public AfAdvertiseDto getDirectionalRecommend(Long userId);
	//public int getDirectionalRecommend(Long userId,String queryConditions);
   	public AfAdvertiseDto getDirectionalRecommendInfo(String positionCode,Long userId); 
}
