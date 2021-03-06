package com.aitlp.ftpsample.service;

import com.aitlp.ftpsample.util.FtpUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@Service
public class FtpService {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${ftp.url}")
    private String ftpUrl;

    private String[] initFtpHostInfo() {
        //ftp服务器地址
        String hostname = ftpUrl.split(",")[0];
        //ftp服务器端口号
        String port = ftpUrl.split(",")[1];
        //ftp登录账号
        String username = ftpUrl.split(",")[2];
        //ftp登录密码
        String password = ftpUrl.split(",")[3];
        return new String[]{hostname, port, username, password};
    }

    public void downloadByPath(HttpServletRequest req, HttpServletResponse resp, String fileFullPath) {
        String filePath = fileFullPath.substring(0, fileFullPath.lastIndexOf("/"));
        String fileName = fileFullPath.substring(fileFullPath.lastIndexOf("/") + 1);
        try {

            String[] ftpInfo = initFtpHostInfo();
            //初始化ftp服务器连接
            FtpUtils ftpUtil = new FtpUtils(ftpInfo[0], Integer.parseInt(ftpInfo[1]), ftpInfo[2], ftpInfo[3]);
            boolean flag = ftpUtil.initFtpClient();
            if (!flag) {
                logger.error("ftp服务器连接失败.....");
            } else {
                byte[] b = ftpUtil.downloadFile(filePath, fileName);
                downLoadFile(req, resp, b, fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void previewFtpFile(HttpServletRequest req, HttpServletResponse resp, String filePath) {
        try {
            byte[] data = viewFtpFile(req, resp, filePath);
            resp.reset();
            resp.setContentType(getContentType(new File(filePath).getName()));
            OutputStream outputStream = new BufferedOutputStream(resp.getOutputStream());
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();
            resp.flushBuffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean downLoadFile(HttpServletRequest req, HttpServletResponse resp, byte[] b, String fileName) {
        boolean flag = false;
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            resp.reset();
            resp.setCharacterEncoding("utf-8");
            resp.setContentType("application/octet-stream");
            //resp.addHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO-8859-1"));
            resp.addHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
            OutputStream os;
            os = resp.getOutputStream();
            os.write(b);
            os.flush();
            os.close();
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public byte[] viewFtpFile(HttpServletRequest req, HttpServletResponse resp, String fileFullPath) {
        byte[] b = null;
        try {
            String[] ftpInfo = initFtpHostInfo();
            //初始化ftp服务器连接
            FtpUtils ftpUtil = new FtpUtils(ftpInfo[0], Integer.parseInt(ftpInfo[1]), ftpInfo[2], ftpInfo[3]);
            String filePath = fileFullPath.substring(0, fileFullPath.lastIndexOf("/"));
            String fileName = fileFullPath.substring(fileFullPath.lastIndexOf("/") + 1);
            boolean flag = ftpUtil.initFtpClient();
            if (!flag) {
                logger.error("ftp服务器连接失败.....");
            } else {
                b = ftpUtil.downloadFile(filePath, fileName);
            }
        } catch (Exception e) {
            logger.error("UploadFileInfoService->viewFtpFile->预览ftp的pdf文件时出错，错误信息：" + e.getMessage());
            e.printStackTrace();
        }
        return b;
    }

    private String getContentType(String fileName){
        String contentType = "text/plain";
        String fileType = FilenameUtils.getExtension(fileName).toLowerCase();
        switch (fileType){
            case "pdf":
                contentType = "application/pdf;charset=UTF-8";
                break;
        }
        return contentType;
    }
}
