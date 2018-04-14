package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 页面底部商品实体
 *
 * @author wangli
 * @date 2018/4/10 15:00
 */
@Getter
@Setter
public class AfBottomGoodsDo extends AbstractSerial {

    private Long id;

    // 底部商品页面id
    private Long pageId;

    // 商品id
    private Long goodsId;

    // 排序值
    private Integer sort;

    private Integer isDelete;

    private Date gmtCreate;

    private Date gmtModified;

    private String creator;

    private String modifier;
}
