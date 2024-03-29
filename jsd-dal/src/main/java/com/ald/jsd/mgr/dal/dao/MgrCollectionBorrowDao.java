package com.ald.jsd.mgr.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;

/**
 * 极速贷Dao
 *
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06jsdBorrowCashDao
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrCollectionBorrowDao {

	List<MgrCollectionBorrowDto> listCollectionBorrow(MgrCommonQuery<?> query);
	
	Long countTotalAmtBetweenGmtCreate(@Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	Long countTotalWaitFinish();

}
