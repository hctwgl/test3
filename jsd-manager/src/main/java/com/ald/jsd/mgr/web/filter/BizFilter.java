package com.ald.jsd.mgr.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.jsd.mgr.web.Sessions;
import com.alibaba.fastjson.JSON;

public class BizFilter implements Filter{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String UTF_8 = "UTF-8";
    private static final String LOG_SEPARATOR = " | ";
    
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest)request;
		HttpServletResponse servletResponse = (HttpServletResponse)response;
		
		String logStr = 
	    		LOG_SEPARATOR + Sessions.getIp(servletRequest) +
	    		LOG_SEPARATOR + Sessions.getUsername(servletRequest) +
	    		LOG_SEPARATOR + servletRequest.getRequestURI() +
	    		LOG_SEPARATOR + servletRequest.getMethod() +
	    		LOG_SEPARATOR + servletRequest.getContentLength() +
	    		LOG_SEPARATOR + JSON.toJSONString(servletRequest.getParameterMap());
		
		servletRequest.setCharacterEncoding(UTF_8);
		servletResponse.setCharacterEncoding(UTF_8);
		
		BizResponseWrapper bizResponseWrapper = new BizResponseWrapper(servletResponse);
		
		long start = System.currentTimeMillis();
		chain.doFilter(request, bizResponseWrapper);
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
    
	
	private static class BizResponseWrapper extends HttpServletResponseWrapper {
		private ByteArrayOutputStream buffer = null;//输出到byte array
	    private ServletOutputStream out = null;
	    private PrintWriter writer = null;

	    public BizResponseWrapper(HttpServletResponse resp) throws IOException {
	        super(resp);
	        buffer = new ByteArrayOutputStream();
	        out = new WapperedOutputStream(buffer);
	        writer = new WapperedPrintWriter(new OutputStreamWriter(buffer, this.getCharacterEncoding()));
	    }

	    @Override
	    public ServletOutputStream getOutputStream() throws IOException {
	        return out;
	    }

	    @Override
	    public PrintWriter getWriter() throws UnsupportedEncodingException {
	        return writer;
	    }

	    @Override
	    public void flushBuffer() throws IOException {
	        if (out != null) {
	            out.flush();
	        }
	        if (writer != null) {
	            writer.flush();
	        }
	    }

	    @Override
	    public void reset() {
	        buffer.reset();
	    }

	    public byte[] getResponseData() throws IOException {
	        flushBuffer();
	        return buffer.toByteArray();
	    }
	    
	    public String getHolderStr() {
	    	return ((WapperedPrintWriter)writer).getHolderString();
	    }

	    private class WapperedPrintWriter extends PrintWriter {
	    	private StringBuilder holderStr = new StringBuilder();
	    	
			public WapperedPrintWriter(OutputStreamWriter outputStreamWriter) {
				super(outputStreamWriter);
			}
			
			@Override
			public void write(String s) {
				this.holderStr.append(s);
				super.write(s);
			}
			
			@Override
			public void write(String s, int off, int len) {
				this.holderStr.append(s.substring(off, off+len));
				super.write(s, off, len);
			}
			
			public String getHolderString() {
				return holderStr.toString();
			}
	    }
	    
	    private class WapperedOutputStream extends ServletOutputStream {
	        private ByteArrayOutputStream bos = null;

	        public WapperedOutputStream(ByteArrayOutputStream stream) throws IOException {
	            bos = stream;
	        }

	        @Override
	        public void write(int b) throws IOException {
	            bos.write(b);
	        }

	        @Override
	        public void write(byte[] b) throws IOException {
	            bos.write(b, 0, b.length);
	        }

	        public boolean isReady() {
	            return false;
	        }

	        public void setWriteListener(WriteListener writeListener) {
	        }
	    }
	    
	}
}
