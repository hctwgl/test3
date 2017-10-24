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
 * @ClassName GetThirdGoodsDetailInfo.
 * @desc
 * 获取第三方自建商品详情
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/12 15:52
 */
@Component("getThirdGoodsDetailInfoApi")
public class GetThirdGoodsDetailInfoApi implements ApiHandle {
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);        Long userId = context.getUserId();

        //基本数据对象map
        Map<String, Object> auctionListMap = new HashMap<>();

        //商品标题
        auctionListMap.put("goodsName","苋茜 2017冬装新款韩版性感露背显胸V领系带修身收腰包臀连衣裙女");
        auctionListMap.put("saleCount","111");
        auctionListMap.put("source","TMALL");
        auctionListMap.put("rebateAmount",new BigDecimal("10"));
        auctionListMap.put("monthAmout",new BigDecimal("100"));
        auctionListMap.put("freeNper",3);
        auctionListMap.put("saleAmount",new BigDecimal("198"));
        auctionListMap.put("realAmount",new BigDecimal("150"));
        auctionListMap.put("goodsId","110543");

        HashMap<String,Object> specificationMap = new HashMap<>();
        specificationMap.put("specificationKey","风格");
        specificationMap.put("specificationValue","通勤");

        HashMap<String,Object> specificationMap1 = new HashMap<>();
        specificationMap1.put("specificationKey","通勤");
        specificationMap1.put("specificationValue","韩版");

        HashMap<String,Object> specificationMap2 = new HashMap<>();
        specificationMap2.put("specificationKey","组合形式");
        specificationMap2.put("specificationValue","单件");

        HashMap<String,Object> specificationMap3 = new HashMap<>();
        specificationMap3.put("specificationKey","裙长");
        specificationMap3.put("specificationValue","中长裙");

        HashMap<String,Object> specificationMap4 = new HashMap<>();
        specificationMap4.put("specificationKey","款式");
        specificationMap4.put("specificationValue","其他/other");

        HashMap<String,Object> specificationMap5 = new HashMap<>();
        specificationMap5.put("specificationKey","袖长");
        specificationMap5.put("specificationValue","长袖");

        HashMap<String,Object> specificationMap6 = new HashMap<>();
        specificationMap6.put("specificationKey","领型");
        specificationMap6.put("specificationValue","V领");

        HashMap<String,Object> specificationMap7 = new HashMap<>();
        specificationMap7.put("specificationKey","适用年龄");
        specificationMap7.put("specificationValue","25-29周岁");

        HashMap<String,Object> specificationMap8 = new HashMap<>();
        specificationMap8.put("specificationKey","成分含量");
        specificationMap8.put("specificationValue","81%(含)-90%(含)");

        HashMap<String,Object> specificationMap9 = new HashMap<>();
        specificationMap9.put("specificationKey","尺码");
        specificationMap9.put("specificationValue","S M L");
        
        HashMap<String,Object> specificationMap10 = new HashMap<>();
        specificationMap10.put("specificationKey","尺码");
        specificationMap10.put("specificationValue","颜色分类: 紫色 黑色 姜黄色");

        List<Object> specificationList = new ArrayList<>();
        specificationList.add(specificationMap);
        specificationList.add(specificationMap1);
        specificationList.add(specificationMap2);
        specificationList.add(specificationMap3);
        specificationList.add(specificationMap4);
        specificationList.add(specificationMap5);
        specificationList.add(specificationMap6);
        specificationList.add(specificationMap7);
        specificationList.add(specificationMap8);
        specificationList.add(specificationMap9);

        auctionListMap.put("specificationList",specificationList);

        //商品主图
        Map<String, Object> data = new HashMap<>();
        data.put("goodsMainImg","https://gd4.alicdn.com/imgextra/i4/1107822861/TB2nJXoXfBNTKJjy0FdXXcPpVXa_!!1107822861.jpg_400x400.jpg_.webp");
        data.put("goodsDeputyImg","https://gd4.alicdn.com/imgextra/i4/1107822861/TB2nJXoXfBNTKJjy0FdXXcPpVXa_!!1107822861.jpg_50x50.jpg_.webp");
        Map<String, Object> data1 = new HashMap<>();
        data1.put("goodsMainImg","https://gd4.alicdn.com/imgextra/i4/1107822861/TB2nJXoXfBNTKJjy0FdXXcPpVXa_!!1107822861.jpg_400x400.jpg_.webp");
        data1.put("goodsDeputyImg","https://gd4.alicdn.com/imgextra/i4/1107822861/TB2nJXoXfBNTKJjy0FdXXcPpVXa_!!1107822861.jpg_50x50.jpg_.webp");
        List<Object> goodsImgList = new ArrayList<>();
        goodsImgList.add(data);
        goodsImgList.add(data1);

        Map<String,Object> data3 = new HashMap<>();
        data3.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        data3.put("width","400");
        data3.put("height","400");
        Map<String,Object> data4 = new HashMap<>();
        data4.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        data4.put("width","400");
        data4.put("height","400");
        Map<String,Object> data5 = new HashMap<>();
        data5.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        data5.put("width","400");
        data5.put("height","400");
        Map<String,Object> data6 = new HashMap<>();
        data6.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        data6.put("width","400");
        data6.put("height","400");
        List<Object> detailImgList = new ArrayList<>();
        detailImgList.add(data3);
        detailImgList.add(data4);
        detailImgList.add(data5);
        detailImgList.add(data6);

        auctionListMap.put("goodsImgList",goodsImgList);
        auctionListMap.put("detailImgList",detailImgList);
        resp.setResponseData(auctionListMap);
        return resp;
    }
}
