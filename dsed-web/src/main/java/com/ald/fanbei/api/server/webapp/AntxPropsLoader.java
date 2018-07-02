package com.ald.fanbei.api.server.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ald.fanbei.api.ioc.start.Bootstrap4Jetty;
/**
 * 加载properties
 * @author rongbo
 *
 */
public class AntxPropsLoader {

	Properties pros = new Properties();

	public void load(String path) {
		try {
			pros.load(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key) {
		return pros.getProperty(key);
	}
	
	public AntxPropsLoader() {
		load();
	}

	public void load() {
		String antxPath = Bootstrap4Jetty.ROOT_PATH + "src/conf/dsed_" + Bootstrap4Jetty.ENV_TYPE + ".properties";
		load(antxPath);
	}

	public Properties getPros() {
		return pros;
	}
	
	

}
