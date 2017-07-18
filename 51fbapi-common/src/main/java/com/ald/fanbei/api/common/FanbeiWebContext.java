/**
 * 
 */
package com.ald.fanbei.api.common;

/**
 *@类现描述：返呗web上下文
 *@author chenjinhu 2017年6月7日 上午12:14:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FanbeiWebContext extends AbstractSerial{

    private static final long serialVersionUID = -8735617973645237819L;
	private String userName;
    private String appInfo;
    private Integer appVersion;
    private boolean isLogin;
    
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAppInfo() {
		return appInfo;
	}
	public void setAppInfo(String appInfo) {
		this.appInfo = appInfo;
	}
	public Integer getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}
	public boolean isLogin() {
		return isLogin;
	}
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
	
}
