package com.atom.release;

import com.google.javascript.jscomp.CompilationLevel;
import com.googlecode.htmlcompressor.compressor.ClosureJavaScriptCompressor;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.googlecode.htmlcompressor.compressor.YuiCssCompressor;
import org.jsoup.nodes.Node;

/**
 * Created by toltek7 on 07.01.2015.
 * FileCompressor
 * https://code.google.com/p/htmlcompressor/
 */
public class FileCompressor {

    public static void removeComments(Node node) {

        for (int i = 0; i < node.childNodes().size();) {

            Node child = node.childNode(i);

            if (child.nodeName().equals("#comment")) child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    public static String compressHTML(String html){

        HtmlCompressor compressor = new HtmlCompressor();

        compressor.setEnabled(true);                   //if false all compression is off (default is true)
        compressor.setRemoveComments(true);            //if false keeps HTML comments (default is true)
        compressor.setRemoveMultiSpaces(true);         //if false keeps multiple whitespace characters (default is true)
        compressor.setRemoveIntertagSpaces(true);      //removes iter-tag whitespace characters
        compressor.setRemoveQuotes(true);              //removes unnecessary tag attribute quotes
        compressor.setSimpleDoctype(true);             //simplify existing doctype
        compressor.setRemoveScriptAttributes(true);    //remove optional attributes from script tags
        compressor.setRemoveStyleAttributes(true);     //remove optional attributes from style tags
        compressor.setRemoveLinkAttributes(true);      //remove optional attributes from link tags
        compressor.setRemoveFormAttributes(true);      //remove optional attributes from form tags
        compressor.setRemoveInputAttributes(true);     //remove optional attributes from input tags
        compressor.setSimpleBooleanAttributes(true);   //remove values from boolean tag attributes
        compressor.setRemoveJavaScriptProtocol(true);  //remove "javascript:" from inline event handlers
//        compressor.setRemoveHttpProtocol(false);        //replace "http://" with "//" inside tag attributes
//        compressor.setRemoveHttpsProtocol(false);       //replace "https://" with "//" inside tag attributes
//        compressor.setPreserveLineBreaks(true);        //preserves original line breaks
 //       compressor.setRemoveSurroundingSpaces("br,p"); //remove spaces around provided tags

        compressor.setCompressCss(true);               //compress inline css
        compressor.setCompressJavaScript(true);        //compress inline javascript
        compressor.setYuiCssLineBreak(80);             //--line-break param for Yahoo YUI Compressor
        compressor.setYuiJsDisableOptimizations(true); //--disable-optimizations param for Yahoo YUI Compressor
        compressor.setYuiJsLineBreak(-1);              //--line-break param for Yahoo YUI Compressor
        compressor.setYuiJsNoMunge(true);              //--nomunge param for Yahoo YUI Compressor
        compressor.setYuiJsPreserveAllSemiColons(true);//--preserve-semi param for Yahoo YUI Compressor

        //use Google Closure Compiler for javascript compression
        compressor.setJavaScriptCompressor(new ClosureJavaScriptCompressor(CompilationLevel.ADVANCED_OPTIMIZATIONS));

       /* System.out.println(String.format(
                "Compression time: %,d ms, Original size: %,d bytes, Compressed size: %,d bytes",
                compressor.getStatistics().getTime(),
                compressor.getStatistics().getOriginalMetrics().getFilesize(),
                compressor.getStatistics().getCompressedMetrics().getFilesize()
        ));*/

        return compressor.compress(html);
    }

    public static String compressCSS(String css){
        YuiCssCompressor cssCompressor = new YuiCssCompressor();
        cssCompressor.setLineBreak(-1);
        return cssCompressor.compress(css);
    }

    public static String compressJS(String js){
        ClosureJavaScriptCompressor jsCompressor = new ClosureJavaScriptCompressor();
        jsCompressor.setCompilationLevel(CompilationLevel.ADVANCED_OPTIMIZATIONS);
        return jsCompressor.compress(js);
    }

    public static String compressImg(String img){
        return img;
    }

}
