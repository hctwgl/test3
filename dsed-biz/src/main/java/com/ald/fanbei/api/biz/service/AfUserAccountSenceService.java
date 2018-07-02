package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.List;

import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.alibaba.druid.support.logging.Log;

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

	BigDecimal getTotalAmount(Long userId);

	BigDecimal getBldUsedAmount(Long userId);


	/** -------- start 借贷额度 ---------- */
	/**
	 * "借贷"-包含小额贷,白领贷等等所有借款产品
	 * 借贷额度自检, 审核目标金额是否可以放行
	 *
	 * @param userId
	 * @param scene
	 * @param amount 借贷目标额
	 */
	void checkLoanQuota(Long userId, SceneType scene, BigDecimal amount) throws FanbeiException;

	/**
	 * 根据场景同步 借贷使用额
	 * 注意! 应在事务区中调用此函数
	 *
	 * @param userId
	 * @param scene
	 * @param amount 修改额
	 */
	void syncLoanUsedAmount(Long userId, SceneType scene, BigDecimal amount);

	/**
	 * 根据场景 获取 实际可借最大额
	 *
	 * @param userId
	 * @param scene
	 * @param cfgAmount 运营配置的最大借款额
	 * @return
	 */
	BigDecimal getLoanMaxPermitQuota(Long userId, SceneType scene, BigDecimal cfgAmount);

	/**
	 * 获取用户 总 可用额度
	 *
	 * @param userId
	 * @param scene
	 * @param cfgAmount 运营配置的最大借款额
	 * @return
	 */
	BigDecimal getTotalUsableAmount(AfUserAccountDo userAccount, AfUserAccountSenceDo... scenes);
	BigDecimal getTotalUsableAmount(AfUserAccountDo userAccount);
	
	/**
	 * 提额,修改目标场景额度为tarAmount
	 *
	 * @param userId
	 * @param scene
	 * @param amount
	 */
	void raiseQuota(Long userId, SceneType scene, BigDecimal tarAmount, BigDecimal totalAmount);
	/** -------- end 借贷额度 ---------- */

    int updateUserSceneAuAmountByScene(String scene, Long userId, BigDecimal auAmount);
    
    /**
     * 初始化用户的 TOTAL_LOAN 记录
     * @param accInfo
     * @return
     */
    AfUserAccountSenceDo initTotalLoan(AfUserAccountDo accInfo);
    AfUserAccountSenceDo initTotalLoanSelection(AfUserAccountDo accInfo);
    
    /**
     * 线上分期提额
     * @param userId
     * @param scene
     * @param riskScene
     * @param riskSceneType
     * @param authType
     */
    void raiseOnlineQuato(Long userId, String scene, String riskScene, String riskSceneType, String authType);
    void raiseOnlineQuatoForBuddle(Long userId, String scene, String riskScene, String riskSceneType, String authType);
}
