package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfContractPdfService;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.domain.AfContractPdfDo;
import com.ald.fanbei.api.dal.domain.dto.AfLenderInfoDto;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;


@Service("afContractPdfService")
public class AfContractPdfServiceImpl implements AfContractPdfService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AfContractPdfServiceImpl.class);

    @Resource
    private AfContractPdfDao afContractPdfDao;
    @Override
    public AfContractPdfDo getContractPdfDoByTypeAndTypeId(Long id, Byte type) {
        AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
        afContractPdfDo.setType(type);
        afContractPdfDo.setTypeId(id);
        return afContractPdfDao.selectByTypeId(afContractPdfDo);
    }

    @Override
    public List<AfLenderInfoDto> selectLenders(Long pdfId) {
        return afContractPdfDao.selectLenders(pdfId);
    }
    @Override
    public List<AfLenderInfoDto> selectLendersSummary(Long pdfId) {
        return afContractPdfDao.selectLendersSummary(pdfId);
    }
}
