package com.ald.fanbei.api.web.h5.api.life;

import com.ald.fanbei.api.biz.service.AfResourceH5ItemService;
import com.ald.fanbei.api.biz.service.AfResourceH5Service;
import com.ald.fanbei.api.common.enums.BottomGoodsPageFlag;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5Dto;
import com.ald.fanbei.api.dal.domain.dto.AfResourceH5ItemDto;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查找生活首页的H5资源列表
 *
 * @author wangli
 * @date 2018/4/10 19:36
 */
@Component("h5ResourceListApi")
public class H5ResourceListApi implements H5Handle {

    // 优惠位置标识  对应H5资源中的sort
    private static final int POSITION_FLAG_DISCOUNTS = 2;

    @Autowired
    private AfResourceH5Service afResourceH5Service;

    @Autowired
    private AfResourceH5ItemService afResourceH5ItemService;

    @Override
    public H5HandleResponse process(Context context) {
        H5HandleResponse resp = new H5HandleResponse(context.getId(), FanbeiExceptionCode.SUCCESS);

        List<AfResourceH5Dto> resourceH5Dtos = afResourceH5Service
                .selectByStatus(BottomGoodsPageFlag.LIFE.getCode());
        if (CollectionUtil.isEmpty(resourceH5Dtos)) {
            logger.error("Cannot find tag:" + BottomGoodsPageFlag.LIFE.getCode() + " , please check H5Resource config.");
            return new H5HandleResponse(context.getId(), FanbeiExceptionCode.RESOURES_H5_ERROR);
        }
        resp.setResponseData(buildResourceList(resourceH5Dtos));

        return resp;
    }

    // 构建结果数据
    private List<Map<String, Object>>  buildResourceList(List<AfResourceH5Dto> resourceH5Dtos) {
        List<Map<String, Object>> result = new ArrayList<>();

        for (AfResourceH5Dto resource : resourceH5Dtos) {
            final List<AfResourceH5ItemDto> itemList = afResourceH5ItemService.selectByModelId(resource.getId());

            if (!configIsEffective(resource, itemList)) {
                continue;
            }

            Map<String, Object> e = new HashMap<>();
            e.put("floorImage", itemList.get(0).getValue3());
            e.put("type", resource.getSort());
            List<Map<String, Object>> itemMapList = CollectionConverterUtil.convertToListFromList(itemList,
                    new Converter<AfResourceH5ItemDto, Map<String, Object>>() {
                @Override
                public Map<String, Object> convert(AfResourceH5ItemDto source) {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("imgUrl", source.getValue3());
                    itemMap.put("skipUrl", source.getValue1());
                    itemMap.put("sort", source.getSort());
                    return itemMap;
                }
            });
            e.put("list", itemMapList);

            result.add(e);
        }

        return result;
    }

    // 检查资源配置是否有效
    private boolean configIsEffective(AfResourceH5Dto resource, List<AfResourceH5ItemDto> itemList) {
        // 根据约定，生活页面H5资源的第一个项应为楼层图
        if (CollectionUtil.isEmpty(itemList) || itemList.size() == 1) {
            return false;
        }
        // 爱优惠布局为1+2，维护不完整则不展示
        if (POSITION_FLAG_DISCOUNTS == resource.getSort()) {
            if (itemList.size() != 4) {
                return false;
            }
        }

        return true;
    }

}
