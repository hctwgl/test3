package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.third.util.yitu.FileHelper;
import com.ald.fanbei.api.dal.domain.IdsBeanDo;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component("EviDoc")
public class EviDoc {
	private static Logger logger = LoggerFactory.getLogger(EviDoc.class);
	private static String encoding = null;
	private static String algorithm = null;
	private static String projectId = null;
	private static String projectSecret = null;
	// 文档保全测试环境地址
	private static String eviUrl = null;
	// 待保全文档路径
	private static String filePath = null;
	private static String eviName = null;
	private static String esignIdFirst = null;
	private static String esignIdSecond = null;
	private static String esignIdThird = null;
	/*public static void main(String[] args) {
		
		// 初始化全部参数
		initGlobalParameters("1111563517","95439b0863c241c63a861b87d1e647b7","http://smlcunzheng.tsign.cn:8083/evi-service/evidence/v1/preservation/original/url","F:/doc/" + File.separator + "test.pdf");
		
		// 用户获取文档保全Url和存证编号
		Map<String, String> eviMap = getEviUrlAndEvId();
		
		// 上传需要保全的文档
		if ("0".equals(eviMap.get("errCode"))) {
			String updateUrl = eviMap.get("文档保全上传Url");
			String evId = eviMap.get("存证编号");
			System.out.println("存证编号= " + evId);
			System.out.println("文件上传地址= " + updateUrl);
			// 文件上传
			updateFileRequestByPost(updateUrl,filePath);
		}
	}*/

	/***
	 * 初始化全局参数
	 */
	public static void initGlobalParameters(String pId, String secret, String eUrl, String path,String name,String esignFirst,String esignSecond,String esignThird) {
		encoding = "UTF-8";
		algorithm = "HmacSHA256";
		projectId = pId;
		projectSecret = secret;
		eviUrl = eUrl;
		filePath = path;
		eviName = name;
		esignIdFirst = esignFirst;
		esignIdSecond = esignSecond;
		esignIdThird = esignThird;
		/*projectId = "1111563517";
		projectSecret = "95439b0863c241c63a861b87d1e647b7";
		// 文档保全测试环境地址
		eviUrl = "http://smlcunzheng.tsign.cn:8083/evi-service/evidence/v1/preservation/original/url";
		// 待保全文档 默认路径在项目下的files文件夹下
		filePath = "F:/doc/" + File.separator + "test.pdf";*/
	}

	/***
	 * 用户获取文档保全Url和存证编号
	 * 
	 * @return
	 */
	public static Map<String, String> getEviUrlAndEvId() {
		Map<String, String> hashMap = null;
		JSONObject jsonObj = eviRequestByPost();
		String errCode = jsonObj.get("errCode").toString();
		if ("0".equals(errCode)) {
			hashMap = new HashMap<String, String>();
			String evId = jsonObj.get("evid").toString();
			String updateUrl = jsonObj.get("url").toString();
			hashMap.put("errCode", errCode);
			hashMap.put("存证编号", evId);
			hashMap.put("文档保全上传Url", updateUrl);
		} else {
			hashMap = new HashMap<String, String>();
			hashMap.put("errCode", errCode);
			System.out.println("errCode = " + errCode + "msg = " + jsonObj.get("msg"));
		}
		return hashMap;
	}

