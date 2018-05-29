package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfContractPdfDo;
import com.ald.fanbei.api.dal.domain.dto.AfLenderInfoDto;

import java.util.HashMap;
import java.util.List;

/**
 * @author guoshuaiqiang 2017年10月27日下午2:26:04
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfContractPdfService {

    AfContractPdfDo getContractPdfDoByTypeAndTypeId(Long id, Byte type);
    List<AfLenderInfoDto> selectLenders(Long pdfId);
    List<AfLenderInfoDto> selectLendersSummary(Long pdfId);
}
