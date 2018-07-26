package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.DsedContractPdfDo;
import com.ald.fanbei.api.dal.domain.dto.DsedLenderInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DsedContractPdfDao {
    int deleteById(Long id);

    int insert(DsedContractPdfDo record);

    DsedContractPdfDo selectById(Long id);

    DsedContractPdfDo selectByTypeId(DsedContractPdfDo dsedContractPdfDo);

    int updateById(DsedContractPdfDo record);

    List<DsedLenderInfoDto> selectLenders(@Param("pdfId") Long pdfId);

    List<DsedLenderInfoDto> selectLendersSummary(@Param("pdfId") Long pdfId);
}