package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfFacescoreImgDo;
import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.dal.domain.AfUserAndRedRelationDo;

/**
 * 颜值测试红包表实体类Service
 * 
 * @author liutengyuan
 * @version 1.0.0 初始化
 * @date 2018-03-12 16:37:31
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfFacescoreRedService extends ParentService<AfFacescoreRedDo, Long>{
	int addUserAndRedRecord(AfUserAndRedRelationDo afUserAndRedRelationDo);

	int findUserAndRedRelationRecordByUserId(Long userId);

	AfFacescoreRedDo getImageUrlByUserId(Long userId);

	int addRed(AfFacescoreRedDo redDo);

	int findUserAndRedRelationRecordByRedId(long redId);

	List<AfFacescoreImgDo> findRedImg();
}
