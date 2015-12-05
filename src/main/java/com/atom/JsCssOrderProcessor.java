package com.atom;

import com.atom.Application;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by toltek77 on 07.09.2015. *
 * jsp technology puts tags to the page in the order they are on the page, but it is bad for JS and CSS tags
 * So, this class process init*.tagx file, get order of JS and CSS in the order they put in init*.tagx
 * Than we use this order when put resources into the page
 * NOTE: currently project works only with one init.tagx file (its name placed in config.txt)
 */

public class JsCssOrderProcessor {

    public Set<String> js,jsCode,css,cssCode;

    public JsCssOrderProcessor(){
        js = new LinkedHashSet<>();
        css = new LinkedHashSet<>();
        jsCode = new LinkedHashSet<>();
        cssCode = new LinkedHashSet<>();
    }

    public void getFilesOrder(String path){

        //System.out.println("  ---   getFilesOrder   ---  ");

        File input = new File(Application.root + File.separator + path);
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.parse(input, "UTF-8");
            js = collectFilesByTagName(doc,"atom:js",jsCode);
            css = collectFilesByTagName(doc,"atom:css",cssCode);
//            print(js);
//            print(css);
            //print(jsCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public LinkedHashSet<String> collectFilesByTagName(org.jsoup.nodes.Document doc, String tagName, Set<String> code){
        LinkedHashSet<String> set = new LinkedHashSet<String>();
        Elements tags = doc.getElementsByTag(tagName);
        String text;
        for(Element tag: tags){
            set.add(tag.attr("src"));
            text = tag.text().replaceAll("\\s", " ").trim();
            if(text!=null && !text.isEmpty()){
                code.add(text);
            }
        }
        return set;
    }

    public void cleanFilesOrder(){
        js.clear();
        css.clear();
    }

    public boolean isEmpty(){
        return js.isEmpty() && css.isEmpty();
    }

    public void print(Set<String> set){
        System.out.println("inits order");
        for(String s: set){
            System.out.println(s);
        }
    }
}
