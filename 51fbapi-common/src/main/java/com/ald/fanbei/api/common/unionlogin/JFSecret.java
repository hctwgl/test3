package com.ald.fanbei.api.common.unionlogin;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static java.lang.System.out;

public class JFSecret {

    public static String encrypt(PublicKey publicKey, byte[] databyte) throws Exception {
        String base64String = null;
        if (publicKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] output = cipher.doFinal(databyte);
            base64String = new BASE64Encoder().encodeBuffer(output);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            throw new Exception("加密公钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
        return base64String;//为最终加密的手机号
    }

    public static PublicKey getPublicKey(String public_key) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(public_key);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("公钥非法");
        } catch (IOException e) {
            throw new Exception("公钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("公钥数据为空");
        }
    }

    public static String decrypt(PrivateKey privateKey, String data) throws Exception {

        byte[] buffer = new BASE64Decoder().decodeBuffer(data);

        String base64String = null;
        if (privateKey == null) {
            throw new Exception("加密公钥为空, 请设置");
        }
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] output = cipher.doFinal(buffer);
            base64String = new String(output);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            throw new Exception("加密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("明文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("明文数据已损坏");
        }
        return base64String; //最后获得的手机号
    }

    public static PrivateKey getPrivateKey(String private_key) throws Exception {
        try {
            BASE64Decoder base64Decoder = new BASE64Decoder();
            byte[] buffer = base64Decoder.decodeBuffer(private_key);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(buffer);

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            return privateKey;
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (IOException e) {
            throw new Exception("私钥数据内容读取错误");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }

    public static void main(String[] args) throws Exception{
        String private_key = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMPYODKFD4bTm13GtVNv1VnbTPYkIpmh0PPwkgl4heMl0mIuvjDUamybaz/tf3Hnckpbcm9zbS4tyPTIPUg4Ez5cGaZxwY1JZFz/jjwsu87MsCJ56xX4dZSpEi6W1LrG/YuqW4eRWb7VN2tgYPWX3xhfnzFlJKx7apS/Et/vCeInAgMBAAECgYBfKmQ0eGARHiZzLSnTf5Zm8Z+2Q9zkVrNYtl/gZkZ5GFnhB+G3jKCiYet9xwSU7uikpUc4TRiSxgDOobbVrxn565+24H9MN9Z9vl5/slDBXhMu+LOODzQCgTceti45Ibd4pwpw6Qq2aq1DqVXTOqCpn/jJfmeZEtbt6MoA0ddKgQJBAOrS0KEVx2RVrz/1jZxh3bM7t3VHKhY3ZPKMYbQ9sIFoS1dzNQYbo5ZAVWDKRvsZSA+t3SyxPuEI73z85Se/JMcCQQDVgYeY1Tb5TyLhmR2ONZwHAnQFuraIbyTyhsP730gCaCrXIpZUjOfukJcH0wgxHpdH3liNRC5RqdfOw3lCqzehAkAJNzTQ3ZXxrhsum2hvVrforNNKWvyf2pSvoCrFdBZVPc6XJAJUtwj4gJXZMpcOi7N0ShKACoS5OCyN7y7fHHVJAkEAwl96Ixl5Qt9Y0imjTqRft8Hz/oNNSkhlSqaGJffQhuBuoA1M7wyY2geod+cXviArebJiy7kWsiH95q7u5lMaAQJBAJmJ75JmvjOCxfWts5BeeLCtp0CWQuwpuZPSUeo9KnwA0rE3PNz6p0KoLxwXUzjEo1rFxAGR3DMzNR/V5J+Wh8c=";

        PrivateKey privateKey = getPrivateKey(private_key);
        out.println("private_key:-----------"+"[{\"nper\":\"3\",\"rebatePercent\":\"10\",\"freeNper\":\"3\"},{\"nper\":\"6\",\"rebatePercent\":\"20\",\"freeNper\":\"6\"},{\"nper\":\"9\",\"rebatePercent\":\"30\",\"freeNper\":\"9\"},{\"nper\":\"12\",\"rebatePercent\":\"40\",\"freeNper\":\"12\"}]".length());


        String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDD2DgyhQ+G05tdxrVTb9VZ20z2JCKZodDz8JIJeIXjJdJiLr4w1Gpsm2s/7X9x53JKW3Jvc20uLcj0yD1IOBM+XBmmccGNSWRc/448LLvOzLAieesV+HWUqRIultS6xv2LqluHkVm+1TdrYGD1l98YX58xZSSse2qUvxLf7wniJwIDAQAB";

        String mobile ="211";
        byte[] mobilebyte = mobile.getBytes();
        PublicKey publicKey = getPublicKey(public_key);

        String mobileEnc = encrypt(publicKey, mobilebyte);
        out.println("mobile enc-----:    "+mobileEnc);
        String mobileDec=decrypt(privateKey, mobileEnc);
                out.println("mobile dec-----:    "+mobileDec);
    }
}
