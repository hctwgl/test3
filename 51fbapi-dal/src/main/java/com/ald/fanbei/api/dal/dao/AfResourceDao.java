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
	 * 
	 * @param allTypes
	 * @return
	 */
	List<AfResourceDo> selectHomeConfigByAllTypes();

	/**
	 * 获取type类型的配置信息
	 * 
	 * @param type
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

	List<AfResourceDo> getFakePersonByActivityId(@Param("activityId")String activityId);

	AfResourceDo getGGSpecificBanner(@Param("value2")String value2);

}
