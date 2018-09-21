package com.ald.jsd.mgr.biz.service;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public interface MgrOfflineRepaymentService {

    void dealOfflineRepayment(Map<String,String> data);
}
