package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfUserSealDo;

import java.util.List;

public interface AfUserSealDao {
    int deleteById(Long id);

    int insert(AfUserSealDo record);

    AfUserSealDo selectByUserId(Long id);

    List<AfUserSealDo> selectByUserType(String type);

    int updateByUserId(AfUserSealDo record);
}