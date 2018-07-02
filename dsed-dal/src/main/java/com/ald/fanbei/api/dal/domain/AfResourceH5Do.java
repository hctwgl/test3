package com.ald.fanbei.api.dal.domain;


import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * h5资源管理实体
 * 
 * @author Jingru
 * @version 1.0.0 初始化
 * @date 2018-03-21 16:39:09
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfResourceH5Do extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Id
     */
    private Long rid;
    

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
     * 模板名称, H5页面名称
     */
    private String name;

    /**
     * 排序
     */
    private Integer sort;
    /**
     * 启用状态 0启用 1关闭
     */
    private Long status;
    private String tag;


	

 
}