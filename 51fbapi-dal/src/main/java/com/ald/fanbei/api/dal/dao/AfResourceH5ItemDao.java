package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfResourceH5ItemDo;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.dal.domain.dto.AfTypeCountDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserH5ItmeGoodsDto;
import com.ald.fanbei.api.dal.domain.query.ResourceH5ItemQuery;

/**
 * h5商品资源管理Dao
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceH5ItemDao extends BaseDao<AfResourceH5ItemDo, Long> {
	List<AfResourceH5ItemDto> listGoods(ResourceH5ItemQuery query);
	int addResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo);
	int updateResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo);
	int deleteResourceH5Item(AfResourceH5ItemDo afResourceH5ItemDo);
	AfResourceH5ItemDo getById(Long id);
	List<Long> listAllGoodsIdByModelIdAndCategoryId(Long modelId,
			Long categoryId);
	void batchAddResourceH5Item(List<AfResourceH5ItemDo> addGoodsList);
	List<AfResourceH5ItemDto> selectByModelId(Long id);
}
