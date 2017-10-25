package com.ald.fanbei.api.biz.foroutapi.service;

import com.ald.fanbei.api.biz.foroutapi.annotation.AFBRPCService;

import java.math.BigDecimal;

/**
 * @author honghzengpei 2017/10/23 14:45
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@AFBRPCService(name = "homeBorrowService")
public interface HomeBorrowService {
    //String addHomeBorrow(final Long orderId,final int nper, final Long userId,BigDecimal amount);
    int addHomeBorrow(final Long orderId,final int nper);
}
