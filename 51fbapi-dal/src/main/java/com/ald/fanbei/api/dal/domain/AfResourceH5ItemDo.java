package com.ald.fanbei.api.dal.domain;


import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 *h5商品资源管理实体
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:41:12
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
 public class AfResourceH5ItemDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long id;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 最后修改者
     */
    private String modifier;

    /**
     * 模板id
     */
    private Long modelId;


    private String name;

    /**
     * 跳转链接
     */
    private String value1;

    /**
     * 商品id
     */
    private String value2;

    /**
     * 图片链接
     */
    private String value3;

    /**
     * 
     */
    private String value4;
    

    /**
     * 排序
     */
    private Long sort;




	
}