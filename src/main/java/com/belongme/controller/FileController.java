package com.belongme.controller;

import com.belongme.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Title: FileController
 * @ProjectName FilePack
 * @Description: TODO
 * @Author DengChao
 * @Date 2022/11/2012:17
 */
@Controller
public class FileController {

    FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @RequestMapping("file")
    public String getFile(MultipartFile uploadedFiles, HttpSession session) {
        String result = null;
        try {
            result = fileService.mainFile(uploadedFiles, session);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @RequestMapping("downLoadFile")
    public ResponseEntity<byte[]> downLoadFile() throws IOException {
        return fileService.downLoadFile();
    }


    @RequestMapping("newFile")
    @ResponseBody
    public String newFile() throws UnsupportedEncodingException {
        String pathBySystem = fileService.getPathBySystem();
        File file = new File(pathBySystem + "files");
        boolean mkdir = file.mkdir();
        System.out.println(mkdir);
        System.out.println(file.exists());
        return pathBySystem;
    }


}
