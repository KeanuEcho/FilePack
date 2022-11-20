package com.belongme.service.impl;

import com.belongme.service.FileService;
import com.belongme.utils.ZipUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLDecoder;
import java.util.List;
import java.util.Properties;

/**
 * @Title: FileServiceImple
 * @ProjectName FilePack
 * @Description: TODO
 * @Author DengChao
 * @Date 2022/11/2012:16
 */
@Service("fileService")
public class FileServiceImpl implements FileService {

    private final String classpath = ResourceUtils.getURL("classpath:").getPath();

    private final String linuxRootPath = getPathBySystem();

    @Autowired
    public FileServiceImpl() throws Exception {
    }

    @Override
    public String mainFile(MultipartFile uploadedFiles, HttpSession session) throws Exception {
        long size = uploadedFiles.getSize();
        // 限制上传文件为20MB
        if (size > 20 * 1024 * 1024) {
            return "redirect:";
        }
        long start = System.currentTimeMillis();
        //获取上传的文件的文件名
        String fileName = uploadedFiles.getOriginalFilename();
        //处理文件重名问题
//        String hzName = fileName.substring(fileName.lastIndexOf("."));
//
//        fileName = fileName.substring(0, fileName.lastIndexOf("."));
//
//        fileName = fileName + hzName;

        String filePath = linuxRootPath + "source";
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        String finalPath = filePath + File.separator + fileName;
        //实现上传功能
        uploadedFiles.transferTo(new File(finalPath));

        System.out.println(finalPath);

        BufferedOutputStream bos;

        //复制文件
        copyFile(finalPath);
        //清除源文件
//        cleanSourceFile(finalPath);
        File zip = new File(linuxRootPath + "zip");
        if (!zip.exists()) {
            zip.mkdir();
        }
        //压缩改名后的文件
        List<File> fileList = ZipUtils.getFileList(linuxRootPath + "files");
        bos = new BufferedOutputStream(new FileOutputStream(linuxRootPath + "zip" + File.separator + "success.zip"));
        ZipUtils.toZip(fileList, bos);
        //清空files文件夹
        cleanFilesFolder();

        bos.close();

        long end = System.currentTimeMillis();
        System.out.println("复制并重命名文件完成，耗时：" + (end - start) + " ms");
        return "forward:downLoadFile";
    }

    @Override
    public ResponseEntity<byte[]> downLoadFile() throws IOException {
        long start = System.currentTimeMillis();
        String realPath = linuxRootPath + "zip" + File.separator + "success.zip";
        //创建输入流
        FileInputStream is = new FileInputStream(realPath);
        //创建字节数组
        byte[] bytes = new byte[is.available()];
        //将流读到字节数组中
        is.read(bytes);
        //创建HttpHeaders对象设置响应头信息
        MultiValueMap<String, String> headers = new HttpHeaders();
        //设置要下载方式以及下载文件的名字
        headers.add("Content-Disposition", "attachment;filename=success.zip");
        //设置响应状态码
        HttpStatus statusCode = HttpStatus.OK;
        //创建ResponseEntity对象
        ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(bytes, headers, statusCode);
        //关闭输入流
        is.close();
        //清空zip文件夹
        cleanZipFolder();
        long end = System.currentTimeMillis();
        System.out.println("下载文件完成，耗时：" + (end - start) + " ms");
        return responseEntity;
    }

    @Override
    public void copyFile(String sourceFilePath) throws Exception {
        FileInputStream is = new FileInputStream(linuxRootPath + "studentConfig" + File.separator + "studentNums.properties");
        BufferedInputStream bisProp = new BufferedInputStream(is);
        Properties prop = new Properties();
        prop.load(bisProp);
        bisProp.close();

        //如果不存在files文件夹则创建
        File folder = new File(linuxRootPath + "files");
        if (!folder.exists()) {
            folder.mkdir();
        }

        prop.forEach((k, v) -> {
            File file = new File(sourceFilePath);
            FileInputStream fis;
            FileOutputStream fos;
            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            try {
                String hzName = file.getName().substring(file.getName().lastIndexOf("."));
                File file1 = new File(linuxRootPath + "files" + File.separator + v + hzName);
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                fos = new FileOutputStream(file1);
                bos = new BufferedOutputStream(fos);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void cleanFilesFolder() {
        long start = System.currentTimeMillis();
        File files = new File(linuxRootPath + "files");
        File[] listFiles = files.listFiles();
        for (File file : listFiles) {
            if (file.isFile()) {
                file.delete();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("清理files目录完成，耗时：" + (end - start) + " ms");
    }

    @Override
    public void cleanZipFolder() {
        long start = System.currentTimeMillis();
        File zip = new File(linuxRootPath + "zip");
        File[] listFiles = zip.listFiles();
        for (File file : listFiles) {
            if (file.isFile()) {
                file.delete();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("清理zip目录完成，耗时：" + (end - start) + " ms");
    }

    @Override
    public void cleanSourceFile(String finalPath) {
        long start = System.currentTimeMillis();
        File[] sourceFiles = new File(linuxRootPath + "source").listFiles();
        for (File file : sourceFiles) {
            file.delete();
        }
        long end = System.currentTimeMillis();
        System.out.println("清理source目录完成，耗时：" + (end - start) + " ms");
    }


    public String getPathBySystem() throws UnsupportedEncodingException {
        String rootPath = "";
        String path = System.getProperty("java.class.path");
        int firstIndex = path.lastIndexOf(System.getProperty("path.separator")) + 1;
        int lastIndex = path.lastIndexOf(File.separator) + 1;
        path = path.substring(firstIndex, lastIndex);
        rootPath = URLDecoder.decode(path, "utf-8");
        return rootPath;
    }
}
