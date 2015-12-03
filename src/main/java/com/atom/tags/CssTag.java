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
    private String  code   = "";
    private Boolean inHead = true;
    private Boolean merge = false;
    private Boolean inline = false;

    public void doTag() throws IOException, JspException {

        JspWriter out     = getJspContext().getOut();
        JspFragment body  = getJspBody();
        StringWriter code = new StringWriter();

        if(body != null) body.invoke(code);

        this.code = Utils.trimString(code.toString());

//        if(this.src.isEmpty()){
//            this.where = "inline";
//        }

        if(!this.code.isEmpty()){
            out.write(String.format(Constants.STYLE_TAG_TEMPLATE,this.code));
            this.code = null;
            this.inHead = true;
        }

     /*   if (this.where.equals("inline")) {
            this.where = "head";
        }*/

        Application.putCss(this.inHead, this.src, this.code, this.merge, this.inline);

    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setInHead(Boolean inHead) {
        this.inHead = inHead;
    }
    public void setMerge(Boolean merge) {
        this.merge = merge;
    }
    public void setInline(Boolean inline) {
        this.inline = inline;
    }
}
