package com.ald.fanbei.api.common;

/**
 * @类现描述：
 * 
 * @author hy 2017年5月10日 上午9:24:03
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class CacheConstants {

	
	/**
	 * @类现描述：配置resource
	 * @author hy 2017年5月10日 上午9:29:15
	 * @version
	 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
	 */
	public static enum RESOURCE{

		RESOURCE_CONFIG_ALL_LIST("RESOURCE_CONFIG_ALL_LIST", "所有配置")
		,RESOURCE_CONFIG_TYPES_LIST("RESOURCE_CONFIG_TYPES_LIST","根据type配置")
		,RESOURCE_TYPE_LIST("RESOURCE_TYPE_LIST","根据type查询resource")
		,RESOURCE_TYPE_DO("RESOURCE_TYPE_DO","根据type获取单个resource")
		,RESOURCE_TYPE_SEC_DO("RESOURCE_TYPE_SEC_DO","根据二级type获取单个resource")
		,RESOURCE_TYPE_LIST_ORDER_BY("RESOURCE_TYPE_LIST_ORDER_BY","")
		,RESOURCE_ID_DO("RESOURCE_ID_DO","")
		,RESOURCE_ONE_TO_MANY_TYPE_LIST("RESOURCE_ONE_TO_MANY_TYPE_LIST","")
		,HOME_CAROUSEL_TO_MANY_TYPE_LIST("HOME_CAROUSEL_TO_MANY_TYPE_LIST","轮播+N配置")
		,RESOURCE_HOME_LIST_ORDER_BY("RESOURCE_HOME_LIST_ORDER_BY","")
		,RESOURCE_BORROW_CONFIG_LIST("RESOURCE_BORROW_CONFIG_LIST","")
		;

		private String code;
		private String name;

		private RESOURCE(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

	}
	
	public static enum HOME_PAGE{
		
		GET_HOME_INFO_V1_ACTIVITY_INFO_LIST("GET_HOME_INFO_V1_ACTIVITY_INFO_LIST","V1首页活动商品"),
		GET_HOME_INFO_V1_MORE_GOODS_INFO("GET_HOME_INFO_V1_MORE_GOODS_INFO","V1首页更多商品"),
		GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD("GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD","V2首页商品（旧）"),
		GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW("GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW","V2首页商品（新）");
		
		private String code;
		private String name;

		private HOME_PAGE(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static enum PART_ACTIVITY{

		GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST("GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST","V2活动商品"),
		GET_ACTIVITY_INFO_V2_PROCESS_KEY("GET_ACTIVITY_INFO_V2_PROCESS_KEY","V2会场活动线程标识位"),
		GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST_CACHE2("GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST_CACHE2","V2活动商品(二级缓存)"),
		GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST("GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST","V2会场商品"),
		GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST_CACHE2("GET_ACTIVITY_INFO_V2_ACTIVITY_PART_LIST_CACHE2","V2会场商品(二级缓存)");

		private String code;
		private String name;

		private PART_ACTIVITY(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

public static enum ASJ_HOME_PAGE{

		ASJ_CUBE_HOMEPAGE_BACKGROUND("ASJ_CUBE_HOMEPAGE_BACKGROUND","爱上街首页背景list"),
		ASJ_HOME_PAGE_CHANNEL_TAB_LIST("HOME_PAGE_CHANNEL_TAB_LIST","频道list"),
		ASJ_HOME_PAGE_TABBAR_LIST("ASJ_HOME_PAGE_TABBAR_LIST","tabbar list"),
		ASJ_HOME_PAGE_TOP_BANNER_LIST_PRE("ASJ_HOME_PAGE_TOP_BANNER_LIST_PRE","首页顶部轮播list(预发)"),

		ASJ_HOME_SLOGAN_LIST("ASJ_HOME_SLOGAN_LIST","首页SLOGAN_LIST"),
		ASJ_HOME_NAVIGATION_UP_ONE("ASJ_HOME_NAVIGATION_UP_ONE","NAVIGATION上部分"),
		ASJ_HOME_NAVIGATION_DOWN_ONE("ASJ_HOME_NAVIGATION_DOWN_ONE","NAVIGATION下部分"),
		ASJ_HOME_NEW_EXCLUSIVE("ASJ_HOME_NEW_EXCLUSIVE","新人专享运营位"),
		ASJ_HOME_NOMAL_POSITION_LIST("ASJ_HOME_NOMAL_POSITION_LIST","常驻运营位"),

		ASJ_HOME_NAVIGATION_INFO("ASJ_HOME_NAVIGATION_INFO","首页NAVIGATION"),
		ASJ_HOME_FINANCIAL_ENTRANC_INFO("ASJ_HOME_FINANCIAL_ENTRANC_INFO","金融服务入口"),
		ASJ_HOME_GRID_VIEW_INFO("ASJ_HOME_GRID_VIEW_INFO","九宫格"),
		ASJ_HOME_ECOMMERCE_AREA_INFO("ASJ_HOME_ECOMMERCE_AREA_INFO","电商运营位"),

		ASJ_HOME_NEW_PRODUCT_INFO("ASJ_HOME_NEW_PRODUCT_INFO","品质新品"),
		ASJ_HOME_ACTIVITY_GOODS_INFO("ASJ_HOME_ACTIVITY_GOODS_INFO","活动商品"),
		ASJ_HOME_BRAND_INFO("ASJ_HOME_BRAND_INFO","大牌汇聚"),
		ASJ_HOME_MORE_GOODS_PAGENO_FIRST("ASJ_HOME_MORE_GOODS_PAGENO_FIRST","首页更多商品（猜你喜欢）"),
	    ASJ_HOME_MORE_GOODS_PAGENO_SECOND("ASJ_HOME_MORE_GOODS_PAGENO_SECOND","首页更多商品（猜你喜欢）二级缓存"),
	    ASJ_HOME_MORE_GOODS_PAGENO_SECOND_PROCESS_KEY("ASJ_HOME_MORE_GOODS_PAGENO_SECOND_PROCESS_KEY","首页更多商品（猜你喜欢)线程标识"),
		ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_FIRST("ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_FIRST","频道页更多商品（猜你喜欢）"),
	   ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_SECOND("ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_SECOND","频道页更多商品（猜你喜欢）二级缓存"),
	   ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_PROCESS_KEY("ASJ_HOME_MORE_CHANNEL_GOODS_PAGENO_PROCESS_KEY","频道页更多商品（猜你喜欢）线程标识"),
		ASJ_PAY_SESULT_PAGE_GOODS_PAGENO("ASJ_PAY_SESULT_PAGE_GOODS_PAGENO","支付结果页（猜你喜欢）"),
	    ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND("ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND","支付结果页（猜你喜欢）二级缓存"),
	    ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND_PROCESS_KEY("ASJ_PAY_SESULT_PAGE_GOODS_PAGENO_SECOND_PROCESS_KEY","支付结果页（猜你喜欢）线程标识"),

		ASJ_HOME_PAGE_CHANNEL_TABID_FIRST("ASJ_HOME_PAGE_CHANNEL_TABID_FIRST","频道页一级缓存"),
	    ASJ_HOME_PAGE_CHANNEL_TABID_SECOND("ASJ_HOME_PAGE_CHANNEL_TABID_SECOND","频道页二级缓存"),
	    ASJ_HOME_PAGE_CHANNEL_TABID__PROCESS_KEY("ASJ_HOME_PAGE_CHANNEL_TABID__PROCESS_KEY","频道页线程标识"),

		ADVERTISE_HOME_NAVIGATION_UP_ONE("ADVERTISE_HOME_NAVIGATION_UP_ONE","广告——中部轮播1"),
		ADVERTISE_HOME_NAVIGATION_DOWN_ONE("ADVERTISE_HOME_NAVIGATION_DOWN_ONE","广告——中部轮播1"),
		ADVERTISE_HOME_TOP_BANNER("ADVERTISE_HOME_TOP_BANNER","广告——中部轮播1"),

		ASJ_HOME_PAGE_INFO("ASJ_HOME_PAGE_INFO","首页"),
	/*	ASJ_HOME_PAGE_TOP_BANNER_LIST_ONLINE("ASJ_HOME_PAGE_TOP_BANNER_LIST_ONLINE","首页顶部轮播list(线上/测试)"),*/
		NEW_CONFIG_INFO("NEW_CONFIG_INFO","APP首页新增可配置项"),

		ASJ_HOME_PAGE_INFO_FIRST("ASJ_HOME_PAGE_INFO_FIRST","首页一级缓存"),
		ASJ_HOME_PAGE_INFO_SECOND("ASJ_HOME_PAGE_INFO_SECOND","首页二级缓存"),
		ASJ_HOME_PAGE_INFO_PROCESS_KEY("ASJ_HOME_PAGE_INFO_PROCESS_KEY","首页线程"),
		ASJ_HOME_PAGE_TOP_BANNER_LIST_ONLINE("ASJ_HOME_PAGE_TOP_BANNER_LIST_ONLINE","首页顶部轮播list(线上/测试)");



		private String code;
		private String name;

		private ASJ_HOME_PAGE(String code, String name) {
			this.code = code;
			this.name = name;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static enum ASJ_CATEGORY{
		ASJ_CATEGORY_DETAIL_GET_GOODS_BY_CATEGORYID_AND_VOLUME("ASJ_CATEGORY_DETAIL_GOODS_BY_CID_AND_VOLUME","爱尚街分类详情页商品按照类目ID和销量降序"),
		ASJ_CATEGORY_DETAIL_RESULT_GET_GOODS_BY_CATEGORYID_AND_PRICE("ASJ_CATEGORY_DETAIL_GOODS_BY_CID_AND_PRICE","爱尚街分类详情页商品按照类目和价格排序"),
		ASJ_CATEGORY_DETAIL_RESULT_ALLGOODS_THE_BRAND("ASJ_CATEGORY_DETAIL_ALLGOODS_THE_BRAND","爱尚街分类品牌结果页该品牌下所有的商品");


		private String code;
		private String name;

		private ASJ_CATEGORY(String code, String name) {
			this.code = code;
			this.name = name;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}

	}

	/**
	 * 保存活动对应商品信息的前缀
	 */
	public static final String CACHE_KEY_NEW_ACTIVITY_GOODS_PREFIX = "CACHE_KEY_NEW_ACTIVITY_GOODS_PREFIX:";

	/**
	 * 秒杀活动每日开始时间数组
	 */
	public static final String CACHE_KEY_ACTIVITY_START_HOUR_ARRAY = "CACHE_KEY_ACTIVITY_START_HOUR_ARRAY:";

}
