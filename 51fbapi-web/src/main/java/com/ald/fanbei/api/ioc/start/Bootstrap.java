package com.ald.fanbei.api.ioc.start;

public class Bootstrap {
	public static void main(String[] args) {
	
		String webapp = "src/main/webapp";
		new JettyServerStart(webapp, 9090, "/").start();
		
	}
}
