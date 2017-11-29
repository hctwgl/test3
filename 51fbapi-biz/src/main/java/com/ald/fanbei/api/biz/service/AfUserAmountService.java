package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.common.enums.AfUserAmountProcessStatus;
import com.ald.fanbei.api.dal.domain.AfRepaymentDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;

/**
 * @author honghzengpei 2017/11/22 14:28
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAmountService {
    void addUseAmountDetail(AfRepaymentDo afRepaymentDo);
    void addUserAmountLog(AfRepaymentDo afRepaymentDo,AfUserAmountProcessStatus afUserAmountProcessStatus);
    void updateUserAmount(AfUserAmountProcessStatus afUserAmountProcessStatus,AfRepaymentDo afRepaymentDo);

    int refundOrder(long orderId);
    
    /**
     * 根据userId和type查询
     * @author yuyue
     * @Time 2017年11月28日 上午11:01:05
     * @param userId
     * @param pageSize 
     * @param page 
     * @param code
     * @return
     */
    List<AfUserAmountDo> getAmountByUserIdAndType(Long userId, int type, int page, int pageSize);
    
    /**
     * 根据amountId查询退还款明细
     * @author yuyue
     * @Time 2017年11月28日 下午5:32:29
     * @param amountId
     * @return
     */
	List<AfUserAmountDetailDo> getAmountDetailByAmountId(Long amountId);
	
	/**
	 * 获取退还款金额
	 * @author yuyue
	 * @Time 2017年11月28日 下午7:22:41
	 * @param amountId
	 * @return
	 */
	BigDecimal getRenfundAmountByAmountId(Long amountId);
	
	/**
	 * 根据id查询
	 * @author yuyue
	 * @Time 2017年11月28日 下午8:07:27
	 * @param amountId
	 * @return
	 */
	AfUserAmountDo getUserAmountById(Long amountId);
}
