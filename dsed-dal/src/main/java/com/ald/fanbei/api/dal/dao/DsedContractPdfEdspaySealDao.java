package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.DsedContractPdfEdspaySealDo;
import com.ald.fanbei.api.dal.domain.dto.DsedContractPdfEdspaySealDto;

import java.util.List;

/**
 * e都市钱包用户出借信息与协议关联表Dao
 *
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-01-22 17:34:26
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface DsedContractPdfEdspaySealDao extends BaseDao<DsedContractPdfEdspaySealDo, Long> {

    int batchInsert(List<DsedContractPdfEdspaySealDo> edspaySealDoList);

    List<DsedContractPdfEdspaySealDto> getByPDFId(Long id);

}
