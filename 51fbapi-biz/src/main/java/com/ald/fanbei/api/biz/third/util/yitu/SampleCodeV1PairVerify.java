package com.ald.fanbei.api.biz.third.util.yitu;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper.Md5EncodingException;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.RSAHelper.PublicKeyException;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SampleCodeV1PairVerify {

    
	private static String pemPath;
	private static String accessId;
	private static String accessKey;
	private static String userDefinedContent;
	private static String ip;

	/**
	 * 测试上传登记照并进行身份比对 将从测试数据文件中读取若干对比对数据(登记照和查询照)，依次调用服务，最终得到比对分数和正确答案进行比较
	 * 
	 * @throws IllegalStateException
	 *             , 未初始化URL或公钥文件
	 * @throws JsonProcessingException
	 * @throws SocketTimeoutException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws Md5EncodingException
	 * @throws PublicKeyException
	 */
	public static void PairVerify() throws Exception {
		System.out.println("Test PairVerify Begin.");
		
		/*
		 * Step 1 生成HTTP body 
		 */
		String databaseImageContent = FileHelper.getImageBase64Content("sample_images/id.jpg"); //  设置上传图片并编码为base64字符串
		int databaseImageType = 1; // 设置上传图片的类型，类型编号请参见接口文档

		String queryImageContent = FileHelper.getImageBase64Content("sample_images/id_test.jpg"); // 设置查询照图片并编码为base64字符串
		int queryImageType = 301; // 设置查询照图片类型
		
	    Map<String, Object> requestData = new HashMap<String, Object>();
	    requestData.put("database_image_content", databaseImageContent);
	    requestData.put("database_image_type", databaseImageType);
	    requestData.put("query_image_content", queryImageContent);
	    requestData.put("query_image_type", queryImageType);
	    
	    String requestBodyString = JSON.toJSONString(requestData);
	    
		
		/*
		 * Step 2 生成 signature
		 */
	    PublicKey publicKey = EncryptionHelper.RSAHelper.loadPublicKey(pemPath);
	    String signature = HttpRequestHelper.generateSignature(publicKey, accessKey, requestBodyString, userDefinedContent);
		System.out.println("生成signature : " + signature);
	    
		/*
		 * Step 3 发送 HTTP请求 
		 */
	    String url = ip + "/face/v1/algorithm/recognition/face_pair_verification";
	    
		String result = HttpRequestHelper.sendPost(url, accessId, signature, requestBodyString);

		/*
		 * Step 4 校验答案
		 */
		JSONObject responseJson =  JSON.parseObject(result);
		if (responseJson.getInteger("rtn") == 0) {
			if (responseJson.getInteger("pair_verify_result") == 0) {
				System.out.println("比对通过");
			} else {
				System.out.println("比对不通过");
			}
			System.out.println("相似度为" + responseJson.getDouble("pair_verify_similarity"));
		} else {
			System.err.println(responseJson.getString("message"));
		}
	}

	/**
	 * 载入配置文件
	 * 
	 * @throws JsonProcessingException
	 */
	public static void loadConfig() throws Exception {

		ip = ConfigProperties.get(Constants.CONFKEY_YITU_URL);
		pemPath = ConfigProperties.get(Constants.CONFKEY_YITU_PEM_PATH);
		accessId = ConfigProperties.get(Constants.CONFKEY_YITU_ID);
		accessKey = ConfigProperties.get(Constants.CONFKEY_YITU_KEY);
		userDefinedContent = ConfigProperties.get(Constants.CONFKEY_YITU_DEFINED_CONTENT);
	}

	public static void main(String[] args) throws Exception {
		loadConfig();
		PairVerify();
		System.out.println("Test PairVerify End.");
	}
}