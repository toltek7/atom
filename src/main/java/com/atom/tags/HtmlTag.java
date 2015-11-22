package com.atom.tags;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Created by toltek7 on 25.12.2014.
 */
public class HtmlTag extends SimpleTagSupport implements DynamicAttributes {

    protected Map<String, String> attributes = new HashMap<String, String>();
    boolean hasAttributes = false;
    private String tag = "div";
    private String extraAttributes = null;
    private Boolean dropEmptyTag = false;

    @Override
    public void doTag() throws JspException, IOException {
        //System.out.println("HtmlTag");
        this.attributes = stringToMap(extraAttributes, attributes);
        JspWriter out = getJspContext().getOut();
        JspFragment body = getJspBody();
        String startTag = getStartTag(tag, body != null);

        if (!hasAttributes && dropEmptyTag) {
            if (body != null) body.invoke(null);
        } else {
            out.write(startTag);
            if (body != null) body.invoke(null);
            out.write(getEndTag(tag));
        }
    }

    private String getStartTag(String tagName, boolean hasBody) {
        StringBuilder startTag = new StringBuilder("<" + tagName);
        for (Map.Entry<String, String> entry : this.attributes.entrySet()) {
            String attributeName = entry.getKey();
            if (!attributeName.equals("tag") && !attributeName.equals("dropEmptyTag")) {
                startTag.append(" ").append(attributeName).append("=").append("\"").append(entry.getValue()).append("\"");
                hasAttributes = true;
            }
        }
        startTag.append(hasBody ? ">" : "/>");
        return startTag.toString();
    }

    private String getEndTag(String tagName) {
        return String.format("</%s>", tagName);
    }

    public Map<String, String> stringToMap(String str, Map<String, String> map) {
        if (isBlank(str) || str.contentEquals("{}")) return map;
        str = str.replace("{", "").replace("}", "").trim();
        String[] pairs = str.split(",");
        for (String s : pairs) {
            String[] pair = s.trim().split("=");
            if (isBlank(map.get(pair[0]))) map.put(pair[0], pair[1]);
        }
        return map;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDropEmptyTag(Boolean dropEmptyTag) {
        this.dropEmptyTag = dropEmptyTag;
    }

    public void setExtraAttributes(String extraAttributes) {
        this.extraAttributes = extraAttributes;
    }

    public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
        if (value == null) return;
        String stringValue = value.toString().trim();
        if (!stringValue.isEmpty()) this.attributes.put(localName, stringValue);
    }

}
