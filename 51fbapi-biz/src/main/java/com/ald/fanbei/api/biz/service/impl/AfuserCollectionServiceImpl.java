/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.dal.dao.AfUserCollectionDao;
import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;

/**
 * @类描述：
 * @author suweili 2017年2月25日下午2:26:53
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("AfuserCollectionService")
public class AfuserCollectionServiceImpl implements AfuserCollectionService {

	@Resource
	AfUserCollectionDao  afUserCollectionDao;
	@Override
	public int addUserCollectionGoods(AfUserCollectionDo afUserCollectionDo) {
		return afUserCollectionDao.addUserCollectionGoods(afUserCollectionDo);
	}


	@Override
	public int updateUserCollectionGoods(AfUserCollectionDo afUserCollectionDo) {
		return afUserCollectionDao.updateUserCollectionGoods(afUserCollectionDo);
	}

	
	@Override
	public List<AfUserCollectionDo> getUserCollectionListByUserId(Long userId) {
		return afUserCollectionDao.getUserCollectionListByUserId(userId);
	}

	
	@Override
	public AfUserCollectionDo getUserCollectionListById(Long rid) {
		return afUserCollectionDao.getUserCollectionListById(rid);
	}

	@Override
	public int deleteUserCollectionGoods(AfUserCollectionDo afUserCollectionDo) {
		return afUserCollectionDao.deleteUserCollectionGoods(afUserCollectionDo);
	}


	@Override
	public Integer getUserCollectionCountByGoodsId(AfUserCollectionDo afUserCollectionDo) {
		return afUserCollectionDao.getUserCollectionCountByGoodsId(afUserCollectionDo);
	}

}
