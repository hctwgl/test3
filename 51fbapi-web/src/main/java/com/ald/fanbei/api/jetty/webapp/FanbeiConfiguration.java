package com.ald.fanbei.api.jetty.webapp;

import java.io.File;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

public class FanbeiConfiguration extends AbstractConfiguration{

	private static final Logger LOG = Log.getLogger(FanbeiConfiguration.class);

    @Override
    public void configure(WebAppContext context) throws Exception
    {
    	WebAppClassLoader clsLoader = (WebAppClassLoader)context.getClassLoader();
    	String dir = System.getProperty("java.io.tmpdir");

		File f = new File(dir, "hsf_jetty_placeholder");

		File autoFileDir = new File(dir, "hsf_jetty");
		if (autoFileDir.exists()) {
			clsLoader.addClassPath(autoFileDir.toString());
			File file = new File(autoFileDir, "WEB-INF");
			if (file.exists()) {
				File clzsDir = new File(file, "classes");
				if (clzsDir.exists()) {
					clsLoader.addClassPath(clzsDir.toString());
				}
			}
		}
    }


}
