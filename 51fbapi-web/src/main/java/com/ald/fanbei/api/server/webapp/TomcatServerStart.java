package com.ald.fanbei.api.server.webapp;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public class TomcatServerStart {

	private int port;
	private String context;
	private String webappPath;
	private boolean autoDeploy = false;

	PlaceHolderParser placeHolderParser = new PlaceHolderParser();

	public TomcatServerStart(int port, String context, String webappPath, boolean autoDeploy) {
		this.port = port;
		this.context = context;
		this.webappPath = webappPath;
		this.autoDeploy = autoDeploy;
	}

	public void start() throws LifecycleException, ServletException {
		placeHolderParser.repalce();
		String tmpDir = System.getProperty("java.io.tmpdir");
		String autoConfigPath = tmpDir + "/jettty_fanbei_api/WEB-INF/classes";

		Tomcat tomcat = new Tomcat();
		tomcat.addWebapp(context, webappPath);
		tomcat.initWebappDefaults(autoConfigPath);
		tomcat.setPort(port);
		tomcat.getHost().setAutoDeploy(autoDeploy);
		tomcat.start();
		tomcat.getServer().await();

	}

}
