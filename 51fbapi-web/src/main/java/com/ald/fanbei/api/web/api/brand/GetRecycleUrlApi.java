/**
 *
 */
package com.ald.fanbei.api.web.api.brand;

import com.ald.fanbei.api.biz.service.AfRecycleViewService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.query.AfRecycleViewQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;

/**
 * @author weiqingeng 2018年3月3日上午11:58:15
 * @类描述：获取有得卖 回收业务 Url
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getRecycleUrlApi")
public class GetRecycleUrlApi implements ApiHandle {

    @Autowired
    private AfRecycleViewService afRecycleViewService;
    private String URL = "http://51fanbei.youdemai.com";

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long uid = context.getUserId();
        String recycleUrl = URL + "?uid=" + uid;
        resp.addResponseData("recycleUrl", recycleUrl);

        //添加页面访问记录
        AfRecycleViewQuery afRecycleViewQuery = new AfRecycleViewQuery(uid, 1);
        afRecycleViewService.getRecycleViewByUid(afRecycleViewQuery);
        return resp;
    }


}
