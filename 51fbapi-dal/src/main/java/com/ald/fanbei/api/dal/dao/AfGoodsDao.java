package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;

/**
 * @类描述：
 * @author hexin 2017年2月17日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfGoodsDao {

	/**
	 * 通过商品id获取商品信息
	 * @param rid
	 * @return
	 */
	AfGoodsDo getGoodsById(@Param("rid")Long rid);
	
	/**
	 * 通过openId获取商品信息
	 * @param openId
	 * @return
	 */
	AfGoodsDo getGoodsByOpenId(@Param("openId")String openId);
	
	/**
	 * 获取类目商品列表
	 * @param query
	 * @return
	 */
	List<AfGoodsDo> getCateGoodsList(AfGoodsQuery query);
}
