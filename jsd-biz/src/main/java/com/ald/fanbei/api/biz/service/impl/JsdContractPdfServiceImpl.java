package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.JsdContractPdfService;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.domain.JsdContractPdfDo;


@Service("jsdContractPdfService")
public class JsdContractPdfServiceImpl implements JsdContractPdfService {

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
