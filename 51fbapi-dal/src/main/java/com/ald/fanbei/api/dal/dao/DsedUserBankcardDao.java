package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * 都市E贷用户绑定的银行卡Dao
 *
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedUserBankcardDao extends BaseDao<DsedUserBankcardDo, Long> {
    /**
     * 获取用户所选银行卡对应的支付方式(代扣WITHHOLD、快捷支付KUAIJIE(有短信))
     * @param userId
     * @param cardNumber
     * author: chefeipeng
     * @return
     */
    HashMap<String,Object> getPayTypeByBankNoAndUserId(@Param("userId")Long userId, @Param("cardNumber")String cardNumber);

    /**
     * 获取用户主卡信息
     *
     * @param userId
     * @return
     */
    DsedUserBankcardDo getUserMainBankcardByUserId(@Param("userId") Long userId);

    /**
     * 获取银行身份信息
     */
    HashMap<String,Object> getUserBankInfo(@Param("bankNo")String bankNo);


    /**
     * 查询银行卡是否已被人绑定
     * @return
     */
    int getUserBankByCardNo(@Param("cardNumber")String cardNumber);


    /**
     * 查询银行卡信息
     * @param userId
     * @return
     */
    List<DsedUserBankcardDo> getUserBankCardInfoByUserId(Long userId);

    /**
     * 增加记录
     * @param userBankcardDo
     * @return
     */
    int addUserBankcard(DsedUserBankcardDo userBankcardDo);
    /**
     * 查询银行卡信息
     * @param cardNumber
     * @return
     */
    DsedUserBankcardDo getUserBankCardByCardNo(@Param("cardNumber")String cardNumber);

    /**
     * 更新卡状态为绑定
     * @param userBankcardDo
     * @return
     */
    int updateUserBankcard(DsedUserBankcardDo userBankcardDo);

}
