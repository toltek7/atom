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
        error = 0;
    if (report) {

        var mainHeadText = readStringFromFileAtPath("js/main-head.js"),
            mainBodyText = readStringFromFileAtPath("js/main-body.js"),
            bodyScripts = $("body script"),
            headScripts = $("head script"),
            scripts = $("script"),
            lastScript = scripts.last().text();

        function test(text,func){
            var node = $("<p/>", {text: text}).appendTo(report);
            if(func.apply()){
                node.append($("<b/>", {class: "success", text: "done"}));
                error++;
            }else{
                node.append($("<b/>", {class: "error", text: "failed"}));
            }
        }

        // ===============  test 1
        test("1.(inline - code) check that inline (7) js only one on page: ",function(){
            return $(".test7").length == 1;
        });

        // ===============  test 2
        test("2.(inline + code) check inline param +code works: ",function(){
            return $(".test7").text().trim() == "inline code from test7";
        });

        // ===============  test 3
        test("3.(head + code - on) -> head + code on page (immediately): ",function(){
            return ($("#tag1").next().text().trim() == "console.log('test1 inline code');") &&
                   ($("head script").filter('[src="js/test-1.js"]').length == 1);
        });

        // ===============  test 4
        test("4.(body + code - on) -> head + code on page (immediately): ",function(){
            return ($("#tag3").next().text().trim() == "console.log('test3 inline code');") &&
                ($("head script").filter('[src="js/test-3.js"]').length == 1);
        });

        // ===============  test 5
        test("5.(head + code - on + merge) -> merge_head + code on page (immediately): ",function(){
            return ($("#tag2").next().text().trim() == "console.log('test2 inline code');") &&
                ($("head script").filter('[src="js/main-head.js"]').length == 1) &&
                mainHeadText.indexOf('console.log("test2 file");')!=-1;
        });

        // ===============  test 6
        test("6.(body + code - on + merge) -> merge_head + code on page (immediately): ",function(){
            return ($("#tag4").next().text().trim() == "console.log('test4 inline code');") &&
                ($("head script").filter('[src="js/main-head.js"]').length == 1) &&
                mainHeadText.indexOf('console.log("test4 file");')!=-1;
        });

        // ===============  test 7
        test("7.(head + code + on) -> head + code inside on: ",function(){
            return ($("#tag5").next().text().trim() != "console.log('test5 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("console.log('test5 inline code')") &&
                ($("head script").filter('[src="js/test-5.js"]').length == 1);
        });

        // ===============  test 8
        test("8.(body + code + on) -> body + code inside on: ",function(){
            return ($("#tag6").next().text().trim() != "console.log('test6 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("console.log('test6 inline code')") &&
                (bodyScripts.filter('[src="js/test-6.js"]').length == 1);
        });

        // ===============  test 9
        test("9.(on+on order) -> inline code with on should support order like in inits: ",function(){
            return lastScript.indexOf("test6 inline code")<lastScript.indexOf("test5 inline code");
        });

        // ===============  test 10
        test("10.(head + code + on + merge) -> head_merge + code inside on: ",function(){
            return ($("#tag8").next().text().trim() != "console.log('test8 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("test8 inline code") &&
                mainHeadText.indexOf('console.log("test8 file");')!=-1;
        });


        // ===============  test 11
        test("11.(body + code + on + merge) -> body_merge + code inside on: ",function(){
            return ($("#tag9").next().text().trim() != "console.log('test9 inline code');") &&
                lastScript.indexOf("custom")<lastScript.indexOf("test9 inline code") &&
                mainBodyText.indexOf('console.log("test9 file");')!=-1;
        });


        // ===============  test 12
        test("12.(head + merge) -> all such files should be merged in head script: ",function(){
            return ($("head script").filter('[src="js/test-8.js"]').length == 0)
        });

        // ===============  test 13
        test("13.(body + merge) -> all such files should be merged in body script: ",function(){
            return (bodyScripts.filter('[src="js/test-9.js"]').length == 0)
        });

        // ===============  test 14
        test("14.(order of code in merged file): ",function(){
            return mainHeadText.indexOf('test2')<mainHeadText.indexOf('test8') &&
                   mainHeadText.indexOf('test8')<mainHeadText.indexOf('test4')
        });


        // ===============  test 15
        test("15.(inline not first) if one file inline all others same files should be inline: ",function(){
            return mainHeadText.indexOf('test7')==-1 &&
                mainBodyText.indexOf('test7')==-1 &&
                (bodyScripts.filter('[src="js/test-7.js"]').length == 0)
        });


        // ===============  test 16
        test("16.(defer > async): ",function(){
            return (bodyScripts.filter('[src="js/test-6.js"]').get(0).hasAttribute("defer")) &&
                !(bodyScripts.filter('[src="js/test-6.js"]').get(0).hasAttribute("async"));
        });

        // ===============  test 17
        test("17.(defer or async gets from the first file) if in first file we have no them, no matter do we have such params in below files, they will be ignore: ",function(){
            return headScripts.filter('[src="js/test-5.js"]').length ==1 &&
                !(headScripts.filter('[src="js/test-5.js"]').get(0).hasAttribute("defer"));
        });

        // ===============  test 18
        test("18.(on events should mention only one time): ",function(){
            return (lastScript.match(/DOMContentLoaded/g) || []).length <= 1 &&
                (lastScript.match(/onload/g) || []).length <= 1 &&
                (lastScript.match(/customevent/g) || []).length <= 1;
        });

        // ===============  test 19
        test("19.(order of script in body should be the same like in init file): ",function(){
            console.log(bodyScripts.filter('[src="js/test-11.js"]').index());
            return (bodyScripts.filter('[src="js/test-6.js"]').index()) < (bodyScripts.filter('[src="js/test-11.js"]').index()) &&
            (bodyScripts.filter('[src="js/test-11.js"]').index()) < (bodyScripts.filter('[src="js/test-10.js"]').index());
        });
    }
});

