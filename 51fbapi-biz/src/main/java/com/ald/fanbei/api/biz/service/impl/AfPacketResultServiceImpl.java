/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfPacketResultService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfPacketResultDao;
import com.ald.fanbei.api.dal.domain.AfPacketResultDo;

/**
 * @类描述：
 * 
 * @author Jiang Rongbo 2017年8月4日下午3:45:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afPacketResultService")
public class AfPacketResultServiceImpl extends BaseService implements AfPacketResultService {
	@Resource
	AfPacketResultDao afPacketResultDao;
	
	@Override
	public int addPacketResult(AfPacketResultDo packetResultDo) {
		return afPacketResultDao.addPacketResult(packetResultDo);
	}

	@Override
	public int getGrantCouponCount(AfPacketResultDo packetResultDo) {
		return afPacketResultDao.getGrantCouponCount(packetResultDo);
	}
	
}
