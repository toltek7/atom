package com.atom;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Properties;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by toltek7 on 27.12.2014.
 */
public class Application {

    public static Properties properties;
    public static String root;
    public static boolean isProductionBuild;
    public static boolean forceJsMerge; //merge JS in single file no matter what value set in its tag

    private static HttpServletRequest _request;
    private static Application instance;
    private static ServletContext context;
    public static String currentPage;
    public static String timeStamp;
    public static String deliveryPath;
    public static String projectFolder;

    private static JsCssManager jsTags, cssTags;

    private static JsCssOrderProcessor srcOrder;

    public static Application getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Application is not initialized");
        }
        return instance;
    }

    public static synchronized Application createInstance(Properties properties, ServletContext context) {
        instance = new Application(properties, context);
        return instance;
    }

    protected Application(Properties properties, ServletContext context) {

        String mergedHeadJs = properties.getProperty(Constants.CONFIG_MAIN_JS_HEAD_FILE, Constants.DEFAULT_MAIN_JS_HEAD_FILE),
               mergedHeadCss = properties.getProperty(Constants.CONFIG_MAIN_CSS_HEAD_FILE, Constants.DEFAULT_MAIN_CSS_HEAD_FILE),
               mergedBodyJs = properties.getProperty(Constants.CONFIG_MAIN_JS_BODY_FILE, Constants.DEFAULT_MAIN_JS_BODY_FILE),
               mergedBodyCss = properties.getProperty(Constants.CONFIG_MAIN_CSS_BODY_FILE, Constants.DEFAULT_MAIN_CSS_BODY_FILE);

        this.properties = properties;
        this.root       = context.getRealPath(File.separator);
        this.context    = context;
        this.jsTags = new JsCssManager(Constants.JS_TAG_TEMPLATE,mergedHeadJs,mergedBodyJs);
        this.cssTags = new JsCssManager(Constants.CSS_TAG_TEMPLATE,mergedHeadCss,mergedBodyCss);
        this.srcOrder = new JsCssOrderProcessor();
        this.currentPage = null;
        this.forceJsMerge = true;

    }

    public static void setRequest(HttpServletRequest request){
        _request = request;
    }

    public static ServletRequest getRequest(){
        return _request;
    }

    public static void clearRequest(){
        _request = null;
    }

    public static boolean setBuildType(String query){
        isProductionBuild = false;
        if(!isBlank(query) && query.equalsIgnoreCase(Constants.BUILD_TYPE_PRO) ||
           getProperty(Constants.APPLICATION_BUILD_TYPE,Constants.DEFAULT_BUILD_TYPE).equalsIgnoreCase(Constants.BUILD_TYPE_PRO)     )
            isProductionBuild = true;
        return isProductionBuild;
    }

    public static void putJs(String where, String src,  String on, String code, boolean async, boolean defer, boolean mergeInSingleFile, boolean inline){
        jsTags.put(where, src, on, code, async, defer, mergeInSingleFile, inline);
    }

    public static void putCss(String where, String src, String code, boolean mergeInSingleFile, boolean inline){
        cssTags.put(where, src, code, mergeInSingleFile, inline);
    }

    public static void clearResourcesHolders(){
        jsTags.clear();
        cssTags.clear();
    }

    public static String getCssInHead(){
       // Utils.print();
        System.out.println("getHeadCssTags");
        System.out.println(getCurrentPagePath());
//        System.out.println(_request.getServletPath());

        return cssTags.getHeadTags(getCurrentPagePath());//_request.getServletPath() - current page path, from webinf folder
    }

    public static String getCssInBody(){
        return cssTags.getBodyTags(getCurrentPagePath());
    }

    public static String getJsInHead(){
        return jsTags.getHeadTags(getCurrentPagePath());
    }
    
    public static String getJsInBody(){
        return jsTags.getBodyTags(getCurrentPagePath());
    }

    public static String getJsInlineCode(){
        return jsTags.getInlineCode();
    }

    public static void streamlineResourcesOrder(){
        if(!srcOrder.isEmpty()){
            jsTags.sortByInitTagOrder(srcOrder.js);
            cssTags.sortByInitTagOrder(srcOrder.css);
        }
    }

    public static String getCurrentPagePath(){
        //solved the problem with delivery
        if(currentPage!= null) return currentPage;
        return _request.getServletPath();
    }

    public static void printJsTags(){
//        jsTags.print();
    }
    

    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static void createListOfFilesOrder(){
        if(srcOrder.isEmpty()){
            srcOrder.getFilesOrder(Application.getProperty(Constants.CONFIG_INIT_FILE, Constants.DEFAULT_INIT_FILE)) ;
        }
    }

    public static void cleanInitsFilesOrder(){
        srcOrder.cleanFilesOrder();
    }


    public static String getCurrentLanguage(ServletRequest request) {

        String result = request.getParameter(Constants.URL_LANGUAGE_PARAM);

        if (isBlank(result)) {
            result = properties.getProperty(Constants.JSON_CONFIG_LANGUAGE, Constants.JSON_DEFAULT_LANGUAGE);
        }

        return result;
    }

    public static ServletContext getContext() {
        return context;
    }

}
