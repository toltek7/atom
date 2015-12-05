package com.atom.release;

import com.atom.Constants;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Set;

/**
 * Created by toltek7 on 08.01.2015.
 */
public class Utils {

    public static void print(String msg){
        System.out.println(msg);
    }

    public static void print(Map<String, Map<String, boolean[]>> map, String message) {
        boolean[] array;
        for (Map.Entry were : map.entrySet()) {
            System.out.println(message + were.getKey());
            for (Map.Entry src : ((Map<String, boolean[]>) were.getValue()).entrySet()) {
                array = (boolean[]) src.getValue();
                System.out.println("   " + src.getKey());
                System.out.println("      " + array[0] + " " + array[1] + " " + array[2] + " " + array[3]);
            }
        }
    }

    public static void print(Set<String> set, String message){
        System.out.println(message);
        for(String s: set){
            System.out.println(s);
        }
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
