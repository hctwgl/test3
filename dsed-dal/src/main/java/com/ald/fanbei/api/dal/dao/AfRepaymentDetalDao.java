package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRepaymentDetalDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author honghzengpei 2017/9/21 10:19
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentDetalDao {
    int addRepaymentDetal(AfRepaymentDetalDo afRepaymentDetalDo);
    AfRepaymentDetalDo getRepaymentDetalByTypeAndId(@Param("repaymentId") long repaymentId,@Param("type") int type);
}
