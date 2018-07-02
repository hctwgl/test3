package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.bo.CollectionUpdateResqBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionSystemUtil;
import com.ald.fanbei.api.biz.third.util.LoanCollectionSystemUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfBorrowCashStatus;
import com.ald.fanbei.api.common.enums.AfBorrowLegalOrderCashStatus;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderCashDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfOverdueOrderDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

/**
 * @author chengkang 2017年8月5日 下午16:59:39
 * @类现描述：和催收平台互调
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/collection")
public class LoanCollectionController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    AfBorrowCashService borrowCashService;

    @Resource
    AfLoanPeriodsService afLoanPeriodsService;

    @Resource
    AfLoanRepaymentService loanRepaymentService;

    @Resource
    LoanCollectionSystemUtil loanCollectionSystemUtil;

    /**
     * 用户通过催收平台还款，经财务审核通过后，系统自动调用此接口向爱上街推送,爱上街记录白领贷线下还款信息
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/offlineLoanRepayment"}, method = RequestMethod.POST)
    @ResponseBody
    public CollectionOperatorNotifyRespBo offlineLoanRepayment(HttpServletRequest request, HttpServletResponse response) {
        String data = ObjectUtils.toString(request.getParameter("data"));
        String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
        String sign = ObjectUtils.toString(request.getParameter("sign"));
        logger.info("deal offlineRepayment begin,sign=" + sign + ",data=" + data + ",timestamp=" + timestamp);
        CollectionOperatorNotifyRespBo notifyRespBo = loanCollectionSystemUtil.loanOfflineRepaymentNotify(timestamp, data, sign);
        return notifyRespBo;
    }

    /**
     * 催收平台查询白领贷提前还款接口接口
     *
     * @param loanId
     * @return
     */
    @RequestMapping(value = {"/getLoanPeriodsInfo"}, method = RequestMethod.POST)
    @ResponseBody
    public CollectionUpdateResqBo getLoanPeriodsInfo(HttpServletRequest request, HttpServletResponse response) {
        Long loanId = NumberUtil.objToLongDefault(request.getParameter("data"),0l);
        String timestamp = ObjectUtils.toString(request.getParameter("timestamp"));
        String sign = ObjectUtils.toString(request.getParameter("sign"));

        logger.info("getLoanPeriodsInfo data=" + loanId + ",timestamp=" + timestamp + ",sign1=" + sign + "");
        JSONArray jsonArray = new JSONArray();
        List<AfLoanPeriodsDo> afLoanPeriodsDoList = afLoanPeriodsService.getNoRepayListByLoanId(loanId);
        for (AfLoanPeriodsDo loanPeriodsDo : afLoanPeriodsDoList) {
            JSONObject object = new JSONObject();
            object.put("nper",loanPeriodsDo.getNper());
            object.put("periodsId",loanPeriodsDo.getRid());
            BigDecimal amount = BigDecimal.ZERO;
            if(loanRepaymentService.canRepay(loanPeriodsDo)) { // 已出账
                 amount = BigDecimalUtil.add(loanPeriodsDo.getAmount(),
                        loanPeriodsDo.getRepaidInterestFee(),loanPeriodsDo.getInterestFee(),
                        loanPeriodsDo.getServiceFee(),loanPeriodsDo.getRepaidServiceFee(),
                        loanPeriodsDo.getOverdueAmount(),loanPeriodsDo.getRepaidOverdueAmount())
                        .subtract(loanPeriodsDo.getRepayAmount());
            }else { // 未出账， 提前还款时不用还手续费和利息
                amount = BigDecimalUtil.add(loanPeriodsDo.getAmount());
            }
            object.put("amount",amount);
            jsonArray.add(object);
        }
        CollectionUpdateResqBo updteBo = new CollectionUpdateResqBo();
        if (afLoanPeriodsDoList == null || afLoanPeriodsDoList.size() == 0) {
            logger.error("findBorrowCashByBorrowNo afBorrowCashDo is null");
            updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST.getCode());
            updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST.getMsg());
            return updteBo;
        }
        String sign1 = DigestUtil.MD5(String.valueOf(loanId));
        if (StringUtil.equals(sign, sign1)) { // 验签成功
            updteBo.setCode(FanbeiThirdRespCode.SUCCESS.getCode());
            updteBo.setMsg(FanbeiThirdRespCode.SUCCESS.getMsg());
            updteBo.setData(jsonArray.toJSONString());
        } else {
            logger.info("sign and sign is fail");
            updteBo.setCode(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getCode());
            updteBo.setMsg(FanbeiThirdRespCode.COLLECTION_REQUEST_SIGN.getMsg());
            return updteBo;
        }
        return updteBo;
    }

}
