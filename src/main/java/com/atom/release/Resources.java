package com.atom.release;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by toltek7 on 07.01.2015.
 */
public class Resources {

    Map<String,String> cssHolder,
                       jsHolder,
                       imageHolder,
                       cssResourcesHolder;

    public boolean isCompressedCss   = false,
                   isCompressedJS    = false,
                   isCompressedImage = false;

    public Resources(){
        this.cssHolder          = new HashMap<String,String>();
        this.jsHolder           = new HashMap<String,String>();
        this.imageHolder        = new HashMap<String,String>();
        this.cssResourcesHolder = new HashMap<String,String>();
    }

    public void addCss(Map<String,String> res){
        cssHolder.putAll(res);
    }

    public void addJs(Map<String,String> res){
        jsHolder.putAll(res);
    }

    public void addImages(Map<String,String> res){
        imageHolder.putAll(res);
    }

    public void save() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        saveFiles(cssHolder,   isCompressedCss   ? "compressCSS" : null);
//        saveFiles(jsHolder, null);
        saveFiles(jsHolder,    isCompressedJS    ? "compressJS"  : null);
        saveFiles(imageHolder, isCompressedImage ? "compressImg" : null);

        saveCssResources();
        processJS();
    }

    private void saveFiles(Map<String,String> holder, String processorName) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        Method method = processorName != null ? FileCompressor.class.getMethod(processorName, String.class): null;

        FileCompressor fileCompressor = new FileCompressor();

        for(Map.Entry<String, String> entry : holder.entrySet()) {
            String destPath = entry.getKey();
            String srcPath = entry.getValue();
            FileManager.copyFile(srcPath, destPath, fileCompressor, method);
        }
    }

    public void saveCssResources() throws IOException, InvocationTargetException, IllegalAccessException {

        CssProcessor cssProcessor = new CssProcessor();
        Map<String,String> cssUrls = new HashMap<String,String>();

        for(Map.Entry<String, String> entry : cssHolder.entrySet()) {
            String destPath = entry.getKey();
            String srcPath = entry.getValue();
            cssUrls.putAll(cssProcessor.getUrls(srcPath, destPath));
        }

        for(Map.Entry<String, String> entry : cssUrls.entrySet()) {
            String destPath = entry.getKey();
            String srcPath = entry.getValue();
            FileManager.copyFile(srcPath, destPath, null, null,false);
        }
    }

    public void processJS(){


    }



    public void print(){

        Utils.print("\nResources copying:");
        printResources(cssHolder);
        printResources(jsHolder);
        printResources(imageHolder);

    }
    
    private void printResources(Map<String,String> resources){

        for(Map.Entry<String, String> entry : resources.entrySet()) {

            Utils.print(entry.getValue() + "  ------->  " + entry.getKey());
        }
    }
}
