package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfBankcardConfigDao;
import com.ald.fanbei.api.dal.domain.dto.BankCardInfoDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 *@类现描述：提交绑卡
 *@author ZJF 2018年4月09日
 *@since 4.1.2
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBankCardInfoApi")
public class GetBankCardInfoApi implements ApiHandle {

    @Autowired
    AfBankcardConfigDao afBankcardConfigDao;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, final FanbeiContext context, final HttpServletRequest request) {

        Object cardNo = requestDataVo.getParams().get("cardNo");
        if (cardNo == null) {
            throw new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
        }

        BankCardInfoDto bankCardInfoDto = afBankcardConfigDao.getByCardNo(cardNo.toString());
        if(bankCardInfoDto==null)
        {
            bankCardInfoDto = new BankCardInfoDto();
            bankCardInfoDto.setCardType(7);
        }

        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        resp.setResponseData(bankCardInfoDto);
        return resp;
    }

}
