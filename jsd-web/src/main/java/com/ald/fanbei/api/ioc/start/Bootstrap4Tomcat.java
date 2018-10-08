package com.ald.fanbei.api.ioc.start;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.ald.fanbei.api.ioc.start.server.TomcatServerStart;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
/**
 * tomcat嵌入式启动类
 * @author rongbo
 *
 */
public class Bootstrap4Tomcat {
	
	public static String ENV_TYPE = "test"; // test,pre_env,online
	
	public static String ROOT_PATH;
	
	static {
		String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		ROOT_PATH = rootPath.replace("target/classes/", "");
	}
	public static void main(String[] args) throws Exception {
	
		String webPath = ROOT_PATH + "src/main/webapp";
		String bannerPath = Bootstrap4Tomcat.class.getClassLoader().getResource("banner.txt").getPath();

		try {
			List<String> lines = Files.readLines(new File(bannerPath), Charsets.UTF_8);
			for (String line : lines) {
				System.out.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		new TomcatServerStart(8089, "/",webPath,  true).start();

	}
}

