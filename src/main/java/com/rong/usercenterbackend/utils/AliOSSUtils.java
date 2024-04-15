package com.rong.usercenterbackend.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 阿里云OSS对象存储
 */
@Component
@ConfigurationProperties(prefix = "aliyun.oss")
@Data
public class AliOSSUtils {
    private String endpoint;
    private String accessKeyId;
    private String accessKeySecret;
    private String bucketName;

    public String upload(MultipartFile multipartFile) throws IOException {
        //获取上传的文件的输入流
        InputStream inputStream = multipartFile.getInputStream();

        //避免文件覆盖
        String fileName = "userCenter/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")) + multipartFile.getOriginalFilename();

        //上传文件到OSS
        OSS ossClient = new OSSClientBuilder().build(endpoint,accessKeyId,accessKeySecret);
        ossClient.putObject(bucketName,fileName,inputStream);

        //文件访问路径
        String url = endpoint.split("//")[0] + "//" +bucketName + "." +endpoint.split("//")[1] +fileName;
        //关闭ossClient
        ossClient.shutdown();
        return url;
    }
}
