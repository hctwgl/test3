package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.dal.domain.DsedUserBankcardDo;

/**
 * 都市E贷用户绑定的银行卡Service
 *
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:50
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedUserBankcardService extends ParentService<DsedUserBankcardDo, Long> {

    /**
     * 获取用户所选银行卡对应的支付方式(代扣WITHHOLD、快捷支付KUAIJIE(有短信))
     *
     * @param userId
     * @param cardNumber author: chefeipeng
     * @return
     */
    HashMap<String, Object> getPayTypeByBankNoAndUserId(Long userId, String cardNumber);

    /**
     * 加工银行卡号,只显示尾号
     *
     * @param bankcard
     * @return
     */
    String hideCardNumber(String bankcard);

    int getUserBankByCardNo(String cardNumber);

    List<DsedUserBankcardDo> getUserBankCardInfoByUserId(Long userId);

    int addUserBankcard(DsedUserBankcardDo userBankcardDo);

    int  updateUserBankcard(DsedUserBankcardDo userBankcardDo);

    void checkUpsBankLimit(String bankCode, String bankChannel, BigDecimal amount);

}
