package com.atom.release;

import com.atom.Application;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.StandardOpenOption;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by toltek7 on 28.12.2014.
 */
public class FileManager {

    public String releaseFolderPath = null;
    public Boolean isReleaseFolderExist = false;

    public FileManager(String path) {

        File dir = createFile(Application.root + path, File.separator);

        if (dir != null && dir.isDirectory()) {
            this.releaseFolderPath = dir.getAbsolutePath();
            this.isReleaseFolderExist = true;
            cleanFolder();
        }
    }

    public static File createFile(String path) {
        return createFile(path, "");
    }

    public static File createFile(String path, String forceDirSeparator) {

        path = getCanonicalPath(path, forceDirSeparator);

        if(path == null) return null;

        //Create directory
        File dir = new File(FilenameUtils.getFullPath(path));
        try {
            FileUtils.forceMkdir(dir);
        } catch (Exception e) {
            System.out.println("Error[FileManager]: can not create release folder: " + path + ". " + e);
            return null;
        }

        //Return file stream
        return new File(dir.getAbsolutePath() + File.separator + FilenameUtils.getName(path));

    }
    public static String getCanonicalPath(String path){
        return getCanonicalPath(path,"");
    }

    public static String getCanonicalPath(String path, String forceDirSeparator){

        try {
            File file = new File(path);
            return file.getCanonicalPath() + forceDirSeparator;
        } catch (Exception e) {
            System.out.println("Error[FileManager]:  path: " + path + "is not valid. " + e);
            return null;
        }
    }

    protected void cleanFolder() {

        try {
            FileUtils.forceDelete(new File(releaseFolderPath));
        } catch (IOException e) {
            System.out.println("Error[FileManager]: " + releaseFolderPath + " can't be removed. " + e);
        }

    }

    public static boolean isFileExist(String path) {

        File file = new File(path);

        if (!file.exists()) {
            System.out.println("Error[FileManager]: file " + file.getAbsolutePath() + " not exist.");
            return false;
        }

        return true;

    }

    public static String addQueryParamToPAth(String path, String query){

        if(isBlank(query)) return path;

        String symbol = "?";

        if(path.contains("?")) symbol = "&";

        return path + symbol + query;
    }

    public static void copyFile(String scrPath, String destPath, Object fileCompressor, Method compressor) throws IOException, InvocationTargetException, IllegalAccessException {
        copyFile(scrPath, destPath, fileCompressor, compressor, true);
    }

    public static void copyFile(String scrPath, String destPath, Object fileCompressor, Method compressor, Boolean forceRewrite) throws IOException, InvocationTargetException, IllegalAccessException {

        File srcFile  = FileManager.createFile(scrPath);
        File destFile = FileManager.createFile(destPath);

        if (destFile.exists()){
            System.out.print("Warning[copyFile] file + " + destFile.getAbsolutePath() + " already exist");
            if(!forceRewrite){
                Utils.print(", skip.");
                return;
            }
        }

        if(compressor != null){
            String content = Files.toString(srcFile, Charsets.UTF_8);
            content = (String)compressor.invoke(fileCompressor, content);
            FileUtils.writeStringToFile(destFile, content);
        } else{
            FileUtils.copyFile(srcFile,destFile);//need for images
        }

        System.out.println("File " + destFile.getAbsolutePath() + " saved.");

    }


    public static void appendToFile(String scrPath, String destPath) throws IOException {
        File srcFile  = FileManager.createFile(scrPath);
        if (!srcFile.exists()){
            Utils.print("Warning[appendToFile] file + " + srcFile.getAbsolutePath() + " not exist");
            return;
        }
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destPath, true));// true to append
            String content = Files.toString(srcFile, Charsets.UTF_8);
            bufferedWriter.write(content);
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFile(String file){
        try {
            FileUtils.forceDelete(new File(file));
        } catch (IOException e) {
            System.out.println("Error[FileManager]: " + file + " can't be removed. " + e);
        }
    }

}
/*        HttpClient client = new HttpClient();
        StringBuffer url = new StringBuffer("http://localhost:8080/E47");
        url.append("/CMSWebDriver?actionName=aa&commandName=cc&Id=" + Id);
// Create a method instance.
        HttpMethod method = new GetMethod(url.toString());
// Execute the method.
        try {
// execute the method.
            int statusCode = client.executeMethod(method);
        } catch (HttpRecoverableException e) {
            System.err.println("A recoverable exception occurred, retrying." + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to download file.");
            e.printStackTrace();
            System.exit(-1);
        }
// Read the response body.
        byte[] responseBody = method.getResponseBody();
// Release the connection.
        method.releaseConnection();*/