package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfResourceDo;

/**
 * @类描述：
 * 
 * @author Xiaotianjian 2017年1月20日上午10:08:53
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceDao {
	/**
	 * 获取首页配置信息
	 * @return
	 */
	List<AfResourceDo> selectHomeConfigByAllTypes();

	/**
	 * 获取type类型的配置信息
	 * @param typeList
	 * @return
	 */
	List<AfResourceDo> getConfigByTypeList(@Param("typeList") List<String> typeList);

	/**
	 * 获取type类型的配置信息
	 * 
	 * @param type
	 * @return
	 */

	List<AfResourceDo> getConfigByTypes(@Param("type") String type);

	/**
	 * 根据id获取资源配置
	 * 
	 * @param rid
	 * @return
	 */
	AfResourceDo getResourceByResourceId(@Param("rid") Long rid);

	/**
	 * 获取type类型的配置信息
	 * 
	 * @param type
	 * @return
	 */

	AfResourceDo getConfigByTypesAndSecType(@Param("type") String type, @Param("secType") String secType);

	/**
	 * 借钱模块配置信息
	 * 
	 * @return
	 */
	List<AfResourceDo> selectBorrowHomeConfigByAllTypes();

	/**
	 * 借钱模块配置信息
	 *
	 * @return
	 */
	List<AfResourceDo> newSelectBorrowHomeConfigByAllTypes();

	/**
	 * 根据type获取资源列表
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByType(@Param("type") String type);

	/**
	 * 根据type获取资源排序列表
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByTypeOrderBy(@Param("type") String type);

	/**
	 * 根据type获取资源排序列表首页
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceHomeListByTypeOrderBy(@Param("type") String type);

	/**
	 * 根据type获取资源排序列表首页
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceHomeListByTypeOrderByOnPreEnv(@Param("type") String type);

	/**
	 * 根据type获取一个资源信息
	 * 
	 * @param type
	 * @return
	 */
	AfResourceDo getSingleResourceBytype(@Param("type") String type);

	/**
	 * 根据type查询一对多模块一对一模块一对二模块
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getOneToManyResourceOrderByBytype(@Param("type") String type);

	/**
	 * 获取首页导航栏模块
	 * 
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getHomeIndexListByOrderby(@Param("type") String type);

	/**
	 * 
	 * @说明：根据活动的类型和活动有效期查找配置信息
	 * @param: @return
	 * @return: List<AfResourceDo>
	 */
	List<AfResourceDo> selectActivityConfig();

	List<AfResourceDo> getScrollbarByType();

	List<AfResourceDo> getOneToManyResourceOrderByBytypeOnPreEnv(String code);

	//List<AfResourceDo> getHomeIndexListByOrderbyOnPreEnv(String code);
	
	List<AfResourceDo> getActivieResourceByType(@Param("type") String type);
	List<AfResourceDo> getFakePersonByActivityId(@Param("activityId")String activityId);

	AfResourceDo getGGSpecificBanner(@Param("value2")String value2);

	List<AfResourceDo> getConfigsByTypesAndSecType(@Param("type")String type,@Param("secType")String secType);

	List<AfResourceDo> getCarouselToManyResourceOrderByType(@Param("type")String type);

	List<AfResourceDo> getManyPricutresResourceDoList(@Param("type")String type);

	List<AfResourceDo> getNavigationUpOneResourceDoList(@Param("type")String type);

	List<AfResourceDo> getNavigationDownTwoResourceDoList(@Param("type")String type);

    List<AfResourceDo> getBackGroundByType(String code);
	/**
	 * 	查询一个活动规则信息
	 */
	List<String> getActivityRule(@Param("type")String type);

	AfResourceDo getLaunchImageInfoByTypeAndVersion(@Param("type")String resourceType, @Param("version")String appVersion);

	AfResourceDo getLaunchImageInfoByType(@Param("type")String resourceType);

	AfResourceDo getOpenBoluomeCouponById(@Param("rid") Long rid);

	/**
	 * 根据type获取滚动条列表
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getScrollbarListByType(String type);

	List<AfResourceDo> getEcommercePositionUpRescoure();

	List<AfResourceDo> getEcommercePositionDownRescoure();

	AfResourceDo getEcommerceFloorImgRes();

	AfResourceDo getBrandFloorImgRes();

	List<AfResourceDo> getHomeNomalPositionList();

	List<AfResourceDo> getHomeBrandPositonInfoList();

	AfResourceDo getFinancialEntranceInfo();

	/**
	 * 根据type和value获取一个resoueceDo
	 * @param type
	 * @param value
	 * @return
	 */
	AfResourceDo getConfigByTypesAndValue(@Param("type") String type, @Param("value")String value);

	int editResource(AfResourceDo assetPushResource);
	
	AfResourceDo getConfigByType(@Param("type") String type);//根据类型获取单个配置信息

	List<AfResourceDo> getFlowFlayerResourceConfig(@Param("type")String resourceType, @Param("secType")String secType);
		List<AfResourceDo> getConfigsListByTypesAndSecType(@Param("type")String type,@Param("secType") String secType);

	List<AfResourceDo> getNewSpecialResource(@Param("type") String type);//获取新的专场信息(未出账单列表页|已出账单列表页)

	List<AfResourceDo> getGridViewInfoCenterList();

	List<AfResourceDo> getGridViewInfoDownList();

	List<AfResourceDo> getGridViewInfoUpList();

//	AfResourceDo getEcommerceFloor();

	List<AfResourceDo> getEcommercePositionUp();

	List<AfResourceDo> getEcommercePositionDown();

	List<AfResourceDo> getBackGroundByTypeOrder(@Param("type") String code);

	List<AfResourceDo> getBackGroundByTypeAndStatusOrder(@Param("type") String code);
}
