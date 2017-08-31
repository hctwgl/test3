package com.ald.fanbei.api.biz.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;

@Component("asyLoginService")
public class AsyLoginService {
	
	private ExecutorService service = Executors.newFixedThreadPool(10);
	
	
	private static class AsyLoginTask implements Runnable{

		public  AsyLoginTask(){
			
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
