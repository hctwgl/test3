package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.biz.util.OssUploadResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;


/**
 * @author 陈金虎 2016年8月1日 上午9:56:48
 * @类描述：oss上传文件
 * @注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface OssFileUploadService {

    /**
     * 上传文件
     *
     * @param file
     * @param inv  环境
     * @return
     */
    public OssUploadResult uploadFileToOss(MultipartFile file);

    /**
     * 上传文件
     *
     * @param file
     * @param inv  环境
     * @return
     */
    public OssUploadResult uploadFileToOssWithName(MultipartFile file, String fileName);

    /**
     * 上传图片
     *
     * @param file
     * @param inv  环境
     * @return
     */
    public OssUploadResult uploadImageToOss(MultipartFile file);

    /**
     * @param file
     * @param inv      环境
     * @param fileName
     * @param fileSize
     * @return
     */
    public OssUploadResult uploadImageToOss(InputStream inputStream, String fileName, int fileSize);

    /**
     * @param file
     * @param inv      环境
     * @param fileName
     * @param fileSize
     * @return
     */
    @Async
    public OssUploadResult uploadImageToOssAsync(InputStream inputStream, String fileName, int fileSize);

}
