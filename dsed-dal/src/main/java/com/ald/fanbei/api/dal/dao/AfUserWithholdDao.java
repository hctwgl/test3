package com.ald.fanbei.api.dal.dao;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;

/**
 * 用户代扣信息Dao
 * 
 * @author hqj
 * @version 1.0.0 初始化
 * @date 2017-11-07 10:52:03
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserWithholdDao extends BaseDao<AfUserWithholdDo, Long> {


    AfUserWithholdDo getByUserId(Long userId);
    
    /**
     * 根据userName得到代扣记录
     *
     * @param userName
     * **/
    AfUserWithholdDo getAfUserWithholdDtoByUserId(long userId);

    /**
     * 插入AfUserWithholdDto 记录
     *
     * **/
    int insertAfUserWithholdDto(AfUserWithholdDo afUserWithholdDo);

    /**
     * 更新代扣开关
     * @param IsSwitch
     * @param userName
     * **/

    int updateAfUserWithholdDtoByUserId(@Param("userId") long userId, @Param("isSwInteger") Integer isSwInteger);
    
    /**
     * 更新银行卡号
     * 
     * **/
    int updateAfUserWithholdDo(AfUserWithholdDo afUserWithholdDo);

    /**
     * 得到代扣初始化信息
     * **/

    AfUserWithholdDo getWithholdInfo(long userId);

    int getCountByUserId(@Param("userId")Long userId);
}
