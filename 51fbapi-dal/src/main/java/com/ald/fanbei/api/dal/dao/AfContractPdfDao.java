package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfContractPdfDo;

public interface AfContractPdfDao {
    int deleteById(Long id);

    int insert(AfContractPdfDo record);

    AfContractPdfDo selectById(Long id);

    int updateById(AfContractPdfDo record);

}