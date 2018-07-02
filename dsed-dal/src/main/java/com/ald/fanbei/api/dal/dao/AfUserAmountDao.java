package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAmountDetailDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountDo;
import com.ald.fanbei.api.dal.domain.AfUserAmountLogDo;
import com.ald.fanbei.api.dal.domain.dto.AfBorrowDto;
import com.ald.fanbei.api.dal.domain.query.AfUserAmountQuery;

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

    int updateUserAmountStatus(AfUserAmountDo afUserAmountDo);

    /**
     * 根据userId和type查询
     * @author yuyue
     * @Time 2017年11月28日 上午11:03:05
     * @param userId
     * @param type
     * @param pageSize 
     * @param begin 
     * @return
     */
    List<AfUserAmountDo> getAmountByUserIdAndType(@Param("userId")Long userId, @Param("type")int type, @Param("begin")int begin, @Param("pageSize")int pageSize);

    /**
     * 根据amountId查询退还款明细
     * @author yuyue
     * @Time 2017年11月28日 下午5:36:59
     * @param amountId
     * @return
     */
	List<AfUserAmountDetailDo> getAmountDetailByAmountId(@Param("amountId")Long amountId);

	/**
	 * 根据amountId查询退还款明细
	 * @author yuyue
	 * @Time 2017年11月28日 下午7:50:46
	 * @param amountId
	 * @return
	 */
	BigDecimal getRenfundAmountByAmountId(@Param("amountId")Long amountId);

	/**
	 * 根据ID查询
	 * @author yuyue
	 * @Time 2017年11月28日 下午8:08:15
	 * @param amountId
	 * @return
	 */
	AfUserAmountDo getUserAmountById(@Param("id")Long amountId);

	/**
	 * 根据amountId查询borrow的拓展信息--退款详情
	 * @author yuyue
	 * @Time 2017年11月29日 下午3:47:42
	 * @param amountId
	 * @return
	 */
	AfBorrowDto getBorrowDtoByAmountId(@Param("amountId")Long amountId);

	/**
	 * 根据条件查询amount
	 * @author yuyue
	 * @Time 2017年11月30日 下午3:33:32
	 * @param query
	 * @return
	 */
	List<AfUserAmountDo> getUserAmountByQuery(AfUserAmountQuery query);

	/**
	 * 获取有记录月数
	 * @author yuyue
	 * @Time 2017年12月1日 下午1:41:46
	 * @param query
	 * @return
	 */
	List<String> getMonthInYearByQuery(AfUserAmountQuery query);

	/**
	 * 计算实际还款金额（自行支付+余额抵扣+优惠卷抵扣）
	 * @author yuyue
	 * @Time 2018年1月2日 下午1:15:33
	 * @param amountId
	 * @return
	 */
	BigDecimal getRepaymentAmountByAmountId(@Param("amountId")Long amountId);

}
