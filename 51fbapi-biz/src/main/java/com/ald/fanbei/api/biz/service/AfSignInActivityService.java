package com.ald.fanbei.api.biz.service;

import java.sql.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSignInActivityDo;

public interface AfSignInActivityService {

	public List<String> initActivitySign(@Param("userId") Long userId ,@Param("activityId") Long activityId); 
	
	public Integer signIn(AfSignInActivityDo afSignInActivityDo);
	
	

}
