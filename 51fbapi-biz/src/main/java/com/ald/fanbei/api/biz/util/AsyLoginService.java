package com.ald.fanbei.api.biz.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.RiskRespBo;
import com.ald.fanbei.api.biz.bo.risk.RiskTrustReqBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.alibaba.fastjson.JSONObject;

@Component("asyLoginService")
public class AsyLoginService extends AbstractThird{
	
	private ExecutorService service = Executors.newFixedThreadPool(10);
	
	public void excute(RiskTrustReqBo req,String url){
		AsyLoginTask task = new AsyLoginTask(req, url);
		service.execute(task);
	}
	
	
	private static class AsyLoginTask implements Runnable{

		private RiskTrustReqBo reqBo;
		private String url;
		public  AsyLoginTask(RiskTrustReqBo req,String url){
			this.reqBo = req;
			this.url = url;
		}
		@Override
		public void run() {
			String reqResult = HttpUtil.post(url, reqBo);
			logThird(reqResult, "verifyAsyLogin", reqBo);
		}
		
	}

}
