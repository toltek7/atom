package com.atom.release;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.HashSet;

/**
 * Created by toltek7 on 29.12.2014.
 */
public class XmlParser {

    static Node root;

    public XmlParser(Node root) {
        this.root = root;
    }

    public static String getFolder() {
        Node node = root.getAttributes().getNamedItem("folder");
        if (node != null) {
            return node.getNodeValue().trim();
        }
        return null;
    }

    public static String[] getLanguages() {
        NodeList node = ((Element) root).getElementsByTagName("languages");
        if (node != null && node.getLength() > 0) {
            String stringLang = node.item(0).getTextContent().replace(" ", "").trim();
            return stringLang.split(",");
        }
        return null;
    }

    public static String getQueryParams() {
        NodeList node = ((Element) root).getElementsByTagName("query");
        if (node != null && node.getLength() > 0) {
            return node.item(0).getTextContent().trim();
        }
        return null;
    }

    public static HashSet<String> getPages() {

        NodeList node = ((Element) root).getElementsByTagName("jspx");

        String page;

        HashSet<String> pages = new HashSet<String>();

        if (node != null && node.getLength() > 0) {

            int length = node.getLength();

            for (int i = 0; i < length; i++) {
                page = node.item(i).getTextContent().trim();
                page = page.replace("/", File.separator);
                page = page.replace("\\", File.separator);
                pages.add(page);
            }
            return pages;
        }
        return null;
    }

    public static boolean hasTimeStamp() {
        return getBooleanTagValue("timestamp", true);
    }

    public static boolean isCompressedJS() {
        return getBooleanTagValue("compress-js", true);
    }

    public static boolean isCompressedCSS() {
        return getBooleanTagValue("compress-css", true);
    }

    public static boolean isCompressedHTML() {
        return getBooleanTagValue("compress-html", true);
    }

    private static boolean getBooleanTagValue(String tagName, Boolean defaultValue) {
        NodeList node = ((Element) root).getElementsByTagName(tagName);
        if (node != null && node.getLength() > 0) {
            return Boolean.parseBoolean(node.item(0).getTextContent());
        }
        return defaultValue;
    }
}
