package com.ald.fanbei.api.biz.third.util;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

/**
 *@类描述：有盾工具类
 *@author 陈金虎 2017年1月20日 下午9:26:28
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 
 */
@Component("youdunUtil")
public class YoudunUtil extends AbstractThird{
	private static String pubkey = null;
	private static String securityKey = null;
	
	/**
	 * 四要素验证
	 * 
	 * @param realName 真实姓名
	 * @param idNumber 身份证号
	 * @param cardNumber 卡号
	 * @param mobile 银行预留手机号
	 */
	public static boolean fourItemCheck(String realName, String idNumber, String cardNumber, String mobile) {

		String outOrderId = idNumber.substring(10) + System.currentTimeMillis();
		String url = ConfigProperties.get(Constants.CONFKEY_YOUDUN_HOST) + "/dsp-front/4.1/dsp-front/default/pubkey/%s/product_code/%s/out_order_id/%s/signature/%s";
		Map<String, String> params = new HashMap<String, String>();
		params.put("id_name", realName);
		params.put("bank_card_no", cardNumber);
		params.put("id_no", idNumber);
		params.put("mobile", mobile);
		params.put("req_type", "01");
		StringBuffer bodySb = new StringBuffer("{");
		for (Map.Entry<String, String> entry : params.entrySet()) {
			bodySb.append("'").append(entry.getKey()).append("':'").append(entry.getValue()).append("',");
		}
		String bodyStr = bodySb.substring(0, bodySb.length() - 1) + "}";

		String signature = "";
		try {
			signature = md5(bodyStr + "|" + getSecuritykey());

			url = String.format(url, getPubkey(), "O1001S0401", outOrderId + "", signature);
			HttpResponse r = makePostRequest(url, bodyStr);

			String reqResult = EntityUtils.toString(r.getEntity());
			// {"header":{"resp_time":"2017-02-18 17:57:30","ret_msg":"操作成功","version":"4.1","ret_code":"000000","req_time":"2017-02-18 17:57:29"},"body":{"card_name":"借记IC卡","req_type":"01","message":"认证一致","card_type":"借记卡","org_name":"浙江省农村信用社联合社","org_code":"14293300","status":"1","ud_order_no":"153906928068722688"}}
			logThird(reqResult, "fourItemCheck", outOrderId, realName, idNumber, cardNumber, mobile);
			if(StringUtil.isBlank(reqResult)){
				throw new FanbeiException(FanbeiExceptionCode.AUTH_CARD_ERROR);
			}
			//认证表中添加记录
			JSONObject resultJson = JSONObject.parseObject(reqResult);
			JSONObject header = resultJson.getJSONObject("header");
			JSONObject body = resultJson.getJSONObject("body");
			if( header != null && StringUtil.equals(header.getString("ret_code"), "000000") && body != null && StringUtil.equals(body.getString("status"), "1")){
				return true;
			}
			return false;
		} catch (Exception e) {
			throw new FanbeiException(FanbeiExceptionCode.AUTH_CARD_ERROR,e);
		}
	}
	

	private static final CloseableHttpClient client = HttpClientBuilder.create().build();

	private static HttpResponse makePostRequest(String uri, String jsonData) throws ClientProtocolException, IOException {
		HttpPost httpPost = new HttpPost(URIUtil.encodeQuery(uri, "utf-8"));
		httpPost.setEntity(new StringEntity(jsonData, "UTF-8"));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json; charset=utf-8");
		return client.execute(httpPost);
	}

	private static String md5(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data.toString().getBytes());
		return bytesToHex(md.digest());
	}

	private static String bytesToHex(byte[] ch) {
		StringBuffer ret = new StringBuffer("");
		for (int i = 0; i < ch.length; i++)
			ret.append(byteToHex(ch[i]));
		return ret.toString();
	}

	/**
	 * 字节转换为16进制字符串
	 */
	private static String byteToHex(byte ch) {
		String str[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };
		return str[ch >> 4 & 0xF] + str[ch & 0xF];
	}
	
//	/**
//	 * 处理有盾异步通知
//	 * @param youdunNotifyBo
//	 */
//	public void dealYoudunNotify(AfYoudunFaceDo afYoudunFaceDo){
//		String frontCard = afYoudunFaceDo.getFrontCard();
//		String backCard = afYoudunFaceDo.getBackCard();
//		String photoGet = afYoudunFaceDo.getPhotoGet();
//		String photoGrid = afYoudunFaceDo.getPhotoGet();
//		String photoLiving = afYoudunFaceDo.getPhotoLiving();
//		String userId = afYoudunFaceDo.getUserId();
//		
//		try {
//			frontCard = this.uploadYoudunFile(frontCard, userId, 1);//TODO 
//			afYoudunFaceDo.setFrontCard(frontCard);
//			backCard = this.uploadYoudunFile(backCard, userId, 2);
//			afYoudunFaceDo.setBackCard(backCard);
//			photoGet = this.uploadYoudunFile(photoGet, userId, 3);
//			afYoudunFaceDo.setPhotoGet(photoGet);
//			photoGrid = this.uploadYoudunFile(photoGrid, userId, 4);
//			afYoudunFaceDo.setPhotoGrid(photoGrid);
//			photoLiving = this.uploadYoudunFile(photoLiving, userId, 5);
//			afYoudunFaceDo.setPhotoLiving(photoLiving);
//		} catch (IOException e) {
//			throw new FanbeiException("deal youdun notify error",FanbeiExceptionCode.DEALWITH_YOUDUN_NOTIFY_ERROR,e);
//		}
//		
//		afYoudunFaceService.addYoudunFace(afYoudunFaceDo);
//		
//	}
	
//	private String uploadYoudunFile(String base64Str,String userId,int type) throws IOException{//TODO 
//		FileOutputStream out = null;
//		try{
//			byte[] imageBytes = Base64.decode(base64Str);
//			String dir = "d:/home/file" + userId + type + ".jpg";//TODO
//			out = new FileOutputStream(new File(dir));
//			out.write(imageBytes);
//			return dir;
//		}catch(Exception e){
//			logger.error("uploadYoudunFile error",e);
//			return "";
//		}finally{
//			if(out != null){
//				out.close();
//			}
//		}
//	}
	
//	public static String getHost(){
//		if(host == null){
//			host = ConfigProperties.get(Constants.CONFKEY_YOUDUN_HOST);
//		}
//		return host;
//	}
	
	public static String getPubkey(){
		if(pubkey == null){
			pubkey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return pubkey;
	}
	
	public static String getSecuritykey(){
		if(securityKey == null){
			securityKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
		}
		return securityKey;
	}
}
