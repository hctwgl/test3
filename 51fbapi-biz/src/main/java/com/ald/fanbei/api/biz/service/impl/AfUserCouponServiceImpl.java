package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.dal.dao.AfUserCouponDao;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;

/**
 * 
 *@类描述：AfUserCouponServiceImpl
 *@author 何鑫 2017年1月20日  13:07:25
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserCouponService")
public class AfUserCouponServiceImpl implements AfUserCouponService{

	@Resource
	private AfUserCouponDao afUserCouponDao;
	
	@Override
	public List<AfUserCouponDto> getUserCouponByUser(AfUserCouponQuery query) {
		return afUserCouponDao.getUserCouponByUser(query);
	}

	@Override
	public int getUserCouponByUserNouse(Long userId) {
		return afUserCouponDao.getUserCouponByUserNouse(userId);
	}

}
