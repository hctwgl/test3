/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfModelH5ItemService;
import com.ald.fanbei.api.dal.dao.AfModelH5ItemDao;
import com.ald.fanbei.api.dal.domain.AfModelH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserH5ItmeGoodsDto;

/**
 * @类描述：
 * @author suweili 2017年2月24日上午10:22:32
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afModelH5ItemService")
public class AfModelH5ItemServiceImpl implements AfModelH5ItemService {

	@Resource
	AfModelH5ItemDao afModelH5ItemDao;
	@Override
	public List<AfModelH5ItemDo> getModelH5ItemListByModelIdAndModelType(Long modelId, String type) {
		return afModelH5ItemDao.getModelH5ItemListByModelIdAndModelType(modelId, type);
	}

	
	@Override
	public List<AfModelH5ItemDo> getModelH5ItemCategoryListByModelId(Long modelId) {
		return afModelH5ItemDao.getModelH5ItemCategoryListByModelId(modelId);
	}


	@Override
	public List<AfTypeCountDto> getModelH5ItemGoodsCountListCountByModelIdAndSort(Long modelId) {
		return afModelH5ItemDao.getModelH5ItemGoodsCountListCountByModelIdAndSort(modelId);
	}


	@Override
	public List<AfUserH5ItmeGoodsDto> getModelH5ItemGoodsListCountByModelIdAndCategory(Long modelId,  String category, Integer start,
			Integer end) {
		return afModelH5ItemDao.getModelH5ItemGoodsListCountByModelIdAndCategory(modelId, category, start, end);
	}


	
	@Override
	public List<AfModelH5ItemDo> getModelH5ItemCategoryListByModelIdAndModelType(Long modelId) {
		return afModelH5ItemDao.getModelH5ItemCategoryListByModelIdAndModelType(modelId);
	}


	@Override
	public List<AfUserH5ItmeGoodsDto> getModelH5ItemGoodsListCountByModelId(Long modelId, Integer start, Integer end) {
		return afModelH5ItemDao.getModelH5ItemGoodsListCountByModelId(modelId, start, end);
	}

}
