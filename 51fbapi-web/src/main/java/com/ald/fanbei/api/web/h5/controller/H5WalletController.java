package com.ald.fanbei.api.web.h5.controller;

import com.ald.fanbei.api.biz.service.AfCashRecordService;
import com.ald.fanbei.api.biz.service.AfOrderService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.dal.domain.AfCashRecordDo;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;
import javafx.scene.chart.ValueAxis;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author luoxiao @date 2018/5/17 10:23
 * @类描述：钱包
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping(value = "/vallet/")
public class H5WalletController extends BaseController{
    @Resource
    private AfUserAccountService afUserAccountService;
    @Resource
    private AfTaskUserService afTaskUserService;
    @Resource
    private AfOrderService afOrderService;
    @Resource
    private AfCashRecordService afCashRecordService;

    @ResponseBody
    @RequestMapping(value = "valletPage", method = RequestMethod.POST, produces = "application/json")
    public String valletPage(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
            FanbeiH5Context context = doH5Check(request, true);
            Long userId = context.getUserId();

            // 累计收益
            AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(userId);
            BigDecimal rebateAmount = userAccountDo.getRebateAmount();
            BigDecimal ucAmount = userAccountDo.getUcAmount();
            BigDecimal totalAmount = rebateAmount.add(ucAmount);
            // 可用余额
            data.put("rebateAmount", rebateAmount);
            // 累计收益
            data.put("totalAmount", totalAmount);

            // 我的金币
            Long availableCoinAmount = afTaskUserService.getAvailableCoinAmount(userId);
            data.put("availableCoinAmount", availableCoinAmount);

            // 近七天的收益情况
            List<Map<String, Object>> IncomeList = afTaskUserService.getIncomeOfNearlySevenDays(userId);
            data.put("IncomeList", IncomeList);

            return H5CommonResponse.getNewInstance(true,"","", data).toString();
        }catch(Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @ResponseBody
    @RequestMapping(value = "getIncomeDetails", method = RequestMethod.POST, produces = "application/json")
    public String getIncomeDetails(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
            FanbeiH5Context context = doH5Check(request, true);
            Long userId = context.getUserId();
            String detailType = request.getParameter("detailType");
            List<AfTaskUserDo> taskUserList = afTaskUserService.getDetailsByUserId(userId,detailType);
            data.put("taskUserList",taskUserList);

            return H5CommonResponse.getNewInstance(true,"", "", data).toString();
        } catch (Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @ResponseBody
    @RequestMapping(value = "getWithDrawDetail", method = RequestMethod.POST, produces = "application/json")
    public String getWithDrawDetail(HttpServletRequest request, HttpServletResponse response){
        Map<String, Object> data = Maps.newHashMap();
        try{
            FanbeiH5Context context = doH5Check(request, true);
            String withdrawId = request.getParameter("withdrawId");
            AfCashRecordDo cashRecordDo = afCashRecordService.getCashRecordById(Long.parseLong(withdrawId));
            data.put("cashRecord",cashRecordDo);

            return H5CommonResponse.getNewInstance(true,"", "", data).toString();
        } catch (Exception e){
            logger.error("unknown error", e);
        }

        return H5CommonResponse.getNewInstance(false,"").toString();
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        return null;
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }

}
