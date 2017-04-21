/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfUserAddressService;
import com.ald.fanbei.api.dal.dao.AfUserAddressDao;
import com.ald.fanbei.api.dal.domain.AfUserAddressDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午9:01:51
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afUserAddressService")
public class AfUserAddressServiceImpl implements AfUserAddressService {

	@Resource
	AfUserAddressDao afUserAddressDao;
	
	@Override
	public int addUserAddress(AfUserAddressDo afUserAddressDo) {
		return afUserAddressDao.addUserAddress(afUserAddressDo);
	}


	@Override
	public int updateUserAddress(AfUserAddressDo afUserAddressDo) {
		
		return afUserAddressDao.updateUserAddress(afUserAddressDo);
	}

	
	@Override
	public List<AfUserAddressDo> selectUserAddressByUserId(Long userId) {
		return afUserAddressDao.selectUserAddressByUserId(userId);
	}


	@Override
	public AfUserAddressDo selectUserAddressByrid(Long rid) {
		return afUserAddressDao.selectUserAddressByrid(rid);
	}

	
	@Override
	public AfUserAddressDo selectUserAddressDefaultByUserId(Long userId) {
		return afUserAddressDao.selectUserAddressDefaultByUserId(userId);
	}


	
	@Override
	public int deleteUserAddress(Long rid) {
		return afUserAddressDao.deleteUserAddress(rid);
	}


	@Override
	public int reselectTheDefaultAddress(Long userId) {
		
		return afUserAddressDao.reselectTheDefaultAddress(userId);
	}




}
