package com.ald.fanbei.api.ioc.start;

import com.ald.fanbei.api.jetty.webapp.JettyServerStart;

public class Bootstrap {
	
	public static String ENV_TYPE = "test"; // test,pre_env,online
	
	public static String ROOT_PATH;
	
	static {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		ROOT_PATH = rootPath.replace("target/classes/", "");
	}
	
	public static void main(String[] args) {
	
		String webPath = ROOT_PATH + "src/main/webapp";
		
		new JettyServerStart(webPath, 8089, "/").start();
		
	}
}
