package com.atom.tags;

import com.atom.Application;
import com.atom.Constants;
import com.atom.release.FileManager;
import com.atom.release.Utils;
import org.apache.commons.lang3.StringUtils;

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

        JspFragment body  = getJspBody();
        StringWriter code = new StringWriter();

        if(body != null) body.invoke(code);

        this.code = Utils.trimString(code.toString());

        if(validateInputs()){
            //print();
            Application.putCss(this.inHead, this.src, this.code, this.merge, this.inline);
        }

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

    public Boolean validateInputs() throws IOException {

        JspWriter out = getJspContext().getOut();

        //script should be added to the page inline, all others attributes not make force
        if (this.inline && StringUtils.isNotBlank(this.src)) {
            //if already on page do not need to write it
            boolean[] attr = Application.getCss(this.src);
            if(attr == null || !attr[Constants.INLINE]){
                //print();
                if(attr != null){ //in the case inline attribute set to some end script
                    attr[Constants.INLINE] = true;
                }
                String path = Application.root + "/" + this.src,
                       content = "";
                if(FileManager.isFileExist(path)){
                     content = FileManager.readFile(path);
                }else{
                    Utils.print("Warning[CSS-tag]: can not find file: " + path);
                }
                content +=this.code;
                if (!content.isEmpty()) {
                    out.write(String.format(Constants.STYLE_TAG_TEMPLATE, content));
                }
            }
            this.code = "";
            this.inHead = true;
            this.merge = false;
        }

        if(!this.code.isEmpty()){
            out.write(String.format(Constants.STYLE_TAG_TEMPLATE,this.code));
            this.code = null;
            this.inHead = true;//to rewrite all other such scripts that can be without inline param
        }


        if(StringUtils.isBlank(this.src) && StringUtils.isBlank(this.code)){
            return false;
        }

        return true;
        //print();
    }

    private void print() {
        System.out.println("  ---   src    ---  " + this.src);
        System.out.println("  ---   inHead ---  " + this.inHead);
        System.out.println("  ---   merge  ---  " + this.merge);
        System.out.println("  ---   inline ---  " + this.inline);
        System.out.println("  ---   code   ---  " + this.code);
    }
}
