package com.ald.fanbei.api.server.webapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.ioc.start.Bootstrap4Jetty;
/**
 * 加载properties
 * @author rongbo
 *
 */
public class AntxPropsLoader {

	Properties pros = new Properties();
	public static String NOTIFY_HOST;
	
	public AntxPropsLoader() {
		load();
	}

	public void load() {
		String antxPath = Bootstrap4Jetty.ROOT_PATH + "src/conf/jsd_admin_" + Bootstrap4Jetty.ENV_TYPE + ".properties";
		try {
			pros.load(new FileInputStream(antxPath));
			
			NOTIFY_HOST = "http://" + GetHostIpUtil.getIpAddress() + ":" + Bootstrap4Jetty.PORT;
			pros.setProperty("fbapi.notify.host", NOTIFY_HOST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Properties getPros() {
		return pros;
	}
	
	public String getProperty(String key) {
		return pros.getProperty(key);
	}
}
