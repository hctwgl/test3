/**
 * 
 */
package com.ald.fanbei.api.common;

/**
 * 
 * @类描述：H5上下文
 * @author xiaotianjian 2017年8月2日下午5:44:17
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FanbeiH5Context extends AbstractSerial{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7059746589359185270L;
	private String userName;
    private boolean isLogin;
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	/**
	 * @return the isLogin
	 */
	public boolean isLogin() {
		return isLogin;
	}
	/**
	 * @param isLogin the isLogin to set
	 */
	public void setLogin(boolean isLogin) {
		this.isLogin = isLogin;
	}
    
    
	
}
