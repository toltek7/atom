package com.atom.tags;

import com.atom.Application;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by toltek7 on 27.06.2015.
 */
public class InitsTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        //System.out.println(" inits tag ");
        //HttpServletRequest requestHttp = (HttpServletRequest) Application.getRequest();// request = (HttpServletRequest) context.getRequest();
        //StringWriter out = new StringWriter();
        //getJspBody().invoke(out);
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }
        Application.getInstance().createListOfFilesOrder();
    }
}
