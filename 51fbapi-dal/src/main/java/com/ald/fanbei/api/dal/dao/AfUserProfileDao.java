package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserProfileDo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户关联账号Dao
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-01-24 16:04:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserProfileDao extends BaseDao<AfUserProfileDo, Long> {

    void saveUserProfile(AfUserProfileDo userProfileDo);
    void updateUserProfileById(AfUserProfileDo userProfileDo);
    AfUserProfileDo getUserProfileById(AfUserProfileDo userProfileDo);
    AfUserProfileDo getUserProfileByCommonCondition(AfUserProfileDo userProfileDo);
    List<AfUserProfileDo> getUserProfileListByCommonCondition(AfUserProfileDo userProfileDo);
    void updateDeleteUserProfileById(@Param("rid")long id);

}
