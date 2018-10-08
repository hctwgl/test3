package com.ald.jsd.mgr.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderInfoDo;


/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2018-09-19 14:09:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface MgrBorrowLegalOrderInfoDao {

    JsdBorrowLegalOrderInfoDo getByBorrowId(@Param("borrowId") Long borrowId);

    List<JsdBorrowLegalOrderInfoDo> getInfoByDays(Integer days);



}
