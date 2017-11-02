package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.dal.dao.AfCouponCategoryDao;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;

/**
 * 
 * @类描述：
 * @author 江荣波 2017年07月04日上午10:04:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afCouponCategoryService")
public class AfCouponCategoryServiceImpl implements AfCouponCategoryService {

	@Resource
	AfCouponCategoryDao afCouponCategoryDao;

	@Override
	public int addCouponCategory(AfCouponCategoryDo afCouponCategoryDo) {
		return afCouponCategoryDao.addCouponCategory(afCouponCategoryDo);
	}

	@Override
	public List<AfCouponCategoryDo> listAllCouponCategory() {
		return afCouponCategoryDao.listAllCouponCategory();
	}

	@Override
	public int deleteCouponCategory(String couponCategoryId) {
		return afCouponCategoryDao.deleteCouponCategory(couponCategoryId);
	}

	@Override
	public AfCouponCategoryDo getCouponCategoryById(String couponCategoryId) {
		return afCouponCategoryDao.getCouponCategoryById(couponCategoryId);
	}

	@Override
	public int updateCouponCategory(AfCouponCategoryDo afCouponCategoryDo) {
		return afCouponCategoryDao.updateCouponCategory(afCouponCategoryDo);
	}

	@Override
	public List<AfCouponCategoryDo> getCouponCategoryByCouponId(Long rid) {
		return afCouponCategoryDao.getCouponCategoryByCouponId(rid);
	}

	@Override
	public AfCouponCategoryDo getCouponCategoryAll() {
		return afCouponCategoryDao.getCouponCategoryAll();
	}
	
	

}
