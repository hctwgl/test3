package com.ald.fanbei.api.biz.service;

import java.math.BigDecimal;
import java.util.Date;

import com.ald.fanbei.api.dal.domain.AfAuthRaiseStatusDo;

/**
 * 贷款业务Service
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2018-02-06 17:58:14 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfAuthRaiseStatusService extends ParentService<AfAuthRaiseStatusDo, Long> {

    public void saveOrUpdateRaiseStatus(AfAuthRaiseStatusDo bldRaiseStatus);

    AfAuthRaiseStatusDo buildAuthRaiseStatusDo(Long userId, String authType, String prdType, String raiseStatus, BigDecimal amount, Date gmtFinish);

    void initRaiseStatus(Long userId, String authType);

    public void initCreditRaiseStatus(Long userId, String authType);

    public void initZhengxinRaiseStatus(Long userId, String authType);

    public void initOnlinebankRaiseStatus(Long userId, String authType);

    AfAuthRaiseStatusDo getByPrdTypeAndAuthType(String prdType, String authType, Long userId);

}