	/***
	 * 模拟发送文档保全Url请求 ，请求方式：POST
	 * 
	 * @return
	 */
	public static JSONObject eviRequestByPost() {
		StringBuffer strBuffer = null;
		try {
			// 建立连接
			URL url = new URL(eviUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			initPostHeaders(httpURLConnection);
			// 连接会话
			httpURLConnection.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			// 设置请求参数
			dos.write(setPostJSONStr().getBytes("UTF-8"));
			System.out.println("body = " + setPostJSONStr());
			dos.flush();
			dos.close();
			// 获得响应状态
			int resultCode = httpURLConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == resultCode) {
				strBuffer = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					strBuffer.append(readLine);
				}
				logger.info("存证信息:",strBuffer);
				responseReader.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return JSONObject.fromObject(strBuffer.toString());
	}

	/***
	 * 模拟上传文档请求 ，请求方式：Put
	 * 
	 * @return
	 */
	public static void updateFileRequestByPost(String updateUrl,String filePath) {
		StringBuffer strBuffer = null;
		try {
			// 建立连接
			URL url = new URL(updateUrl);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setDoOutput(true); // 需要输出
			httpURLConnection.setDoInput(true); // 需要输入
			httpURLConnection.setUseCaches(false); // 不允许缓存

			httpURLConnection.setRequestMethod("PUT");
			httpURLConnection.setRequestProperty("Content-MD5", AlgorithmHelper.getContentMD5(filePath));
			httpURLConnection.setRequestProperty("Content-Type", "application/octet-stream");
			httpURLConnection.setRequestProperty("Charset", encoding);
			// 连接会话
			httpURLConnection.connect();
			// 建立输入流，向指向的URL传入参数
			DataOutputStream dos = new DataOutputStream(httpURLConnection.getOutputStream());
			
			// 设置请求参数
			dos.write(FileHelper.getBytes(filePath));
			dos.flush();
			dos.close();
			// 获得响应状态
			int resultCode = httpURLConnection.getResponseCode();
			if (HttpURLConnection.HTTP_OK == resultCode) {
				System.out.println("上传成功！Http-Status = " + resultCode + " " + httpURLConnection.getResponseMessage());
				strBuffer = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					strBuffer.append(readLine);
				}
				responseReader.close();
			}else{
				System.out.println("上传失败！Http-Status = " + resultCode + " " + httpURLConnection.getResponseMessage());
				strBuffer = new StringBuffer();
				String readLine = new String();
				BufferedReader responseReader = new BufferedReader(
						new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
				while ((readLine = responseReader.readLine()) != null) {
					strBuffer.append(readLine);
				}
				responseReader.close();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/***
	 * 设置请求报头 HTTP Headers
	 * 
	 * @param
	 * @param httpURLConnection
	 * @return
	 */
	public static void initPostHeaders(HttpURLConnection httpURLConnection) {
		// 通过HmacSHA256算法获取签名信息，用以验签
		String signature = AlgorithmHelper.getXtimevaleSignature(setPostJSONStr(), projectSecret, algorithm, encoding);
		// 设置Headers参数
		try {
			httpURLConnection.setDoOutput(true); // 需要输出
			httpURLConnection.setDoInput(true); // 需要输入
			httpURLConnection.setUseCaches(false); // 不允许缓存

			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setRequestProperty("Content-type", "application/json");
			httpURLConnection.setRequestProperty("X-timevale-project-id", projectId);
			httpURLConnection.setRequestProperty("X-timevale-signature", signature);
			httpURLConnection.setRequestProperty("signature-algorithm", algorithm);
			httpURLConnection.setRequestProperty("Content-Type", "application/json");
			httpURLConnection.setRequestProperty("Charset", encoding);

		} catch (IOException e) {
			e.printStackTrace();
			logger.info("设置请求报头 HTTP Headers异常：" + e.getMessage());
		}
	}

	/***
	 * 设置POST请求参数
	 * 
	 * @return
	 */
	public static String setPostJSONStr() {
		JSONObject eviObj = new JSONObject();
		eviObj.put("eviName", eviName);
		eviObj.put("content", setContent(filePath));
		eviObj.element("eSignIds", setEsignIds());
		return eviObj.toString();
	}

	/***
	 * 
	 * @param contentDescription
	 *            内容描述，如文件名
	 * @param contentLength
	 *            内容数据长度，单位：字节
	 * @param contentBase64Md5
	 *            内容字节流MD5的Base64编码值
	 * @return
	 */
	public static String setContent(String filePath) {
		Map<String, String> fileInfos = FileHelper.getFileInfo(filePath);
		JSONObject contentObj = new JSONObject();
		// 内容描述，如文件名等
		contentObj.put("contentDescription", fileInfos.get("FileName"));
		// 内容数据长度，单位：字节，如文件大小
		contentObj.put("contentLength", fileInfos.get("FileLength"));
		// 内容字节流MD5的Base64编码值，如获取文件MD5后再Base64编码
		contentObj.put("contentBase64Md5", AlgorithmHelper.getContentMD5(filePath));
		return contentObj.toString();
	}

	/***
	 * eSignIds eSignIds 电子签名证据id,0-电子签名，1-时间戳；电子签名和时间戳Ids至少包含一个值
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static String setEsignIds() {

		IdsBeanDo eSignIdsBean1 = new IdsBeanDo();
		// 0 代表使用e签宝的电子签名服务
		eSignIdsBean1.setType("0");
		// value值就是 e签宝 SDK电子签名后返回的 signServiceId 签署记录id
		eSignIdsBean1.setValue(esignIdFirst);
		IdsBeanDo eSignIdsBean2 = new IdsBeanDo();
		// 0 代表使用e签宝的电子签名服务
		eSignIdsBean2.setType("0");
		// value值就是 e签宝 SDK电子签名后返回的 signServiceId 签署记录id
		eSignIdsBean2.setValue(esignIdSecond);
		IdsBeanDo eSignIdsBean3 = new IdsBeanDo();
		// 0 代表使用e签宝的电子签名服务
		eSignIdsBean3.setType("0");
		// value值就是 e签宝 SDK电子签名后返回的 signServiceId 签署记录id
		eSignIdsBean3.setValue(esignIdThird);
//		IdsBean eSignIdsBean2 = new IdsBean();
//		// 1 代表使用e签宝的时间戳服务
//		eSignIdsBean2.setType("1");
//		// value值就是 e签宝时间戳服务返回的 timestampId 时间戳数据记录ID
//		eSignIdsBean2.setValue("456987-12f11230123-12ojawfowjfoj-afweafawfe");

		List<IdsBeanDo> eSignIds = new ArrayList<IdsBeanDo>();
		eSignIds.add(eSignIdsBean1);
		eSignIds.add(eSignIdsBean2);
		eSignIds.add(eSignIdsBean3);
		return setIds(eSignIds);
	}

	/***
	 * bizIds不是必须的，允许保全未使用e签宝实名认证服务的文档； bizIds e签宝业务id列表中type = 0
	 * 的value就是调用e签宝实名认证成功返回的serviceId
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static String setBizIds() {
		IdsBeanDo bizIdsBean1 = new IdsBeanDo();
		// 0 代表使用了e签宝的实名认证服务
		bizIdsBean1.setType("0");
		// value值就是调用e签宝实名认证服务返回的serviceId 实名认证请求ID
		bizIdsBean1.setValue("3684eb08-f089-49f9-b5e3-bfa251669fe6");
		List<IdsBeanDo> bizIds = new ArrayList<IdsBeanDo>();
		bizIds.add(bizIdsBean1);
		return setIds(bizIds);
	}

	/***
	 * 设置Ids
	 * 
	 * @param type
	 * @param value
	 * @return
	 */
	public static String setIds(List<IdsBeanDo> list) {
		JSONArray idsArray = JSONArray.fromObject(list);
		return idsArray.toString();
	}

}
