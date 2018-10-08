package com.ald.jsd.mgr.ioc.start;

import com.ald.jsd.mgr.ioc.start.server.JettyServerStart;
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
	public static final String PARENT_ROOT_PATH;
	
	static {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		ROOT_PATH = rootPath.replace("target/classes/", "");
		String tmpStr = ROOT_PATH.substring(0, ROOT_PATH.length() - 1);
		PARENT_ROOT_PATH = tmpStr.substring(0, tmpStr.lastIndexOf("/") + 1);
	}
	public static void main(String[] args) {
		try {
			System.out.println("ROOT_PATH = " + ROOT_PATH);
			System.out.println("PARENT_ROOT_PATH = " + PARENT_ROOT_PATH);
			
			String webPath = ROOT_PATH + "src/main/webapp";
			new JettyServerStart(webPath, PORT, "/", 0, true).start();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}