package com.ald.fanbei.api.ioc.start;

import com.ald.fanbei.api.jetty.webapp.JettyServerStart;

public class Bootstrap {
	
	public static void main(String[] args) {
	
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		rootPath = rootPath.replace("target/classes/", "");
		String webPath = rootPath + "src/main/webapp";
		
		new JettyServerStart(webPath, 8089, "/").start();
		
	}
}
