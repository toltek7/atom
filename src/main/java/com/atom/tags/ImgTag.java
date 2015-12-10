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
import java.util.Map;

/**
 * Created by toltek7 on 11.12.2015.
 */
public class ImgTag extends HtmlTag{
    private String  src   = "";

    public void doTag() throws IOException, JspException {

        JspFragment body  = getJspBody();
        StringWriter code = new StringWriter();

        if(body != null) body.invoke(code);

        if(validateInputs()){
            //todo add to image manager for delivery
         //   Application.putCss(this.inHead, this.src, this.code, this.merge, this.inline);
        }

    }

    public Boolean validateInputs() throws IOException {

        JspWriter out = getJspContext().getOut();

        if (StringUtils.isNotBlank(this.src)) {
            if (FileManager.isFileExist(Application.root + "/" + this.src)){
                String path = Utils.getRelatedPath(this.src, Application.getCurrentPagePath());
                String tag = getStartTag("img", false, "src=\"" + path + "\" ");
                //Utils.print(tag);
                out.write(tag);
            }else{
                Utils.print("Warning[Img-tag]: can not find img: " + this.src);
            }
            return true;
        }

        return false;
        //print();
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
