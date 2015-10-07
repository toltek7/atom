package com.atom.release;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by toltek7 on 09.01.2015.
 */
public class CssProcessor {

    public Map<String,String> getUrls(String srcPath, String destPath) throws IOException {
        return getUrls(srcPath, destPath, true);
    }
    public Map<String,String> getUrls(String cssSrcPath, String cssDestPath, boolean dropExternals) throws IOException {

        Map<String,String> hash = new HashMap<String,String>();

        String content = Files.toString(new File(cssSrcPath), Charsets.UTF_8);

        Pattern p = Pattern.compile("url[\\s]*\\((.*?)\\)");
        Matcher m = p.matcher(content);

        String srcUrl,
               destUrl;

        while(m.find()) {
            srcUrl = m.group(1).trim();
            if(!dropExternals || !isExternalUrl(srcUrl)){

                srcUrl = srcUrl.replaceAll("\'","");
                srcUrl = srcUrl.replaceAll("\"","");

                destUrl = convertToDestPath(srcUrl, cssDestPath);

                if(destUrl != null){
                    srcUrl = convertToDestPath(srcUrl, cssSrcPath);
                    hash.put(destUrl,srcUrl);
                }
            }
        }
        return hash;
    }

    private boolean isExternalUrl(String url){

        return url.contains("http") || url.contains("https");
    }

    private String convertToDestPath(String url, String cssDestPath){
       
        int pos = cssDestPath.lastIndexOf("css\\");
        
        if(pos == -1){
            Utils.print("Error[CssProcessor]: can not find i/ int css path: " + cssDestPath);
            return null;
        }
        
        return FileManager.getCanonicalPath(cssDestPath.substring(0,pos+4) + url);
    }
}
