package com.ald.fanbei.api.biz.util;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.util.HttpUtil;

@Component("asyLoginService")
public class AsyLoginService extends AbstractThird{
	
	private ExecutorService service = Executors.newFixedThreadPool(10);
	
	public void excute(Map<String,String> req,String url,String logType){
		AsyLoginTask task = new AsyLoginTask(req, url,logType);
		service.execute(task);
	}
	
	
	private static class AsyLoginTask implements Runnable{

		private Map<String,String> reqBo;
		private String url;
		private String logType;
		public  AsyLoginTask(Map<String,String> req,String url,String logType){
			this.reqBo = req;
			this.url = url;
			this.logType = logType;
		}
		@Override
		public void run() {
			String reqResult = HttpUtil.post(url, reqBo);
			logThird(reqResult, logType, reqBo);
		}
		
	}

}
