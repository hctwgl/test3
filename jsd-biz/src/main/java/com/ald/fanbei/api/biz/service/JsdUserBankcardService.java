package com.ald.fanbei.api.biz.service;

import java.util.HashMap;
import java.util.List;

import com.ald.fanbei.api.dal.domain.JsdUserBankcardDo;

/**
 * 极速贷用户银行卡信息Service
 * 
 * @author yanghailong
 * @version 1.0.0 初始化
 * @date 2018-08-23 09:40:59
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface JsdUserBankcardService extends ParentService<JsdUserBankcardDo, Long>{

	JsdUserBankcardDo getByBankNo(String bankNo,Long userId);

    HashMap<String,Object> getBankByBankNoAndUserId(Long userId,String bankNo);

    List<JsdUserBankcardDo> getUserBankCardInfoByUserId(Long userId);

    int addUserBankcard(JsdUserBankcardDo userBankcardDo);

    int updateUserBankcard(JsdUserBankcardDo userBankcardDo);

    int getUserBankByCardNo(String cardNumber,Long userId);

    JsdUserBankcardDo getByBindNo(String bindNO);


    JsdUserBankcardDo getMainBankByUserId(Long userId);

    List<JsdUserBankcardDo> getUserNoMainBankCardInfoByUserId(Long userId);



}
