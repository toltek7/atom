readStringFromFileAtPath = function(pathOfFileToReadFrom)
{
    var request = new XMLHttpRequest();
    request.open("GET", pathOfFileToReadFrom, false);
    request.send(null);
    var returnValue = request.responseText;

    return returnValue;
};


$(window).load(function () {
    var report = $("#report"),
        error = 0,
        tests = 0;
    if (report) {

        var mainHeadText = readStringFromFileAtPath("js/main-head.js"),
            mainBodyText = readStringFromFileAtPath("js/main-body.js"),
            mainCssHeadText = readStringFromFileAtPath("css/main-head.css"),
            mainCssBodyText = readStringFromFileAtPath("css/main-body.css"),
            bodyScripts = $("body script"),
            headScripts = $("head script"),
            bodyStyles = $("body link"),
            headStyles = $("head link"),
            scripts = $("script"),
            lastScript = scripts.last().text();

        function test(text,func){
            var node = $("<p/>", {text: text}).appendTo(report);
            tests++;
            if(func.apply()){
                node.append($("<b/>", {class: "success", text: "done"}));
            }else{
                node.append($("<b/>", {class: "error", text: "failed"}));
                error++;
            }
        }

        // ===============  test 1
        test("[JS] 1.(inline - code) check that inline (7) js only one on page: ",function(){
            return $(".test7").length == 1;
        });

        // ===============  test 2
        test("[JS] 2.(inline + code) check inline param +code works: ",function(){
            return $(".test7").text().trim() == "inline code from test7";
        });

        // ===============  test 3
        test("[JS] 3.(head + code - on) -> head + code on page (immediately): ",function(){
            return ($("#tag1").next().text().trim() == "console.log('test1 inline code');") &&
                   ($("head script").filter('[src="js/test-1.js"]').length == 1);
        });

        // ===============  test 4
        test("[JS] 4.(body + code - on) -> head + code on page (immediately): ",function(){
            return ($("#tag3").next().text().trim() == "console.log('test3 inline code');") &&
                ($("head script").filter('[src="js/test-3.js"]').length == 1);
        });

        // ===============  test 5
        test("[JS] 5.(head + code - on + merge) -> merge_head + code on page (immediately): ",function(){
            return ($("#tag2").next().text().trim() == "console.log('test2 inline code');") &&
                ($("head script").filter('[src="js/main-head.js"]').length == 1) &&
                mainHeadText.indexOf('console.log("test2 file");')!=-1;
        });

        // ===============  test 6
        test("[JS] 6.(body + code - on + merge) -> merge_head + code on page (immediately): ",function(){
            return ($("#tag4").next().text().trim() == "console.log('test4 inline code');") &&
                ($("head script").filter('[src="js/main-head.js"]').length == 1) &&
                mainHeadText.indexOf('console.log("test4 file");')!=-1;
        });

        // ===============  test 7
        test("[JS] 7.(head + code + on) -> head + code inside on: ",function(){
            return ($("#tag5").next().text().trim() != "console.log('test5 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("console.log('test5 inline code')") &&
                ($("head script").filter('[src="js/test-5.js"]').length == 1);
        });

        // ===============  test 8
        test("[JS] 8.(body + code + on) -> body + code inside on: ",function(){
            return ($("#tag6").next().text().trim() != "console.log('test6 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("console.log('test6 inline code')") &&
                (bodyScripts.filter('[src="js/test-6.js"]').length == 1);
        });

        // ===============  test 9
        test("[JS] 9.(on+on order) -> inline code with on should support order like in inits: ",function(){
            return lastScript.indexOf("test6 inline code")<lastScript.indexOf("test5 inline code");
        });

        // ===============  test 10
        test("[JS] 10.(head + code + on + merge) -> head_merge + code inside on: ",function(){
            return ($("#tag8").next().text().trim() != "console.log('test8 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("test8 inline code") &&
                mainHeadText.indexOf('console.log("test8 file");')!=-1;
        });


        // ===============  test 11
        test("[JS] 11.(body + code + on + merge) -> body_merge + code inside on: ",function(){
            return ($("#tag9").next().text().trim() != "console.log('test9 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("test9 inline code") &&
                mainBodyText.indexOf('console.log("test9 file");')!=-1;
        });


        // ===============  test 12
        test("[JS] 12.(head + merge) -> all such files should be merged in head script (we should not see such file src, it goes to merge): ",function(){
            return ($("head script").filter('[src="js/test-8.js"]').length == 0)
        });

        // ===============  test 13
        test("[JS] 13.(body + merge) -> all such files should be merged in body script: ",function(){
            return (bodyScripts.filter('[src="js/test-9.js"]').length == 0)
        });

        // ===============  test 14
        test("[JS] 14.(order of code in merged file): ",function(){
            return mainHeadText.indexOf('test2')<mainHeadText.indexOf('test8') &&
                   mainHeadText.indexOf('test8')<mainHeadText.indexOf('test4')
        });


        // ===============  test 15
        test("[JS] 15.(inline not first) if one file inline all others same files should be inline: ",function(){
            return mainHeadText.indexOf('test7')==-1 &&
                mainBodyText.indexOf('test7')==-1 &&
                (bodyScripts.filter('[src="js/test-7.js"]').length == 0)
        });


        // ===============  test 16
        test("[JS] 16.(defer > async): ",function(){
            return (bodyScripts.filter('[src="js/test-6.js"]').get(0).hasAttribute("defer")) &&
                !(bodyScripts.filter('[src="js/test-6.js"]').get(0).hasAttribute("async"));
        });

        // ===============  test 17
        test("[JS] 17.(defer or async gets from the first file) if in first file we have no them, no matter do we have such params in below files, they will be ignore: ",function(){
            return headScripts.filter('[src="js/test-5.js"]').length ==1 &&
                !(headScripts.filter('[src="js/test-5.js"]').get(0).hasAttribute("defer"));
        });

        // ===============  test 18
        test("[JS] 18.(on events should mention only one time): ",function(){
            return (lastScript.match(/DOMContentLoaded/g) || []).length <= 1 &&
                (lastScript.match(/onload/g) || []).length <= 1 &&
                (lastScript.match(/customevent/g) || []).length <= 1;
        });

        // ===============  test 19
        test("[JS] 19.(order of script in body should be the same like in init file): ",function(){
            console.log(bodyScripts.filter('[src="js/test-11.js"]').index());
            return (bodyScripts.filter('[src="js/test-6.js"]').index()) < (bodyScripts.filter('[src="js/test-11.js"]').index()) &&
            (bodyScripts.filter('[src="js/test-11.js"]').index()) < (bodyScripts.filter('[src="js/test-10.js"]').index());
        });




        // ===============  test 1
        test("[CSS] 1. (inline + code)-> file code and code immediately at the page, only one time: ",function(){
          //  console.log(($("#tag1").next().next().text().trim() == ".test-1-inline{}"));
            return ($("#tag1").next().next().text().trim() == ".test1{}\n.test-1-inline{}")&&
                headStyles.filter('[href="css/test-1.css"]').length == 0
        });
        // ===============  test 2
        test("[CSS] 2. (without src)-> code immediately on the page, after the tag: ",function(){
            return ($("#tag2").next().next().text().trim() == ".test-2-inline{}");
        });
        // ===============  test 3
        test("[CSS] 3. (head + code)-> file on head, code on page immediately: ",function(){
            return ($("#tag6").next().text().trim() == ".test-3-inline{}") &&
                headStyles.filter('[href="css/test-3.css"]').length != 0;
        });
        // ===============  test 4
        test("[CSS] 4. (body + code)-> file on head, code on page immediately: ",function(){
            return ($("#tag7").next().next().text().trim() == ".test-4-inline{}") &&
                headStyles.filter('[href="css/test-4.css"]').length != 0 ;
        });
        // ===============  test 5
        test("[CSS] 5. (body)-> code file on body: ",function(){
            return headStyles.filter('[href="css/test-5.css"]').length == 0 &&
                   bodyStyles.filter('[href="css/test-5.css"]').length != 0 ;
        });
        // ===============  test 6
        test("[CSS] 6. (body merge works + order of included in merge files): ",function(){
            return bodyStyles.filter('[href="css/main-body.css"]').length != 0 &&
                mainCssBodyText.indexOf('.test6{}')!=-1&&
                mainCssBodyText.indexOf('.test7{}')!=-1&&
                bodyStyles.filter('[href="css/test-6.css"]').length == 0&&
                bodyStyles.filter('[href="css/test-7.css"]').length == 0&&
                mainCssBodyText.indexOf('.test7{}') < mainCssBodyText.indexOf('.test6{}');
        });
        // ===============  test 7
        test("[CSS] 7. (head merge works + order of included in merge files): ",function(){
            return headStyles.filter('[href="css/main-head.css"]').length != 0 &&
                mainCssHeadText.indexOf('.test8{}')!=-1&&
                mainCssHeadText.indexOf('.test9{}')!=-1&&
                headStyles.filter('[href="css/test-8.css"]').length == 0&&
                headStyles.filter('[href="css/test-9.css"]').length == 0&&
                mainCssHeadText.indexOf('.test9{}') < mainCssHeadText.indexOf('.test8{}');
        });
        // ===============  test 8
        test("[CSS] 8. (If res in head and in body)-> should be only in head: ",function(){
            return headStyles.filter('[href="css/test-8.css"]').length== 0 &&
                bodyStyles.filter('[href="css/test-8.css"]').length== 0 ;
        });
        // ===============  test 9
        test("[CSS] 9. (inline)-> all such files will be ignored, it will be added one time: ",function(){
            return headStyles.filter('[href="css/test-10.css"]').length== 0 &&
                bodyStyles.filter('[href="css/test-10.css"]').length== 0 &&
                mainCssBodyText.indexOf('.test10-file-code{}') == -1&&
                mainCssHeadText.indexOf('.test10-file-code{}') ==-1&&
                ($("#tag8").next().text().trim() == ".test10-file-code{}");
        });
        // ===============  test 10
        test("[CSS] 10. (src not correct)-> all such files will be ignored, it will be added one time: ",function(){
            return headStyles.filter('[href="css/test-10.css"]').length== 0 &&
                bodyStyles.filter('[href="css/test-10.css"]').length== 0 &&
                mainCssBodyText.indexOf('.test10-file-code{}') == -1&&
                mainCssHeadText.indexOf('.test10-file-code{}') ==-1&&
                ($("#tag8").next().text().trim() == ".test10-file-code{}");
        });


        // ===============  result
        test("Results: " + tests + " tests run (error: " + error + "): ",function(){
            return error == 0;
        });
    }
});

