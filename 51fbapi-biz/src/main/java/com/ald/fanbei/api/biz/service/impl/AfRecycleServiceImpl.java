/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.dal.dao.AfRecycleDao;
import com.ald.fanbei.api.dal.domain.AfRecycleDo;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 * @类描述： 有得卖  回收业务
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRecycleService")
public class AfRecycleServiceImpl implements AfRecycleService {
	@Resource
	private AfRecycleDao afRecycleDao;
	@Override
	public Integer addRecycleOrder(AfRecycleQuery afRecycleQuery) {
		return afRecycleDao.addRecycleOrder(afRecycleQuery);
	}

	@Override
	public AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery) {
		return afRecycleDao.getRecycleOrder(afRecycleQuery);
	}
}
