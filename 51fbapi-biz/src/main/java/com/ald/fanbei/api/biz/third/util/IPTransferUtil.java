package com.ald.fanbei.api.biz.third.util;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.IPTransferBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

/**
 * 
 * @类描述：IP转换工具
 * @author xiaotianjian 2017年8月15日上午11:39:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("iPTransferUtil")
public class IPTransferUtil extends AbstractThird{
	
	private static final String IP_DAT_URL = "/home/aladin/project/tomcat/ipData/GeoLiteCity.dat";
	
	public IPTransferBo parseIpToLatAndLng(String ip) throws IOException{
		LookupService cl = new LookupService(IP_DAT_URL, LookupService.GEOIP_MEMORY_CACHE);
		Location l2 = cl.getLocation(ip);
		IPTransferBo bo = new IPTransferBo();
		if(l2 != null ){
			bo.setCountryCode(l2.countryCode);
			bo.setCountryName(l2.countryName);
			bo.setLatitude(new BigDecimal(l2.latitude));
			bo.setLongitude(new BigDecimal(l2.longitude));
			bo.setRegion(l2.region);
		}
		thirdLog.info("parseIpToLatAndLng complete ip = {},result = {}",ip,bo);
		return bo;
	}
	
}
