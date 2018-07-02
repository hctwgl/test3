package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.common.enums.BankCardType;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.AfUserBankDto;
import com.ald.fanbei.api.dal.domain.dto.UpsBankStatusDto;

import java.math.BigDecimal;
import java.util.List;

/**
 * @类现描述：
 * @author hexin 2017年2月18日 下午17:23:47
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserBankcardService {

    /**
     * 获取用户主卡信息
     * 
     * @param userId
     * @return
     */
    AfUserBankcardDo getUserMainBankcardByUserId(Long userId);

    /**
     * 获取银行卡列表
     * 
     * @param userId
     * @return
     */
    List<AfBankUserBankDto> getUserBankcardByUserId(Long userId, Integer appVersion,String cardType);

    /**
     * 删除银行卡
     * 
     * @param userId
     * @param rid
     * @return
     */
    int deleteUserBankcardByIdAndUserId(Long userId, Long rid);

    /**
     * 获取用户银行卡信息
     * 
     * @param userId
     * @return
     */
    AfUserBankcardDo getUserBankcardById(Long id);

    /**
     * 修改银行卡信息
     * 
     * @param afUserBankcardDo
     * @return
     */
    int updateUserBankcard(AfUserBankcardDo afUserBankcardDo);

    /**
     * 获取银行卡个数
     * 
     * @param userId
     * @return
     */
    int getUserBankcardCountByUserId(Long userId);

    /**
     * 获取银行身份信息
     */
    AfUserBankDto getUserBankInfo(Long bankId);

    /**
     * 获取银行卡列表
     * 
     * **/

    List<AfUserBankcardDo> getAfUserBankcardDoList(long userId);

    /**
     * 根据银行卡号获取ID
     * **/
    AfUserBankcardDo getUserBankcardIdByCardNumber(String cardNumber);

    /**
     * 获取银行卡cardNumber ,cardId
     *
     * **/

    String getAfUserBankcardList(long userId);

    AfUserBankcardDo getUserBankcardByIdAndStatus(Long cardId);

    /**
     * 加工银行卡号,只显示尾号
     * 
     * @param bankcard
     * @return
     */
    String hideCardNumber(String bankcard);

    int updateMainBankCard(Long userId);

    int updateViceBankCard(String cardNumber, Long userId);

    UpsBankStatusDto getUpsBankStatus(String bankCode, String bankChannel);

    //UpsBankStatusDto getUpsBankStatus(Long cardId);
    
    void checkUpsBankLimit(String bankCode, String bankChannel, BigDecimal amount);
}
