package com.ald.fanbei.api.biz.bo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.SignUtil;

/** 
 * @类描述:
 * @author fanmanfu 创建时间：2017年7月31日 下午8:07:41 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

public class RiskDataBo extends HashMap<String,String>{
	
	private String sign;//MD5签名,对data的json串签名
	private String timestamp;
	private String data;//数据集合
	
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
		this.put("sign",sign);
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		this.put("timestamp",timestamp);
	}
	/*public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
		this.put("dataType",dataType);
	}*/
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		this.put("data",data);
	}

}
