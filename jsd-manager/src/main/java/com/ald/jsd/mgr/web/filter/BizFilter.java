package com.ald.jsd.mgr.web.filter;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.jsd.mgr.web.Sessions;

public class BizFilter implements Filter{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public static final Charset CHARSET_UTF_8 = Charset.forName("UTF-8");
	public static final String UTF_8 = "UTF-8";
    public static final String LOG_SEPARATOR = " | ";
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		servletRequest.setCharacterEncoding(UTF_8);
		servletResponse.setCharacterEncoding(UTF_8);
		
		BizRequestWrapper bizRequestWrapper = new BizRequestWrapper(servletRequest);
		BizResponseWrapper bizResponseWrapper = new BizResponseWrapper(servletResponse);
		
		
		String logStr = 
	    		LOG_SEPARATOR + Sessions.getIp(servletRequest) +
	    		LOG_SEPARATOR + Sessions.getUsername(servletRequest) +
	    		LOG_SEPARATOR + servletRequest.getRequestURI() +
	    		LOG_SEPARATOR + servletRequest.getMethod() +
	    		LOG_SEPARATOR + servletRequest.getContentLength() +
	    		LOG_SEPARATOR + new String(bizRequestWrapper.getRequestBytes(), CHARSET_UTF_8);
		
		long start = System.currentTimeMillis();
		chain.doFilter(bizRequestWrapper, bizResponseWrapper);
		long end = System.currentTimeMillis();
		
		String result = bizResponseWrapper.getHolderStr();
		if(result.length() <= 2048) {
			logStr += LOG_SEPARATOR + result;
		}else {
			logStr += LOG_SEPARATOR + result.substring(0, 1024) + "......";
		}
		logStr += LOG_SEPARATOR + (end - start);
		
		logger.info(logStr);
		
		try (
			ServletOutputStream output = servletResponse.getOutputStream();
				){
			output.write(bizResponseWrapper.getResponseData());
		} catch (IOException e) {
			logger.error("BizFilter.write.error", e);
		}
	}
	@Override
	public void destroy() {}
    
	
}
