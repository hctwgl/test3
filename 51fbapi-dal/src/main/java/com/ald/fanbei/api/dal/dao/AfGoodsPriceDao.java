package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfGoodsPriceDto;

import org.apache.ibatis.annotations.Param;

/**
 * '第三方-上树请求记录Dao
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:34:26
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsPriceDao extends BaseDao<AfGoodsPriceDo, Long> {

	List<AfGoodsPriceDo> getByGoodsId(Long goodsId);

	AfGoodsPriceDo getGoodsPriceByGoodsId(Long goodsId);
    Integer selectSumStock(Long goodsId);

    List<AfGoodsPriceDto> selectSumStockMap(List<AfEncoreGoodsDto> list);

    int updateSell(@Param("priceId") Long priceId,@Param("count") Long count);

    int updateReturnGoods(@Param("priceId")Long priceId,@Param("count")Long count);

    List<AfGoodsPriceDo> getLeaseListByGoodsId(@Param("goodsId")Long goodsId);

	AfGoodsPriceDo getCheapByGoodsId(@Param("goodsId")Long vipGoodsId);
}
