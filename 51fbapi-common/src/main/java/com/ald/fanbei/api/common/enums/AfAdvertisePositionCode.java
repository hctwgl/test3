
/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author chenqiwei 2018年05.18上午10:46:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfAdvertisePositionCode {
	   HOME_TOP_BANNER("HOME_TOP_BANNER", "首页顶部轮播"),
	   HOME_NAVIGATION_UP_ONE("HOME_NAVIGATION_UP_ONE", "中部轮播1"),
	   HOME_NAVIGATION_DOWN_ONE("HOME_NAVIGATION_DOWN_ONE","中部轮播2");
		

	 	private String code;
		private String name;
	
		AfAdvertisePositionCode(String code,String name) {
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

