package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfRecommendMoneyDo;
import com.ald.fanbei.api.dal.domain.AfRecommendShareDo;
import com.ald.fanbei.api.dal.domain.AfRecommendUserDo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public interface AfRecommendUserDao {
    int  addRecommendUser(AfRecommendUserDo afRecommendUserDo);
    AfRecommendUserDo getARecommendUserById(@Param("user_id") Long user_id);
    int updateLoanById(AfRecommendUserDo afRecommendUserDo);
    HashMap getRecommedData(@Param("userId") long userId);
    List<HashMap> getRecommendListByUserId(@Param("userId") long userId,@Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);
    List<HashMap> getRecommendListSort(@Param("startTime") Date startTime,@Param("endTime") Date endTime);

    List<HashMap> getPrizeUser(@Param("datamonth") String datamonth);

    int addRecommendShared(AfRecommendShareDo afRecommendShareDo);

    HashMap getRecommendSharedById(@Param("id") String id);

    double getSumPrizeMoney(@Param("userId") long userId);

    List<AfRecommendUserDo> firstRewardQuery(@Param("userId") long userId,@Param("pageNo")long pageNo,@Param("pageSize")Integer pageSize);

    List<AfRecommendUserDo> twoLevelRewardQuery(@Param("userId") long userId,@Param("pageNo")long pageNo,@Param("pageSize")Integer pageSize);

    int firstRewardQueryCount(@Param("userId") long userId);

    int twoLevelRewardQueryCount(@Param("userId") long userId);

    int addRecommendMoney(AfRecommendMoneyDo afRecommendMoneyDo);

    AfRecommendUserDo  getARecommendUserByIdAndType(@Param("user_id") Long user_id,@Param("type") int type);

}
