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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName GetGoodsRateInfo.
 * @desc
 * 获取第三方商品评论详情
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/12 14:20
 */
@Component("getGoodsRateInfoApi")
public class GetGoodsRateInfo implements ApiHandle {

    /**第三方商品自建-评论内容**/
    public String content = "content";
    /**第三方商品自建-用户名**/
    public String userName = "userName";
    /**第三方商品自建-评论类型 0 普通评论 1 图文评论 2 追评评论**/
    public String type = "type";
    /**第三方商品自建-店主评论内容**/
    public String reply = "reply";
    /**第三方商品自建-评论时间**/
    public String rateTime = "rateTime";
    /**第三方商品自建-评论商品规格参数**/
    public String auctionSku = "auctionSku";
    /**第三方商品自建-评论图片**/
    public String picUrl = "picUrl";
    /**第三方商品自建-评论图片宽度**/
    public String width = "width";
    /**第三方商品自建-评论图片高度**/
    public String height = "height";

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("goodsId")), 0l);
        
        Long page = NumberUtil.objToLongDefault(ObjectUtils.toString(requestDataVo.getParams().get("page")), 0l);
        
        //基本数据对象map
        Map<String, Object> ratedataListMap = new HashMap<>();

        List<Object> ratedataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put(content,"紫的这件我超级满意的，特别显身材，第一次尝试紫色原来也什么肤色都可以驾驭哦！这件我妈很挑剔都说好看！");
            data.put(userName,"已***8（匿名）");
            data.put(type,"1");
            data.put(reply,"没有一点点防备，也没有一丝顾虑，您就这样出现在我的世界里，带给我惊喜，情不自己~您的惊喜是有愉快的购物体验，芝华仕伴你同行~ 　　　　————芝华仕官方旗舰店");
            data.put(rateTime,"2017-10-06 14:29:03");
            data.put(auctionSku,"颜色分类：紫色  尺码：S");
            
            Map<String,Object> rateDetail1 = new HashMap<>();
            rateDetail1.put(picUrl,"https://img.alicdn.com/imgextra/i1/0/TB2BtIUaPlxYKJjSZFuXXaYlVXa_!!0-rate.jpg_400x400.jpg");
            Map<String,Object> rateDetail2 = new HashMap<>();
            rateDetail2.put(picUrl,"https://img.alicdn.com/imgextra/i3/0/TB2JEVCdfBNTKJjy1zdXXaScpXa_!!0-rate.jpg_400x400.jpg");
            Map<String,Object> rateDetail3 = new HashMap<>();
            rateDetail3.put(picUrl,"https://img.alicdn.com/imgextra/i3/0/TB2JEVCdfBNTKJjy1zdXXaScpXa_!!0-rate.jpg_400x400.jpg");
            List<Object> rateDetailList = new ArrayList<>();
            rateDetailList.add(rateDetail1);
            rateDetailList.add(rateDetail2);
            rateDetailList.add(rateDetail3);
            data.put("rateDetail",rateDetailList);
            ratedataList.add(data);
        }
        ratedataListMap.put("ratedataList",ratedataList);

        resp.setResponseData(ratedataListMap);
        return resp;
    }
}
