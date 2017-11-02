/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;

/**
 * @类描述：
 * @author suweili 2017年2月25日下午2:19:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserCollectionDao {
	/**
	 * 添加收藏
	 * @param afUserCollectionDo
	 * @return
	 */
	int addUserCollectionGoods(AfUserCollectionDo afUserCollectionDo);
	/***
	 * 修改收藏
	 * @param afUserCollectionDo
	 * @return
	 */
	int updateUserCollectionGoods(AfUserCollectionDo afUserCollectionDo);
	/**
	 * 根据userId获取用户收藏列表
	 * @param userId
	 * @return
	 */
	List<AfUserCollectionDo> getUserCollectionListByUserId(@Param("userId")Long userId);
	
	List<AfUserCollectionDo> getUserGoodsIdCollectionListByUserId(@Param("userId")Long userId);

	
	/**
	 * 根据收藏id获取收藏信息
	 * @param rid
	 * @return
	 */
	AfUserCollectionDo getUserCollectionListById(@Param("rid") Long rid);
	
	int deleteUserCollectionGoods(AfUserCollectionDo afUserCollectionDo);

	Integer getUserCollectionCountByGoodsId(AfUserCollectionDo afUserCollectionDo);
}
