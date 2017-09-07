package com.ald.fanbei.api.biz.service.yibaopay;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.jaxb.XmlJaxbAnnotationIntrospector;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;

/**
 * @author honghzengpei 2017/9/7 19:49
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JacksonXmlMarshaller implements YopMarshaller {
    private static XmlMapper objectMapper;

    public JacksonXmlMarshaller() {
    }

    public void marshal(Object object, OutputStream outputStream) {
        try {
            this.getObjectMapper().writeValue(outputStream, object);
        } catch (IOException var4) {
            var4.printStackTrace();
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

    public XmlMapper getObjectMapper() throws IOException {
        if (objectMapper == null) {
            objectMapper = new XmlMapper();
            objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
            objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            objectMapper.setSerializationInclusion(Include.NON_EMPTY);
            XmlJaxbAnnotationIntrospector xmlJaxbAnnotationIntrospector = new XmlJaxbAnnotationIntrospector(objectMapper.getTypeFactory());
            objectMapper.setAnnotationIntrospector((AnnotationIntrospector)xmlJaxbAnnotationIntrospector);
        }

        return objectMapper;
    }
}
