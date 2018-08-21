package com.ald.fanbei.api.biz.util;

import com.itextpdf.text.DocumentException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.*;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * @author guoshuaiqiang
 * @date 2017/12/24
 * @descriptions velocity模板生成
 */
public final class VelocityUtil {

    private static Logger logger = Logger.getLogger(VelocityUtil.class);
    /**
     * 创建html文件
     *
     * @throws IOException
     * @throws DocumentException
     */
    public static String getHtml(Map<String,Object> map) throws IOException, DocumentException {
        try {
            //初始化vm模板
            Properties p = new Properties();
            p.put("file.resource.loader.class",
                    "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            Velocity.init(p);
            Template template = Velocity.getTemplate(String.valueOf(map.get("templateSrc")), "UTF-8");
            //初始化上下文
            VelocityContext context = new VelocityContext();
            //添加数据到上下文中
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                Object value = entry.getValue();
                context.put(key,value);
            }
            StringWriter writer = new StringWriter();
            //生成html页面
            template.merge(context, writer);
            //关闭流
            return writer.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logger.error("VelocityUtil FileNotFoundException =>{}",e);
        } catch (ResourceNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("VelocityUtil ResourceNotFoundException =>{}",e);
        } catch (ParseErrorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("VelocityUtil ParseErrorException =>{}",e);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("VelocityUtil Exception =>{}",e);
        }
        return null;
    }

}
