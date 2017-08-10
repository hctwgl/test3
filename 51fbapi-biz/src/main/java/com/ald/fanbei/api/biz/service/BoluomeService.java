package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.bo.BoluomeGetDidiRiskInfoRespBo;



/**
 * 
 * @类描述：关于菠萝觅的Service
 * @author xiaotianjian 2017年8月10日下午5:15:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface BoluomeService {
	
	BoluomeGetDidiRiskInfoRespBo getRiskInfo(String orderId, String type);
	
	
}