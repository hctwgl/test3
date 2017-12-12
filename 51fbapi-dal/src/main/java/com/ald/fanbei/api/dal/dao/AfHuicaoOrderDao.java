package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfHuicaoOrderDo;
import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author honghzengpei 2017/10/30 17:16
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfHuicaoOrderDao {
    AfHuicaoOrderDo getOrderByOrderNo(@Param("orderNo") String orderNo);
    AfHuicaoOrderDo getHuicaoOrderByThirdOrderNo(@Param("thirdNo") String thirdNo);
    int updateOrderStatus(@Param("id") long id,@Param("status") int status);
    int addHuicaoOrder(AfHuicaoOrderDo afHuicaoOrderDo);
    int updateHuicaoOrderStatusByOrderNo(@Param("orderNo") String orderNo,@Param("status") int status);

    List<AfHuicaoOrderDo> getUnFinishOrder(@Param("userId") Long userId, @Param("payType") String payType);

    int updateHuicaoOrderStatusLock(@Param("status") int status, @Param("id")long id, @Param("gmtModified")Date gmtModified);

    List<AfHuicaoOrderDo> getHuicaoUnFinishOrderAll();
}
