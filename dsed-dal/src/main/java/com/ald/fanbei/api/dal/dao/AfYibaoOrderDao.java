package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfYibaoOrderDo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @author honghzengpei 2017/9/8 9:04
 * @类描述：易宝订单
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfYibaoOrderDao {
    AfYibaoOrderDo getYiBaoOrderByOrderNo(@Param("orderNo") String orderNo);
    int updateYiBaoOrderStatus(@Param("id") long id,@Param("status") int status);
    int addYibaoOrder(AfYibaoOrderDo afYibaoOrderDo);
    int updateYiBaoOrderStatusByOrderNo(@Param("orderNo") String orderNo,@Param("status") int status);

    List<AfYibaoOrderDo> getYiBaoUnFinishOrderByUserId(@Param("userId") Long userId,@Param("oType") int oType);

    int updateYiBaoOrderStatusLock(@Param("status") int status, @Param("id")long id, @Param("gtmUpdate")Date gtmUpdate);

    List<AfYibaoOrderDo> getYiBaoUnFinishOrderAll();
}
