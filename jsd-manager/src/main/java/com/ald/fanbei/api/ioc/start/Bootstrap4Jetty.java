package com.ald.fanbei.api.ioc.start;

import com.ald.fanbei.api.server.webapp.JettyServerStart;
/**
 * 启动类
 * @attention 使用此类启动项目时，配置文件fbapi.notify.host = http://192.168.106.191:8280 会自动替换为当前机器IP:http://localhost:8078
 * @author rongbo
 *
 */
public class Bootstrap4Jetty {
	
	public static final int PORT = 8098;
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