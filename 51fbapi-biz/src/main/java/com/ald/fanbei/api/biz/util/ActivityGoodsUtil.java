package com.ald.fanbei.api.biz.util;

import com.ald.fanbei.api.biz.service.AfSeckillActivityService;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.dal.domain.AfSeckillActivityDo;
import com.ald.fanbei.api.dal.domain.dto.AfActGoodsDto;
import com.ald.fanbei.api.dal.domain.query.AfSeckillActivityQuery;
import org.apache.commons.beanutils.BeanMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component("activityGoodsUtil")
public class ActivityGoodsUtil {
    @Resource
    AfSeckillActivityService afSeckillActivityService;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    public List<Map<String, Object>> getActivityGoods(AfSeckillActivityQuery query){
        List<Map<String, Object>> activityInfoList = new ArrayList<Map<String, Object>>();
        try{
            List<AfSeckillActivityDo> afSeckillActivityDos = afSeckillActivityService.getActivityList(query);
            for(AfSeckillActivityDo afSeckillActivityDo:afSeckillActivityDos){
                Map<String, Object> activityData = CollectionConverterUtil.convertObjToMap(afSeckillActivityDo);
                Long actId = afSeckillActivityDo.getRid();
                List<AfActGoodsDto> afActGoodsDtos = afSeckillActivityService.getActivityGoodsByGoodsIdsAndActId(query.getGoodsIdList(),actId);
                List<Map<String, Object>> goodsList = new ArrayList<Map<String, Object>>();
                for (AfActGoodsDto goodsDo : afActGoodsDtos) {
                    BigDecimal specialPrice = goodsDo.getSpecialPrice();
                    BigDecimal secKillRebAmount = specialPrice.multiply(goodsDo.getRebateRate()).setScale(2,BigDecimal.ROUND_HALF_UP);
                    if(goodsDo.getRebateAmount().compareTo(secKillRebAmount)>0){
                        goodsDo.setRebateAmount(secKillRebAmount);
                    }
                    Map<String, Object> goodsInfo = new BeanMap(goodsDo);
                    goodsList.add(goodsInfo);
                }
                activityData.put("goodsList", goodsList);
                activityInfoList.add(activityData);
            }
        }catch (Exception e){
            logger.info("getActivityGoods error for" + e);
        }

        return activityInfoList;
    }
}
