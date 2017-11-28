/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.dal.dao.AfIdNumberDao;
import com.ald.fanbei.api.dal.domain.AfIdNumberDo;

/**
 * @类描述：
 * @author suweili 2017年4月17日下午6:37:24
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afIdNumberService")
public class AfIdNumberServiceImpl implements AfIdNumberService {

	@Resource
	AfIdNumberDao afIdNumberDao;
	
	@Override
	public int addIdNumber(AfIdNumberDo afIdNumberDo) {
		return afIdNumberDao.addIdNumber(afIdNumberDo);
	}

	
	@Override
	public int updateIdNumber(AfIdNumberDo afIdNumberDo) {
		return afIdNumberDao.updateIdNumber(afIdNumberDo);
	}

	@Override
	public AfIdNumberDo selectUserIdNumberByUserId(Long userId) {
		return afIdNumberDao.selectUserIdNumberByUserId(userId);
	}

}
