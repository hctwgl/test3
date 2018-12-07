package com.ald.fanbei.api.web.controller;

import com.ald.fanbei.api.biz.service.JsdResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.SerializeUtil;
import com.ald.fanbei.api.dal.domain.JsdResourceDo;
import com.alibaba.fastjson.JSONObject;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Controller
public class HealthController {
    @Resource
    JsdResourceService jsdResourceService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @RequestMapping(value = {"/health/index"}, method = RequestMethod.GET)
    @ResponseBody
    public String index() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("status", true);
            JsdResourceDo jsdResourceDo = jsdResourceService.getByTypeAngSecType(Constants.JSD_CONFIG, Constants.JSD_RATE_INFO);
            jsonObject.put("mysql", true);
            redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection connection) throws DataAccessException {
                    connection.set(redisTemplate.getStringSerializer().serialize("redis_health"), SerializeUtil.serialize(jsdResourceDo), Expiration.seconds(1), null);
                    return null;
                }
            });
            jsonObject.put("redis", true);
        } catch (Exception e) {
            jsonObject.put("status", false);
            jsonObject.put("msg", e);
        }
        return jsonObject.toJSONString();
    }
}
