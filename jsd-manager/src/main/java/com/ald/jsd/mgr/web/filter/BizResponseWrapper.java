package com.ald.jsd.mgr.web.filter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author ZJF, Create 2018年9月19日 下午10:18:41
 */
public class BizResponseWrapper extends HttpServletResponseWrapper {
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
    	String holderStr = ((WapperedPrintWriter)writer).getHolderString();
    	if( "".equals(holderStr.trim()) ) {
    		holderStr = new String(buffer.toByteArray(), BizFilter.CHARSET_UTF_8);
    	}
    	return holderStr;
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
