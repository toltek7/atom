$(window).load(function () {
    var report = $("#report"),
        error = 0;
    if (report) {

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

        // ===============  test 3
        test("3.(body + code - on) -> head + code on page (immediately): ",function(){
            return ($("#tag3").next().text().trim() == "console.log('test3 inline code');") &&
                ($("head script").filter('[src="js/test-3.js"]').length == 1);
        });
    }
});

