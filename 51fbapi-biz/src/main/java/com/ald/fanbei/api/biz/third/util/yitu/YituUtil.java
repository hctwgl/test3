package com.ald.fanbei.api.biz.third.util.yitu;

import java.security.PublicKey;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;

@Component("yituUtil")
public class YituUtil extends AbstractThird {

	private static String pemPath;
	private static String accessId;
	private static String accessKey;
	private static String userDefinedContent;
	private static String ip;

	
	public static 
	
	/**
	 * 
	 * @方法说明：加签方法
	 * @author huyang
	 * @param requestBody
	 *            加签内容
	 * @return
	 * @throws Exception
	 */
	private String signature(String requestBody) throws Exception {
		PublicKey publicKey = EncryptionHelper.RSAHelper.loadPublicKey(getPemPath());
		String signature = HttpRequestHelper.generateSignature(publicKey, getAccessId(), requestBody, getUserDefinedContent());
		logger.info(StringUtil.appendStrs("Yitu signature requestBody=", requestBody, "|,signature=", signature));
		return signature;
	}

	public static String getPemPath() {
		if (pemPath == null) {
			pemPath = ConfigProperties.get(Constants.CONFKEY_YITU_PEM_PATH);
		}
		return pemPath;
	}

	public static void setPemPath(String pemPath) {
		YituUtil.pemPath = pemPath;
	}

	public static String getAccessId() {
		if (accessId == null) {
			accessId = ConfigProperties.get(Constants.CONFKEY_YITU_ID);
		}
		return accessId;
	}

	public static void setAccessId(String accessId) {
		YituUtil.accessId = accessId;
	}

	public static String getAccessKey() {
		if (accessKey == null) {
			accessKey = ConfigProperties.get(Constants.CONFKEY_YITU_KEY);
		}
		return accessKey;
	}

	public static void setAccessKey(String accessKey) {
		YituUtil.accessKey = accessKey;
	}

	public static String getUserDefinedContent() {
		if (userDefinedContent == null) {
			userDefinedContent = ConfigProperties.get(Constants.CONFKEY_YITU_DEFINED_CONTENT);
		}
		return userDefinedContent;
	}

	public static void setUserDefinedContent(String userDefinedContent) {
		YituUtil.userDefinedContent = userDefinedContent;
	}

	public static String getIp() {
		if (ip == null) {
			ip = ConfigProperties.get(Constants.CONFKEY_YITU_URL);
		}
		return ip;
	}

	public static void setIp(String ip) {
		YituUtil.ip = ip;
	}

}
