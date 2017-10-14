package com.ald.fanbei.api.web.api.goods;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GetThirdGoodsAuctionApi.
 * @desc
 * 获取第三方自建商品规则参数详情
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/12 15:52
 */
@Component("getThirdGoodsAuctionApi")
public class GetThirdGoodsAuctionApi implements ApiHandle {
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);

        //基本数据对象map
        Map<String, Object> auctionListMap = new HashMap<>();

        Map<String, Object> data = new HashMap<>();
        data.put("auctionkey","尺码");

        Map<String, Object> auctionValueMap = new HashMap<>();
        auctionValueMap.put("auctionValue","S");
        auctionValueMap.put("auctionValueAlt","该码为中国码");
        auctionValueMap.put("auctionValueId","0");

        Map<String, Object> auctionValueMap1 = new HashMap<>();
        auctionValueMap1.put("auctionValue","M");
        auctionValueMap1.put("auctionValueAlt","该码为中国码");
        auctionValueMap1.put("auctionValueId","1");

        Map<String, Object> auctionValueMap2 = new HashMap<>();
        auctionValueMap2.put("auctionValue","L");
        auctionValueMap2.put("auctionValueAlt","该码为中国码");
        auctionValueMap2.put("auctionValueId","2");

        data.put("auctionkey","尺码");
        List<Object> auctionValueList1 = new ArrayList<>();
        auctionValueList1.add(auctionValueMap);
        auctionValueList1.add(auctionValueMap);
        auctionValueList1.add(auctionValueMap);

        data.put("auctionValueList",auctionValueList1);
        data.put("type","0");


        Map<String, Object> data2 = new HashMap<>();
        data2.put("auctionkey","颜色");

        Map<String, Object> auctionValueMap3 = new HashMap<>();
        auctionValueMap3.put("auctionValue","https://gd3.alicdn.com/imgextra/i1/1107822861/TB26QKGaZeK.eBjSszgXXczFpXa_!!1107822861.jpg_30x30.jpg");
        auctionValueMap3.put("auctionValueAlt","紫色");
        auctionValueMap3.put("auctionValueId","3");

        Map<String, Object> auctionValueMap4 = new HashMap<>();
        auctionValueMap4.put("auctionValue","https://gd2.alicdn.com/imgextra/i4/1107822861/TB2Z95Ia4mI.eBjy0FlXXbgkVXa_!!1107822861.jpg_30x30.jpg");
        auctionValueMap4.put("auctionValueAlt","姜黄色");
        auctionValueMap4.put("auctionValueId","4");

        Map<String, Object> auctionValueMap5 = new HashMap<>();
        auctionValueMap5.put("auctionValue","https://gd1.alicdn.com/imgextra/i2/1107822861/TB2aqUlavTJXuFjSspeXXapipXa_!!1107822861.jpg_30x30.jpg");
        auctionValueMap5.put("auctionValueAlt","黑色");
        auctionValueMap5.put("auctionValueId","5");

        List<Object> auctionValueList2 = new ArrayList<>();
        auctionValueList2.add(auctionValueMap3);
        auctionValueList2.add(auctionValueMap4);
        auctionValueList2.add(auctionValueMap5);

        data2.put("auctionValueList",auctionValueList2);
        data2.put("type","1");

        List<Object> list = new ArrayList<>();
        list.add(data);
        list.add(data2);
        auctionListMap.put("auctionList",list);

        //sku项组合
        Map<String, Object> skuGroup = new HashMap<>();
        skuGroup.put("skuGroup","0,3");
        skuGroup.put("stockGoods","132");
        skuGroup.put("costPrice",new BigDecimal("368.00"));
        skuGroup.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup1 = new HashMap<>();
        skuGroup1.put("skuGroup","0,4");
        skuGroup1.put("stockGoods","113");
        skuGroup1.put("costPrice",new BigDecimal("368.00"));
        skuGroup1.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup2 = new HashMap<>();
        skuGroup2.put("skuGroup","0,5");
        skuGroup2.put("stockGoods","166");
        skuGroup2.put("costPrice",new BigDecimal("368.00"));
        skuGroup2.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup3 = new HashMap<>();
        skuGroup3.put("skuGroup","1,3");
        skuGroup3.put("stockGoods","177");
        skuGroup3.put("costPrice",new BigDecimal("368.00"));
        skuGroup3.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup4 = new HashMap<>();
        skuGroup4.put("skuGroup","1,4");
        skuGroup4.put("stockGoods","188");
        skuGroup4.put("costPrice",new BigDecimal("368.00"));
        skuGroup4.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup5 = new HashMap<>();
        skuGroup5.put("skuGroup","1,5");
        skuGroup5.put("stockGoods","187");
        skuGroup5.put("costPrice",new BigDecimal("368.00"));
        skuGroup5.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup6 = new HashMap<>();
        skuGroup6.put("skuGroup","2,3");
        skuGroup6.put("stockGoods","185");
        skuGroup6.put("costPrice",new BigDecimal("368.00"));
        skuGroup6.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup7 = new HashMap<>();
        skuGroup7.put("skuGroup","2,4");
        skuGroup7.put("stockGoods","183");
        skuGroup7.put("costPrice",new BigDecimal("368.00"));
        skuGroup7.put("price",new BigDecimal("129.00"));

        Map<String, Object> skuGroup8 = new HashMap<>();
        skuGroup8.put("skuGroup","2,5");
        skuGroup8.put("stockGoods","197");
        skuGroup8.put("costPrice",new BigDecimal("368.00"));
        skuGroup8.put("price",new BigDecimal("129.00"));

        List<Object> skuGroupList = new ArrayList<>();
        skuGroupList.add(skuGroup);
        skuGroupList.add(skuGroup1);
        skuGroupList.add(skuGroup2);
        skuGroupList.add(skuGroup3);
        skuGroupList.add(skuGroup4);
        skuGroupList.add(skuGroup5);
        skuGroupList.add(skuGroup6);
        skuGroupList.add(skuGroup7);
        skuGroupList.add(skuGroup8);

        auctionListMap.put("skuGroupList",skuGroupList);

        resp.setResponseData(auctionListMap);
        return resp;
    }
}
