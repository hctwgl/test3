package com.ald.fanbei.api.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.exception.FanbeiException;



/**
 * 
 *@类描述：序列化类
 *@author 陈金虎 2017年1月16日 下午11:42:52
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class SerializeUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(SerializeUtil.class);
    
    /**
     * 序列化列表
     * 
     * @param value
     * @return
     * @throws IOException
     */
    public static<T> byte[] serializeList(List<T> value){
        if (value == null) {
            throw new NullPointerException("Can't serialize null");
        }
        byte[] rv=null;
        ByteArrayOutputStream bos = null;
        ObjectOutputStream os = null;
        try {
            bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            for(T user : value){
                os.writeObject(user);
            }
            os.writeObject(null);
            os.close();
            bos.close();
            rv = bos.toByteArray();
        } catch (IOException e) {
            throw new FanbeiException("serializeList ioexception",e);
        } finally {
            try{
                if(os != null){
                    os.close();
                }
                if(bos != null){
                    bos.close();
                }
            }catch (IOException e) {
                throw new FanbeiException("",e);
            }
        }
        return rv;
    }

    /**
     * 反序列化列表
     * @param in
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static<T> List<T> unserializeList(byte[] in){
        List<T> list = new ArrayList<T>();
        ByteArrayInputStream bis = null;
        ObjectInputStream is = null;
        try {
            if(in != null) {
                bis=new ByteArrayInputStream(in);
                is=new ObjectInputStream(bis);
                while (true) {
                    T user = (T) is.readObject();
                    if(user == null){
                        break;
                    }else{
                        list.add(user);
                    }
                }
                is.close();
                bis.close();
            }
        } catch (IOException e) {
            logger.warn("Caught IOException decoding %d bytes of data",
                    in == null ? 0 : in.length, e);
            throw new FanbeiException("unserializeList ioexception",e);
        } catch (ClassNotFoundException e) {
            logger.warn("Caught CNFE decoding %d bytes of data",
                    in == null ? 0 : in.length, e);
            throw new FanbeiException("unserializeList cnfexception",e);
        } finally {
            try{
                if(is != null){
                    is.close();
                }
                if(bis != null){
                    bis.close();
                }
            }catch (IOException e) {
                throw new FanbeiException("",e);
            }
        }
        return list;
    }
    
    /**
     * 序列化对象
     * @param object
     * @return
     * @throws Exception
     */
    public static byte[] serialize(Object object){
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            // 序列化
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            byte[] bytes = baos.toByteArray();
            return bytes;
        } catch (Exception e) {
            throw new FanbeiException("serialize exception",e);
        }
    }

    /**
     * 反序列化对象
     * 
     * @param bytes
     * @return
     * @throws Exception
     */
    public static Object unserialize(byte[] bytes){
        ByteArrayInputStream bais = null;
        try {
            bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return ois.readObject();
        } catch (Exception e) {
            throw new FanbeiException("unserialize exception",e);
        }
    }
}
