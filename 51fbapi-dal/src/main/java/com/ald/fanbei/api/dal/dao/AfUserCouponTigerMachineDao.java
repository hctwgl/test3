package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserCouponTigerMachineDo;

/**
 * 老虎机用户领券次数Dao
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-01-05 16:20:41
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserCouponTigerMachineDao extends BaseDao<AfUserCouponTigerMachineDo, Long> {

	int getTotalTimesByUserId(@Param("userId")Long userId);

	int decreaseOnce(@Param("userId")Long userId);

    

}
