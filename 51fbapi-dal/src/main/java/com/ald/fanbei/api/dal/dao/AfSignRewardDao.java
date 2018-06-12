package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfSignRewardDo;
import com.ald.fanbei.api.dal.domain.dto.AfSignRewardDto;
import com.ald.fanbei.api.dal.domain.query.AfSignRewardQuery;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 分类运营位配置Dao
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-07 13:51:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSignRewardDao  {

    int isExist(@Param("userId")Long userId);

    List<AfSignRewardDo> sumSignDays(@Param("userId")Long userId,@Param("startTime")Date startTime);

    List<AfSignRewardDto> getRewardDetailList(AfSignRewardQuery query);

    int saveRecord(AfSignRewardDo afSignRewardDo);

    int checkUserSign(@Param("userId") Long userId);

    int friendUserSign(@Param("friendUserId")Long friendUserId);

    int frienddUserSignCount(@Param("userId")Long userId,@Param("friendUserId")Long friendUserId);

    int frienddUserSignCountToDay(@Param("userId")Long userId,@Param("friendUserId")Long friendUserId);

    int saveRecordBatch(@Param("list") List<AfSignRewardDo> list);
}
