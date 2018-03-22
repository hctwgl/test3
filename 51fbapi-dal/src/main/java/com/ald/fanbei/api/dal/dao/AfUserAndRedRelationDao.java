package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfFacescoreRedDo;
import com.ald.fanbei.api.dal.domain.AfUserAndRedRelationDo;


public interface AfUserAndRedRelationDao {
	/**
	 * 增加记录
	 * 
	 * @param afUserAndRedRelationDo
	 * @return
	 */
	int addUserAndRedRelation(AfUserAndRedRelationDo afUserAndRedRelationDo);

	int findUserAndRedRelationRecordByUserId(Long userId);

	int findUserAndRedRelationRecordByRedId(long redId);


	
	
}
