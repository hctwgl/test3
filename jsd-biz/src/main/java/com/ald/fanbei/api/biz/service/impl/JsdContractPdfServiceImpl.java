package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.JsdContractPdfService;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.domain.JsdContractPdfDo;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("jsdContractPdfService")
public class JsdContractPdfServiceImpl implements JsdContractPdfService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JsdContractPdfServiceImpl.class);

    @Resource
    private JsdContractPdfDao afContractPdfDao;

    @Override
    public JsdContractPdfDo getContractPdfDoByTypeAndTypeId(Long id, Byte type) {
        JsdContractPdfDo afContractPdfDo = new JsdContractPdfDo();
        afContractPdfDo.setType(type);
        afContractPdfDo.setTypeId(id);
        return afContractPdfDao.selectByTypeId(afContractPdfDo);
    }
}
