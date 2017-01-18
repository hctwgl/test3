package com.ald.fanbei.common.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *@类描述：http请求工具
 *@author 陈金虎 2017年1月16日 下午11:41:47
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class HttpUtil {

    protected static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    /**
     * 忽视证书HostName
     */
    private static HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {

        public boolean verify(String s,
                              SSLSession sslsession) {
            logger.debug("WARNING: Hostname is not matched for cert.");
            return true;
        }
    };

    /**
     * Ignore Certification
     */
    private static TrustManager ignoreCertificationTrustManger = new X509TrustManager() {

        private X509Certificate[] certificates;

        public void checkClientTrusted(X509Certificate certificates[],
                                       String authType)
                throws CertificateException {
            if (this.certificates == null) {
                this.certificates = certificates;
                logger.debug("init at checkClientTrusted");
            }
        }

        public void checkServerTrusted(X509Certificate[] ax509certificate,
                                       String s)
                throws CertificateException {
            if (this.certificates == null) {
                this.certificates = ax509certificate;
                logger.debug("init at checkServerTrusted");
            }
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    };

    /*
     * 发送HTTPS的POST请求，并且忽略证书验证,将参数放置到BODY里边
     */
    public static String httpsPostIgnoreCert(String urlString, String query) {

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

    // 发送POST请求，将参数放置到BODY里边
    public static String httpPost(String url, String param) {
        BufferedReader in = null;
        OutputStreamWriter out = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("content-type", "application/json");
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

    // 发送POST请求，将参数放置到BODY里边
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
            conn.setRequestProperty("content-type", "application/json");
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

}
