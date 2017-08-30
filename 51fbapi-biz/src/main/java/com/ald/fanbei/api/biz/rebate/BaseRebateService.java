package com.ald.fanbei.api.biz.rebate;

import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountLogDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountLogDo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseRebateService {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    AfUserAccountDao afUserAccountDao;
    @Resource
    AfUserAccountLogDao afUserAccountLogDao;
    @Resource
    SmsUtil smsUtil;
    /**
     * 是否可以进行返利的前置数据验证
     *
     * @return
     */
    protected boolean valid(AfOrderDo afOrderDo) {
        if (afOrderDo == null) {
            logger.error("404,can not found this order!");
            return false;
        }
        //只有那几种状态能进行返利,基础验证
        if (
                afOrderDo.getStatus().equals(OrderStatus.CLOSED) ||
                        afOrderDo.getStatus().equals(OrderStatus.AGENCYCOMPLETED) ||
                        afOrderDo.getStatus().equals(OrderStatus.DEAL_REFUNDING) ||
                        afOrderDo.getStatus().equals(OrderStatus.DEALING) ||
                        afOrderDo.getStatus().equals(OrderStatus.NEW) ||
                        afOrderDo.getStatus().equals(OrderStatus.PAYFAIL) ||
                        afOrderDo.getStatus().equals(OrderStatus.REBATED) ||
                        afOrderDo.getStatus().equals(OrderStatus.REVIEW) ||
                        afOrderDo.getStatus().equals(OrderStatus.WAITING_REFUND) ||
                        afOrderDo.getStatus().equals(OrderStatus.NEW)) {
            logger.error("invalid order state:"+ afOrderDo.getStatus());
            return false;
        }
        return true;
    }

    /**
     * 执行返利操作
     *
     * @return
     */
    protected abstract boolean rebate(AfOrderDo afOrderDo);

    /**
     * 取消返利 执行返利逆向操作
     *
     * @return
     */
    protected abstract boolean reverse(AfOrderDo afOrderDo);

    /**
     * 取消返利 执行返利逆向操作
     *
     * @return
     */
    protected  boolean addRebateAmount(BigDecimal rebateAmount,AfOrderDo orderInfo){
        //用户账户操作
        AfUserAccountDo accountInfo = afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId());
        accountInfo.setRebateAmount(rebateAmount.add(accountInfo.getRebateAmount()));

        AfUserAccountLogDo accountLog = new AfUserAccountLogDo();
        accountLog.setRefId(orderInfo.getRid() + StringUtils.EMPTY);
        accountLog.setUserId(orderInfo.getUserId());
        accountLog.setType(UserAccountLogType.REBATE_CASH.getCode());
        accountLog.setAmount(orderInfo.getRebateAmount());
        //插入账户日志
        afUserAccountLogDao.addUserAccountLog(accountLog);
        //修改账户表
        afUserAccountDao.updateRebateAmount(accountInfo);
       //返利已经到账通知
        smsUtil.sendRebate(accountInfo.getUserName(), new Date(),orderInfo.getRebateAmount());

        return true;
    }
}
