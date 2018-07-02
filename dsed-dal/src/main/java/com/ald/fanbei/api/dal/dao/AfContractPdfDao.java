package com.ald.fanbei.api.dal.dao;


import com.ald.fanbei.api.dal.domain.AfContractPdfDo;
import com.ald.fanbei.api.dal.domain.dto.AfLenderInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

public interface AfContractPdfDao {
    int deleteById(Long id);

    int insert(AfContractPdfDo record);

    AfContractPdfDo selectById(Long id);

    AfContractPdfDo selectByTypeId(AfContractPdfDo afContractPdfDo);

    int updateById(AfContractPdfDo record);

    List<AfLenderInfoDto> selectLenders(@Param("pdfId")Long pdfId);
    List<AfLenderInfoDto> selectLendersSummary(@Param("pdfId")Long pdfId);
}