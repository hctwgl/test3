/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfAbTestDeviceService;
import com.ald.fanbei.api.dal.dao.AfAbTestDeviceDao;
import com.ald.fanbei.api.dal.domain.AfAbTestDeviceDo;

/**
 * @类描述：
 * @author 江荣波 2017年6月20日下午4:47:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afAbTestDeviceService")
public class AfAbTestDeviceServiceImpl  implements AfAbTestDeviceService {
	@Resource 
	AfAbTestDeviceDao afAbTestDeviceDao;
	@Override
	public int addUserDeviceInfo(AfAbTestDeviceDo abTestDeviceDo) {
		return afAbTestDeviceDao.addUserDeviceInfo(abTestDeviceDo);
	}

}
