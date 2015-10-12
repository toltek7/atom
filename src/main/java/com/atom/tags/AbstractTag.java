package com.atom.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import javax.servlet.jsp.tagext.JspFragment;

/**
 * Created by toltek7 on 25.12.2014.
 */
public class AbstractTag extends SimpleTagSupport {
    @Override
    public void doTag() throws JspException, IOException {
        JspFragment body = getJspBody();
        if (body != null) {
            body.invoke(null);
        }
    }
}
