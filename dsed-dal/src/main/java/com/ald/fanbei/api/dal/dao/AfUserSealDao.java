package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfUserSealDo;

import java.util.List;

public interface AfUserSealDao {
    int deleteById(Long id);

    int insert(AfUserSealDo record);

    AfUserSealDo selectByUserId(Long id);

    AfUserSealDo selectByCardId(String id);

    AfUserSealDo selectById(Long id);

    List<AfUserSealDo> selectByIds(List list);

    AfUserSealDo selectByUserName(String userName);

    AfUserSealDo selectBySealInfo(AfUserSealDo afUserSealDo);

    List<AfUserSealDo> selectByUserType(String type);

    int updateByUserId(AfUserSealDo record);
}