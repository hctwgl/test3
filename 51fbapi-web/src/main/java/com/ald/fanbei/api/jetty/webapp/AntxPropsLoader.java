package com.ald.fanbei.api.jetty.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ald.fanbei.api.ioc.start.Bootstrap;

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
		String antxPath = Bootstrap.ROOT_PATH + "src/conf/antx_" + Bootstrap.ENV_TYPE + ".properties";
		load(antxPath);
	}

	public Properties getPros() {
		return pros;
	}
	
	public static void main(String[] args) {
		new AntxPropsLoader().load();
	}

}
