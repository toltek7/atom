package com.atom;

import com.atom.release.FileManager;
import com.atom.release.Path;
import com.atom.release.Utils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * Created by toltek7 on 13.01.2015.
 * <p>
 * by using <js></js> tag we collect all scripts in page and put them
 * (already filtered by inits's file order and unique value) in HTML
 * <p>
 * <js></js>- can be placed in:
 * 1. <head></head>
 * 2. end of <body></body> (default - end of body)
 * 3. or any place of HTML
 * <p>
 * <js></js> can have inline code inside, this code can executes on
 * 1. document.addEventListener("DOMContentLoaded" (by default) NOTE: not supported in IE8-
 * 2. window.onload or any other custom event
 * 3. immediately at the page placement (like inline)
 * <p>
 * NOTE: if <js></js> executes on "inline" (+code -on), than src placed on <head></head> automatically + dropped async and defer params
 * NOTE: html <script></script> also can be used, but will not be collected in delivery
 */
//enum tagType { JS_HEAD, JS_BODY, CSS_HEAD, CSS_BODY}
public class JsCssManager {

    //NOTE: in mergedSrcHolder store files that should be merged in one, after all parsings complited
    private Map<String, Map<String, boolean[]>> srcHolder, mergedSrcHolder;
    private Map<String, LinkedHashSet> codeHolder;
    private String tagTemplate, mergedHeadFile, mergedBodyFile;

    protected JsCssManager(String tagTemplate, String headFile, String bodyFile) {
        this.srcHolder = new LinkedHashMap<>();
        this.mergedSrcHolder = new LinkedHashMap<>();
        this.codeHolder = new LinkedHashMap<>();
        this.tagTemplate = tagTemplate;
        this.mergedHeadFile = headFile;
        this.mergedBodyFile = bodyFile;
    }

    /**
     * where - where to place <script scr="xxx"/> tag, can be values - head (in head)/body (at the bottom of the body)
     * src  - src attr of js files
     * on - event, to execute the code (document.ready or load or any custom event)
     * code - any code inside <script/> tag
     * async - async attribute of script
     * defer - defer attribute of script
     * mergeInSingleFile - if true, the script placed to the some common.js file and src link to this common.js file
     * inline - if true js puts on page inline, no processing in manager
     */
    public void save(Boolean inHead, String src, String onEvent, String code, boolean async, boolean defer, boolean mergeInSingleFile, boolean inline) {
        boolean[] attr = new boolean[]{async, defer, mergeInSingleFile, inline};
        attr = _saveSrc(src, attr, _getWhere(inHead));
        //process inline attribute, if it is true, means it is already on page, res from head and body should be removed
        if(attr[3]){
            _reWriteSrc(src, attr, _getAntiWhere(inHead));
        }
        //Utils.print(srcHolder,"-save ");
        //save inline code, key=onEvent
        LinkedHashSet<String> codeSet = new LinkedHashSet<>();
        if (codeHolder.containsKey(onEvent)) codeSet = codeHolder.get(onEvent);
        codeSet.add(code);
        codeHolder.put(onEvent, codeSet);
    }

    /**
     * put css
     * */
    public void save(Boolean inHead, String src, String code, boolean mergeInSingleFile, boolean inline) {
        save(inHead, src, null, code, false, false, mergeInSingleFile, inline);
    }

    private boolean[] _saveSrc(String src, boolean[] attributes, String where){
        Map<String, boolean[]> attrSet = new LinkedHashMap<>();
        if (srcHolder.containsKey(where)) attrSet = srcHolder.get(where);
        if (attrSet.containsKey(src)){
            boolean[]  array = attrSet.get(src);
            attributes[Constants.ASYNC] = array[Constants.ASYNC];
            attributes[Constants.DEFER] = array[Constants.DEFER];
            attributes[Constants.MERGE] = attributes[Constants.MERGE] || array[Constants.MERGE]; //merge attribute, if some script has it, others also should have it
            attributes[Constants.INLINE] = attributes[Constants.INLINE] || array[Constants.INLINE]; //inline attribute, if some script has it, others also should have it
        }
        attrSet.put(src, attributes);
        srcHolder.put(where, attrSet);
        return attributes;
    }

    //to fix problem with inline attribute
    private void _reWriteSrc(String src, boolean[] attributes, String where){
        Map<String, boolean[]> attrSet = new LinkedHashMap<>();
        if (srcHolder.containsKey(where)) attrSet = srcHolder.get(where);
        if (attrSet.containsKey(src)){
            attrSet.put(src, attributes);
            srcHolder.put(where, attrSet);
        }
    }

    public boolean[] getSrc(String where, String src){
        Map<String, boolean[]> attrSet = new LinkedHashMap<>();
        if (srcHolder.containsKey(where)) attrSet = srcHolder.get(where);
        if (attrSet.containsKey(src)) attrSet.get(src);
        return null;
    }

    private String _getWhere(Boolean inHead){
        if(!inHead) return "body";
        return "head";
    }

    private String _getAntiWhere(Boolean inHead){
        if(inHead) return "body";
        return "head";
    }

    public void clear(){
        Utils.print(srcHolder, "----------------------------------------------get head print ");
        srcHolder.clear();
        codeHolder.clear();
        if(!Application.isProductionBuild){ //if production build, we store all merged files from all pages and only after build finished, clear it.
            clearMergedData();
        }
    }

    public void clearMergedData(){
        mergedSrcHolder.clear();
    }

    public String getHeadTags(String pagePath) {
        String result = getTags("head", mergedHeadFile, pagePath);
        if(!Application.isProductionBuild) {
            //Utils.print(srcHolder, "get head print ");
            writeMergedFile("head", mergedHeadFile, pagePath);
        }
        return result;
    }

    public String getBodyTags(String pagePath) {
        String result = getTags("body", mergedBodyFile, pagePath);
        if(!Application.isProductionBuild) {
            //Utils.print(srcHolder, "get head print ");
            writeMergedFile("body", mergedBodyFile, pagePath);
        }
        return result;
    }

    private String getTags(String where, String mergedFile, String currentPagePath)  {
        Boolean isHeadTag = where.equals("head");
        Map<String, boolean[]> srcMap = srcHolder.get(where),
                               srcInHead = srcHolder.get("head");
        if(srcMap == null || srcMap.isEmpty()) return null;
        String attribute, src;
        StringBuilder tags = new StringBuilder();
        boolean[] array;
        boolean mergedFileAdded = false; //indicate id merged tag already added to the page

        Iterator<Map.Entry<String, boolean[]>> iter = srcMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, boolean[]> entry = iter.next();
            array = entry.getValue();
            if(array[Constants.INLINE]){
                //inline code, already added on page in JsTag -> nothing to do, remove this src
                iter.remove();
                continue;
            }
            src =  entry.getKey();
            //NOTE: check whether we have such script in head
            if(isHeadTag || !(srcInHead != null && srcInHead.containsKey(src))){
                //set async or defer attr, defer has more force
                attribute = "";
                if (array[Constants.ASYNC])        attribute = "async=''";
                else if (array[Constants.DEFER])   attribute = "defer=''";
                if(!array[Constants.MERGE]){//not mergeInSingleFile
                    //Path path = new Path(src,currentPagePath);
                    tags.append(String.format(tagTemplate, Utils.getRelatedPath(src,currentPagePath), attribute));
                    //Utils.print("currentPagePath: " + currentPagePath);
                    //Utils.print("path.relatedPath: " + path.relatedPath);
                    //Utils.print(String.format(tagTemplate, path.relatedPath, attribute));
                    iter.remove();// remove it to not store in merged holder
                    if(Application.isProductionBuild){
                        //todo: save file to delivery folder if it is not exist in it
                    }
                }
                else{
                    //can be 2 merged files - in head and in body
                    if(!mergedFileAdded){
                        mergedFileAdded = true;
                        //todo not clear what to do in delivery, can be a problems
                        String mergedFilePath = Path.getMergedFilePath(mergedFile, currentPagePath);
                        tags.append(String.format(tagTemplate, Utils.getRelatedPath(mergedFilePath,currentPagePath), ""));
                    }
                }
            }else{
                iter.remove(); // mean that it is body iteration and such file already exist in head
            }
        }
        mergedSrcHolder.put(where,srcMap); // store for delivery, when we need full list of files
        return tags.toString();
    }

    public void writeMergedFile(String were, String mergedFile, String currentPagePath) {
        Map<String, boolean[]> srcMap = mergedSrcHolder.get(were);
        if(srcMap == null || srcMap.isEmpty()) return;

        String mergedFilePath;
        if(Application.isProductionBuild){
            mergedFilePath = Application.root + Application.deliveryPath + Application.projectFolder + mergedFile;
        }else{
            mergedFilePath = Application.root + Path.getMergedFilePath(mergedFile, currentPagePath);
        }
        FileManager.removeFile(mergedFilePath);
        Iterator<Map.Entry<String, boolean[]>> iter = srcMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, boolean[]> entry = iter.next();
            String src = entry.getKey();
            try {
                FileManager.appendToFile(Application.root + src, mergedFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getInlineCode() {
        StringBuilder finalCode = new StringBuilder(),
                      mergedCode = new StringBuilder();
        String onEvent, inlineCode;
        Iterator iterator;
        for (Map.Entry entry : codeHolder.entrySet()) {
            mergedCode.setLength(0);
            mergedCode.trimToSize();
            onEvent = (String) entry.getKey();
            iterator = ((LinkedHashSet) entry.getValue()).iterator();
            while (iterator.hasNext()) {
                inlineCode = (String) iterator.next();
                if (inlineCode != null && !inlineCode.isEmpty()) mergedCode.append(inlineCode.trim());
            }
            if(StringUtils.isNotBlank(onEvent)){
                switch (onEvent) {
                    case "ready":finalCode.append(String.format(Constants.JS_ONREADY_TEMPLATE, mergedCode)); break;
                    case "load": finalCode.append(String.format(Constants.JS_ONLOAD_TEMPLATE, mergedCode));  break;
                    default:     finalCode.append(String.format(Constants.JS_CUSTOM_TEMPLATE, onEvent, mergedCode));
                }
            }
        }
        if (finalCode.length() > 0) {
            return String.format(Constants.SCRIPT_TAG_TEMPLATE, finalCode.toString()) + "\n";
        }
        return "";
    }

    public void sort(Set<String> src, Set<String> code){
        if(src != null && !src.isEmpty()){
            sortSrcByOrder(src);
        }
        if(code != null && !code.isEmpty()){
            sortCodeByOrder(code);
        }
    }

    private void sortSrcByOrder(Set<String> orderedList){
        Iterator<Map.Entry<String, Map<String, boolean[]>>> iterMain = srcHolder.entrySet().iterator();
        while (iterMain.hasNext()) {
            Map.Entry<String, Map<String, boolean[]>> entryMain = iterMain.next();
            Map<String, boolean[]> srcMap = entryMain.getValue();
            Map<String, boolean[]> sortedMap = new LinkedHashMap<>();
            //firstly detect src that not in order list, but on page (example external scripts)
            String src;
            Iterator<Map.Entry<String, boolean[]>> iter = srcMap.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<String, boolean[]> entry = iter.next();
                src = entry.getKey();
                if(!orderedList.contains(src)){
                    sortedMap.put(src,entry.getValue());
                    iter.remove(); //remove it from future analization
                }
            }
            //now sort other src
            for(String s : orderedList){
                if(srcMap.containsKey(s)){
                    sortedMap.put(s,srcMap.get(s));
                }
            }
            //rewrite main holder
            if(!sortedMap.isEmpty()){
                srcHolder.put(entryMain.getKey(),sortedMap);
            }
        }

      /*  Map<String, boolean[]> srcMap = srcHolder.get(key);
        if(srcMap == null || srcMap.isEmpty()) return;
        Map<String, boolean[]> sortedMap = new LinkedHashMap<>();
        //firstly detect src that not in order list, but on page (example external scripts)
        String src;
        Iterator<Map.Entry<String, boolean[]>> iter = srcMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, boolean[]> entry = iter.next();
            src = entry.getKey();
            //Utils.print("---" + src);
            if(!orderedList.contains(src)){
                //Utils.print("---" + src);
                sortedMap.put(src,entry.getValue());
                iter.remove(); //remove it from future analization
            }
        }
        //now sort other src
        for(String s : orderedList){
            if(srcMap.containsKey(s)){
                sortedMap.put(s,srcMap.get(s));
            }
        }
        //rewrite main holder
        if(!sortedMap.isEmpty()){
            srcHolder.put(key,sortedMap);
        }*/
        //Utils.print(srcHolder, "sorted print ");
    }

    private void sortCodeByOrder(Set<String> orderedList){
        Iterator<Map.Entry<String, LinkedHashSet>> iterMain = codeHolder.entrySet().iterator();
        while (iterMain.hasNext()) {
            Map.Entry<String, LinkedHashSet> entryMain = iterMain.next();
            LinkedHashSet codes = entryMain.getValue();
            LinkedHashSet <String> sortedMap = new LinkedHashSet<>();
            //firstly detect code that not in order list, but on page (example external scripts)
            Iterator iter = codes.iterator();
            while (iter.hasNext()) {
                String code = (String) iter.next();
                if(!orderedList.contains(code)){
                    sortedMap.add(code);
                    iter.remove(); //remove it from future analization
                }
            }
            //now sort other src
            for(String s : orderedList){
                if(codes.contains(s)){
                    sortedMap.add(s);
                }
            }
            //rewrite main holder
            if(!sortedMap.isEmpty()){
                codeHolder.put(entryMain.getKey(),sortedMap);
            }
            //Utils.print(codeHolder.get(entryMain.getKey()),"sorted map after");
        }

    }
}
