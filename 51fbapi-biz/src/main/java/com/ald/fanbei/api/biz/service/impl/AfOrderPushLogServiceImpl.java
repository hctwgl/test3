package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfOrderPushLogService;
import com.ald.fanbei.api.dal.dao.AfOrderPushLogDao;
import com.ald.fanbei.api.dal.domain.AfOrderPushLogDo;

/**
 *@类描述：
 *@author xiaotianjian 2017年3月30日上午11:12:34
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afOrderPushLogService")
public class AfOrderPushLogServiceImpl implements AfOrderPushLogService {

	@Resource
	AfOrderPushLogDao afOrderPushLogDao;
	
	@Override
	public int addOrderPushLog(AfOrderPushLogDo afOrderPushLogDo) {
		return afOrderPushLogDao.addOrderPushLog(afOrderPushLogDo);
	}

	@Override
	public int updateOrderPushLog(AfOrderPushLogDo afOrderPushLogDo) {
		return afOrderPushLogDao.updateOrderPushLog(afOrderPushLogDo);
	}

}
