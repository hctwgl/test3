package com.ald.jsd.mgr.biz.service;

import com.ald.jsd.mgr.web.dto.resp.Resp;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface MgrOfflineRepaymentService {

    Resp dealOfflineRepayment(Map<String,String> data);
}
