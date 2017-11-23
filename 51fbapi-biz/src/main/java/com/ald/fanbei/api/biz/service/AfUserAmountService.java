package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.common.enums.AfUserAmountProcessStatus;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

/**
 * @author honghzengpei 2017/11/22 14:28
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAmountService {
    void addUseAmountDetail(AfRepaymentDo afRepaymentDo);
    void addUserAmountLog(AfRepaymentDo afRepaymentDo,AfUserAmountProcessStatus afUserAmountProcessStatus);
    void updateUserAmount(AfUserAmountProcessStatus afUserAmountProcessStatus,AfRepaymentDo afRepaymentDo);
}
