/**
 * 
 */
package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserH5ItmeGoodsDto;

/**
 * @类描述：
 * @author suweili 2017年2月24日上午10:15:58
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfModelH5ItemService {
	/**
	 * 获取某种类型的modelH5Item对象列表
	 * @param modelId
	 * @param modelType
	 * @return
	 */
	List<AfModelH5ItemDo> getModelH5ItemListByModelIdAndModelType(Long modelId,String type);
	
	/**
	 * 获取分类以及分类下面的商品列表
	 * @param modelId
	 * @return
	 */
	List<AfModelH5ItemDo> getModelH5ItemCategoryListByModelId(Long modelId);
	
	/**
	 * 安装sort 取modelH5Item
	 * @param modelId
	 * @param sort
	 * @param start
	 * @param end
	 * @return
	 */
	List<AfUserH5ItmeGoodsDto> getModelH5ItemGoodsListCountByModelIdAndCategory(Long modelId,String category,Integer start, Integer end);

	
	List<AfTypeCountDto> getModelH5ItemGoodsCountListCountByModelIdAndSort(Long modelId);
}
