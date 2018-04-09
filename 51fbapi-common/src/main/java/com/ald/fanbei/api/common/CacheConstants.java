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

		GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST("GET_ACTIVITY_INFO_V2_ACTIVITY_INFO_LIST","V2首页活动商品"),
		GET_ACTIVITY_INFO_V2_PROCESS_KEY("GET_ACTIVITY_INFO_V2_PROCESS_KEY","V2会场活动线程标识位"),
		GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD("GET_HOME_INFO_V2_GOODS_INFO_FOR_OLD","V2首页商品（旧）"),
		GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW("GET_HOME_INFO_V2_GOODS_INFO_FOR_NEW","V2首页商品（新）");

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

}
