package com.atom;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by toltek7 on 12.01.2015.
 */
public class JsCssRequestListener implements  ServletRequestListener {

    public void requestInitialized(ServletRequestEvent event) {

        ServletRequest request = event.getServletRequest();
        Application.setRequest((HttpServletRequest)request);
        HttpServletRequest requestHttp = (HttpServletRequest)request;
        //System.out.println("---------------999------------------------------------- requestInitialized JsCssRequestListener ");
        //System.out.println(requestHttp.getParameter(Constants.URL_BUILD_NAME));

        System.out.println("servelet path --------- " + requestHttp.getServletPath());//that we need


    }
    public void requestDestroyed(ServletRequestEvent event) {
        Application.clearRequest();
        System.out.println("requestDestroyed JsCssRequestListener");

    }
}
