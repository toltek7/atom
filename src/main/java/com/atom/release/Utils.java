package com.atom.release;

import com.atom.Constants;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by toltek7 on 08.01.2015.
 */
public class Utils {

    public static void print(String msg){
        System.out.println(msg);
    }

    public static int countOfSymbolsInString(String symbol, String s){
        int counter = 0;
        char sym = symbol.charAt(0);
        for( int i=0; i<s.length(); i++ ) {
            if( s.charAt(i) == sym ) {
                counter++;
            }
        }
        return counter;
    }

    public static void insertMarkupBeforeNode(String node, StringBuilder html, String markup) {
        int position = html.indexOf(node);
        if(position > 0){
            html.insert(position, markup);
        }
    }

    public static String trimString(String str){
        if (str == null) return null;
        return str.replaceAll("\\s", " ").trim();
    }

    public static boolean isJspRequest(ServletRequest request){
        String path = ((HttpServletRequest)request).getServletPath();
        return path.contains(".html") || path.contains(".jspx");
    }
}
