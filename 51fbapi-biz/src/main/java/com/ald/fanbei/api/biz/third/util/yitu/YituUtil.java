package com.ald.fanbei.api.biz.third.util.yitu;

import java.security.PublicKey;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.YituFaceCardReqBo;
import com.ald.fanbei.api.biz.bo.YituFaceCardRespBo;
import com.ald.fanbei.api.biz.bo.YituFaceLivingReqBo;
import com.ald.fanbei.api.biz.bo.YituFaceLivingRespBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component("yituUtil")
public class YituUtil extends AbstractThird {

	private static String pemPath;
	private static String accessId;
	private static String accessKey;
	private static String userDefinedContent;
	private static String ip;

	public YituFaceCardRespBo checkCard(String filePath1, String filePath2) throws Exception {
		String pic = FileHelper.getImageBase64Content(filePath1);
		YituFaceCardReqBo bo = new YituFaceCardReqBo();
		bo.genUserInfo(pic);
		bo.genOptions(YituFaceCardReqBo.OCR_MODE_FRONT);
		String requestBody = JSON.toJSONString(bo);
		String signature = signature(requestBody);
		String url = getIp() + "/face/basic/ocr";
		String result = HttpRequestHelper.sendPost(url, getAccessId(), signature, requestBody);
		YituFaceCardRespBo respBo1 = JSONObject.parseObject(result, YituFaceCardRespBo.class);

		YituFaceCardReqBo logBo = new YituFaceCardReqBo(); 
		BeanUtils.copyProperties(bo, logBo,new String[]{"user_info"});
		logger.info(StringUtil.appendStrs("yitu checkCard front params=|", JSON.toJSONString(logBo), "|,reqResult=", result));
		if (respBo1.getRtn() == 0 && respBo1.getIdcard_ocr_result().getIdcard_type() != -1) {
			pic = FileHelper.getImageBase64Content(filePath2);
			bo = new YituFaceCardReqBo();
			bo.genUserInfo(pic);
			bo.genOptions(YituFaceCardReqBo.OCR_MODE_BACK);
			requestBody = JSON.toJSONString(bo);
			signature = signature(requestBody);
			result = HttpRequestHelper.sendPost(url, getAccessId(), signature, requestBody);
			YituFaceCardRespBo respBo2 = JSONObject.parseObject(result, YituFaceCardRespBo.class);
			BeanUtils.copyProperties(bo, logBo,new String[]{"user_info"});
			logger.info(StringUtil.appendStrs("yitu checkCard back params=|", JSON.toJSONString(logBo), "|,reqResult=", result));
			if (respBo2.getRtn() == 0 && respBo2.getIdcard_ocr_result().getIdcard_type() != -1) {
				respBo1.getIdcard_ocr_result().setAgency(respBo2.getIdcard_ocr_result().getAgency());
				respBo1.getIdcard_ocr_result().setValid_date_begin(respBo2.getIdcard_ocr_result().getValid_date_begin());
				respBo1.getIdcard_ocr_result().setValid_date_end(respBo2.getIdcard_ocr_result().getValid_date_end());
			} else {
				throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
			}
		} else {
			throw new FanbeiException(FanbeiExceptionCode.USER_CARD_AUTH_ERROR);
		}
		return respBo1;
	}

	/**
	 * 
	 * @方法说明：活体检验
	 * @author huyang
	 * @param packages
	 *            依图识别图像
	 * @return
	 * @throws Exception
	 */
	public YituFaceLivingRespBo checkLiving(String packages, String idcardPath) throws Exception {
		YituFaceLivingReqBo bo = new YituFaceLivingReqBo();
		bo.setQuery_image_package(packages);
		String pic = FileHelper.getImageBase64Content(idcardPath);
		bo.setDatabase_image_content(pic);
		String requestBody = JSON.toJSONString(bo);
		String signature = signature(requestBody);
		String url = getIp() + "/face/v1/algorithm/recognition/face_pair_verification";
		String result = HttpRequestHelper.sendPost(url, getAccessId(), signature, requestBody);
		YituFaceLivingRespBo respBo = JSONObject.parseObject(result, YituFaceLivingRespBo.class);
		YituFaceLivingReqBo logBo = new YituFaceLivingReqBo();
		BeanUtils.copyProperties(bo, logBo,new String[]{"database_image_content"});
		logger.info(StringUtil.appendStrs("yitu checkLiving params=|", JSON.toJSONString(logBo), "|,reqResult=", result));
		if (respBo.getRtn() == 0) {
			return respBo;
		} else {
			throw new FanbeiException(FanbeiExceptionCode.USER_FACE_AUTH_ERROR);
		}
	}

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
		String signature = HttpRequestHelper.generateSignature(publicKey, getAccessKey(), requestBody, getUserDefinedContent());
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
