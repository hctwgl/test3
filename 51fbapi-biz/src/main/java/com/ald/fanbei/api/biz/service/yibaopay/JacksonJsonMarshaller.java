package com.ald.fanbei.api.biz.service.yibaopay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * @author honghzengpei 2017/9/7 20:05
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JacksonJsonMarshaller implements YopMarshaller {
    private static ObjectMapper objectMapper;

    public JacksonJsonMarshaller() {
    }

    public void marshal(Object object, OutputStream outputStream) {
        try {
            JsonGenerator jsonGenerator = this.getObjectMapper().getFactory().createGenerator(outputStream, JsonEncoding.UTF8);
            this.getObjectMapper().writeValue(jsonGenerator, object);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public <T> T unmarshal(String content, Class<T> objectType) {
        try {
            return this.getObjectMapper().readValue(content, objectType);
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    public ObjectMapper getObjectMapper() throws IOException {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
            objectMapper.setAnnotationIntrospector(new JacksonAnnotationIntrospector());
        }

        return objectMapper;
    }
}
