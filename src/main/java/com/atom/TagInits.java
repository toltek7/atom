package com.atom;

import com.atom.release.Utils;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by toltek7 on 27.06.2015.
 */
public class TagInits extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        System.out.println("  --------   doTag inits tAG -------  ");


        HttpServletRequest requestHttp = (HttpServletRequest)Application.getRequest();// request = (HttpServletRequest) context.getRequest();

        StringWriter out = new StringWriter();

        getJspBody().invoke(out);

        JspFragment body = getJspBody();


        Application  app = Application.getInstance();
        app.getInitsFilesOrder();

//        TagInitsProcessor filesOrder = new TagInitsProcessor("project1/index.jspx");

       // System.out.println("  -------- 777 --------------------------------------------------  ");
//        System.out.println(out.toString());
//        System.out.println(requestHttp.getRequestURL().toString());
//        System.out.println(requestHttp.getContextPath());
//        System.out.println(requestHttp.getContextPath());
//        System.out.println(requestHttp.getAuthType());
//        System.out.println(requestHttp.getHeaderNames());
//        System.out.println(requestHttp.getPathInfo());
//        System.out.println(requestHttp.getPathTranslated());
//        System.out.println(requestHttp.getServletPath());//that we need
//        System.out.println(requestHttp.getRemoteAddr());
//        System.out.println(requestHttp.getServerName());
//        System.out.println(body.getJspContext().toString());
     //   System.out.println("  -------- 777 --------------------------------------------------  ");
        //String startTag = getStartTag(tag, body != null);

        if (body != null) {
            body.invoke(null);
        }
    }
}
