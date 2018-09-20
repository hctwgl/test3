package com.ald.jsd.mgr.dal.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.JsdCollectionRepaymentDo;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;

/**
 * 极速贷管理后台专用Dao
 *
 * @author ZJF
 * @version 1.0.0 初始化
 * @date 2018-08-22 16:18:06jsdBorrowCashDao
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrCollectionRepaymentDao extends BaseDao<JsdCollectionRepaymentDo, Long> {

	List<JsdCollectionRepaymentDo> listCollectionRepayment(MgrCommonQuery<?> query);

	Long countTotalAmtBetweenGmtCreate(@Param("dateStart") Date dateStart, @Param("dateEnd") Date dateEnd);
	Long countTotalWaitReview();
}
