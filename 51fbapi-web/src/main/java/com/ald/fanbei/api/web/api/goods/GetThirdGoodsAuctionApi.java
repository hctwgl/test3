package com.ald.fanbei.api.web.api.goods;

import com.ald.fanbei.api.biz.bo.worm.SkuBo;
import com.ald.fanbei.api.biz.bo.worm.SkuDetailBo;
import com.ald.fanbei.api.biz.bo.worm.SkuGroupBo;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.taobao.api.domain.SkuInfo;
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

        SkuBo skuBo = new SkuBo();
        skuBo.setSkuKey("尺码");

        List<SkuDetailBo> skuDetailList = new ArrayList<>();
        SkuDetailBo skuDetailBo = new SkuDetailBo();
        skuDetailBo.setSkuValue("S");
        skuDetailBo.setSkuValueAlt("该码为中国码");
        skuDetailBo.setSkuType("0");
        skuDetailBo.setSkuValueId("0");

        SkuDetailBo skuDetailBo1 = new SkuDetailBo();
        skuDetailBo1.setSkuValue("M");
        skuDetailBo1.setSkuValueAlt("该码为中国码");
        skuDetailBo1.setSkuType("0");
        skuDetailBo1.setSkuValueId("1");

        SkuDetailBo skuDetailBo2 = new SkuDetailBo();
        skuDetailBo2.setSkuValue("L");
        skuDetailBo2.setSkuValueAlt("该码为中国码");
        skuDetailBo2.setSkuType("0");
        skuDetailBo2.setSkuValueId("2");

        SkuDetailBo skuDetailBo21 = new SkuDetailBo();
        skuDetailBo21.setSkuValue("O");
        skuDetailBo21.setSkuValueAlt("该码为中国码");
        skuDetailBo21.setSkuType("0");
        skuDetailBo21.setSkuValueId("21");

        SkuDetailBo skuDetailBo22 = new SkuDetailBo();
        skuDetailBo22.setSkuValue("P");
        skuDetailBo22.setSkuValueAlt("该码为中国码");
        skuDetailBo22.setSkuType("0");
        skuDetailBo22.setSkuValueId("22");

        SkuDetailBo skuDetailBo23 = new SkuDetailBo();
        skuDetailBo23.setSkuValue("Q");
        skuDetailBo23.setSkuValueAlt("该码为中国码");
        skuDetailBo23.setSkuType("0");
        skuDetailBo23.setSkuValueId("23");

        skuDetailList.add(skuDetailBo);
        skuDetailList.add(skuDetailBo1);
        skuDetailList.add(skuDetailBo2);
        skuBo.setSkuValueList(skuDetailList);

        SkuBo skuBo1 = new SkuBo();
        skuBo1.setSkuKey("颜色");

        List<SkuDetailBo> skuDetailList1 = new ArrayList<>();
        SkuDetailBo skuDetailBo3 = new SkuDetailBo();
        skuDetailBo3.setSkuValue("https://gd3.alicdn.com/imgextra/i1/1107822861/TB26QKGaZeK.eBjSszgXXczFpXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo3.setSkuValueAlt("紫色");
        skuDetailBo3.setSkuType("1");
        skuDetailBo3.setSkuValueId("3");

        SkuDetailBo skuDetailBo4 = new SkuDetailBo();
        skuDetailBo4.setSkuValue("https://gd2.alicdn.com/imgextra/i4/1107822861/TB2Z95Ia4mI.eBjy0FlXXbgkVXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo4.setSkuValueAlt("姜黄色");
        skuDetailBo4.setSkuType("1");
        skuDetailBo4.setSkuValueId("4");

        SkuDetailBo skuDetailBo5 = new SkuDetailBo();
        skuDetailBo5.setSkuValue("https://gd1.alicdn.com/imgextra/i2/1107822861/TB2aqUlavTJXuFjSspeXXapipXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo5.setSkuValueAlt("黑色");
        skuDetailBo5.setSkuType("1");
        skuDetailBo5.setSkuValueId("5");

        SkuDetailBo skuDetailBo51 = new SkuDetailBo();
        skuDetailBo51.setSkuValue("https://gd1.alicdn.com/imgextra/i2/1107822861/TB2aqUlavTJXuFjSspeXXapipXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo51.setSkuValueAlt("黑色");
        skuDetailBo51.setSkuType("1");
        skuDetailBo51.setSkuValueId("51");

        SkuDetailBo skuDetailBo52 = new SkuDetailBo();
        skuDetailBo52.setSkuValue("https://gd1.alicdn.com/imgextra/i2/1107822861/TB2aqUlavTJXuFjSspeXXapipXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo52.setSkuValueAlt("黑色");
        skuDetailBo52.setSkuType("1");
        skuDetailBo52.setSkuValueId("52");

        SkuDetailBo skuDetailBo53 = new SkuDetailBo();
        skuDetailBo53.setSkuValue("https://gd1.alicdn.com/imgextra/i2/1107822861/TB2aqUlavTJXuFjSspeXXapipXa_!!1107822861.jpg_400x400.jpg");
        skuDetailBo53.setSkuValueAlt("黑色");
        skuDetailBo53.setSkuType("1");
        skuDetailBo53.setSkuValueId("53");

        skuDetailList1.add(skuDetailBo3);
        skuDetailList1.add(skuDetailBo4);
        skuDetailList1.add(skuDetailBo5);
        skuDetailList1.add(skuDetailBo51);
        skuDetailList1.add(skuDetailBo52);
        skuDetailList1.add(skuDetailBo53);
        skuBo1.setSkuValueList(skuDetailList1);

        SkuGroupBo skuGroupBo = new SkuGroupBo();
        skuGroupBo.setSkuGroup("0,3");
        skuGroupBo.setStockGoods("0");
        skuGroupBo.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo.setPrice(new BigDecimal("198.00"));
        skuGroupBo.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(0).getSkuValue()+
                                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(0).getSkuValueAlt());
        skuGroupBo.setSkuGroupId("379");

        SkuGroupBo skuGroupBo1 = new SkuGroupBo();
        skuGroupBo1.setSkuGroup("0,4");
        skuGroupBo1.setStockGoods("100");
        skuGroupBo1.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo1.setPrice(new BigDecimal("198.00"));
        skuGroupBo1.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(0).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(1).getSkuValueAlt());
        skuGroupBo1.setSkuGroupId("380");

        SkuGroupBo skuGroupBo2 = new SkuGroupBo();
        skuGroupBo2.setSkuGroup("0,5");
        skuGroupBo2.setStockGoods("197");
        skuGroupBo2.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo2.setPrice(new BigDecimal("198.00"));
        skuGroupBo2.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(0).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(2).getSkuValueAlt());
        skuGroupBo2.setSkuGroupId("381");

        SkuGroupBo skuGroupBo3 = new SkuGroupBo();
        skuGroupBo3.setSkuGroup("1,3");
        skuGroupBo3.setStockGoods("197");
        skuGroupBo3.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo3.setPrice(new BigDecimal("198.00"));
        skuGroupBo3.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(1).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(0).getSkuValueAlt());
        skuGroupBo3.setSkuGroupId("382");

        SkuGroupBo skuGroupBo4 = new SkuGroupBo();
        skuGroupBo4.setSkuGroup("1,4");
        skuGroupBo4.setStockGoods("197");
        skuGroupBo4.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo4.setPrice(new BigDecimal("198.00"));
        skuGroupBo4.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(1).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(1).getSkuValueAlt());
        skuGroupBo4.setSkuGroupId("383");

        SkuGroupBo skuGroupBo5 = new SkuGroupBo();
        skuGroupBo5.setSkuGroup("1,5");
        skuGroupBo5.setStockGoods("197");
        skuGroupBo5.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo5.setPrice(new BigDecimal("198.00"));
        skuGroupBo5.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(1).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(2).getSkuValueAlt());
        skuGroupBo5.setSkuGroupId("384");

        SkuGroupBo skuGroupBo6 = new SkuGroupBo();
        skuGroupBo6.setSkuGroup("2,3");
        skuGroupBo6.setStockGoods("197");
        skuGroupBo6.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo6.setPrice(new BigDecimal("198.00"));
        skuGroupBo6.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(2).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(0).getSkuValueAlt());
        skuGroupBo6.setSkuGroupId("385");

        SkuGroupBo skuGroupBo7 = new SkuGroupBo();
        skuGroupBo7.setSkuGroup("2,4");
        skuGroupBo7.setStockGoods("197");
        skuGroupBo7.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo7.setPrice(new BigDecimal("198.00"));
        skuGroupBo7.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(2).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(1).getSkuValueAlt());
        skuGroupBo7.setSkuGroupId("386");

        SkuGroupBo skuGroupBo8 = new SkuGroupBo();
        skuGroupBo8.setSkuGroup("2,5");
        skuGroupBo8.setStockGoods("197");
        skuGroupBo8.setCoutPrice(new BigDecimal("368.00"));
        skuGroupBo8.setPrice(new BigDecimal("198.00"));
        skuGroupBo8.setSkuGroupValue(skuBo.getSkuKey()+":"+skuBo.getSkuValueList().get(2).getSkuValue()+
                skuBo1.getSkuKey()+":"+skuBo1.getSkuValueList().get(2).getSkuValueAlt());
        skuGroupBo8.setSkuGroupId("387");


        //sku项组合
        List<Object> skuGroupList = new ArrayList<>();
        skuGroupList.add(skuGroupBo);
        skuGroupList.add(skuGroupBo1);
        skuGroupList.add(skuGroupBo2);
        skuGroupList.add(skuGroupBo3);
        skuGroupList.add(skuGroupBo4);
        skuGroupList.add(skuGroupBo5);
        skuGroupList.add(skuGroupBo6);
        skuGroupList.add(skuGroupBo7);
        skuGroupList.add(skuGroupBo8);

        List<SkuBo> skuBos = new ArrayList<>();
        skuBos.add(skuBo);
        skuBos.add(skuBo1);
        auctionListMap.put("skuList",skuBos);
        auctionListMap.put("skuGroupList",skuGroupList);

        resp.setResponseData(auctionListMap);
        return resp;
    }
}
