package com.ald.fanbei.api.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.HttpURLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @类描述：http请求工具
 * @author 陈金虎 2017年1月16日 下午11:41:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@SuppressWarnings("deprecation")
public class HttpUtil {

    protected static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 执行GET请求
     * 
     * @param url
     * @param timeout
     * @return
     */
    public static String doGet(String url, int timeout) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            return "";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 执行GET请求
     * 
     * @param url
     * @param timeout
     * @return
     */
    public static String doPost(String url, String param) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            out = new OutputStreamWriter(conn.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            return "";
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

//    /**
//     * 发送post请求
//     * 
//     * @param url
//     * @param paramsMap
//     * @return
//     * @throws ClientProtocolException
//     * @throws IOException
//     */
//    public static String httpPost(String url, Map<String, String> paramsMap) {
//    	logger.info("httpPost begin url = {}, paramsMap = {}",url, paramsMap);
//        HttpClientBuilder builder = HttpClientBuilder.create();
//        CloseableHttpClient httpClient = builder.build();
//        // 要传递的参数
//        List<NameValuePair> query = new ArrayList<NameValuePair>();
//        for (String name : paramsMap.keySet()) {
//            NameValuePair paramItem = new BasicNameValuePair(name, paramsMap.get(name));
//            query.add(paramItem);
//        }
//        if (url.indexOf("?") > 0) {
//            url = url + "&";
//        } else {
//            url = url + "?";
//        }
//        url = url + URLEncodedUtils.format(query, CharEncoding.UTF_8);
//        HttpUriRequest request = new HttpPost(url);
//        HttpResponse response = null;
//        HttpPost postMethod = new HttpPost();
//        postMethod.setURI(request.getURI());
//        postMethod.setHeader("content-type", "application/json;charset=utf-8");
//        try {
//            response = httpClient.execute(postMethod);
//            HttpEntity entity = response.getEntity();
//            // System.out.println(response.getStatusLine());
//            // 显示结果
//            if (!url.contains("preview")) {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
//
//                String line = null;
//                StringBuffer sb = new StringBuffer();
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line);
//                }
//                reader.close();
//                logger.info("httpPost result = {}",sb.toString());
//                return sb.toString();
//            } else {
//                BufferedInputStream bi = new BufferedInputStream(entity.getContent());
//                FileOutputStream fo = new FileOutputStream(new File("D:/picture/" + new Date().getTime() + ".jpg"));
//                int readByte = 0;
//                while ((readByte = bi.read()) != -1) {
//                    fo.write(readByte);
//                }
//                bi.close();
//                fo.close();
//                return null;
//            }
//        } catch (Exception e) {
//            logger.error("httpPost", e);
//            return "";
//        }
//    }

    /**
     * 发送POST请求，将参数放置到BODY里边
     * 
     * @param url
     * @param param
     * @return
     */
    public static String doHttpPost(String url, String param) {
    	logger.info("doHttpPost begin url = {} param = {}",url,param);
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            out = new OutputStreamWriter(conn.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        logger.info("doHttpPost result = {}", result);
        return result;
    }

    /**
     * 发送POST请求，将参数放置到BODY里边
     * 
     * @param url
     * @param param
     * @return
     */
    public static String doHttpPostJsonParam(String url, String param) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/json");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("contentType", "utf-8");
            out = new OutputStreamWriter(conn.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 发送POST请求，将参数放置到BODY里边
     * 
     * @param url
     * @param param
     * @return
     */
    public static String ttt(String url, String param) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = new HttpURLConnection(new PostMethod(), realUrl);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            byte[] data = "api_key=vYdfhZ0iR6eP5FPXhVLGg_uUfoe_T9a5&api_secret=Zk6jMac1vTIln1Qe_2Ymo3J9hQzignpm".getBytes();

            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=ABCD");


            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            out = new OutputStreamWriter(conn.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    /**
     * 发送POST请求，将参数放置到BODY里边
     * 
     * @param url
     * @param param
     * @param cookie
     * @return
     */
    public static String httpPost(String url, String param, String cookie) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/json");
            conn.setRequestProperty("Cookie", cookie);
            out = new OutputStreamWriter(conn.getOutputStream());
            // 把数据写入请求的Body
            out.write(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            logger.error("发送失败" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发送HTTPS的POST请求，并且忽略证书验证,将参数放置到BODY里边
     * 
     * @param urlString
     * @param query
     * @return
     */
    public static String doHttpsPostIgnoreCert(String urlString, String query) {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream(512);
        try {
            URL url = new URL(urlString);
            /*
             * use ignore host name verifier
             */
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

            // Prepare SSL Context
            TrustManager[] tm = { ignoreCertificationTrustManger };
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 设置doOutput属性为true表示将使用此urlConnection写入数据
            connection.setDoOutput(true);
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            connection.setSSLSocketFactory(ssf);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            // 把数据写入请求的Body
            out.write(query);
            out.flush();
            out.close();

            InputStream reader = connection.getInputStream();
            byte[] bytes = new byte[512];
            int length = reader.read(bytes);

            do {
                buffer.write(bytes, 0, length);
                length = reader.read(bytes);
            } while (length > 0);

            reader.close();
            connection.disconnect();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        }
        String repString = new String(buffer.toByteArray());
        return repString;
    }

    /**
     * 忽视证书HostName
     */
    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {

        public boolean verify(String s, SSLSession sslsession) {
            logger.debug("WARNING: Hostname is not matched for cert.");
            return true;
        }
    };

    /**
     * Ignore Certification
     */
    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

        private X509Certificate[] certificates;

        public void checkClientTrusted(X509Certificate certificates[], String authType) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
                logger.debug("init at checkClientTrusted");
            }
        }

        public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
                logger.debug("init at checkServerTrusted");
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    public static String post(String url, Map<String, String> params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String body = null;

        logger.info("create httppost:" + url);
        HttpPost post = postForm(url, params);

        body = invoke(httpclient, post);

        httpclient.getConnectionManager().shutdown();

        return body;
    }

    private static String invoke(DefaultHttpClient httpclient, HttpUriRequest httpost) {

        HttpResponse response = sendRequest(httpclient, httpost);
        String body = paseResponse(response);

        return body;
    }

    private static String paseResponse(HttpResponse response) {
        logger.info("get response from http server..");
        HttpEntity entity = response.getEntity();

        logger.info("response status: " + response.getStatusLine());
        String charset = EntityUtils.getContentCharSet(entity);
        logger.info(charset);

        String body = null;
        try {
            body = EntityUtils.toString(entity);
            logger.info(body);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return body;
    }

    private static HttpResponse sendRequest(DefaultHttpClient httpclient, HttpUriRequest httpost) {
        logger.info("execute post...");
        HttpResponse response = null;

        try {
            response = httpclient.execute(httpost);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static HttpPost postForm(String url, Map<String, String> params) {

        HttpPost httpost = new HttpPost(url);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            nvps.add(new BasicNameValuePair(key, params.get(key)));
        }

        try {
            logger.info("set utf-8 form entity to httppost");
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return httpost;
    }

}
