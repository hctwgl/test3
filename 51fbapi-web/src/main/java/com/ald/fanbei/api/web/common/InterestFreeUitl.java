package com.ald.fanbei.api.web.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.AfInterestFreeRulesService;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceRulesDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceSchemeDo;
import org.apache.commons.lang.StringUtils;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.InterestfreeCode;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.SpringBeanContextUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * 分期业务封装处理工具类
 * @Desc
 * @author hantao
 */
public class InterestFreeUitl {
    public static AfResourceService afResourceService;


    /**
     * 计算分期、免息规则封装结果
     *
     * @param array             分期规则json集合
     * @param interestFreeArray 免期规则json集合
     * @param goodsNum          商品数量
     * @param goodsAmount       商品原价
     * @param value1            手续费率
     * @param value2            手续费上下限预设值
     * @return
     */
    public static List<Map<String, Object>> getConsumeList(JSONArray array, JSONArray interestFreeArray, int goodsNum, BigDecimal goodsAmount, String value1, String value2,Long goodsid,String method) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        if (goodsid != null && goodsid >0l){
            afResourceService = (AfResourceService)SpringBeanContextUtil.getBean("afResourceService");
            AfResourceDo resource1 = afResourceService.getBrandRate(goodsid);//资源配置中的品牌利率
            if(resource1!=null){

                if ("1".equals(method)){
                    Set<String> set = new HashSet<>();
                    JSONArray temparray = JSON.parseArray(resource1.getValue());
                    for (Object temp1:array){
                        JSONObject tempobj1 = (JSONObject)temp1;
                        set.add(tempobj1.getString("nper"));
                    }
                    JSONArray arr = new JSONArray();
                    for (Object temp:temparray){
                        JSONObject tempobj = (JSONObject)temp;
                        String nper = tempobj.getString("nper");
                        if (set.contains(nper)){
                            arr.add(tempobj);
                        }
                    }
                    array = arr;
                }else{
                    array = JSON.parseArray(resource1.getValue());
                }

            }
        /*    afInterestReduceGoodsService = (AfInterestReduceGoodsService)SpringBeanContextUtil.getBean("afInterestReduceGoodsService");

            	JSONArray newArray = afInterestReduceGoodsService.checkIfReduce(goodsid);
                if (newArray != null) {
                	array = newArray;

			}*/


        }
        if (array == null) {
            throw new FanbeiException(FanbeiExceptionCode.BORROW_CONSUME_NOT_EXIST_ERROR);
        }
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            Map<String, Object> attrs = new HashMap<String, Object>();
            String key = obj.getString(Constants.DEFAULT_NPER);//分期数
            String value = obj.getString(Constants.DEFAULT_RATE);//分期利率
            if (StringUtils.isBlank(value)) {
                return list;
            }
            //每期还款金额
            BigDecimal amount = null;
            BigDecimal totalGoodsAmount = goodsAmount.multiply(new BigDecimal(goodsNum));//商品总金额
            //手续费上限
            BigDecimal rangeBegin = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MIN, BigDecimal.ZERO);
            //手续费下限
            BigDecimal rangeEnd = NumberUtil.objToBigDecimalDefault(Constants.DEFAULT_CHARGE_MAX, BigDecimal.ZERO);
            //获取手续费上下限预设值
            String[] range = StringUtil.split(value2, ",");
            BigDecimal nPer = new BigDecimal(key);//分期数
            BigDecimal nPerRate = new BigDecimal(value);//分期利率
            if (null != range && range.length == 2) {
                rangeBegin = NumberUtil.objToBigDecimalDefault(range[0], BigDecimal.ZERO);
                rangeEnd = NumberUtil.objToBigDecimalDefault(range[1], BigDecimal.ZERO);
            }
            BigDecimal poundageRate = new BigDecimal(value1);//手续费率

            BigDecimal mouthRate = nPerRate.divide(new BigDecimal(Constants.MONTH_OF_YEAR), 8,
                    BigDecimal.ROUND_HALF_UP);//月利率
            JSONObject interestFreeObject =null;
            if (interestFreeArray != null&&interestFreeArray.size()>0) {
                for (int j=0;j<interestFreeArray.size();j++) {
                    JSONObject item = interestFreeArray.getJSONObject(j);
                    if(item.getString(Constants.DEFAULT_NPER).equals(key)){ // 免息规则有这样的分期数
                        interestFreeObject = item;
                        break;
                    }
                }
                if(interestFreeObject!=null){
                    String freeNper = interestFreeObject.getString(Constants.DEFAULT__FREENPER);//免期数
                    BigDecimal freeNperB = new BigDecimal(freeNper);//免期数BigDecimal
                    String fNper = interestFreeObject.getString(Constants.DEFAULT_NPER);//带免息的期数
                    if(fNper.equals(key)){
                        if(freeNper.equals("0")){
                            //总手续费
                            attrs = getAttrs(totalGoodsAmount,nPer,poundageRate,rangeBegin,rangeEnd,mouthRate);
                            list.add(attrs);
                            continue;
                        }

                        //总手续费
                        BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(totalGoodsAmount, nPer.intValue(),
                                poundageRate, rangeBegin, rangeEnd,InterestfreeCode.IS_FREE.getCode());
                        //本金/总期数
                        BigDecimal b1 = BigDecimalUtil.divHalfDown(totalGoodsAmount, nPer, Constants.HALFUP_DIGIT);
                        if (freeNper.equals(key)) {
                            amount = b1;
                            attrs.put("nper", key);
                            attrs.put("amount", BigDecimal.ZERO);
                            attrs.put("poundageAmount", BigDecimal.ZERO);
                            attrs.put("freeNper", freeNper);
                            attrs.put("freeAmount", amount);
                            attrs.put("totalAmount", totalGoodsAmount);
                            attrs.put("isFree", InterestfreeCode.IS_FREE.getCode());
                            list.add(attrs);
                            continue;
                        } else {
                            //计息期
                            BigDecimal noNper = nPer.subtract(freeNperB);
                            //本金*每期利率
                            BigDecimal b2 = BigDecimalUtil.multiply(totalGoodsAmount,mouthRate);
                            //免期前每期手续费
                            BigDecimal b3 = totalPoundage.divide(nPer,4,BigDecimal.ROUND_HALF_EVEN);

                            //手续费最小值<总手续费-免息期数*手续费<手续费最大值)/期数
                            BigDecimal d1 = totalPoundage.subtract(b3.multiply(freeNperB));
                            if (rangeBegin.compareTo(d1) > 0) {
                                d1 = rangeBegin;
                            } else if (d1.compareTo(rangeEnd) > 0) {
                                d1 = rangeEnd;
                            }
                            BigDecimal d2 = BigDecimalUtil.divHalfUp(d1,noNper,Constants.HALFUP_DIGIT); //免息后每期手续费

                            amount = b1.add(b2).add(d2);
                            //计算免息
                            BigDecimal freeAmount = b1;
                            BigDecimal totalAmount = amount.multiply(noNper).add(freeAmount.multiply(freeNperB));
                            //BigDecimal poundageAmount = (amount.subtract(freeAmount)).multiply(noNper);
                            BigDecimal poundageAmount = noNper.multiply(b2.add(d2));
                            attrs.put("nper", nPer);
                            attrs.put("amount", amount);
                            attrs.put("poundageAmount", poundageAmount);
                            attrs.put("totalAmount", totalAmount);
                            attrs.put("freeNper", freeNper);
                            attrs.put("freeAmount", freeAmount);
                            attrs.put("isFree", InterestfreeCode.HALF_FREE.getCode());
                            list.add(attrs);
                            continue;
                        }
                    }else{
                        attrs = getAttrs(totalGoodsAmount,nPer,poundageRate,rangeBegin,rangeEnd,mouthRate);
                        list.add(attrs);
                        continue;
                    }
                }else{
                    attrs = getAttrs(totalGoodsAmount,nPer,poundageRate,rangeBegin,rangeEnd,mouthRate);
                    list.add(attrs);
                    continue;
                }
            } else {
                attrs = getAttrs(totalGoodsAmount,nPer,poundageRate,rangeBegin,rangeEnd,mouthRate);
                list.add(attrs);
                continue;
            }
        }
        return list;
    }
    public static JSONArray getreducenpers(AfInterestReduceRulesDo afInterestReduceRulesDo){
        JSONArray array = new JSONArray();
        BigDecimal nper1 = afInterestReduceRulesDo.getNper1();
        BigDecimal nper2 = afInterestReduceRulesDo.getNper2();
        BigDecimal nper3 = afInterestReduceRulesDo.getNper3();
        BigDecimal nper6 = afInterestReduceRulesDo.getNper6();
        BigDecimal nper9 = afInterestReduceRulesDo.getNper9();
        BigDecimal nper12 = afInterestReduceRulesDo.getNper12();
        Map<String,Object> temp1 = new HashMap<>();
        temp1.put("rate",nper1);
        temp1.put(Constants.DEFAULT_NPER,1);
        Map<String,Object> temp2 = new HashMap<>();
        temp2.put("rate",nper2);
        temp2.put(Constants.DEFAULT_NPER,2);
        Map<String,Object> temp3 = new HashMap<>();
        temp3.put("rate",nper3);
        temp3.put(Constants.DEFAULT_NPER,3);
        Map<String,Object> temp6 = new HashMap<>();
        temp6.put("rate",nper6);
        temp6.put(Constants.DEFAULT_NPER,6);
        Map<String,Object> temp9 = new HashMap<>();
        temp9.put("rate",nper9);
        temp9.put(Constants.DEFAULT_NPER,9);
        Map<String,Object> temp12 = new HashMap<>();
        temp12.put("rate",nper12);
        temp12.put(Constants.DEFAULT_NPER,12);
        array.add(temp1);
        array.add(temp2);
        array.add(temp3);
        array.add(temp6);
        array.add(temp9);
        array.add(temp12);

        return array;
    }

    /**
     *
     * @param totalGoodsAmount
     * @param nPer
     * @param poundageRate
     * @param rangeBegin
     * @param rangeEnd
     * @param mouthRate
     * @return
     */
    private static Map<String, Object> getAttrs(BigDecimal totalGoodsAmount, BigDecimal nPer, BigDecimal poundageRate,
                                                BigDecimal rangeBegin, BigDecimal rangeEnd, BigDecimal mouthRate){
        Map<String, Object> attrs = new HashMap<>();

        //本金/总期数
        BigDecimal b1 = BigDecimalUtil.divHalfDown(totalGoodsAmount, nPer, Constants.HALFUP_DIGIT);

        //本金*每期利率   每一期利息
        BigDecimal b2 = BigDecimalUtil.multiply(totalGoodsAmount,mouthRate);

        //总手续费
        BigDecimal totalPoundage = BigDecimalUtil.getTotalPoundage(totalGoodsAmount, nPer.intValue(),
                poundageRate, rangeBegin, rangeEnd,InterestfreeCode.NO_FREE.getCode());

        //每期手续费
        BigDecimal b3 = BigDecimalUtil.divHalfUp(totalPoundage, nPer, Constants.HALFUP_DIGIT);

        // 每一期应还的总钱数
        BigDecimal amount = b1.add(b2).add(b3);
        //借款总金额
        BigDecimal totalAmount = amount.multiply(nPer);
        //总利息+手续费
        BigDecimal poundageAmount = nPer.multiply(b2.add(b3));
        attrs.put("nper", nPer.toString());
        attrs.put("amount", amount);
        attrs.put("poundageAmount", poundageAmount);
        attrs.put("totalAmount", totalAmount);
        attrs.put("freeNper", BigDecimal.ZERO.toString());
        attrs.put("freeAmount", BigDecimal.ZERO);
        attrs.put("isFree", InterestfreeCode.NO_FREE.getCode());

        return attrs;
    }
}
