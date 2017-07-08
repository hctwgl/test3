/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.ParentService;
import com.ald.fanbei.api.dal.dao.BaseDao;

/**
 * @类现描述：
 * @author chenjinhu 2017年6月28日 下午1:21:52
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public abstract class ParentServiceImpl<T, ID extends Serializable> implements ParentService<T, ID> {
	@Resource
	private BaseDao<T, ID> baseDao;
	
	@Override
	public int saveRecord(T t) {
		return getDao().saveRecord(t);
	}

	@Override
	public int updateById(T t) {
		return getDao().updateById(t);
	}
	
	@Override
	public T getById(ID id) {
		return getDao().getById(id);
	}

	@Override
	public T getByCommonCondition(T t) {
		return getDao().getByCommonCondition(t);
	}

	@Override
	public List<T> getListByCommonCondition(T t) {
		return getDao().getListByCommonCondition(t);
	}
	
	public abstract BaseDao<T, ID> getDao();
}
