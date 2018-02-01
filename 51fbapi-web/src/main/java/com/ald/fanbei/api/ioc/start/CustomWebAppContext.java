package com.ald.fanbei.api.ioc.start;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.util.resource.FileResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;


public class CustomWebAppContext  extends WebAppContext{
	
	private static String visualPath = null;
	private static String autoconfigPath = null;
	PlaceHolderReplace placeHolder = new PlaceHolderReplace();
	
	private WebAppClassLoader appClassLoader = null;
	
	static {
		
		String dir = System.getProperty("java.io.tmpdir");

		File f = new File(dir, "hsf_jetty_placeholder");
		visualPath = f.getAbsolutePath();
		f = new File(dir, "hsf_jetty");
		autoconfigPath = f.getAbsolutePath();
		
		
	}
	
	public CustomWebAppContext(String webappPath, String context) {
		super(webappPath, context);
	}

	@Override
	public Resource getResource(String uriInContext) throws MalformedURLException {
		if ((this.appClassLoader != null) && (!("/".equals(uriInContext)))) {
			URL url = this.appClassLoader.getResource(uriInContext);
			if ((url != null) && ("file".equals(url.getProtocol()))) {
				try {
					String file = url.toURI().getPath();
					File f = new File(file);
					if (f.exists())
						try {
							System.out.println("HSFJettyWebAppContext replace servlet context get file " + file);
							return new FileResource(url);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}

		}

		Resource resource = super.getResource(uriInContext);

		if ((resource != null) && ("file".equalsIgnoreCase(resource.getURL().getProtocol()))
				&& (!(uriInContext.endsWith("web.xml"))) && (PlaceHolderReplace.needReplace(uriInContext))) {
			try {
				File f = resource.getFile();
				if ((f != null) && (f.exists())) {
					String charset = "UTF-8";
					String xmlString = getFile2String(resource.getURL(), charset);
					Set set = new HashSet();
					if (this.placeHolder.hasPlaceHolder(xmlString)) {
						String finalString = this.placeHolder.parseStringValue(xmlString, System.getProperties(), set);
						return new FileResource(saveFile(finalString, uriInContext, charset));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				return resource;
			} catch (URISyntaxException e) {
				e.printStackTrace();
				return resource;
			}
		}
		return resource;
	}
	
	private URL saveFile(String finalString, String name, String charset) {
		File f = new File(visualPath + File.separator + name);
		System.out.println("Web Context替换文件到【" + f.getAbsolutePath() + "】");
		PrintWriter pw = null;
		try {
			if (!(f.exists())) {
				File parent = f.getParentFile();
				if (!(parent.exists())) {
					parent.mkdirs();
				}
				f.createNewFile();
			}
			if (charset == null)
				pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f))), false);
			else {
				pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), charset)),
						false);
			}
			pw.print(finalString);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (pw != null)
				pw.close();
		}
		try {
			return f.toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String getFile2String(URL orignalPath, String charset) {
		BufferedReader br = null;
		try {
			InputStream stream = orignalPath.openStream();
			if (charset == null)
				br = new BufferedReader(new InputStreamReader(stream));
			else {
				br = new BufferedReader(new InputStreamReader(stream, charset));
			}

			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line + "\r\n");
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException localIOException3) {
				}
		}
		return null;
	}

	public WebAppClassLoader getAppClassLoader() {
		return appClassLoader;
	}

	public void setAppClassLoader(WebAppClassLoader appClassLoader) {
		this.appClassLoader = appClassLoader;
	}
	
}
