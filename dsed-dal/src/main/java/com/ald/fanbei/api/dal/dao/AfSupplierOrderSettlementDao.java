
package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfSupplierOrderSettlementDo;
import org.apache.ibatis.annotations.Param;


/**
 *
 * @类描述：自营商城 订单结算
 * @author weiqingeng 2017年12月12日下午10:22:55
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSupplierOrderSettlementDao {

	void updateBatchOrderSettlementStatus(AfSupplierOrderSettlementDo afSupplierOrderSettlementDo);//批量修改结算订单 结算状态

	Integer updateSettlementOrderPayStatus(AfSupplierOrderSettlementDo afSupplierOrderSettlementDo);//修改结算单打款状态

	Integer updateSettlementOrderPayStatusBySettleNo(AfSupplierOrderSettlementDo afSupplierOrderSettlementDo);//修改结算单打款状态

	String selectSettlementNoById(Long rid);//根据id查找结算单

	Long selectSettlementNoBySettleNo(String settleNo);//根据settleNo查找结算单

    int updateStatusByOrderId(@Param("rid") Long rid);
}
