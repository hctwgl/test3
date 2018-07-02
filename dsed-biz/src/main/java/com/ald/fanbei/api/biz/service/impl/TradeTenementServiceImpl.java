package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.TradeTenementService;
import com.ald.fanbei.api.dal.dao.TradeTenementDao;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;
import com.ald.fanbei.api.dal.domain.AfTradeTenementInfoDo;

@Service("tradeTenementService")
public class TradeTenementServiceImpl implements TradeTenementService {

	@Resource
	private TradeTenementDao tradeTenementDao;
	
	//增加租客信息
	public int addTenementInfoDo(AfTradeTenementInfoDo afTradeTenementInfoDo) {
		
		return tradeTenementDao.addTenementInfoDo(afTradeTenementInfoDo);
	}

	//查询所有租客信息
	@Override
	public List<AfTradeTenementInfoDo> getTenementInfoDo(Long businessId) {
		
		return tradeTenementDao.getTenementInfoDo(businessId);
		
	}

	//根据租客电话号码查询该租客信息
	@Override
	public AfTradeTenementInfoDo getTenementInfoDoById(Long id) {
		
		return tradeTenementDao.getTenementInfoDoById(id);
		 
	}

	//根据电话号码得到用户身份证照片
	public AfIdNumberDo getUserIdentityUrl(String mobile) {
		
		return tradeTenementDao.getUserIdentityUrl(mobile);
	}

	//更新租客信息
	public int updateTenementInfo(AfTradeTenementInfoDo afTradeTenementInfoDo) {
		
		return tradeTenementDao.updateTenementInfo(afTradeTenementInfoDo);
	}

	//根据电话号码查询用户Id
	public Long getUserIdBymobile(String mobile) {
		
		return tradeTenementDao.getUserIdBymobile(mobile);
	}

	//根据时间查询租客信息
	public List<AfTradeTenementInfoDo> getTenementInfoDoByTime(String applyTime,Long businessId) {
		
		return tradeTenementDao.getTenementInfoDoByTime(applyTime,businessId);
	}

	//根据商户id得到提交时间
	public List<String> getTimeByBusinessId(Long businessId) {
		
		return tradeTenementDao.getTimeByBusinessId(businessId);
	}

	
}
