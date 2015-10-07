# <js></js>
hello
<script scr="ff"></script>

- a
- f

 * by using <js></js> tag we collect all scripts in page and put them
 * (already filtered by inits's file order and unique value) in HTML
 *
 * <js></js>- can be placed in:
 * 1. <head></head>
 * 2. end of <body></body> (default - end of body)
 * 3. or any place of HTML
 *
 * <js></js> can have inline code inside, this code can executes on
 * 1. window.ondomready (by default)
 * 2. window.onload or any other custom event
 * 3. immediately at the page placement (like inline)
 *
 * NOTE: if <js></js> executes on "inline", than src placed on <head></head> automatically
 * NOTE: html <script></script> is also supported but not processed thru this code


 js atttr

   private String src   = "file.js";
     private String where = "body";
     private String on    = "ready";

     private Boolean async = false;
     private Boolean defer = false;

     private Boolean merge = false;