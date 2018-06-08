package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfGoodsPriceDto;

/**
 * '第三方-上树请求记录Service
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:34:26 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsPriceService extends ParentService<AfGoodsPriceDo, Long> {
	int updateStockAndSaleByPriceId(Long priceId, AfOrderDo afOrderDo, boolean isSold);

	List<AfGoodsPriceDo> getByGoodsId(Long goodsId);

	Integer selectSumStock(Long goodsId);
	AfGoodsPriceDo getGoodsPriceByGoodsId(Long goodsId);
	List<AfGoodsPriceDto> selectSumStockMap(List<AfEncoreGoodsDto> list);

	int updateNewStockAndSaleByPriceId(Long priceId,Integer count, boolean isSold);

	List<AfGoodsPriceDo> getLeaseListByGoodsId(Long goodsId);

	AfGoodsPriceDo getCheapByGoodsId(Long vipGoodsId);
}
