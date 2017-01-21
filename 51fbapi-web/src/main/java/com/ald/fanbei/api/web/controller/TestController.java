package com.ald.fanbei.api.web.controller;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.third.util.TongdunUtil;
import com.ald.fanbei.api.common.Constants;
import com.alibaba.fastjson.JSONObject;

@Controller
public class TestController {
	
    @RequestMapping(value ={
        	"/test"
        },method = RequestMethod.POST,produces="application/json;charset=utf-8")
        @ResponseBody
        public String goodsRequest(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException{
            request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
            response.setContentType("application/json;charset=utf-8");
            
//            String reportId = TongdunUtil.applyPreloan("362525198601022112", "陈金虎", "15958119936", "410228573@qq.com");
//            ER2017012122013411346564
            JSONObject result = TongdunUtil.queryPreloan("ER2017012121595110613362");
            
            System.out.println("-----reportId---" + 11 + ",result=" + result.toJSONString());
            return "succ";
        }
	
//	TongdunUtil
}
