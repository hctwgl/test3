package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserFootmarkDo;
import com.ald.fanbei.api.dal.domain.query.AfUserFootmarkQuery;

/**
 * 
 *@类描述：AfUserFootmarkDao
 *@author 何鑫 2017年1月19日  14:57:42
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserFootmarkDao {

	/**
	 * 新增用户足迹
	 * @param userFootmark
	 * @return
	 */
	int addUserFootmark(AfUserFootmarkDo userFootmark);
	
	/**
	 * 修改用户足迹
	 * @param userFootmark
	 * @return
	 */
	int updateUserFootmark(AfUserFootmarkDo userFootmark);
	
	/**
	 * 查看用户商品足迹数据
	 * @param userId
	 * @return
	 */
	AfUserFootmarkDo getUserFootmarkByUserId(@Param("userId")Long userId,@Param("goodsId")Long goodsId);
	
	/**
	 * 获取用户足迹数据
	 * @param userId
	 * @param pageNo
	 * @return
	 */
	List<AfUserFootmarkDo> getUserFootmarkList(AfUserFootmarkQuery query);
}
