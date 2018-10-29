package com.ald.fanbei.api.web.controller.third;

import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.enums.JsdRepayType;
import com.ald.fanbei.api.common.enums.RepayType;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.domain.dto.JsdBorrowCashDto;
import com.ald.jsd.mgr.dal.dao.MgrOperateLogDao;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yinxiangyu 2018年10月26日 下午15:14:32
 * @类现描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/clearing")
public class ClearingController {
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
//    @Resource
//    MgrOperateLogDao mgrOperateLogDao;

    @RequestMapping(value = {"/getUserInfo"},method = RequestMethod.GET)
    @ResponseBody
    public JsdUserDo getUserInfo(HttpServletRequest request){
        String mobile= ObjectUtils.toString(request.getParameter("mobile"),null);
        JsdUserDo jsdUserDo=jsdUserService.getUserInfo(mobile);
        return jsdUserDo;
    }

    @RequestMapping(value = {"/getBorrowInfos"},method = RequestMethod.GET)
    @ResponseBody
    public List<JsdBorrowCashDto> getBorrowInfos(HttpServletRequest request){
        String mobile= ObjectUtils.toString(request.getParameter("mobile"),null);
        List<JsdBorrowCashDto> borrowList = jsdBorrowCashService.getBorrowCashsInfos(mobile);
        return borrowList;
    }

    @RequestMapping(value = {"/offline/repay"},method = RequestMethod.POST)
    @ResponseBody
    public Resp<?> offlineRepay(@RequestBody Map<String, String> data){
        String borrowNo = data.get("borrowNo");
        String operator=data.get("operator");
        String amount = data.get("accountAmount");
        String remark = data.get("remark");
        JsdBorrowCashDo borrowCashDo = jsdBorrowCashService.getByBorrowNo(borrowNo);
        JsdBorrowLegalOrderCashDo legalOrderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByBorrowId(borrowCashDo.getRid());
        String dataId = String.valueOf(borrowCashDo.getRid() + borrowCashDo.getRenewalNum());
        jsdBorrowCashRepaymentService.offlineRepay(borrowCashDo, legalOrderCashDo, amount, null, borrowCashDo.getUserId(), JsdRepayType.SETTLE_SYSTEM, null, null, null, dataId, remark);
//        mgrOperateLogDao.addOperateLog(operator, "线下还款：" + JSON.toJSONString(data));
        return Resp.succ();
    }
}
