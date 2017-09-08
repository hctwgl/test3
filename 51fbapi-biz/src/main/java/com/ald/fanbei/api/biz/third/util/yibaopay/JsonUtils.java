package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import com.yeepay.g3.sdk.yop.exception.YopClientException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * @author honghzengpei 2017/9/7 19:10
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class JsonUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectWriter writer;
    private static final ObjectWriter prettyWriter;

    public JsonUtils() {
    }

    public static String toJsonPrettyString(Object value) throws JsonProcessingException {
        return prettyWriter.writeValueAsString(value);
    }

    public static String toJsonString(Object value) {
        try {
            return writer.writeValueAsString(value);
        } catch (Exception var2) {
            throw new IllegalStateException(var2);
        }
    }

    public static <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        } else {
            try {
                return objectMapper.readValue(json, clazz);
            } catch (Exception var3) {
                throw new YopClientException("Unable to parse Json String.", var3);
            }
        }
    }

    public static JsonNode jsonNodeOf(String json) {
        return (JsonNode)fromJsonString(json, JsonNode.class);
    }

    public static JsonGenerator jsonGeneratorOf(Writer writer) throws IOException {
        return (new JsonFactory()).createGenerator(writer);
    }

    public static <T> T loadFrom(File file, Class<T> clazz) throws IOException {
        try {
            return objectMapper.readValue(file, clazz);
        } catch (IOException var3) {
            throw var3;
        } catch (Exception var4) {
            throw new IllegalStateException(var4);
        }
    }

    public static void load(InputStream input, Object obj) throws IOException, JsonProcessingException {
        objectMapper.readerForUpdating(obj).readValue(input);
    }

    public static <T> T loadFrom(InputStream input, Class<T> clazz) throws JsonParseException, JsonMappingException, IOException {
        return objectMapper.readValue(input, clazz);
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectWriter getWriter() {
        return writer;
    }

    public static ObjectWriter getPrettywriter() {
        return prettyWriter;
    }

    static {
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        writer = objectMapper.writer();
        prettyWriter = objectMapper.writerWithDefaultPrettyPrinter();
    }
}
