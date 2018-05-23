package com.ald.fanbei.api.biz.arbitration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * 字符串解、压缩工具类
 * 
 * @Title：StringCompressUtils.java
 * @Author：wangyihui
 * @Date：2017年7月3日下午8:03:25
 * @Description
 */
public class StringCompressUtils {

	/**
	 * 压缩
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String compress(String str) throws IOException {
	    if (str == null || str.length() == 0) {
	    	return str;
	    }
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    GZIPOutputStream gzip = new GZIPOutputStream(out);
	    gzip.write(str.getBytes());
	    gzip.close();
	    return out.toString("ISO-8859-1");
	}  
	  
	/**
	 * 解压缩
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String decompress(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(str
	        .getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		//toString()使用平台默认编码，也可以显式的指定如toString("GBK")
		return out.toString("utf-8");
	}

}
