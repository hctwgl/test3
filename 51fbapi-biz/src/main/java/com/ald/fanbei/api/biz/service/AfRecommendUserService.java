package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.*;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface AfRecommendUserService {

    /**
     *更新个人活动相关数据
     * @param userId
     * @param createTime
     * @return
     */
    int updateRecommendByBorrow(long userId, Date createTime);

    /**
     * 个人活动数据总计
     * @param userId
     * @return
     */
    HashMap getRecommedData(long userId);

    /**
     * 个人推荐列表
     * @param userId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    List<HashMap> getRecommendListByUserId(long userId,int pageIndex,int pageSize);

    /**
     *全国排行
     * @return
     */
    List<HashMap> getRecommendListSort(Date startTime,Date endTtime);

    /**
     * 获取活动对应的图片或奖品
     * @param type  RECOMMEND_IMG||RECOMMEND_PRIZE
     * @return
     */
    List<AfResourceDo> getActivieResourceByType(String type);

    /**
     * 获取中奖名单
     * @param datamonth
     * @return
     */
    List<HashMap> getPrizeUser( String datamonth);

    int addRecommendShared(AfRecommendShareDo afRecommendShareDo);

    HashMap getRecommendSharedById(String id);

    int updateRecommendCash(long userId);

    /**
     * 获取活动规则
     * @param type
     * @return
     */

    List<String> getActivityRule(String type);

    /**
     * 用户的邀请码
     * @param userId
     * @return
     */
    String getUserRecommendCode(long userId);

    /**
     * 用户总共的奖励金额
     * @param userId
     * @return
     */
    double getSumPrizeMoney(long userId);

    /**
     * 奖励查询
     * @param userId
     * @param type
     * @return
     */
    List<AfRecommendUserDo> rewardQuery(long userId,String type,Integer currentPage, Integer pageSize);

    /**
     * 总奖励查询总条数
     * @param userId
     * @return
     */
    int rewardQueryCount(long userId,String type);
}
