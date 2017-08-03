/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfMobileChargeService;
import com.ald.fanbei.api.dal.dao.AfMoblieChargeDao;
import com.ald.fanbei.api.dal.domain.AfMoblieChargeDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月3日下午7:15:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afMobileChargeService")
public class AfMobileChargeServiceImpl implements AfMobileChargeService {
	@Resource
	AfMoblieChargeDao afMoblieChargeDao;

	@Override
	public AfMoblieChargeDo getMoblieChargeByTypeAndCompany(String province, String company) {
		return afMoblieChargeDao.getMoblieChargeByTypeAndCompany(province, company);
	}

}
