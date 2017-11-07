package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfContractPdfDao;
import com.ald.fanbei.api.dal.domain.AfContractPdfDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author gsq 2017年4月10日上午9:42:06
 * @类描述：获取合同pdfUrl
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getContractPdfUrlApi")
public class GetContractPdfUrlApi implements ApiHandle {

    @Resource
    private AfContractPdfDao afContractPdfDao;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long typeId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("typeId"), 0);
        String type = ObjectUtils.toString(requestDataVo.getParams().get("type"), "");
        AfContractPdfDo afContractPdfDo = new AfContractPdfDo();
        if (typeId == null) {
            logger.error("id not exist" + FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
        if (type == null) {
            logger.error("id not exist" + FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
            return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST);
        }
        afContractPdfDo.setTypeId(typeId);
        afContractPdfDo.setType(Byte.parseByte(type));
        AfContractPdfDo afContractPdfDo1 = afContractPdfDao.selectByTypeId(afContractPdfDo);
        if (null != afContractPdfDo1 && null != afContractPdfDo1.getContractPdfUrl()) {
            resp.addResponseData("pdfUrl", afContractPdfDo1.getContractPdfUrl());
        } else {
            resp.addResponseData("pdfUrl", "");
        }
        return resp;
    }

}
