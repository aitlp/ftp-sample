package com.aitlp.ftpsample.controller;

import com.aitlp.ftpsample.service.FtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value = "/ftp")
public class FtpController {
    private FtpService ftpService;

    @Autowired
    public void setFtpService(FtpService ftpService) {
        this.ftpService = ftpService;
    }

    @PostMapping(value = "/download")
    public void downloadByPath(HttpServletRequest req, HttpServletResponse resp, @RequestParam("filePath") String filePath) {
        ftpService.downloadByPath(req, resp, filePath);
    }

    @GetMapping(value = "/preview")
    public void previewFtpFile(HttpServletRequest req, HttpServletResponse resp, @RequestParam("filePath") String filePath) {
        ftpService.previewFtpFile(req, resp, filePath);
    }
}
