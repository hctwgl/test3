/**
 * 
 */
package com.ald.fanbei.api.biz.service;


import com.ald.fanbei.api.dal.domain.AfMoblieChargeDo;

/**
 * @类描述：
 * @author suweili 2017年3月3日下午7:14:11
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfMobileChargeService {
	
	AfMoblieChargeDo getMoblieChargeByTypeAndCompany(String province,String company);

}
