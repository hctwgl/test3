package com.ald.fanbei.api.common;


/**
 * 
 *@类AppContext.java 的实现描述：app服务上下文
 *@author 陈金虎 2017年1月16日 下午11:15:38
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FanbeiContext {
    private Long userId;
    private String userName;
    private String nick;
    private Integer appVersion;
    private String mobile;//绑定手机号
    
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public Integer getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(Integer appVersion) {
		this.appVersion = appVersion;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("userId=");
		sb.append(this.userId).append(",userName=").append(userName).append(",nick=").append(nick)
		.append(",appVersion=").append(appVersion);
		return sb.toString();
	}   
}
