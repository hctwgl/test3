package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;

public interface TradeTenementDao {

	int addTenementInfoDo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	List<AfTradeTenementInfoDo> getTenementInfoDo(Long businessId);
	
	AfTradeTenementInfoDo getTenementInfoDoById(Long id);
	
	AfIdNumberDo getUserIdentityUrl(String mobile);
	
	int updateTenementInfo(AfTradeTenementInfoDo afTradeTenementInfoDo);
	
	Long getUserIdBymobile(String mobile);
	
	List<AfTradeTenementInfoDo> getTenementInfoDoByTime(@Param("applyTime") String applyTime,@Param("businessId") long businessId);
	
	List<String> getTimeByBusinessId(Long businessId);
}
