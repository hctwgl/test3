package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecycleViewService;
import com.ald.fanbei.api.dal.dao.AfRecycleViewDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleViewQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @类描述： 有得卖  回收业务 页面访问
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRecycleViewService")
public class AfRecycleViewServiceImpl implements AfRecycleViewService {
	@Autowired
	private AfRecycleViewDao afRecycleViewDao;


	@Override
	public Integer addRecycleView(AfRecycleViewQuery afRecycleviewQuery) {
		return afRecycleViewDao.addRecycleView(afRecycleviewQuery);
	}

	@Override
	public Integer updateRecycleView(AfRecycleViewQuery afRecycleviewQuery) {
		return afRecycleViewDao.updateRecycleView(afRecycleviewQuery);
	}

	@Override
	public AfRecycleViewDo getRecycleViewByUid(AfRecycleViewQuery afRecycleViewQuery) {
		return afRecycleViewDao.getRecycleViewByUid(afRecycleViewQuery);
	}


}
