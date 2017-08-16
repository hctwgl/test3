package com.ald.fanbei.api.biz.service.wxpay;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 *@类描述：微信xml数据解析
 *@author hexin 2017年2月27日 下午17:03:05
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class WxXMLParser {
	private static class PropertyCollector extends DefaultHandler {
		private Properties prop = new Properties();
		private LinkedList<String> keyStack = new LinkedList<String>();
		private LinkedList<StringBuilder> valueStack = new LinkedList<StringBuilder>();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			this.keyStack.add(qName);
			this.valueStack.add(new StringBuilder());

			return;
		}

		@Override
		public void endElement(String uri, String localName, String qName) {
			String key = this.keyStack.removeLast();
			StringBuilder value = this.valueStack.removeLast();

			this.prop.setProperty(key, value.toString());

			if (valueStack.size() > 0)
				this.valueStack.getLast().append('<').append(key).append('>').append(value).append("</").append(key)
						.append('>');

			return;
		}

		@Override
		public void characters(char[] ch, int start, int length) {
			this.valueStack.getLast().append(ch, start, length);

			return;
		}

		public Properties returnProperties() {
			return (this.prop);
		}
	}

	public static Properties parseXML(String xmlString) {
		if (xmlString == null)
			throw (new IllegalArgumentException("argument xmlString must not be null."));

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			InputStream source = new ByteArrayInputStream(xmlString.getBytes("utf-8"));
			PropertyCollector pc = new PropertyCollector();

			parser.parse(source, pc);
			return (pc.returnProperties());
		} catch (Exception ex) {
			throw (new RuntimeException(ex.getMessage(), ex));
		}
	}

	public static Properties parseXML(InputStream xmlStream) {
		if (xmlStream == null)
			throw (new IllegalArgumentException("argument xmlStream must not be null."));

		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			PropertyCollector pc = new PropertyCollector();

			parser.parse(xmlStream, pc);
			return (pc.returnProperties());
		} catch (Exception ex) {
			throw (new RuntimeException(ex.getMessage(), ex));
		}
	}

	public static void main(String[] args) {
		System.out.println("Hello World!");
	}
}
