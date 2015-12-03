package com.atom.release;

import com.atom.Application;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by toltek7 on 28.12.2014.
 * NOTE: if income name = path1/path2/index.jspx;
 * than namePrefix = path1/path2/
 *      name = index.jspx
 *      nestingLvl = 2
 *      resourceFolder = path1
 *      resourcePathCorrection = ../ (=nestingLvl-1)
 *
 */
public class Page {

    private String name,
                   content,
                   language,
                   namePrefix = "",
                   resourcePathCorrection = "", // prefix to load resource from resourceFolder   
                   resourceFolder = "",     // place to copy resource
                   timeStamp="";

    private int nestingLvl;

    private boolean isDefaultLanguage;

    private ReleaseObject releaseProcessor;

    public Page(String name, String content, boolean isDefaultLanguage, String language, ReleaseObject releaseProcessor) {
        
        this.name               = name;
        this.content            = content;
        this.isDefaultLanguage  = isDefaultLanguage;
        this.language           = language;
        this.releaseProcessor   = releaseProcessor;

        //todo out timeStemp to delivery level
        if(releaseProcessor.setTimeStamp){
            this.timeStamp      = "_ts=" + Long.toString(System.currentTimeMillis());
        }


        setVariablesValues();

        process();
    }

    private void setVariablesValues(){

        name = name.split("\\.")[0] + ".html";

        int pos = name.lastIndexOf(File.separator) + 1;

        nestingLvl = Utils.countOfSymbolsInString(File.separator, name);

        // NOTE: wee need to put lang correction before the file name, so project1/index.jsp -> project1/en/index.jsp
        if(!isDefaultLanguage) namePrefix = language + File.separator;

        if(pos != 0){
            if(nestingLvl <= 1){
                resourceFolder = name.substring(0,pos);
                namePrefix = resourceFolder + namePrefix;
            }else{
                String tmpNamePrefix  = name.substring(0,pos);
                int tmpPos = tmpNamePrefix.indexOf(File.separator) + 1;
                resourceFolder = tmpNamePrefix.substring(0,tmpPos);
                namePrefix = resourceFolder + namePrefix + tmpNamePrefix.substring(tmpPos);
            }
            name = name.substring(pos);
        }

        setResourcePathCorrection(); 
    }

    private void setResourcePathCorrection(){

        for(int i = 0; i <nestingLvl - 1;i++) resourcePathCorrection += "../";
        
        if(!isDefaultLanguage) resourcePathCorrection += "../";
    }

    public void save() throws IOException {

        File file = FileManager.createFile(getDestPath());

        if (file.exists()) Utils.print("Warning[Page:save]: file " + file.getAbsolutePath() + " has been rewritten.");

        FileUtils.writeStringToFile(file, content, "UTF-8");

        Utils.print("File " + file.getAbsolutePath() + " saved.");
    }

    private String getDestPath() {
        return releaseProcessor.releaseFolderPath + File.separator + namePrefix + name;
    }

    private void process(){
        
        if (content == null) return;

        org.jsoup.nodes.Document doc = Jsoup.parse(content, "UTF-8");

        processResources(doc);

        content = getProcessedContent(doc);
        
    }

    private void processResources(org.jsoup.nodes.Document doc){             

        //correct css path
        releaseProcessor.resourcesManager.addCss(processResourcesPaths("css", "href", timeStamp,
                doc.getElementsByAttributeValue("type", "text/css")));

        //correct js path
        releaseProcessor.resourcesManager.addJs(processResourcesPaths("js", "src", timeStamp,
                doc.getElementsByAttributeValue("type", "text/javascript")));

        //correct images path
        releaseProcessor.resourcesManager.addImages(processResourcesPaths("i", "src", "",
                doc.select("img")));

        //correct style background-image
//        releaseProcessor.resourcesManager.addJs(processResourcesPaths("i", "src",
//                doc.getElementsByAttributeValue("type", "text/javascript")));
        
    }

    private Map<String, String> processResourcesPaths(String resType, String attr,  String query, Elements resources){

        String  pathFromMarkup,
                pathToRootFolder,
                realPath,
                copyPath;

        Map<String, String> resourceHolder = new HashMap<String, String>();

        for(Element res: resources){

            pathFromMarkup = res.attr(attr);

            pathToRootFolder = getPathFromResourceFolder(pathFromMarkup, resType);

            //NOTE: null means that resource external
            if(pathToRootFolder != null){

                if(isDefaultLanguage){

                    // NOTE: for others lang resources the same (NOTE: can be exeption on images)
                    realPath = FileManager.getCanonicalPath(Application.root + namePrefix + pathFromMarkup);
                    copyPath = FileManager.getCanonicalPath(releaseProcessor.releaseFolderPath + File.separator + resourceFolder + pathToRootFolder);
                    resourceHolder.put(copyPath, realPath);
                }

                pathToRootFolder = FileManager.addQueryParamToPAth(pathToRootFolder,query);

                res.attr(attr, resourcePathCorrection + pathToRootFolder);
            }
        }

        return resourceHolder;
    }

    private String getProcessedContent(org.jsoup.nodes.Document doc){

        String content;

   /*     if(releaseProcessor.isCompressedHTML){
            FileCompressor.removeComments(doc);
            doc.outputSettings().prettyPrint(false);
        }*/

        content = doc.html();

        if(releaseProcessor.isCompressedHTML) content = FileCompressor.compressHTML(content);

      /*  if(releaseProcessor.isCompressedHTML) {
            content = content.replaceAll("\n","");
            content = content.replaceAll("\\s+"," ");
        }*/

        return content.replaceAll(".jspx",".html");
    }

    private String getPathFromResourceFolder(String path, String symbol){

        if(path.contains("http") || path.contains("https")) return null;

        int pos = path.indexOf(symbol);

        if(pos != -1) return path.substring(pos);

        return path;
    }

    public void print() {
        System.out.println("name:                   " + name);
        System.out.println("namePrefix:             " + namePrefix);
        System.out.println("nestingLvl:             " + nestingLvl);
        System.out.println("resourcePathCorrection: " + resourcePathCorrection);
        System.out.println("language:               " + language);
        System.out.println("isDefaultLanguage:      " + isDefaultLanguage);
        System.out.println("");
    }
}
