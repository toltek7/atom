package com.atom.json;

import com.atom.Application;
import com.atom.Constants;
import com.atom.release.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by toltek7 on 27.12.2014.
 */
public class JsonProcessorRequestListener implements ServletRequestListener {

    public void requestInitialized(ServletRequestEvent event) {
        ServletRequest request = event.getServletRequest();
        if(Utils.isJspRequest(request)){
            reloadJsonData(Application.getCurrentLanguage(request), event.getServletContext());
        }
    }

    public void reloadJsonData(String lang, ServletContext context) {
        JsonProcessor jsonProcessor = JsonProcessor.getInstance();
        context.setAttribute(getJsonRootName(), jsonProcessor.getData(lang));
        //System.out.println("--------------------------    reloadJsonData  ------------------");
    }

    public void requestDestroyed(ServletRequestEvent event) {
        if(Utils.isJspRequest(event.getServletRequest())){
            ServletContext context = event.getServletContext();
            context.setAttribute(getJsonRootName(), null);
            //System.out.println("----------------------    JsonProcessorRequestListener  requestDestroyed -------");
        }
    }

    private String getJsonRootName(){
        return Application.getProperty(Constants.JSON_CONFIG_ROOT_VARIABLE_NAME,
                                       Constants.JSON_DEFAULT_ROOT_VARIABLE_NAME);
    }


}


