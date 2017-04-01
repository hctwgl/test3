package com.ald.fanbei.api.biz.third.util;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.common.util.TripleDESUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：开心果手机充值工具类
 * @author 何鑫 2017年2月16日 11:20:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("kaixinUtil")
public class KaixinUtil extends AbstractThird{

	public static String PARTNER_ID = "10330184"; // 商户编号
	public static String ORGCODE = "KX0000"; // 商户运营机构
	public static String AGENT_ID = "sc0055"; // 代理商编号
	public static String PASSWORD = null; // 登陆密码
	public static String PAY_PASSWORD = null; // 支付密码
	public static String SIGN_TYPE = "md5"; // 签名方式
	public static String KEY = null; // 密钥

	public static String URL_CHARGE = null;

	public static TripleDESUtil TDES = TripleDESUtil.getInstance();

	private static String getUrlCharge(){
		if(URL_CHARGE != null){
			return URL_CHARGE;
		}
		URL_CHARGE = ConfigProperties.get(Constants.CONFKEY_KXG_URL_CHARGE);
		return URL_CHARGE;
	}
	
	private static String getPassword(){
		if(PASSWORD != null){
			return PASSWORD;
		}
		PASSWORD = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_KXG_PASSWORD), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return PASSWORD;
	}
	
	private static String getPayPwd(){
		if(PAY_PASSWORD != null){
			return PAY_PASSWORD;
		}
		PAY_PASSWORD = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_KXG_PAY_PASSWORD), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return PAY_PASSWORD;
	}
	
	private static String getKey(){
		if(KEY != null){
			return KEY;
		}
		KEY = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_KXG_KEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		return KEY;
	}

	/**
	 * header 参数配置
	 * @return
	 * @throws Exception
	 */
	private static JSONObject getHeaderObject() throws Exception{
		JSONObject header = new JSONObject();
		header.put("partner_id", PARTNER_ID);
		header.put("orgcode", ORGCODE);
		header.put("agent_id", AGENT_ID);
		header.put("password", TDES.encrypt(getPassword(), getKey()));
		header.put("timestamp", DateUtil.formatDate(new Date(), DateUtil.FULL_PATTERN));
		header.put("sign_type", SIGN_TYPE);
		return header;
	}
	
	/**
	 * 充值下单
	 * 
	 * @param order_no
	 *            订单号
	 * @param account_no
	 *            客户号码
	 * @param face_price
	 *            充值面额
	 * @throws Exception
	 */
	public String charge(String order_no, String account_no, String face_price) {
		try {
			JSONObject header = getHeaderObject();
			
			JSONObject body = new JSONObject();
			body.put("pay_password", TDES.encrypt(getPayPwd(), getKey()));
			body.put("order_no", order_no);
			if (StringUtil.equals(ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE),
					Constants.INVELOMENT_TYPE_ONLINE)){
				body.put("account_no", account_no);
				body.put("face_price", face_price);
			}else{
				body.put("account_no", "18934199597");//测试，广东电信50面值
				body.put("face_price", "50");
			}

			// 设置签名
			header.put("sign", sign(header, body));

			JSONObject obj = new JSONObject();
			obj.put("header", header);
			obj.put("body", body);

			String message = obj.toString();

			String retMsg = post(getUrlCharge(), message);
			
			return retMsg;
		} catch (Exception e) {
			logger.error("charge:", e);
			return null;
		}
	}

	public static String post(String url, String message) throws ClientProtocolException, IOException {
		logger.info("发送消息：{}", message);

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httppost = new HttpPost(url);

		StringEntity entity = new StringEntity(message, "UTF-8");
		entity.setContentType("application/json;charset=UTF-8");
		httppost.setEntity(entity);
		httppost.addHeader("Accept", "application/json");
		httppost.addHeader("ContentType", "application/json;charset=UTF-8");

		CloseableHttpResponse response = httpclient.execute(httppost);

		try {
			int code = response.getStatusLine().getStatusCode();
			logger.error("code-->{}", code);
			if (HttpURLConnection.HTTP_OK == code) {
				String retMsg = EntityUtils.toString(response.getEntity(), "UTF-8");
				logger.info("返回消息：{}", retMsg);

				return retMsg;
			}
		} catch (Exception e) {
			// TODO 请针对不同异常做相应的处理
			logger.error(e.getMessage(), e);
		} finally {
			response.close();
		}
		return null;
	}

	public static String sign(JSONObject... jsonObjects) {
		StringBuffer sb = new StringBuffer();

		Map<String, String> attrs = new TreeMap<String, String>();
		for (JSONObject obj : jsonObjects) {
			for (Map.Entry<String, Object> entry : obj.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue().toString();
				if (value != null && !"".equals(value.trim())) {
					attrs.put(key, value);
				}
			}
		}

		Set<Map.Entry<String, String>> es = attrs.entrySet();
		Iterator<Map.Entry<String, String>> it = es.iterator();

		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append("=").append(value).append("&");
		}
		sb.deleteCharAt(sb.length() - 1).append(getKey());

		logger.info("签名前串：{}", sb.toString());
		String sign = DigestUtil.MD5(sb.toString());
		logger.info("签名：{}", sign);
		return sign;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(TDES.encrypt("88888888", "FD0762380D980D52"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
