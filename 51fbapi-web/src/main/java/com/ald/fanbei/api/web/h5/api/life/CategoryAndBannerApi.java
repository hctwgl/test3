package com.ald.fanbei.api.web.h5.api.life;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfShopService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfShopDo;
import com.ald.fanbei.api.dal.domain.query.AfShopQuery;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.ald.fanbei.api.web.vo.AfShopVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查找生活首页分类和banner列表
 *
 * @author wangli
 * @date 2018/4/10 18:02
 */
@Component("categoryAndBannerApi")
public class CategoryAndBannerApi implements H5Handle {

    private static final int SHOP_NUM = 10;

    @Autowired
    private AfShopService afShopService;

    @Autowired
    private AfResourceService afResourceService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

        Map<String, Object> data = new HashMap<>();
        data.put("shopList", findShopList());
        data.put("bannerList", findBannerList());
        resp.setResponseData(data);

        return resp;
    }

    // 查找分类列表
    private List<AfShopVo> findShopList() {
        List<AfShopVo> result = new ArrayList<>();

        AfShopQuery query = new AfShopQuery();
        query.setPageNo(1);
        query.setPageSize(SHOP_NUM);
        List<AfShopDo> shopList = afShopService.getShopList(query);

        if (CollectionUtil.isNotEmpty(shopList)) {
            result = CollectionConverterUtil.convertToListFromList(shopList, new Converter<AfShopDo, AfShopVo>() {
                @Override
                public AfShopVo convert(AfShopDo source) {
                    AfShopVo vo = new AfShopVo();
                    vo.setRid(source.getRid());
                    vo.setName(source.getName());
                    vo.setType(source.getType());
                    vo.setShopUrl(source.getShopUrl());
                    vo.setIcon(source.getNewIcon());
                    return vo;
                }
            });
        }

        return result;
    }

    // 查找banner列表
    private List<Map<String, Object>> findBannerList() {
        List<Map<String, Object>> result = new ArrayList<>();

        List<AfResourceDo> bannerList = null;
        String invelomentType = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
        if (Constants.INVELOMENT_TYPE_ONLINE.equals(invelomentType)
                || Constants.INVELOMENT_TYPE_TEST.equals(invelomentType)) {
            bannerList = afResourceService
                    .getResourceHomeListByTypeOrderBy(AfResourceType.GGHomeTopBanner.getCode());
        } else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(invelomentType)){
            bannerList = afResourceService
                    .getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.GGHomeTopBanner.getCode());
        }

        if (CollectionUtil.isNotEmpty(bannerList)) {
            result = CollectionConverterUtil.convertToListFromList(bannerList,
                    new Converter<AfResourceDo, Map<String, Object>>() {
                @Override
                public Map<String, Object> convert(AfResourceDo source) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("imageUrl", source.getValue());
                    map.put("type", source.getValue1());
                    map.put("content", source.getValue2());
                    map.put("sort", source.getSort());
                    return map;
                }
            });
        }

        return result;
    }
}
