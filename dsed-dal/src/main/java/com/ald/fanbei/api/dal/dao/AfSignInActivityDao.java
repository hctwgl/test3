package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSignInActivityDo;

public interface AfSignInActivityDao {

	public List<Date> initActivitySign(@Param("userId") Long userId ,@Param("activityId") Long activityId); 
	
	public Integer singIn(AfSignInActivityDo afSignInActivityDo);
}
