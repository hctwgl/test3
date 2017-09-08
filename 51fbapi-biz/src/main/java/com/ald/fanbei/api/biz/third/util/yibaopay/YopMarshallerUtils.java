package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.fasterxml.jackson.databind.JsonNode;
import com.yeepay.g3.sdk.yop.enums.FormatType;
import com.yeepay.g3.sdk.yop.unmarshaller.JacksonJsonMarshaller;
import com.yeepay.g3.sdk.yop.unmarshaller.YopMarshaller;
import org.apache.log4j.Logger;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author honghzengpei 2017/9/7 20:46
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YopMarshallerUtils {
    private static final Logger logger = Logger.getLogger(com.yeepay.g3.sdk.yop.unmarshaller.YopMarshallerUtils.class);
    private static Map<FormatType, YopMarshaller> marshallers = new HashMap();

    public YopMarshallerUtils() {
    }

    public static void marshal(FormatType format, Object object, OutputStream outputStream) {
        YopMarshaller marshaller = (YopMarshaller)marshallers.get(format);
        if (marshaller == null) {
            marshaller = (YopMarshaller)marshallers.get(FormatType.json);
        }

        marshaller.marshal(object, outputStream);
    }

    public static <T> T unmarshal(String content, FormatType format, Class<T> objectType) {
        YopMarshaller marshaller = (YopMarshaller)marshallers.get(format);
        if (marshaller == null) {
            marshaller = (YopMarshaller)marshallers.get(FormatType.json);
        }

        return marshaller.unmarshal(content, objectType);
    }

    public static JsonNode parse(String content) {
        try {
            YopMarshaller marshaller = (YopMarshaller)marshallers.get(FormatType.json);
            return marshaller.getObjectMapper().readTree(content);
        } catch (Exception var2) {
            logger.error("", var2);
            return null;
        }
    }

    static {
        marshallers.put(FormatType.json, new JacksonJsonMarshaller());
    }
}
