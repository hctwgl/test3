package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfBankcardConfigDo;
import com.ald.fanbei.api.dal.domain.dto.BankCardInfoDto;
import org.apache.ibatis.annotations.Param;

/**
 * 信用卡绑定及订单支付Dao
 * 
 * @author gaojibin
 * @version 1.0.0 初始化
 * @date 2018-05-09 10:01:47
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBankcardConfigDao extends BaseDao<AfBankcardConfigDo, Long> {

    BankCardInfoDto getByCardNo(@Param("cardNo") String cardNo);

}
