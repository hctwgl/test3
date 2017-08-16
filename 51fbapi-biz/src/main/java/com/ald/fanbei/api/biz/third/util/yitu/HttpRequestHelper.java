package com.ald.fanbei.api.biz.third.util.yitu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.MD5Helper.Md5EncodingException;
import com.ald.fanbei.api.biz.third.util.yitu.EncryptionHelper.RSAHelper.PublicKeyException;
import com.alibaba.fastjson.JSON;

/**
 * HttpRequestHelper.java
 * 
 * 包含发送HTTP请求相关的一些函数
 */

public class HttpRequestHelper {

	public static int connectTimeOut = 10000;
	public static int readTimeOut = 10000;
	private static final char[] toDigit = ("0123456789ABCDEF").toCharArray();
	public static PublicKey publicKey;

	/**
	 * 合并两个byte数组
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static byte[] mergeArray(byte[] a, byte[] b) {
		byte[] c = new byte[a.length + b.length];
		System.arraycopy(a, 0, c, 0, a.length);
		System.arraycopy(b, 0, c, a.length, b.length);
		return c;
	}

	/**
	 * base16编码
	 */
	public static String encode(byte[] b) {
		char[] chars = new char[2 * b.length];
		int j = 0;

		for (int i = 0; i < b.length; ++i) {
			byte bits = b[i];

			chars[j++] = toDigit[((bits >>> 4) & 0xF)];
			chars[j++] = toDigit[(bits & 0xF)];
		}

		return new String(chars);
	}

	/**
	 * 生成Signature
	 */
	public static String generateSignature(String accessKey, String MD5String, String userDefinedContent)
			throws Exception {

		String result = null;

		// 生成unix时间戳
		int unixTime = (int) (System.currentTimeMillis() / 1000L);
		byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();

		// 生成随机数
		SecureRandom sr = new SecureRandom();
		byte[] rndBytes = new byte[8];
		sr.nextBytes(rndBytes);

		// 拼接Signature
		byte[] temp = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")), EncryptionHelper.MD5Helper
				.md5(MD5String).getBytes(Charset.forName("UTF-8")));
		temp = mergeArray(temp, unixTimeArray);
		temp = mergeArray(temp, rndBytes);
		temp = mergeArray(temp, userDefinedContent.getBytes(Charset.forName("UTF-8")));

