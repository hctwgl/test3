package com.ald.fanbei.api.biz.third.util.yitu;

import java.io.InputStream;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.FacePlusCardRespBo;
import com.ald.fanbei.api.biz.bo.FacePlusFaceLivingRespBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.HttpsUtil;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：face++工具类
 * @author xiaotianjian 2017年7月23日下午10:49:12
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("facePlusUtil")
public class FacePlusUtil extends AbstractThird {

	private static final String FRONT_SIDE = "front";
	private static final String BACK_SIDE = "back";
	public FacePlusCardRespBo checkCard(String filePath1, String filePath2) throws Exception {
		FacePlusCardRespBo resBo = new FacePlusCardRespBo();
		checkCardImage(filePath1, resBo);
		checkCardImage(filePath2, resBo);
		return resBo;
	}
	
	private void checkCardImage(String imageUrl, FacePlusCardRespBo resBo) throws Exception {
		//TODO 改善忽略证书
		CloseableHttpClient httpclient = (CloseableHttpClient) HttpsUtil.getNoCertificateHttpClient(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_ID_CARD_URL));
		HttpPost httppost = new HttpPost(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_ID_CARD_URL));
		InputStream is = FileHelper.getImageStream(imageUrl); 
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("api_key", AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) )
				.addTextBody("api_secret", AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) )
				.addPart("image", new InputStreamBody(is, "aa.jpg"))
				.build();
		httppost.setHeader("contentType", "UTF-8");  
		httppost.setEntity(entity);
		HttpResponse httpResponse = httpclient.execute(httppost);
		httpResponse.getStatusLine().getStatusCode();
//		  if(httpResponse.getStatusLine().getStatusCode() == 200) {
			  HttpEntity httpEntity = httpResponse.getEntity();  
			  String tempResult = EntityUtils.toString(httpEntity,"UTF-8");//取出应答字符串  
			  if (StringUtils.isNotBlank(tempResult)) {
				  FacePlusCardRespBo tempResp = JSONObject.parseObject(tempResult, FacePlusCardRespBo.class);
				  if (FRONT_SIDE.equals(tempResp.getSide())) {
					  resBo.setId_card_number(tempResp.getId_card_number());
					  resBo.setName(tempResp.getName());
					  resBo.setRace(tempResp.getRace());
					  resBo.setAddress(tempResp.getAddress());
					  resBo.setGender(tempResp.getGender());
					  resBo.setBirthday(tempResp.getBirthday());
				  } else if (BACK_SIDE.equals(tempResp.getSide())) {
					  String[] validDates = tempResp.getValid_date().split("-");
					  resBo.setValid_date_begin(validDates[0]);
					  resBo.setValid_date_end(validDates[1]);
					  resBo.setIssued_by(tempResp.getIssued_by());
				  }
			  }
//		  }
	}

	/**
	 * face++活体验证
	 * @param delta 在配合MegLive SDK使用时，用于校验上传数据的校验字符串，此字符串会由MegLive SDK直接返回。
	 * @param imageBest 此参数请传MegLive SDK返回的质量最佳的人脸图片。
	 * @param idCardNumber 身份证号
	 * @param idCardName 身份证名称
	 * @return
	 * @throws Exception
	 */
	public FacePlusFaceLivingRespBo checkLiving(String delta, String imageBest, String idCardNumber, String idCardName) throws Exception {
		CloseableHttpClient httpclient = (CloseableHttpClient) HttpsUtil.getNoCertificateHttpClient(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_ID_CARD_URL));
		HttpPost httppost = new HttpPost(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_ID_CARD_URL));
		URL url = new URL(imageBest);
		InputStream is = url.openStream();
		HttpEntity entity = MultipartEntityBuilder.create()
				.addTextBody("api_key", AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_APPKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) )
				.addTextBody("api_secret", AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_FACE_PLUS_SECRET), ConfigProperties.get(Constants.CONFKEY_AES_KEY)) )
				.addTextBody("comparison_type", "1")
				.addTextBody("face_image_type", "meglive")
				.addTextBody("idcard_name", idCardName)
				.addTextBody("idcard_number", idCardNumber)
				.addTextBody("delta", delta)
				.addPart("image_best", new InputStreamBody(is, "aa.txt"))
				.build();
		httppost.setHeader("contentType", "UTF-8");  
		httppost.setEntity(entity);
		HttpResponse httpResponse = httpclient.execute(httppost);
		httpResponse.getStatusLine().getStatusCode();
		HttpEntity httpEntity = httpResponse.getEntity();
		String sresult = EntityUtils.toString(httpEntity, "UTF-8");// 取出应答字符串
		FacePlusFaceLivingRespBo result = JSONObject.parseObject(sresult, FacePlusFaceLivingRespBo.class);
          return result;
	}
}
