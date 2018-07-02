package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSignRewardExtDo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 分类运营位配置Dao
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 14:01:11
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSignRewardExtDao  {

    AfSignRewardExtDo selectByUserId(@Param("userId") Long userId);

    int extractMoney(@Param("userId")Long userId,@Param("amount") BigDecimal amount);

    int updateSignRewardExt(AfSignRewardExtDo afSignRewardExtDo);

    int increaseMoney(AfSignRewardExtDo afSignRewardExtDo);

    int saveRecord(AfSignRewardExtDo afSignRewardExtDo);

    List<AfSignRewardExtDo> selectByUserIds(@Param("userIds") List<Long> userIds);

    int saveRecordBatch(List<AfSignRewardExtDo> list );

    int increaseMoneyBtach(@Param("list") List<AfSignRewardExtDo> list );

    int updateSignRemind(AfSignRewardExtDo afSignRewardExtDo);


}
