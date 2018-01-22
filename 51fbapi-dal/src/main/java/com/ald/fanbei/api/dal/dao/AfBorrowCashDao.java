
package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserBorrowCashOverdueInfoDto;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午4:02:23
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashDao {
	/**
	 * 增加记录
	 * 
	 * @param afBorrowCashDo
	 * @return
	 */
	int addBorrowCash(AfBorrowCashDo afBorrowCashDo);

	/**
	 * 更新记录
	 * 
	 * @param afBorrowCashDo
	 * @return
	 */
	int updateBorrowCash(AfBorrowCashDo afBorrowCashDo);

	/**
	 * 获取最近一次借钱信息
	 * 
	 * @param userId
	 * @return
	 */
	AfBorrowCashDo getBorrowCashByUserId(@Param("userId") Long userId);

	/**
	 * 借钱信息
	 * 
	 * @param userId
	 * @param 开始条数
	 * @return
	 */
	List<AfBorrowCashDo> getBorrowCashListByUserId(@Param("userId") Long userId, @Param("start") Integer start);

	/**
	 * 根据rid获取借款信息
	 * 
	 * @param rid
	 * @return
	 */
	AfBorrowCashDo getBorrowCashByrid(@Param("rid") Long rid);

	/**
	 * 获取当前最大的借款编号
	 * 
	 * @param orderNoPre
	 * @return
	 */
	String getCurrentLastBorrowNo(String orderNoPre);

	/**
	 * 根据rishOrderNo获取借款信息
	 * 
	 * @param rishOrderNo
	 * @return
	 */
	public AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo);

	/**
	 * 获取某个时间段用户的最后一条借款
	 * 
	 * @param userId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	AfBorrowCashDo getUserDayLastBorrowCash(@Param("userId") Long userId, @Param("startTime") Date startTime,
			@Param("endTime") Date endTime);

	/**
	 * 获取某个用户某个时间后，风控拒绝的借款申请数量 获取总借款次数
	 * 
	 * @param userId
	 * @param reviewStatus
	 * @param startTime
	 * @return
	 */
	Integer getSpecBorrowCashNums(@Param("userId") Long userId, @Param("reviewStatus") String reviewStatus,
			@Param("startTime") Date startTime);

	/**
	 * 获取总借款次数
	 * 
	 * @param userId
	 * @return
	 */
	int getBorrowNumByUserId(@Param("userId") Long userId);

	/**
	 * 获取不在finish和closed状态的借款
	 * 
	 * @param userId
	 * 
	 * @return
	 */
	List<AfBorrowCashDo> getBorrowCashByStatusNotInFinshAndClosed(@Param("userId") Long userId);

	/***
	 * 获取最久一次在已打款借钱信息
	 * 
	 * @param userId
	 * @return
	 */
	AfBorrowCashDo getNowTransedBorrowCashByUserId(Long userId);
	
	/***
	 * 获取最近一次没有完成的打款借钱信息
	 * 
	 * @param userId
	 * @return
	 */
	AfBorrowCashDo getNowUnfinishedBorrowCashByUserId(Long userId);
	
	/***
	 * 根据借钱编号获取借钱账号
	 * @param userId
	 * @return
	 */
	AfBorrowCashDo getBorrowCashInfoByBorrowNo(@Param("borrowNo")String borrowNo);

	List<AfBorrowCashDo> getRiskRefuseBorrowCash(@Param("userId")Long userId, @Param("gmtStart")Date gmtStart, @Param("gmtEnd")Date gmtEnd);

	/**
	 * 获取借过款的用户id集合，放入缓存，用于给没借过钱的用户app端高亮显示
	 * @return
	 */
	List<String> getBorrowedUserIds();

	HashMap getBorrowCashByRemcommend(@Param("user_id")long user_id);

	/**
	 * 催收平账更新数据
	 * @param
	 * @return
	 *
	 * **/
	int updateBalancedDate(AfBorrowCashDo afBorrowCashDo);

	/**
	 * 借钱活动
	 *
	 * @return
	 * **/
	BigDecimal getBorrowCashSumAmount();

	/**
	 * 获取随机用户
	 *
	 * @return
	 * **/
	List<String> getRandomUser();

	/**
	 * 获取未中奖用户id
	 * @param userId
	 * @return
	 * **/
	List<String> getNotRandomUser(List<String> userId);
	
	/**
	 * 获取当前用户当日打款失败次数
	 * @param userId
	 * @return
	 */
	int getCurrDayTransFailTimes(@Param("userId")long userId);
	
	/**
	 * 更新续借前的逾期状态
	 * @param borrowId
	 * @return
	 * **/
	int updateAfBorrowCashService(AfBorrowCashDo afBorrowCashDo);

	AfBorrowCashDo getBorrowCash(@Param("userId") long userId);

	/**
	 * 更新au_amount
	 *  @param borrowId,auAmount
	 *  @return
	 * **/
	int updateAuAmountByRid(@Param("rid")long rid, @Param("auAmount")BigDecimal auAmount);

    int updateBorrowCashLock(@Param("borrowId") long borrowId);

	int updateBorrowCashUnLock(@Param("borrowId")Long borrowId);

    AfBorrowCashDo getBorrowCashByStatus(@Param("userId")Long userId);

	int updateAfBorrowCashPlanTime(@Param("userId")Long userId);
	/**
	 * 获取最近一次借钱信息
	 * 
	 * @param userId
	 * @return
	 */
	AfUserBorrowCashOverdueInfoDto getOverdueInfoByUserId(@Param("userId") Long userId);
	
	/**
	 * 宜信查询
	 * */
	List<AfBorrowCashDo> getListByUserId(@Param("userId")Long userId, @Param("rows")Long rows);

	AfBorrowCashDo getBorrowCashByUserIdDescById(@Param("userId")Long userId);
	
	AfBorrowCashDo getBorrowCashInfoByBorrowNoV1(@Param("borrowNo")String borrowNo);
	
	AfBorrowCashDo fetchLastByUserId(@Param("userId")Long userId);
}
