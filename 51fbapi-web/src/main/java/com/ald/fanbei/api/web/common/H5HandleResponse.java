package com.ald.fanbei.api.web.common;

/**
 * 
 *@类描述：H5HandleResponse
 *@author 江荣波 2018年1月10日 下午6:14:32
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class H5HandleResponse implements BaseResponse{

	private String id;
	private H5Response result;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public H5Response getResult() {
		return result;
	}
	public void setResult(H5Response result) {
		this.result = result;
	}

}
