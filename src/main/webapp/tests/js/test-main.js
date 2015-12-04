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
        test("1. check that inline (7) js only one on page: ",function(){
            return $(".test7").length == 1;
        });

        // ===============  test 2
        test("2. check inline param +code works: ",function(){
            var test = $(".test7");
            return test.text != "inline code from test7";
        });
    }
});

