package com.pinyougou.manage.controller;

import com.pinyougou.common.util.FastDFSClient;
import com.pinyougou.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    /**
     * 接收图片并保存到fastdfs中
     * @param multipartFile 图片文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile multipartFile){
        Result result = Result.fail("上传图片失败");

        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");

            //文件后缀；如jpg
            String file_ext_name = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf(".")+1);
            //上传图片
            String url = fastDFSClient.uploadFile(multipartFile.getBytes(), file_ext_name);

            result = Result.ok(url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
