package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.JsdOfflineOverdueRemoveDo;
import org.apache.ibatis.annotations.Param;

/**
 * Dao
 * 
 * @author yinxiangyu
 * @version 1.0.0 初始化
 * @date 2018-10-13 17:46:16
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdOfflineOverdueRemoveDao extends BaseDao<JsdOfflineOverdueRemoveDo, Long> {

    JsdOfflineOverdueRemoveDo getInfoByoverdueLogId(@Param("overdueLogId") String overdueLogId);

}
