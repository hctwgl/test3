package com.ald.fanbei.api.biz.util;


//拼接跳转url
public class eviurl {


	public static void main(String[] args) {
		
			String evId ="O927820140198785033";
			String projectId = "1111563517";
			String projectSecret = "95439b0863c241c63a861b87d1e647b7";
			String viewUrl = "https://smlcunzheng.tsign.cn/evi-web/static/certificate-info.html" ;
			long timestamp=System.currentTimeMillis();
			String str = "id=" + evId + "&projectId=" + projectId + "&timestamp=" + timestamp;
			System.out.println(str);
			String signature = AlgorithmHelper.getXtimevaleSignature(str, projectSecret, "HmacSHA256", "UTF-8");
		//	System.out.println(signature);
			String urlStr = str + "&signature=" + signature;
			System.out.println(viewUrl + "?" + urlStr);		
		


	}

}
