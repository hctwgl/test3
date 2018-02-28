package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;

/**
 * 额度拆分多场景分期额度记录Service
 * 
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:57:51 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAccountSenceService extends ParentService<AfUserAccountSenceDo, Long> {

    int updateUserSceneAuAmount(String scene, Long userId, BigDecimal auAmount);

    AfUserAccountSenceDo getByUserIdAndType(String scene, Long userId);

    /**
     * 通过订单ID查询商圈类别code
     * @param orderId
     * @return
     */
    String getBusinessTypeByOrderId(Long orderId);

    AfUserAccountSenceDo getByUserIdAndScene(String scene, Long userId);

    List<AfUserAccountSenceDo> getByUserId(Long userId);
    
    BigDecimal getAuAmountByScene(String scene, Long userId);
    
    AfUserAccountSenceDo buildAccountScene(Long userId, String loanType, String amount);

	void saveOrUpdateAccountSence(AfUserAccountSenceDo bldAccountSenceDo);
}
