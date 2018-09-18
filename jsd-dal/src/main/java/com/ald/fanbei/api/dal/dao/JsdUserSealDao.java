package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.JsdUserSealDo;

import java.util.List;

public interface JsdUserSealDao {
    int deleteById(Long id);

    int insert(JsdUserSealDo record);

    JsdUserSealDo selectByUserId(Long id);

    JsdUserSealDo selectByCardId(String id);

    JsdUserSealDo selectById(Long id);

    List<JsdUserSealDo> selectByIds(List list);

    JsdUserSealDo selectByUserName(String userName);

    JsdUserSealDo selectBySealInfo(JsdUserSealDo afUserSealDo);

    List<JsdUserSealDo> selectByUserType(String type);

    int updateByUserId(JsdUserSealDo record);
}