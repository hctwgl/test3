package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfPacketResultDo;

/**
 * @类描述：
 * @author 江荣波  2017年6月20日上午9:49:15
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfPacketResultDao {

	int addPacketResult(AfPacketResultDo packetResultDo);

	int getGrantCouponCount(AfPacketResultDo packetResultDo);

}
