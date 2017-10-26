package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;

public interface TradeTenementService {

	int addTenementInfoDo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	List<AfTradeTenementInfoDo> getTenementInfoDo(Long businessId);
	
	AfTradeTenementInfoDo getTenementInfoDoById(Long id);
	
	AfIdNumberDo getUserIdentityUrl(String mobile);
	
	int updateTenementInfo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	Long getUserIdBymobile(String mobile);
	
	List<AfTradeTenementInfoDo> getTenementInfoDoByTime( String applyTime,Long businessId);
	
	List<String> getTimeByBusinessId(Long businessId);
}
