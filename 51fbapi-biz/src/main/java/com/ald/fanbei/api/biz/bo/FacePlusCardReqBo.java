package com.ald.fanbei.api.biz.bo;

import org.apache.http.entity.mime.content.InputStreamBody;

/**
 * 
 * @类描述：Face++身份验证请求
 * @author xiaotianjian 2017年7月23日下午11:00:53
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FacePlusCardReqBo {

	private String api_key;//用于验证客户身份的API Key； 对于每一个客户此字段不会变更，相当于用户名。
	private String api_secret;//用于验证客户身份的API Secret； 对于每一个客户可以申请变更此字段，相当于密码。
	private InputStreamBody image;//通过非MegLive的途径获得的真人人脸照片。
	
	/**
	 * @return the api_key
	 */
	public String getApi_key() {
		return api_key;
	}
	/**
	 * @param api_key the api_key to set
	 */
	public void setApi_key(String api_key) {
		this.api_key = api_key;
	}
	/**
	 * @return the api_secret
	 */
	public String getApi_secret() {
		return api_secret;
	}
	/**
	 * @param api_secret the api_secret to set
	 */
	public void setApi_secret(String api_secret) {
		this.api_secret = api_secret;
	}
	/**
	 * @return the image
	 */
	public InputStreamBody getImage() {
		return image;
	}
	/**
	 * @param image the image to set
	 */
	public void setImage(InputStreamBody image) {
		this.image = image;
	}

}
