package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;

public interface TradeTenementDao {

	int addTenementInfoDo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	List<AfTradeTenementInfoDo> getTenementInfoDo(Long businessId);
	
	AfTradeTenementInfoDo getTenementInfoDoBymobile(String mobile);
	
	AfIdNumberDo getUserIdentityUrl(String mobile);
	
	int updateTenementInfo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	Long getUserIdBymobile(String mobile);
	
	List<AfTradeTenementInfoDo> getTenementInfoDoByTime(String applyTime);
	
	List<String> getTimeByBusinessId(Long businessId);
}
