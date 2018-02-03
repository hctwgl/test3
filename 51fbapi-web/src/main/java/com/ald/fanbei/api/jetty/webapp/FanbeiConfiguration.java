package com.ald.fanbei.api.jetty.webapp;

import java.net.URL;

import org.eclipse.jetty.webapp.AbstractConfiguration;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

import com.ald.fanbei.api.ioc.start.Bootstrap;

public class FanbeiConfiguration extends AbstractConfiguration{

	
    @Override
    public void configure(WebAppContext context) throws Exception
    {
    	
    	URL webInf =  Bootstrap.class.getClassLoader().getResource("WEB-INF/classes");
    	WebAppClassLoader clsLoader = (WebAppClassLoader)context.getClassLoader();
    	clsLoader.addClassPath(webInf.getPath());
    	
    }


}
