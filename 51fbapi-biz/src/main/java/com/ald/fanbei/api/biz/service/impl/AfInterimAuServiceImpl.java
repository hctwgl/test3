package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfInterimAuService;
import com.ald.fanbei.api.dal.dao.AfInterimAuDao;
import com.ald.fanbei.api.dal.domain.AfInterimAuDo;
import com.ald.fanbei.api.dal.domain.AfInterimAuLogDo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @类描述：临时提额活动服务
 * @author caowu 2017年11月17日下午4:03:04
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("AfInterimAuService")
public class AfInterimAuServiceImpl implements AfInterimAuService {
    @Resource
    AfInterimAuDao afInterimAuDao;

    @Resource
    TransactionTemplate transactionTemplate;

    @Override
    public int selectExistAuByUserId(Long userId) {
        return afInterimAuDao.selectExistAuByUserId(userId);
    }

    @Override
    public BigDecimal selectAuByUserId(Long userId) {
        return afInterimAuDao.selectAuByUserId(userId);
    }

    @Override
    public List<AfInterimAuDo> selectApplyFailByUserId(Long userId) {
        return afInterimAuDao.selectApplyFailByUserId(userId);
    }

    @Override
    public int insertInterimAmountAndLog(boolean isSuccess, final Long userId, final BigDecimal interimAmount, final Date date) {
        final int[] returnValue = {0};
        if(isSuccess){
            //查询是否有失效记录 有更新记录
            if(afInterimAuDao.getFailureCount(userId)>0){
                transactionTemplate.execute(new TransactionCallback<Long>() {
                    @Override
                    public Long doInTransaction(TransactionStatus transactionStatus) {
                        if(afInterimAuDao.updateInterimAmount(userId,interimAmount,date)>0){
                            returnValue[0]  = afInterimAuDao.insertInterimAmountLog(userId,interimAmount,date,0);//插入日志
                        }
                        return 1L;
                    }
                });
            }else{
                transactionTemplate.execute(new TransactionCallback<Long>() {
                    @Override
                    public Long doInTransaction(TransactionStatus transactionStatus) {
                        if(afInterimAuDao.insertInterimAmount(userId,interimAmount,date)>0){
                            returnValue[0]  = afInterimAuDao.insertInterimAmountLog(userId,interimAmount,date,0);//插入日志
                        }
                        return 1L;
                    }
                });
            }
        }else{
            returnValue[0]= afInterimAuDao.insertInterimAmountLog(userId,interimAmount,date,1);
        }
        return returnValue[0];
    }

    @Override
    public AfInterimAuDo getAfInterimAuByUserId(Long userId) {
        return afInterimAuDao.getByUserId(userId);
    }

    @Override
    public AfInterimAuDo getByUserId(Long userId) {
       return afInterimAuDao.getByUserId(userId);
    }

    @Override
    public AfInterimAuLogDo getLastLogByUserId(Long userId) {
        return afInterimAuDao.getLastLogByUserId(userId);
    }
}
