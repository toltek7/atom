package com.atom;

import com.atom.release.Utils;
import com.sun.corba.se.spi.protocol.LocalClientRequestDispatcher;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by toltek7 on 12.01.2015.
 */
public class JsCssRequestListener implements  ServletRequestListener {

    public void requestInitialized(ServletRequestEvent event) {


       // event.getServletContext();
       //
        ServletRequest request = event.getServletRequest();
//        if(Utils.isJspRequest(event.getServletRequest())){}

        Application.setRequest((HttpServletRequest)request);

        HttpServletRequest requestHttp = (HttpServletRequest)request;
        System.out.println("---------------999------------------------------------- requestInitialized JsCssRequestListener ");
        System.out.println(requestHttp.getParameter(Constants.URL_BUILD_NAME));

        System.out.println("servelet path ---------------------------------------------- " + requestHttp.getServletPath());//that we need


    }
    public void requestDestroyed(ServletRequestEvent event) {
        Application.clearRequest();
        System.out.println("requestDestroyed JsCssRequestListener");
        if(!Application.isProductionBuild){
            Application.removeResourceTags();
            Application.cleanInitsFilesOrder();
        }
    }
}
