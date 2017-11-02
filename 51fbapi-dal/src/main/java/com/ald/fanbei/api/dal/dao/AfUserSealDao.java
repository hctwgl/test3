package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfUserSealDo;

public interface AfUserSealDao {
    int deleteById(Long id);

    int insert(AfUserSealDo record);

    AfUserSealDo selectByUserId(Long id);

    AfUserSealDo selectByUserType(String type);

    int updateByUserId(AfUserSealDo record);
}