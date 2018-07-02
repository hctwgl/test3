package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.alibaba.fastjson.JSONObject;

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

	public H5HandleResponse(String id, FanbeiExceptionCode excCode) {
		this.id = id;
		result = new H5Response();
		result.setCode(excCode.getErrorCode());
		result.setMsg(excCode.getDesc());
	}
	public H5HandleResponse(String id, FanbeiExceptionCode excCode,String msg) {
		this.id = id;
		result = new H5Response();
		result.setCode(excCode.getErrorCode());
		result.setMsg(msg);
	}
	public H5HandleResponse() {

	}

	/**
	 * 设置相应数据
	 *
	 * @param data
	 */
	public void setResponseData(Object data) {
		if (result != null) {
			result.setData(data);
		} else {
			result = new H5Response();
			result.setData(data);
		}
	}

	/**
	 * 设置相应数据,可以调用多次来设置返回的数据
	 *
	 * @param dateKey
	 * @param dateValue
	 */
	public void addResponseData(String dateKey, Object dateValue) {
		JSONObject data = (JSONObject) result.getData();
		if (data == null) {
			data = new JSONObject();
		}
		data.put(dateKey, dateValue);
		if (result != null) {
			result.setData(data);
		} else {
			result = new H5Response();
			result.setData(data);
		}
	}

}
