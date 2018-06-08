package com.ald.fanbei.api.biz.service;

import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdBizType;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayBo;
import com.ald.fanbei.api.biz.bo.thirdpay.ThirdPayTypeEnum;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.biz.service.impl.AfResourceServiceImpl.BorrowLegalCfgBean;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 *
 * @类描述：
 *
 * @author Xiaotianjian 2017年1月20日上午10:08:53
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfResourceService {

	/**
	 * 本地缓存取配置
	 *
	 * @return
	 */
	List<AfResourceDo> getLocalByType(String type);

	/**
	 * 清除本地库数据
	 */
	void cleanLocalCache();

	/**
	 * 获取首页配置信息
	 *
	 * @param allTypes
	 * @return
	 */
	List<AfResourceDo> getHomeConfigByAllTypes();

	/**
	 * 获取type类型的配置信息
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getConfigByTypes(String type);

	/**
	 * 根据id获取资源配置
	 *
	 * @param rid
	 * @return
	 */
	AfResourceDo getResourceByResourceId(Long rid);

	/**
	 * 获取type类型的配置信息
	 *
	 * @param type
	 * @return
	 */
	AfResourceDo getConfigByTypesAndSecType(String type, String secType);

	/**
	 * 根据type获取资源列表
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByType(String type);

	/**
	 * 根据type获取资源排序列表
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceListByTypeOrderBy(String type);

	/**
	 * 根据type获取资源排序列表
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceHomeListByTypeOrderBy(String type);

	/**
	 * 根据type获取资源排序列表 在预发布环境，禁用的也可以显示
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getResourceHomeListByTypeOrderByOnPreEnv(String type);

	/**
	 * 根据type获取一个资源信息
	 *
	 * @param type
	 * @return
	 */
	AfResourceDo getSingleResourceBytype(String type);

	List<AfResourceDo> getOneToManyResourceOrderByBytype(String type);

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
	/*
	 * 获取借款手续费等信息
	 */

	BorrowRateBo borrowRateWithResource(Integer realTotalNper,String userName,Long goodId);

	/**
	 * 降息校验
	 * @param goodsid
	 * @param method
	 * @param array
	 * @return
	 */
	public JSONArray checkNper(Long goodsid, String method, JSONArray array);
	  	/*
	 * 获取信用支付手续费等信息
	 */

	BorrowRateBo borrowRateWithResourceCredit(Integer realTotalNper);
	/**
	 * 获取首页导航栏配置
	 *
	 * @return
	 */
	List<AfResourceDo> getHomeIndexListByOrderby(String type);

	JSONObject borrowRateWithResourceOld(Integer realTotalNper);

	/**
	 *
	 * @说明：根据活动的类型和活动有效期查找配置信息
	 * @param: @return
	 * @return: List<AfResourceDo>
	 */
	List<AfResourceDo> selectActivityConfig();

	/*
	 * 获取商圈借款手续费等信息
	 */

	BorrowRateBo borrowRateWithResourceForTrade(Integer realTotalNper);

	/**
	 *
	 * @说明：借贷超市的滚动条显示
	 * @param: @param code
	 * @param: @return
	 * @return: AfResourceDo
	 */
	AfResourceDo getScrollbarByType();

	List<AfResourceDo> getOneToManyResourceOrderByBytypeOnPreEnv(String code);

//	List<AfResourceDo> getHomeIndexListByOrderbyOnPreEnv(String code);
	/**
	 *
	 * @说明：在活动点亮中根据活动id获取假的人数
	 * @param: @param string
	 * @param: @return
	 * @return: AfResourceDo
	 */
	AfResourceDo getFakePersonByActivityId(String string);

	AfResourceDo getGGSpecificBanner(String value2);
	/**
	 * 获取type类型的配置信息
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getConfigsByTypesAndSecType(String type, String secType);

	List<AfResourceDo> getCarouselToManyResourceOrderByType(String code);

	List<AfResourceDo> getManyPricutresResourceDoList(String code);

	List<AfResourceDo> getNavigationUpOneResourceDoList(String code);

	List<AfResourceDo> getNavigationDownTwoResourceDoList(String code);

	AfResourceDo getAfResourceAppVesion();

	String getAfResourceAppVesionV1();

	ThirdPayBo getThirdPayBo(ThirdPayTypeEnum thirdPayTypeEnum);

	boolean checkThirdPayByType(ThirdBizType thirdBizType,ThirdPayTypeEnum thirdPayTypeEnum);


	List<AfResourceDo> getBackGroundByType(String code);

	AfResourceDo getLaunchImageInfoByTypeAndVersion(String resourceType, String appVersion);

	AfResourceDo getLaunchImageInfoByType(String resourceType);

	AfResourceDo getOpenBoluomeCouponById(long rid);

	/**
	 * 根据type获取滚动条列表
	 *
	 * @param type
	 * @return
	 */
	List<AfResourceDo> getScrollbarListByType(String type);

	List<AfResourceDo> getEcommercePositionUpRescoure();
	List<AfResourceDo> getEcommercePositionDownRescoure();

	/**
	 * 获取vip用户专有利率
	 * @param userName 用户名
	 * @return 利率相关详情
	 */
	AfResourceDo getVipUserRate(String userName);
	public AfResourceDo getBrandRate(long goodsId);
	public boolean getBorrowCashCLosed() ;
		boolean getBlackList();
	AfResourceDo getEcommerceFloorImgRes();

	AfResourceDo getBrandFloorImgRes();

	List<AfResourceDo> getHomeNomalPositionList();

	List<AfResourceDo> getHomeBrandPositonInfoList();

	AfResourceDo getFinancialEntranceInfo();
	
	AfResourceDo getWechatConfig();

	AfResourceDo getConfigByTypesAndValue(String type,String value);

	int editResource(AfResourceDo assetPushResource);
	
	/**
	 * 获取合规借钱相关的所有配置信息
	 * @return
	 */
	BorrowLegalCfgBean getBorrowLegalCfgInfo();

	/**
	 *
	 * @param type (banner的类型)
	 * @param envType (区分预发和线上)
	 * @return
	 */
	List<Object> getLoanHomeListByType();

	/**
	 * @param type (banner的类型)
	 * @param envType (区分预发和线上)
	 * @return
	 */
	List<Object> getBorrowRecycleHomeListByType();


	/**
	 * @param type (banner的类型)
	 * @param envType (区分预发和线上)
	 * @return
	 */
	List<Object> getBorrowFinanceHomeListByType();


	List<AfResourceDo> getFlowFlayerResourceConfig(String resourceType, String secType);
	List<AfResourceDo> getConfigsListByTypesAndSecType(String type, String secType);

	List<AfResourceDo> getNewSpecialResource(String type);//获取新的专场信息(未出账单列表页|已出账单列表页)
	
	String getCashProductName();

	int getIsShow(Long goodsId);

	List<AfResourceDo> getGridViewInfoUpList();

	List<AfResourceDo> getGridViewInfoCenterList();

	List<AfResourceDo> getGridViewInfoDownList();

//	AfResourceDo getEcommerceFloor();

	List<AfResourceDo> getEcommercePositionUp();

	List<AfResourceDo> getEcommercePositionDown();

	List<AfResourceDo> getBackGroundByTypeOrder(String code);

	List<AfResourceDo> getBackGroundByTypeAndStatusOrder(String code);

	/**
	 * 获取风控白名单用户
	 * @return
	 */
	List<String> getBorrowCashWhiteList();

	Map<String, Object> getRateInfo(String borrowRate, String borrowType, String tag,String secType);

	List<Object> rewardBannerList(String type,String homeBanner);
}
