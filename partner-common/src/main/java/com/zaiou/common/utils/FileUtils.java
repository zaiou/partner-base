package com.zaiou.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @Description: 文件操作公共类
 * @auther: LB 2018/11/6 15:29
 * @modify: LB 2018/11/6 15:29
 */
@Slf4j
public class FileUtils {

    /**
     * 将流写入到浏览器客户端
     *
     * @param is
     * @param out
     * @throws IOException
     */
    public static void writeFileToClient(InputStream is, OutputStream out) throws IOException {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(out);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * 根据路径创建指定的文件夹
     *
     * @param path
     *            文件夹路径
     */
    public static void createDir(String path) throws Exception {
        File filePath = new File(path);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }
    }

    /**
     *
     * @Title: addToZip
     * @Description: 将文件压缩输出
     * @author: liushuangxi 2018年8月6日
     * @modify: liushuangxi 2018年8月6日
     * @param is
     * @param zipOut
     * @param fileName
     * @throws IOException
     */
    public static void addToZip(InputStream is, ZipOutputStream zipOut, String fileName) throws IOException {
        ZipEntry entry = new ZipEntry(fileName);
        zipOut.putNextEntry(entry);
        int len;
        byte[] buffer = new byte[1024];
        while ((len = is.read(buffer)) > 0) {
            zipOut.write(buffer, 0, len);
        }
        zipOut.closeEntry();
        is.close();
    }

    /**
     * 复制文件到另外一个目录
     * @param source
     * @param dest
     * @throws IOException
     * @throws Exception
     */
    public static void copyFileUsingFileChannels(File source, File dest) throws IOException,Exception{
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;

        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {

            fis = new FileInputStream(source);
            fos	= new FileOutputStream(dest);
            inputChannel = fis.getChannel();
            outputChannel = fos.getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

        }catch (IOException e) {
            log.error(e.getMessage(),e);
            throw e;
        }catch (Exception e) {
            log.error(e.getMessage(),e);
            throw e;
        } finally {
            if(outputChannel!=null) {
                outputChannel.close();
            }
            if(inputChannel!=null) {
                inputChannel.close();
            }
            if(fos!=null) {
                fos.close();
            }
            if(fis!=null) {
                fis.close();
            }
        }
    }

    /**
     * 删除多个文件夹
     * @param paths
     */
    public static void delFolders(List<String> paths){
        if(paths == null || paths.size() <= 0){
            return;
        }
        for(String path : paths){
            delFolder(path);
        }
    }

    /**
     * 删除一个文件夹
     * @param path
     */
    public static void delFolder(String path){
        File folder = new File(path);
        if(!folder.isDirectory()){
            return;
        }
        File[] files = folder.listFiles();
        for(File file : files){
            if(file.isDirectory()){
                delFolder(file.getPath());
            }
            file.delete();
        }
        folder.delete();
    }

    public static void main(String[] args) {
        String path = "D:\\360Downloads\\credit-anhui-base";
        List<String> list = new ArrayList<String>();
        list.add(path);
        delFolders(list);
    }

}
