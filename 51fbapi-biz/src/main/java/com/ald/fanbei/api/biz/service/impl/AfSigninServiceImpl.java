/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSigninService;
import com.ald.fanbei.api.dal.dao.AfSigninDao;
import com.ald.fanbei.api.dal.domain.AfSigninDo;

/**
 * @类描述：
 * @author suweili 2017年2月7日下午5:04:16
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("AfSigninService")
public class AfSigninServiceImpl implements AfSigninService {

	@Resource
	AfSigninDao afSigninDao;

	@Override
	public int addSignin(AfSigninDo afSigninDo) {
		return afSigninDao.insertSignin(afSigninDo);
	}

	
	@Override
	public int changeSignin(AfSigninDo afSigninDo) {
		return afSigninDao.updateSignin(afSigninDo);
	}


	@Override
	public AfSigninDo selectSigninByUserId(Long userId) {
		return afSigninDao.selectSigninByUserId(userId);
	}

}
