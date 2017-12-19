package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuLogDo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @类描述：临时提额活动服务类
 * @author caowu 2017年11月17日下午4:03:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfInterimAuService {
    /**
     * 查询用户是否已经提额过
     */
    int selectExistAuByUserId(Long userId);
    /**
     * 查询提额的额度
     */
    BigDecimal selectAuByUserId(Long userId);

    /**
     * 查询申请失败记录
     */
    List<AfInterimAuDo>selectApplyFailByUserId(Long userId);

    /**
     * 插入申请记录
     */
    int insertInterimAmountAndLog(boolean isSuccess, Long userId, BigDecimal interimAmount, Date date);

    /**
     * 根据userId获取AfInterimAuDo
     * @param userId
     * @return
     */
    AfInterimAuDo getAfInterimAuByUserId(Long userId);
    /**
     * 查询临时额度ByUserId
     */
    AfInterimAuDo getByUserId(Long userId);

    /**
     * 获取最后一次提升临时额度记录
     */
    AfInterimAuLogDo getLastLogByUserId(Long userId);
}