		// RSA加密
		Security.addProvider(new BouncyCastleProvider());
		try {
			result = encode(EncryptionHelper.RSAHelper.encrypt(temp, publicKey));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 16进制编码
	 * 
	 * @param bytes
	 *            , 输入字符数组
	 * @return 经过16进制编码后的数组
	 */
	public static String hexEncode(byte[] bytes) {
		char[] chars = new char[2 * bytes.length];
		int j = 0;

		for (int i = 0; i < bytes.length; ++i) {
			byte bits = bytes[i];

			chars[j++] = toDigit[((bits >>> 4) & 0xF)];
			chars[j++] = toDigit[(bits & 0xF)];
		}

		return new String(chars);
	}
	
	/**
	 * 生成Signature
	 * 
	 * @param accessKey
	 *            , access Key
	 * @param bodyString
	 *            , HTTP 请求内容
	 * @param userDefinedContent
	 *            , 客户自定义域，要求小于41字节
	 * @return 生成的加密后的Signature
	 * @throws Md5EncodingException
	 * @throws PublicKeyException
	 */
	public static String generateSignature(PublicKey publicKey, String accessKey, String bodyString,
			String userDefinedContent) throws Exception {

		String result = null;

		// 生成unix时间戳
		int unixTime = (int) (System.currentTimeMillis() / 1000L);
		byte[] unixTimeArray = ByteBuffer.allocate(4).putInt(unixTime).array();

		// 生成随机数
		SecureRandom sr = new SecureRandom();
		byte[] rndBytes = new byte[8];
		sr.nextBytes(rndBytes);

		// 拼接Signature
		byte[] signatureStr = mergeArray(accessKey.getBytes(Charset.forName("UTF-8")),
				EncryptionHelper.MD5Helper.md5(bodyString).getBytes(Charset.forName("UTF-8")));
		signatureStr = mergeArray(signatureStr, unixTimeArray);
		signatureStr = mergeArray(signatureStr, rndBytes);
		signatureStr = mergeArray(signatureStr, userDefinedContent.getBytes(Charset.forName("UTF-8")));

		// RSA加密
		result = hexEncode(EncryptionHelper.RSAHelper.encrypt(signatureStr, publicKey));

		return result;
	}

	/**
	 * 发送HTTP POST请求
	 * 
	 * @param url
	 *            , 请求地址
	 * @param accessId
	 *            , access Id
	 * @param accessKey
	 *            , access Key
	 * @param bodyString
	 *            , HTTP 请求内容
	 * @param userDefinedContent
	 *            , 客户自定义域，要求小于41字节
	 * @return 请求响应内容
	 * @throws SocketTimeoutException
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws Md5EncodingException
	 * @throws PublicKeyException
	 */
	public static String sendPost(PublicKey publicKey, String url, String accessId, String accessKey,
			String bodyString, String userDefinedContent) throws Exception {

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";

		URL realUrl = new URL(url);
		// 打开和URL之间的连接
		URLConnection conn = realUrl.openConnection();
		HttpURLConnection httpConn = (HttpURLConnection) conn;
		// 设置通用的请求属性,设置accessId和signature
		httpConn.setRequestProperty("accept", "*/*");
		httpConn.setRequestProperty("connection", "Keep-Alive");
		httpConn.setRequestProperty("Content-Type", "application/json");
		httpConn.setRequestProperty("x-access-id", accessId);
		httpConn.setRequestProperty("x-signature",
				generateSignature(publicKey, accessKey, bodyString, userDefinedContent));
		httpConn.setConnectTimeout(connectTimeOut);
		httpConn.setReadTimeout(readTimeOut);
		// 发送POST请求必须设置如下两行
		httpConn.setDoOutput(true);
		httpConn.setDoInput(true);
		httpConn.setUseCaches(false);
		httpConn.setRequestMethod("POST");

		httpConn.connect();
		// 获取URLConnection对象对应的输出流
		out = new PrintWriter(httpConn.getOutputStream());
		// 发送请求参数
		out.print(bodyString);
		// flush输出流的缓冲
		out.flush();

		// System.out.println(bodyString);

		// 定义BufferedReader输入流来读取URL的响应,当返回码不为200的时候取出错误信息
		if (httpConn.getResponseCode() != 200) {
			in = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
		} else {
			in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
		}

		String line;
		while ((line = in.readLine()) != null) {
			result += line;
		}

		if (out != null) {
			out.close();
		}
		if (in != null) {
			in.close();
		}

		return result;
	}

	public static String sendPost(String url, String accessId, String signature, String bodyString)
			throws Exception {

		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";

		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			URLConnection conn = realUrl.openConnection();
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			// 设置通用的请求属性,设置accessId和signature
			httpConn.setRequestProperty("accept", "*/*");
			httpConn.setRequestProperty("connection", "Keep-Alive");
			httpConn.setRequestProperty("Content-Type", "application/json");
			httpConn.setRequestProperty("x-access-id", accessId);
			httpConn.setRequestProperty("x-signature", signature);
			httpConn.setConnectTimeout(connectTimeOut);
			httpConn.setReadTimeout(readTimeOut);
			// 发送POST请求必须设置如下两行
			httpConn.setDoOutput(true);
			httpConn.setDoInput(true);
			httpConn.setUseCaches(false);
			httpConn.setRequestMethod("POST");

			httpConn.connect();
			// 获取URLConnection对象对应的输出流
			out = new PrintWriter(httpConn.getOutputStream());
			// 发送请求参数
			out.print(bodyString);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应,当返回码不为200的时候取出错误信息
			if (httpConn.getResponseCode() != 200) {
				in = new BufferedReader(new InputStreamReader(httpConn.getErrorStream(), "UTF-8"));
			} else {
				in = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), "UTF-8"));
			}

			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (SocketTimeoutException e) {
			Map<String, Object> errorData = new HashMap<String, Object>();
			errorData.put("rtn", -1);
			errorData.put("message", "Time out");
			result = JSON.toJSONString(errorData);
			// e.printStackTrace();
		} catch (UnknownHostException e) {
			Map<String, Object> errorData = new HashMap<String, Object>();
			errorData.put("rtn", -1);
			errorData.put("message", "Wrong url");
			result = JSON.toJSONString(errorData);
		} catch (Exception e) {
			Map<String, Object> errorData = new HashMap<String, Object>();
			errorData.put("rtn", -1);
			errorData.put("message", "Unknow error");
			result = JSON.toJSONString(errorData);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
