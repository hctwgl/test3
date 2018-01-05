package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;

/**
 * @author honghzengpei 2017/11/21 15:25
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAmountDetailDao {
    /**
     * 增加记录
     * @param afUserAmountDetailDo
     * @return
     */
    int addUserAmountDetail(AfUserAmountDetailDo afUserAmountDetailDo);
}
