package com.ald.fanbei.api.web.common;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：接口响应类
 * @author 陈金虎 2017年1月16日 下午11:53:49
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class ApiHandleResponse extends AbstractSerial implements BaseResponse {

	private static final long serialVersionUID = 6654764459668387122L;
	private String id;
	private AppResponse result;

	public ApiHandleResponse(String id, FanbeiExceptionCode excCode) {
		this.id = id;
		result = new AppResponse();
		result.setCode(excCode.getErrorCode());
		result.setMsg(excCode.getDesc());
	}

	public ApiHandleResponse() {

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
			result = new AppResponse();
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
			result = new AppResponse();
			result.setData(data);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AppResponse getResult() {
		return result;
	}

	public void setResult(AppResponse result) {
		this.result = result;
	}

}
