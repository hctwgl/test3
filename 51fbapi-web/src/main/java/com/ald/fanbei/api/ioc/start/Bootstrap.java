package com.ald.fanbei.api.ioc.start;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.ald.fanbei.api.server.webapp.JettyServerStart;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
/**
 * 启动类
 * @author rongbo
 *
 */
public class Bootstrap {
	
	public static String ENV_TYPE = "test"; // test,pre_env,online
	
	public static String ROOT_PATH;
	
	static {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		ROOT_PATH = rootPath.replace("target/classes/", "");
	}
	public static void main(String[] args) {
	
		String webPath = ROOT_PATH + "src/main/webapp";
		String bannerPath = Bootstrap.class.getClassLoader().getResource("banner.txt").getPath();

		try {
			List<String> lines = Files.readLines(new File(bannerPath), Charsets.UTF_8);
			for (String line : lines) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		new JettyServerStart(webPath, 8089, "/", 2, true).start();

	}
}

