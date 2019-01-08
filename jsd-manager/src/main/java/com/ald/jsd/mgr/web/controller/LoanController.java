package com.ald.jsd.mgr.web.controller;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.common.enums.BorrowVersionType;
import com.ald.fanbei.api.common.enums.ResourceSecType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.BizException;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.JsdProctocolBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRenewalService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdCollectionBorrowService;
import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.biz.service.JsdTotalInfoService;
import com.ald.fanbei.api.biz.service.JsdUserService;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdTotalInfoDao;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRenewalDo;
import com.ald.fanbei.api.dal.domain.JsdCollectionBorrowDo;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.ald.fanbei.api.dal.domain.JsdTotalInfoDo;
import com.ald.fanbei.api.dal.domain.JsdUserDo;
import com.ald.fanbei.api.dal.query.LoanQuery;
import com.ald.jsd.mgr.web.dto.req.LoanDetailsReq;
import com.ald.jsd.mgr.web.dto.resp.Resp;
import com.alibaba.fastjson.JSONObject;

@Controller
@ResponseBody
@RequestMapping("/api/loan/")
public class LoanController {

    @Resource
    JsdBorrowCashService jsdBorrowCashService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;
    @Resource
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    JsdTotalInfoDao jsdTotalInfoDao;
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    JsdTotalInfoService jsdTotalInfoService;
    

    @RequestMapping(value = {"list.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> list(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
    	loanQuery.setFull(true);
    loanQuery.setList(jsdBorrowCashService.getLoanList(loanQuery));
    return Resp.succ(loanQuery, "");}
    @RequestMapping(value = {"statistics.json"}, method = RequestMethod.POST)
    public Resp<HashMap<String, BigDecimal>> statistics(HttpServletRequest request) {
        HashMap<String, BigDecimal> hashMap = jsdBorrowCashService.getLoanStatistics();
        return Resp.succ(hashMap, "");
    }

    @RequestMapping(value = {"details.json"}, method = RequestMethod.POST)
    public Resp<LoanDetailsReq> details(@RequestBody JSONObject jsonObject, HttpServletRequest request) throws InvocationTargetException, IllegalAccessException {
        LoanDetailsReq loanDetailsReq = new LoanDetailsReq();
        String tradeNoXgxy = jsonObject.getString("tradeNoXgxy");
        if(StringUtil.isEmpty(tradeNoXgxy)){
            Resp.fail("参数错误");
        }
        //借款信息
        JsdBorrowCashDo jsdBorrowCashDo = jsdBorrowCashService.getByTradeNoXgxy(tradeNoXgxy);
        BeanUtils.copyProperties(loanDetailsReq, jsdBorrowCashDo);
        loanDetailsReq.setTerm(jsdBorrowCashDo.getType());
        loanDetailsReq.setApplyDate(jsdBorrowCashDo.getGmtCreate());
        loanDetailsReq.setUnrepayAmount(jsdBorrowCashDo.getAmount().add(jsdBorrowCashDo.getInterestAmount()).add(jsdBorrowCashDo.getPoundageAmount()).add(jsdBorrowCashDo.getOverdueAmount()).add(jsdBorrowCashDo.getSumRepaidPoundage()).add(jsdBorrowCashDo.getSumRepaidInterest()).add(jsdBorrowCashDo.getSumRepaidOverdue()).subtract(jsdBorrowCashDo.getRepayAmount()));
        loanDetailsReq.setUnrepayInterestAmount(jsdBorrowCashDo.getInterestAmount());
        loanDetailsReq.setUnrepayOverdueAmount(jsdBorrowCashDo.getOverdueAmount());
        loanDetailsReq.setUnrepayServiceAmount(jsdBorrowCashDo.getPoundageAmount());
        loanDetailsReq.setLoanRemark(jsdBorrowCashDo.getBorrowRemark());
        //用户信息
        JsdUserDo jsdUserDo = jsdUserService.getById(jsdBorrowCashDo.getUserId());
        loanDetailsReq.setRealName(jsdUserDo.getRealName());
        loanDetailsReq.setMobile(jsdUserDo.getMobile().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
        loanDetailsReq.setIdNumber(jsdUserDo.getIdNumber());
        //是否入催
        JsdCollectionBorrowDo jsdCollectionBorrowDo = jsdCollectionBorrowService.selectByBorrowId(jsdBorrowCashDo.getRid());
        if (jsdCollectionBorrowDo == null) {
            loanDetailsReq.setIsCollection("N");
        } else {
            loanDetailsReq.setIsCollection("Y");
        }
        List<JsdBorrowCashRenewalDo> list = jsdBorrowCashRenewalService.getMgrJsdRenewalByBorrowId(jsdBorrowCashDo.getRid());
        loanDetailsReq.setRenewal(list);
        List<JsdProctocolBo> proctocol = new ArrayList<>();
        if(StringUtil.equals(jsdBorrowCashDo.getVersion(), BorrowVersionType.SELL.name())){
            proctocol = jsdBorrowCashService.getBorrowProtocols(jsdUserDo.getOpenId(),tradeNoXgxy,"");
        }else if(StringUtil.equals(jsdBorrowCashDo.getVersion(),BorrowVersionType.BEHEAD.name())){
            proctocol = jsdBorrowCashService.getBorrowPlusProtocols(jsdUserDo.getOpenId(),tradeNoXgxy,"");
        }
        loanDetailsReq.setProctocols(proctocol);
        return Resp.succ(loanDetailsReq, "");
    }
    
    
    
    

    /**
     * total:借款汇总统计
     *
     * @author zhangxinxing
     * @param loanQuery
     * @param request
     * @return
     */
    @RequestMapping(value = {"total.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> total(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
    	//转换提日期输出格式

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -8);
		date = calendar.getTime();
		
		
		
		Date startDate = loanQuery.getStartDate() == null ? date : loanQuery.getStartDate();
		Date endDate = loanQuery.getEndDate() == null ? new Date() : loanQuery.getEndDate();
    	String nper=loanQuery.getNper()==null? "all":loanQuery.getNper();
    	JsdTotalInfoDo jsdTotalInfoDo=new JsdTotalInfoDo();
    	jsdTotalInfoDo.setEndDate(endDate);
    	jsdTotalInfoDo.setStartDate(startDate);
    	jsdTotalInfoDo.setNper(nper);
    	System.out.println(sdf.format(endDate));
		System.out.println(sdf.format(startDate));
    	jsdTotalInfoDao.getListByCommonCondition(jsdTotalInfoDo);
        loanQuery.setList(jsdTotalInfoDao.getListByCommonCondition(jsdTotalInfoDo));
        return Resp.succ(loanQuery, "");
    }
    
    
    /**
     * total:借款汇总统计
     *
     * @author zhangxinxing
     * @param loanQuery
     * @param request
     * @return
     */
    @RequestMapping(value = {"updateTotal.json"}, method = RequestMethod.POST)
    public Resp<LoanQuery> updateTotal(@RequestBody LoanQuery loanQuery, HttpServletRequest request) {
    	if(null==loanQuery.getQueryDate()){
    		throw new BizException("参数错误");
    	}
    	JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),
				ResourceSecType.JSD_RATE_INFO.name());
    	if(null==resourceDo){
    		throw new BizException("参数错误");
    	}
    	String date = DateUtil.formatDate(loanQuery.getQueryDate(),DateUtil.DEFAULT_PATTERN_WITH_HYPHEN);
    	jsdTotalInfoService.updateTotalInfo(loanQuery.getQueryDate(), date, resourceDo);
        return Resp.succ();
    }
    
    
    
    
}
