package com.ald.fanbei.api.biz.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
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
	AfGoodsDo getGoodsById(@Param("rid")Long rid);
	
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
	 * 根据商品numId获取规则json
	 * @param numId
	 * @return
	 */
	String getInterestFreeRuleJsonByGoodsNumId(String numId);
}
