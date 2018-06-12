package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;

/**
 * @类描述：
 * 
 * @author suweili 2017年3月24日下午5:02:52
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowCashService {
	/**
	 * 增加记录
	 * 
	 * @param afBorrowCashDo
	 * @return
	 */
	int addBorrowCash(AfBorrowCashDo afBorrowCashDo);


	int borrowSuccess(final AfBorrowCashDo afBorrowCashDo);
	
	void borrowFail(final Long borrowId, String tradeNoOut, String msgOut);

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
	AfBorrowCashDo getBorrowCashByUserId(Long userId);

	AfBorrowCashDo getDealingCashByUserId(Long userId);

	/**
	 * 借钱信息
	 * 
	 * @param userId
	 * @param 开始条数
	 * @return
	 */
	List<AfBorrowCashDo> getBorrowCashListByUserId(Long userId, Integer start);

	/**
	 * 根据rid获取借款信息
	 * 
	 * @param rid
	 * @return
	 */
	AfBorrowCashDo getBorrowCashByrid(Long rid);

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
	AfBorrowCashDo getBorrowCashByRishOrderNo(String rishOrderNo);

	/**
	 * 获取用户当天最后一条申请记录
	 * 
	 * @param userId
	 * @return
	 */
	AfBorrowCashDo getUserDayLastBorrowCash(Long userId);

	/**
	 * 获取某个用户某个时间后，风控拒绝的借款申请数量
	 * 
	 * @param userId
	 * @param reviewStatus
	 * @param startTime
	 * @return
	 */
	Integer getSpecBorrowCashNums(Long userId,String reviewStatus,Date startTime);
	
	/**
	 * 获取总借款次数
	 * @param userId
	 * @return
	 */
	int getBorrowNumByUserId(Long userId);

	/**
	 * 是否可借款
	 * 
	 * @param userId
	 * @return
	 */
	boolean isCanBorrowCash(Long userId);
	
	/**
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
	AfBorrowCashDo getBorrowCashInfoByBorrowNo(String borrowNo);

	List<AfBorrowCashDo> getRiskRefuseBorrowCash(Long userId, Date gmtStart, Date gmtEnd);
	
	/**
	 * 获取借过款的用户id集合，放入缓存，用于给没借过钱的用户app端高亮显示
	 * @return
	 */
	List<String> getBorrowedUserIds();

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
	int getCurrDayTransFailTimes(Long userId);
	
	/**
	 * 更新续借前的逾期状态
	 * @param borrowId
	 * @return
	 * **/
	int updateAfBorrowCashService(AfBorrowCashDo afBorrowCashDo);

	/**
	 * 更新au_amount
	 *  @param borrowId,auAmount
	 *  @return
	 * **/
	int updateAuAmountByRid(long rid,BigDecimal auAmount);

    int updateBorrowCashLock(Long borrowId);

    int updateBorrowCashUnLock(Long borrowId);

	AfBorrowCashDo getBorrowCashByStatus(Long userId);

	int updateAfBorrowCashPlanTime(Long userId);
	
	/**
	 * 宜信查询
	 * */
	List<AfBorrowCashDo> getListByUserId(Long userId, Long rows);


	int borrowSuccessForNew(AfBorrowCashDo afBorrowCashDo);


	AfBorrowCashDo getBorrowCashByUserIdDescById(Long userId);


	int getCashBorrowSuccessByUserId(Long userId, String activityTime);

	
	AfBorrowCashDo getBorrowCashInfoByBorrowNoV1(String borrowNo);
	int getCashBorrowByUserIdAndActivity(Long userId, String activityTime);
	/**
	 * 计算剩余应还的金额
	 * @param cashDo
	 * @param orderCashDo
	 * @return
	 */
	BigDecimal calculateLegalRestAmount(AfBorrowCashDo cashDo);

	/**
	 * 计算违约金额
	 * @param cashDo
	 * @return
	 */
	BigDecimal calculateLegalRestOverdue(AfBorrowCashDo cashDo);


	/**
	 * 检查当前用户是否有进行中的借款
	 * @param userId
	 * @return
	 */
	boolean haveDealingBorrowCash(Long userId);
}
