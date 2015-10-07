package com.atom.release;

import com.atom.Application;
import com.atom.Constants;
import org.xml.sax.SAXException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by toltek7 on 28.12.2014.
 */
public class ReleaseManagerStart extends HttpServlet {
    @Override
    public void init() throws ServletException
    {
       // System.out.println("----------");
       // System.out.println("---------- CrunchifyExample Servlet Initialized successfully ----------");
        //System.out.println("----------");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        buildRelease(request, response);
    }

    public void buildRelease(HttpServletRequest request, final HttpServletResponse response) {

        System.out.println("\nHTML release building .....");
        long startTime = System.currentTimeMillis();



        String xmlFilePath = Application.root + Application.getProperty(Constants.CONFIG_RELEASE_FILE, Constants.DEFAULT_RELEASE_FILE);
     
        File file = getXmlFile(xmlFilePath);

        ReleaseManager releaseManager = ReleaseManager.createInstance(file);

        try {
            releaseManager.buildRelease(request, response);
            long endTime = System.currentTimeMillis();
            System.out.println("Release builded in " + (endTime - startTime)+ " milliseconds.");
            return;
        }
        catch (IOException e) {
            System.out.println("Error[buildRelease]: IOException " + e);
            e.printStackTrace();
        }
        catch (SAXException e) {
            System.out.println("Error[buildRelease]: SAXException " + e);
            e.printStackTrace();
        }
        catch (ParserConfigurationException e)  {
            System.out.println("Error[buildRelease]: ParserConfigurationException " + e);
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        System.out.println("Something wrong with release building (");
    }

    public File getXmlFile(String fileName){

        File file = new File(fileName);

        if(file.exists() && !file.isDirectory()) return file;
        else{
            System.out.println("Error[ReleaseManagerStart]: could not find file: " + fileName);
            return null;
        }

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
