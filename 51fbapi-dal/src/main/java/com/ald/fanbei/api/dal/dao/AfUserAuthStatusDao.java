package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 额度拆分多场景认证状体记录Dao
 *
 * @author gaojb
 * @version 1.0.0 初始化
 * @date 2018-01-05 14:58:48
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserAuthStatusDao extends BaseDao<AfUserAuthStatusDo, Long>{
    /**
     * 新增一个用户认证信息场景
     * @param afUserAuthStatusDo
     * @return
     */
    Long addAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);

    /**
     * 根据userId,scene查询一个用户认证场景
     * @param userId
     * @param scene
     * @return
     */
    int selectAfUserAuthStatusByUserIdAndScene(@Param("userId") Long userId, @Param("scene") String scene);

    /**
     * 根据userId,scene,status查询一个用户认证场景
     * @param userId
     * @param scene
     * @param status
     * @return
     */
    AfUserAuthStatusDo selectAfUserAuthStatusByCondition(@Param("userId") Long userId, @Param("scene") String scene,@Param("status") String status);

    /**
     * 查询场景认证
     * @param userId
     * @param scene
     * @return
     */
    AfUserAuthStatusDo getAfUserAuthStatusByUserIdAndScene(@Param("userId") Long userId, @Param("scene") String scene);

    /**
     * 更新一个用户认证场景
     * @param afUserAuthStatusDo
     * @return
     */
    int updateAfUserAuthStatus(AfUserAuthStatusDo afUserAuthStatusDo);


    /**
     * 根据userId,status查询用户认证场景
     * @param userId
     * @param status
     * @return
     */
    List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);
    

    List<AfUserAuthStatusDo> selectAfUserAuthStatusByUserId(@Param("userId") Long userId);
    
    /**
     * 更新场景状态
     * @Param userId
     * @Param scene
     * @Param status
     * @return 
     * **/
    int updateAfUserAuthStatusByUserId(@Param("userId") Long userId, @Param("scene") String scene,@Param("status") String status);
    
    /**
     * 增加场景状态
     * @Param userId
     * @Param scene
     * @Param status
     * @return
     * **/
    int insertAfUserAuthStatus(@Param("userId") Long userId, @Param("scene") String scene,@Param("status") String status);
}


