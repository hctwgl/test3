package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdOfflineOverdueRemoveDo;
import com.ald.fanbei.api.dal.domain.JsdTotalInfoDo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * Dao
 * 
 * @author CodeGenerate
 * @version 1.0.0 初始化
 * @date 2019-01-03 13:49:13
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdTotalInfoDao extends BaseDao<JsdTotalInfoDo, Long> {

    int saveAll(@Param("list") List<JsdTotalInfoDo> list);
    void batchDelete(Date date);


}
