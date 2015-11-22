package com.atom.tags;

import com.atom.Application;
import com.atom.Constants;
import com.atom.release.Utils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Created by toltek7 on 07.09.2015.
 * Process css tag, add all info about this tag in CSS Manager, wich after processing all included css
 * put them on page in corresponding order
 */
public class CssTag extends SimpleTagSupport {

    private String  src   = "";
    private String  where = "head";
    private Boolean merge = false;

    public void doTag() throws IOException, JspException {

        JspWriter out     = getJspContext().getOut();
        JspFragment body  = getJspBody();
        StringWriter code = new StringWriter();

        if(body != null) body.invoke(code);

        String processedCode = Utils.trimString(code.toString());

        if(this.src.isEmpty()){
            this.where = "inline";
        }

        if (this.where.equals("inline")) {
            if(!processedCode.isEmpty()){
                out.write(String.format(Constants.STYLE_TAG_TEMPLATE,processedCode));
                processedCode = null;
            }
            this.where = "head";
        }

        Application.putCss(this.where, this.src, processedCode, this.merge);
//        out.write("<div> CSS tag: " + this.src + "</div>");

    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setWhere(String where) {
        switch (where) {
            case "head":
            case "inline":
                this.where = where;
                break;
            default:
                this.where = "body";
                break;
        }
    }
    public void setMerge(Boolean merge) {
        this.merge = merge;
    }
}
