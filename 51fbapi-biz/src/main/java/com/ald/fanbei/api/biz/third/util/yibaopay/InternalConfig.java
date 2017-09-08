package com.ald.fanbei.api.biz.third.util.yibaopay;

import com.google.common.collect.Maps;
import com.yeepay.g3.facade.yop.ca.enums.CertTypeEnum;
import com.yeepay.g3.facade.yop.ca.enums.KeyStoreTypeEnum;
import com.yeepay.g3.frame.yop.ca.rsa.RSAKeyUtils;
import com.yeepay.g3.frame.yop.ca.utils.Exceptions;
import com.yeepay.g3.sdk.yop.exception.YopClientException;
import com.yeepay.g3.sdk.yop.utils.JsonUtils;
import com.yeepay.g3.sdk.yop.utils.config.CertConfig;
import com.yeepay.g3.sdk.yop.utils.config.SDKConfig;
import org.apache.commons.lang3.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.Enumeration;
import java.util.Map;

/**
 * @author honghzengpei 2017/9/7 18:07
 * @类描述：订单支付
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class InternalConfig {
    private static final String SP_SDK_CONFIG_FILE = "yop.sdk.config.file";
    private static final String DEFAULT_SDK_CONFIG_FILE = "/yop_sdk_config_default.json";
    private static final String DEFAULT_PROTOCOL_VERSION = "yop-auth-v2";
    private static final String DEFAULT_SDK_VERSION = "20170104.2103";
    private String protocolVersion;
    private String sdkVersion;
    private PublicKey isvPublicKey;
    private Map<CertTypeEnum, PublicKey> yopPublicKeyMap = Maps.newEnumMap(CertTypeEnum.class);
    private Map<CertTypeEnum, PrivateKey> isvPrivateKeyMap = Maps.newEnumMap(CertTypeEnum.class);

    public InternalConfig() {
    }

    public String getProtocolVersion() {
        return this.protocolVersion;
    }

    public String getSdkVersion() {
        return this.sdkVersion;
    }

    public PublicKey getISVPublicKey() {
        return this.isvPublicKey;
    }

    public PublicKey getYopPublicKey(CertTypeEnum certType) {
        return (PublicKey)this.yopPublicKeyMap.get(certType);
    }

    public PrivateKey getISVPrivateKey(CertTypeEnum certType) {
        return (PrivateKey)this.isvPrivateKeyMap.get(certType);
    }

    static InternalConfig load() throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, CertificateException, NoSuchProviderException {
        String configFile = System.getProperty("yop.sdk.config.file");
        if (StringUtils.isBlank(configFile)) {
            configFile = "/yop_sdk_config_default.json";
        }

        SDKConfig config = load(configFile);
        InternalConfig internalConfig = new InternalConfig();
        if (StringUtils.isNotBlank(config.getDefaultProtocolVersion())) {
            internalConfig.protocolVersion = config.getDefaultProtocolVersion();
        } else {
            internalConfig.protocolVersion = "yop-auth-v2";
        }

        if (StringUtils.isNotBlank(config.getSdkVersion())) {
            internalConfig.sdkVersion = config.getSdkVersion();
        } else {
            internalConfig.sdkVersion = "20170104.2103";
        }

        if (null == config.getYopPublicKey()) {
            throw new YopClientException("Can't init YOP public key!");
        } else {
            CertConfig[] var3;
            int var4;
            int var5;
            CertConfig certConfig;
            if (config.getYopPublicKey().length > 0) {
                var3 = config.getYopPublicKey();
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    certConfig = var3[var5];
                    internalConfig.yopPublicKeyMap.put(certConfig.getCertType(), loadPublicKey(certConfig));
                }
            }

            if (null != config.getIsvPrivateKey() && config.getIsvPrivateKey().length > 0) {
                var3 = config.getIsvPrivateKey();
                var4 = var3.length;

                for(var5 = 0; var5 < var4; ++var5) {
                    certConfig = var3[var5];
                    internalConfig.isvPrivateKeyMap.put(certConfig.getCertType(), loadPrivateKey(certConfig));
                }
            }

            return internalConfig;
        }
    }

    static SDKConfig load(String configFile) {
        InputStream fis = null;
        SDKConfig config = null;

        try {
            fis = getInputStream(configFile);
            config = (SDKConfig) JsonUtils.loadFrom(fis, SDKConfig.class);
        } catch (Exception var11) {
            System.out.println("Config format is error, file name:" + configFile);
            throw Exceptions.unchecked(var11);
        } finally {
            if (null != fis) {
                try {
                    fis.close();
                } catch (IOException var10) {
                    var10.printStackTrace();
                }
            }

        }

        return config;
    }

    private static PublicKey loadPublicKey(CertConfig certConfig) {
        PublicKey publicKey = null;
        if (null == certConfig.getStoreType()) {
            throw new YopClientException("Can't init YOP public key! Store type is error.");
        } else {
            switch(certConfig.getStoreType()) {
                case STRING:
                    try {
                        publicKey = RSAKeyUtils.string2PublicKey(certConfig.getValue());
                        return publicKey;
                    } catch (Exception var3) {
                        System.out.println("Failed to load public key form config file is error, " + certConfig);
                        throw Exceptions.unchecked(var3);
                    }
                default:
                    throw new RuntimeException("Not support cert store type.");
            }
        }
    }

    private static PrivateKey loadPrivateKey(CertConfig certConfig) {
        PrivateKey privateKey = null;
        if (null == certConfig.getStoreType()) {
            throw new YopClientException("Can't init ISV private key! Store type is error.");
        } else {
            switch(certConfig.getStoreType()) {
                case STRING:
                    try {
                        privateKey = RSAKeyUtils.string2PrivateKey(certConfig.getValue());
                    } catch (Exception var6) {
                        System.out.println("Failed to load private key form config file is error, " + certConfig);
                    }
                    break;
                case FILE_P12:
                    try {
                        char[] password = certConfig.getPassword().toCharArray();
                        KeyStore keystore = null;
                        keystore = KeyStore.getInstance(KeyStoreTypeEnum.PKCS12.getValue());
                        keystore.load(InternalConfig.class.getResourceAsStream(certConfig.getValue()), password);
                        Enumeration aliases = keystore.aliases();

                        String keyAlias;
                        for(keyAlias = ""; aliases.hasMoreElements(); keyAlias = (String)aliases.nextElement()) {
                            ;
                        }

                        privateKey = (PrivateKey)keystore.getKey(keyAlias, password);
                        break;
                    } catch (Exception var7) {
                        System.out.println("Cert key is error, " + certConfig);
                        throw Exceptions.unchecked(var7);
                    }
                default:
                    throw new RuntimeException("Not support cert store type.");
            }

            return privateKey;
        }
    }

    private static InputStream getInputStream(String location) throws FileNotFoundException {
        InputStream fis = null;
        if (StringUtils.startsWith(location, "file://")) {
            fis = new FileInputStream(StringUtils.substring(location, 6));
        } else {
            fis = InternalConfig.class.getResourceAsStream(location);
        }

        return (InputStream)fis;
    }

    public static class Factory {
        private static final InternalConfig SINGELTON;

        public Factory() {
        }

        public static InternalConfig getInternalConfig() {
            return SINGELTON;
        }

        static {
            InternalConfig config = null;

            try {
                config = InternalConfig.load();
            } catch (RuntimeException var2) {
                throw var2;
            } catch (Exception var3) {
                throw new IllegalStateException("Fatal: Failed to load the internal config for YOP Java SDK", var3);
            }

            SINGELTON = config;
        }
    }
}
