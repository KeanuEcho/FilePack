package com.belongme.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @Title: FileService
 * @ProjectName FilePack
 * @Description: TODO
 * @Author DengChao
 * @Date 2022/11/2012:14
 */
public interface FileService {
    String mainFile(MultipartFile uploadedFiles, HttpSession session) throws Exception;

    ResponseEntity<byte[]> downLoadFile() throws IOException;

    void copyFile(String sourceFilePath) throws Exception;

    void cleanFilesFolder();

    void cleanZipFolder();

    void cleanSourceFile(String finalPath);

    String getPathBySystem() throws UnsupportedEncodingException;
}
