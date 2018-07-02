package com.ald.fanbei.api.web.third.controller;

import cn.jiguang.common.utils.StringUtils;
import com.ald.fanbei.api.biz.service.AfUserChangXiaoService;
import com.ald.fanbei.api.dal.domain.query.AfUserChangXiaoDo;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


/**
 * 畅效广告平台与广告主对接接口
 *
 * @Author weiqingeng
 */
@Controller
@RequestMapping(value = "/third/changxiao")
public class ChangXiaoController {

    @Resource
    AfUserChangXiaoService afThirdChangXiaoService;

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());


    @ResponseBody
    @RequestMapping(value = {"/notice"}, method = RequestMethod.GET)
    public JSONObject insertChangXiaoUser(HttpServletRequest request) throws IOException {
        JSONObject returnjson = new JSONObject();
        try {
   /*         StringBuilder sb = new StringBuilder();
            sb.append("---/third/changxiao/notice  begin:");
            Map<String, String[]> paramMap = request.getParameterMap();
            for (String key : paramMap.keySet()) {
                String[] values = paramMap.get(key);
                for (String value : values) {
                    sb.append("键:" + key + ",值:" + value);
                }
            }
            logger.debug("/third/changxiao/notice 源数据" + sb.toString());
            AfUserChangXiaoDo afThirdCHangXiaoDo = buildParam(request);
            if (StringUtils.isNotEmpty(afThirdCHangXiaoDo.getIdfa())) {//先对接ios
                AfUserChangXiaoDo existDo = afThirdChangXiaoService.getUser(afThirdCHangXiaoDo);
                if (null == existDo) {//原用户不存在，添加到数据库
                    int result = afThirdChangXiaoService.insertChangxiaoUser(afThirdCHangXiaoDo);
                } else {
                    afThirdChangXiaoService.updateUser(existDo);
                }
            } else {
                returnjson.put("success", false);
                returnjson.put("msg", "参数错误");
                logger.debug("/third/changxiao/notice 参数错误" + sb.toString());
                return returnjson;
            }*/
            returnjson.put("success", true);
            returnjson.put("msg", null);
        	
        } catch (Exception e) {
            logger.debug("/third/changxiao/notice Exception" + e);
        }
        return returnjson;
    }

    /**
     * 组装参数
     *
     * @param request
     * @return
     */
    private AfUserChangXiaoDo buildParam(HttpServletRequest request) throws Exception {
        AfUserChangXiaoDo afThirdCHangXiaoDo = new AfUserChangXiaoDo();
        Integer os = NumberUtils.toInt(request.getParameter("os"));
        String osVersion = ObjectUtils.toString(request.getParameter("osVersion"), null);
        String ip = ObjectUtils.toString(request.getParameter("ip"), null);
        String mac = ObjectUtils.toString(request.getParameter("mac"), null);
        String imei = ObjectUtils.toString(request.getParameter("imei"), null);
        String idfa = ObjectUtils.toString(request.getParameter("idfa"), null);
        String callbackUrl = ObjectUtils.toString(request.getParameter("callback"), null);
        if (StringUtils.isNotEmpty(callbackUrl)) {
            callbackUrl = URLDecoder.decode(callbackUrl, "UTF-8");
        }
        afThirdCHangXiaoDo.setOs(os);
        afThirdCHangXiaoDo.setOsVersion(osVersion);
        afThirdCHangXiaoDo.setIp(ip);
        afThirdCHangXiaoDo.setMac(mac);
        afThirdCHangXiaoDo.setImei(imei);
        afThirdCHangXiaoDo.setIdfa(idfa);
        afThirdCHangXiaoDo.setCallbackUrl(callbackUrl);
        return afThirdCHangXiaoDo;
    }

}
