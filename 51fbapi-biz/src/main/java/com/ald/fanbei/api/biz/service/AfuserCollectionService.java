/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;

/**
 * @类描述：
 * @author suweili 2017年2月25日下午2:26:00
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfuserCollectionService {
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
	List<AfUserCollectionDo> getUserCollectionListByUserId(Long userId);
	/**
	 * 根据userId获取用户收藏列表
	 * @param userId
	 * @return
	 */
	List<AfUserCollectionDo> getUserGoodsIdCollectionListByUserId(Long userId);
	
	/**
	 * 根据收藏id获取收藏信息
	 * @param rid
	 * @return
	 */
	AfUserCollectionDo getUserCollectionListById( Long rid);
	
	int deleteUserCollectionGoods(AfUserCollectionDo afUserCollectionDo);
	
    Integer getUserCollectionCountByGoodsId(AfUserCollectionDo afUserCollectionDo);

}
