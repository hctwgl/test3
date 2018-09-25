package com.ald.jsd.mgr.biz.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.common.enums.JsdRepayType;

@Service
public interface MgrOfflineRepaymentService {

    void dealOfflineRepayment(Map<String,String> data, JsdRepayType repayType, String realName);
}
