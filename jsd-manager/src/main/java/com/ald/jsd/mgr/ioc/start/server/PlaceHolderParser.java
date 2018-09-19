package com.ald.jsd.mgr.ioc.start.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ald.jsd.mgr.ioc.start.Bootstrap4Jetty;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
/**
 * placeholder解析类
 * @author rongbo
 *
 */
public class PlaceHolderParser {

	private AutoConfigParser autoConfigParser = new AutoConfigParser();

	private AntxPropsLoader propsLoader = new AntxPropsLoader();

	private String prefix = "${";

	private String postfix = "}";

	public void repalce() {
		AutoConfig autoConfig = autoConfigParser.parse();
		List<String> scriptList = autoConfig.getScriptList();
		for (String scriptPath : scriptList) {
			try {
				String readPath = Bootstrap4Jetty.ROOT_PATH + "src/main/resources/"
						+ scriptPath.replace("WEB-INF/classes/", "");
				String tmpDir = System.getProperty("java.io.tmpdir");
				String writePath = tmpDir + "jettty_fanbei_api/" + scriptPath;
				File writeFile = new File(writePath);
				File parentPath = writeFile.getParentFile();
				if (!parentPath.exists()) {
					parentPath.mkdirs();
				} 
				if(writeFile.exists()) {
					writeFile.delete();
					writeFile.createNewFile();
				} else {
					writeFile.createNewFile();
				}
				List<String> lines = Files.readLines(new File(readPath), Charsets.UTF_8);
				for (int i = 0; i < lines.size(); i++) {
					String line = lines.get(i);
					int beginIndex = 0;
					int endIndex = 0;
					while ((beginIndex = line.indexOf(prefix, 0)) != -1) {
						endIndex = line.indexOf(postfix, beginIndex);
						String placeHolder = line.substring(beginIndex, endIndex + 1);
						String content = line.substring(beginIndex + 2, endIndex);
						String holderContent = propsLoader.getProperty(content);
						if (StringUtils.isBlank(holderContent)) {
							throw new RuntimeException("placeholder not config in properties file=>" + placeHolder);
						}
						line = line.replace(placeHolder, holderContent);
					}
					Files.append(line + "\r\n", writeFile, Charsets.UTF_8);
				}
				

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
