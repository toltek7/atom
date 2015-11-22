package com.atom;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by toltek7 on 23.12.2014.
 */
public class ApplicationStart implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        Application.createInstance(readProperties(context), context);

    }

    private Properties readProperties(ServletContext context) {
        try {
            Properties properties = new Properties();
            InputStream config = context.getResourceAsStream(Constants.APPLICATION_CONFIG_FILE);
            if (config == null) return properties;
            properties.load(config);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void contextDestroyed(ServletContextEvent event) {
    }
}
