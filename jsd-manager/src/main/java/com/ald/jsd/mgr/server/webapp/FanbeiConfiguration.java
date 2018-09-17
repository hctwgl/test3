package com.ald.jsd.mgr.server.webapp;

import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;
/**
 * Jetty服务Configuration扩展
 * @author rongbo
 *
 */
public class FanbeiConfiguration extends AbstractConfiguration{

	PlaceHolderParser placeHolderParser = new PlaceHolderParser();
	
	
    @Override
    public void configure(WebAppContext context) throws Exception
    {
    	placeHolderParser.repalce();
    	
    	String tmpDir = System.getProperty("java.io.tmpdir");
		String autoConfigPath = tmpDir + "jettty_fanbei_api/WEB-INF/classes";
    	
    	WebAppClassLoader clsLoader = (WebAppClassLoader)context.getClassLoader();
    	clsLoader.addClassPath(autoConfigPath);
    }


}
