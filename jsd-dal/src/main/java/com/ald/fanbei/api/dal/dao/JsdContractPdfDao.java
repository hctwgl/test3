package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.JsdContractPdfDo;

public interface JsdContractPdfDao {
    int deleteById(Long id);

    int insert(JsdContractPdfDo record);

    JsdContractPdfDo selectById(Long id);

    JsdContractPdfDo selectByTypeId(JsdContractPdfDo afContractPdfDo);

    int updateById(JsdContractPdfDo record);

}