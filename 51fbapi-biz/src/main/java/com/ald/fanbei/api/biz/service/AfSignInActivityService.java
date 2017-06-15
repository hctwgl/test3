package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfSignInActivityDo;

public interface AfSignInActivityService {

	public List<Date> initActivitySign(Long userId ,Long activityId); 
	
	public Integer signIn(AfSignInActivityDo afSignInActivityDo);
	
	

}
