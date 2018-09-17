package com.ald.jsd.mgr.server.webapp;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ald.jsd.mgr.ioc.start.Bootstrap4Jetty;
import com.google.common.collect.Lists;
/**
 * 解析autoconfig配置文件
 * @author rongbo
 *
 */
public class AutoConfigParser {

	private static String autoConfigPath = "src/main/webapp/META-INF/autoconf/auto-config.xml";

	AutoConfig parse(String path) {
		AutoConfig autoConfig = new AutoConfig();
		try {
			DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.parse(path);
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList groups = (NodeList) xPath.evaluate("/config/group/*", doc, XPathConstants.NODESET);
			List<AutoConfig.Property> groupList = Lists.newArrayList();
			for (int i = 0; i < groups.getLength(); i++) {
				Node groupNode = groups.item(i);
				String name = (String) xPath.evaluate("@name", groupNode, XPathConstants.STRING);
				String defaultValue = (String) xPath.evaluate("@defaultValue", groupNode, XPathConstants.STRING);
				String description = (String) xPath.evaluate("@description", groupNode, XPathConstants.STRING);
				groupList.add(new AutoConfig.Property(name, defaultValue, description));
				autoConfig.setGroupList(groupList);
			}
			NodeList scripts = (NodeList) xPath.evaluate("/config/script/*", doc, XPathConstants.NODESET);
			List<String> scriptList = Lists.newArrayList();
			for (int i = 0; i < scripts.getLength(); i++) {
				Node scriptNode = scripts.item(i);
				
				String template = (String) xPath.evaluate("@template", scriptNode, XPathConstants.STRING);
				scriptList.add(template);
				autoConfig.setScriptList(scriptList);
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return autoConfig;
	}
	
	AutoConfig parse() {
		String rootPath = Bootstrap4Jetty.ROOT_PATH;
		return parse(rootPath + autoConfigPath);
	}
	
	public AutoConfigParser() {
		parse();
	}
	

	public static void main(String[] args) {
		new AutoConfigParser().parse();
	}

}
