package com.ald.fanbei.api.common.util;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import java.io.IOException;  
import java.net.Socket;  
import java.net.UnknownHostException;  
import java.security.KeyManagementException;  
import java.security.KeyStore;  
import java.security.KeyStoreException;  
import java.security.NoSuchAlgorithmException;  
import java.security.UnrecoverableKeyException;  
import java.security.cert.CertificateException;  
import java.security.cert.X509Certificate;  
  
import javax.net.ssl.SSLContext;  
import javax.net.ssl.SSLException;  
import javax.net.ssl.SSLSession;  
import javax.net.ssl.SSLSocket;  
import javax.net.ssl.TrustManager;  
import javax.net.ssl.X509TrustManager;  
  
import org.apache.http.HttpVersion;  
import org.apache.http.client.HttpClient;  
import org.apache.http.conn.ClientConnectionManager;  
import org.apache.http.conn.scheme.PlainSocketFactory;  
import org.apache.http.conn.scheme.Scheme;  
import org.apache.http.conn.scheme.SchemeRegistry;  
import org.apache.http.conn.ssl.SSLSocketFactory;  
import org.apache.http.conn.ssl.X509HostnameVerifier;  
import org.apache.http.impl.client.DefaultHttpClient;  
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;  
import org.apache.http.params.BasicHttpParams;  
import org.apache.http.params.HttpParams;  
import org.apache.http.params.HttpProtocolParams;  
import org.apache.http.protocol.HTTP;  

/**
 * 
 *@类描述：https请求工具
 *@author 陈金虎 2017年2月28日 上午10:46:36
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class HttpsUtil {

    private static final String METHOD_POST     = "POST";
    private static final String DEFAULT_CHARSET = "utf-8";

    public static String doPostToWx(String url, String params, int connectTimeout, int readTimeout)
            throws Exception {
        String ctype = "text/xml;charset=" + DEFAULT_CHARSET;
        byte[] content = { };
        if (params != null) {
            content = params.getBytes(DEFAULT_CHARSET);
        }

        return doPost(url, ctype, content, connectTimeout, readTimeout);
    }

    public static String doPost(String url, String params, String charset, int connectTimeout, int readTimeout)
            throws Exception {
        String ctype = "application/json;charset=" + charset;
        byte[] content = { };
        if (params != null) {
            content = params.getBytes(charset);
        }

        return doPost(url, ctype, content, connectTimeout, readTimeout);
    }

    public static String doPost(String url, String ctype, byte[] content, int connectTimeout, int readTimeout)
            throws Exception {
        HttpsURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0], new TrustManager[] { new DefaultTrustManager() }, new SecureRandom());
                SSLContext.setDefault(ctx);

                conn = getConnection(new URL(url), METHOD_POST, ctype);
                conn.setHostnameVerifier(new HostnameVerifier() {

                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            } catch (Exception e) {
                throw e;
            }
            try {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsString(conn);
            } catch (IOException e) {
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

    }

    private static HttpsURLConnection getConnection(URL url, String method, String ctype)
            throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "stargate");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }

    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        } else {
            String msg = getStreamAsString(es, charset);
            if (StringUtil.isEmpty(msg)) {
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());
            } else {
                throw new IOException(msg);
            }
        }
    }

    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!StringUtil.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!StringUtil.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    public static void main(String[] args) {
        try {
            for (int i = 0; i < 10000; i++)
                doPost("https://localhost:8443/service", "a=a", "UTF-8", 500, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static HttpClient getNoCertificateHttpClient(String url){  
        return getCertificateValidationIgnoredHttpClient();  
    }  
      
    private static HttpClient getCertificateValidationIgnoredHttpClient() {    
        try {    
            KeyStore trustStore = KeyStore.getInstance(KeyStore    
                    .getDefaultType());    
            trustStore.load(null, null);    
            //核心代码，创建一个UnVerifySocketFactory对象，验证证书时总是返回true  
            SSLSocketFactory sf = new UnVerifySocketFactory(trustStore);  
              
            HttpParams params = new BasicHttpParams();    
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);    
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);    
            SchemeRegistry registry = new SchemeRegistry();    
            registry.register(new Scheme("http", PlainSocketFactory    
                    .getSocketFactory(), 80));    
            registry.register(new Scheme("https", sf, 443));    
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(    
                    params, registry);    
            return new DefaultHttpClient(ccm, params);    
        } catch (Exception e) {    
            e.printStackTrace();  
            return new DefaultHttpClient();    
        }    
    }    
      
    /** 
     * 核心类 
     * UnVerifySocketFactory:一个验证证书时总是返回true的SSLSocketFactory的子类 
     */  
    private static X509HostnameVerifier ignoreVerifier;  
    private static class UnVerifySocketFactory extends SSLSocketFactory {  
        SSLContext sslContext = SSLContext.getInstance("TLS");  
  
        public UnVerifySocketFactory(KeyStore truststore)  
                throws NoSuchAlgorithmException, KeyManagementException,  
                KeyStoreException, UnrecoverableKeyException {  
            super(truststore);  
  
            TrustManager tm = new X509TrustManager() {  
                public void checkClientTrusted(X509Certificate[] chain,  
                        String authType) throws CertificateException {  
                }  
  
                public void checkServerTrusted(X509Certificate[] chain,  
                        String authType) throws CertificateException {  
                }  
  
                public X509Certificate[] getAcceptedIssuers() {  
                    return null;  
                }  
            };  
  
            sslContext.init(null, new TrustManager[] { tm }, null);  
        }  
  
        @Override  
        public Socket createSocket(Socket socket, String host, int port,  
                boolean autoClose) throws IOException, UnknownHostException {  
            return sslContext.getSocketFactory().createSocket(socket, host,  
                    port, autoClose);  
        }  
  
        //核心代码  
        @Override  
        public void setHostnameVerifier(X509HostnameVerifier hostnameVerifier) {  
            // TODO Auto-generated method stub  
            ignoreVerifier = new X509HostnameVerifier() {  
                @Override  
                public void verify(String arg0, String[] arg1, String[] arg2)  
                        throws SSLException {  
                }  
                @Override  
                public void verify(String arg0, X509Certificate arg1)  
                        throws SSLException {  
                }  
                @Override  
                public void verify(String arg0, SSLSocket arg1)  
                        throws IOException {  
                }  
                  
                //最最核心代码  
                @Override  
                public boolean verify(String arg0, SSLSession arg1) {  
                    return true;  
                }  
            };  
            super.setHostnameVerifier(ignoreVerifier);  
        }  
  
        @Override  
        public X509HostnameVerifier getHostnameVerifier() {  
            return ignoreVerifier;  
        }  
  
        @Override  
        public Socket createSocket() throws IOException {  
            return sslContext.getSocketFactory().createSocket();  
        }  
    }  
}
