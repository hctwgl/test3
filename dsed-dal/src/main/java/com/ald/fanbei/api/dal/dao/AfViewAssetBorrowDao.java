package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfViewAssetBorrowDo;
import com.ald.fanbei.api.dal.domain.query.AfViewAssetBorrowQuery;

/**
 * 资产方消费分期债权视图Dao
 * 
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017-12-14 16:59:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfViewAssetBorrowDao extends BaseDao<AfViewAssetBorrowDo, Long> {
	
	BigDecimal getSumAmount(@Param("gmtCreateStart")Date gmtCreateStart,@Param("gmtCreateEnd") Date gmtCreateEnd);

	List<AfViewAssetBorrowDo> getListByQueryCondition(AfViewAssetBorrowQuery query);

	BigDecimal checkAmount(AfViewAssetBorrowQuery query);

	AfViewAssetBorrowDo getByQueryCondition(AfViewAssetBorrowQuery query);

}
