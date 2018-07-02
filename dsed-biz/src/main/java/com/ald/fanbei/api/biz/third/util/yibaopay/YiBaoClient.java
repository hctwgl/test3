package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yeepay.g3.facade.yop.ca.dto.DigitalSignatureDTO;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.facade.yop.ca.enums.DigestAlgEnum;
import com.yeepay.g3.frame.yop.ca.DigitalEnvelopeUtils;
import com.yeepay.g3.frame.yop.ca.rsa.RSAKeyUtils;
import com.yeepay.g3.frame.yop.ca.utils.Exceptions;
import com.yeepay.g3.sdk.yop.client.*;
import com.yeepay.g3.sdk.yop.enums.FormatType;
import com.yeepay.g3.sdk.yop.enums.HttpMethodType;
import com.yeepay.g3.sdk.yop.exception.YopClientException;
import com.yeepay.g3.sdk.yop.http.HttpUtils;
import com.yeepay.g3.sdk.yop.utils.Assert;
import com.yeepay.g3.sdk.yop.utils.DateUtils;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author honghzengpei 2017/9/7 16:11
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class YiBaoClient {
    protected static final Logger logger = Logger.getLogger(YopClient3.class);
    protected static RestTemplate restTemplate = new YopRestTemplate();
    protected static Map<String, List<String>> uriTemplateCache = new HashMap();
    private static final Set<String> defaultHeadersToSign = Sets.newHashSet();
    private static final Joiner headerJoiner = Joiner.on('\n');
    private static final Joiner signedHeaderStringJoiner = Joiner.on(';');
    private static final String EXPIRED_SECONDS = "1800";
    private static final Joiner queryStringJoiner;
    private static BitSet URI_UNRESERVED_CHARACTERS;
    private static String[] PERCENT_ENCODED_STRINGS;

    public YiBaoClient() {
    }

    public static YopResponse postRsa(String methodOrUri, YopRequest request) {
        String content = postRsaString(methodOrUri, request);
        YopResponse response = (YopResponse) YopMarshallerUtils.unmarshal(content, request.getFormat(), YopResponse.class);
        handleRsaResult(request, response, content);
        return response;
    }

    public static String postRsaString(String methodOrUri, YopRequest request) {
        String serverUrl = richRequest(HttpMethodType.POST, methodOrUri, request);
        logger.info("signature:" + request.getParamValue("sign"));
        request.setAbsoluteURL(serverUrl);
        String appKey = request.getAppKey();
        String timestamp = DateUtils.formatAlternateIso8601Date(new Date());
        InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
        String protocolVersion = internalConfig.getProtocolVersion();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap();
        String requestId = UUID.randomUUID().toString();
        headers.add("x-yop-request-id", requestId);
        headers.add("x-yop-date", timestamp);
        String authString = protocolVersion + "/" + appKey + "/" + timestamp + "/" + "1800";
        Set<String> headersToSignSet = new HashSet();
        headersToSignSet.add("x-yop-request-id");
        headersToSignSet.add("x-yop-date");
        if (org.apache.commons.lang3.StringUtils.isBlank(request.getCustomerNo())) {
            headers.add("x-yop-appkey", appKey);
            headersToSignSet.add("x-yop-appkey");
        } else {
            headers.add("x-yop-customerid", appKey);
            headersToSignSet.add("x-yop-customerid");
        }

        String canonicalURI = HttpUtils.getCanonicalURIPath(methodOrUri);
        String canonicalQueryString = getCanonicalQueryString(request.getParams().toSingleValueMap(), true);
        SortedMap<String, String> headersToSign = getHeadersToSign(headers, headersToSignSet);
        String canonicalHeader = getCanonicalHeaders(headersToSign);
        String signedHeaders = "";
        if (headersToSignSet != null) {
            signedHeaders = signedHeaderStringJoiner.join(headersToSign.keySet());
            signedHeaders = signedHeaders.trim().toLowerCase();
        }

        String canonicalRequest = authString + "\nPOST\n" + canonicalURI + "\n" + canonicalQueryString + "\n" + canonicalHeader;
        PrivateKey isvPrivateKey = null;
        if (org.apache.commons.lang3.StringUtils.isNotBlank(request.getSecretKey())) {
            try {
                isvPrivateKey = RSAKeyUtils.string2PrivateKey(request.getSecretKey());
            } catch (NoSuchAlgorithmException var21) {
                throw Exceptions.unchecked(var21);
            } catch (InvalidKeySpecException var22) {
                throw Exceptions.unchecked(var22);
            }
        }
        else{
            isvPrivateKey = internalConfig.getISVPrivateKey(CertTypeEnum.RSA2048);
        }

        if (null == isvPrivateKey) {
            throw new YopClientException("Can't init ISV private key!");
        } else {
            DigitalSignatureDTO digitalSignatureDTO = new DigitalSignatureDTO();
            digitalSignatureDTO.setPlainText(canonicalRequest);
            digitalSignatureDTO.setCertType(CertTypeEnum.RSA2048);
            digitalSignatureDTO.setDigestAlg(DigestAlgEnum.SHA256);
            digitalSignatureDTO = DigitalEnvelopeUtils.sign(digitalSignatureDTO, isvPrivateKey);
            headers.add("Authorization", "YOP-RSA2048-SHA256 " + protocolVersion + "/" + appKey + "/" + timestamp + "/" + "1800" + "/" + signedHeaders + "/" + digitalSignatureDTO.getSignature());
            request.encoding();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity(request.getParams(), headers);
            String content = (String)getRestTemplate(request).postForObject(serverUrl, httpEntity, String.class, new Object[0]);
            if (logger.isDebugEnabled()) {
                logger.debug("requestId:" + requestId + ", response:\n" + content);
            }

            return content;
        }
    }

    public static String getCanonicalQueryString(Map<String, String> parameters, boolean forSignature) {
        if (parameters.isEmpty()) {
            return "";
        } else {
            List<String> parameterStrings = Lists.newArrayList();
            Iterator var3 = parameters.entrySet().iterator();

            while(true) {
                Map.Entry entry;
                String key;
                do {
                    if (!var3.hasNext()) {
                        Collections.sort(parameterStrings);
                        return queryStringJoiner.join(parameterStrings);
                    }

                    entry = (Map.Entry)var3.next();
                    key = (String)entry.getKey();
                    Preconditions.checkNotNull(key, "parameter key should not be null");
                } while(forSignature && "Authorization".equalsIgnoreCase(key));

                parameterStrings.add(normalize(key) + '=' + normalize((String)entry.getValue()));
            }
        }
    }

    public static String normalize(String value) {
        try {
            StringBuilder builder = new StringBuilder();
            byte[] var2 = value.getBytes("UTF-8");
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                byte b = var2[var4];
                int bl = b & 255;
                if (URI_UNRESERVED_CHARACTERS.get(bl)) {
                    builder.append((char)b);
                } else {
                    builder.append(PERCENT_ENCODED_STRINGS[bl]);
                }
            }

            return builder.toString();
        } catch (UnsupportedEncodingException var7) {
            throw new RuntimeException(var7);
        }
    }

    private static RestTemplate getRestTemplate(YopRequest request) {
        if (null == request.getConnectTimeout() && null == request.getReadTimeout()) {
            return restTemplate;
        } else {
            int connectTimeout = null != request.getConnectTimeout() ? request.getConnectTimeout().intValue() : YopConfig.getConnectTimeout();
            int readTimeout = null != request.getReadTimeout() ? request.getReadTimeout().intValue() : YopConfig.getReadTimeout();
            return new YopRestTemplate(connectTimeout, readTimeout);
        }
    }

    protected static String richRequest(HttpMethodType type, String methodOrUri, YopRequest request) {
        Assert.notNull(methodOrUri, "method name or rest uri");
        if (methodOrUri.startsWith(request.getServerRoot())) {
            methodOrUri = methodOrUri.substring(request.getServerRoot().length() + 1);
        }

        boolean isRest = methodOrUri.startsWith("/rest/");
        request.setRest(isRest);
        String serverUrl = request.getServerRoot();
        if (isRest) {
            methodOrUri = mergeTplUri(methodOrUri, request);
            serverUrl = serverUrl + methodOrUri;
            String version = org.apache.commons.lang3.StringUtils.substringBetween(methodOrUri, "/rest/v", "/");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(version)) {
                request.setVersion(version);
            }
        } else {
            serverUrl = serverUrl + "/command?method=" + methodOrUri;
        }

        request.setMethod(methodOrUri);
        return serverUrl;
    }

    protected static void handleRsaResult(YopRequest request, YopResponse response, String content) {
        response.setFormat(request.getFormat());
        String ziped = "";
        if (response.isSuccess()) {
            String strResult = getBizResult(content, request.getFormat());
            ziped = strResult.replaceAll("[ \t\n]", "");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(strResult) && response.getError() == null) {
                response.setStringResult(strResult);
            }
        }

        isValidResult(ziped, response.getSign());
    }

    public static boolean isValidResult(String result, String sign) {
        if (org.apache.commons.lang3.StringUtils.isBlank(sign)) {
            return true;
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(org.apache.commons.lang3.StringUtils.trimToEmpty(result));
            DigitalSignatureDTO digitalSignatureDTO = new DigitalSignatureDTO();
            digitalSignatureDTO.setCertType(CertTypeEnum.RSA2048);
            digitalSignatureDTO.setSignature(sign);
            digitalSignatureDTO.setPlainText(sb.toString());
            InternalConfig internalConfig = InternalConfig.Factory.getInternalConfig();
            DigitalEnvelopeUtils.verify(digitalSignatureDTO, internalConfig.getYopPublicKey(CertTypeEnum.RSA2048));
            return true;
        }
    }

    private static String getBizResult(String content, FormatType format) {
        if (org.apache.commons.lang3.StringUtils.isBlank(content)) {
            return content;
        } else {
            switch(format) {
                case json:
                    String jsonStr = org.apache.commons.lang3.StringUtils.substringAfter(content, "\"result\" : ");
                    jsonStr = org.apache.commons.lang3.StringUtils.substringBeforeLast(jsonStr, "\"ts\"");
                    jsonStr = org.apache.commons.lang3.StringUtils.substringBeforeLast(jsonStr, ",");
                    return jsonStr;
                default:
                    String xmlStr = org.apache.commons.lang3.StringUtils.substringAfter(content, "</state>");
                    xmlStr = org.apache.commons.lang3.StringUtils.substringBeforeLast(xmlStr, "<ts>");
                    return xmlStr;
            }
        }
    }

    protected static String mergeTplUri(String tplUri, YopRequest request) {
        String uri = tplUri;
        if (tplUri.indexOf("{") < 0) {
            return tplUri;
        } else {
            List<String> dynaParamNames = (List)uriTemplateCache.get(tplUri);
            if (dynaParamNames == null) {
                dynaParamNames = new LinkedList();
                Pattern pattern = Pattern.compile("\\{([^\\}]+)\\}");
                Matcher matcher = pattern.matcher(tplUri);

                while(matcher.find()) {
                    ((List)dynaParamNames).add(matcher.group(1));
                }

                uriTemplateCache.put(tplUri, dynaParamNames);
            }

            String value;
            String dynaParamName;
            for(Iterator var7 = ((List)dynaParamNames).iterator(); var7.hasNext(); uri = uri.replace("{" + dynaParamName + "}", value)) {
                dynaParamName = (String)var7.next();
                value = request.removeParam(dynaParamName);
                Assert.notNull(value, dynaParamName + " must be specified");
            }

            return uri;
        }
    }

    private static String getCanonicalHeaders(SortedMap<String, String> headers) {
        if (headers.isEmpty()) {
            return "";
        } else {
            List<String> headerStrings = Lists.newArrayList();
            Iterator var2 = headers.entrySet().iterator();

            while(var2.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var2.next();
                String key = (String)entry.getKey();
                if (key != null) {
                    String value = (String)entry.getValue();
                    if (value == null) {
                        value = "";
                    }

                    headerStrings.add(HttpUtils.normalize(key.trim().toLowerCase()) + ':' + HttpUtils.normalize(value.trim()));
                }
            }

            Collections.sort(headerStrings);
            return headerJoiner.join(headerStrings);
        }
    }

    private static SortedMap<String, String> getHeadersToSign(MultiValueMap<String, String> headers, Set<String> headersToSign) {
        SortedMap<String, String> ret = Maps.newTreeMap();
        String key;
        if (headersToSign != null) {
            Set<String> tempSet = Sets.newHashSet();
            Iterator var4 = ((Set)headersToSign).iterator();

            while(var4.hasNext()) {
                key = (String)var4.next();
                tempSet.add(key.trim().toLowerCase());
            }

            headersToSign = tempSet;
        }

        Iterator var6 = headers.entrySet().iterator();

        while(true) {
            Map.Entry entry;
            do {
                do {
                    do {
                        if (!var6.hasNext()) {
                            return ret;
                        }

                        entry = (Map.Entry)var6.next();
                        key = (String)entry.getKey();
                    } while(entry.getValue() == null);
                } while(((List)entry.getValue()).isEmpty());
            } while((headersToSign != null || !isDefaultHeaderToSign(key)) && (headersToSign == null || !((Set)headersToSign).contains(key.toLowerCase()) || "Authorization".equalsIgnoreCase(key)));

            ret.put(key, ((List)entry.getValue()).get(0).toString());
        }
    }

    private static boolean isDefaultHeaderToSign(String header) {
        header = header.trim().toLowerCase();
        return header.startsWith("x-yop-") || defaultHeadersToSign.contains(header);
    }

    static {
        defaultHeadersToSign.add("Host".toLowerCase());
        defaultHeadersToSign.add("Content-Length".toLowerCase());
        defaultHeadersToSign.add("Content-Type".toLowerCase());
        defaultHeadersToSign.add("Content-MD5".toLowerCase());
        queryStringJoiner = Joiner.on('&');
        URI_UNRESERVED_CHARACTERS = new BitSet();
        PERCENT_ENCODED_STRINGS = new String[256];

        int i;
        for(i = 97; i <= 122; ++i) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }

        for(i = 65; i <= 90; ++i) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }

        for(i = 48; i <= 57; ++i) {
            URI_UNRESERVED_CHARACTERS.set(i);
        }

        URI_UNRESERVED_CHARACTERS.set(45);
        URI_UNRESERVED_CHARACTERS.set(46);
        URI_UNRESERVED_CHARACTERS.set(95);
        URI_UNRESERVED_CHARACTERS.set(126);

        for(i = 0; i < PERCENT_ENCODED_STRINGS.length; ++i) {
            PERCENT_ENCODED_STRINGS[i] = String.format("%%%02X", i);
        }

    }
}
