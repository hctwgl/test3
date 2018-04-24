package com.ald.fanbei.api.dal.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录Dao
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountSenceDao extends BaseDao<AfUserAccountSenceDo, Long> {

    int updateUserSceneAuAmount(@Param("scene") String scene, @Param("userId") Long userId, @Param("auAmount") BigDecimal auAmount);

    AfUserAccountSenceDo getByUserIdAndType(@Param("scene") String scene, @Param("userId") Long userId);

    /**
     * 通过订单ID查询商圈类别code
     * @param orderId
     * @return
     */
    String getBusinessTypeByOrderId(@Param("orderId") Long orderId);

    AfUserAccountSenceDo getByUserIdAndScene(@Param("scene") String scene, @Param("userId") Long userId);

    int updateUsedAmount(@Param("scene") String scene, @Param("userId") Long userId, @Param("usedAmount") BigDecimal usedAmount);

    int updateFreezeAmount(@Param("scene") String scene, @Param("userId") Long userId, @Param("freezeAmount") BigDecimal freezeAmount);

    List<AfUserAccountSenceDo> getByUserId(@Param("userId") Long userId);
    
    int updateTrainInitUsedAmount(@Param("userId") Long userId);
    int updateOnlineInitUsedAmountByBills(@Param("userId") Long userId);
    int updateOnlineInitUsedAmountByOrderAp(@Param("userId") Long userId);
    int updateOnlineInitUsedAmountByOrderCp(@Param("userId") Long userId);
    

    int updateUserSceneAuAmountByScene(@Param("scene") String scene, @Param("userId") Long userId, @Param("auAmount") BigDecimal auAmount);
}
