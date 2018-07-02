package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfUserRegisterTypeDo;

public interface AfUserRegisterTypeDao {
    int deleteById(Long id);

    int insert(AfUserRegisterTypeDo record);

    AfUserRegisterTypeDo selectByUserId(Long id);

    int updateById(AfUserRegisterTypeDo record);

}