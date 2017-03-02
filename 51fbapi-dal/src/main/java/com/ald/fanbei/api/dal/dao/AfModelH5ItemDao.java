/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;

/**
 * @类描述：
 * @author suweili 2017年2月23日下午10:32:19
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfModelH5ItemDao {
	/**
	 * 获取某种类型的modelH5Item对象列表
	 * @param modelId
	 * @param modelType
	 * @return
	 */
	List<AfModelH5ItemDo> getModelH5ItemListByModelIdAndModelType(@Param("modelId")Long modelId,@Param("type")String modelType);
	
	/** 
	 * 获取分类以及分类下面的商品列表
	 * @param modelId
	 * @return
	 */
	List<AfModelH5ItemDo> getModelH5ItemCategoryListByModelId(@Param("modelId")Long modelId);
	
	List<AfModelH5ItemDo> getModelH5ItemGoodsListCountByModelIdAndCategory(@Param("modelId")Long modelId,@Param("category")String category,@Param("start")Integer start, @Param("end")Integer end);
	
	List<AfTypeCountDto> getModelH5ItemGoodsCountListCountByModelIdAndSort(@Param("modelId")Long modelId);
	
}
