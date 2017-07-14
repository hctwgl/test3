/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.io.Serializable;
import java.util.List;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月28日 下午1:57:30
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface BaseDao<T, ID extends Serializable> {
	
	/**
	 * 插入数据，返回对象，包含主键ID
	 * @param t
	 * @return
	 */
	int saveRecord(T t);
	
	/**
	 * 通过id更新记录
	 * @param id
	 * @return
	 */
	int updateById(T t);
	
	/**
	 * 通过普通条件查询对象（表字段对应的条件）
	 * @param t
	 * @return
	 */
	T getById(ID id);
	
	/**
	 * 通过普通条件查询对象（表字段对应的条件）
	 * @param t
	 * @return
	 */
	T getByCommonCondition(T t);
	
	/**
	 * 通过普通条件查询对象列表（表字段对应的条件）
	 * @param t
	 * @return
	 */
	List<T> getListByCommonCondition(T t);
}
