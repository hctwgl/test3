package com.ald.fanbei.api.biz.service;

import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.service.impl.AfBorrowRecycleServiceImpl.BorrowRecycleHomeInfoBo;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfBorrowRecycleOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;

/**
 * 回收业务中未归类的函数写入此类
 * 
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2018-04-28 14:08:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowRecycleService extends ParentService<AfBorrowCashDo, Long> {
	
	/**
	 * 获取回收首页信息
	 * @param userId
	 * @return
	 */
	BorrowRecycleHomeInfoBo getRecycleInfo(Long userId, Map<String,String> params);

	/**
	 * 获取回收记录
	 * 
	 * @param userId
	 * @param start
	 * @return
	 */
	List<BorrowRecycleHomeInfoBo> getRecycleRecord(Long userId);

	BorrowRecycleHomeInfoBo getRecycleRecordByBorrowId(Long borrowId);

	/**
	 * 判断当前借款是否是回收借款
	 * @return
	 */
	boolean isRecycleBorrow(Long borrowId);
	
	/**
	 * 添加回收借款记录
	 * @param afBorrowCashDo
	 * @param recycleOrderDo
	 * @return
	 */
	Long addBorrowRecord( AfBorrowCashDo afBorrowCashDo, AfBorrowRecycleOrderDo recycleOrderDo);

	BorrowRecycleHomeInfoBo getRejectCodeAndAction(AfUserAccountDo userAccount);
	
}
