package com.atom.tags;

import com.atom.Application;
import com.atom.Constants;
import com.atom.release.FileManager;
import com.atom.release.Utils;

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
    private String on = "";
    private String code = "";
    private Boolean inHead = false;
    private Boolean async = false;
    private Boolean defer = false;
    private Boolean merge = false;
    private Boolean inline = false;


    @Override
    public void doTag() throws JspException, IOException {

        JspFragment body = getJspBody();
        StringWriter code = new StringWriter();

        if (body != null) body.invoke(code);

        this.code = this.processCode(code);

        validateInputs();

        Application.putJs(this.inHead, this.src, this.on, this.code, this.async, this.defer, this.merge, this.inline);

    }

    public void setSrc(String src) {
        this.src = src;
    }

    public void setInHead(Boolean inHead) {
        this.inHead = inHead;
    }

    public void setOn(String on) {
        if (isBlank(on)) return;
        this.on = on;
    }

    public void setCode(String code) {
        if (isBlank(on)) return;
        this.code = code;
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

    public void validateInputs() throws IOException {

        JspWriter out = getJspContext().getOut();

        //script should be added to the page inline, all others attributes not make force
        if (this.inline) {
            //if already on page do not need to write it
            boolean[] attr = Application.getJs(this.src);
            if (attr == null || !attr[Constants.INLINE]) {
                if (attr != null) { //in the case inline attribute set to some end script
                    attr[Constants.INLINE] = true;
                }
                String path = Application.root + "/" + this.src,
                        content = "";
                if (FileManager.isFileExist(path)) {
                    content = FileManager.readFile(path);
                } else {
                    Utils.print("Warning[JS-tag]: can not find file: " + path);
                }
                content += this.code;
                if (!content.isEmpty()) {
                    out.write(String.format(Constants.SCRIPT_TAG_TEMPLATE, content));
                }
            }
            this.code = "";
            this.inHead = true;
            //print();
        }

        //defer has more force
        if (this.defer) this.async = false;

        //-on+code = code to the page
        if (this.on.isEmpty() && !this.code.isEmpty()) {//if we have a code inside and on = ""
            out.write(String.format(Constants.SCRIPT_TAG_TEMPLATE, this.code));
            this.async = false;
            this.defer = false;
            this.inHead = true;//to rewrite all other such scripts that can be without inline param
            this.code = null;
        }

        if (this.code == null || this.code.isEmpty()) {
            this.on = "";
        }

        //print();
    }

    private void print() {
        System.out.println("  ---   src    ---  " + this.src);
        System.out.println("  ---   inHead ---  " + this.inHead);
        System.out.println("  ---   on     ---  " + this.on);
        System.out.println("  ---   async  ---  " + this.async);
        System.out.println("  ---   defer  ---  " + this.defer);
        System.out.println("  ---   merge  ---  " + this.merge);
        System.out.println("  ---   inline ---  " + this.inline);
        System.out.println("  ---   code   ---  " + this.code);
    }

}
