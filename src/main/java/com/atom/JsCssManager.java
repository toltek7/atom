package com.atom;

import com.atom.release.Page;
import com.atom.release.Path;
import com.atom.release.Utils;

import java.util.*;

/**
 * Created by toltek77 on 13.01.2015.
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
 * NOTE: if <js></js> executes on "inline", than src placed on <head></head> automatically + dropped async and defer params
 * NOTE: html <script></script> is also supported but not processed thru this code
 */
public class JsCssManager {

    private Map<String, Map<String, boolean[]>> srcHolder;
    private Map<String, HashSet> codeHolder;
    private boolean isSorted; //indicated whether we sorted src by inits.tag order

    protected JsCssManager() {
        this.srcHolder = new LinkedHashMap<String, Map<String, boolean[]>>();
        this.codeHolder = new LinkedHashMap<String, HashSet>();
        this.isSorted = false;
    }

    /**
     * where - where to place <script scr="xxx"/> tag, can be values - head (in head)/body (at the bottom of the body)
     * src  - src attr of js files
     * on - event, to execute the code (document.ready or load or any custom event)
     * code - any code inside <script/> tag
     * async - async attribute of script
     * defer - defer attribute of script
     * mergeInSingleFile - if true, the script placed to the some common.js file and src link to this common.js file
     */
    public void put(String where, String src, String onEvent, String code, boolean async, boolean defer, boolean mergeInSingleFile) {

        Map<String, boolean[]> attrSet = new LinkedHashMap<String, boolean[]>();
        HashSet<String> codeSet = new HashSet<String>();

        //todo: default user values need to set
        boolean[] attributes = new boolean[]{async, defer, mergeInSingleFile};

        if (srcHolder.containsKey(where)) attrSet = srcHolder.get(where);
        attrSet.put(src, attributes);
        srcHolder.put(where, attrSet);

        if (codeHolder.containsKey(onEvent)) codeSet = codeHolder.get(onEvent);
        codeSet.add(code);
        codeHolder.put(onEvent, codeSet);

        isSorted = false;

    }

    /**
     * put css
     * */
    public void put(String where, String src, String code, boolean mergeInSingleFile) {
        put(where, src, null, code, false, false, mergeInSingleFile);
    }

    public void clean(){
        srcHolder.clear();
        codeHolder.clear();
        isSorted = false;
    }

    public String getHeadTags(String tagTemplate, String pagePath) {
        return collectTags(tagTemplate, "head", pagePath);
    }

    public String getBodyTags(String tagTemplate, String pagePath) {
        return collectTags(tagTemplate, "body", pagePath);
    }


    private String collectTags(String tagTemplate, String were, String currentPagePath) {
        Map<String, boolean[]> srcMap = srcHolder.get(were),
                               srcInHead = srcHolder.get("head");

        if(srcMap == null || srcMap.isEmpty()) return null;

        String attribute, src;
        StringBuilder scripts = new StringBuilder();
        boolean[] array;

        for (Map.Entry entry : srcMap.entrySet()) {
            src = (String) entry.getKey();

            //NOTE: check whether we have such script in head
            if(were.equals("head") || !(srcInHead != null && srcInHead.containsKey(src))){
                array = (boolean[]) entry.getValue();
                attribute = "";
                if (array[0])        attribute = "async=''";
                else if (array[1])   attribute = "defer=''";
                if (!array[2]) {//not mergeInSingleFile
                    Path path = new Path(src,currentPagePath);
                    scripts.append(String.format(tagTemplate, path.relatedPath, attribute));
                    //Utils.print("currentPagePath: " + currentPagePath);
                    //Utils.print("path.relatedPath: " + path.relatedPath);
                    //Utils.print(String.format(tagTemplate, path.relatedPath, attribute));
                }
                else {
                    //todo merge scrip in common file
                }

            }

        }


        return scripts.toString();
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
            iterator = ((HashSet) entry.getValue()).iterator();

            while (iterator.hasNext()) {
                inlineCode = (String) iterator.next();
                if (inlineCode != null && !inlineCode.isEmpty()) mergedCode.append(inlineCode.trim());
            }

            switch (onEvent) {
                case "ready":
                case "":     finalCode.append(String.format(Constants.JS_ONREADY_TEMPLATE, mergedCode)); break;
                case "load": finalCode.append(String.format(Constants.JS_ONLOAD_TEMPLATE, mergedCode));  break;
                default:     finalCode.append(String.format(Constants.JS_CUSTOM_TEMPLATE, onEvent, mergedCode));
            }

        }

        if (finalCode.length() > 0) {
            return String.format(Constants.SCRIPT_TAG_TEMPLATE, finalCode.toString()) + "\n";
        }

        return "";
    }



    public void sortByInitTagOrder(Set<String> orderedList){
        if(isSorted || orderedList == null ||  orderedList.isEmpty()) return;
        //print();
        sortTags("head",orderedList);
        sortTags("body",orderedList);
        //print();
        isSorted = true;
    }

    private void sortTags(String key, Set<String> orderedList){
        Map<String, boolean[]> sortedMap = new LinkedHashMap<String, boolean[]>();
        Map<String, boolean[]> curMap = srcHolder.get(key);
        if(curMap == null || curMap.isEmpty()) return;
        for(String s : orderedList){
            if(curMap.containsKey(s)){
                sortedMap.put(s,curMap.get(s));
            }
        }
        if(!sortedMap.isEmpty()){
            srcHolder.put(key,sortedMap);
        }
    }

    public void print() {
        boolean[] array;
        for (Map.Entry were : this.srcHolder.entrySet()) {
            System.out.println(were.getKey() + " scripts:");
            for (Map.Entry src : ((Map<String, boolean[]>) were.getValue()).entrySet()) {
                array = (boolean[]) src.getValue();
                System.out.println("   " + src.getKey());
                System.out.println("      " + array[0] + " " + array[1] + " " + array[2] + " ");
            }
        }
    }

}
