package com.ald.fanbei.api.ioc.start;

import com.ald.fanbei.api.server.webapp.JettyServerStart;
/**
 * 启动类
 * @author rongbo
 *
 */
public class Bootstrap4Jetty {
	
	public static final int PORT = 8078;
	public static final String ENV_TYPE = "test"; // test,pre_env,online
	public static final String ROOT_PATH;
	
	static {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		ROOT_PATH = rootPath.replace("target/classes/", "");
	}
	public static void main(String[] args) {
	
		String webPath = ROOT_PATH + "src/main/webapp";
		new JettyServerStart(webPath, PORT, "/", 0, true).start();
	}
}

