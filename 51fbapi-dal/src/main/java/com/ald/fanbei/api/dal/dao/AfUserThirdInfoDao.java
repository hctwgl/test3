package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import org.apache.ibatis.annotations.Param;

/**
 * 用户第三方信息Dao
 *
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserThirdInfoDao extends BaseDao<AfUserThirdInfoDo, Long> {

    /**
     * 根据第三方id查找本平台用户信息
     *
     * @author wangli
     * @date 2018/5/6 16:01
     */
    UserWxInfoDto getLocalUserInfoByThirdId(@Param("thirdId") String thirdId, @Param("thirdType") String thirdType);

    int saveRecord(AfUserThirdInfoDo afUserThirdInfoDo);

    int selectUserThirdInfoByUserName(@Param("userName") String userName);

    int updateByUserName(AfUserThirdInfoDo afUserThirdInfoDo);

}
