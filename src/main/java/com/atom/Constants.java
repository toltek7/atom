package com.atom;

/**
 * Created by toltek7 on 19.12.2014.
 */
public class Constants {

    public static final String APPLICATION_CONFIG_FILE = "/WEB-INF/config.txt";

    public static final String JSON_CONFIG_FOLDER_NAME = "jsonFolderName";
    public static final String JSON_CONFIG_ROOT_VARIABLE_NAME = "jsonRootVariableName";
    public static final String JSON_CONFIG_LANGUAGE = "jsonDefaultLanguage";

    public static final String JSON_DEFAULT_FOLDER_NAME = "/data";
    public static final String JSON_DEFAULT_ROOT_VARIABLE_NAME = "data";
    public static final String JSON_DEFAULT_LANGUAGE = "en";

    public static final String URL_LANGUAGE_PARAM = "lang";

    public static final String CONFIG_RELEASE_FILE = "releaseConfigFile";
    public static final String DEFAULT_RELEASE_FILE = "delivery.xml";

    public static final String CONFIG_INIT_FILE = "inits";
    public static final String DEFAULT_INIT_FILE = "WEB-INF/tags/project1/inits.tagx";

    public static final String CONFIG_MAIN_JS_HEAD_FILE = "mainJsHeadFile";
    public static final String DEFAULT_MAIN_JS_HEAD_FILE = "/js/main-head.js";
    public static final String CONFIG_MAIN_JS_BODY_FILE = "mainJsBodyFile";
    public static final String DEFAULT_MAIN_JS_BODY_FILE = "/js/main-body.js";

    public static final String CONFIG_MAIN_CSS_HEAD_FILE = "mainCssHeadFile";
    public static final String DEFAULT_MAIN_CSS_HEAD_FILE = "/css/main-head.css";
    public static final String CONFIG_MAIN_CSS_BODY_FILE = "mainCssBodyFile";
    public static final String DEFAULT_MAIN_CSS_BODY_FILE = "/css/main-body.css";

    public static final String DEFAULT_RELEASE_FOLDER = "../../../release";

    public static final String APPLICATION_BUILD_TYPE = "projectBuild";
    public static final String BUILD_TYPE_DEV = "development";
    public static final String BUILD_TYPE_PRO = "production";
    public static final String DEFAULT_BUILD_TYPE = BUILD_TYPE_DEV;
    public static final String URL_BUILD_NAME = "build";

    public static final String JS_CSS_REQUEST_HOLDR = "js-css-request-holder";

    public static final String HTML5 = "<!DOCTYPE html>";
    public static final String XHTML_TRANSITIONAL_DOCTYPE = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";

    public static final String CSS_TAG_TEMPLATE = "<link href=\"%s\" media=\"%s\" rel=\"stylesheet\" type=\"text/css\"/>";
    public static final String JS_TAG_TEMPLATE = "<script src=\"%s\" type=\"text/javascript\" %s>//</script>\n";
    public static final String JS_ONLOAD_TEMPLATE = "window.onload=function(){%s};\n";
    public static final String JS_ONREADY_TEMPLATE = "document.addEventListener(\"DOMContentLoaded\", function(){%s});\n";
    public static final String JS_CUSTOM_TEMPLATE = "document.addEventListener(\"%s\", function(){%s});\n";
    public static final String SCRIPT_TAG_TEMPLATE = "<script>%s</script>\n";
    public static final String STYLE_TAG_TEMPLATE = "<style type=\"text/css\">%s</style>\n";

    public static final String NL = "\r\n";
}
