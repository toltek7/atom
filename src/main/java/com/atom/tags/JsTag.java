package com.atom.tags;

import com.atom.Application;
import com.atom.Constants;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.StringWriter;

import static org.apache.commons.lang3.StringUtils.isBlank;


/**
 * Created by toltek7 on 11.01.2015.
 */
public class JsTag extends SimpleTagSupport {

    private String src = "default.js";
    private String where = "body";
    private String on = "ready";
    private Boolean async = false;
    private Boolean defer = false;
    private Boolean merge = false;
    private Boolean inline = false;


    @Override
    public void doTag() throws JspException, IOException {

        JspWriter out = getJspContext().getOut();
        JspFragment body = getJspBody();
        StringWriter code = new StringWriter();

        if (body != null) body.invoke(code);

        String processedCode = this.processCode(code);

        if (this.where.equals("inline")) {
            if (!processedCode.isEmpty()) {
                out.write(String.format(Constants.SCRIPT_TAG_TEMPLATE, processedCode));
                this.async = false;
                this.defer = false;
                processedCode = null;
            }
            this.where = "head";
        }
        printInputs("");
        Application.putJs(this.where, this.src, this.on, processedCode, this.async, this.defer, this.merge);
        //out.write("<div> JS tag: " + this.src + "</div>");
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

    public void setOn(String on) {
        if (isBlank(on)) return;
        this.on = on;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public void setDefer(Boolean defer) {
        this.defer = defer;
    }

    public void setMerge(Boolean merge) {
        this.merge = merge;
    }

    public void setInline(Boolean inline) {
        this.inline = inline;
    }

    public String processCode(StringWriter code) {
        if (code == null) return null;
        return code.toString().replaceAll("\\s", " ").trim();
    }

    private void printInputs(String code) {
        System.out.println("  ---   src   ---  " + this.src);
        System.out.println("  ---   where ---  " + this.where);
        System.out.println("  ---   on    ---  " + this.on);
        System.out.println("  ---   async ---  " + this.async);
        System.out.println("  ---   defer ---  " + this.defer);
        System.out.println("  ---   merge ---  " + this.merge);
        System.out.println("  ---   inline---  " + this.inline);
        System.out.println("  ---   code  ---  " + code);
    }

}
