package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfTaskDo;
import lombok.Getter;
import lombok.Setter;


/**
 * 分类运营位配置实体
 * 
 * @author chefeipeng
 * @version 1.0.0 初始化
 * @date 2018-05-08 14:44:04
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
 public class AfTaskDto extends AfTaskDo{


      private String receiveReward;

      private Integer sumTaskCondition;

      private Integer finishTaskCondition;

      private String rewardName;
}