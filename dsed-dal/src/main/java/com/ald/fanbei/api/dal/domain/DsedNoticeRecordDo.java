package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 通知任务实体
 * 
 * @author jilong
 * @version 1.0.0 初始化
 * @date 2018-06-19 09:40:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class DsedNoticeRecordDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
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
     * 用户id
     */
    private Long userId;

    /**
     * 业务类型
     */
    private String type;

    /**
     * 关联id
     */
    private String refId;

    /**
     * 打款状态【SUCCESS:成功 FAIL:失败】
     */
    private String status;

    /**
     * 剩余通知次数
     */
    private String times;

    private String params;

}