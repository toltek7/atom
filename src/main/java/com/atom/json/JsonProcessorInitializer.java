package com.atom.json;

import com.atom.Application;
import com.atom.Constants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;
import java.io.File;

/**
 * Created by toltek7 on 27.12.2014.
 */
public class JsonProcessorInitializer implements ServletContextListener{

    private static Logger log = Logger.getLogger(JsonProcessorInitializer.class.getName());

    public void contextInitialized(ServletContextEvent event){

        ServletContext context = event.getServletContext();

        String jsonFolderName = Application.getProperty(Constants.JSON_CONFIG_FOLDER_NAME, Constants.JSON_DEFAULT_FOLDER_NAME);

        File dir = getFolder(context.getRealPath(jsonFolderName));

        JsonProcessor.createInstance(dir);
    }

    public void contextDestroyed(ServletContextEvent event){}

    public File getFolder(String folderName){

        File folder = new File(folderName);

        if(folder.exists() && folder.isDirectory()) return folder;
        else{
            log.info("Error[JsonInitializer]: could not find folder: " + folderName);
            return null;
        }

    }
}
