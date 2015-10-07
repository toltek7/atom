package com.atom.release;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by toltek7 on 28.12.2014.
 */
public class ReleaseManager {

    private final File xmlConfigFile;

    private static ReleaseManager instance;

    public static ReleaseManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("JsonProcessor is not initialized");
        }
        return instance;
    }

    public static synchronized ReleaseManager createInstance(File file) {
        instance = new ReleaseManager(file);
        return instance;
    }

    private ReleaseManager(File file) {
        if (file == null) {
            throw new IllegalStateException("ReleaseManager can't find xml config file");
        }
        this.xmlConfigFile = file;
    }


    public void buildRelease(HttpServletRequest request, final HttpServletResponse response) throws IOException, SAXException, ParserConfigurationException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        NodeList releaseDescription = parseXmlFile();

        int releaseNumbers = releaseDescription.getLength();

        ReleaseObject release;

        for (int i = 0; i < releaseNumbers; i++) {
            release = new ReleaseObject(releaseDescription.item(i));
            release.build(request,response);
        }

       /* List<ReleaseObject> releases = new ArrayList<ReleaseObject>(releaseNumbers);

        for (int i = 0; i < releaseNumbers; i++) {
            releases.add(new ReleaseObject(releaseDescription.item(i)));
        }

        for (ReleaseObject release : releases) {
            release.build(request,response);
        }*/

    }


    public NodeList parseXmlFile() throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlConfigFile);
        Element root = document.getDocumentElement();

//todo: remove disabled description
/*        NodeList nodes = root.getElementsByTagName("release");
        Boolean isDisabled = false;
        for (int i = 0; i < nodes.getLength(); i++) {
            isDisabled = Boolean.parseBoolean(nodes.item(i).getAttributes().getNamedItem("disabled").getTextContent());

            if(isDisabled){
                System.out.println("Warning[parseXmlFile]: found disabled release description - " + (i+1));
            }
        }*/
        return root.getElementsByTagName("release");

    }
}
