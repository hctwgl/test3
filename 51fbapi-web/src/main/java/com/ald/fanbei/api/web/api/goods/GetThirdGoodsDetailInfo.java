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
public class GetThirdGoodsDetailInfo implements ApiHandle {
    
    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);

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
        auctionListMap.put("specification","风格: 通勤通勤: 韩版组合形式: 单件裙长: 中长裙款式: 其他/other袖长: 长袖领型: V领袖型: 衬衫袖腰型: 高腰衣门襟: 拉链裙型: 一步裙图案: 纯色流行元素/工艺: 露背 系带 拉链品牌: 苋茜成分含量: 81%(含)-90%(含)材质: 涤纶适用年龄: 25-29周岁年份季节: 2017年冬季颜色分类: 紫色 黑色 姜黄色尺码: S M L\n" +
                " \t\n" +
                "￥119.00\n" +
                " \t\n" +
                "￥129.00\n" +
                " \t\n" +
                "￥139.00\n" +
                " \n" +
                " \n" +
                " \t\n" +
                "￥119.00\n" +
                " \t\n" +
                "￥129.00\n" +
                " \t\n" +
                "￥123.00\n" +
                " \n" +
                " \n" +
                " \t\n" +
                "￥119.00\n" +
                " \t\n" +
                "￥118.00\n" +
                " \t\n" +
                "￥129.00\n" +
                " \n" +
                " \n");
        auctionListMap.put("businessName","苋茜 原创女装专橱");

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
        Map<String,Object> data4 = new HashMap<>();
        data4.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        Map<String,Object> data5 = new HashMap<>();
        data5.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
        Map<String,Object> data6 = new HashMap<>();
        data6.put("deatailImg","https://img.alicdn.com/imgextra/i3/1107822861/TB2En1BaY1K.eBjSsphXXcJOXXa_!!1107822861.jpg");
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
