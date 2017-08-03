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
	private static String PRIVATE_KEY = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBANXSVyvH4C55YKzvTUCN0fvrpKjIC5lBzDe6QlHCeMZaMmnhJpG/O+aao0q7vwnV08nk14woZEEVHbNHCHcfP+gEIQ52kQvWg0L7DUS4JU73pXRQ6MyLREGHKT6jgo/i1SUhBaaWOGI9w5N2aBxj1DErEzI7TA1h/M3Ban6J5GZrAgMBAAECgYAHPIkquCcEK6Nz9t1cc/BJYF5AQBT0aN+qeylHbxd7Tw4puy78+8XhNhaUrun2QUBbst0Ap1VNRpOsv5ivv2UAO1wHqRS8i2kczkZQj8vcCZsRh3jX4cZru6NoBb6QTTFRS6DRh06iFm0NgBPfzl9PSc3VwGpdj9ZhMO+oTYPBwQJBAPApB74XhZG7DZVpCVD2rGmE0pAlO85+Dxr2Vle+CAgGdtw4QBq89cA/0TvqHPC0xZaYWK0N3OOlRmhO/zRZSXECQQDj7JjxrUaKTdbS7gD88qLZBbk8c07ghO0qDCpp8J2U6D9baVBOrkcz+fTh7B8LzyCo5RY8vk61v/rYqcgk1F+bAkEAvYkELUfPCGZBoCsXSSiEhXpn248nFh5yuWq0VecJ25uObtqN7Qw4PxOeg9SOJoHkdqehRGJuc9LaMDQ4QQ4+YQJAJaIaOsVWgV2K2/cKWLmjY9wLEs0jN/Uax7eMhUOCcWTLmUdRSDyEazOZWHhJRATmKpzwyATQMDhLrdySvGoIgwJBALusECkz5zT4lIujwUNO30LlO8PKPCSKiiQJk4pN60pv2AFX4s2xVdZlXsFJh6btIJ9CGrMvEmogZTIGWq1xOFs=";
	
	private String sign;//MD5签名,对data的json串签名
	private String timestamp;
	private String dataType;//数据类型
	private String data;//数据集合
	/*public RiskDataBo(){}
	
	public RiskDataBo(Map data){
		this.sign=SignUtil.sign(createLinkString(data), PRIVATE_KEY);//MD5签名
		//获取时间戳
		long times = new Date().getTime();
		String date = DateUtil.formatDate(times, "yyyyMMddHHmmssSSS");
		this.timestamp=date;
		this.data=JsonUtil.toJSONString(data);
	}
	
	public RiskDataBo(Map data,String dataType){
		this.sign=SignUtil.sign(createLinkString(data), PRIVATE_KEY);//MD5签名
		//获取时间戳
		long times = new Date().getTime();
		String date = DateUtil.formatDate(times, "yyyyMMddHHmmssSSS");
		this.timestamp=date;
		this.dataType=dataType;//数据类型
		this.data=JsonUtil.toJSONString(data);//数据集合
	}
	*//**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 * 
	 * @param params
	 *            需要排序并参与字符拼接的参数组
	 * @return 拼接后字符串
	 *//*
	public static String createLinkString(Map<String, String> params) {

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			prestr = prestr + value;
			
			 * if (i == keys.size() - 1) {//拼接时，不包括最后一个&字符 prestr = prestr + key
			 * + "=" + value; } else { prestr = prestr + key + "=" + value +
			 * "&"; }
			 
		}

		return prestr;
	}*/
	
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
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
		this.put("dataType",dataType);
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
		this.put("data",data);
	}

	@Override
	public String toString() {
		return "RiskDataBo [sign=" + sign + ", timestamp=" + timestamp
				+ ", dataType=" + dataType + ", data=" + data + "]";
	}
	
	
}
