package com.ald.fanbei.api.biz.service.yibaopay;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author honghzengpei 2017/9/7 20:04
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface YopMarshaller {
    void marshal(Object var1, OutputStream var2);

    <T> T unmarshal(String var1, Class<T> var2);

    ObjectMapper getObjectMapper() throws IOException;
}
