package com.ald.fanbei.api.biz.service;

import java.util.Date;
import java.util.List;


import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsService {

	/**
	 * 获取类目商品信息
	 * @param query
	 * @return
	 */
	List<AfGoodsDo> getCateGoodsList(AfGoodsQuery query);
	
	/**
	 * 通过商品id获取商品信息
	 * @param rid
	 * @return
	 */
	AfGoodsDo getGoodsById(Long rid);
	
	/**
	 * 通过商品numid获取商品信息
	 * @param rid
	 * @return
	 */
	AfGoodsDo getGoodsByNumId(String numId);
	
	/**
	 * 订阅商品
	 * @param messageContent
	 * @return
	 */
	
	int subscribeGoods(String messageContent);
	
	/**
	 * 取消订阅商品
	 * @param messageContent
	 * @return
	 */
	
	int unSubscribeGoods(String messageContent);
	
	/**
	 * 淘宝商品，价格,主图,title发生变化，进行修改
	 * @param messageContent
	 * @return
	 */
	int updateTaobaoInfo(String messageContent, String messageType);
	
	/**
	 * 淘宝商品下架
	 * @param messageContent
	 * @return
	 */
	int cancelPublishGoods(String messageContent);
	/**
	 * 下单修改销售量
	 * @param rid
	 * @param addSaleCount
	 * @return
	 */
	int updateSelfSupportGoods(Long rid,Integer addSaleCount);

	/**
	 * 根据numId查看是否有自建商品
	 * @author yuyue
	 * @Time 2017年9月12日 下午6:46:18
	 * @param numId
	 * @return
	 */
	AfGoodsDo checkIsSelfBuild(String numId);

/**
	 * 限时抢购 的 商品列表
	 * @return
	 */
	List<AfEncoreGoodsDto> selectFlashSaleGoods(AfGoodsQuery query);

	List<AfEncoreGoodsDto> selectBookingRushGoods(AfGoodsQuery query);
	List<AfGoodsDo> getGoodsByCategoryId(Long categoryId);

	List<AfGoodsDo> getHomeCategoryGoodsList(AfGoodsQuery query);

	List<AfGoodsDo> getGoodsByModelId (Long categoryId);

	List<AfGoodsDo> getHomeGoodsByModelId (AfGoodsQuery query);

	List<AfGoodsDo> getGoodsVerifyByCategoryId(AfGoodsQuery query);
}
