package com.ald.fanbei.api.biz.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 退货变更的债权信息vo
 * @author wujun
 * @version 1.0.0 初始化
 * @date 2017年12月15日下午4:42:20
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class MgrTrendTodayInfoVo implements Serializable {
	
	private static final long serialVersionUID = 4204652534348461359L;
	private ArrayList<Map<Integer,Integer>> loanNumPerHourToday;//每小时的放款笔数

	private ArrayList<Map<Integer,Integer>> pvPerHourToday;//每小时的访问量


}
