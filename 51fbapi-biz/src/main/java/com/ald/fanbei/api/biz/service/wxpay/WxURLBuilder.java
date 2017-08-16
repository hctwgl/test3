package com.ald.fanbei.api.biz.service.wxpay;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * 
 *@类描述：微信url构建
*@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxURLBuilder {
	private static boolean eucIgnore[] = new boolean[256];
	static {
		String ignore = "1234567890qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM-_.!~*'()";
		for (int i = 0; i < ignore.length(); i++)
			eucIgnore[Character.codePointAt(ignore, i)] = true;
	}

	private static String percentify(String s) {
		StringBuilder sb = new StringBuilder();

		ByteBuffer bb = Charset.forName("utf-8").encode(s);
		int size = bb.limit();
		for (int i = 0; i < size; i++)
			sb.append(String.format("%%%02x", bb.get() & 0xff));

		return (sb.toString());
	}

	public static String encodeURIComponent(String s) {
		if (s == null)
			return (null);

		if ("".equals(s))
			return ("");

		StringBuilder sb = new StringBuilder();

		int _i = 0;
		int c = Character.codePointAt(s, _i);
		boolean ignore = c < 256 && eucIgnore[c];

		for (int i = 0; i < s.length(); i++) {
			c = Character.codePointAt(s, i);
			if (ignore != (c < 256 && eucIgnore[c])) {
				if (ignore)
					sb.append(s.substring(_i, i));
				else
					sb.append(percentify(s.substring(_i, i)));
				ignore = !ignore;
				_i = i;
			}
		}

		if (ignore)
			sb.append(s.substring(_i, s.length()));
		else
			sb.append(percentify(s.substring(_i, s.length())));

		return (sb.toString());
	}

	protected StringBuilder builder = new StringBuilder();
	protected boolean firstParam = true;
	protected boolean hasPath = false;

	protected void appendParamPrefix() {
		if (this.firstParam) {
			this.firstParam = false;
			if (this.hasPath)
				this.builder.append('?');
		} else {
			this.builder.append('&');
		}
	}

	public WxURLBuilder appendPath(String path) {
		if (path == null)
			return (this);

		if (this.hasPath || !this.firstParam)
			throw (new IllegalStateException("Missed the trick to set path."));

		this.hasPath = true;

		this.builder.append(path);

		return (this);
	}

	public WxURLBuilder appendParam(String key, String value) {
		if (key != null && value != null) {
			this.appendParamPrefix();
			this.builder.append(key).append('=');
			this.builder.append(value);
		}

		return (this);
	}

	public WxURLBuilder appendParamEncode(String key, String value) {
		if (key != null && value != null) {
			this.appendParamPrefix();
			this.builder.append(key).append('=');
			this.builder.append(encodeURIComponent(value));
		}

		return (this);
	}

	/**
	 * just for compatible, charset is ignored.
	 * 
	 * @param charset
	 *            <span color="red">ignored</span>
	 */
	public WxURLBuilder appendParamEncode(String key, String value, String charset) {
		this.appendParamEncode(key, value);

		return (this);
	}

	public WxURLBuilder appendLabel(String label) {
		this.builder.append('#').append(label);

		return (this);
	}

	public WxURLBuilder append(String str) {
		this.builder.append(str);

		return (this);
	}

	@Override
	public String toString() {
		return (this.builder.toString());
	}
}
