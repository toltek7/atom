package com.atom.release;
import java.io.File;
/**
 * Created by toltek7 on 22.07.2015.
 *
 * In project webapp supposed to be a root folder
 * In <js>/<css> tags and delivery.xml "src" build base on this fact.
 * As <js>/<css> tags can be placed at the pages with different project nesting lvl
 * We should process "src" to be valid in particular page.
 *
 * Example:
 *   <js scr="project1/js/file.js">(project path) placed to the next pages:
 *   index.jspx(currentPage)                --> <script src="project1/js/file.js"/>(related paths)
 *   project1/index1.jspx                   --> <script src="js/file.js"/>
 *   project1/js/index1.jspx                --> <script src="file.js"/>
 *   project1/sub-folder/index-sub.jspx     --> <script src="../js/file.js"/>
 *   project2/index2.jspx                   --> <script src="../project1/js/file.js"/>
 *   project2/sub-folder/index-sub.jspx     --> <script src="../../project1/js/file.js"/>
 *
 *   class @Path can make such transformations
 *
 *
 * NOTE: if income name = path1/path2/index.jspx;
 * than namePrefix = path1/path2/
 *      name = index.jspx
 *      nestingLvl = 2
 *      resourceFolder = path1
 *      resourcePathCorrection = ../ (=nestingLvl-1)
 */


/**
 * Created by toltek7 on 28.12.2014.

 *
 */
public class Path {

    public String projectPath;
    public String relatedPath;
    public String currentPage;

    public Path(String projectPath, String currentPage){
        this.projectPath = validatePath(projectPath);
        this.currentPage = validatePath(currentPage);
        this.relatedPath = setRelatedPath();
//        Utils.print("Page");
//        Utils.print(this.projectPath);
//        Utils.print(this.currentPage);
//        Utils.print(this.relatedPath);
    }

    private String validatePath(String path){
        path = path.replace("\\","/");
        if(path.charAt(0) == '/'){
            path = path.substring(1);
        }
        return path;
    }

    private String setRelatedPath(){

        int nestedOfCurPage = 0, i=0;

        String[] parts = this.projectPath.split("/");

        for (String str: this.currentPage.split("/")){
            if(i< parts.length  && str.equals(parts[i])){//if the same, remove it from projectPath
                parts[i++] = null;
            }else{
                nestedOfCurPage++;
            }
        }
        nestedOfCurPage--;// as we have then in +1 more

        StringBuilder result = new StringBuilder();

        for(i=0;i<nestedOfCurPage;i++) result.append("../");

        for(i=0;i<parts.length;i++){
            if(parts[i]!=null){
                result.append(parts[i]);
                if(i!=parts.length-1){
                    result.append("/");
                }
            }
        }

        return result.toString();
    }

    /**
     * if pagePath = /project/*.../page.html -> mergedJsPath = project/js/name
     * if pagePath = /page.html -> mergedJsPath = /js/name
     * the same works for CSS
     * NOTE: 100% works only for single page, fro pro build need to check
     */
    public static String getMergedFilePath(String name, String pagePath){
        String[] parts = pagePath.split("/");
        if(parts.length <= 1) return name;
        return parts[1] + name;
    }

}
