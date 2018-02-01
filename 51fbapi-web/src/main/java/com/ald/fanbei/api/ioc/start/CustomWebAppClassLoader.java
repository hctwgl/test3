package com.ald.fanbei.api.ioc.start;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.webapp.WebAppClassLoader;

public class CustomWebAppClassLoader extends WebAppClassLoader{

	public CustomWebAppClassLoader(ClassLoader parent, Context context) throws IOException {
		super(parent, context);
	}
	
	@Override
	public URL getResource(String name) {
		System.out.println("name=>" + name);
		return super.getResource(name);
	}

}
