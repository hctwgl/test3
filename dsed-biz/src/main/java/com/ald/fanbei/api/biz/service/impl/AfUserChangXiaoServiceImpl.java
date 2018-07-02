/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfUserChangXiaoService;
import com.ald.fanbei.api.dal.dao.AfUserChangXiaoDao;
import com.ald.fanbei.api.dal.domain.query.AfUserChangXiaoDo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @类描述：畅效广告平台与广告主对接接口
 * @author weiqingeng
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afThirdChangXiaoService")
public class AfUserChangXiaoServiceImpl implements AfUserChangXiaoService {

	@Resource
	AfUserChangXiaoDao afUserChangXiaoDao;

	@Override
	public int insertChangxiaoUser(AfUserChangXiaoDo afUserChangXiaoDo) {
		return afUserChangXiaoDao.insertChangXiaoUser(afUserChangXiaoDo);
	}

	@Override
	public AfUserChangXiaoDo getUser(AfUserChangXiaoDo afUserChangXiaoDo) {
		return afUserChangXiaoDao.getUser(afUserChangXiaoDo);
	}

	@Override
	public AfUserChangXiaoDo getUserOpen(AfUserChangXiaoDo afUserChangXiaoDo) {
		return afUserChangXiaoDao.getUserOpen(afUserChangXiaoDo);
	}

	@Override
	public int updateUserOpen(AfUserChangXiaoDo afUserChangXiaoDo) {
		return afUserChangXiaoDao.updateUserOpen(afUserChangXiaoDo);
	}

	@Override
	public int updateUser(AfUserChangXiaoDo afUserChangXiaoDo) {

		return afUserChangXiaoDao.updateUser(afUserChangXiaoDo);
	}


}
