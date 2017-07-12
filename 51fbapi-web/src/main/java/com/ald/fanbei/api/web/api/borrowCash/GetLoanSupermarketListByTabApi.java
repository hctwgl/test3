package com.ald.fanbei.api.web.api.borrowCash;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfLoanSupermarketDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 沈铖 2017/7/5 下午4:37
 * @类描述: GetLoanSupermarketListByTabApi
 * @注意:本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLoanSupermarketListByTabApi")
public class GetLoanSupermarketListByTabApi implements ApiHandle {

    @Resource
    private AfLoanSupermarketDao afLoanSupermarketDao;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        String label = ObjectUtils.toString(requestDataVo.getParams().get("label"), null);
        List<AfLoanSupermarketDo> supermarketList = afLoanSupermarketDao.getLoanSupermarketByLabel(label);
        resp.addResponseData("supermarketList", supermarketList);
        return resp;
    }
}
