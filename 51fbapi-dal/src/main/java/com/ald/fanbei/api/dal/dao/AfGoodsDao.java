package com.ald.fanbei.api.dal.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.dal.domain.AfActivityDo;
import com.ald.fanbei.api.dal.domain.dto.AfActivityGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.AfEncoreGoodsDto;
import com.ald.fanbei.api.dal.domain.dto.HomePageSecKillGoods;
import com.ald.fanbei.api.dal.domain.dto.LeaseGoods;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsDoQuery;
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
	
	/**
	 * 修改商品订阅状态
	 * @param messageContent
	 * @return
	 */
	int updateSubscribeStatus(@Param("numId")String numId, @Param("status")String status, @Param("publishStatus")String publishStatus);
	
	/**
	 * 更新商品淘宝信息
	 * @param afGoodsDo
	 * @return
	 */
	int updateTaobaoGoodsInfo(AfGoodsDo afGoodsDo);
	
	/**
	 * 下架淘宝商品
	 * @param messageContent
	 * @return
	 */
	int cancelPublishGoods(@Param("numId")String numId);
	
	/**
	 * 通过商品numid获取商品信息
	 * @param rid
	 * @return
	 */
	AfGoodsDo getGoodsByNumId(@Param("numId")String numId);
	/**
	 * 下单修改销售量
	 * @param rid
	 * @param addSaleCount
	 * @return
	 */
	int updateSelfSupportGoods(@Param("rid")Long rid,@Param("addSaleCount")Integer addSaleCount);

	/**
	 * 根据numId查询是否有自建商品
	 * @author yuyue
	 * @Time 2017年9月12日 下午6:47:44
	 * @param numId
	 * @return
	 */
	AfGoodsDo checkIsSelfBuild(String numId);
	List<AfGoodsDo> listGoodsListByParentIdAndFormerCategoryId(@Param("parentId")Long parentId);

	List<AfGoodsDo> listGoodsListByPrimaryCategoryIdAndCategoryId(@Param("primaryCategoryId")Long primaryCategoryId,@Param("categoryId") Long categoryId);
	List<AfEncoreGoodsDto> selectFlashSaleGoods(AfGoodsQuery query);

	List<AfEncoreGoodsDto> selectBookingRushGoods(AfGoodsQuery query);
	List<AfGoodsDo> getGoodsByCategoryId(Long categoryId);

	List<AfGoodsDo> getHomeCategoryGoodsList(AfGoodsQuery query);

	List<AfGoodsDo> getGoodsByModelId(@Param("categoryId")Long categoryId);

	List<AfGoodsDo> getHomeGoodsByModelId(AfGoodsQuery query);

	List<AfGoodsDo> getGoodsVerifyByCategoryId(AfGoodsQuery query);
	
	List<AfGoodsDo> listGoodsListByParentIdFromSubjectGoods(long parentId);

	List<AfGoodsDo> listGoodsListBySubjectId(@Param("subjectId")Long subjectId);

	List<AfGoodsDo> getAvaliableSelfGoods(AfGoodsDoQuery query);

	AfGoodsDo getAvaliableSelfGoodsBySolr(AfGoodsDoQuery query);

	List<AfGoodsDo> getGoodsByItem(@Param("categoryId") Long categoryId);


	List<LeaseGoods> getHomeLeaseGoods(@Param("pageIndex")Long pageIndex,@Param("pageSize")Long pageSize);

	LeaseGoods getLeaseGoodsByGoodsId(@Param("goodsId")Long goodsId);

	List<AfGoodsDo> getGoodsListByGoodsId(List goodsId);

	List<AfActivityGoodsDto> getGoodsDoByGoodsId(@Param("goodsId")String goodsId);
	/**
	 * 根据品牌id查询出该品牌下所有的商品
	 * @param brandId
	 * @return
	 */
	List<AfGoodsDo> getGoodsListByBrandId(Long brandId);
	/**
	 * 爱上街根据三级类目id查询所有商品 销量降序
	 * @param goodsQuery 
	 * @return
	 */
	List<HomePageSecKillGoods> getGoodsVerifyByCategoryIdAndVolume(AfGoodsQuery goodsQuery);
	/**
	 * 查询出该品牌下的所有商品 按照销量进行降序
	 * @param brandId
	 * @return
	 */
	List<HomePageSecKillGoods> getAllByBrandIdAndVolume(Long brandId);

	List<HomePageSecKillGoods> getGoodsByCategoryIdAndPrice(AfGoodsQuery goodsQuery);

	List<AfGoodsDo> getAvaliableSelfGoodsForSort(AfGoodsDoQuery query);

	List<HashMap> getVisualGoodsByGoodsId(@Param("ids")List<String> ids);

	List<Map<String, Object>> getGoodsByIds(@Param("list") List<Long> goodList);

	List<Long> getGoodsisGlobal3(@Param("list") List<Long> goodsIdList);

    List<Long> getGoodsisGlobal1(@Param("modelId") Long modelId);

	List<AfGoodsDo> getAfGoodsListByBrandId(AfGoodsQuery goodsQuery);

	List<HashMap> getTaskGoodsList(AfGoodsQuery goodsQuery);
}
