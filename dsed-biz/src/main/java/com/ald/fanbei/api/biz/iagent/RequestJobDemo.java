package com.ald.fanbei.api.biz.iagent; /**
 * 
 */


import com.ald.fanbei.api.biz.iagent.utils.AOSHttpClient;
import com.ald.fanbei.api.biz.iagent.utils.AOSJson;
import com.ald.fanbei.api.biz.iagent.utils.HttpRequestVO;
import com.ald.fanbei.api.biz.iagent.utils.HttpResponseVO;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chailongjie
 *
 */
public class RequestJobDemo {

	/**
	 * 实时工单请求
	 */
	public static void requestJob() {
		Map map=new HashMap();
		map.put("work_id", "11111111");
		map.put("corp_code", "爱上街");
		map.put("access_token", "xCGQF6wNeA1z6Cmz2UMW8F1as");
		map.put("job_code", "51FB-SP01");
		map.put("order_id", "111311111");
		String work_data = "{'csv_phone_num':'13613317541','csv_arn':'SG18033003238','csv_name':'谢强','csv_sex':'男','csv_digit_4':8755,'csv_birth_date':'19890610','csv_staging':9,'csv_amt':1500,'csv_pay_way':'分期付款','csv_product_category':'手机通讯' }";
		Map dtt=null;
		try {
			dtt = AOSJson.fromJson(work_data, HashMap.class);

		} catch (Exception e) {
			e.printStackTrace();
		}

		Map<String, String> mdata = Maps.newHashMap();

		map.put("work_data", dtt);
		mdata.put("mdata", AOSJson.toJson(map));

		HttpRequestVO httpRequestVO = new HttpRequestVO("http://221.133.225.245:18080/worksheetAPI/requestJob", mdata);

		try {
			HttpResponseVO httpResponseVO = AOSHttpClient.upload(httpRequestVO);
			httpResponseVO.getOut();
			httpResponseVO.getStatus();
			System.out.println(httpResponseVO.getOut());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void main(String[] args) {
		RequestJobDemo.requestJob();
	}

}
