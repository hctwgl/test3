package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfPacketResultDo;


/**
 * @类描述：
 * 
 * @author Jiang Rongbo 2017年8月4日下午5:02:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPacketResultService {

	int addPacketResult(AfPacketResultDo packetResultDo);

	int getGrantCouponCount(AfPacketResultDo packetResultDo);
	
}
