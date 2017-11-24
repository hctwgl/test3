package com.ald.fanbei.afu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import jodd.util.URLDecoder;

import org.apache.velocity.tools.config.ConfigurationException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.ald.fanbei.api.common.util.RC4_128_V2;
import com.ald.fanbei.api.web.vo.afu.ParamsFather;
import com.alibaba.fastjson.JSONObject;


/**
 * 
 * @author 
 *
 */
public class EchoQueryOrgApiClient {

	/**
	 * @param args
	 * @throws ConfigurationException
	 */
	public static void main(final String[] args) throws ConfigurationException {

		try {
			String data = null;
			final String url = "http://localhost:8005/third/getPersonInfo";
			final String rc4Key = "e4588f6b0a65fab9";
			System.out.println("查询程序启动~~~~~~~~");
			final String name = "俞毓民";
			final String idNo = "330726199311151519";
			
			final long startTime=System.currentTimeMillis();
				data = echoQueryOrg(url, rc4Key, name, idNo);
			System.out.println( data );
			final JSONObject jsonObject = JSONObject.parseObject(data);
			if (jsonObject.containsKey("errorCode") && "0000".equals(jsonObject.getString("errorCode"))) {
				// 解密解码返回结果
				final String dataField = URLDecoder.decode(jsonObject.getString("params"), "utf-8");
				final String decryptResult = RC4_128_V2.decode(dataField, rc4Key);
				System.out.println(decryptResult);
				jsonObject.put("params", JSONObject.parse(decryptResult));
				
			}
			final String decrptedString = jsonObject.toString();
			System.out.println("decrpted data: " + decrptedString);
			final long endTime=System.currentTimeMillis();
			System.out.println("Time duration: " + formatDuring(endTime - startTime));
			System.out.println("]查询程序结束~~~~~~~~");
			
		} catch (final Exception e) {
			e.printStackTrace(); 
		}
	}

	public static String formatDuring(final long mss) {
		final long days = mss / (1000 * 60 * 60 * 24);
		final long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		final long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		final long seconds = (mss % (1000 * 60)) / 1000;
		return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds ";
	}
	private static RestTemplate sender = new RestTemplate();
	public static String echoQueryOrg(final String url,final String rc4Key,final String name, final String idNo) throws UnsupportedEncodingException{
		final JSONObject data = new JSONObject();
		String strParams = "";
		data.put("name", name);
		data.put("idNo", idNo);
		final InnerEcParams inner = new InnerEcParams("201",data.toJSONString());
		strParams = URLEncoder.encode(RC4_128_V2.encode(inner.getJson(), rc4Key),"UTF-8");

		final MultiValueMap<String, String> paraMap = new LinkedMultiValueMap<String, String>();
		final JSONObject jsonObject = new JSONObject();
		jsonObject.put("params", strParams);
		jsonObject.put("errorCode", "0000");
		jsonObject.put("message", "queryFromEcho");
		paraMap.add("params",  jsonObject.toJSONString());
		//ParamsFather paramsFather = (ParamsFather)JSONObject.parse(jsonObject.toJSONString());
		
		SSLUtil.trustEveryone();
		return sender.postForObject(url, paraMap
				, String.class);
	}
}

