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

    private static HttpServletRequest _request;
    private static Application instance;
    private static ServletContext context;
    public static String currentPage;

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
        this.properties = properties;
        this.root       = context.getRealPath(File.separator);
        this.context    = context;
        this.jsTags = new JsCssManager();
        this.cssTags = new JsCssManager();
        this.srcOrder = new JsCssOrderProcessor();
        this.currentPage = null;
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

    public static void putJs(String where, String src,  String on, String code, boolean async, boolean defer, boolean mergeInSingleFile){
        jsTags.put(where, src, on, code, async, defer, mergeInSingleFile);
    }

    public static void putCss(String where, String src, String code, boolean mergeInSingleFile){
        cssTags.put(where, src, code, mergeInSingleFile);
    }

    public static void removeResourceTags(){
        jsTags.clean();
        cssTags.clean();
    }

    public static String getHeadCssTags(){
       // Utils.print();
        System.out.println("getHeadCssTags");
        System.out.println(getCurrentPagePath());
//        System.out.println(_request.getServletPath());
        return cssTags.getHeadTags(Constants.CSS_TAG_TEMPLATE, getCurrentPagePath());//_request.getServletPath() - current page path, from webinf folder
    }

    public static String getBodyCssTags(){
        return cssTags.getBodyTags(Constants.CSS_TAG_TEMPLATE, getCurrentPagePath());
    }

    public static String getHeadJsTags(){
        return jsTags.getHeadTags(Constants.JS_TAG_TEMPLATE, getCurrentPagePath());//_request.getServletPath() - current page path, from webinf folder
    }
    
    public static String getBodyJsTags(){
        return jsTags.getBodyTags(Constants.JS_TAG_TEMPLATE, getCurrentPagePath());
    }

    public static String getJsInlineCode(){
        return jsTags.getInlineCode();
    }

    public static void orderResourceFilesByInitTag(){
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
        jsTags.print();
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
