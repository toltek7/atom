package com.atom.tags;

import com.atom.Application;
import com.atom.Constants;
import com.atom.release.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;


/**
 * Created by toltek7 on 19.12.2014.
 */
public class PageTag extends SimpleTagSupport {

    private Boolean html5 = true;

    @Override
    public void doTag() throws JspException, IOException {

        System.out.println("PageTag");

 /*       PageContext context = (PageContext) getJspContext();

        HttpServletRequest request;
        request = (HttpServletRequest) context.getRequest();
        request.getContextPath();
        System.out.println( request.getContextPath());
        System.out.println( request.getServletPath());
        System.out.println( request.getRequestURL().toString());
        System.out.println( "getRequestURI" + request.getRequestURI());
        System.out.println( request.getHeaderNames());
        System.out.println( request.getPathInfo());
        System.out.println( request.getPathTranslated());
        System.out.println( request.getRemoteAddr());
        System.out.println( request.getServerName());
        System.out.println( context.getServletContext().getServletContextName());//getPage().toString());
        System.out.println( context.getServletContext().getContextPath());//getPage().toString());
        System.out.println( context.getPage().toString());
//        System.out.println( context.getrgetServletContext().getServerInfo());
        System.out.println(context.getServletContext().getAttributeNames());
        System.out.println( context.getServletContext().getRealPath("/"));//getPage().toString());
        System.out.println(this.getClass().getSimpleName());//getPage().toString());
        System.out.println(context.getServletContext().getResourcePaths(this.getClass().getSimpleName()));//getPage().toString());
*/
//getParent().

        StringWriter out = new StringWriter();

        if (getHtml5()) {
            out.write(Constants.HTML5);
        } else {
            out.write(Constants.XHTML_TRANSITIONAL_DOCTYPE);
        }

        out.write(Constants.NL);

        getJspBody().invoke(out);

        StringBuilder html = new StringBuilder(out.toString());



        Application.orderResourceFilesByInitTag();
        Utils.insertMarkupBeforeNode("</head>", html, Application.getHeadCssTags());
        Utils.insertMarkupBeforeNode("</head>", html, Application.getHeadJsTags());
        Utils.insertMarkupBeforeNode("</body>", html, Application.getBodyCssTags());
        Utils.insertMarkupBeforeNode("</body>", html, Application.getBodyJsTags());
        Utils.insertMarkupBeforeNode("</body>", html, Application.getJsInlineCode());

        getJspContext().getOut().write(html.toString());

        // Application.printJsTags();


    }

    public Boolean getHtml5() {
        return html5 != null ? html5 : true;
    }
}
