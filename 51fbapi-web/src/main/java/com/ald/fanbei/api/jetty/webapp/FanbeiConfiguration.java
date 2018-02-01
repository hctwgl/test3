package com.ald.fanbei.api.jetty.webapp;

import java.net.URL;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;

public class FanbeiConfiguration extends AbstractConfiguration{

	private static final Logger LOG = Log.getLogger(FanbeiConfiguration.class);

 

    @Override
    public void configure(WebAppContext context) throws Exception
    {
    	ClassLoader clsLoader = context.getClassLoader();
    	URL rootPath = clsLoader.getResource("/");
    	System.out.println(rootPath.getPath());
       
    }

    



	

}
