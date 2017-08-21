package com.ald.fanbei.api.common;


import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import jodd.util.Base64;

import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.DESedeParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * 类DesedeUtil.java的实现描述：DES加密
 * @author 陈金虎 2015年12月18日 下午2:37:10
 */
public class DesedeUtil {

    private static final Logger logger           = LoggerFactory.getLogger(DesedeUtil.class);

    // 加解密算法
    private static final String KEY_ALGORITHM    = "DESede";

    // 加密算法的填充方式
    private static final String CIPHER_ALGORITHM = "DESede/ECB/PKCS5Padding";

    private static BlockCipher  blockCipher;

    static {
        if (null == blockCipher) {
            blockCipher = new DESedeEngine();
        }
    }

    /**
     * 生成密钥,生成二进制秘钥
     * 
     * @return
     * @throws Exception
     */
    public static byte[] initkey() {
        SecretKey secretKey = null;
        try {
            KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            // init长度，112-168长度
            kg.init(168);
            secretKey = kg.generateKey();
        } catch (Exception e) {
            logger.error("init key error ", e);
        }
        return (null == secretKey) ? null : secretKey.getEncoded();
    }

    /**
     * 获取DESedeKey的key
     * 
     * @param key
     * @return
     */
    public static Key buildDESedeKey(byte[] key) {
        SecretKey secretKey = null;
        try {
            DESedeKeySpec keySpec = new DESedeKeySpec(key);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
            secretKey = keyFactory.generateSecret(keySpec);
        } catch (Exception e) {
            logger.error("encrypt desede error ", e);
        }
        return secretKey;
    }

    /**
     * @param key
     * @param data
     * @return
     */
    public static byte[] encrypt(byte[] key, byte[] data) {
        Cipher cipher = null;
        try {
            Key encryptKey = buildDESedeKey(key);
            // 实例化
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            if (null != cipher) {
                // init,set加密设置模式
                cipher.init(Cipher.ENCRYPT_MODE, encryptKey);
            }
            return (null == cipher) ? null : cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("encrypt desede error ", e);
            return null;
        }

    }

    /**
     * 解密数据
     * 
     * @param data 待解密数据
     * @param key 密钥
     * @return
     */
    public static byte[] decrypt(byte[] key, byte[] data) {
        Cipher cipher = null;
        try {
            Key decryptKey = buildDESedeKey(key);
            cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            if (null != cipher) {
                // init，set解密模式
                cipher.init(Cipher.DECRYPT_MODE, decryptKey);
            }
            return (null == cipher) ? null : cipher.doFinal(data);
        } catch (Exception e) {
            logger.error("decrypt desede error ", e);
            return null;
        }
    }

    /**
     * 调用bouncycastle实现DESede加密
     * 
     * @param key
     * @param source
     * @return
     */
    public static byte[] encryptEdeDES(byte[] key, byte[] source) {
        DESedeParameters tripleParam = new DESedeParameters(key);
        BufferedBlockCipher bufCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher));
        bufCipher.init(true, tripleParam);
        byte[] encryptSrc = new byte[bufCipher.getOutputSize(source.length)];
        int outoff = bufCipher.processBytes(source, 0, source.length, encryptSrc, 0);
        try {
            bufCipher.doFinal(encryptSrc, outoff);
        } catch (Exception e) {
            logger.error("encrypt desede bouncy castle error ", e);
        }
        return encryptSrc;
    }

    /**
     * 调用bouncycastle实现DESede解密
     * 
     * @param key
     * @param encryptStr
     * @return
     */
    public static byte[] decryptEdeDES(byte[] key, byte[] encryptStr) {
        DESedeParameters tripleParam = new DESedeParameters(key);
        BufferedBlockCipher bufCipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(blockCipher));
        bufCipher.init(false, tripleParam);
        byte[] decryptSrc = new byte[bufCipher.getOutputSize(encryptStr.length)];
        int outoff = bufCipher.processBytes(encryptStr, 0, encryptStr.length, decryptSrc, 0);
        try {
            bufCipher.doFinal(decryptSrc, outoff);
        } catch (Exception e) {
            logger.error("decrypt desede bouncy castle error ", e);
        }
        return decryptSrc;
    }

    /**
     * 进行加解密的测试
     * 
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String str = "chenjinhu";
        // System.out.println("原文：" + str);
        // 初始化密钥
        byte[] key = Constants.TIRPLE_DES_KEY.getBytes("UTF-8");
        // System.out.println("密钥：" + Base64.encodeToString(key));
        // // 加密数据
        // byte[] data = DesedeUtil.encrypt(key, str.getBytes());
        // System.out.println("加密后：" + Base64.encodeToString(data));
        // // 解密数据
        // data = DesedeUtil.decrypt(key, data);
        // System.out.println("解密后：" + new String(data));

        // 加密数据
        byte[] data1 = DesedeUtil.encryptEdeDES(key, str.getBytes("UTF-8"));
         System.out.println("加密后1：" + Base64.encodeToString(data1));
        // 解密数据
        byte[] data2 = DesedeUtil.decryptEdeDES(key, data1);
         System.out.println("解密后1：" + StringUtils.trim(new String(data2)));
    }
}
