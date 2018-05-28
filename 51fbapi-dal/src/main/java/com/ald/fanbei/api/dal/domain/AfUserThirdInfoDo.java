package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 实体
 *
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfUserThirdInfoDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    
    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户在第三方的id
     */
    private String thirdId;

    /**
     * 第三方类型，WX：微信
     */
    private String thirdType;

    /**
     * 第三方信息，json字符串
     */
    private String thirdInfo;


    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 添加者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;

    private String userName;

}