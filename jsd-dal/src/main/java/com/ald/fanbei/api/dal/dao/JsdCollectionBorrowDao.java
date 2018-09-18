package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.jsd.mgr.dal.domain.dto.MgrCollectionBorrowDto;
import com.ald.jsd.mgr.dal.query.MgrCommonQuery;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-16 11:51:40
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdCollectionBorrowDao extends BaseDao<JsdCollectionBorrowDo, Long> {

	List<MgrCollectionBorrowDto> listMgrCollectionBorrow(MgrCommonQuery<?> query);

}
