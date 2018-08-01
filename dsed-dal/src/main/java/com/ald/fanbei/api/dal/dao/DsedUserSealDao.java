package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.DsedUserSealDo;

import java.util.List;

public interface DsedUserSealDao {
    int deleteById(Long id);

    int insert(DsedUserSealDo record);

    DsedUserSealDo selectByUserId(Long id);

    DsedUserSealDo selectByCardId(String id);

    DsedUserSealDo selectById(Long id);

    List<DsedUserSealDo> selectByIds(List list);

    DsedUserSealDo selectByUserName(String userName);

    DsedUserSealDo selectBySealInfo(DsedUserSealDo dsedUserSealDo);

    List<DsedUserSealDo> selectByUserType(String type);

    int updateByUserId(DsedUserSealDo record);
}