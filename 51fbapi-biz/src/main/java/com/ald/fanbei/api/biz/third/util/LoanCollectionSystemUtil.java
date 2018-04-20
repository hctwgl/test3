package com.ald.fanbei.api.biz.third.util;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.service.AfLoanRepaymentService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.biz.util.CommitRecordUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiThirdRespCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.domain.AfLoanDo;
import com.ald.fanbei.api.dal.domain.AfLoanRepaymentDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author guoshuaiqiang 2018年4月3日 16:55:23
 * @类描述：白领贷和催收系统互调工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的 需加密参数 真实姓名 ， 身份证号， 手机号，邮箱，银行卡号
 */
@Component("loanCollectionSystemUtil")
public class LoanCollectionSystemUtil extends AbstractThird {

    private static String url = null;

    @Resource
    AfLoanRepaymentService afLoanRepaymentService;
    @Resource
    AfLoanService afLoanService;
    @Resource
    CommitRecordUtil commitRecordUtil;

    private static String getUrl() {
        if (url == null) {
            url = ConfigProperties.get(Constants.CONFKEY_COLLECTION_URL);
            return url;
        }
        return url;
    }


    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }




    public CollectionOperatorNotifyRespBo loanOfflineRepaymentNotify(String timestamp, String data, String sign) {
        // 响应数据,默认成功
        CollectionOperatorNotifyRespBo notifyRespBo = new CollectionOperatorNotifyRespBo(FanbeiThirdRespCode.SUCCESS);
        try {
            CollectionOperatorNotifyReqBo reqBo = new CollectionOperatorNotifyReqBo();
            reqBo.setData(data);
            reqBo.setSign(DigestUtil.MD5(data));
            logThird(sign, "loanOfflineRepaymentNotify", reqBo);
            if (StringUtil.equals(sign, reqBo.getSign())) {// 验签成功
                JSONObject obj = JSON.parseObject(data);
                String repayNo = obj.getString("repay_no");
                String loanNo = obj.getString("loan_no");
                String repayType = obj.getString("repay_type");
                String repayAmount = obj.getString("repay_amount");
                String restAmount = obj.getString("rest_amount");
                String repayCardNum = obj.getString("repay_cardNum");
                String operator = obj.getString("operator");
                String tradeNo = obj.getString("trade_no"); // 三方交易流水号
                String isBalance = obj.getString("is_balance");
                String isAdmin = obj.getString("is_admin");
                Long repaymentId = NumberUtil.objToLongDefault("repayment_id",0l);
                JSONArray array = obj.getJSONArray("periods_list");
                List<HashMap> periodsList = JSONObject.parseArray(obj.getString("periods_list"),HashMap.class);
                if (array == null || array.size() == 0){
                    notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
                    return notifyRespBo;
                }
                boolean isAllRepay = obj.getBoolean("is_all_repay");
                if (StringUtil.isAllNotEmpty(repayNo, loanNo, tradeNo)) {
                    AfLoanDo loanDo = afLoanService.getByLoanNo(loanNo);
                    if (loanDo == null) {
                        notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.BORROW_CASH_NOT_EXISTS);
                        return notifyRespBo;
                    }
                    afLoanRepaymentService.offlineRepay(loanDo, loanNo, repayType, repayAmount, restAmount, tradeNo, isBalance, repayCardNum, operator, isAdmin, isAllRepay,repaymentId,periodsList);
                    notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SUCCESS);
                } else {
                    notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_PARAM_NOT_EXIST);
                }
            } else {
                logger.info("loanOfflineRepaymentNotify sign is invalid", FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
                notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.REQUEST_INVALID_SIGN_ERROR);
            }
        } catch (FanbeiException e) {
            logger.error("loanOfflineRepaymentNotify fanbei error", e);
            notifyRespBo.setCode(e.getErrorCode().getCode());
            notifyRespBo.setMsg(e.getErrorCode().getErrorMsg());
        } catch (Exception e) {
            logger.error("loanOfflineRepaymentNotify error", e);
            notifyRespBo.resetMsgInfo(FanbeiThirdRespCode.SYSTEM_ERROR);
        } finally {
            notifyRespBo.setSign(DigestUtil.MD5(JsonUtil.toJSONString(notifyRespBo)));
        }

        return notifyRespBo;
    }
}
