package com.ald.fanbei.api.web.controller.third;

import com.ald.fanbei.api.biz.bo.FinanceSystemRespBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.common.exception.BizThirdRespCode;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.jsd.mgr.dal.domain.FinaneceDataDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fanmanfu 2018年11月02日 16:55:23
 * @类描述：清结算工具类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third/finance")
public class FinanceSystemController {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Autowired
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Autowired
    JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;

    private static final String SING="FINANACE_JSD";
    private static final String DATA_TYPE1="ACTUAL_PAID";	//实付数据
    private static final String DATA_TYPE2="ACTUAL_INCOME";	//实收数据
    private static final String DATA_TYPE3="PROMISE_INCOME";	//应收数据
    /**
     * 统计清结算系统对账所需数据
     * @Param request {@link FinanceSystemRespBo} 对象
     *@return 根据数据类型得到所需数据 <code>List<code/>
     *
     * **/
    @RequestMapping(value = {"/getData"}, method = RequestMethod.POST)
    @ResponseBody
    public FinanceSystemRespBo getData(HttpServletRequest request){

        FinanceSystemRespBo finance = new FinanceSystemRespBo();
        try {
            String params = new BufferedReader(new InputStreamReader(request.getInputStream())).lines().collect(Collectors.joining(System.lineSeparator()));
            JSONObject paramsJson = JSON.parseObject(params);
            String data = paramsJson.getString("data");
            String timestamp = paramsJson.getString("timestamp");
            String sign = paramsJson.getString("sign");
            String dataType = paramsJson.getString("dataType");

            logger.info("getData is begin  data=" + data + ",timestamp=" + timestamp + ",sign1=" + sign + ",dataType= " + dataType);

            if (StringUtil.equals(sign, DigestUtil.MD5(SING + timestamp))) {// 验签成功

                List<FinaneceDataDo> list = new ArrayList<FinaneceDataDo>();
                if (DATA_TYPE1.equals(dataType)) {    //实付数据
                    list = jsdBorrowCashService.getPaymentDetail();
                } else if (DATA_TYPE2.equals(dataType)) {    //实收数据
                    list = jsdBorrowCashRepaymentService.getRepayData();       //还款数据
                    list.addAll(jsdBorrowCashRenewalService.getRenewalData());   // 续借数据
                } else if (DATA_TYPE3.equals(dataType)) {    //应收数据
                    list = jsdBorrowCashService.getPromiseIncomeDetail();
                } else {
                    logger.info("dataType is not exist");
                }

                finance.setData(JSONObject.toJSONString(list));
                finance.setCode(BizThirdRespCode.SUCCESS.getCode());
                finance.setMsg(BizThirdRespCode.SUCCESS.getMsg());
                return finance;
            }

        } catch (Exception e) {
            logger.error("getData hava a Exception, e = "+e+" and currTime = "+new Date());
        }
        finance.setCode(BizThirdRespCode.FAILED.getCode());
        finance.setMsg(BizThirdRespCode.FAILED.getMsg());
        return finance;

    }
}
