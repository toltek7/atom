package com.atom.release;

import com.atom.Application;
import com.atom.Constants;
import com.atom.json.JsonProcessorRequestListener;
import org.w3c.dom.*;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Created by toltek7 on 28.12.2014.
 * ReleaseObject - define release builder
 */
public class ReleaseObject {

    private static Logger log = Logger.getLogger(ReleaseObject.class.getName());

    private String[]        languages;             //NOTE: first lang is main
    private String          commonQueryParams;
    private HashSet<String> pagesPath;
    private List<Page>      pageObjects;

    public  String       releaseFolderPath;
    public  String       releaseName;

    public boolean       setTimeStamp,
                         isCompressedHTML;

    public Resources     resourcesManager;

    public ReleaseObject(Node rootElement) {
        
        new XmlParser(rootElement);

        releaseFolderPath    = XmlParser.getFolder();
        releaseName          = getReleaseName(releaseFolderPath);
        languages            = XmlParser.getLanguages();
        commonQueryParams    = XmlParser.getQueryParams();
        pagesPath            = XmlParser.getPages();

        setTimeStamp         = XmlParser.hasTimeStamp();
        isCompressedHTML     = XmlParser.isCompressedHTML();

        validateInputs();

        //NOTE: we add data to resourcesManager inside Page class
        resourcesManager = new Resources();
        resourcesManager.isCompressedCss = XmlParser.isCompressedCSS();
        resourcesManager.isCompressedJS  = XmlParser.isCompressedJS();
//        resourcesManager.isCompressedImage = isCompressedCss;
    }

    private void validateInputs() {

        if (releaseFolderPath == null || releaseFolderPath.isEmpty()) {
            releaseFolderPath = Constants.DEFAULT_RELEASE_FOLDER;
        }

        if (languages == null || languages[0].isEmpty()) {
            languages = new String[]{Application.getProperty(
                    Constants.JSON_CONFIG_LANGUAGE,
                    Constants.JSON_DEFAULT_LANGUAGE)};
        }

    }

    public void build(HttpServletRequest request, final HttpServletResponse response) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if(!createReleaseFolder()) return;

        Utils.print("\n******* " + releaseName + " *******");

        pageObjects = createPageObjects(request, response);

        resourcesManager.print();

        savePages();
        
        saveResources();

        Utils.print("\n");
    }

    protected void savePages() throws IOException {
        Utils.print("\nPages saving:");
        for(Page page: pageObjects) page.save();
    }

    protected void saveResources() throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        resourcesManager.save();
    }

    protected List<Page> createPageObjects(HttpServletRequest request, final HttpServletResponse response) throws IOException {

        String query,
               content;

        List<Page> pageObjects = new ArrayList<Page>();

        JsonProcessorRequestListener jsonProcessorRequestListener = new JsonProcessorRequestListener();

        int langCount = languages.length;

        for(int j = 0; j < langCount; j++){
            //change data
            jsonProcessorRequestListener.reloadJsonData(languages[j], Application.getContext());
            
            //Concatenate common param + language 
            query = commonQueryParams + "&" + Constants.URL_LANGUAGE_PARAM + "="  + languages[j];
            
            for(String path:pagesPath){
                Application.currentPage = path;
                content = getPageContent(request, response, FileManager.addQueryParamToPAth(path, query));
                if(content != null){
                    pageObjects.add(new Page(path, content, j == 0, languages[j], this)); //j==0 default language
                }
            }
            Application.currentPage = null;
        }

        return pageObjects;
    }

    protected String getPageContent(HttpServletRequest request, final HttpServletResponse response, String resourcePath) throws IOException {

        if(!FileManager.isFileExist(Application.root + resourcePath.split("\\?")[0])) return null;

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(stringWriter);

        RequestDispatcher dispatcher = request.getRequestDispatcher(File.separator + resourcePath);

        try {
            dispatcher.include(request, replaceWriteThread(response, printWriter));
        }
        catch (ServletException e)  {log.info(e.toString());}
        catch (IOException e)       {log.info(e.toString());}

        return stringWriter.toString();
    }

    private HttpServletResponseWrapper replaceWriteThread(final HttpServletResponse response, final PrintWriter printWriter) {
        return new HttpServletResponseWrapper(response) {
            @Override
            public PrintWriter getWriter() throws IOException {
                return printWriter;
            }
        };
    }

    private boolean createReleaseFolder(){

        FileManager folder = new FileManager(releaseFolderPath);
        
        releaseFolderPath = folder.releaseFolderPath;
        
        return folder.isReleaseFolderExist;
    }

    private String getReleaseName(String path){

        int pos = path.lastIndexOf("../");

        if(pos == -1) return path;

        return path.substring(pos+3);

    }

}
