package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserAmountDo;

/**
 * @author honghzengpei 2017/11/21 14:55
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAmountDao {
    /**
     * 增加记录
     * @param afUserAmountDo
     * @return
     */
    int addUserAmount(AfUserAmountDo afUserAmountDo);
}
