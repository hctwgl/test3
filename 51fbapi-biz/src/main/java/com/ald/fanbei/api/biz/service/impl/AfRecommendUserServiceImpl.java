package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfRecommendUserDao;
import com.ald.fanbei.api.dal.dao.AfResourceDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import sun.awt.geom.AreaOp;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service("afRecommendUserService")
public class AfRecommendUserServiceImpl implements AfRecommendUserService {

    @Resource
    AfRecommendUserDao afRecommendUserDao;
    @Resource
    AfUserAccountDao afUserAccountDao;

    @Resource
    AfResourceDao afResourceDao;

    @Resource
    AfBorrowCashDao afBorrowCashDao;

    private BigDecimal getAddMoney(){
        List<AfResourceDo> list = afResourceDao.getActivieResourceByType("RECOMMEND_MONEY");
        if(list != null && list.size()>0){
            try{
                return new BigDecimal( Double.parseDouble( list.get(0).getValue()));
            }
            catch (Exception e){

            }
        }
        return new BigDecimal(50);
    }


    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     *
     * @param userId
     * @param createTime
     * @return
     */
    public int updateRecommendByBorrow(long userId, Date createTime){
        try {
            //不影响原来逻辑，不保持事物一样
            AfRecommendUserDo afRecommendUserDo = afRecommendUserDao.getARecommendUserById(userId);
            if (afRecommendUserDo != null && !afRecommendUserDo.isIs_loan()) {
                Long count = 0L;
                HashMap map =  afBorrowCashDao.getBorrowCashByRemcommend(userId);
                try {
                    count = (Long) map.get("count");
                    if (count > 1) return 1;
                }
                catch (Exception e){
                    
                }

                afRecommendUserDo.setLoan_time(createTime);
                BigDecimal addMoney = getAddMoney();
                afRecommendUserDo.setPrize_money(addMoney);
                afRecommendUserDao.updateLoanById(afRecommendUserDo);
                //修改返现金额
                long pid = afRecommendUserDo.getParentId();
                AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
                afUserAccountDo.setUserId(pid);
                afUserAccountDo.setRebateAmount(addMoney);
                afUserAccountDao.updateRebateAmount(afUserAccountDo);
            }
            return 1;
        }
        catch (Exception e){
            return 1;
        }
    }


    public HashMap getRecommedData(long userId){
        return  afRecommendUserDao.getRecommedData(userId);
    }


    public List<HashMap> getRecommendListByUserId(long userId, int pageIndex, int pageSize){
        pageIndex = pageSize* pageIndex;
        return afRecommendUserDao.getRecommendListByUserId(userId,pageIndex,pageSize);
    }

    public List<HashMap> getRecommendListSort(Date startTime,Date endTime){
        return afRecommendUserDao.getRecommendListSort(startTime,endTime);
    }

    /**
     * 获取活动对应的图片或奖品
     * @param type  RECOMMEND_IMG||RECOMMEND_PRIZE
     * @return
     */
    public List<AfResourceDo> getActivieResourceByType(String type){
        return afResourceDao.getActivieResourceByType(type);
    }

    /**
     * 获取中奖名单
     * @param datamonth  月份
     * @return
     */
    public List<HashMap> getPrizeUser( String datamonth){
        return afRecommendUserDao.getPrizeUser(datamonth);
    }

    public int addRecommendShared(AfRecommendShareDo afRecommendShareDo){
        return  afRecommendUserDao.addRecommendShared(afRecommendShareDo);
    }

    public HashMap getRecommendSharedById(String id){
        return  afRecommendUserDao.getRecommendSharedById(id);
    }

}
