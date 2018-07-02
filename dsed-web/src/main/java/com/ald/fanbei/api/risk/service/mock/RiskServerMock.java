package com.ald.fanbei.api.risk.service.mock;

import com.ald.fanbei.api.ioc.start.Bootstrap4Jetty;
import com.github.tomakehurst.wiremock.standalone.WireMockServerRunner;
/**
 * 风控restful接口模拟工具
 * @author rongbo
 *
 */
public class RiskServerMock {

	
	public static void main(String[] args) {
		/**
		 * 请在51fbapi-web/mapping目录下配置接口，格式
		 {
		    "request": {
		        "method": "POST",
		        "url": "/modules/api/thrid/userSupplementQuota.htm"
		    },
		    "response": {
		        "status": 200,
		        "body": "More content\n"
		    }
		}
		 
		 */
		String[] extArgs = new String[]{"--port","80",
				"--root-dir",Bootstrap4Jetty.ROOT_PATH};
		new WireMockServerRunner().run(extArgs);
		
		
	}
}
