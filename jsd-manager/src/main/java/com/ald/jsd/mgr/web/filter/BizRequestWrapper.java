package com.ald.jsd.mgr.web.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ZJF, Create 2018年9月19日 下午10:18:06
 */
public class BizRequestWrapper extends HttpServletRequestWrapper {
	private Logger logger = LoggerFactory.getLogger(BizRequestWrapper.class);
	
    private ServletInputStream in;
    private byte[] bytesBuf;
    
    public BizRequestWrapper(HttpServletRequest req) throws IOException {
        super(req);
        
        int contentLength = req.getContentLength();
        if(contentLength == -1) {
        	logger.warn("request length is not kown!");
        	ByteArrayOutputStream output = new ByteArrayOutputStream();
        	IOUtils.copy(req.getInputStream(), output);
        	bytesBuf = output.toByteArray();
        }else if(contentLength == 0){
        	logger.warn("request body has nothing!");
        	bytesBuf = "{}".getBytes(BizFilter.CHARSET_UTF_8);
        }else {
        	bytesBuf = new byte[req.getContentLength()];
            IOUtils.read(req.getInputStream(), bytesBuf);
        }
        
        in = new WapperedInputStream(new ByteArrayInputStream(bytesBuf));
    }

    public byte[] getRequestBytes() {
    	return bytesBuf;
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException {
        return in;
    }
    
    private class WapperedInputStream extends ServletInputStream {
        private ByteArrayInputStream bis;

        public WapperedInputStream(ByteArrayInputStream stream) throws IOException {
        	bis = stream;
        }

        @Override
        public boolean isReady() {
            return false;
        }

		@Override
		public boolean isFinished() {
			return false;
		}

		@Override
		public void setReadListener(ReadListener readListener) {
		}

		@Override
		public int read() throws IOException {
			return bis.read();
		}
    }
    
}
